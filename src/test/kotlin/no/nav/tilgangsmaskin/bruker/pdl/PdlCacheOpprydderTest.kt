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
import no.nav.tilgangsmaskin.tilgang.Token
import java.time.Instant

class PdlCacheOpprydderTest : BehaviorSpec({

    val token = mockk<Token>().also {
        every { it.system } returns "test"
        every { it.clusterAndSystem } returns "test:dev-gcp"
    }
    val client = mockk<CacheOperations>()
    val opprydder = PdlHendelseKonsument(client, PdlCacheTømmerTeller(SimpleMeterRegistry(), token))

    fun hendelse(identer: List<String>, endringstype: Endringstype = OPPRETTET,
        gradering: Adressebeskyttelse? = null) =
        Personhendelse("hendelse-id", identer, "PDL", Instant.now(), "PDL_HENDELSE", endringstype, null, gradering, null)

    beforeEach {
        every { client.delete(any(), any()) } returns 1L
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
                Then("slettes cache for alle PDL-cacher") {
                    opprydder.listen(hendelse(listOf(I1), endringstype))
                    PDL_CACHES.forEach { cache -> verify { client.delete(cache, I1) } }
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
}) {
    companion object {
        private const val I1 = "03508331575"
        private const val I2 = "20478606614"
    }
}
