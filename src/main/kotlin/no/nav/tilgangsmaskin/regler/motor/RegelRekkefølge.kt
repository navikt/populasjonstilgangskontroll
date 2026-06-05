package no.nav.tilgangsmaskin.regler.motor

/**
 * Sentralisert oversikt over evalueringsrekkefølgen for alle regler.
 * Lavere verdi evalueres først. Gap på 100 mellom hvert trinn gir rom for nye regler.
 *
 * Kjerneregler (blokkerende, fast):  100–900
 * Overstyrbareule regler (mykere):  1000–1400
 */
object RegelRekkefølge {
    const val STRENGT_FORTROLIG        = 100
    const val STRENGT_FORTROLIG_UTLAND = 200
    const val FORTROLIG                = 300
    const val SKJERMING                = 400
    const val EGNE_DATA                = 500
    const val FORELDRE_BARN            = 600
    const val PARTNER                  = 700
    const val SØSKEN                   = 800
    const val FELLES_BARN              = 900

    const val VERGEMÅL                 = 1000
    const val AVDØD                    = 1100
    const val UTLAND                   = 1200
    const val UKJENT_BOSTED            = 1300
    const val GEOGRAFISK               = 1400
}
