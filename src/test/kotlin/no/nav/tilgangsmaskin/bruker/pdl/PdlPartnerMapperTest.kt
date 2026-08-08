package no.nav.tilgangsmaskin.bruker.pdl

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.INGEN
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.PARTNER
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.TIDLIGERE_PARTNER
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.ENKE_ELLER_ENKEMANN
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.GIFT
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.GJENLEVENDE_PARTNER
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.REGISTRERT_PARTNER
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.SEPARERT
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.SEPARERT_PARTNER
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.SKILT
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.SKILT_PARTNER
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.UGIFT
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.UOPPGITT
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPartner

class PdlPartnerMapperTest : BehaviorSpec({
    Given("tilPartner") {
        When("GIFT") {
            Then("mappes til PARTNER") { tilPartner(GIFT) shouldBe PARTNER }
        }
        When("REGISTRERT_PARTNER") {
            Then("mappes til PARTNER") { tilPartner(REGISTRERT_PARTNER) shouldBe PARTNER }
        }
        When("SKILT") {
            Then("mappes til TIDLIGERE_PARTNER") { tilPartner(SKILT) shouldBe TIDLIGERE_PARTNER }
        }
        When("ENKE_ELLER_ENKEMANN") {
            Then("mappes til TIDLIGERE_PARTNER") { tilPartner(ENKE_ELLER_ENKEMANN) shouldBe TIDLIGERE_PARTNER }
        }
        When("SEPARERT") {
            Then("mappes til TIDLIGERE_PARTNER") { tilPartner(SEPARERT) shouldBe TIDLIGERE_PARTNER }
        }
        When("SKILT_PARTNER") {
            Then("mappes til TIDLIGERE_PARTNER") { tilPartner(SKILT_PARTNER) shouldBe TIDLIGERE_PARTNER }
        }
        When("GJENLEVENDE_PARTNER") {
            Then("mappes til TIDLIGERE_PARTNER") { tilPartner(GJENLEVENDE_PARTNER) shouldBe TIDLIGERE_PARTNER }
        }
        When("SEPARERT_PARTNER") {
            Then("mappes til TIDLIGERE_PARTNER") { tilPartner(SEPARERT_PARTNER) shouldBe TIDLIGERE_PARTNER }
        }
        When("UGIFT") {
            Then("mappes til INGEN") { tilPartner(UGIFT) shouldBe INGEN }
        }
        When("UOPPGITT") {
            Then("mappes til INGEN") { tilPartner(UOPPGITT) shouldBe INGEN }
        }
    }
})
