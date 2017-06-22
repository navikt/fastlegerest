package no.nav.syfo.config;

import net.sf.ehcache.CacheManager;
import no.nav.sbl.dialogarena.common.cxf.userkeygenerator.UserKeyGenerator;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;


@Configuration
@EnableCaching
@ImportResource("classpath*:*cacheconfig.xml")
public class CacheConfig {

    @Bean
    public EhCacheCacheManager cacheManager() {
        EhCacheCacheManager cacheManager = new EhCacheCacheManager();
        CacheManager manager = ehCacheManagerFactoryBean().getObject();
        cacheManager.setCacheManager(manager);
        return cacheManager;
    }

    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        ehCacheManagerFactoryBean.setCacheManagerName("cachemanager");
        return ehCacheManagerFactoryBean;
    }

    @Bean
    public UserKeyGenerator userkeygenerator() {
        return new UserKeyGenerator();
    }

    @Bean
    public SimpleKeyGenerator simplekeygenerator() {
        return new SimpleKeyGenerator();
    }

}
