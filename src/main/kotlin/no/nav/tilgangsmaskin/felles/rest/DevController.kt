package no.nav.tilgangsmaskin.felles.rest

import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.UnprotectedRestController
import org.springframework.core.annotation.AliasFor
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS

@Target(CLASS)
@Retention(RUNTIME)
@ConditionalOnNotProd
@UnprotectedRestController
@Tag(name = "")
annotation class DevController(
    @get:AliasFor(annotation = UnprotectedRestController::class, attribute = "value")
    val value: Array<String>,
    @get:AliasFor(annotation = Tag::class, attribute = "name")
    val name: String = "",
    @get:AliasFor(annotation = Tag::class, attribute = "description")
    val description: String = ""
)
