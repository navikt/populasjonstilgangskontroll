package no.nav.tilgangsmaskin.felles.cache

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidConfig.Companion.OID_CACHE
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL_MED_FAMILIE_CACHE

class CacheNøkkelMapperTest : BehaviorSpec({
    val id = "01011111111"

    Given("en cache uten ekstra-prefiks") {
        When("tilNøkkel kalles") {
            Then("legger til prefiks og id") {
                OID_CACHE.tilNøkkel(id) shouldBe "${OID_CACHE.name}::$id"
            }
        }
    }

    Given("en cache med ekstra-prefiks") {
        When("tilNøkkel kalles") {
            Then("legger til prefiks, ekstra-prefiks og id") {
                PDL_MED_FAMILIE_CACHE.tilNøkkel(id) shouldBe "${PDL_MED_FAMILIE_CACHE.name}::${PDL_MED_FAMILIE_CACHE.extraPrefix}:$id"
            }
        }
    }
})
