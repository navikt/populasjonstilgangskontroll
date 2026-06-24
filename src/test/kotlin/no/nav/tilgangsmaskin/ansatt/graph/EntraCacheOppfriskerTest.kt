package no.nav.tilgangsmaskin.ansatt.graph

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import io.mockk.verify
import io.kotest.assertions.throwables.shouldNotThrowAny
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraCacheOppfrisker.Companion.GEO
import no.nav.tilgangsmaskin.ansatt.graph.EntraCacheOppfrisker.Companion.GEO_OG_GLOBALE
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidConfig.Companion.OID_CACHE
import no.nav.tilgangsmaskin.felles.cache.CacheOppfriskerTeller
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkel
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.rest.NotFoundRestException
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
    private lateinit var oid: EntraOidTjeneste

    @MockkBean(relaxed = true)
    private lateinit var cache: CacheOperations

    @MockkBean
    private lateinit var teller: OIDEndringTeller

    @MockkBean(relaxed = true)
    private lateinit var cacheOppfriskerTeller: CacheOppfriskerTeller

    @Autowired
    private lateinit var oppfrisker: EntraCacheOppfrisker

    init {
        beforeEach {
            every { oid.oid(ansattId) } returns OID
        }

        Given("cache-innslag går ut på tid") {
            When("innslag var fra $GEO") {
                Then("kaller $GEO med riktig ansattId og oid") {
                    shouldNotThrowAny {
                        oppfrisker.oppfrisk(nøkkel(GEO))
                    }
                    verify { entra.geoGrupper(ansattId, OID) }
                    verify(exactly = 0) { entra.geoOgGlobaleGrupper(any(), any()) }
                }
            }
            When("innslaget var fra $GEO_OG_GLOBALE") {
                Then("kaller $GEO_OG_GLOBALE med riktig ansattId og oid") {
                    shouldNotThrowAny {
                        oppfrisker.oppfrisk(nøkkel(GEO_OG_GLOBALE))
                    }

                    verify { entra.geoOgGlobaleGrupper(ansattId, OID) }
                    verify(exactly = 0) { entra.geoGrupper(any(), any()) }
                }
            }

            When("ansatt ikke finnes i Entra") {
                beforeEach {
                    every { oid.oid(ansattId) } returnsMany listOf(OID, NY_OID)
                    every { entra.geoGrupper(ansattId, OID) } throws NotFoundRestException(URI.create("http://entra"))
                }

                Then("sletter OID-cache og henter ny OID") {
                    oppfrisker.oppfrisk(nøkkel(GEO))
                    verify { cache.delete(OID_CACHE, ansattId.verdi) }
                    verify(exactly = 2) { oid.oid(ansattId) }
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
                    oppfrisker.oppfrisk(nøkkel(GEO_OG_GLOBALE))

                    verify { entra.geoOgGlobaleGrupper(ansattId, OID) }
                    verify(exactly = 0) { entra.geoGrupper(any(), any()) }
                }
            }

            When("NotFoundRestException kastes") {
                Then("sletter OID-cache, henter ny OID og kaller oppfrisk pa nytt") {
                    every { oid.oid(ansattId) } returnsMany listOf(OID, NY_OID)
                    every { entra.geoOgGlobaleGrupper(ansattId, OID) } throws NotFoundRestException(URI.create("http://entra"))

                    oppfrisker.oppfrisk(nøkkel(GEO_OG_GLOBALE))

                    verify { cache.delete(OID_CACHE, ansattId.verdi) }
                    verify { entra.geoOgGlobaleGrupper(ansattId, NY_OID) }
                    verify { teller.tell() }
                }
            }
        }

        Given("doOppfrisk kaster uventet exception") {
            When("oidTjeneste.oid feiler") {
                Then("oppfrisk fanger exception og propagerer ikke") {
                    every { oid.oid(ansattId) } throws RuntimeException("oid-oppslag feilet")

                    shouldNotThrowAny {
                        oppfrisker.oppfrisk(nøkkel(GEO))
                    }

                    verify(exactly = 0) { entra.geoGrupper(any(), any()) }
                    verify(exactly = 0) { entra.geoOgGlobaleGrupper(any(), any()) }
                    verify(exactly = 0) { cache.delete(any(), any()) }
                    verify(exactly = 0) { teller.tell() }
                }
            }
        }

        Given("retry ved NotFoundRestException fra Entra") {
            When("geoGrupper kaster NotFoundRestException") {
                Then("sletter OID-cache, henter ny OID og kaller geoGrupper på nytt") {
                    every { oid.oid(ansattId) } returnsMany listOf(OID, NY_OID)
                    every { entra.geoGrupper(ansattId, OID) } throws NotFoundRestException(URI.create("http://entra"))

                    shouldNotThrowAny {
                        oppfrisker.oppfrisk(nøkkel(GEO))
                    }

                    verify { cache.delete(OID_CACHE, ansattId.verdi) }
                    verify(exactly = 2) { oid.oid(ansattId) }
                    verify { entra.geoGrupper(ansattId, NY_OID) }
                    verify { teller.tell() }
                }
            }

            When("geoOgGlobaleGrupper kaster NotFoundRestException") {
                Then("sletter OID-cache, henter ny OID og kaller geoOgGlobaleGrupper på nytt") {
                    every { oid.oid(ansattId) } returnsMany listOf(OID, NY_OID)
                    every { entra.geoOgGlobaleGrupper(ansattId, OID) } throws NotFoundRestException(URI.create("http://entra"))

                    shouldNotThrowAny {
                        oppfrisker.oppfrisk(nøkkel(GEO_OG_GLOBALE))
                    }

                    verify { cache.delete(OID_CACHE, ansattId.verdi) }
                    verify(exactly = 2) { oid.oid(ansattId) }
                    verify { entra.geoOgGlobaleGrupper(ansattId, NY_OID) }
                    verify { teller.tell() }
                }
            }

            When("retry også feiler med NotFoundRestException") {
                Then("oppfrisk fanger exception og propagerer ikke") {
                    every { oid.oid(ansattId) } returnsMany listOf(OID, NY_OID)
                    every { entra.geoGrupper(ansattId, OID) } throws NotFoundRestException(URI.create("http://entra"))
                    every { entra.geoGrupper(ansattId, NY_OID) } throws NotFoundRestException(URI.create("http://entra"))

                    shouldNotThrowAny {
                        oppfrisker.oppfrisk(nøkkel(GEO))
                    }

                    verify { cache.delete(OID_CACHE, ansattId.verdi) }
                    verify(exactly = 0) { teller.tell() }
                }
            }
        }

        Given("ukjent metode") {
            When("oppfrisk kalles") {
                Then("kaller verken geoGrupper eller geoOgGlobaleGrupper") {
                    oppfrisker.oppfrisk(CacheNøkkel("graph::ukjentMetode:${ansattId.verdi}"))

                    verify(exactly = 0) { entra.geoGrupper(any(), any()) }
                    verify(exactly = 0) { entra.geoOgGlobaleGrupper(any(), any()) }
                }
            }
        }
    }

    private companion object {
        private val ansattId = AnsattId("Z999999")

        private val OID = UUID.randomUUID()
        private val NY_OID = UUID.randomUUID()
        private fun nøkkel(metode: String) = CacheNøkkel("graph::$metode:${ansattId.verdi}")
    }
}
