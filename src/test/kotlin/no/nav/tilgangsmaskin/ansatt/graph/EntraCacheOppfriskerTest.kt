package no.nav.tilgangsmaskin.ansatt.graph

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraCacheOppfrisker.Companion.GEO
import no.nav.tilgangsmaskin.ansatt.graph.EntraCacheOppfrisker.Companion.GEO_OG_GLOBALE
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
class EntraCacheOppfriskerTest : BehaviorSpec() {

    @MockkBean(relaxed = true)
    private lateinit var entra: EntraTjeneste

    @MockkBean
    private lateinit var oid: AnsattOidTjeneste

    @MockkBean(relaxed = true)
    private lateinit var cache: CacheClient

    @MockkBean
    private lateinit var teller: OppfriskingTeller

    @Autowired
    private lateinit var oppfrisker: EntraCacheOppfrisker

    init {
        beforeEach {
            clearMocks(entra, oid, cache, teller)
            every { oid.oidFraEntra(ansattId) } returns OID
        }

        Given("cache-innslag går ut på tid") {
            When("innslag var fra $GEO") {
                Then("kaller $GEO med riktig ansattId og oid") {
                    oppfrisker.oppfrisk(nøkkel(GEO))

                    verify { entra.geoGrupper(ansattId, OID) }
                    verify(exactly = 0) { entra.geoOgGlobaleGrupper(any(), any()) }
                }
            }
            When("innslaget var fra $GEO_OG_GLOBALE") {
                Then("kaller $GEO_OG_GLOBALE med riktig ansattId og oid") {
                    oppfrisker.oppfrisk(nøkkel(GEO_OG_GLOBALE))

                    verify { entra.geoOgGlobaleGrupper(ansattId, OID) }
                    verify(exactly = 0) { entra.geoGrupper(any(), any()) }
                }
            }

            When("ansatt ikke finnes i Entra") {
                beforeEach {
                    every { oid.oidFraEntra(ansattId) } returnsMany listOf(OID, NY_OID)
                    every { entra.geoGrupper(ansattId, OID) } throws NotFoundRestException(URI.create("http://entra"))
                }

                Then("sletter OID-cache og henter ny OID") {
                    oppfrisker.oppfrisk(nøkkel("geoGrupper"))
                    verify { cache.delete(OID_CACHE, ansattId.verdi) }
                    verify(exactly = 2) { oid.oidFraEntra(ansattId) }
                }

                Then("kaller oppfrisk pa nytt med ny OID") {
                    oppfrisker.oppfrisk(nøkkel(GEO))
                    verify { entra.geoGrupper(ansattId, NY_OID) }
                }

                Then("teller oppdateres") {
                    oppfrisker.oppfrisk(nøkkel(GEO))
                    verify { teller.tell() }
                }
            }

            When("Uforventet feil ved oppslag i Entra") {
                Then("teller ikke og sletter ikke cache") {
                    every { entra.geoGrupper(ansattId, OID) } throws RuntimeException("noe gikk galt")

                    oppfrisker.oppfrisk(nøkkel(GEO))

                    verify(exactly = 0) { teller.tell() }
                    verify(exactly = 0) { cache.delete(any(), any()) }
                }
            }
        }

        Given("geoOgGlobaleGrupper") {
            When("oppfrisk kalles") {
                Then("kaller entra.geoOgGlobaleGrupper med riktig ansattId og oid") {
                    oppfrisker.oppfrisk(nøkkel("geoOgGlobaleGrupper"))

                    verify { entra.geoOgGlobaleGrupper(ansattId, OID) }
                    verify(exactly = 0) { entra.geoGrupper(any(), any()) }
                }
            }

            When("NotFoundRestException kastes") {
                Then("sletter OID-cache, henter ny OID og kaller oppfrisk pa nytt") {
                    every { oid.oidFraEntra(ansattId) } returnsMany listOf(OID, NY_OID)
                    every { entra.geoOgGlobaleGrupper(ansattId, OID) } throws NotFoundRestException(URI.create("http://entra"))

                    oppfrisker.oppfrisk(nøkkel("geoOgGlobaleGrupper"))

                    verify { cache.delete(OID_CACHE, ansattId.verdi) }
                    verify { entra.geoOgGlobaleGrupper(ansattId, NY_OID) }
                    verify { teller.tell() }
                }
            }
        }

        Given("ukjent metode") {
            When("oppfrisk kalles") {
                Then("kaller verken geoGrupper eller geoOgGlobaleGrupper") {
                    oppfrisker.oppfrisk(CacheNøkkelElementer("graph::ukjentMetode:${ansattId.verdi}"))

                    verify(exactly = 0) { entra.geoGrupper(any(), any()) }
                    verify(exactly = 0) { entra.geoOgGlobaleGrupper(any(), any()) }
                }
            }
        }
    }

    private companion object {
        private val ansattId = AnsattId("Z999999")

        private val OID = UUID.fromString("11111111-1111-1111-1111-111111111111")
        private val NY_OID = UUID.fromString("22222222-2222-2222-2222-222222222222")
        private fun nøkkel(metode: String) = CacheNøkkelElementer("graph::$metode:${ansattId.verdi}")
    }
}
