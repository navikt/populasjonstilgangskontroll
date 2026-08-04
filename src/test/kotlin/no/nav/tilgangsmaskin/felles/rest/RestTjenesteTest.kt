package no.nav.tilgangsmaskin.felles.rest

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.resilience.annotation.EnableResilientMethods
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(initializers = [RestTjenesteTestContextInitializer::class])
@EnableResilientMethods
abstract class RestTjenesteTest(body: BehaviorSpec.() -> Unit = {}) : BehaviorSpec(body) {
    init {
        extension(SpringExtension())
    }
}