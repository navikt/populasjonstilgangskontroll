package no.nav.tilgangsmaskin.felles.cache

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class CacheNøkkelElementerTest : DescribeSpec({

    describe("format med metode: cacheName::metode:id") {

        val nøkkel = CacheNøkkel("graph::geoGrupper:Z999999")

        it("parser cacheName") {
            nøkkel.cacheName shouldBe "graph"
        }

        it("parser metode") {
            nøkkel.metode shouldBe "geoGrupper"
        }

        it("parser id") {
            nøkkel.id shouldBe "Z999999"
        }

        it("maskerer id i masked") {
            nøkkel.masked shouldBe "graph::geoGrupper:Z999999"
        }
    }

    describe("format uten metode: cacheName::id") {

        val nøkkel = CacheNøkkel("oppfolging::08526835670")

        it("parser cacheName") {
            nøkkel.cacheName shouldBe "oppfolging"
        }

        it("metode er null") {
            nøkkel.metode shouldBe null
        }

        it("parser id") {
            nøkkel.id shouldBe "08526835670"
        }

        it("maskerer fnr i masked") {
            nøkkel.masked shouldBe "oppfolging::0852*******"
        }
    }

    describe("maskering") {

        it("maskerer 11-sifret fnr i id") {
            val nøkkel = CacheNøkkel("skjerming::08526835670")
            nøkkel.masked shouldBe "skjerming::0852*******"
        }

        it("maskerer 13-sifret aktørId i id") {
            val elementer = CacheNøkkel("pdl::1234567890123")
            elementer.masked shouldBe "pdl::123456*******"
        }

        it("maskerer ikke kort id") {
            val elementer = CacheNøkkel("nom::Z999999")
            elementer.masked shouldBe "nom::Z999999"
        }

        it("maskerer fnr i id også når metode er satt") {
            val nøkkel = CacheNøkkel("pdl::medFamilie:08526835670")
            nøkkel.masked shouldBe "pdl::medFamilie:0852*******"
        }
    }

    describe("nøkkel bevares") {

        it("nøkkel er uendret") {
            val nøkkel = "graph::geoGrupper:Z999999"
            CacheNøkkel(nøkkel).nøkkel shouldBe nøkkel
        }
    }
})

