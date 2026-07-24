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
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import java.time.Instant
import java.util.*

class TokenTest : BehaviorSpec({

    val token = Token()
    val oid = UUID.randomUUID()

    fun setJwt(vararg claims: Pair<String, Any>) {
        val jwtClaims = claims.toMap()
        val jwt = Jwt.withTokenValue("test-token")
            .header("alg", "RS256")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .claims { it.putAll(jwtClaims) }
            .build()
        SecurityContextHolder.setContext(SecurityContextImpl(JwtAuthenticationToken(jwt)))
    }

    beforeEach {
        SecurityContextHolder.clearContext()
    }

    Given("type er CCF") {
        When("idtyp er 'app'") {
            Then("CC er true") {
                setJwt(IDTYP to APP)
                token.type shouldBe CCF
            }
        }
        When("idtyp ikke er 'app'") {
            Then("CC er false") {
                setJwt(IDTYP to "user")
                token.type shouldBe UNAUTHENTICATED
            }
        }
        When("idtyp mangler") {
            Then("CC er false") {
                setJwt()
                token.type shouldBe UNAUTHENTICATED
            }
        }
    }

    Given("type er OBO") {
        When("oid finnes og idtyp ikke er 'app'") {
            Then("OBO er true") {
                setJwt(OID to oid.toString())
                token.type shouldBe OBO
            }
        }
        When("token er CC (idtyp=app)") {
            Then("OBO er false") {
                setJwt(IDTYP to APP, OID to oid.toString())
                token.type shouldBe CCF
            }
        }
        When("oid mangler") {
            Then("OBO er false") {
                setJwt()
                token.type shouldBe UNAUTHENTICATED
            }
        }
    }

    Given("ansattId") {
        When("NAVident finnes") {
            Then("returnerer AnsattId") {
                setJwt(NAVIDENT to "Z999999")
                token.ansattId shouldBe AnsattId("Z999999")
            }
        }
        When("NAVident mangler") {
            Then("AnsattId er null") {
                setJwt()
                token.ansattId shouldBe null
            }
        }
    }

    Given("oid-oppslag fra token") {
        When("oid finnes") {
            Then("returnerer oid") {
                setJwt(OID to oid.toString())
                token.oid shouldBe oid
            }
        }
        When("oid mangler") {
            Then("oid er null") {
                setJwt()
                token.oid shouldBe null
            }
        }
    }

    Given("system") {
        When("azp_name finnes") {
            Then("returnerer azp_name") {
                setJwt(AZP_NAME to "dev-gcp:team:app")
                token.system shouldBe "dev-gcp:team:app"
            }
        }
        When("azp_name mangler") {
            Then("returnerer UTILGJENGELIG") {
                setJwt()
                token.system shouldBe UTILGJENGELIG
            }
        }
    }

    Given("systemNavn") {
        When("azp_name har tre deler") {
            Then("returnerer siste del") {
                setJwt(AZP_NAME to "dev-gcp:team:app")
                token.systemNavn shouldBe "app"
            }
        }
        When("azp_name er ett ord uten kolon") {
            Then("returnerer azp_name uendret") {
                setJwt(AZP_NAME to "app")
                token.systemNavn shouldBe "app"
            }
        }
        When("azp_name mangler") {
            Then("returnerer UTILGJENGELIG") {
                setJwt()
                token.systemNavn shouldBe UTILGJENGELIG
            }
        }
    }

    Given("cluster-informasjon fra token") {
        When("azp_name har tre deler") {
            Then("returnerer første del") {
                setJwt(AZP_NAME to "dev-gcp:team:app")
                token.cluster shouldBe "dev-gcp"
            }
        }
        When("azp_name er ett ord uten kolon") {
            Then("returnerer azp_name uendret") {
                setJwt(AZP_NAME to "app")
                token.cluster shouldBe "app"
            }
        }
        When("azp_name mangler") {
            Then("returnerer UTILGJENGELIG") {
                setJwt()
                token.cluster shouldBe UTILGJENGELIG
            }
        }
    }

    Given("systemAndNs") {
        When("azp_name er cluster:namespace:app") {
            Then("returnerer namespace:app") {
                setJwt(AZP_NAME to "dev-gcp:team:app")
                token.systemAndNs shouldBe "team:app"
            }
        }
        When("azp_name har to deler") {
            Then("returnerer siste del") {
                setJwt(AZP_NAME to "dev-gcp:app")
                token.systemAndNs shouldBe "app"
            }
        }
        When("azp_name er ett ord uten kolon") {
            Then("returnerer tom streng") {
                setJwt(AZP_NAME to "app")
                token.systemAndNs shouldBe ""
            }
        }
        When("azp_name mangler") {
            Then("returnerer tom streng") {
                setJwt()
                token.systemAndNs shouldBe ""
            }
        }
    }

    Given("clusterAndSystem") {
        When("azp_name har tre deler") {
            Then("returnerer 'app:cluster'") {
                setJwt(AZP_NAME to "dev-gcp:team:app")
                token.clusterAndSystem shouldBe "app:dev-gcp"
            }
        }
        When("azp_name ikke har tre deler") {
            Then("returnerer system uendret") {
                setJwt(AZP_NAME to "app")
                token.clusterAndSystem shouldBe "app"
            }
        }
    }

    Given("globaleGruppeIds") {
        When("groups-claim inneholder gyldige UUIDs") {
            Then("returnerer set av UUIDs") {
                val gruppeId = UUID.randomUUID()
                setJwt("groups" to listOf(gruppeId.toString()))
                token.globaleGruppeIds shouldBe setOf(gruppeId)
            }
        }
        When("groups mangler") {
            Then("returnerer tomt set") {
                setJwt()
                token.globaleGruppeIds.shouldBeEmpty()
            }
        }
        When("groups er tom liste") {
            Then("returnerer tomt set") {
                setJwt("groups" to emptyList<String>())
                token.globaleGruppeIds.shouldBeEmpty()
            }
        }
        When("ingen authentication i context") {
            Then("returnerer tomt set") {
                SecurityContextHolder.clearContext()
                token.globaleGruppeIds.shouldBeEmpty()
            }
        }
        When("groups inneholder ugyldig UUID-verdi") {
            Then("ignoreres og returnerer tomt sett") {
                setJwt("groups" to listOf("ikke-en-uuid"))
                token.globaleGruppeIds.shouldBeEmpty()
            }
        }
    }

    Given("ingen gyldig token-kontekst") {
        When("SecurityContext er tomt") {
            Then("type er UNAUTHENTICATED") {
                token.type shouldBe UNAUTHENTICATED
            }
            Then("ansattId er null") {
                token.ansattId shouldBe null
            }
        }
    }

    Given("token.type") {
        When("token er OBO") {
            Then("returnerer OBO") {
                setJwt(OID to oid.toString())
                token.type shouldBe OBO
            }
        }
        When("token er CC") {
            Then("returnerer CCF") {
                setJwt(IDTYP to APP)
                token.type shouldBe CCF
            }
        }
        When("ingen claims finnes") {
            Then("returnerer UNAUTHENTICATED") {
                token.type shouldBe UNAUTHENTICATED
            }
        }
    }
})
