import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject
import java.io.ByteArrayOutputStream

@CacheableTask
abstract class GenerateGitPropertiesTask : DefaultTask() {

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:Input
    abstract val buildVersion: Property<String>

    @get:Inject
    abstract val execOperations: ExecOperations

    @TaskAction
    fun generate() {
        fun git(vararg args: String): String {
            val stdout = ByteArrayOutputStream()
            execOperations.exec {
                commandLine("git", *args)
                isIgnoreExitValue = true
                standardOutput = stdout
                errorOutput = ByteArrayOutputStream()
            }
            return stdout.toString().trim()
        }

        outputFile.get().asFile.apply {
            parentFile.mkdirs()
            writeText(
                buildString {
                    appendLine("git.branch=${git("rev-parse", "--abbrev-ref", "HEAD")}")
                    appendLine("git.commit.id=${git("rev-parse", "HEAD")}")
                    appendLine("git.commit.id.abbrev=${git("rev-parse", "--short", "HEAD")}")
                    appendLine("git.commit.time=${git("log", "-1", "--format=%cI")}")
                    appendLine("git.dirty=${git("status", "--porcelain").isNotEmpty()}")
                    appendLine("git.build.version=${buildVersion.get()}")
                }
            )
        }
    }
}
