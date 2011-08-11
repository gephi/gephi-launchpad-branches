package org.gephi.data.store.attributes;

import org.gephi.data.store.api.StoreController;
import org.gephi.data.store.api.Store;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.EnvironmentStats;
import com.sleepycat.je.StatsConfig;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.sf.ehcache.CacheManager;
import org.gephi.data.attributes.api.AttributeModel;
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
        URL xmlConfigURL = getClass().getResource("ehcache.xml");
        cacheManager = new CacheManager(xmlConfigURL);
        
        int ehcacheMemoryPercent = NbPreferences.forModule(AttributeStore.class).getInt("cacheSizePercent", 30);
        int bdbMemoryPercent = NbPreferences.forModule(AttributeStore.class).getInt("bdbMaxMemoryPercent", 10);
        
        long totalMemory = Runtime.getRuntime().maxMemory();
        long ehcacheMemory = (long)(totalMemory * ehcacheMemoryPercent / 100.0);
        long bdbMemory = (long)(totalMemory * bdbMemoryPercent / 100.0);
        System.out.println("Ehcache memory: " + ehcacheMemory / 1024 + " Kbytes");
        System.out.println("BDB cache memory: " + bdbMemory / 1024 + " Kbytes");
        
        cacheManager.getConfiguration().setMaxBytesLocalHeap(ehcacheMemoryPercent + "%");
        
        defaultDbConfig.setAllowCreate(true);          
        defaultDbConfig.setExclusiveCreate(true);      
        defaultDbConfig.setTransactional(false);       
        defaultDbConfig.setSortedDuplicates(false);

        defaultEnvConfig.setAllowCreate(true);
        defaultEnvConfig.setTransactional(false);

        // Max memory usage for BDB (see above)
        // See http://download.oracle.com/docs/cd/E17277_02/html/java/com/sleepycat/je/EnvironmentMutableConfig.html#setCacheSize(long)
        defaultEnvConfig.setConfigParam(EnvironmentConfig.MAX_MEMORY_PERCENT, 
                Long.toString(bdbMemoryPercent));
        
        // Eviction algorithm
        // See http://www.oracle.com/technetwork/database/berkeleydb/je-faq-096044.html#35
        defaultEnvConfig.setConfigParam(EnvironmentConfig.EVICTOR_LRU_ONLY,
                Boolean.toString(false));
        defaultEnvConfig.setConfigParam(EnvironmentConfig.EVICTOR_NODES_PER_SCAN,
                Integer.toString(100));

        int bdbLogFileSize = NbPreferences.forModule(AttributeStore.class).getInt("bdbLogFileSize", 20);
        int bdbLogFaultReadSize = NbPreferences.forModule(AttributeStore.class).getInt("bdbLogFaultReadSize", 5120);
        int bdbLogIteratorReadSize = NbPreferences.forModule(AttributeStore.class).getInt("bdbLogIteratorReadSize", 16384);
        int bdbTotalLogBufferSize = NbPreferences.forModule(AttributeStore.class).getInt("bdbTotalLogBufferSize", 12288);
        int bdbLogNumBuffers = NbPreferences.forModule(AttributeStore.class).getInt("bdbLogNumBuffers", 3);

        // Log file size and cleaner threads
        // See http://download.oracle.com/docs/cd/E17277_02/html/GettingStartedGuide/logfilesrevealed.html
        defaultEnvConfig.setConfigParam(EnvironmentConfig.LOG_FILE_MAX,
                Integer.toString(bdbLogFileSize * 1024 * 1024));
        defaultEnvConfig.setConfigParam(EnvironmentConfig.CHECKPOINTER_BYTES_INTERVAL,
                Integer.toString((bdbLogFileSize / 4) * 1024 * 1024));
        defaultEnvConfig.setConfigParam(EnvironmentConfig.CLEANER_LOOK_AHEAD_CACHE_SIZE,
                Integer.toString(64 * 1024));

        // BDB Read buffers
        // See http://www.oracle.com/technetwork/database/berkeleydb/je-faq-096044.html#39
        defaultEnvConfig.setConfigParam(EnvironmentConfig.LOG_FAULT_READ_SIZE,
                Integer.toString(bdbLogFaultReadSize));
        // Double the buffer size for multiple object reads from disk (default 8K)
        defaultEnvConfig.setConfigParam(EnvironmentConfig.LOG_ITERATOR_READ_SIZE,
                Integer.toString(bdbLogIteratorReadSize));
        
        // BDB Write buffers
        // See http://www.oracle.com/technetwork/database/berkeleydb/je-faq-096044.html#40
        // According to link: total buffer bytes = buffer size * num buffers
        defaultEnvConfig.setConfigParam(EnvironmentConfig.LOG_TOTAL_BUFFER_BYTES, 
                Integer.toString(bdbTotalLogBufferSize));
        defaultEnvConfig.setConfigParam(EnvironmentConfig.LOG_NUM_BUFFERS, 
                Integer.toString(bdbLogNumBuffers));
        defaultEnvConfig.setConfigParam(EnvironmentConfig.LOG_BUFFER_SIZE, 
                Integer.toString(bdbTotalLogBufferSize / bdbLogNumBuffers));
        
        defaultEnvConfig.setLockTimeout(1, TimeUnit.SECONDS);
    }
    
    @Override
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
    
    @Override
    public Store getStore(AttributeModel model) {
        return stores.get(model);
    }

    @Override
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

    @Override
    public void shutdown() {
        for (Store ns : stores.values()) {
            ((AttributeStore) ns).close();
        }
    }
    
    public String getStats() {
        StringBuilder sb = new StringBuilder();
        
        for (AttributeModel am : databases.keySet()) {
            String name = databases.get(am).getDatabaseName();
            String path = environments.get(am).getHome().getAbsolutePath();
            
            sb.append(name).append(" @ ").append(path).append("\n");
            
            StatsConfig statsConfig = new StatsConfig();
            EnvironmentStats stats = environments.get(am).getStats(statsConfig);
            sb.append(stats.toStringVerbose()).append("\n\n");
        }
        
        return sb.toString();
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
