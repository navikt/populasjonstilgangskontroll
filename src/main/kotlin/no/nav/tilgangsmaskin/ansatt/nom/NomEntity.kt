package no.nav.tilgangsmaskin.ansatt.nom

import jakarta.persistence.*
import jakarta.persistence.GenerationType.IDENTITY
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.time.Instant
import org.hibernate.annotations.Cache

@Entity
@Table(
        name = "ansatte",
        indexes = [Index(name = "idx_gyldig", columnList = "gyldigtil")],
        uniqueConstraints = [UniqueConstraint(name = "uc_ansattentity_navid", columnNames = ["navid"])])
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
class NomEntity(
        @Column(length = 7, nullable = false) val navid: String,
        @Column(length = 11, nullable = false) var fnr: String,
        @Column(nullable = false) var startdato: Instant,
        @Column(nullable = false) var gyldigtil: Instant) {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long? = null

    @Column(nullable = false, updatable = false)
    var created: Instant? = null

    @Column(nullable = false)
    var updated: Instant? = null
}