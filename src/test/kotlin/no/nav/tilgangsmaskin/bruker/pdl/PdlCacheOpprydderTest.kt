package no.nav.tilgangsmaskin.bruker.pdl

import io.kotest.core.spec.style.BehaviorSpec
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.person.pdl.leesah.Endringstype
import no.nav.person.pdl.leesah.Endringstype.OPPRETTET
import no.nav.person.pdl.leesah.Endringstype.entries
import no.nav.person.pdl.leesah.Personhendelse
import no.nav.person.pdl.leesah.adressebeskyttelse.Adressebeskyttelse
import no.nav.person.pdl.leesah.adressebeskyttelse.Gradering.FORTROLIG
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL_CACHES
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.regler.motor.PdlCacheTømmerTeller
import no.nav.tilgangsmaskin.tilgang.Token
import java.time.Instant

class PdlCacheOpprydderTest : BehaviorSpec({

    val token = mockk<Token>().also {
        every { it.system } returns "test"
        every { it.clusterAndSystem } returns "test:dev-gcp"
    }
    val pdl = mockk<PdlTjeneste>(relaxed = true)
    val client = mockk<CacheOperations>()
    val opprydder = PdlHendelseKonsument(pdl, client, PdlCacheTømmerTeller(SimpleMeterRegistry(), token))

    fun hendelse(identer: List<String>, endringstype: Endringstype = OPPRETTET,
        gradering: Adressebeskyttelse? = null) =
        Personhendelse("hendelse-id", identer, "PDL", Instant.now(), "PDL_HENDELSE", endringstype, null, gradering, null)

    beforeEach {
       // every { client.tilNøkkel(any(), any()) } answers { "${firstArg<Any>()}::${secondArg<String>()}" }
        every { client.delete(any(), any()) } returns 1
    }

    Given("sletting av cache ved personhendelse") {
        When("hendelsen inneholder to identer") {
            Then("slettes fra alle PDL-cacher for begge identer") {
                opprydder.listen(hendelse(listOf(I1, I2)))
                PDL_CACHES.forEach { cache ->
                    listOf(I1, I2).forEach { id -> verify { client.delete(cache, id) } }
                }
            }
        }

        entries.forEach { endringstype ->
            When("endringstype er $endringstype") {
                Then("slettes cache og refresh utføres") {
                    opprydder.listen(hendelse(listOf(I1), endringstype))
                    PDL_CACHES.forEach { cache -> verify { client.delete(cache, I1) } }
                    verify { pdl.medFamilie(I1) }
                    verify { pdl.medUtvidetFamilie(I1) }
                }
            }
        }

        When("adressebeskyttelse er null") {
            Then("brukes UGRADERT og slettes fra alle PDL-cacher") {
                opprydder.listen(hendelse(listOf(I1), gradering = null))
                PDL_CACHES.forEach { cache -> verify { client.delete(cache, I1) } }
            }
        }

        When("hendelsen har FORTROLIG gradering") {
            Then("slettes fra alle PDL-cacher") {
                opprydder.listen(hendelse(listOf(I1), gradering = Adressebeskyttelse(FORTROLIG)))
                PDL_CACHES.forEach { cache -> verify { client.delete(cache, I1) } }
            }
        }
    }

    Given("oppfrisking av cache etter endring") {
        When("hendelsen inneholder to identer") {
            Then("kalles medFamilie og medUtvidetFamilie for begge") {
                opprydder.listen(hendelse(listOf(I1, I2)))
                verify { pdl.medFamilie(I1) }
                verify { pdl.medFamilie(I2) }
                verify { pdl.medUtvidetFamilie(I1) }
                verify { pdl.medUtvidetFamilie(I2) }
            }
        }

        When("ingen cache-innslag ble slettet") {
            Then("utføres refresh likevel") {
                opprydder.listen(hendelse(listOf(I1)))
                verify { pdl.medFamilie(I1) }
                verify { pdl.medUtvidetFamilie(I1) }
            }
        }
    }
}) {
    companion object {
        private const val I1 = "03508331575"
        private const val I2 = "20478606614"
    }
}
