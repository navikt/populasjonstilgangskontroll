package no.nav.tilgangsmaskin.ansatt.vergemål

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålConfig.Companion.VERGEMÅL
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest

@RestClientTest(components = [VergemålCacheOppfrisker::class])
@ApplyExtension(SpringExtension::class)
class VergemålCacheOppfriskerTest : BehaviorSpec() {

    @MockkBean(relaxed = true)
    private lateinit var vergemål: VergemålTjeneste

    @Autowired
    private lateinit var oppfrisker: VergemålCacheOppfrisker

    init {
        beforeEach { clearMocks(vergemål) }

        Given("cache-innslag for vergemål går ut på tid") {
            When("oppfrisk kalles") {
                Then("kaller vergemål.vergemål med riktig ansattId") {
                    oppfrisker.oppfrisk(nøkkel(ANSATT_ID))

                    verify { vergemål.vergemål(AnsattId(ANSATT_ID)) }
                }
            }

            When("vergemål.vergemål kaster exception") {
                Then("exception svelges — propageres ikke ut") {
                    every { vergemål.vergemål(AnsattId(ANSATT_ID)) } throws RuntimeException("connection refused")

                    shouldNotThrowAny { oppfrisker.oppfrisk(nøkkel(ANSATT_ID)) }
                }
            }
        }

        Given("oppfrisker-konfigurasjon") {
            Then("cacheName er $VERGEMÅL") {
                oppfrisker.cacheName shouldBe VERGEMÅL
            }
        }
    }

    private companion object {
        private const val ANSATT_ID = "Z999999"
        private fun nøkkel(id: String) = CacheNøkkel("$VERGEMÅL::$id")
    }
}



