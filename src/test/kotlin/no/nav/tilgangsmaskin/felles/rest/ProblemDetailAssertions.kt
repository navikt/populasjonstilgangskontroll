package no.nav.tilgangsmaskin.felles.rest

import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MvcResult
import tools.jackson.databind.node.ArrayNode
import tools.jackson.databind.json.JsonMapper

fun MvcResult.assertProblemDetailBody(
    mapper: JsonMapper,
    httpStatus: HttpStatus,
    msg: String? = null,
    title: String? = null,
    fields: List<String> = emptyList(),
    requireBody: Boolean = true
) {
    val content = response.contentAsString
    if (content.isBlank()) {
        if (requireBody) error("Expected ProblemDetail body for status ${httpStatus.value()} but response was empty")
        return
    }

    val body = mapper.readTree(content)

    body["status"]?.asInt() shouldBe httpStatus.value()
    body["title"]?.asString() shouldBe (title ?: "${httpStatus.value()}")
    msg?.let { body["detail"]?.asString() shouldBe it }

    if (fields.isNotEmpty()) {
        val feil = (body["feil"] as? ArrayNode) ?: error("Expected feil array in ProblemDetail body")
        fields.forEach { felt ->
            (feil.any { it["felt"]?.asString() == felt }) shouldBe true
        }
    }
}
