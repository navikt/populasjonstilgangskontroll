package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import jakarta.persistence.*
import jakarta.persistence.GenerationType.IDENTITY
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import java.time.Instant.EPOCH
import java.time.Instant.MAX

@Entity
@Table(name = "ansatte",  uniqueConstraints = [
    UniqueConstraint(name = "uc_ansattentity_navid", columnNames = ["navid"])
])
@EntityListeners(AuditingEntityListener::class, NomEntityListener::class)
class NomEntity(
    @Column(length = 7, nullable = false) val navid: String,
    @Column(length = 11, nullable = false) var fnr: String,
    @Column(nullable = false) var startdato: Instant? = EPOCH,
    @Column(nullable = false) var gyldigtil: Instant? = MAX)  {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id : Long? = null
    @CreatedDate
    @Column(nullable = false, updatable = false)
    var created: Instant? = null

    @LastModifiedDate
    @Column(nullable = false)
    var updated: Instant? = null

}