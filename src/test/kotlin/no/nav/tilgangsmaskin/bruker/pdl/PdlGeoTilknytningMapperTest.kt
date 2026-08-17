package no.nav.tilgangsmaskin.bruker.pdl

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.BydelTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UdefinertTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTBydel
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTKommune
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTLand
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.BYDEL
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.KOMMUNE
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.UDEFINERT
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.UTLAND
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilGeoTilknytning

class PdlGeoTilknytningMapperTest : BehaviorSpec({
    Given("tilGeoTilknytning") {
        When("input er null") {
            Then("mappes til UdefinertTilknytning") {
                tilGeoTilknytning(null).shouldBeInstanceOf<UdefinertTilknytning>()
            }
        }
        When("UDEFINERT") {
            Then("mappes til UdefinertTilknytning") {
                tilGeoTilknytning(PdlGeografiskTilknytning(UDEFINERT)).shouldBeInstanceOf<UdefinertTilknytning>()
            }
        }
        When("UTLAND med land") {
            Then("mappes til UtenlandskTilknytning") {
                tilGeoTilknytning(PdlGeografiskTilknytning(UTLAND, gtLand = GTLand("SWE"))).shouldBeInstanceOf<UtenlandskTilknytning>()
            }
        }
        When("UTLAND uten land") {
            Then("mappes til UkjentBosted") {
                tilGeoTilknytning(PdlGeografiskTilknytning(UTLAND)).shouldBeInstanceOf<UkjentBosted>()
            }
        }
        When("KOMMUNE med kode") {
            Then("mappes til KommuneTilknytning med riktig verdi") {
                tilGeoTilknytning(PdlGeografiskTilknytning(KOMMUNE, gtKommune = GTKommune("0301")))
                    .shouldBeInstanceOf<KommuneTilknytning>().kommune.verdi shouldBe "0301"
            }
        }
        When("KOMMUNE uten kode") {
            Then("mappes til UkjentBosted") {
                tilGeoTilknytning(PdlGeografiskTilknytning(KOMMUNE)).shouldBeInstanceOf<UkjentBosted>()
            }
        }
        When("BYDEL med kode") {
            Then("mappes til BydelTilknytning med riktig verdi") {
                tilGeoTilknytning(PdlGeografiskTilknytning(BYDEL, gtBydel = GTBydel("030101")))
                    .shouldBeInstanceOf<BydelTilknytning>().bydel.verdi shouldBe "030101"
            }
        }
        When("BYDEL med ugyldig kode") {
            Then("mappes til UkjentBosted") {
                tilGeoTilknytning(PdlGeografiskTilknytning(BYDEL, gtBydel = GTBydel("03010X"))).shouldBeInstanceOf<UkjentBosted>()
            }
        }
        When("BYDEL med 5 siffer i stedet for 6") {
            Then("mappes til UkjentBosted") {
                tilGeoTilknytning(PdlGeografiskTilknytning(BYDEL, gtBydel = GTBydel("03010"))).shouldBeInstanceOf<UkjentBosted>()
            }
        }
        When("BYDEL uten kode") {
            Then("mappes til UkjentBosted") {
                tilGeoTilknytning(PdlGeografiskTilknytning(BYDEL)).shouldBeInstanceOf<UkjentBosted>()
            }
        }
    }
})
