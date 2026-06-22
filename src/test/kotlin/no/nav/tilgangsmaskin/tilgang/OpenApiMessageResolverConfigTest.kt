package no.nav.tilgangsmaskin.tilgang

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.Paths
import io.swagger.v3.oas.models.tags.Tag
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
import org.springframework.boot.test.context.runner.ApplicationContextRunner

class OpenApiMessageResolverConfigTest : BehaviorSpec({

    val contextRunner = ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(MessageSourceAutoConfiguration::class.java))
        .withUserConfiguration(OpenApiMessageResolverConfig::class.java)
        .withPropertyValues(
            "spring.messages.basename=messages,openapi-prod-tilgang,openapi/dev/regel",
        )

    fun openApiWith(
        tagDescription: String,
        summary: String,
        description: String,
    ) = OpenAPI()
        .tags(listOf(Tag().name("test-tag").description(tagDescription)))
        .paths(
            Paths().addPathItem(
                "/test",
                PathItem().get(Operation().summary(summary).description(description)),
            )
        )

    Given("openApiMessageCustomizer") {
        When("tag description, summary og description er msg-prefikset") {
            Then("resolves alle verdier via Boot-injisert MessageSource") {
                contextRunner.run { context ->
                    val customizer = context.getBean(OpenApiCustomizer::class.java)
                    val openApi = openApiWith(
                        tagDescription = "msg:openapi.dev.regel.tag.description",
                        summary = "msg:openapi.tilgang.komplett.obo.summary",
                        description = "msg:openapi.dev.regel.komplett.description",
                    )

                    customizer.customise(openApi)

                    openApi.tags.single().description shouldBe "Denne kontrolleren skal kun brukes til testing"
                    val operation = openApi.paths["/test"]!!.get
                    operation.summary shouldBe "Evaluer komplett regelsett for bruker"
                    operation.description shouldBe "Kjorer komplett regelsett for oppgitt ansatt og bruker i dev."
                }
            }
        }

        When("verdier ikke er msg-prefikset") {
            Then("forblir de uendret") {
                contextRunner.run { context ->
                    val customizer = context.getBean(OpenApiCustomizer::class.java)
                    val openApi = openApiWith(
                        tagDescription = "Plain tag description",
                        summary = "Plain summary",
                        description = "Plain description",
                    )

                    customizer.customise(openApi)

                    openApi.tags.single().description shouldBe "Plain tag description"
                    val operation = openApi.paths["/test"]!!.get
                    operation.summary shouldBe "Plain summary"
                    operation.description shouldBe "Plain description"
                }
            }
        }
    }
})
