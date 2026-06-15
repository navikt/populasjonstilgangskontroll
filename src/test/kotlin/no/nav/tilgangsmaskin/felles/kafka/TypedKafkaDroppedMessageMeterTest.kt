package no.nav.tilgangsmaskin.felles.kafka

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.doubles.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.apache.kafka.clients.consumer.ConsumerRecord

class TypedKafkaDroppedMessageMeterTest : BehaviorSpec({

    val registry = SimpleMeterRegistry()

    Given("en TypedKafkaDroppedMessageMeter for TestHendelse") {
        val meter = TestHendelseMeter(registry)

        When("recovered kalles med riktig type") {
            val record = ConsumerRecord<Any, Any>("test-topic", 0, 42L, "key", TestHendelse("verdi"))
            val exception = RuntimeException("noe gikk galt")
            meter.recovered(record, exception)

            Then("skal metrikken inkrementeres") {
                val count = registry.counter(
                    "kafka.message.dropped",
                    "topic", "test-topic",
                    "partition", "0",
                    "exception", "RuntimeException"
                ).count()
                count shouldBeExactly 1.0
            }
        }

        When("recovered kalles med feil type") {
            val registryForFeilType = SimpleMeterRegistry()
            val meterForFeilType = TestHendelseMeter(registryForFeilType)
            val record = ConsumerRecord<Any, Any>("annen-topic", 0, 10L, "key", "ikke en TestHendelse")
            meterForFeilType.recovered(record, RuntimeException("feil"))

            Then("skal metrikken ikke inkrementeres") {
                val count = registryForFeilType.counter(
                    "kafka.message.dropped",
                    "topic", "annen-topic",
                    "partition", "0",
                    "exception", "RuntimeException"
                ).count()
                count shouldBeExactly 0.0
            }
        }

        When("failedDelivery kalles med riktig type") {
            Then("skal ikke kaste exception") {
                val record = ConsumerRecord<Any, Any>("test-topic", 0, 1L, "key", TestHendelse("retry"))
                meter.failedDelivery(record, RuntimeException("midlertidig feil"), 2)
            }
        }

        When("failedDelivery kalles med feil type") {
            Then("skal ignorere meldingen") {
                val record = ConsumerRecord<Any, Any>("test-topic", 0, 1L, "key", "feil type")
                meter.failedDelivery(record, RuntimeException("feil"), 1)
            }
        }
    }

    Given("formatEvent") {
        val meter = TestHendelseMeter(SimpleMeterRegistry())

        When("recovered kalles") {
            Then("skal bruke formatEvent for logging") {
                val record = ConsumerRecord<Any, Any>("test-topic", 0, 99L, "key", TestHendelse("hemmelig"))
                meter.recovered(record, RuntimeException("feil"))
                meter.lastFormatted shouldBe "felt=hemmelig"
            }
        }
    }
})

private data class TestHendelse(val felt: String)

private class TestHendelseMeter(registry: SimpleMeterRegistry) :
    KafkaTypedDroppedMessageMeter<TestHendelse>(registry, TestHendelse::class) {

    var lastFormatted: String? = null

    override fun formatEvent(event: TestHendelse): String {
        val formatted = "felt=${event.felt}"
        lastFormatted = formatted
        return formatted
    }
}

