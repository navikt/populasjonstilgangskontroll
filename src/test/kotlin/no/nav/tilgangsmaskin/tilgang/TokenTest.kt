package no.nav.tilgangsmaskin.tilgang

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.security.token.support.core.context.TokenValidationContext
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.security.token.support.core.jwt.JwtTokenClaims
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import no.nav.tilgangsmaskin.tilgang.Token.Companion.AAD_ISSUER
import no.nav.tilgangsmaskin.tilgang.Token.Companion.APP
import no.nav.tilgangsmaskin.tilgang.Token.Companion.AZP_NAME
import no.nav.tilgangsmaskin.tilgang.Token.Companion.IDTYP
import no.nav.tilgangsmaskin.tilgang.Token.Companion.NAVIDENT
import no.nav.tilgangsmaskin.tilgang.Token.Companion.OID
import java.util.*

class TokenTest : DescribeSpec({

    val contextHolder = mockk<TokenValidationContextHolder>()
    val validationContext = mockk<TokenValidationContext>()
    val claims = mockk<JwtTokenClaims>()
    val token = Token(contextHolder)

    val oid = UUID.fromString("11111111-1111-1111-1111-111111111111")

    beforeEach {
        every { contextHolder.getTokenValidationContext() } returns validationContext
        every { validationContext.getClaims(AAD_ISSUER) } returns claims
        every { claims.getStringClaim(any()) } returns null
        every { claims.getAsList(any()) } returns emptyList()
    }

    describe("erCC") {
        it("er true når idtyp er 'app'") {
            every { claims.getStringClaim(IDTYP) } returns APP
            token.erCC shouldBe true
        }

        it("er false når idtyp ikke er 'app'") {
            every { claims.getStringClaim(IDTYP) } returns "user"
            token.erCC shouldBe false
        }

        it("er false når idtyp mangler") {
            token.erCC shouldBe false
        }
    }

    describe("erObo") {
        it("er true når oid finnes og idtyp ikke er 'app'") {
            every { claims.getStringClaim(OID) } returns oid.toString()
            token.erObo shouldBe true
        }

        it("er false når token er CC (idtyp=app)") {
            every { claims.getStringClaim(IDTYP) } returns APP
            every { claims.getStringClaim(OID) } returns oid.toString()
            token.erObo shouldBe false
        }

        it("er false når oid mangler") {
            token.erObo shouldBe false
        }
    }

    describe("ansattId") {
        it("returnerer AnsattId når NAVident finnes") {
            every { claims.getStringClaim(NAVIDENT) } returns "Z999999"
            token.ansattId shouldBe AnsattId("Z999999")
        }

        it("returnerer null når NAVident mangler") {
            token.ansattId shouldBe null
        }
    }

    describe("oid") {
        it("returnerer UUID når oid finnes") {
            every { claims.getStringClaim(OID) } returns oid.toString()
            token.oid shouldBe oid
        }

        it("returnerer null når oid mangler") {
            token.oid shouldBe null
        }
    }

    describe("system") {
        it("returnerer azp_name når den finnes") {
            every { claims.getStringClaim(AZP_NAME) } returns "dev-gcp:team:app"
            token.system shouldBe "dev-gcp:team:app"
        }

        it("returnerer UTILGJENGELIG når azp_name mangler") {
            token.system shouldBe UTILGJENGELIG
        }
    }

    describe("systemNavn") {
        it("returnerer siste del av azp_name når tre deler") {
            every { claims.getStringClaim(AZP_NAME) } returns "dev-gcp:team:app"
            token.systemNavn shouldBe "app"
        }

        it("returnerer azp_name uendret når én del uten kolon") {
            every { claims.getStringClaim(AZP_NAME) } returns "app"
            token.systemNavn shouldBe "app"
        }

        it("returnerer UTILGJENGELIG når azp_name mangler") {
            token.systemNavn shouldBe UTILGJENGELIG
        }
    }

    describe("cluster") {
        it("returnerer første del av azp_name når tre deler") {
            every { claims.getStringClaim(AZP_NAME) } returns "dev-gcp:team:app"
            token.cluster shouldBe "dev-gcp"
        }

        it("returnerer azp_name uendret når én del uten kolon") {
            every { claims.getStringClaim(AZP_NAME) } returns "app"
            token.cluster shouldBe "app"
        }

        it("returnerer UTILGJENGELIG når azp_name mangler") {
            token.cluster shouldBe UTILGJENGELIG
        }
    }

    describe("systemAndNs") {
        it("returnerer namespace:app når azp_name er cluster:namespace:app") {
            every { claims.getStringClaim(AZP_NAME) } returns "dev-gcp:team:app"
            token.systemAndNs shouldBe "team:app"
        }

        it("returnerer tom streng når azp_name er et enkelt ord uten kolon") {
            every { claims.getStringClaim(AZP_NAME) } returns "app"
            token.systemAndNs shouldBe ""
        }

        it("returnerer app når azp_name har to deler") {
            every { claims.getStringClaim(AZP_NAME) } returns "dev-gcp:app"
            token.systemAndNs shouldBe "app"
        }

        it("returnerer tom streng når azp_name mangler") {
            token.systemAndNs shouldBe ""
        }
    }


    describe("clusterAndSystem") {
        it("returnerer 'app:cluster' når azp_name har tre deler") {
            every { claims.getStringClaim(AZP_NAME) } returns "dev-gcp:team:app"
            token.clusterAndSystem shouldBe "app:dev-gcp"
        }

        it("returnerer system uendret når azp_name ikke har tre deler") {
            every { claims.getStringClaim(AZP_NAME) } returns "app"
            token.clusterAndSystem shouldBe "app"
        }
    }

    describe("globaleGruppeIds") {
        it("returnerer liste av UUIDs fra groups-claim") {
            val gruppeId = UUID.randomUUID()
            every { claims.getAsList("groups") } returns listOf(gruppeId.toString())
            token.globaleGruppeIds shouldBe listOf(gruppeId)
        }

        it("returnerer tom liste når groups mangler") {
            token.globaleGruppeIds shouldBe emptyList()
        }

        it("returnerer tom liste når groups er en tom liste") {
            every { claims.getAsList("groups") } returns emptyList()
            token.globaleGruppeIds shouldBe emptyList()
        }

        it("returnerer tom liste når claimSet er null (ingen token-kontekst)") {
            every { validationContext.getClaims(AAD_ISSUER) } throws RuntimeException("ingen token")
            token.globaleGruppeIds shouldBe emptyList()
        }

        it("returnerer tom liste når getAsList returnerer null") {
            every { claims.getAsList("groups") } returns null
            token.globaleGruppeIds shouldBe emptyList()
        }

        it("kaster IllegalArgumentException ved ugyldig UUID-verdi i groups") {
            every { claims.getAsList("groups") } returns listOf("ikke-en-uuid")
            shouldThrow<IllegalArgumentException> { token.globaleGruppeIds }
        }
    }

    describe("ingen gyldig token-kontekst") {
        it("erCC er false når getClaims kaster exception") {
            every { validationContext.getClaims(AAD_ISSUER) } throws RuntimeException("ingen token")
            token.erCC shouldBe false
        }

        it("erObo er false når getClaims kaster exception") {
            every { validationContext.getClaims(AAD_ISSUER) } throws RuntimeException("ingen token")
            token.erObo shouldBe false
        }

        it("ansattId er null når getClaims kaster exception") {
            every { validationContext.getClaims(AAD_ISSUER) } throws RuntimeException("ingen token")
            token.ansattId shouldBe null
        }
    }

    describe("TokenType.from") {
        it("returnerer OBO for OBO-token") {
            every { claims.getStringClaim(OID) } returns oid.toString()
            TokenType.from(token) shouldBe TokenType.OBO
        }

        it("returnerer CCF for CC-token") {
            every { claims.getStringClaim(IDTYP) } returns APP
            TokenType.from(token) shouldBe TokenType.CCF
        }

        it("returnerer UNAUTHENTICATED når ingen claims finnes") {
            every { validationContext.getClaims(AAD_ISSUER) } throws RuntimeException("ingen token")
            TokenType.from(token) shouldBe TokenType.UNAUTHENTICATED
        }
    }
})
