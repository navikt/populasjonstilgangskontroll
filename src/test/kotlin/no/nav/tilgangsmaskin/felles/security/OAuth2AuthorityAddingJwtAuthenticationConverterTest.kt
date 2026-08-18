package no.nav.tilgangsmaskin.felles.security

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import no.nav.tilgangsmaskin.felles.rest.Token.Companion.AZP_NAME
import no.nav.tilgangsmaskin.felles.security.OAuth2AuthorityAddingJwtAuthenticationConverter.SystemAuthority
import org.springframework.security.oauth2.jwt.Jwt

class OAuth2AuthorityAddingJwtAuthenticationConverterTest : BehaviorSpec({
    val converter = OAuth2AuthorityAddingJwtAuthenticationConverter()
    val gosysAuthority  = SystemAuthority("gosys")

    Given("JWT-konvertering med azp_name") {
        When("azp_name inneholder ${gosysAuthority.system}") {
            Then("legges $gosysAuthority til") {
                val jwt = Jwt.withTokenValue("token")
                    .header("alg", "none")
                    .claim(AZP_NAME, "dev-gcp:tilgangsmaskin:gosys")
                    .build()

                converter.convert(jwt).authorities shouldContain gosysAuthority
            }
        }

        When("azp_name mangler") {
            Then("inneholder ikke ${gosysAuthority.system}") {
               val jwt = Jwt.withTokenValue("token")
                    .header("alg", "none")
                    .claim("sub", "subject")
                    .build()

                converter.convert(jwt).authorities.shouldNotContain(gosysAuthority)
            }
        }
    }
})
