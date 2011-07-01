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
import net.sf.ehcache.CacheManager;
import org.gephi.project.api.Workspace;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Ernesto A
 */
@ServiceProvider(service = AttributeStoreController.class)
public class AttributeStoreControllerImpl implements AttributeStoreController {
    private static final String DB_HOME = "cache";

    private final CacheManager cacheManager;
    
    private final EnvironmentConfig defaultEnvConfig = new EnvironmentConfig();
    private final DatabaseConfig defaultDbConfig = new DatabaseConfig();
    
    private final Map<Workspace, Environment> environments = new HashMap<Workspace, Environment>();
    private final Map<Workspace, Database> databases = new HashMap<Workspace, Database>();
    private final Map<Workspace, AttributeStore> nodeStores = new HashMap<Workspace, AttributeStore>();
    private final Map<Workspace, AttributeStore> edgeStores = new HashMap<Workspace, AttributeStore>();

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
    
    public void newStore(Workspace workspace) {
        Environment env = getEnvironment(workspace);
        String dbName = getRandomName();

        Database db = env.openDatabase(null, dbName, defaultDbConfig);
        databases.put(workspace, db);

        AttributeStore nodeStore = new AttributeStoreImpl(dbName + "n", env, db, cacheManager);
        nodeStores.put(workspace, nodeStore);
        AttributeStore edgeStore = new AttributeStoreImpl(dbName + "e", env, db, cacheManager);
        edgeStores.put(workspace, edgeStore);
    }
    
    public AttributeStore getNodeStore(Workspace workspace) {
        return nodeStores.get(workspace);
    }

    public AttributeStore getEdgeStore(Workspace workspace) {
        return nodeStores.get(workspace);
    }

    public void removeStore(Workspace workspace) {
        AttributeStore ns = nodeStores.get(workspace);
        ((AttributeStoreImpl) ns).close();

        AttributeStore es = edgeStores.get(workspace);
        ((AttributeStoreImpl) es).close();

        environments.remove(workspace);
        databases.remove(workspace);
        nodeStores.remove(workspace);
        edgeStores.remove(workspace);
    }

    public void shutdown() {
        for (AttributeStore ns : nodeStores.values()) {
            ((AttributeStoreImpl) ns).close();
        }
        
        for (AttributeStore es : edgeStores.values()) {
            ((AttributeStoreImpl) es).close();
        }
    }
    
    private Environment getEnvironment(Workspace w) {
        Environment env = environments.get(w);
        
        if (env == null) {
            String envName = null;
            File envHome = null;
            
            while (envHome == null) {
                envName = getRandomName();
                envHome = getEnvHomeDir(envName);
            }
            
            env = new Environment(envHome, defaultEnvConfig);
            environments.put(w, env);
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
