package no.nav.tilgangsmaskin.tilgang

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import no.nav.tilgangsmaskin.regler.motor.AvvisningsKode
import org.springframework.http.MediaType
import org.springframework.http.MediaType.*
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*

@Target(FUNCTION)
@Retention(RUNTIME)
@ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Tilgang ble godkjent"
            ),
            ApiResponse(
                responseCode = "403",
                description = "Tilgang ble avvist",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(
                        implementation = BulkSwaggerResultater::class,
                        example = """{
                          "ansattId": "Z990883",
                          "resultater": [
                          {
                             "brukerId": "08526835671",
                             "status": 204
                          },
                          {
                              "brukerId": "03508331575",
                              "status": 403,
                              "detaljer": {
                                 "type": "https://confluence.adeo.no/display/TM/Tilgangsmaskin+API+og+regelsett",
                                 "title": "AVVIST_STRENGT_FORTROLIG_ADRESSE",
                                 "status": 403,
                                 "instance": "Z990883/03508331575",
                                 "brukerIdent": "03508331575",
                                 "navIdent": "Z990883",
                                 "begrunnelse": "Du har ikke tilgang til brukere med strengt fortrolig adresse",
                                 "traceId": "f85c9caa87a57b6dfde1068ce97f10a5",
                                 "kanOverstyres": false
                              }
                           },
                           {
                             "brukerId": "01011111111",
                             "status": 404
                           }
                       ]
                   }"""))])])
annotation class ProblemDetailBulkApiResponse
private data class BulkSwaggerResultater(
    val ansattId: String,
    val resultater: List<BulkSwaggerResultat>
)

data class BulkSwaggerResultat(
    val brukerId: String,
    val status: Int,
    val detaljer: BulkSwaggerDetaljer? = null
)

data class BulkSwaggerDetaljer(
    val type: String,
    val title: AvvisningsKode,
    val status: Int,
    val instance: String,
    val brukerIdent: String,
    val navIdent: String,
    val begrunnelse: String,
    val traceId: String,
    val kanOverstyres: Boolean
)