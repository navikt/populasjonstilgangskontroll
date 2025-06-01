package no.nav.tilgangsmaskin.felles.rest.cache

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS
import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.cfg.MapperConfig
import com.fasterxml.jackson.databind.introspect.AnnotatedClass
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder
import com.fasterxml.jackson.databind.module.SimpleModule
import no.nav.boot.conditionals.ConditionalOnNotProd
import org.slf4j.LoggerFactory.getLogger

/**
    Denne modulen konfigurerer Jackson til å serialisere classes annotatert med  @JsonCacheable, slik at de inkluderer typeinformasjon i JSON-representasjonen.
    Tas inn i produksjon når Jackson 3.0 er i bruke, antagelig høsten 2025.
    Mapper brukt for serialiserig av cache innslag i ValKey må da endres til NON_FINAL_AND_ENUMS, og alle mulig cachebare klasser må transitivt annoteres
 */
@ConditionalOnNotProd  // TODO fix when Jackson 3.0 is in use
class JsonCacheableModule : SimpleModule() {

    private val log = getLogger(javaClass)

    override fun setupModule(context: SetupContext) {
        context.insertAnnotationIntrospector(object : JacksonAnnotationIntrospector() {
            override fun findTypeResolver(config: MapperConfig<*>, ac: AnnotatedClass, baseType: JavaType) =
                if (ac.hasAnnotation(JsonCacheable::class.java)) {
                    log.trace("${ac.name} er annotert ned @JsonCacheable, bruker JsonTypeInfo for å serialisere typeinformasjon")
                    StdTypeResolverBuilder()
                        .init(CLASS, null)
                        .inclusion(PROPERTY)
                        .typeProperty("@class")
                } else {
                    super.findTypeResolver(config, ac, baseType)
                }
        })
    }
}