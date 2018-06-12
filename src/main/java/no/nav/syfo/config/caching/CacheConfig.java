package no.nav.syfo.config.caching;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static net.sf.ehcache.config.PersistenceConfiguration.Strategy.NONE;
import static net.sf.ehcache.store.MemoryStoreEvictionPolicy.LRU;


@Configuration
@EnableCaching
public class CacheConfig {

    public static final CacheConfiguration TILGANG = setupCache("tilgang");
    public static final CacheConfiguration FASTLEGE = setupCache("fastlege");

    @Bean
    public CacheManager ehCacheManager() {
        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.addCache(TILGANG);
        config.addCache(FASTLEGE);
        return CacheManager.newInstance(config);
    }

    @Bean
    public EhCacheCacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheManager());
    }

    @Bean
    public UserKeyGenerator userkeygenerator() {
        return new UserKeyGenerator();
    }

    @Bean
    public KeyGenerator keygenerator() {
        return new KeyGenerator();
    }

    private static CacheConfiguration setupCache(String name) {
        return new CacheConfiguration(name, 1000)
                .memoryStoreEvictionPolicy(LRU)
                .timeToIdleSeconds(3600)
                .timeToLiveSeconds(3600)
                .persistence(new PersistenceConfiguration().strategy(NONE));
    }

}