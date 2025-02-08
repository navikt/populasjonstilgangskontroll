package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import java.util.UUID

enum class FortroligGruppe(val gruppeNavn: String, val gruppeId: UUID) {
    STRENGT_FORTROLIG("0000-GA-STRENGT_FORTROLIG_ADRESSE",UUID.fromString("5ef775f2-61f8-4283-bf3d-8d03f428aa14")),
    FORTROLIG("0000-GA-FORTROLIG_ADRESSE",UUID.fromString("ea930b6b-9397-44d9-b9e6-f4cf527a632a"))
}
