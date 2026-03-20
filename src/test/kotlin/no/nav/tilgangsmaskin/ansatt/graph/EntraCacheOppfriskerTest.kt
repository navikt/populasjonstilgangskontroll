package no.nav.tilgangsmaskin.ansatt.graph

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.OID_CACHE
import no.nav.tilgangsmaskin.felles.cache.CacheClient
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelElementer
import no.nav.tilgangsmaskin.felles.rest.NotFoundRestException
import no.nav.tilgangsmaskin.regler.motor.OppfriskingTeller
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import java.net.URI
import java.util.UUID

@ApplyExtension(SpringExtension::class)
@RestClientTest(components = [EntraCacheOppfrisker::class])
class EntraCacheOppfriskerTest : DescribeSpec() {

    @MockkBean(relaxed = true)
    private lateinit var entra: EntraTjeneste

    @MockkBean
    private lateinit var oidTjeneste: AnsattOidTjeneste

    @MockkBean(relaxed = true)
    private lateinit var cache: CacheClient

    @MockkBean
    private lateinit var teller: OppfriskingTeller

    @Autowired
    private lateinit var oppfrisker: EntraCacheOppfrisker

    private val ansattId = AnsattId("Z999999")

    private fun nøkkel(metode: String) = CacheNøkkelElementer("graph::$metode:${ansattId.verdi}")

    init {
        beforeEach {
            clearMocks(entra, oidTjeneste, cache, teller)
            every { oidTjeneste.oidFraEntra(ansattId) } returns OID
        }

        describe("oppfrisk") {

            describe("geoGrupper") {
                it("kaller entra.geoGrupper med riktig ansattId og oid") {
                    oppfrisker.oppfrisk(nøkkel("geoGrupper"))

                    verify { entra.geoGrupper(ansattId, OID) }
                    verify(exactly = 0) { entra.geoOgGlobaleGrupper(any(), any()) }
                }
            }

            describe("geoOgGlobaleGrupper") {
                it("kaller entra.geoOgGlobaleGrupper med riktig ansattId og oid") {
                    oppfrisker.oppfrisk(nøkkel("geoOgGlobaleGrupper"))

                    verify { entra.geoOgGlobaleGrupper(ansattId, OID) }
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
                beforeEach {
                    every { oidTjeneste.oidFraEntra(ansattId) } returnsMany listOf(OID, NY_OID)
                }

                describe("geoGrupper") {
                    beforeEach {
                        every { entra.geoGrupper(ansattId, OID) } throws NotFoundRestException(URI.create("http://entra"))
                    }

                    it("sletter OID-cache og henter ny OID") {
                        oppfrisker.oppfrisk(nøkkel("geoGrupper"))

                        verify { cache.delete(OID_CACHE, ansattId.verdi) }
                        verify(exactly = 2) { oidTjeneste.oidFraEntra(ansattId) }
                    }

                    it("kaller oppfrisk på nytt med ny OID") {
                        oppfrisker.oppfrisk(nøkkel("geoGrupper"))

                        verify { entra.geoGrupper(ansattId, NY_OID) }
                    }

                    it("teller") {
                        oppfrisker.oppfrisk(nøkkel("geoGrupper"))

                        verify { teller.tell() }
                    }
                }

                it("fungerer også for geoOgGlobaleGrupper") {
                    every { entra.geoOgGlobaleGrupper(ansattId, OID) } throws NotFoundRestException(URI.create("http://entra"))

                    oppfrisker.oppfrisk(nøkkel("geoOgGlobaleGrupper"))

                    verify { cache.delete(OID_CACHE, ansattId.verdi) }
                    verify { entra.geoOgGlobaleGrupper(ansattId, NY_OID) }
                    verify { teller.tell() }
                }
            }

            describe("annen exception") {
                it("teller ikke og sletter ikke cache ved RuntimeException") {
                    every { entra.geoGrupper(ansattId, OID) } throws RuntimeException("noe gikk galt")

                    oppfrisker.oppfrisk(nøkkel("geoGrupper"))

                    verify(exactly = 0) { teller.tell() }
                    verify(exactly = 0) { cache.delete(any(), any()) }
                }
            }
        }
    }

    companion object {
        private val OID = UUID.fromString("11111111-1111-1111-1111-111111111111")
        private val NY_OID = UUID.fromString("22222222-2222-2222-2222-222222222222")
    }
}
