package no.nav.tilgangsmaskin.regler.overstyring

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.hibernate.annotations.Check
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FIELD

@Entity(name = "overstyring")
@Table(indexes = [Index(name = "idx_overstyringentity_navid", columnList = "navid, fnr")])
@EntityListeners(OverstyringEntityListener::class)
@Check(constraints = "char_length(begrunnelse) >= 10 AND char_length(begrunnelse) <= 255")
class OverstyringEntity(
        @Column(length = 7, nullable = false) val navid: String,
        @Column(length = 11, nullable = false) val fnr: String,
        @Column(nullable = false) val begrunnelse: String,
        @Column(nullable = false) val expires: Instant) {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0

    @CreatedDato
    @Column(nullable = false)
    var created: Instant? = null

    @LastModifiedDato
    @Column(nullable = false)
    var updated: Instant? = null

    @Column(name = "oppretter", length = 7)
    @CreatedByAnsatt
    var oppretter: String? = null

    @Column(name = "system", length = 50)
    @CreatedBySystem
    var system: String? = null
}


@Target(FIELD)
@Retention(RUNTIME)
annotation class CreatedBySystem

@Target(FIELD)
@Retention(RUNTIME)
annotation class CreatedByAnsatt

@Target(FIELD)
@Retention(RUNTIME)
annotation class CreatedDato

@Target(FIELD)
@Retention(RUNTIME)
annotation class LastModifiedDato