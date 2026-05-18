package no.nav.tilgangsmaskin.tilgang

import io.swagger.v3.oas.models.Operation
import no.nav.tilgangsmaskin.felles.Generated
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.Locale
import java.util.Locale.getDefault
typealias NoCoverageAnalysis = Generated
@Configuration
@NoCoverageAnalysis
class OpenApiMessageResolverConfig(private val messageSource: MessageSource) {

    @Bean
    fun openApiMessageCustomizer(): OpenApiCustomizer = OpenApiCustomizer { openApi ->
        openApi.tags?.forEach { tag ->
            tag.description = resolve(tag.description)
        }

        openApi.paths?.values?.forEach { pathItem ->
            pathItem.readOperations().forEach { operation ->
                resolveOperation(operation)
            }
        }
    }

    private fun resolveOperation(operation: Operation) {
        operation.summary = resolve(operation.summary)
        operation.description = resolve(operation.description)
    }

    private fun resolve(text: String?): String? {
        if (text.isNullOrBlank() || !text.startsWith(MSG_PREFIX)) return text
        val key = text.removePrefix(MSG_PREFIX)
        return messageSource.getMessage(key, null, key, getDefault())
    }

    private companion object {
        const val MSG_PREFIX = "msg:"
    }
}

