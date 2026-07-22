import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import kotlin.text.Charsets.UTF_8

abstract class GenerateRestDocsIndexTask : DefaultTask() {

    @get:InputDirectory
    @get:SkipWhenEmpty
    abstract val snippetsDir: DirectoryProperty

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val openApiPropertiesFile: RegularFileProperty

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val tilgangControllerFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val snippets = snippetsDir.get().asFile
        if (!snippets.isDirectory) return

        val dirs = snippets.listFiles { f -> f.isDirectory }
            ?.map { it.name }
            ?.sorted()
            ?: return

        val grouped = dirs.groupBy { it.substringBefore("-") }

        val sb = StringBuilder()
        val sharedProblemDetailSnippet = dirs.firstOrNull {
            snippets.resolve(it).resolve("response-fields.adoc").exists()
        }
        val sharedProblemDetailContent = sharedProblemDetailSnippet?.let {
            snippets.resolve(it).resolve("response-fields.adoc").readText(UTF_8)
        }

        val properties = loadProperties()
        val controllerConstants = parseControllerConstants()

        val endpointDescriptionKeys: Map<String, String> = mapOf(
            "obo-komplett" to (controllerConstants["SUMMARY_KOMPLETT_OBO"] ?: "openapi.tilgang.komplett.obo.summary"),
            "obo-kjerne" to (controllerConstants["SUMMARY_KJERNE_OBO"] ?: "openapi.tilgang.kjerne.obo.summary"),
            "obo-enkelttilgang" to (controllerConstants["SUMMARY_OVERSTYR"] ?: "openapi.tilgang.overstyr.summary"),
            "obo-bulk" to (controllerConstants["SUMMARY_BULK"] ?: "openapi.tilgang.bulk.summary"),
            "obo-bulk-regeltype" to (controllerConstants["DESCRIPTION_BULK_OBO_REGELTYPE"] ?: "openapi.tilgang.bulk.obo.regeltype.description"),
            "ccf-komplett" to (controllerConstants["SUMMARY_KOMPLETT_CCF"] ?: "openapi.tilgang.komplett.ccf.summary"),
            "ccf-kjerne" to (controllerConstants["SUMMARY_KJERNE_CCF"] ?: "openapi.tilgang.kjerne.ccf.summary"),
            "ccf-bulk" to (controllerConstants["SUMMARY_BULK"] ?: "openapi.tilgang.bulk.summary"),
            "ccf-bulk-regeltype" to (controllerConstants["DESCRIPTION_BULK_CCF_REGELTYPE"] ?: "openapi.tilgang.bulk.ccf.regeltype.description"),
        )

        fun sectionTitle(name: String, prefix: String) =
            name.substringAfter("$prefix-")
                .replace("-", " ")
                .replaceFirstChar { it.uppercase() }

        fun docsTitle(name: String, prefix: String): String {
            val title = sectionTitle(name, prefix)
            return if (name.contains("-overstyr")) title.replaceFirst("Overstyr", "Enkelttilgang") else title
        }

        fun getDescription(name: String): String {
            val key = endpointDescriptionKeys[name] ?: return docsTitle(name, name.substringBefore("-"))
            val normalizedKey = key.removePrefix("msg:")
            return properties[normalizedKey] ?: properties[key] ?: key
        }

        fun appendSnippetIncludes(name: String) {
            sb.appendLine("include::{snippets}/$name/http-request.adoc[]")
            sb.appendLine("include::{snippets}/$name/http-response.adoc[]")
            val responseFields = snippets.resolve(name).resolve("response-fields.adoc")
            if (responseFields.exists() && responseFields.readText(UTF_8) != sharedProblemDetailContent) {
                sb.appendLine()
                sb.appendLine(".Response fields")
                sb.appendLine("include::{snippets}/$name/response-fields.adoc[]")
            }
            sb.appendLine()
        }

        sb.appendLine("= Populasjonstilgangskontroll API")
        sb.appendLine(":doctype: book")
        sb.appendLine(":icons: font")
        sb.appendLine(":toc: left")
        sb.appendLine(":toclevels: 3")
        sb.appendLine(":sectlinks:")
        sb.appendLine()
        sb.appendLine("== Oversikt")
        sb.appendLine()
        sb.appendLine("REST API-dokumentasjon for Populasjonstilgangskontroll (Tilgangsmaskinen).")
        sb.appendLine()
        sb.appendLine("Tjenesten avgjør om en Nav-ansatt har tilgang til en bruker")
        sb.appendLine()

        if (sharedProblemDetailSnippet != null) {
            sb.appendLine("=== Felles ProblemDetail-felter for enkeltoppslag og enkelttilgang")
            sb.appendLine()
            sb.appendLine("include::{snippets}/$sharedProblemDetailSnippet/response-fields.adoc[]")
            sb.appendLine()
        }

        for ((prefix, names) in grouped) {
            val heading = when (prefix) {
                "obo" -> "On-Behalf-Of flow"
                "ccf" -> "Client Credentials Flow"
                else -> prefix.uppercase()
            }
            sb.appendLine("== $heading")
            sb.appendLine()

            val sortedNames = names.sorted()
            val overstyrRoot = "$prefix-enkelttilgang"
            val overstyrRelated = sortedNames.filter { it == overstyrRoot || it.startsWith("$overstyrRoot-") }
            val remaining = sortedNames.filterNot { it in overstyrRelated }

            for (name in remaining) {
                sb.appendLine("=== ${getDescription(name)}")
                sb.appendLine()
                appendSnippetIncludes(name)
            }

            if (overstyrRelated.isNotEmpty()) {
                sb.appendLine("=== Enkelttilgang")
                sb.appendLine()

                if (overstyrRelated.contains(overstyrRoot)) {
                    appendSnippetIncludes(overstyrRoot)
                }

                val alternatives = overstyrRelated.filter {
                    it != overstyrRoot && (it.contains("begrunnelse-for-kort") || it.contains("uten-token"))
                }
                if (alternatives.isNotEmpty()) {
                    sb.appendLine("==== Alternative responser")
                    sb.appendLine()
                    for (name in alternatives) {
                        sb.appendLine("===== ${getDescription(name)}")
                        sb.appendLine()
                        sb.appendLine("include::{snippets}/$name/http-request.adoc[]")
                        sb.appendLine("include::{snippets}/$name/http-response.adoc[]")
                        sb.appendLine()
                    }
                }
            }
        }

        val outDir = outputDir.get().asFile
        outDir.mkdirs()
        outDir.resolve("index.adoc").writeText(sb.toString(), UTF_8)
    }

    private fun loadProperties(): Map<String, String> {
        val propsFile = openApiPropertiesFile.get().asFile
        if (!propsFile.exists()) return emptyMap()
        return propsFile.readLines()
            .map { it.trim() }
            .filter { it.isNotEmpty() && !it.startsWith("#") }
            .mapNotNull { line ->
                val parts = line.split("=", limit = 2)
                if (parts.size == 2) parts[0] to parts[1] else null
            }
            .toMap()
    }

    private fun parseControllerConstants(): Map<String, String> {
        val controllerFile = tilgangControllerFile.get().asFile
        if (!controllerFile.exists()) return emptyMap()
        val content = controllerFile.readText(UTF_8).replace("\n", " ")
        val pattern = """private const val (\w+)\s*=\s*"([^"]+)"""".toRegex()
        return pattern.findAll(content).associate { match ->
            val (name, value) = match.destructured
            name to value.trim()
        }
    }
}

