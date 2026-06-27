package no.nav.tilgangsmaskin.felles.kafka

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldNotBe
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer
import tools.jackson.databind.json.JsonMapper

class ErrorHandlingDeserializerTest : BehaviorSpec({

    val mapper = JsonMapper.builder().build()
    val deserializer = ErrorHandlingDeserializer(JacksonJsonDeserializer(mapper))
    val topic = "test-topic"

    Given("ErrorHandlingDeserializer wrapping JacksonJsonDeserializer") {

        When("deserialize kalles med gyldig JSON") {
            Then("skal returnere deserialisert objekt") {
                val json = """{"hendelse":"test"}""".toByteArray()
                val result = deserializer.deserialize(topic, json)
                result shouldNotBe null
            }
        }

        When("deserialize kalles med ugyldig JSON") {
            Then("skal ikke kaste exception, men logge feilen") {
                val invalidJson = "{invalid json}".toByteArray()
                val result = deserializer.deserialize(topic, invalidJson)
                result shouldNotBe null
            }
        }

        When("deserialize kalles med tom byte array") {
            Then("skal ikke kaste exception") {
                val emptyBytes = byteArrayOf()
                val result = deserializer.deserialize(topic, emptyBytes)
                result shouldNotBe null
            }
        }
    }
})

