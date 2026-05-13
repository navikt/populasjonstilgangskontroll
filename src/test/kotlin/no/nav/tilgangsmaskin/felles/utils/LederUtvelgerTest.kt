package no.nav.tilgangsmaskin.felles.utils

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import no.nav.tilgangsmaskin.felles.utils.LederUtvelger.LeaderChangedEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import reactor.core.publisher.Mono
import java.net.URI
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger

class LederUtvelgerTest : BehaviorSpec({

    val publisher = mockk<ApplicationEventPublisher>(relaxed = true)

    fun sseBody(vararg navn: String) =
        navn.joinToString("") {
            """data: {"name":"$it","last_update":"${LocalDateTime.now()}"}""" + "\n\n"
        }

    fun lederUtvelger(body: String): LederUtvelger {
        val exchangeFn = ExchangeFunction {
            Mono.just(
                ClientResponse.create(OK)
                    .header("Content-Type", TEXT_EVENT_STREAM_VALUE)
                    .body(body)
                    .build()
            )
        }
        return LederUtvelger(
            WebClient.builder().exchangeFunction(exchangeFn),
            URI.create("http://elector/sse"),
            publisher
        )
    }

    beforeEach { clearMocks(publisher) }

    Given("onApplicationReady") {

        When("SSE-stream inneholder ett event") {
            Then("publiserer LeaderChangedEvent med riktig leder-navn") {
                val utvelger = lederUtvelger(sseBody("pod-abc-123"))
                utvelger.onApplicationReady()
                Thread.sleep(500)
                val event = slot<LeaderChangedEvent>()
                verify { publisher.publishEvent(capture(event)) }
                event.captured.leder shouldBe "pod-abc-123"
            }
        }

        When("SSE-stream inneholder flere events") {
            Then("publiserer LeaderChangedEvent for hvert event") {
                val utvelger = lederUtvelger(sseBody("pod-1", "pod-2", "pod-3"))
                utvelger.onApplicationReady()
                Thread.sleep(500)
                verify(exactly = 3) { publisher.publishEvent(any<LeaderChangedEvent>()) }
            }
        }

        When("SSE-stream er tom") {
            Then("publiserer ikke noe event") {
                val utvelger = lederUtvelger("")
                utvelger.onApplicationReady()
                Thread.sleep(500)
                verify(exactly = 0) { publisher.publishEvent(any<LeaderChangedEvent>()) }
            }
        }
    }

    Given("onPreDestroy") {

        When("kalt etter onApplicationReady") {
            Then("setter shuttingDown og disposer subscription") {
                val utvelger = lederUtvelger("")
                utvelger.onApplicationReady()
                Thread.sleep(100)
                utvelger.onPreDestroy()
                Thread.sleep(200)
                verify(exactly = 0) { publisher.publishEvent(any<LeaderChangedEvent>()) }
            }
        }
    }

    Given("onShutdown") {

        When("kalt etter onApplicationReady") {
            Then("setter shuttingDown og disposer subscription") {
                val utvelger = lederUtvelger("")
                utvelger.onApplicationReady()
                Thread.sleep(100)
                utvelger.onShutdown()
                Thread.sleep(200)
                verify(exactly = 0) { publisher.publishEvent(any<LeaderChangedEvent>()) }
            }
        }
    }

    Given("shuttingDown filter i retryWhen") {

        When("shuttingDown er true og WebClientRequestException kastes") {
            Then("hindrer retry og kaller exchange kun én gang") {
                val callCount = AtomicInteger(0)
                val failingExchange = ExchangeFunction { _ ->
                    callCount.incrementAndGet()
                    Mono.error(
                        WebClientRequestException(
                            RuntimeException("connection refused"),
                            GET,
                            URI.create("http://elector/sse"),
                            HttpHeaders()
                        )
                    )
                }
                val utvelger = LederUtvelger(
                    WebClient.builder().exchangeFunction(failingExchange),
                    URI.create("http://elector/sse"),
                    publisher
                )
                utvelger.onPreDestroy()
                utvelger.onApplicationReady()
                Thread.sleep(200)
                callCount.get() shouldBe 1
                verify(exactly = 0) { publisher.publishEvent(any<LeaderChangedEvent>()) }
            }
        }
    }
})
