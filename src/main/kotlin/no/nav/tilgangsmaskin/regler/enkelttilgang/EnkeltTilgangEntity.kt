package no.nav.tilgangsmaskin.regler.enkelttilgang

import jakarta.persistence.CheckConstraint
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import no.nav.tilgangsmaskin.ansatt.AnsattId.Companion.ANSATTID_LENGTH
import no.nav.tilgangsmaskin.bruker.BrukerId.Companion.BRUKERID_LENGTH
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity(name = "overstyring")
@Table(
    indexes = [Index(name = "idx_overstyringentity_navid", columnList = "navid, fnr")],
    check = [CheckConstraint(constraint = "char_length(begrunnelse) >= 10 AND char_length(begrunnelse) <= 255")
    ]
)
@EntityListeners(EnkeltTilgangEntityListener::class, AuditingEntityListener::class)
class EnkeltTilgangEntity(
        @Column(length = ANSATTID_LENGTH, nullable = false) val navid: String,
        @Column(length = BRUKERID_LENGTH, nullable = false) val fnr: String,
        @Column(length = 400, nullable = false) val begrunnelse: String,
        @Column(length = 6) val enhet: String,
        @Column(nullable = false) val expires: Instant) {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long? = null

    @CreatedDate
    @Column(nullable = false)
    var created: Instant? = null

    @LastModifiedDate
    @Column(nullable = false)
    var updated: Instant? = null

    @Column(name = "oppretter", length = ANSATTID_LENGTH)
    var oppretter: String? = null

    @Column(name = "system", length = 50)
    var system: String? = null
}

