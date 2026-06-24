package no.nav.tilgangsmaskin.felles.rest

import no.nav.tilgangsmaskin.felles.rest.RequestAttributter.BRUKER_IDENT
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangData
import no.nav.tilgangsmaskin.tilgang.TilgangController
import org.springframework.core.MethodParameter
import org.springframework.http.HttpInputMessage
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter
import java.lang.reflect.Type

@RestControllerAdvice
class BrukerIdentRequestBodyAdvice : RequestBodyAdviceAdapter() {

    override fun supports(methodParameter: MethodParameter, targetType: Type, converterType: Class<out HttpMessageConverter<*>>): Boolean = methodParameter.containingClass == TilgangController::class.java &&
        (targetType == String::class.java || targetType == EnkeltTilgangData::class.java)

    override fun afterBodyRead(body: Any, inputMessage: HttpInputMessage, parameter: MethodParameter, targetType: Type, converterType: Class<out HttpMessageConverter<*>>, ): Any {
        val request = (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.request ?: return body
        val brukerIdent = when (body) {
            is String -> body.trim('"')
            is EnkeltTilgangData -> body.brukerId.verdi
            else -> null
        }
        brukerIdent
            ?.takeIf { it.isNotBlank() }
            ?.let { request.setAttribute(BRUKER_IDENT, it) }
        return body
    }
}


