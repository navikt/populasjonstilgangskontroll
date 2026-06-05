package no.nav.tilgangsmaskin.bruker.pdl

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import no.nav.person.pdl.leesah.Endringstype
import no.nav.person.pdl.leesah.Personhendelse
import no.nav.person.pdl.leesah.adressebeskyttelse.Adressebeskyttelse
import no.nav.person.pdl.leesah.adressebeskyttelse.Gradering
import org.apache.avro.io.DecoderFactory
import org.apache.avro.io.EncoderFactory
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.specific.SpecificDatumWriter
import java.io.ByteArrayOutputStream
import java.time.Instant

/**
 * Verifiserer at lokalt Avro-skjema (avdl-filer) er kompatibelt med forventet meldingsformat fra PDL.
 * Fanger opp skjemaendringer (feltfjerning, navneendring) som ellers ville gitt stille deserialiseringsfeil i prod.
 */
class PdlAvroSkjemaTest : BehaviorSpec({

    Given("en Personhendelse-melding fra PDL leesah-topic") {
        val original = Personhendelse(
            "hendelse-id-123",
            listOf("12345678901"),
            "PDL",
            Instant.parse("2025-01-15T10:00:00Z"),
            "ADRESSEBESKYTTELSE_V1",
            Endringstype.OPPRETTET,
            null,
            Adressebeskyttelse(Gradering.STRENGT_FORTROLIG),
            null
        )

        When("serialisert og deserialisert via Avro binary encoding") {
            val deserialisert = roundtrip(original)

            Then("hendelseId er bevart") {
                deserialisert.hendelseId shouldBe original.hendelseId
            }
            Then("personidenter er bevart") {
                deserialisert.personidenter shouldBe original.personidenter
            }
            Then("opplysningstype er bevart") {
                deserialisert.opplysningstype shouldBe original.opplysningstype
            }
            Then("endringstype er bevart") {
                deserialisert.endringstype shouldBe Endringstype.OPPRETTET
            }
            Then("adressebeskyttelse.gradering er bevart") {
                deserialisert.adressebeskyttelse.gradering shouldBe Gradering.STRENGT_FORTROLIG
            }
        }
    }

    Given("en Personhendelse uten adressebeskyttelse") {
        val original = Personhendelse(
            "hendelse-id-456",
            listOf("98765432109"),
            "FREG",
            Instant.parse("2025-06-01T08:00:00Z"),
            "NAVN_V1",
            Endringstype.KORRIGERT,
            "hendelse-id-123",
            null,
            null
        )

        When("serialisert og deserialisert") {
            val deserialisert = roundtrip(original)

            Then("adressebeskyttelse er null") {
                deserialisert.adressebeskyttelse shouldBe null
            }
            Then("tidligereHendelseId er bevart") {
                deserialisert.tidligereHendelseId shouldBe "hendelse-id-123"
            }
        }
    }
})

private fun roundtrip(hendelse: Personhendelse): Personhendelse {
    val writer = SpecificDatumWriter(Personhendelse::class.java)
    val baos = ByteArrayOutputStream()
    val encoder = EncoderFactory.get().binaryEncoder(baos, null)
    writer.write(hendelse, encoder)
    encoder.flush()

    val reader = SpecificDatumReader(Personhendelse::class.java)
    val decoder = DecoderFactory.get().binaryDecoder(baos.toByteArray(), null)
    return reader.read(null, decoder)
}
