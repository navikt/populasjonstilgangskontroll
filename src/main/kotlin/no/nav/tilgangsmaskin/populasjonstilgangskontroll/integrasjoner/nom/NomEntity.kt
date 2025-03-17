package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "ansatte",  uniqueConstraints = [
    UniqueConstraint(name = "uc_ansattentity_navid", columnNames = ["navid"])
])
@EntityListeners(AuditingEntityListener::class)
class NomEntity(
    @Column(length = 7, nullable = false) val navid: String,
    @Column(length = 11, nullable = false) var fnr: String,
    @Column(nullable = true) var gyldigtil: Instant? = null)  {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id : Long? = null
    @CreatedDate
    @Column(nullable = false, updatable = false)
    var created: Instant? = null

    @LastModifiedDate
    @Column(nullable = false)
    var updated: Instant? = null

    fun copy(id: Long, navid: String = this.navid, fnr: String = this.fnr, gyldigtil: Instant? = this.gyldigtil) =
        NomEntity(navid, fnr, gyldigtil)
}