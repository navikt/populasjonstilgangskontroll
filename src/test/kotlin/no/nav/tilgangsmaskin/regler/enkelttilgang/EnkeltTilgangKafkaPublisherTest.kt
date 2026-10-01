package no.nav.tilgangsmaskin.regler.enkelttilgang

import no.nav.sikkerhetstjenesten.felles.domain.AnsattId

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.core.KafkaOperations
import org.springframework.kafka.support.SendResult
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeoutException
import java.util.concurrent.TimeUnit.SECONDS

class EnkeltTilgangKafkaPublisherTest : BehaviorSpec({
    val kafka = mockk<KafkaOperations<String, Any>>()
    val future = mockk<CompletableFuture<SendResult<String, Any>>>()
    val publisher = EnkeltTilgangHendelseProdusent(kafka)

    beforeEach {
        clearMocks(kafka, future)
    }

    Given("publisering av enkelttilgang") {
        When("en ansattident publiseres") {
            Then("sendes identen som key med null value og venter på bekreftelse") {
                every { kafka.sendDefault("Z123456", AnsattId("Z123456")) } returns future
                every { future.get(10, SECONDS) } returns mockk()

                publisher.publiser(AnsattId("Z123456"))

                verify(exactly = 1) { kafka.sendDefault("Z123456", AnsattId("Z123456")) }
                verify(exactly = 1) { future.get(10, SECONDS) }
            }

            Then("logges feil hvis publisering feiler") {
                every { kafka.sendDefault("Z123456", AnsattId("Z123456")) } returns future
                every { future.get(10, SECONDS) } throws TimeoutException("Kafka timeout")

                publisher.publiser(AnsattId("Z123456"))

                verify(exactly = 1) { future.get(10, SECONDS) }
            }
        }
    }
})
