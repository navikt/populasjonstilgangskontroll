package no.nav.tilgangsmaskin.felles.cache

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class CacheNøkkelTest : BehaviorSpec({

    Given("format med metode: cacheName::metode:id") {

        val nøkkel = CacheNøkkel("graph::geoGrupper:Z999999")

        When("cacheName parses") {
            Then("cacheName er graph") { nøkkel.cacheName shouldBe "graph" }
        }
        When("metode parses") {
            Then("metode er geoGrupper") { nøkkel.metode shouldBe "geoGrupper" }
        }
        When("id parses") {
            Then("id er Z999999") { nøkkel.id shouldBe "Z999999" }
        }
        When("masked genereres") {
            Then("id maskeres ikke for kort id") { nøkkel.masked shouldBe "graph::geoGrupper:Z999999" }
        }
    }

    Given("format uten metode: cacheName::id") {

        val nøkkel = CacheNøkkel("oppfolging::08526835670")

        When("cacheName parses") {
            Then("cacheName er oppfolging") { nøkkel.cacheName shouldBe "oppfolging" }
        }
        When("metode parses") {
            Then("metode er null") { nøkkel.metode shouldBe null }
        }
        When("id parses") {
            Then("id er fnr") { nøkkel.id shouldBe "08526835670" }
        }
        When("masked genereres") {
            Then("fnr maskeres") { nøkkel.masked shouldBe "oppfolging::0852*******" }
        }
    }

    Given("maskering") {
        When("11-sifret fnr i id") {
            Then("maskeres") { CacheNøkkel("skjerming::08526835670").masked shouldBe "skjerming::0852*******" }
        }
        When("13-sifret aktørId i id") {
            Then("maskeres") { CacheNøkkel("pdl::1234567890123").masked shouldBe "pdl::123456*******" }
        }
        When("kort id") {
            Then("maskeres ikke") { CacheNøkkel("nom::Z999999").masked shouldBe "nom::Z999999" }
        }
        When("fnr i id med metode satt") {
            Then("maskeres") { CacheNøkkel("pdl::medFamilie:08526835670").masked shouldBe "pdl::medFamilie:0852*******" }
        }
    }

    Given("nøkkel bevares") {
        When("nøkkel opprettes") {
            Then("originalnøkkel er uendret") {
                val nøkkel = "graph::geoGrupper:Z999999"
                CacheNøkkel(nøkkel).verdi shouldBe nøkkel
            }
        }
    }
})

