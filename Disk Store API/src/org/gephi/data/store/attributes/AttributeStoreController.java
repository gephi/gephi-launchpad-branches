package org.gephi.data.store.attributes;

import org.gephi.data.store.api.StoreController;
import org.gephi.data.store.api.Store;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.sf.ehcache.CacheManager;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.store.options.DiskCachePanel;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Ernesto A
 */
@ServiceProvider(service = StoreController.class)
public class AttributeStoreController implements StoreController {
    private static final String DB_HOME = "cache";
    
    private final CacheManager cacheManager;
    
    private final EnvironmentConfig defaultEnvConfig = new EnvironmentConfig();
    private final DatabaseConfig defaultDbConfig = new DatabaseConfig();
    
    private final Map<AttributeModel, Environment> environments = new HashMap<AttributeModel, Environment>();
    private final Map<AttributeModel, Database> databases = new HashMap<AttributeModel, Database>();
    private final Map<AttributeModel, Store> stores = new HashMap<AttributeModel, Store>();

    public AttributeStoreController() {
        try {
            InputStream is = getConfigInputStream();
            cacheManager = new CacheManager(is);
            is.close();
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        
        defaultEnvConfig.setAllowCreate(true);
        defaultEnvConfig.setTransactional(false);
        defaultEnvConfig.setCachePercent(30);
        defaultEnvConfig.setLockTimeout(10, TimeUnit.MINUTES);
        
        defaultDbConfig.setAllowCreate(true);          
        defaultDbConfig.setExclusiveCreate(true);      
        defaultDbConfig.setTransactional(false);       
        defaultDbConfig.setSortedDuplicates(false);

        defaultEnvConfig.setConfigParam(EnvironmentConfig.LOG_FILE_MAX, 
                Long.toString(120L * 1024L * 1024L));
        defaultEnvConfig.setConfigParam(EnvironmentConfig.CHECKPOINTER_BYTES_INTERVAL,
                Long.toString(20L * 1024L * 1024L));
        defaultEnvConfig.setConfigParam(EnvironmentConfig.CLEANER_MIN_FILE_UTILIZATION,
                Integer.toString(5));
        defaultEnvConfig.setConfigParam(EnvironmentConfig.CLEANER_MIN_UTILIZATION,
                Integer.toString(50));
        defaultEnvConfig.setConfigParam(EnvironmentConfig.CLEANER_THREADS,
                Integer.toString(2));
        defaultEnvConfig.setConfigParam(EnvironmentConfig.CLEANER_LOOK_AHEAD_CACHE_SIZE,
                Integer.toString(64 * 1024));
        defaultEnvConfig.setConfigParam(EnvironmentConfig.LOCK_N_LOCK_TABLES,
                Integer.toString(1));
        defaultEnvConfig.setConfigParam(EnvironmentConfig.ENV_FAIR_LATCHES,
                Boolean.toString(false));
        defaultEnvConfig.setConfigParam(EnvironmentConfig.CHECKPOINTER_HIGH_PRIORITY,
                Boolean.toString(false));
        defaultEnvConfig.setConfigParam(EnvironmentConfig.CLEANER_MAX_BATCH_FILES,
                Integer.toString(0));
        defaultEnvConfig.setLockTimeout(5, TimeUnit.SECONDS);
    }
    
    public Store newStore(AttributeModel model) {
        Environment env = getEnvironment(model);
        String dbName = getRandomName();

        Database db = env.openDatabase(null, dbName, defaultDbConfig);
        databases.put(model, db);

        Store store = new AttributeStore(dbName, env, db, cacheManager);
        stores.put(model, store);
        
        String log = String.format("Attribute store created (name=%s, location=%s)", dbName, env.getHome().getAbsolutePath());
        System.out.println(log);
        
        return store;
    }
    
    public Store getStore(AttributeModel model) {
        return stores.get(model);
    }

    public void removeStore(AttributeModel model) {
        Store ns = stores.get(model);
        
        if (ns == null) return;
        
        ((AttributeStore) ns).close();

        environments.remove(model);
        databases.remove(model);
        stores.remove(model);
        
        String log = String.format("Attribute store deleted (name=%s)", ns.getName());
        System.out.println(log);
    }

    public void shutdown() {
        for (Store ns : stores.values()) {
            ((AttributeStore) ns).close();
        }
    }
    
    /**
     * Ehcache does not offer a programatic way of changing the maxBytesLocalOnHeap
     * config parameter. To go around this limitation the config xml is read completely
     * into memory, the parameter is modified on-the-fly and then passed to the CacheManager 
     * constructor.
     */
    private InputStream getConfigInputStream() {
        try {
            URL configURL = getClass().getResource("ehcache.xml");    
            URLConnection conn = configURL.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }

            int cachePercent = NbPreferences.forModule(DiskCachePanel.class).getInt("cacheSizePercent", 40);
            String config = sb.toString().replaceAll("maxBytesLocalOnHeap=\".+\"", "maxBytesLocalOnHeap=\"" + cachePercent + "%\"");

            return new ByteArrayInputStream(config.getBytes());
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private Environment getEnvironment(AttributeModel m) {
        Environment env = environments.get(m);
        
        if (env == null) {
            String envName = null;
            File envHome = null;
            
            while (envHome == null) {
                envName = getRandomName();
                envHome = getEnvHomeDir(envName);
            }
            
            env = new Environment(envHome, defaultEnvConfig);
            environments.put(m, env);
        }
        
        if (!env.isValid()) 
            throw new RuntimeException("Database environment is closed");

        return env;
    }
    
    private File getEnvHomeDir(String envName) {
        String netbeansPath = System.getProperty("netbeans.user");
        String sep = File.separator;
        
        File dir = null;
        
        if (netbeansPath == null) {
            // if netbeans path is null then create a directory relative to the
            // current working directory
            dir = new File(DB_HOME + sep + envName);
        }
        else {
            if (netbeansPath.endsWith(sep))
                dir = new File(netbeansPath + DB_HOME + sep + envName);
            else
                dir = new File(netbeansPath + sep + DB_HOME + sep + envName);
        }

        // duplicate dir name
        if (dir.exists())
            return null;
        
        dir.mkdirs();
        
        return dir;
    }
    
    private String getRandomName() {
        char[] alpha = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        
        StringBuilder sb = new StringBuilder("bdb");
        for (int i = 0; i < 5; i++) {
            int index = (int)(Math.random() * alpha.length);
            sb.append(alpha[index]);
        }
        return sb.toString();
    }
}