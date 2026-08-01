package no.nav.tilgangsmaskin.tilgang

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import no.nav.tilgangsmaskin.tilgang.Token.Companion.APP
import no.nav.tilgangsmaskin.tilgang.Token.Companion.AZP_NAME
import no.nav.tilgangsmaskin.tilgang.Token.Companion.IDTYP
import no.nav.tilgangsmaskin.tilgang.Token.Companion.NAVIDENT
import no.nav.tilgangsmaskin.tilgang.Token.Companion.OID
import no.nav.tilgangsmaskin.tilgang.TokenType.CCF
import no.nav.tilgangsmaskin.tilgang.TokenType.OBO
import no.nav.tilgangsmaskin.tilgang.TokenType.UNAUTHENTICATED
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import java.time.Instant
import java.util.UUID

class TokenTest : BehaviorSpec({

    val token = Token()
    val oid = UUID.randomUUID()

    fun authenticate(claims: Map<String, Any>) {
        val jwt = Jwt.withTokenValue("token")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .header("alg", "none")
            .claim("sub", "test-sub")
            .claims { it.putAll(claims) }
            .build()
        SecurityContextHolder.getContext().authentication = JwtAuthenticationToken(jwt)
    }

    beforeEach {
        authenticate(emptyMap())
    }

    afterSpec {
        SecurityContextHolder.clearContext()
    }

    Given("type er CCF") {
        When("idtyp er 'app'") {
            Then("CC er true") {
                authenticate(mapOf(IDTYP to APP))
                token.type shouldBe CCF
            }
        }
        When("idtyp ikke er 'app'") {
            Then("CC er false") {
                authenticate(mapOf(IDTYP to "user"))
                token.type shouldBe UNAUTHENTICATED
            }
        }
    }

    Given("type er OBO") {
        When("oid finnes og idtyp ikke er 'app'") {
            Then("OBO er true") {
                authenticate(mapOf(OID to oid.toString()))
                token.type shouldBe OBO
            }
        }
        When("token er CC (idtyp=app)") {
            Then("OBO er false") {
                authenticate(mapOf(IDTYP to APP, OID to oid.toString()))
                token.type shouldBe CCF
            }
        }
    }

    Given("ansattId") {
        When("NAVident finnes") {
            Then("returnerer AnsattId") {
                authenticate(mapOf(NAVIDENT to "Z999999"))
                token.ansattId shouldBe AnsattId("Z999999")
            }
        }
        When("NAVident mangler") {
            Then("AnsattId er null") {
                token.ansattId shouldBe null
            }
        }
    }

    Given("oid-oppslag fra token") {
        When("oid finnes") {
            Then("returnerer oid") {
                authenticate(mapOf(OID to oid.toString()))
                token.oid shouldBe oid
            }
        }
        When("oid mangler") {
            Then("oid er null") {
                token.oid shouldBe null
            }
        }
    }

    Given("system") {
        When("azp_name finnes") {
            Then("returnerer azp_name") {
                authenticate(mapOf(AZP_NAME to "dev-gcp:team:app"))
                token.system shouldBe "dev-gcp:team:app"
            }
        }
        When("azp_name mangler") {
            Then("returnerer UTILGJENGELIG") {
                token.system shouldBe UTILGJENGELIG
            }
        }
    }

    Given("avledede systemfelt") {
        When("azp_name har tre deler") {
            Then("returnerer korrekt systemNavn, cluster, systemAndNs og clusterAndSystem") {
                authenticate(mapOf(AZP_NAME to "dev-gcp:team:app"))
                token.systemNavn shouldBe "app"
                token.cluster shouldBe "dev-gcp"
                token.systemAndNs shouldBe "team:app"
                token.clusterAndSystem shouldBe "app:dev-gcp"
            }
        }
        When("azp_name er ett ord uten kolon") {
            Then("returnerer fallback-verdier") {
                authenticate(mapOf(AZP_NAME to "app"))
                token.systemNavn shouldBe "app"
                token.cluster shouldBe "app"
                token.systemAndNs shouldBe ""
                token.clusterAndSystem shouldBe "app"
            }
        }
    }

    Given("globaleGruppeIds") {
        When("groups-claim inneholder gyldige UUIDs") {
            Then("returnerer set av UUIDs") {
                val gruppeId = UUID.randomUUID()
                authenticate(mapOf("groups" to listOf(gruppeId.toString())))
                token.globaleGruppeIds shouldBe setOf(gruppeId)
            }
        }
        When("groups mangler") {
            Then("returnerer tomt set") {
                token.globaleGruppeIds.shouldBeEmpty()
            }
        }
        When("groups inneholder ugyldig UUID-verdi") {
            Then("ignoreres og returnerer tomt sett") {
                authenticate(mapOf("groups" to listOf("ikke-en-uuid")))
                token.globaleGruppeIds.shouldBeEmpty()
            }
        }
    }

    Given("ingen gyldig token-kontekst") {
        When("authentication mangler") {
            Then("returnerer UNAUTHENTICATED og tomme claims") {
                SecurityContextHolder.clearContext()
                token.type shouldBe UNAUTHENTICATED
                token.ansattId shouldBe null
                token.globaleGruppeIds.shouldBeEmpty()
            }
        }
    }
})
