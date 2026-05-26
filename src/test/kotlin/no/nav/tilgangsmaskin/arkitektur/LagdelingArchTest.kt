package no.nav.tilgangsmaskin.arkitektur

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import com.tngtech.archunit.library.freeze.FreezingArchRule

/**
 * Arkitektur-tester som beskytter mot regresjon av lagdelingen.
 *
 * Reglene er pakket inn i [FreezingArchRule] - eksisterende brudd er fryst som baseline
 * i `src/test/resources/archunit_store/` og blokkerer ikke bygget.
 * Nye brudd vil derimot faa testen til aa feile.
 *
 * For aa oppdatere baseline naar et eksisterende brudd er fikset:
 *   `./gradlew test --tests "*LagdelingArchTest*" -Darchunit.freeze.refreeze=true`
 *
 * For aa forby ALLE nye brudd (default i CI):
 *   ingen flagg trengs - `archunit.freeze.refreeze=false` er default.
 */
@AnalyzeClasses(
    packages = ["no.nav.tilgangsmaskin"],
    importOptions = [ImportOption.DoNotIncludeTests::class, ImportOption.DoNotIncludeJars::class],
)
class LagdelingArchTest {

    @ArchTest
    val httpStatusKunIWebLaget: ArchRule = FreezingArchRule.freeze(
        noClasses()
            .that()
            .resideInAnyPackage("..regler..", "..bruker..", "..ansatt..", "..populasjon..")
            .and()
            .resideOutsideOfPackages("..tilgang..", "..felles.rest..")
            // HTTP-klienter, klient-adaptere og REST-feilhaandterere er semantisk
            // web-grensesnitt og maa kjenne til Spring HTTP-API selv om de ikke ligger i
            // tilgang-pakken. De er unntatt fra denne regelen.
            .and()
            .haveSimpleNameNotEndingWith("Client")
            .and()
            .haveSimpleNameNotEndingWith("ClientAdapter")
            .and()
            .haveSimpleNameNotEndingWith("ErrorHandler")
            .and()
            .haveSimpleNameNotEndingWith("RestClientAdapter")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                "org.springframework.http..",
                "org.springframework.web.bind..",
                "org.springframework.web.servlet..",
                "jakarta.servlet..",
            )
            .because(
                "Web-konserner skal vaere isolert til controller-laget og HTTP-klient-laget; " +
                    "domeneklasser og services skal kaste business-exceptions i stedet for aa referere HTTP-status."
            )
    )

    @ArchTest
    val jpaKunIPersistensLaget: ArchRule = FreezingArchRule.freeze(
        noClasses()
            .that()
            .haveSimpleNameNotEndingWith("Entity")
            .and()
            .haveSimpleNameNotEndingWith("JPAAdapter")
            .and()
            .haveSimpleNameNotEndingWith("Repository")
            .and()
            .haveSimpleNameNotEndingWith("EntityListener")
            .and()
            .haveSimpleNameNotEndingWith("Config")
            .and()
            .haveSimpleNameNotEndingWith("Configuration")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                "jakarta.persistence..",
                "org.hibernate..",
                "org.springframework.data.jpa..",
            )
            .because(
                "JPA-annotasjoner og EntityManager skal vaere isolert til *Entity, *JPAAdapter, " +
                    "*Repository, *EntityListener og JPA-config - services skal vaere persistens-agnostiske."
            )
    )

    @ArchTest
    val kafkaKunIKonsumentLaget: ArchRule = FreezingArchRule.freeze(
        noClasses()
            .that()
            .haveSimpleNameNotEndingWith("Konsument")
            .and()
            .haveSimpleNameNotEndingWith("Produsent")
            .and()
            .haveSimpleNameNotEndingWith("CacheOpprydder")
            .and()
            .haveSimpleNameNotEndingWith("Config")
            .and()
            .haveSimpleNameNotEndingWith("Configuration")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                "org.apache.kafka.clients..",
                "org.springframework.kafka..",
            )
            .because(
                "Kafka-API skal vaere isolert til *Konsument-, *Produsent- og *CacheOpprydder-klasser " +
                    "samt Kafka-config - services skal motta domeneobjekter, ikke ConsumerRecord."
            )
    )

    @ArchTest
    val controllereSkalIkkeInjisereRepositoriesDirekte: ArchRule = FreezingArchRule.freeze(
        noClasses()
            .that()
            .haveSimpleNameEndingWith("Controller")
            .should()
            .dependOnClassesThat()
            .haveSimpleNameEndingWith("Repository")
            .orShould()
            .dependOnClassesThat()
            .haveSimpleNameEndingWith("JPAAdapter")
            .because(
                "Controllere skal gaa via *Tjeneste for transaksjonshaandtering, caching og forretningslogikk."
            )
    )

    @ArchTest
    val ingenSykliskeAvhengigheterMellomToppnivaaPakker: ArchRule = FreezingArchRule.freeze(
        slices()
            .matching("no.nav.tilgangsmaskin.(*)..")
            .should()
            .beFreeOfCycles()
    )
}

