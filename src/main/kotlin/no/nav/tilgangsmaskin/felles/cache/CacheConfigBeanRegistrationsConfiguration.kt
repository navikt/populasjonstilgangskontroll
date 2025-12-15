package no.nav.tilgangsmaskin.felles.cache

import no.nav.tilgangsmaskin.felles.cache.CacheConfigBeanRegistrationsConfiguration.MyBeanRegistrar
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.springframework.beans.factory.BeanRegistrarDsl
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(MyBeanRegistrar::class)
class CacheConfigBeanRegistrationsConfiguration {
    class MyBeanRegistrar(private vararg val cfgs: CachableRestConfig) : BeanRegistrarDsl({
        registerBean {
            AllCaches(buildMap {
                cfgs.forEach {
                    put( it.navn, it.caches)
                }
            })
        }
    })
}