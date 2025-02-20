package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import jakarta.persistence.Column
import jakarta.persistence.Entity
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
class Overstyring : IdentifiableTimestampedBaseEntity() {
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "navid", length = 7)
    var navid: String? = null

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "fnr", length = 11)
    var fnr: String? = null
}