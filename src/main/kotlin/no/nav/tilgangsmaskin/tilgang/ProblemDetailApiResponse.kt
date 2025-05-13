package no.nav.tilgangsmaskin.tilgang

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
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
                            mediaType = "application/json",
                            schema = Schema(
                                    implementation = GruppeMetadata::class,
                                    example = """{
                        "type": "https://confluence.adeo.no/display/TM/Tilgangsmaskin+API+og+regelsett",
                        "title": "AVVIST_STRENGT_FORTROLIG_ADRESSE",
                        "status": 403,
                        "instance": "Z990883/03508331575",
                        "brukerIdent": "03508331575",
                        "navIdent": "Z990883",
                        "begrunnelse": "Du har ikke tilgang til brukere med strengt fortrolig adresse",
                        "kanOverstyres": false
                    }"""
                                           )
                                      )]
                       )
        ]
             )
annotation class ProblemDetailApiResponse