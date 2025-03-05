package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring.OverstyringEntityListener
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "ansatte",  uniqueConstraints = [
    UniqueConstraint(name = "uc_ansattentity_navid", columnNames = ["navid"])
])
@EntityListeners(AuditingEntityListener::class)
class AnsattEntity(@Column(length = 7, nullable = false) val navid: String,
                        @Column(length = 11, nullable = false) val brukerid: String,
                        @Column(nullable = true) val gyldigtil: Instant)  {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id : Long = 0
    @CreatedDate
    @Column(nullable = false)
    var created: Instant? = null

    @LastModifiedDate
    @Column(nullable = false)
    var updated: Instant? = null
}