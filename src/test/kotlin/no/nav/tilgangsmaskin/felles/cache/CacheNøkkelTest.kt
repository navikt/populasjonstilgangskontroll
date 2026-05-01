package no.nav.tilgangsmaskin.felles.cache

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class CacheNøkkelTest : BehaviorSpec({

    Given("format med metode: cacheName::metode:id") {
        val nøkkel = CacheNøkkel("graph::geoGrupper:Z999999")
        When("nøkkelen parses") {
            Then("cacheName er korrekt") { nøkkel.cacheName shouldBe "graph" }
            Then("metode er korrekt") { nøkkel.metode shouldBe "geoGrupper" }
            Then("id er korrekt") { nøkkel.id shouldBe "Z999999" }
            Then("masked er uendret (kort id)") { nøkkel.masked shouldBe "graph::geoGrupper:Z999999" }
        }
    }

    Given("format uten metode: cacheName::id") {
        val nøkkel = CacheNøkkel("oppfolging::08526835670")
        When("nøkkelen parses") {
            Then("cacheName er korrekt") { nøkkel.cacheName shouldBe "oppfolging" }
            Then("metode er null") { nøkkel.metode shouldBe null }
            Then("id er korrekt") { nøkkel.id shouldBe "08526835670" }
            Then("masked maskerer fnr") { nøkkel.masked shouldBe "oppfolging::0852*******" }
        }
    }

    Given("maskering") {
        When("id er 11-sifret fnr") {
            Then("maskeres fnr i masked") {
                CacheNøkkel("skjerming::08526835670").masked shouldBe "skjerming::0852*******"
            }
        }
        When("id er 13-sifret aktørId") {
            Then("maskeres aktørId i masked") {
                CacheNøkkel("pdl::1234567890123").masked shouldBe "pdl::123456*******"
            }
        }
        When("id er kort (ikke fnr eller aktørId)") {
            Then("maskeres ikke id") {
                CacheNøkkel("nom::Z999999").masked shouldBe "nom::Z999999"
            }
        }
        When("metode er satt og id er fnr") {
            Then("maskeres fnr også i masked") {
                CacheNøkkel("pdl::medFamilie:08526835670").masked shouldBe "pdl::medFamilie:0852*******"
            }
        }
    }

    Given("nøkkel bevares") {
        When("CacheNøkkel opprettes") {
            Then("er nøkkel uendret") {
                val nøkkel = "graph::geoGrupper:Z999999"
                CacheNøkkel(nøkkel).nøkkel shouldBe nøkkel
            }
        }
    }
})
