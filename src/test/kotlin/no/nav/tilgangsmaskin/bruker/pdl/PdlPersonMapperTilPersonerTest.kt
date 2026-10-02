package no.nav.tilgangsmaskin.bruker.pdl

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTKommune
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.KOMMUNE
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPersoner
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapperTestFixture.BRUKER_ID
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapperTestFixture.identer
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapperTestFixture.pdlRespons

class PdlPersonMapperTilPersonerTest : BehaviorSpec({
    Given("tilPersoner") {
        When("flere responser") {
            Then("mappes til map keyed på oppslagId") {
                val brukerId2 = "20478606614"
                val aktorId2 = "9876543210987"
                val result = tilPersoner(
                    mapOf(
                        BRUKER_ID to pdlRespons(),
                        brukerId2 to pdlRespons(
                            identer = identer(fnr = brukerId2, aktor = aktorId2),
                            geo = PdlGeografiskTilknytning(KOMMUNE, gtKommune = GTKommune("0301")),
                        ),
                    )
                )

                assertSoftly(result) {
                    shouldHaveSize(2)
                    get(BRUKER_ID).shouldNotBeNull().brukerId shouldBe BrukerId(BRUKER_ID)
                    get(brukerId2).shouldNotBeNull().geoTilknytning.shouldBeInstanceOf<KommuneTilknytning>()
                }
            }
        }
        When("responser med null") {
            Then("filtreres null ut") {
                val result = tilPersoner(mapOf(BRUKER_ID to pdlRespons(), "ukjent" to null))
                assertSoftly(result) {
                    shouldHaveSize(1)
                    get(BRUKER_ID).shouldNotBeNull()
                }
            }
        }
        When("ingen responser") {
            Then("returneres tom map") {
                tilPersoner(emptyMap()).shouldBeEmpty()
            }
        }
    }
})
