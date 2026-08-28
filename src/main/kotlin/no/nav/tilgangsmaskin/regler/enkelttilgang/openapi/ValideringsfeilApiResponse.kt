package no.nav.tilgangsmaskin.regler.enkelttilgang.openapi

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import no.nav.tilgangsmaskin.tilgang.openapi.ProblemSwaggerDetailResponse
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ApiResponses(
    value = [
        ApiResponse(
            responseCode = "204",
            description = "Enkelttilgang ble registrert",
            content = [Content()]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Validering av request body feilet",
            content = [Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = Schema(
                    example = """{
                        "title": "Validering feilet",
                        "status": 400,
                        "detail": "En eller flere felter er ugyldige",
                        "feil": [
                            {
                                "felt": "gyldigtil",
                                "melding": "Gyldig til må være mellom i dag og 3 måneder frem i tid"
                            },
                            {
                                "felt": "begrunnelse",
                                "melding": "Begrunnelse må være mellom 10 og 255 tegn"
                            }
                        ]
                    }"""
                )
            )]
        ),
        ApiResponse(
            responseCode = "403",
            description = "Tilgang ble avvist",
            content = [Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = Schema(
                    implementation = ProblemSwaggerDetailResponse::class,
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
                    }"""
                )
            )]
        )
    ]
)
annotation class ValideringsfeilApiResponse
