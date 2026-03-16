package no.nav.tilgangsmaskin.ansatt

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.graph.EntraRestClientAdapter
import java.util.UUID

class AnsattOidTjenesteTest : DescribeSpec() {

    val adapter = mockk<EntraRestClientAdapter>(relaxed = true)
    val tjeneste = AnsattOidTjeneste(adapter)

    val ansattId = AnsattId("Z999999")

    init {
        describe("oidFraEntra") {

            it("returnerer oid fra adapter") {
                val oid = UUID.randomUUID()
                every { adapter.oidFraEntra(ansattId.verdi) } returns oid

                tjeneste.oidFraEntra(ansattId) shouldBe oid

                verify{ adapter.oidFraEntra(ansattId.verdi) }
            }

            it("propagerer exception fra adapter") {
                every { adapter.oidFraEntra(ansattId.verdi) } throws RuntimeException("Entra nede")

                shouldThrow<RuntimeException> {
                    tjeneste.oidFraEntra(ansattId)
                }
            }
        }
    }
}

