package no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object RestUtils {
    private val mediaTypeJson = "application/json".toMediaType()

    fun String.toJsonRequestBody(): RequestBody {
        return this.toRequestBody(mediaTypeJson)
    }

    fun Request.Builder.authorization(bearerToken: String): Request.Builder {
        return this.header("Authorization", "Bearer $bearerToken")
    }

    fun Request.Builder.authorization(bearerTokenProvider: () -> String): Request.Builder {
        return authorization(bearerTokenProvider.invoke())
    }

}