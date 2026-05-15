package no.nav.tilgangsmaskin.felles.cache

import no.nav.tilgangsmaskin.felles.cache.CacheConfigBeanRegistrationsConfiguration.MyBeanRegistrar
import no.nav.tilgangsmaskin.felles.rest.CacheConfig
import org.springframework.beans.factory.BeanRegistrarDsl
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(MyBeanRegistrar::class)
class CacheConfigBeanRegistrationsConfiguration {
    class MyBeanRegistrar(private vararg val cfgs: CacheConfig) : BeanRegistrarDsl({
        registerBean {
            AllCaches(buildMap {
                cfgs.forEach {
                    put( it.navn, it.caches)
                }
            })
        }
    })
}
data class AllCaches(val map: Map<String,Set<CacheNøkkelConfig>>)