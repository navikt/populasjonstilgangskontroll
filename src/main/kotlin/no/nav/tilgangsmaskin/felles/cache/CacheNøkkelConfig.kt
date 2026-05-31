package no.nav.tilgangsmaskin.felles.cache

data class CacheNøkkelConfig(val name: String, val extraPrefix: String? = null) {
    val fullName: String get() = extraPrefix?.let { "$name:$it" } ?: name

    fun tilNøkkel(id: String) = "${extraPrefix?.let { "$it:" } ?: ""}$id"}
