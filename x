> Task :checkKotlinGradlePluginConfigurationErrors
> Task :compileKotlin UP-TO-DATE
> Task :compileJava NO-SOURCE
> Task :cyclonedxBom UP-TO-DATE
> Task :processResources UP-TO-DATE
> Task :classes UP-TO-DATE
> Task :resolveMainClassName UP-TO-DATE
> Task :bootJar UP-TO-DATE

> Task :bootBuildImage
Building image 'docker.io/library/tilgangsmaskin:1.0.1'

 > Pulling builder image 'docker.io/paketobuildpacks/builder-noble-java-tiny:latest' ..................................................
 > Pulled builder image 'paketobuildpacks/builder-noble-java-tiny@sha256:ae0cd71a4e28e3c3ba07fc409d197f04d28ed321910a2474eeb496e37d6ecc05'
 > Pulling run image 'docker.io/paketobuildpacks/ubuntu-noble-run-tiny:0.0.21' for platform 'linux/arm64' ..................................................
 > Pulled run image 'paketobuildpacks/ubuntu-noble-run-tiny@sha256:084c81f50c9619c5da73bd7310febf44437db8621c7563601989ef8839882bce'
 > Executing lifecycle version v0.20.12
 > Using build cache volume 'pack-cache-2e7764f7574a.build'

 > Running creator
    [creator]     ===> ANALYZING
    [creator]     Restoring data for SBOM from previous image
    [creator]     ===> DETECTING
    [creator]     target distro name/version labels not found, reading /etc/os-release file
    [creator]     ======== Error: paketo-buildpacks/yarn@2.0.5 ========
    [creator]     fork/exec /cnb/buildpacks/paketo-buildpacks_yarn/2.0.5/bin/detect: exec format error
    [creator]     ======== Error: paketo-buildpacks/node-engine@7.1.0 ========
    [creator]     fork/exec /cnb/buildpacks/paketo-buildpacks_node-engine/7.1.0/bin/detect: exec format error
    [creator]     err:  paketo-buildpacks/yarn@2.0.5
    [creator]     err:  paketo-buildpacks/node-engine@7.1.0
    [creator]     6 of 26 buildpacks participating
    [creator]     paketo-buildpacks/ca-certificates   3.10.3
    [creator]     paketo-buildpacks/bellsoft-liberica 11.2.5
    [creator]     paketo-buildpacks/syft              2.17.0
    [creator]     paketo-buildpacks/executable-jar    6.13.2
    [creator]     paketo-buildpacks/dist-zip          5.10.2
    [creator]     paketo-buildpacks/spring-boot       5.33.3
    [creator]     ===> RESTORING
    [creator]     Restoring metadata for "paketo-buildpacks/ca-certificates:helper" from app image
    [creator]     Restoring metadata for "paketo-buildpacks/bellsoft-liberica:helper" from app image
    [creator]     Restoring metadata for "paketo-buildpacks/bellsoft-liberica:java-security-properties" from app image
    [creator]     Restoring metadata for "paketo-buildpacks/bellsoft-liberica:jre" from app image
    [creator]     Restoring metadata for "paketo-buildpacks/syft:syft" from cache
    [creator]     Restoring metadata for "paketo-buildpacks/spring-boot:helper" from app image
    [creator]     Restoring metadata for "paketo-buildpacks/spring-boot:spring-cloud-bindings" from app image
    [creator]     Restoring metadata for "paketo-buildpacks/spring-boot:web-application-type" from app image
    [creator]     Restoring data for "paketo-buildpacks/syft:syft" from cache
    [creator]     Restoring data for "paketo-buildpacks/spring-boot:spring-cloud-bindings" from cache
    [creator]     Restoring data for SBOM from cache
    [creator]     ===> BUILDING
    [creator]     target distro name/version labels not found, reading /etc/os-release file
    [creator]     
    [creator]     Paketo Buildpack for CA Certificates 3.10.3
    [creator]       https://github.com/paketo-buildpacks/ca-certificates
    [creator]       Build Configuration:
    [creator]         $BP_EMBED_CERTS                    false  Embed certificates into the image
    [creator]         $BP_ENABLE_RUNTIME_CERT_BINDING    true   Deprecated: Enable/disable certificate helper layer to add certs at runtime
    [creator]         $BP_RUNTIME_CERT_BINDING_DISABLED  false  Disable certificate helper layer to add certs at runtime
    [creator]       Launch Helper: Reusing cached layer
    [creator]     
    [creator]     Paketo Buildpack for BellSoft Liberica 11.2.5
    [creator]       https://github.com/paketo-buildpacks/bellsoft-liberica
    [creator]       Build Configuration:
    [creator]         $BP_JVM_JLINK_ARGS           --no-man-pages --no-header-files --strip-debug --compress=1  configure custom link arguments (--output must be omitted)
    [creator]         $BP_JVM_JLINK_ENABLED        false                                                        enables running jlink tool to generate custom JRE
    [creator]         $BP_JVM_TYPE                 JRE                                                          the JVM type - JDK or JRE
    [creator]         $BP_JVM_VERSION              21                                                           the Java version
    [creator]       Launch Configuration:
    [creator]         $BPL_DEBUG_ENABLED           false                                                        enables Java remote debugging support
    [creator]         $BPL_DEBUG_PORT              8000                                                         configure the remote debugging port
    [creator]         $BPL_DEBUG_SUSPEND           false                                                        configure whether to suspend execution until a debugger has attached
    [creator]         $BPL_HEAP_DUMP_PATH                                                                       write heap dumps on error to this path
    [creator]         $BPL_JAVA_NMT_ENABLED        true                                                         enables Java Native Memory Tracking (NMT)
    [creator]         $BPL_JAVA_NMT_LEVEL          summary                                                      configure level of NMT, summary or detail
    [creator]         $BPL_JFR_ARGS                                                                             configure custom Java Flight Recording (JFR) arguments
    [creator]         $BPL_JFR_ENABLED             false                                                        enables Java Flight Recording (JFR)
    [creator]         $BPL_JMX_ENABLED             false                                                        enables Java Management Extensions (JMX)
    [creator]         $BPL_JMX_PORT                5000                                                         configure the JMX port
    [creator]         $BPL_JVM_HEAD_ROOM           0                                                            the headroom in memory calculation
    [creator]         $BPL_JVM_LOADED_CLASS_COUNT  35% of classes                                               the number of loaded classes in memory calculation
    [creator]         $BPL_JVM_THREAD_COUNT        250                                                          the number of threads in memory calculation
    [creator]         $JAVA_TOOL_OPTIONS                                                                        the JVM launch flags
    [creator]         Using Java version 21 extracted from MANIFEST.MF
    [creator]       BellSoft Liberica JRE 21.0.8: Reusing cached layer
    [creator]       Launch Helper: Reusing cached layer
    [creator]       Java Security Properties: Reusing cached layer
    [creator]     
    [creator]     Paketo Buildpack for Syft 2.17.0
    [creator]       https://github.com/paketo-buildpacks/syft
    [creator]     
    [creator]     Paketo Buildpack for Executable JAR 6.13.2
    [creator]       https://github.com/paketo-buildpacks/executable-jar
    [creator]       Class Path: Contributing to layer
    [creator]         Writing env/CLASSPATH.delim
    [creator]         Writing env/CLASSPATH.prepend
    [creator]       Process types:
    [creator]         executable-jar: java org.springframework.boot.loader.launch.JarLauncher (direct)
    [creator]         task:           java org.springframework.boot.loader.launch.JarLauncher (direct)
    [creator]         web:            java org.springframework.boot.loader.launch.JarLauncher (direct)
    [creator]     
    [creator]     Paketo Buildpack for Spring Boot 5.33.3
    [creator]       https://github.com/paketo-buildpacks/spring-boot
    [creator]       Build Configuration:
    [creator]         $BPL_JVM_CDS_ENABLED                 false  whether to enable CDS optimizations at runtime
    [creator]         $BPL_SPRING_AOT_ENABLED              false  whether to enable Spring AOT at runtime
    [creator]         $BP_JVM_CDS_ENABLED                  false  whether to enable CDS & perform JVM training run
    [creator]         $BP_SPRING_AOT_ENABLED               false  whether to enable Spring AOT
    [creator]         $BP_SPRING_CLOUD_BINDINGS_DISABLED   false  whether to contribute Spring Boot cloud bindings support
    [creator]         $BP_SPRING_CLOUD_BINDINGS_VERSION    1      default version of Spring Cloud Bindings library to contribute
    [creator]       Launch Configuration:
    [creator]         $BPL_SPRING_CLOUD_BINDINGS_DISABLED  false  whether to auto-configure Spring Boot environment properties from bindings
    [creator]         $BPL_SPRING_CLOUD_BINDINGS_ENABLED   true   Deprecated - whether to auto-configure Spring Boot environment properties from bindings
    [creator]       Creating slices from layers index
    [creator]         dependencies (111.0 MB)
    [creator]         spring-boot-loader (458.0 KB)
    [creator]         snapshot-dependencies (0.0 B)
    [creator]         application (1.6 MB)
    [creator]       Spring Cloud Bindings 2.0.4: Reusing cached layer
    [creator]       Web Application Type: Reusing cached layer
    [creator]       Launch Helper: Reusing cached layer
    [creator]       4 application slices
    [creator]       Image labels:
    [creator]         org.opencontainers.image.title
    [creator]         org.opencontainers.image.version
    [creator]         org.springframework.boot.version
    [creator]     ===> EXPORTING
    [creator]     Reusing layer 'paketo-buildpacks/ca-certificates:helper'
    [creator]     Reusing layer 'paketo-buildpacks/bellsoft-liberica:helper'
    [creator]     Reusing layer 'paketo-buildpacks/bellsoft-liberica:java-security-properties'
    [creator]     Reusing layer 'paketo-buildpacks/bellsoft-liberica:jre'
    [creator]     Reusing layer 'paketo-buildpacks/executable-jar:classpath'
    [creator]     Reusing layer 'paketo-buildpacks/spring-boot:helper'
    [creator]     Reusing layer 'paketo-buildpacks/spring-boot:spring-cloud-bindings'
    [creator]     Reusing layer 'paketo-buildpacks/spring-boot:web-application-type'
    [creator]     Reusing layer 'buildpacksio/lifecycle:launch.sbom'
    [creator]     Reused 5/5 app layer(s)
    [creator]     Reusing layer 'buildpacksio/lifecycle:launcher'
    [creator]     Reusing layer 'buildpacksio/lifecycle:config'
    [creator]     Reusing layer 'buildpacksio/lifecycle:process-types'
    [creator]     Adding label 'io.buildpacks.lifecycle.metadata'
    [creator]     Adding label 'io.buildpacks.build.metadata'
    [creator]     Adding label 'io.buildpacks.project.metadata'
    [creator]     Adding label 'org.opencontainers.image.title'
    [creator]     Adding label 'org.opencontainers.image.version'
    [creator]     Adding label 'org.springframework.boot.version'
    [creator]     Setting default process type 'web'
    [creator]     Saving docker.io/library/tilgangsmaskin:1.0.1...
    [creator]     *** Images (b6b371810016):
    [creator]           docker.io/library/tilgangsmaskin:1.0.1
    [creator]     Reusing cache layer 'paketo-buildpacks/syft:syft'
    [creator]     Adding cache layer 'paketo-buildpacks/syft:syft'
    [creator]     Reusing cache layer 'paketo-buildpacks/spring-boot:spring-cloud-bindings'
    [creator]     Adding cache layer 'paketo-buildpacks/spring-boot:spring-cloud-bindings'
    [creator]     Reusing cache layer 'buildpacksio/lifecycle:cache.sbom'
    [creator]     Adding cache layer 'buildpacksio/lifecycle:cache.sbom'

Successfully built image 'docker.io/library/tilgangsmaskin:1.0.1'


BUILD SUCCESSFUL in 6s
7 actionable tasks: 2 executed, 5 up-to-date
