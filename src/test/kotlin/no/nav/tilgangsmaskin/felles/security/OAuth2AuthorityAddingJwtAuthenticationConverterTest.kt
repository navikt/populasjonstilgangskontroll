package no.nav.tilgangsmaskin.felles.security

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import no.nav.tilgangsmaskin.felles.rest.Token.Companion.AZP_NAME
import org.springframework.security.oauth2.jwt.Jwt

class OAuth2AuthorityAddingJwtAuthenticationConverterTest : BehaviorSpec({
    val converter = OAuth2AuthorityAddingJwtAuthenticationConverter()

    Given("JWT-konvertering med azp_name") {
        When("azp_name inneholder systemnavn") {
            Then("legges SYSTEM-authority til") {
                val jwt = Jwt.withTokenValue("token")
                    .header("alg", "none")
                    .claim(AZP_NAME, "dev-gcp:tilgangsmaskin:gosys")
                    .build()

                converter.convert(jwt).authorities.map { it.authority } shouldContain "${SYSTEM_AUTHORITY_PREFIX}gosys"
            }
        }

        When("azp_name mangler") {
            Then("inneholder ikke SYSTEM-authority") {
               val jwt = Jwt.withTokenValue("token")
                    .header("alg", "none")
                    .claim("sub", "subject")
                    .build()

                converter.convert(jwt).authorities.map { it.authority }.shouldNotContain("${SYSTEM_AUTHORITY_PREFIX}gosys")
            }
        }
    }
})
