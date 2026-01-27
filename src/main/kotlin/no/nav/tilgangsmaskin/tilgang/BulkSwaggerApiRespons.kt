package no.nav.tilgangsmaskin.tilgang

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.EntraOidException
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.motor.AvvisningsKode
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

@Target(FUNCTION)
@Retention(RUNTIME)
@ApiResponses(
    value = [ApiResponse(
        responseCode = "404",
        content = [Content(mediaType = APPLICATION_JSON_VALUE, schema = Schema(implementation = EntraOidException::class))],
        description = "navident ikke funnet i Entra (gjelder kun Client Credentials Flow der navident oppgis som path-parameter)"),
        ApiResponse(
            responseCode = "413",
            description = "Mer enn 1000 id-er ble sendt i bulk"),
        ApiResponse(
            responseCode = "207",
            description = "Tilganger ble evaluert for alle brukere i bulk",
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
                             "status": 204
                           }
                       ]
                   }"""))])])
annotation class BulkSwaggerApiRespons
private data class BulkSwaggerResultater(val ansattId: String, val resultater: List<BulkSwaggerResultat>) {
    data class BulkSwaggerResultat(val brukerId: String, val status: Int, val detaljer: BulkSwaggerDetaljer? = null) {
        data class BulkSwaggerDetaljer(val type: String, val title: AvvisningsKode, val status: Int, val instance: String,
                                               val brukerIdent: BrukerId,
                                               val navIdent: AnsattId,
                                               val begrunnelse: String,
                                               val traceId: String,
                                               val kanOverstyres: Boolean
        )
    }

}


