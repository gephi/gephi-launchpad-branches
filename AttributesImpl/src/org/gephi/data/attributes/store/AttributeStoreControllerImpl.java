package org.gephi.data.attributes.store;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import net.sf.ehcache.CacheManager;
import org.gephi.data.attributes.api.AttributeModel;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Ernesto A
 */
@ServiceProvider(service = AttributeStoreController.class)
public class AttributeStoreControllerImpl implements AttributeStoreController {
    private static final String DB_HOME = "cache";
    
    private final Logger logger = Logger.getLogger("AttributeStoreController");
    
    private final CacheManager cacheManager;
    
    private final EnvironmentConfig defaultEnvConfig = new EnvironmentConfig();
    private final DatabaseConfig defaultDbConfig = new DatabaseConfig();
    
    private final Map<AttributeModel, Environment> environments = new HashMap<AttributeModel, Environment>();
    private final Map<AttributeModel, Database> databases = new HashMap<AttributeModel, Database>();
    
    private final Map<AttributeModel, AttributeStore> nodeStores = new HashMap<AttributeModel, AttributeStore>();
    private final Map<AttributeModel, AttributeStore> edgeStores = new HashMap<AttributeModel, AttributeStore>();

    public AttributeStoreControllerImpl() {
        URL configURL = getClass().getResource("ehcache.xml");
        cacheManager = new CacheManager(configURL);
        
        defaultEnvConfig.setAllowCreate(true);
        defaultEnvConfig.setTransactional(true);
        defaultEnvConfig.setCachePercent(20);
        defaultEnvConfig.setLockTimeout(10, TimeUnit.MINUTES);
        
        defaultDbConfig.setAllowCreate(true);          
        defaultDbConfig.setExclusiveCreate(true);      
        defaultDbConfig.setTransactional(true);       
        defaultDbConfig.setSortedDuplicates(false);    
    }
    
    public void newStore(AttributeModel model) {
        if (environments.containsKey(model))
            return;
        
        Environment env = getEnvironment(model);
        String dbName = getRandomName();

        Database db = env.openDatabase(null, dbName, defaultDbConfig);
        databases.put(model, db);

        AttributeStore nodeStore = new AttributeStoreImpl(dbName + "n", env, db, cacheManager, model.getNodeTable());
        nodeStores.put(model, nodeStore);
        AttributeStore edgeStore = new AttributeStoreImpl(dbName + "e", env, db, cacheManager, model.getEdgeTable());
        edgeStores.put(model, edgeStore);
    }
    
    public AttributeStore getNodeStore(AttributeModel model) {
        return getStore(nodeStores, model);
    }

    public AttributeStore getEdgeStore(AttributeModel model) {
        return getStore(edgeStores, model);
    }

    public void removeStore(AttributeModel model) {
        AttributeStore ns = nodeStores.get(model);
        ((AttributeStoreImpl) ns).close();

        AttributeStore es = edgeStores.get(model);
        ((AttributeStoreImpl) es).close();

        environments.remove(model);
        databases.remove(model);
        nodeStores.remove(model);
        edgeStores.remove(model);
    }

    public void shutdown() {
        for (AttributeStore ns : nodeStores.values()) {
            ((AttributeStoreImpl) ns).close();
        }
        
        for (AttributeStore es : edgeStores.values()) {
            ((AttributeStoreImpl) es).close();
        }
    }
    
    private AttributeStore getStore(Map<AttributeModel, AttributeStore> stores, AttributeModel m) {
        AttributeStore store = stores.get(m);
        return store;
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
        String sep = File.pathSeparator;
        
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
        for (int i = 0; i < 4; i++) {
            int index = (int)(Math.random() * alpha.length);
            sb.append(alpha[index]);
        }
        return sb.toString();
    }
}
