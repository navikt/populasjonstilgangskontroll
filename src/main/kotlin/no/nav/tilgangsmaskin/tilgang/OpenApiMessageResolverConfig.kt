package no.nav.tilgangsmaskin.tilgang

import io.swagger.v3.oas.models.Operation
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import org.springdoc.core.customizers.OperationCustomizer
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.HandlerMethod
import java.util.Locale.*

internal const val MSG = "msg:"

@Configuration
@NoCoverageAnalysis
class OpenApiMessageResolverConfig(private val messageSource: MessageSource) {

    @Bean
    fun openApiMessageCustomizer(): OpenApiCustomizer = OpenApiCustomizer { openApi ->
        openApi.tags?.forEach { tag ->
            tag.description = resolve(tag.description)
        }

        @Bean
        fun bulkSwaggerOperationCustomizer(): OperationCustomizer = OperationCustomizer { operation, handlerMethod ->
            applyBulkSwaggerOperation(operation, handlerMethod)
            operation
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

    private fun applyBulkSwaggerOperation(operation: Operation, handlerMethod: HandlerMethod) {
        val bulkSwaggerApiRespons = handlerMethod.getMethodAnnotation(BulkSwaggerApiRespons::class.java) ?: return

        if (bulkSwaggerApiRespons.summary.isNotBlank()) {
            operation.summary = bulkSwaggerApiRespons.summary
        }
        if (bulkSwaggerApiRespons.description.isNotBlank()) {
            operation.description = bulkSwaggerApiRespons.description
        }
    }

    private fun resolve(text: String?): String? {
        if (text.isNullOrBlank() || !text.startsWith(MSG)) return text
        val key = text.removePrefix(MSG)
        return messageSource.getMessage(key, null, key, getDefault())
    }
}
