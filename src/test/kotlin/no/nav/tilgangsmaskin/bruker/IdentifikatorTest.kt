package no.nav.tilgangsmaskin.bruker

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import no.nav.sikkerhetstjenesten.felles.cache.CacheBeanConfig.Companion.VALKEY_MAPPER
import no.nav.sikkerhetstjenesten.felles.utils.cluster.ClusterUtils
import no.nav.sikkerhetstjenesten.felles.utils.cluster.ClusterUtils.Companion.isProd
import no.nav.tilgangsmaskin.ansatt.AnsattId
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule

class BrukerIdTest : BehaviorSpec({

    Given("identifikatorer i opprinnelig namespace") {
        Then("beholdes klassenavn og JSON-format") {
            val mapper = JsonMapper.builder().addModule(KotlinModule.Builder().build()).build()
            val ansattId = AnsattId("Z999999")
            val brukerId = BrukerId("08526835671")

            ansattId.javaClass.name shouldBe "no.nav.tilgangsmaskin.ansatt.AnsattId"
            brukerId.javaClass.name shouldBe "no.nav.tilgangsmaskin.bruker.BrukerId"
            mapper.writeValueAsString(ansattId) shouldBe "\"Z999999\""
            mapper.writeValueAsString(brukerId) shouldBe "\"08526835671\""
            mapper.readValue("\"Z999999\"", AnsattId::class.java) shouldBe ansattId
            mapper.readValue("\"08526835671\"", BrukerId::class.java) shouldBe brukerId
        }

        Then("kan identifikatorene leses fra Valkey med opprinnelige klassenavn") {
            val serializer = GenericJacksonJsonRedisSerializer(VALKEY_MAPPER)
            val ansattId = AnsattId("Z999999")
            val brukerId = BrukerId("08526835671")

            serializer.deserialize(serializer.serialize(ansattId)) shouldBe ansattId
            serializer.deserialize(serializer.serialize(brukerId)) shouldBe brukerId
            serializer.deserialize(
                """["no.nav.tilgangsmaskin.ansatt.AnsattId","Z999999"]""".toByteArray()
            ) shouldBe ansattId
            serializer.deserialize(
                """["no.nav.tilgangsmaskin.bruker.BrukerId","08526835671"]""".toByteArray()
            ) shouldBe brukerId
        }
    }

    Given("Identifikator") {
        When("verdien er gyldig AktørId (13 siffer)") {
            Then("aksepteres") {
                shouldNotThrowAny {
                    Identifikator("1234567890123")
                }
            }
        }
        When("verdien er gyldig BrukerId (11 siffer)") {
            Then("aksepteres") {
                shouldNotThrowAny {
                    Identifikator("08526835671")
                }
            }
        }
        When("verdien er verken AktørId eller BrukerId") {
            Then("kastes IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> {
                    Identifikator("abc") }
            }
        }
    }

    Given("AktørId") {
        When("verdien er 13 siffer") {
            Then("aksepteres") {
                shouldNotThrowAny {
                    AktørId("1234567890123")
                }
            }
        }
        When("verdien inneholder ikke-numeriske tegn") {
            Then("kastes IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> {
                    AktørId("123456789012a")
                }
            }
        }
        When("verdien har feil lengde") {
            Then("kastes IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> {
                    AktørId("123456789")
                }
            }
        }
    }

    Given("BrukerId") {
        When("verdien er gyldig fødselsnummer") {
            Then("opprettes uten feil") {
                shouldNotThrowAny {
                    BrukerId("08526835671")
                }
            }
        }
        When("verdien har ugyldig lengde") {
            Then("kastes IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> {
                    BrukerId("111")
                }
            }
        }
        When("verdien inneholder ikke bare tall") {
            Then("kastes IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> {
                    BrukerId("1111111111a")
                }
            }
        }
    }

    Given("BrukerId i prod - mod11") {
        beforeEach {
            mockkObject(ClusterUtils)
            every { isProd } returns true
        }
        afterEach { unmockkObject(ClusterUtils) }

        When("W1=0 og W2=0") {
            Then("aksepteres (00000000000)") {
                shouldNotThrowAny {
                    BrukerId("00000000000")
                }
            }
        }
        When("vanlig gyldig fødselsnummer") {
            Then("aksepteres (08526835671)") {
                shouldNotThrowAny {
                    BrukerId("08526835671")
                }
            }
        }
        When("kontrollsiffer 1 er feil") {
            Then("kastes IllegalArgumentException (08526835682)") {
                shouldThrow<IllegalArgumentException> {
                    BrukerId("08526835682")
                }
            }
        }
        When("kontrollsiffer 2 er feil") {
            Then("kastes IllegalArgumentException (10000000910)") {
                shouldThrow<IllegalArgumentException> {
                    BrukerId("10000000910")
                }
            }
        }
        When("W2 kontrollsiffer matcher ikke") {
            Then("kastes IllegalArgumentException (08526835672)") {
                shouldThrow<IllegalArgumentException> {
                    BrukerId("08526835672")
                }
            }
        }
    }
})
