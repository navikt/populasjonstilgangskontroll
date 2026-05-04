package no.nav.tilgangsmaskin.regler.motor

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.TestPropertySource
import java.util.UUID

@SpringBootTest(classes = [GlobaleGrupperConfigTest.TestConfig::class])
@TestPropertySource(locations = ["classpath:test.properties"])
@ApplyExtension(SpringExtension::class)
class GlobaleGrupperConfigTest : BehaviorSpec() {

    @Configuration
    @EnableConfigurationProperties(GlobaleGrupperConfig::class)
    class TestConfig

    @Autowired
    lateinit var cfg: GlobaleGrupperConfig

    init {
        Given("GlobaleGrupperConfig lastet fra properties") {
            When("config er bundet") {
                Then("bindes alle UUID-ene korrekt") {
                    cfg.strengt   shouldBe UUID.fromString("5ef775f2-61f8-4283-bf3d-8d03f428aa14")
                    cfg.nasjonal  shouldBe UUID.fromString("c7107487-310d-4c06-83e0-cf5395dc3be3")
                    cfg.utland    shouldBe UUID.fromString("de62a4bf-957b-4cde-acdb-6d8bcbf821a0")
                    cfg.udefinert shouldBe UUID.fromString("35d9d1ac-7fcb-4a22-9155-e0d1e57898a8")
                    cfg.fortrolig shouldBe UUID.fromString("ea930b6b-9397-44d9-b9e6-f4cf527a632a")
                    cfg.egenansatt shouldBe UUID.fromString("dbe4ad45-320b-4e9a-aaa1-73cca4ee124d")
                }
            }

            When("@PostConstruct er kjørt") {
                Then("settes STRENGT_FORTROLIG-IDen på GlobalGruppe") {
                    GlobalGruppe.STRENGT_FORTROLIG.id shouldBe cfg.strengt
                }
                Then("settes NASJONAL-IDen på GlobalGruppe") {
                    GlobalGruppe.NASJONAL.id shouldBe cfg.nasjonal
                }
                Then("settes UTENLANDSK-IDen på GlobalGruppe") {
                    GlobalGruppe.UTENLANDSK.id shouldBe cfg.utland
                }
                Then("settes UKJENT_BOSTED-IDen på GlobalGruppe") {
                    GlobalGruppe.UKJENT_BOSTED.id shouldBe cfg.udefinert
                }
                Then("settes FORTROLIG-IDen på GlobalGruppe") {
                    GlobalGruppe.FORTROLIG.id shouldBe cfg.fortrolig
                }
                Then("settes SKJERMING-IDen på GlobalGruppe") {
                    GlobalGruppe.SKJERMING.id shouldBe cfg.egenansatt
                }
            }
        }
    }
}




