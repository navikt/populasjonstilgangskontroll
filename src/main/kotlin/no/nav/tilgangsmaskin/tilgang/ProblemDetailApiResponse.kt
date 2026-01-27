package no.nav.tilgangsmaskin.tilgang

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import no.nav.tilgangsmaskin.regler.motor.AvvisningsKode
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE
import java.net.URI
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

@Target(FUNCTION)
@Retention(RUNTIME)
@ApiResponses(
        value = [
            ApiResponse(
                responseCode = "404",
                content = [Content(mediaType = APPLICATION_JSON_VALUE, schema = Schema(
                    example = """{
                "detail": "Fant ingen oid for navident A222222, er den fremdeles gyldig?",
                "instance": "/api/v1/ccf/komplett/A222222",
                "status": 404,
                "title": "Uventet respons fra Entra",
                "navident": "A222222"
              }"""))],
                description = "navident ikke funnet i Entra (gjelder kun Client Credentials Flow der navident oppgis som path-parameter)"),
            ApiResponse(
                    responseCode = "204",
                    description = "Tilgang ble godkjent"),
            ApiResponse(
                    responseCode = "403",
                    description = "Tilgang ble avvist",
                    content = [Content(
                            mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                            schema = Schema(
                                    implementation = ProblemDetailResponse::class,
                                    example = """{
                        "type": "https://confluence.adeo.no/display/TM/Tilgangsmaskin+API+og+regelsett",
                        "title": "AVVIST_STRENGT_FORTROLIG_ADRESSE",
                        "status": 403,
                        "instance": "Z990883/03508331575",
                        "brukerIdent": "03508331575",
                        "navIdent": "Z990883",
                        "traceId": "444290be30ed4fdd9a849654bad9dc1b",
                        "begrunnelse": "Du har ikke tilgang til brukere med strengt fortrolig adresse",
                        "kanOverstyres": false
                    }"""))])])
annotation class ProblemDetailApiResponse
@Schema(description = "Problem Detail")
internal data class ProblemDetailResponse(
        val type: URI,
        val title: AvvisningsKode,
        val status: Int,
        val instance: String,
        val brukerIdent: String,
        val navIdent: String,
        val begrunnelse: String,
        val traceId: String,
        val kanOverstyres: Boolean)

