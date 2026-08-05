package no.nav.tilgangsmaskin.felles.rest

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.resilience.annotation.EnableResilientMethods
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(initializers = [RestTjenestePropertySettingTestContextInitializer::class])
@EnableResilientMethods
abstract class RestTjenesteTest(body: BehaviorSpec.() -> Unit = {}) : BehaviorSpec(body) {
    init {
        extension(SpringExtension())
    }
}