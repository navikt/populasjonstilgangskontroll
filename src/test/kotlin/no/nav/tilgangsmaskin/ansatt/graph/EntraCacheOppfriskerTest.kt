package no.nav.tilgangsmaskin.ansatt.graph

import io.kotest.core.spec.style.DescribeSpec
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.OID_CACHE
import no.nav.tilgangsmaskin.felles.cache.CacheClient
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelElementer
import no.nav.tilgangsmaskin.felles.rest.NotFoundRestException
import no.nav.tilgangsmaskin.regler.motor.OppfriskingTeller
import java.net.URI
import java.util.UUID

class EntraCacheOppfriskerTest : DescribeSpec({

    val entra = mockk<EntraTjeneste>(relaxed = true)
    val oidTjeneste = mockk<AnsattOidTjeneste>()
    val cache = mockk<CacheClient>(relaxed = true)
    val teller = mockk<OppfriskingTeller>(relaxed = true)
    val oppfrisker = EntraCacheOppfrisker(entra, oidTjeneste, cache, teller)

    val ansattId = AnsattId("Z999999")
    val oid = UUID.fromString("11111111-1111-1111-1111-111111111111")
    val nyOid = UUID.fromString("22222222-2222-2222-2222-222222222222")

    fun nøkkel(metode: String) = CacheNøkkelElementer("graph::$metode:${ansattId.verdi}")

    beforeEach {
        clearMocks(entra, oidTjeneste, cache, teller)
        every { oidTjeneste.oidFraEntra(ansattId) } returns oid
    }

    describe("oppfrisk") {

        describe("geoGrupper") {

            it("kaller entra.geoGrupper med riktig ansattId og oid") {
                oppfrisker.oppfrisk(nøkkel("geoGrupper"))

                verify { entra.geoGrupper(ansattId, oid) }
                verify(exactly = 0) { entra.geoOgGlobaleGrupper(any(), any()) }
            }
        }

        describe("geoOgGlobaleGrupper") {

            it("kaller entra.geoOgGlobaleGrupper med riktig ansattId og oid") {
                oppfrisker.oppfrisk(nøkkel("geoOgGlobaleGrupper"))

                verify { entra.geoOgGlobaleGrupper(ansattId, oid) }
                verify(exactly = 0) { entra.geoGrupper(any(), any()) }
            }
        }

        describe("ukjent metode") {

            it("kaller verken geoGrupper eller geoOgGlobaleGrupper") {
                oppfrisker.oppfrisk(CacheNøkkelElementer("graph::ukjentMetode:${ansattId.verdi}"))

                verify(exactly = 0) { entra.geoGrupper(any(), any()) }
                verify(exactly = 0) { entra.geoOgGlobaleGrupper(any(), any()) }
            }
        }

        describe("NotFoundRestException — tøm og oppfrisk") {

            it("sletter OID-cache og henter ny OID ved NotFoundRestException") {
                every { entra.geoGrupper(ansattId, oid) } throws NotFoundRestException(URI.create("http://entra"))
                every { oidTjeneste.oidFraEntra(ansattId) } returnsMany listOf(oid, nyOid)

                oppfrisker.oppfrisk(nøkkel("geoGrupper"))

                verify { cache.delete(OID_CACHE, ansattId.verdi) }
                verify(exactly = 2) { oidTjeneste.oidFraEntra(ansattId) }
            }

            it("kaller oppfrisk på nytt med ny OID etter NotFoundRestException") {
                every { entra.geoGrupper(ansattId, oid) } throws NotFoundRestException(URI.create("http://entra"))
                every { oidTjeneste.oidFraEntra(ansattId) } returnsMany listOf(oid, nyOid)

                oppfrisker.oppfrisk(nøkkel("geoGrupper"))

                verify { entra.geoGrupper(ansattId, nyOid) }
            }

            it("teller ved vellykket tøm-og-oppfrisk") {
                every { entra.geoGrupper(ansattId, oid) } throws NotFoundRestException(URI.create("http://entra"))
                every { oidTjeneste.oidFraEntra(ansattId) } returnsMany listOf(oid, nyOid)

                oppfrisker.oppfrisk(nøkkel("geoGrupper"))

                verify { teller.tell() }
            }

            it("fungerer også for geoOgGlobaleGrupper") {
                every { entra.geoOgGlobaleGrupper(ansattId, oid) } throws NotFoundRestException(URI.create("http://entra"))
                every { oidTjeneste.oidFraEntra(ansattId) } returnsMany listOf(oid, nyOid)

                oppfrisker.oppfrisk(nøkkel("geoOgGlobaleGrupper"))

                verify { cache.delete(OID_CACHE, ansattId.verdi) }
                verify { entra.geoOgGlobaleGrupper(ansattId, nyOid) }
                verify { teller.tell() }
            }
        }

        describe("annen exception") {

            it("teller ikke og sletter ikke cache ved RuntimeException") {
                every { entra.geoGrupper(ansattId, oid) } throws RuntimeException("noe gikk galt")

                oppfrisker.oppfrisk(nøkkel("geoGrupper"))

                verify(exactly = 0) { teller.tell() }
                verify(exactly = 0) { cache.delete(any(), any()) }
            }
        }
    }
})
