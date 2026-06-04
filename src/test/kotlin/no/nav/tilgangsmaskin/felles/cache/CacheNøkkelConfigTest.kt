package no.nav.tilgangsmaskin.felles.cache

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class CacheNøkkelConfigTest : BehaviorSpec({

    Given("tilNøkkel") {
        When("extraPrefix er null") {
            Then("returneres bare id") {
                val cfg = CacheNøkkelConfig("mycache")
                cfg.tilNøkkel("12345") shouldBe "12345"
            }
            Then("tom id gir tom streng") {
                val cfg = CacheNøkkelConfig("mycache")
                cfg.tilNøkkel("") shouldBe ""
            }
        }
        When("extraPrefix er satt") {
            Then("returneres prefix:id") {
                val cfg = CacheNøkkelConfig("mycache", "sub")
                cfg.tilNøkkel("12345") shouldBe "sub:12345"
            }
            Then("tom id gir bare prefix:") {
                val cfg = CacheNøkkelConfig("mycache", "sub")
                cfg.tilNøkkel("") shouldBe "sub:"
            }
        }
    }

    Given("fullName") {
        When("extraPrefix er null") {
            Then("returneres bare name") {
                CacheNøkkelConfig("mycache").fullName shouldBe "mycache"
            }
        }
        When("extraPrefix er satt") {
            Then("returneres name:prefix") {
                CacheNøkkelConfig("mycache", "sub").fullName shouldBe "mycache:sub"
            }
        }
    }
})

