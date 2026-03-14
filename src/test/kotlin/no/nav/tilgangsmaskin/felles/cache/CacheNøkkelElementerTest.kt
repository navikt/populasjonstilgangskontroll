package no.nav.tilgangsmaskin.felles.cache

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class CacheNøkkelElementerTest : DescribeSpec({

    describe("format med metode: cacheName::metode:id") {

        val elementer = CacheNøkkelElementer("graph::geoGrupper:Z999999")

        it("parser cacheName") {
            elementer.cacheName shouldBe "graph"
        }

        it("parser metode") {
            elementer.metode shouldBe "geoGrupper"
        }

        it("parser id") {
            elementer.id shouldBe "Z999999"
        }

        it("maskerer id i masked") {
            elementer.masked shouldBe "graph::geoGrupper:Z999999"
        }
    }

    describe("format uten metode: cacheName::id") {

        val elementer = CacheNøkkelElementer("oppfolging::08526835670")

        it("parser cacheName") {
            elementer.cacheName shouldBe "oppfolging"
        }

        it("metode er null") {
            elementer.metode shouldBe null
        }

        it("parser id") {
            elementer.id shouldBe "08526835670"
        }

        it("maskerer fnr i masked") {
            elementer.masked shouldBe "oppfolging::0852*******"
        }
    }

    describe("maskering") {

        it("maskerer 11-sifret fnr i id") {
            val elementer = CacheNøkkelElementer("skjerming::08526835670")
            elementer.masked shouldBe "skjerming::0852*******"
        }

        it("maskerer 13-sifret aktørId i id") {
            val elementer = CacheNøkkelElementer("pdl::1234567890123")
            elementer.masked shouldBe "pdl::123456*******"
        }

        it("maskerer ikke kort id") {
            val elementer = CacheNøkkelElementer("nom::Z999999")
            elementer.masked shouldBe "nom::Z999999"
        }

        it("maskerer fnr i id også når metode er satt") {
            val elementer = CacheNøkkelElementer("pdl::medFamilie:08526835670")
            elementer.masked shouldBe "pdl::medFamilie:0852*******"
        }
    }

    describe("nøkkel bevares") {

        it("nøkkel er uendret") {
            val nøkkel = "graph::geoGrupper:Z999999"
            CacheNøkkelElementer(nøkkel).nøkkel shouldBe nøkkel
        }
    }
})

