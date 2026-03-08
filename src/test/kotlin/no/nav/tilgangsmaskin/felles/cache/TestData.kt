package no.nav.tilgangsmaskin.felles.cache

import no.nav.tilgangsmaskin.felles.cache.TestData.Kontakt.Adresse

data class TestData(val id: String, val navn: String, val alder: Int, val kontakt: Kontakt) {
    data class Kontakt(val epost: String, val telefon: String, val adresse: Adresse) {
        data class Adresse(val gate: String, val postnummer: String, val by: String)
    }
    companion object {
        fun of(id: String) = TestData(
            id, "Navn $id", 42,
            Kontakt("$id@test.no", "99887766", Adresse("Testgata 1", "0001", "Oslo")
            )
        )
    }
}

