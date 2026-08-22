package no.nav.tilgangsmaskin.felles.security

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import no.nav.tilgangsmaskin.felles.security.AuthContext.Companion.APP
import no.nav.tilgangsmaskin.felles.security.AuthContext.Companion.IDTYP
import no.nav.tilgangsmaskin.felles.security.AuthContext.Companion.OID
import org.springframework.security.oauth2.jwt.Jwt

class OAuth2AuthorityAddingJwtAuthenticationConverterTest : BehaviorSpec({
    val converter = OAuth2AuthorityAddingJwtAuthenticationConverter()

    Given("JWT-konvertering for token-type og roller") {
        When("tokenet er OBO og har roller") {
            Then("legges OBO- og ROLE-authorities til") {
                val jwt = Jwt.withTokenValue("token")
                    .header("alg", "none")
                    .claim(OID, "123e4567-e89b-12d3-a456-426614174000")
                    .claim(ROLES_CLAIM, listOf("ENKELT", "ROLE_DEV"))
                    .build()

                converter.convert(jwt).authorities.map { it.authority }.shouldContainAll(
                    OBO_AUTHORITY,
                    "ROLE_ENKELT",
                    "ROLE_DEV",
                )
            }
        }

        When("tokenet er CCF") {
            Then("legges CCF-authority til") {
                val jwt = Jwt.withTokenValue("token")
                    .header("alg", "none")
                    .claim(IDTYP, APP)
                    .build()

                converter.convert(jwt).authorities.map { it.authority } shouldContain CCF_AUTHORITY
            }
        }
    }
})
