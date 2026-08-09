package no.nav.tilgangsmaskin.felles.rest

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import no.nav.tilgangsmaskin.tilgang.TilgangControllerBase
import no.nav.tilgangsmaskin.tilgang.TilgangControllerBase.Companion.PROD_BASE_PATH
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@SecurityScheme(bearerFormat = "JWT", name = "bearerAuth", scheme = "bearer", type = HTTP)
@RestController
@RequestMapping(PROD_BASE_PATH)
@SecurityRequirement(name = "bearerAuth")
annotation class ProdController