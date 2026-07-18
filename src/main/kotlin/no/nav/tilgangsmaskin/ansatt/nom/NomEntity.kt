package no.nav.tilgangsmaskin.ansatt.nom

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import no.nav.tilgangsmaskin.ansatt.AnsattId.Companion.ANSATTID_LENGTH
import no.nav.tilgangsmaskin.bruker.BrukerId.Companion.BRUKERID_LENGTH
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "ansatte",
    indexes = [Index(name = "idx_gyldig", columnList = "gyldigtil")],
    uniqueConstraints = [UniqueConstraint(name = "uc_ansattentity_navid", columnNames = ["navid"])])
@EntityListeners(AuditingEntityListener::class)
class NomEntity(
    @Column(length = ANSATTID_LENGTH, nullable = false) val navid: String,
    @Column(length = BRUKERID_LENGTH, nullable = false) val fnr: String,
    @Column(nullable = false) val startdato: Instant,
    @Column(nullable = false) val gyldigtil: Instant) {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    var id: Long? = null

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var created: Instant? = null

    @LastModifiedDate
    @Column(nullable = false)
    var updated: Instant? = null
}