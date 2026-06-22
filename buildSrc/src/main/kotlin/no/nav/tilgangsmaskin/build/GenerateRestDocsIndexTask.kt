package no.nav.tilgangsmaskin.build

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.Properties

abstract class GenerateRestDocsIndexTask : DefaultTask() {

    @get:Internal
    abstract val snippetsDir: DirectoryProperty

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val docsDir: DirectoryProperty

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val openApiPropertiesFile: RegularFileProperty

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val controllerFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    abstract val title: Property<String>

    init {
        title.convention("Tilgangsmaskin API")
    }

    @TaskAction
    fun generate() {
        val snippetsRoot = snippetsDir.asFile.get()
        val docsRoot = docsDir.asFile.get()
        val outputRoot = outputDir.asFile.get()

        val properties = Properties().apply {
            openApiPropertiesFile.asFile.get().inputStream().use { load(it) }
        }
        val overviewDescription =
            properties.getProperty("openapi.tilgang.tag.description") ?: "Dokumentasjon for Tilgangsmaskin API."

        val sections = mutableListOf<String>()
        sections += "= ${title.get()}"
        sections += ":toc:"
        sections += ":toclevels: 2"
        sections += ""
        sections += overviewDescription
        sections += ""

        docsRoot.listFiles { file -> file.isFile && file.extension == "adoc" }
            ?.sortedBy { it.name }
            ?.forEach { docFile ->
                sections += "include::${docFile.absolutePath}[]"
            }

        snippetsRoot.listFiles(File::isDirectory)
            ?.sortedBy { it.name }
            ?.forEach { snippetDirectory ->
                val adocFiles = snippetDirectory.listFiles { file -> file.isFile && file.extension == "adoc" }
                    ?.sortedBy { it.name }
                    .orEmpty()
                if (adocFiles.isNotEmpty()) {
                    sections += ""
                    sections += "== ${snippetDirectory.name.replace('-', ' ')}"
                    adocFiles.forEach { snippetFile ->
                        sections += "include::${snippetFile.absolutePath}[]"
                    }
                }
            }

        outputRoot.mkdirs()
        outputRoot.resolve("index.adoc").writeText(sections.joinToString(System.lineSeparator()))
    }
}
