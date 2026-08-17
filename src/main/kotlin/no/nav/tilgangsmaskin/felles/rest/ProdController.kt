package no.nav.tilgangsmaskin.felles.rest

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS

const val PROD_BASE_PATH = "/api/v1"

@Target(CLASS)
@Retention(RUNTIME)
@SecurityScheme(bearerFormat = "JWT", name = "bearerAuth", scheme = "bearer", type = HTTP)
@RestController
@RequestMapping(PROD_BASE_PATH)
@SecurityRequirement(name = "bearerAuth")
annotation class ProdController