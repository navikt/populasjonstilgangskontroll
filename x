> Task :buildSrc:checkKotlinGradlePluginConfigurationErrors SKIPPED
> Task :buildSrc:compileKotlin UP-TO-DATE
> Task :buildSrc:compileJava NO-SOURCE
> Task :buildSrc:compileGroovy NO-SOURCE
> Task :buildSrc:pluginDescriptors UP-TO-DATE
> Task :buildSrc:processResources NO-SOURCE
> Task :buildSrc:classes UP-TO-DATE
> Task :buildSrc:jar UP-TO-DATE

> Task :dependencies

------------------------------------------------------------
Root project 'populasjonstilgangskontroll'
------------------------------------------------------------

__$$asciidoctorj$$___d (n)
\--- org.asciidoctor:asciidoctorj:2.5.7 (n)

__$$asciidoctorj$$___r
\--- org.asciidoctor:asciidoctorj:2.5.7
     +--- com.beust:jcommander:1.82
     +--- org.asciidoctor:asciidoctorj-api:2.5.7
     \--- org.jruby:jruby:9.3.8.0 -> org.jruby:jruby-complete:9.3.8.0

__$$asciidoctorj_asciidoctor$$__d (n)
\--- org.asciidoctor:asciidoctorj:2.5.7 (n)

__$$asciidoctorj_asciidoctor$$__r
\--- org.asciidoctor:asciidoctorj:2.5.7
     +--- com.beust:jcommander:1.82
     +--- org.asciidoctor:asciidoctorj-api:2.5.7
     \--- org.jruby:jruby:9.3.8.0 -> org.jruby:jruby-complete:9.3.8.0

annotationProcessor - Annotation processors and their dependencies for source set 'main'.
No dependencies

api - API dependencies for 'main'. (n)
No dependencies

apiElements - API elements for the 'main' feature. (n)
No dependencies

apiElements-published (n)
No dependencies

bootArchives - Configuration for Spring Boot archive artifacts. (n)
No dependencies

compileClasspath - Compile classpath for 'main'.
+--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0
|    +--- org.jetbrains:annotations:13.0
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0 -> 2.4.0 (c)
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.0 -> 2.4.0 (c)
|    \--- org.jetbrains.kotlin:kotlin-stdlib-common:2.4.0 (c)
+--- no.nav.pdl.libs:contract-pdl-avro:18
|    \--- io.confluent:kafka-avro-serializer:7.7.1 -> 8.3.1
|         +--- org.apache.avro:avro:1.12.1
|         |    +--- com.fasterxml.jackson.core:jackson-core:2.20.0 -> 2.21.5
|         |    |    \--- com.fasterxml.jackson:jackson-bom:2.21.5
|         |    |         +--- com.fasterxml.jackson.core:jackson-annotations:2.21 (c)
|         |    |         +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (c)
|         |    |         +--- com.fasterxml.jackson.core:jackson-databind:2.21.5 (c)
|         |    |         +--- com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.21.5 (c)
|         |    |         +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.21.5 (c)
|         |    |         +--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.21.5 (c)
|         |    |         \--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.21.5 (c)
|         |    +--- com.fasterxml.jackson.core:jackson-databind:2.20.0 -> 2.21.5
|         |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|         |    |    +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (*)
|         |    |    \--- com.fasterxml.jackson:jackson-bom:2.21.5 (*)
|         |    +--- org.apache.commons:commons-compress:1.28.0
|         |    |    +--- commons-codec:commons-codec:1.19.0 -> 1.21.0
|         |    |    +--- commons-io:commons-io:2.20.0
|         |    |    \--- org.apache.commons:commons-lang3:3.18.0 -> 3.20.0
|         |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|         +--- org.apache.commons:commons-compress:1.26.1 -> 1.28.0 (*)
|         +--- io.confluent:kafka-schema-serializer:8.3.1
|         |    +--- io.confluent:kafka-schema-registry-client:8.3.1
|         |    |    +--- io.confluent:kafka-avro-types:8.3.1
|         |    |    |    +--- io.confluent:kafka-schema-types:8.3.1
|         |    |    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.2 -> 2.21.5 (*)
|         |    |    |    |    \--- io.confluent:common-utils:8.3.1
|         |    |    |    |         \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    |    |    +--- org.apache.avro:avro:1.12.1 (*)
|         |    |    |    \--- io.confluent:common-utils:8.3.1 (*)
|         |    |    +--- org.apache.kafka:kafka-clients:8.3.1-ccs -> 4.2.1
|         |    |    +--- org.apache.avro:avro:1.12.1 (*)
|         |    |    +--- org.apache.commons:commons-compress:1.26.1 -> 1.28.0 (*)
|         |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.2 -> 2.21.5 (*)
|         |    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.21.2 -> 2.21.5
|         |    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (*)
|         |    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.5 (*)
|         |    |    |    \--- com.fasterxml.jackson:jackson-bom:2.21.5 (*)
|         |    |    +--- org.yaml:snakeyaml:2.0 -> 2.6
|         |    |    +--- io.swagger.core.v3:swagger-annotations-jakarta:2.2.42 -> 2.2.52
|         |    |    +--- com.google.guava:guava:32.0.1-jre
|         |    |    |    +--- com.google.guava:failureaccess:1.0.1
|         |    |    |    +--- com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava
|         |    |    |    +--- com.google.code.findbugs:jsr305:3.0.2
|         |    |    |    +--- org.checkerframework:checker-qual:3.33.0
|         |    |    |    +--- com.google.errorprone:error_prone_annotations:2.18.0 -> 2.41.0
|         |    |    |    \--- com.google.j2objc:j2objc-annotations:2.8
|         |    |    +--- org.apache.httpcomponents.client5:httpclient5:5.5 -> 5.6.4
|         |    |    |    +--- org.apache.httpcomponents.core5:httpcore5:5.4.3
|         |    |    |    +--- org.apache.httpcomponents.core5:httpcore5-h2:5.4.3
|         |    |    |    |    \--- org.apache.httpcomponents.core5:httpcore5:5.4.3
|         |    |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    |    \--- io.confluent:common-utils:8.3.1 (*)
|         |    +--- com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.21.2 -> 2.21.5
|         |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.5 (*)
|         |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|         |    |    +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (*)
|         |    |    \--- com.fasterxml.jackson:jackson-bom:2.21.5 (*)
|         |    \--- io.confluent:common-utils:8.3.1 (*)
|         +--- io.confluent:kafka-schema-registry-client:8.3.1 (*)
|         +--- com.google.guava:guava:32.0.1-jre (*)
|         +--- io.confluent:logredactor:1.0.18
|         |    +--- com.google.re2j:re2j:1.6
|         |    +--- io.confluent:logredactor-metrics:1.0.18
|         |    +--- com.eclipsesource.minimal-json:minimal-json:0.9.5
|         |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         \--- io.confluent:common-utils:8.3.1 (*)
+--- no.nav.boot:boot-conditionals:6.0.7
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.4.0
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.4.0
|    |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:2.4.0
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
|    +--- ch.qos.logback:logback-core:1.5.34 -> 1.5.38
|    +--- org.slf4j:slf4j-api:2.0.18
|    \--- org.springframework.boot:spring-boot-autoconfigure:4.1.0 -> 4.1.1
|         \--- org.springframework.boot:spring-boot:4.1.1
|              +--- org.springframework:spring-core:7.0.9
|              |    +--- commons-logging:commons-logging:1.3.5 -> 1.3.6
|              |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|              \--- org.springframework:spring-context:7.0.9
|                   +--- org.springframework:spring-aop:7.0.9
|                   |    +--- org.springframework:spring-beans:7.0.9
|                   |    |    \--- org.springframework:spring-core:7.0.9 (*)
|                   |    \--- org.springframework:spring-core:7.0.9 (*)
|                   +--- org.springframework:spring-beans:7.0.9 (*)
|                   +--- org.springframework:spring-core:7.0.9 (*)
|                   +--- org.springframework:spring-expression:7.0.9
|                   |    \--- org.springframework:spring-core:7.0.9 (*)
|                   \--- io.micrometer:micrometer-observation:1.16.7 -> 1.17.1
|                        +--- org.jspecify:jspecify:1.0.1
|                        \--- io.micrometer:micrometer-commons:1.17.1
|                             \--- org.jspecify:jspecify:1.0.1
+--- io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations -> 2.30.0
|    \--- io.opentelemetry:opentelemetry-api:1.64.0
|         \--- io.opentelemetry:opentelemetry-context:1.64.0
|              \--- io.opentelemetry:opentelemetry-common:1.64.0
+--- io.opentelemetry.instrumentation:opentelemetry-logback-mdc-1.0:2.30.0-alpha
|    +--- io.opentelemetry.instrumentation:opentelemetry-instrumentation-api:2.30.0
|    |    \--- io.opentelemetry:opentelemetry-api:1.64.0 (*)
|    +--- io.opentelemetry.instrumentation:opentelemetry-instrumentation-api-incubator:2.30.0-alpha
|    |    +--- io.opentelemetry.semconv:opentelemetry-semconv:1.43.0
|    |    +--- io.opentelemetry.instrumentation:opentelemetry-instrumentation-api:2.30.0 (*)
|    |    \--- io.opentelemetry:opentelemetry-api-incubator:1.64.0-alpha
|    |         \--- io.opentelemetry:opentelemetry-api:1.64.0 (*)
|    \--- io.opentelemetry:opentelemetry-api:1.64.0 (*)
+--- io.micrometer:micrometer-registry-prometheus -> 1.17.1
|    +--- org.jspecify:jspecify:1.0.1
|    +--- io.micrometer:micrometer-core:1.17.1
|    |    +--- org.jspecify:jspecify:1.0.1
|    |    +--- io.micrometer:micrometer-commons:1.17.1 (*)
|    |    \--- io.micrometer:micrometer-observation:1.17.1 (*)
|    +--- io.prometheus:prometheus-metrics-core:1.7.0
|    |    +--- io.prometheus:prometheus-metrics-model:1.7.0
|    |    |    \--- io.prometheus:prometheus-metrics-config:1.7.0
|    |    \--- io.prometheus:prometheus-metrics-config:1.7.0
|    \--- io.prometheus:prometheus-metrics-tracer-common:1.7.0
+--- com.slack.api:slack-api-client-kotlin-extension:1.49.0
|    +--- com.slack.api:slack-api-model-kotlin-extension:1.49.0
|    |    +--- com.slack.api:slack-api-model:1.49.0
|    |    |    \--- com.google.code.gson:gson:2.12.1 -> 2.13.2
|    |    |         \--- com.google.errorprone:error_prone_annotations:2.41.0
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.24 -> 2.4.0 (*)
|    +--- com.slack.api:slack-api-client:1.49.0
|    |    +--- com.slack.api:slack-api-model:1.49.0 (*)
|    |    +--- com.squareup.okhttp3:okhttp:4.12.0
|    |    |    +--- com.squareup.okio:okio:3.6.0
|    |    |    |    \--- com.squareup.okio:okio-jvm:3.6.0
|    |    |    |         +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.10 -> 2.4.0 (*)
|    |    |    |         \--- org.jetbrains.kotlin:kotlin-stdlib-common:1.9.10 -> 2.4.0
|    |    |    |              \--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
|    |    |    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.21 -> 2.4.0 (*)
|    |    +--- com.google.code.gson:gson:2.12.1 -> 2.13.2 (*)
|    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|    \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.24 -> 2.4.0 (*)
+--- org.apache.commons:commons-pool2:2.13.1
+--- io.confluent:kafka-avro-serializer:8.3.1 (*)
+--- org.flywaydb:flyway-database-postgresql -> 12.4.0
|    \--- org.flywaydb:flyway-core:12.4.0
|         \--- tools.jackson.core:jackson-databind:3.1.1 -> 3.1.5
|              +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|              +--- tools.jackson.core:jackson-core:3.1.5
|              |    \--- tools.jackson:jackson-bom:3.1.5
|              |         +--- com.fasterxml.jackson.core:jackson-annotations:2.21 (c)
|              |         +--- tools.jackson.core:jackson-core:3.1.5 (c)
|              |         +--- tools.jackson.core:jackson-databind:3.1.5 (c)
|              |         \--- tools.jackson.module:jackson-module-kotlin:3.1.5 (c)
|              \--- tools.jackson:jackson-bom:3.1.5 (*)
+--- org.hibernate.orm:hibernate-micrometer -> 7.4.5.Final
|    \--- org.hibernate.orm:hibernate-platform:7.4.5.Final
|         +--- org.hibernate.orm:hibernate-micrometer:7.4.5.Final (c)
|         +--- org.hibernate.orm:hibernate-core:7.4.5.Final (c)
|         +--- jakarta.persistence:jakarta.persistence-api:3.2.0 (c)
|         \--- jakarta.transaction:jakarta.transaction-api:2.0.1 (c)
+--- tools.jackson.module:jackson-module-kotlin -> 3.1.5
|    +--- tools.jackson.core:jackson-databind:3.1.5 (*)
|    +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|    +--- org.jetbrains.kotlin:kotlin-reflect:2.1.21 -> 2.4.0 (*)
|    \--- tools.jackson:jackson-bom:3.1.5 (*)
+--- org.zalando:logbook-spring-boot-starter:4.1.0
|    +--- org.zalando:logbook-spring-boot-autoconfigure:4.1.0
|    |    +--- org.zalando:logbook-core:4.1.0
|    |    |    +--- org.zalando:logbook-api:4.1.0
|    |    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    |    +--- org.zalando:faux-pas:0.9.0
|    |    |    |    |    +--- com.google.code.findbugs:jsr305:3.0.2
|    |    |    |    |    \--- org.slf4j:slf4j-api:1.7.30 -> 2.0.18
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    |    +--- org.zalando:logbook-common:4.1.0
|    |    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.zalando:logbook-json:4.1.0
|    |    |    +--- org.zalando:logbook-api:4.1.0 (*)
|    |    |    +--- org.zalando:logbook-common:4.1.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.22 -> 2.21
|    |    |    +--- com.jayway.jsonpath:json-path:3.0.0 -> 2.10.0
|    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.zalando:logbook-json-jackson2:4.1.0
|    |    |    +--- org.zalando:logbook-api:4.1.0 (*)
|    |    |    +--- org.zalando:logbook-common:4.1.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.22 -> 2.21
|    |    |    +--- com.jayway.jsonpath:json-path:3.0.0 -> 2.10.0
|    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.zalando:logbook-spring:4.1.0
|    |    |    +--- org.zalando:logbook-core:4.1.0 (*)
|    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.zalando:logbook-servlet:4.1.0
|    |    |    +--- org.zalando:logbook-api:4.1.0 (*)
|    |    |    +--- org.zalando:logbook-core:4.1.0 (*)
|    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    +--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    +--- org.zalando:logbook-spring-boot-ecs-autoconfigure:4.1.0
|    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    +--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    +--- org.apiguardian:apiguardian-api:1.1.2
|    +--- org.zalando:faux-pas:0.9.0 (*)
|    \--- org.slf4j:slf4j-api:2.0.18
+--- net.logstash.logback:logstash-logback-encoder:9.0
|    \--- tools.jackson.core:jackson-databind:3.0.1 -> 3.1.5 (*)
+--- org.postgresql:postgresql -> 42.7.13
+--- org.springframework.boot:spring-boot-starter-actuator -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter-logging:4.1.1
|    |    |    +--- ch.qos.logback:logback-classic:1.5.38
|    |    |    |    +--- ch.qos.logback:logback-core:1.5.38
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    +--- org.apache.logging.log4j:log4j-to-slf4j:2.25.5
|    |    |    |    +--- org.apache.logging.log4j:log4j-api:2.25.5
|    |    |    |    |    +--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|    |    |    |    |    +--- biz.aQute.bnd:biz.aQute.bnd.annotation:7.1.0
|    |    |    |    |    |    +--- org.osgi:org.osgi.resource:1.0.0
|    |    |    |    |    |    \--- org.osgi:org.osgi.service.serviceloader:1.0.0
|    |    |    |    |    +--- com.google.errorprone:error_prone_annotations:2.38.0 -> 2.41.0
|    |    |    |    |    +--- org.osgi:org.osgi.annotation.bundle:2.0.0
|    |    |    |    |    |    \--- org.osgi:org.osgi.annotation.versioning:1.1.2
|    |    |    |    |    \--- org.osgi:org.osgi.annotation.versioning:1.1.2
|    |    |    |    +--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    |    +--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|    |    |    |    +--- biz.aQute.bnd:biz.aQute.bnd.annotation:7.1.0 (*)
|    |    |    |    +--- com.google.errorprone:error_prone_annotations:2.38.0 -> 2.41.0
|    |    |    |    +--- org.osgi:org.osgi.annotation.bundle:2.0.0 (*)
|    |    |    |    \--- org.osgi:org.osgi.annotation.versioning:1.1.2
|    |    |    \--- org.slf4j:jul-to-slf4j:2.0.18
|    |    |         \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.springframework.boot:spring-boot-autoconfigure:4.1.1 (*)
|    |    +--- jakarta.annotation:jakarta.annotation-api:3.0.0
|    |    \--- org.yaml:snakeyaml:2.6
|    +--- org.springframework.boot:spring-boot-starter-micrometer-metrics:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-micrometer-metrics:4.1.1
|    |         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |         +--- org.springframework.boot:spring-boot-micrometer-observation:4.1.1
|    |         |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |         |    \--- io.micrometer:micrometer-observation:1.17.1 (*)
|    |         \--- io.micrometer:micrometer-core:1.17.1 (*)
|    +--- org.springframework.boot:spring-boot-actuator-autoconfigure:4.1.1
|    |    +--- org.springframework.boot:spring-boot-autoconfigure:4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-actuator:4.1.1
|    |         \--- org.springframework.boot:spring-boot:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-health:4.1.1
|    |    \--- org.springframework.boot:spring-boot:4.1.1 (*)
|    +--- io.micrometer:micrometer-observation:1.17.1 (*)
|    \--- io.micrometer:micrometer-jakarta9:1.17.1
|         +--- org.jspecify:jspecify:1.0.1
|         +--- io.micrometer:micrometer-core:1.17.1 (*)
|         +--- io.micrometer:micrometer-commons:1.17.1 (*)
|         \--- io.micrometer:micrometer-observation:1.17.1 (*)
+--- org.springframework.boot:spring-boot-starter-cache -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-cache:4.1.1
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         \--- org.springframework:spring-context-support:7.0.9
|              +--- org.springframework:spring-beans:7.0.9 (*)
|              +--- org.springframework:spring-context:7.0.9 (*)
|              \--- org.springframework:spring-core:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-data-jpa -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jdbc:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-jdbc:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    +--- org.springframework.boot:spring-boot-sql:4.1.1
|    |    |    |    \--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    +--- org.springframework.boot:spring-boot-transaction:4.1.1
|    |    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    |    +--- org.springframework.boot:spring-boot-persistence:4.1.1
|    |    |    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    |    |    \--- org.springframework:spring-tx:7.0.9
|    |    |    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    |    |         \--- org.springframework:spring-core:7.0.9 (*)
|    |    |    |    \--- org.springframework:spring-tx:7.0.9 (*)
|    |    |    \--- org.springframework:spring-jdbc:7.0.9
|    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |    |         \--- org.springframework:spring-tx:7.0.9 (*)
|    |    \--- com.zaxxer:HikariCP:7.0.2
|    |         \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    +--- org.springframework.boot:spring-boot-data-jpa:4.1.1
|    |    +--- org.springframework.boot:spring-boot-data-commons:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    +--- org.springframework.boot:spring-boot-persistence:4.1.1 (*)
|    |    |    \--- org.springframework.data:spring-data-commons:4.1.1
|    |    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |         \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.springframework.boot:spring-boot-hibernate:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot-jpa:4.1.1
|    |    |    |    +--- org.springframework.boot:spring-boot-jdbc:4.1.1 (*)
|    |    |    |    +--- org.springframework.boot:spring-boot-transaction:4.1.1 (*)
|    |    |    |    +--- jakarta.persistence:jakarta.persistence-api:3.2.0
|    |    |    |    \--- org.springframework:spring-orm:7.0.9
|    |    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |    |    |         +--- org.springframework:spring-jdbc:7.0.9 (*)
|    |    |    |         \--- org.springframework:spring-tx:7.0.9 (*)
|    |    |    +--- org.hibernate.orm:hibernate-core:7.4.5.Final
|    |    |    |    +--- org.hibernate.orm:hibernate-platform:7.4.5.Final (*)
|    |    |    |    +--- jakarta.persistence:jakarta.persistence-api:3.2.0
|    |    |    |    \--- jakarta.transaction:jakarta.transaction-api:2.0.1
|    |    |    \--- org.springframework:spring-orm:7.0.9 (*)
|    |    +--- org.springframework.data:spring-data-jpa:4.1.1
|    |    |    +--- org.springframework.data:spring-data-commons:4.1.1 (*)
|    |    |    +--- org.springframework:spring-orm:7.0.9 (*)
|    |    |    +--- org.springframework:spring-context:7.0.9 (*)
|    |    |    +--- org.springframework:spring-aop:7.0.9 (*)
|    |    |    +--- org.springframework:spring-tx:7.0.9 (*)
|    |    |    +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    +--- org.springframework:spring-core:7.0.9 (*)
|    |    |    +--- org.antlr:antlr4-runtime:4.13.2
|    |    |    +--- jakarta.annotation:jakarta.annotation-api:2.0.0 -> 3.0.0
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    \--- org.springframework:spring-aspects:7.0.9
|    |         \--- org.aspectj:aspectjweaver:1.9.25 -> 1.9.25.1
|    \--- org.springframework.boot:spring-boot-jdbc:4.1.1 (*)
+--- org.springframework.boot:spring-boot-starter-data-redis -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-data-redis:4.1.1
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-data-commons:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-transaction:4.1.1 (*)
|    |    +--- io.lettuce:lettuce-core:7.5.2.RELEASE
|    |    |    +--- redis.clients.authentication:redis-authx-core:0.1.1-beta2
|    |    |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|    |    |    +--- io.netty:netty-common:4.2.13.Final -> 4.2.17.Final
|    |    |    +--- io.netty:netty-handler:4.2.13.Final -> 4.2.17.Final
|    |    |    |    +--- io.netty:netty-common:4.2.17.Final
|    |    |    |    +--- io.netty:netty-resolver:4.2.17.Final
|    |    |    |    |    \--- io.netty:netty-common:4.2.17.Final
|    |    |    |    +--- io.netty:netty-buffer:4.2.17.Final
|    |    |    |    |    \--- io.netty:netty-common:4.2.17.Final
|    |    |    |    +--- io.netty:netty-transport:4.2.17.Final
|    |    |    |    |    +--- io.netty:netty-common:4.2.17.Final
|    |    |    |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|    |    |    |    |    \--- io.netty:netty-resolver:4.2.17.Final (*)
|    |    |    |    +--- io.netty:netty-transport-native-unix-common:4.2.17.Final
|    |    |    |    |    +--- io.netty:netty-common:4.2.17.Final
|    |    |    |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|    |    |    |    |    \--- io.netty:netty-transport:4.2.17.Final (*)
|    |    |    |    \--- io.netty:netty-codec-base:4.2.17.Final
|    |    |    |         +--- io.netty:netty-common:4.2.17.Final
|    |    |    |         +--- io.netty:netty-buffer:4.2.17.Final (*)
|    |    |    |         \--- io.netty:netty-transport:4.2.17.Final (*)
|    |    |    +--- io.netty:netty-transport:4.2.13.Final -> 4.2.17.Final (*)
|    |    |    +--- io.projectreactor:reactor-core:3.6.6 -> 3.8.7
|    |    |    |    +--- org.reactivestreams:reactive-streams:1.0.4
|    |    |    |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|    |    |    \--- io.netty:netty-resolver-dns:4.2.13.Final -> 4.2.17.Final
|    |    |         +--- io.netty:netty-common:4.2.17.Final
|    |    |         +--- io.netty:netty-buffer:4.2.17.Final (*)
|    |    |         +--- io.netty:netty-resolver:4.2.17.Final (*)
|    |    |         +--- io.netty:netty-transport:4.2.17.Final (*)
|    |    |         +--- io.netty:netty-codec-base:4.2.17.Final (*)
|    |    |         +--- io.netty:netty-codec-dns:4.2.17.Final
|    |    |         |    +--- io.netty:netty-common:4.2.17.Final
|    |    |         |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|    |    |         |    +--- io.netty:netty-transport:4.2.17.Final (*)
|    |    |         |    \--- io.netty:netty-codec-base:4.2.17.Final (*)
|    |    |         \--- io.netty:netty-handler:4.2.17.Final (*)
|    |    \--- org.springframework.data:spring-data-redis:4.1.1
|    |         +--- org.springframework.data:spring-data-keyvalue:4.1.1
|    |         |    +--- org.springframework.data:spring-data-commons:4.1.1 (*)
|    |         |    +--- org.springframework:spring-context:7.0.9 (*)
|    |         |    +--- org.springframework:spring-tx:7.0.9 (*)
|    |         |    \--- org.slf4j:slf4j-api:2.0.18
|    |         +--- org.springframework:spring-tx:7.0.9 (*)
|    |         +--- org.springframework:spring-oxm:7.0.9
|    |         |    +--- org.springframework:spring-beans:7.0.9 (*)
|    |         |    \--- org.springframework:spring-core:7.0.9 (*)
|    |         +--- org.springframework:spring-aop:7.0.9 (*)
|    |         +--- org.springframework:spring-context-support:7.0.9 (*)
|    |         \--- org.slf4j:slf4j-api:2.0.18
|    \--- org.springframework:spring-messaging:7.0.9
|         +--- org.springframework:spring-beans:7.0.9 (*)
|         \--- org.springframework:spring-core:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-flyway -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jdbc:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-flyway:4.1.1
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-jdbc:4.1.1 (*)
|    |    \--- org.flywaydb:flyway-core:12.4.0 (*)
|    \--- org.springframework.boot:spring-boot-jdbc:4.1.1 (*)
+--- org.springframework.boot:spring-boot-starter-graphql -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jackson:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-jackson:4.1.1
|    |         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |         \--- tools.jackson.core:jackson-databind:3.1.5 (*)
|    +--- org.springframework.boot:spring-boot-reactor:4.1.1
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    \--- io.projectreactor:reactor-core:3.8.7 (*)
|    \--- org.springframework.boot:spring-boot-graphql:4.1.1
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         \--- org.springframework.graphql:spring-graphql:2.0.5
|              +--- com.graphql-java:graphql-java:25.0
|              |    +--- com.graphql-java:java-dataloader:6.0.0
|              |    |    +--- org.reactivestreams:reactive-streams:1.0.3 -> 1.0.4
|              |    |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|              |    +--- org.reactivestreams:reactive-streams:1.0.3 -> 1.0.4
|              |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|              +--- io.projectreactor:reactor-core:3.8.7 (*)
|              \--- org.springframework:spring-context:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-jetty -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jetty-runtime:4.1.1
|    |    +--- org.springframework.boot:spring-boot-jetty:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot-web-server:4.1.1
|    |    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    |    \--- org.springframework:spring-web:7.0.9
|    |    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |    |    |         \--- io.micrometer:micrometer-observation:1.16.7 -> 1.17.1 (*)
|    |    |    \--- org.eclipse.jetty.ee11:jetty-ee11-webapp:12.1.12
|    |    |         +--- org.eclipse.jetty:jetty-session:12.1.12
|    |    |         |    +--- org.eclipse.jetty:jetty-server:12.1.12
|    |    |         |    |    +--- org.eclipse.jetty:jetty-http:12.1.12
|    |    |         |    |    |    +--- org.eclipse.jetty:jetty-io:12.1.12
|    |    |         |    |    |    |    +--- org.eclipse.jetty:jetty-util:12.1.12
|    |    |         |    |    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         |    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         |    |    |    +--- org.eclipse.jetty:jetty-util:12.1.12 (*)
|    |    |         |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         |    |    +--- org.eclipse.jetty:jetty-io:12.1.12 (*)
|    |    |         |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         +--- org.eclipse.jetty:jetty-xml:12.1.12
|    |    |         |    +--- org.eclipse.jetty:jetty-util:12.1.12 (*)
|    |    |         |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         +--- org.eclipse.jetty.ee:jetty-ee-webapp:12.1.12
|    |    |         |    +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |         |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         +--- org.eclipse.jetty.ee11:jetty-ee11-servlet:12.1.12
|    |    |         |    +--- jakarta.servlet:jakarta.servlet-api:6.1.0
|    |    |         |    +--- org.eclipse.jetty:jetty-security:12.1.12
|    |    |         |    |    +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |         |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         |    +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |         |    +--- org.eclipse.jetty:jetty-session:12.1.12 (*)
|    |    |         |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    +--- org.springframework.boot:spring-boot-web-server:4.1.1 (*)
|    |    +--- jakarta.servlet:jakarta.servlet-api:6.1.0
|    |    +--- jakarta.websocket:jakarta.websocket-api:2.2.0
|    |    +--- jakarta.websocket:jakarta.websocket-client-api:2.2.0
|    |    +--- org.apache.tomcat.embed:tomcat-embed-el:11.0.24
|    |    +--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-jakarta-server:12.1.12
|    |    |    +--- jakarta.websocket:jakarta.websocket-api:2.2.0
|    |    |    +--- org.eclipse.jetty.ee11:jetty-ee11-annotations:12.1.12
|    |    |    |    +--- jakarta.annotation:jakarta.annotation-api:3.0.0
|    |    |    |    +--- jakarta.servlet:jakarta.servlet-api:6.1.0
|    |    |    |    +--- org.eclipse.jetty:jetty-annotations:12.1.12
|    |    |    |    |    +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |    |    |    +--- org.ow2.asm:asm:9.10.1
|    |    |    |    |    \--- org.ow2.asm:asm-commons:9.10.1
|    |    |    |    |         +--- org.ow2.asm:asm:9.10.1
|    |    |    |    |         \--- org.ow2.asm:asm-tree:9.10.1
|    |    |    |    |              \--- org.ow2.asm:asm:9.10.1
|    |    |    |    +--- org.eclipse.jetty.ee11:jetty-ee11-plus:12.1.12
|    |    |    |    |    +--- jakarta.enterprise:jakarta.enterprise.cdi-api:4.1.0
|    |    |    |    |    |    +--- jakarta.enterprise:jakarta.enterprise.lang-model:4.1.0
|    |    |    |    |    |    +--- jakarta.annotation:jakarta.annotation-api:3.0.0
|    |    |    |    |    |    +--- jakarta.interceptor:jakarta.interceptor-api:2.2.0
|    |    |    |    |    |    |    \--- jakarta.annotation:jakarta.annotation-api:3.0.0
|    |    |    |    |    |    \--- jakarta.inject:jakarta.inject-api:2.0.1
|    |    |    |    |    +--- jakarta.enterprise:jakarta.enterprise.lang-model:4.1.0
|    |    |    |    |    +--- jakarta.interceptor:jakarta.interceptor-api:2.2.0 (*)
|    |    |    |    |    +--- jakarta.transaction:jakarta.transaction-api:2.0.1
|    |    |    |    |    +--- org.eclipse.jetty:jetty-plus:12.1.12
|    |    |    |    |    |    +--- org.eclipse.jetty:jetty-security:12.1.12 (*)
|    |    |    |    |    |    \--- org.eclipse.jetty:jetty-util:12.1.12 (*)
|    |    |    |    |    \--- org.eclipse.jetty.ee11:jetty-ee11-webapp:12.1.12 (*)
|    |    |    |    +--- org.eclipse.jetty.ee11:jetty-ee11-webapp:12.1.12 (*)
|    |    |    |    +--- org.ow2.asm:asm:9.10.1
|    |    |    |    \--- org.ow2.asm:asm-commons:9.10.1 (*)
|    |    |    +--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-jakarta-client:12.1.12
|    |    |    |    +--- jakarta.websocket:jakarta.websocket-api:2.2.0
|    |    |    |    +--- jakarta.websocket:jakarta.websocket-client-api:2.2.0
|    |    |    |    +--- org.eclipse.jetty:jetty-client:12.1.12
|    |    |    |    |    +--- org.eclipse.jetty:jetty-alpn-client:12.1.12
|    |    |    |    |    |    \--- org.eclipse.jetty:jetty-io:12.1.12 (*)
|    |    |    |    |    +--- org.eclipse.jetty:jetty-http:12.1.12 (*)
|    |    |    |    |    +--- org.eclipse.jetty:jetty-io:12.1.12 (*)
|    |    |    |    |    \--- org.eclipse.jetty.compression:jetty-compression-gzip:12.1.12
|    |    |    |    |         \--- org.eclipse.jetty.compression:jetty-compression-common:12.1.12
|    |    |    |    |              +--- org.eclipse.jetty:jetty-http:12.1.12 (*)
|    |    |    |    |              +--- org.eclipse.jetty:jetty-io:12.1.12 (*)
|    |    |    |    |              \--- org.eclipse.jetty:jetty-util:12.1.12 (*)
|    |    |    |    +--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-jakarta-common:12.1.12
|    |    |    |    |    +--- jakarta.websocket:jakarta.websocket-api:2.2.0
|    |    |    |    |    +--- jakarta.websocket:jakarta.websocket-client-api:2.2.0
|    |    |    |    |    \--- org.eclipse.jetty.websocket:jetty-websocket-core-client:12.1.12
|    |    |    |    |         +--- org.eclipse.jetty:jetty-client:12.1.12 (*)
|    |    |    |    |         \--- org.eclipse.jetty.websocket:jetty-websocket-core-common:12.1.12
|    |    |    |    |              +--- org.eclipse.jetty:jetty-http:12.1.12 (*)
|    |    |    |    |              \--- org.eclipse.jetty:jetty-io:12.1.12 (*)
|    |    |    |    \--- org.eclipse.jetty.websocket:jetty-websocket-core-client:12.1.12 (*)
|    |    |    \--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-servlet:12.1.12
|    |    |         +--- org.eclipse.jetty.ee11:jetty-ee11-servlet:12.1.12 (*)
|    |    |         \--- org.eclipse.jetty.websocket:jetty-websocket-core-server:12.1.12
|    |    |              +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |              \--- org.eclipse.jetty.websocket:jetty-websocket-core-common:12.1.12 (*)
|    |    \--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-jetty-server:12.1.12
|    |         +--- jakarta.servlet:jakarta.servlet-api:6.1.0
|    |         +--- org.eclipse.jetty.ee11:jetty-ee11-annotations:12.1.12 (*)
|    |         +--- org.eclipse.jetty.ee11:jetty-ee11-servlet:12.1.12 (*)
|    |         +--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-servlet:12.1.12 (*)
|    |         +--- org.eclipse.jetty.websocket:jetty-websocket-jetty-api:12.1.12
|    |         +--- org.eclipse.jetty.websocket:jetty-websocket-jetty-common:12.1.12
|    |         |    +--- org.eclipse.jetty.websocket:jetty-websocket-core-common:12.1.12 (*)
|    |         |    \--- org.eclipse.jetty.websocket:jetty-websocket-jetty-api:12.1.12
|    |         \--- org.eclipse.jetty.websocket:jetty-websocket-jetty-server:12.1.12
|    |              +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |              +--- org.eclipse.jetty.websocket:jetty-websocket-core-server:12.1.12 (*)
|    |              \--- org.eclipse.jetty.websocket:jetty-websocket-jetty-common:12.1.12 (*)
|    +--- org.springframework.boot:spring-boot-jetty:4.1.1 (*)
|    +--- org.slf4j:slf4j-api:2.0.18
|    \--- jakarta.annotation:jakarta.annotation-api:3.0.0
+--- org.springframework.boot:spring-boot-starter-kafka -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-kafka:4.1.1
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         +--- org.springframework.boot:spring-boot-transaction:4.1.1 (*)
|         \--- org.springframework.kafka:spring-kafka:4.1.1
|              +--- org.springframework:spring-context:7.0.9 (*)
|              +--- org.springframework:spring-messaging:7.0.9 (*)
|              +--- org.springframework:spring-tx:7.0.9 (*)
|              +--- org.apache.kafka:kafka-clients:4.2.1
|              \--- io.micrometer:micrometer-observation:1.17.1 (*)
+--- org.springframework.boot:spring-boot-starter-oauth2-client -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-security:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-security:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    +--- org.springframework.security:spring-security-config:7.1.1
|    |    |    |    +--- org.springframework.security:spring-security-core:7.1.1
|    |    |    |    |    +--- org.springframework.security:spring-security-crypto:7.1.1
|    |    |    |    |    +--- org.springframework:spring-aop:7.0.9 (*)
|    |    |    |    |    +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    |    |    +--- org.springframework:spring-context:7.0.9 (*)
|    |    |    |    |    +--- org.springframework:spring-core:7.0.9 (*)
|    |    |    |    |    +--- org.springframework:spring-expression:7.0.9 (*)
|    |    |    |    |    \--- io.micrometer:micrometer-observation:1.17.1 (*)
|    |    |    |    +--- org.springframework:spring-aop:7.0.9 (*)
|    |    |    |    +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    |    +--- org.springframework:spring-context:7.0.9 (*)
|    |    |    |    \--- org.springframework:spring-core:7.0.9 (*)
|    |    |    \--- org.springframework.security:spring-security-web:7.1.1
|    |    |         +--- org.springframework.security:spring-security-core:7.1.1 (*)
|    |    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |    |         +--- org.springframework:spring-aop:7.0.9 (*)
|    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |         +--- org.springframework:spring-context:7.0.9 (*)
|    |    |         +--- org.springframework:spring-expression:7.0.9 (*)
|    |    |         \--- org.springframework:spring-web:7.0.9 (*)
|    |    \--- org.springframework:spring-aop:7.0.9 (*)
|    +--- org.springframework.boot:spring-boot-security-oauth2-client:4.1.1
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    \--- org.springframework.security:spring-security-oauth2-client:7.1.1
|    |         +--- org.springframework.security:spring-security-core:7.1.1 (*)
|    |         +--- org.springframework.security:spring-security-oauth2-core:7.1.1
|    |         |    +--- org.springframework.security:spring-security-core:7.1.1 (*)
|    |         |    +--- org.springframework:spring-core:7.0.9 (*)
|    |         |    \--- org.springframework:spring-web:7.0.9 (*)
|    |         +--- org.springframework.security:spring-security-web:7.1.1 (*)
|    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |         \--- com.nimbusds:oauth2-oidc-sdk:11.38.2
|    |              +--- com.github.stephenc.jcip:jcip-annotations:1.0-1
|    |              +--- com.nimbusds:content-type:2.3
|    |              +--- net.minidev:json-smart:2.6.0
|    |              |    \--- net.minidev:accessors-smart:2.6.0
|    |              |         \--- org.ow2.asm:asm:9.7.1 -> 9.10.1
|    |              +--- com.nimbusds:lang-tag:1.7
|    |              \--- com.nimbusds:nimbus-jose-jwt:10.9.1
|    \--- org.springframework.security:spring-security-oauth2-jose:7.1.1
|         +--- org.springframework.security:spring-security-core:7.1.1 (*)
|         +--- org.springframework.security:spring-security-oauth2-core:7.1.1 (*)
|         +--- org.springframework:spring-core:7.0.9 (*)
|         \--- com.nimbusds:nimbus-jose-jwt:10.9.1
+--- org.springframework.boot:spring-boot-starter-oauth2-resource-server -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-security:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-security-oauth2-resource-server:4.1.1
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         +--- org.springframework.security:spring-security-oauth2-jose:7.1.1 (*)
|         \--- org.springframework.security:spring-security-oauth2-resource-server:7.1.1
|              +--- org.springframework.security:spring-security-core:7.1.1 (*)
|              +--- org.springframework.security:spring-security-oauth2-core:7.1.1 (*)
|              +--- org.springframework.security:spring-security-web:7.1.1 (*)
|              \--- org.springframework:spring-core:7.0.9 (*)
+--- org.springframework.boot:spring-boot-restclient -> 4.1.1
|    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-http-client:4.1.1
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         \--- org.springframework:spring-web:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-validation -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-validation:4.1.1
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         +--- org.apache.tomcat.embed:tomcat-embed-el:11.0.24
|         \--- org.hibernate.validator:hibernate-validator:9.1.3.Final
|              +--- jakarta.validation:jakarta.validation-api:3.1.1
|              +--- org.jboss.logging:jboss-logging:3.6.3.Final
|              \--- com.fasterxml:classmate:1.7.1 -> 1.7.3
+--- org.springframework.boot:spring-boot-starter-web -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter-jackson:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-http-converter:4.1.1
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    \--- org.springframework:spring-web:7.0.9 (*)
|    \--- org.springframework.boot:spring-boot-webmvc:4.1.1
|         +--- org.springframework.boot:spring-boot-servlet:4.1.1
|         |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         |    \--- org.springframework:spring-web:7.0.9 (*)
|         +--- org.springframework:spring-web:7.0.9 (*)
|         \--- org.springframework:spring-webmvc:7.0.9
|              +--- org.springframework:spring-aop:7.0.9 (*)
|              +--- org.springframework:spring-beans:7.0.9 (*)
|              +--- org.springframework:spring-context:7.0.9 (*)
|              +--- org.springframework:spring-core:7.0.9 (*)
|              +--- org.springframework:spring-expression:7.0.9 (*)
|              \--- org.springframework:spring-web:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-webclient -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jackson:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-reactor:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-webclient:4.1.1
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-http-client:4.1.1 (*)
|    |    \--- org.springframework:spring-webflux:7.0.9
|    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |         +--- org.springframework:spring-web:7.0.9 (*)
|    |         \--- io.projectreactor:reactor-core:3.8.7 (*)
|    \--- io.projectreactor.netty:reactor-netty-http:1.3.7
|         +--- io.netty:netty-codec-http:4.2.17.Final
|         |    +--- io.netty:netty-common:4.2.17.Final
|         |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-compression:4.2.17.Final
|         |    |    +--- io.netty:netty-common:4.2.17.Final
|         |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    |    \--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    \--- io.netty:netty-handler:4.2.17.Final (*)
|         +--- io.netty:netty-codec-http2:4.2.17.Final
|         |    +--- io.netty:netty-common:4.2.17.Final
|         |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    +--- io.netty:netty-handler:4.2.17.Final (*)
|         |    \--- io.netty:netty-codec-http:4.2.17.Final (*)
|         +--- io.netty:netty-codec-http3:4.2.17.Final
|         |    +--- io.netty:netty-common:4.2.17.Final
|         |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-http:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-compression:4.2.17.Final (*)
|         |    +--- io.netty:netty-handler:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport-native-unix-common:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    +--- io.netty:netty-resolver:4.2.17.Final (*)
|         |    \--- io.netty:netty-codec-classes-quic:4.2.17.Final
|         +--- io.netty:netty-resolver-dns:4.2.17.Final (*)
|         +--- io.netty:netty-resolver-dns-native-macos:4.2.17.Final
|         |    \--- io.netty:netty-resolver-dns-classes-macos:4.2.17.Final
|         |         +--- io.netty:netty-common:4.2.17.Final
|         |         +--- io.netty:netty-resolver-dns:4.2.17.Final (*)
|         |         \--- io.netty:netty-transport-native-unix-common:4.2.17.Final (*)
|         +--- io.netty:netty-transport-native-epoll:4.2.17.Final
|         |    +--- io.netty:netty-common:4.2.17.Final
|         |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport-native-unix-common:4.2.17.Final (*)
|         |    \--- io.netty:netty-transport-classes-epoll:4.2.17.Final
|         |         +--- io.netty:netty-common:4.2.17.Final
|         |         +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |         +--- io.netty:netty-transport:4.2.17.Final (*)
|         |         \--- io.netty:netty-transport-native-unix-common:4.2.17.Final (*)
|         +--- io.projectreactor.netty:reactor-netty-core:1.3.7
|         |    +--- io.netty:netty-handler:4.2.17.Final (*)
|         |    +--- io.netty:netty-handler-proxy:4.2.17.Final
|         |    |    +--- io.netty:netty-common:4.2.17.Final
|         |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-codec-socks:4.2.17.Final
|         |    |    |    +--- io.netty:netty-common:4.2.17.Final
|         |    |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    |    |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    |    |    \--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-codec-http:4.2.17.Final (*)
|         |    |    \--- io.netty:netty-handler:4.2.17.Final (*)
|         |    +--- io.netty:netty-resolver-dns:4.2.17.Final (*)
|         |    +--- io.netty:netty-resolver-dns-native-macos:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport-native-epoll:4.2.17.Final (*)
|         |    +--- io.projectreactor:reactor-core:3.8.7 (*)
|         |    \--- org.jspecify:jspecify:1.0.1
|         +--- io.projectreactor:reactor-core:3.8.7 (*)
|         \--- org.jspecify:jspecify:1.0.1
\--- org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.0
     +--- org.springdoc:springdoc-openapi-starter-webmvc-api:3.1.0
     |    +--- org.springdoc:springdoc-openapi-starter-common:3.1.0
     |    |    +--- org.springframework.boot:spring-boot-starter:4.1.0 -> 4.1.1 (*)
     |    |    +--- org.springframework.boot:spring-boot-autoconfigure:4.1.0 -> 4.1.1 (*)
     |    |    +--- org.springframework.boot:spring-boot-validation:4.1.0 -> 4.1.1 (*)
     |    |    +--- io.swagger.core.v3:swagger-core-jakarta:2.2.52
     |    |    |    +--- org.apache.commons:commons-lang3:3.20.0
     |    |    |    +--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
     |    |    |    +--- io.swagger.core.v3:swagger-annotations-jakarta:2.2.52
     |    |    |    +--- io.swagger.core.v3:swagger-models-jakarta:2.2.52
     |    |    |    |    \--- com.fasterxml.jackson.core:jackson-annotations:2.21
     |    |    |    +--- org.yaml:snakeyaml:2.6
     |    |    |    +--- jakarta.xml.bind:jakarta.xml.bind-api:3.0.1 -> 4.0.5
     |    |    |    |    \--- jakarta.activation:jakarta.activation-api:2.1.4
     |    |    |    +--- jakarta.validation:jakarta.validation-api:3.0.2 -> 3.1.1
     |    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.21
     |    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.1 -> 2.21.5 (*)
     |    |    |    +--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.22.0 -> 2.21.5
     |    |    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.5 (*)
     |    |    |    |    +--- org.yaml:snakeyaml:2.5 -> 2.6
     |    |    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (*)
     |    |    |    |    \--- com.fasterxml.jackson:jackson-bom:2.21.5 (*)
     |    |    |    \--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.22.0 -> 2.21.5
     |    |    |         +--- com.fasterxml.jackson.core:jackson-annotations:2.21
     |    |    |         +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (*)
     |    |    |         +--- com.fasterxml.jackson.core:jackson-databind:2.21.5 (*)
     |    |    |         \--- com.fasterxml.jackson:jackson-bom:2.21.5 (*)
     |    |    \--- org.springframework.boot:spring-boot-jackson:4.1.0 -> 4.1.1 (*)
     |    +--- org.springframework.boot:spring-boot-webmvc:4.1.0 -> 4.1.1 (*)
     |    \--- org.springframework.boot:spring-boot-web-server:4.1.0 -> 4.1.1 (*)
     +--- org.webjars:swagger-ui:5.32.11
     \--- org.webjars:webjars-locator-lite:1.1.3 -> 1.1.4
          \--- org.jspecify:jspecify:1.0.0 -> 1.0.1

compileOnly - Compile only dependencies for 'main'. (n)
No dependencies

cyclonedxBom
\--- root project 'populasjonstilgangskontroll' (*)

cyclonedxDirectBom (n)
No dependencies

default - Configuration for default artifacts. (n)
No dependencies

developmentOnly - Configuration for development-only dependencies such as Spring Boot's DevTools.
No dependencies

implementation - Implementation only dependencies for 'main'. (n)
+--- no.nav.pdl.libs:contract-pdl-avro:18 (n)
+--- no.nav.boot:boot-conditionals:6.0.7 (n)
+--- io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations (n)
+--- io.opentelemetry.instrumentation:opentelemetry-logback-mdc-1.0:2.30.0-alpha (n)
+--- io.micrometer:micrometer-registry-prometheus (n)
+--- com.slack.api:slack-api-client-kotlin-extension:1.49.0 (n)
+--- org.apache.commons:commons-pool2:2.13.1 (n)
+--- io.confluent:kafka-avro-serializer:8.3.1 (n)
+--- org.flywaydb:flyway-database-postgresql (n)
+--- org.hibernate.orm:hibernate-micrometer (n)
+--- tools.jackson.module:jackson-module-kotlin (n)
+--- org.zalando:logbook-spring-boot-starter:4.1.0 (n)
+--- net.logstash.logback:logstash-logback-encoder:9.0 (n)
+--- org.postgresql:postgresql (n)
+--- org.springframework.boot:spring-boot-starter-actuator (n)
+--- org.springframework.boot:spring-boot-starter-cache (n)
+--- org.springframework.boot:spring-boot-starter-data-jpa (n)
+--- org.springframework.boot:spring-boot-starter-data-redis (n)
+--- org.springframework.boot:spring-boot-starter-flyway (n)
+--- org.springframework.boot:spring-boot-starter-graphql (n)
+--- org.springframework.boot:spring-boot-starter-jetty (n)
+--- org.springframework.boot:spring-boot-starter-kafka (n)
+--- org.springframework.boot:spring-boot-starter-oauth2-client (n)
+--- org.springframework.boot:spring-boot-starter-oauth2-resource-server (n)
+--- org.springframework.boot:spring-boot-restclient (n)
+--- org.springframework.boot:spring-boot-starter-validation (n)
+--- org.springframework.boot:spring-boot-starter-web (n)
+--- org.springframework.boot:spring-boot-starter-webclient (n)
\--- org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.0 (n)

implementationDependenciesMetadata
No dependencies

jacocoAgent - The Jacoco agent to use to get coverage data.
\--- org.jacoco:org.jacoco.agent:0.8.14

jacocoAnt - The Jacoco ant tasks to use to get execute Gradle tasks.
\--- org.jacoco:org.jacoco.ant:0.8.14
     +--- org.jacoco:org.jacoco.core:0.8.14
     |    +--- org.ow2.asm:asm:9.9
     |    +--- org.ow2.asm:asm-commons:9.9
     |    |    +--- org.ow2.asm:asm:9.9
     |    |    \--- org.ow2.asm:asm-tree:9.9
     |    |         \--- org.ow2.asm:asm:9.9
     |    \--- org.ow2.asm:asm-tree:9.9 (*)
     +--- org.jacoco:org.jacoco.report:0.8.14
     |    \--- org.jacoco:org.jacoco.core:0.8.14 (*)
     \--- org.jacoco:org.jacoco.agent:0.8.14

kotlinAbiValidationCompatClasspath
+--- org.jetbrains.kotlin:kotlin-build-tools-compat:2.4.0
|    \--- org.jetbrains.kotlin:kotlin-tooling-core:2.4.0 -> 2.4.20
\--- org.jetbrains.kotlin:kotlin-build-tools-impl:{strictly [2.4.0-Beta2, 2.5.0)} -> 2.4.20
     +--- org.jetbrains.kotlin:kotlin-build-tools-api:2.4.20
     +--- org.jetbrains.kotlin:kotlin-stdlib:2.4.20 -> 2.4.0
     |    \--- org.jetbrains:annotations:13.0
     +--- org.jetbrains.kotlin:kotlin-build-tools-cri-impl:2.4.20
     |    +--- org.jetbrains.kotlin:kotlin-build-tools-api:2.4.20
     |    \--- org.jetbrains.kotlin:kotlin-stdlib:2.4.20 -> 2.4.0 (*)
     +--- org.jetbrains.kotlin:kotlin-tooling-core:2.4.20
     +--- org.jetbrains.kotlin:kotlin-compiler-embeddable:2.4.20 -> 2.4.0
     |    +--- org.jetbrains.kotlin:kotlin-build-tools-api:2.4.0 -> 2.4.20
     |    +--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
     |    +--- org.jetbrains.kotlin:kotlin-script-runtime:2.4.0
     |    +--- org.jetbrains.kotlin:kotlin-reflect:1.6.10 -> 2.4.0
     |    +--- org.jetbrains.kotlin:kotlin-daemon-embeddable:2.4.0
     |    \--- org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.8.0 -> 1.10.2
     \--- org.jetbrains.kotlin:kotlin-compiler-runner:2.4.20
          +--- org.jetbrains.kotlin:kotlin-daemon-client:2.4.20 -> 2.4.0
          |    \--- org.jetbrains.kotlin:kotlin-stdlib:2.2.21 -> 2.4.0 (*)
          +--- org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.8.0 -> 1.10.2
          \--- org.jetbrains.kotlin:kotlin-compiler-embeddable:2.4.20 -> 2.4.0 (*)

kotlinBuildToolsApiClasspath
+--- org.jetbrains.kotlin:kotlin-build-tools-compat:2.4.0
|    \--- org.jetbrains.kotlin:kotlin-tooling-core:2.4.0
\--- org.jetbrains.kotlin:kotlin-build-tools-impl:{strictly 2.4.0} -> 2.4.0
     +--- org.jetbrains.kotlin:kotlin-build-tools-api:2.4.0
     +--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0
     |    \--- org.jetbrains:annotations:13.0
     +--- org.jetbrains.kotlin:kotlin-build-tools-cri-impl:2.4.0
     |    +--- org.jetbrains.kotlin:kotlin-build-tools-api:2.4.0
     |    \--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
     +--- org.jetbrains.kotlin:kotlin-compiler-embeddable:2.4.0
     |    +--- org.jetbrains.kotlin:kotlin-build-tools-api:2.4.0
     |    +--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
     |    +--- org.jetbrains.kotlin:kotlin-script-runtime:2.4.0
     |    +--- org.jetbrains.kotlin:kotlin-reflect:1.6.10 -> 2.4.0
     |    +--- org.jetbrains.kotlin:kotlin-daemon-embeddable:2.4.0
     |    \--- org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.8.0 -> 1.10.2
     \--- org.jetbrains.kotlin:kotlin-compiler-runner:2.4.0
          +--- org.jetbrains.kotlin:kotlin-daemon-client:2.4.0
          |    \--- org.jetbrains.kotlin:kotlin-stdlib:2.2.21 -> 2.4.0 (*)
          +--- org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.8.0 -> 1.10.2
          \--- org.jetbrains.kotlin:kotlin-compiler-embeddable:2.4.0 (*)

kotlinCompilerClasspath
+--- org.jetbrains.kotlin:kotlin-build-tools-compat:2.4.0
|    \--- org.jetbrains.kotlin:kotlin-tooling-core:2.4.0
\--- org.jetbrains.kotlin:kotlin-build-tools-impl:2.4.0
     +--- org.jetbrains.kotlin:kotlin-build-tools-api:2.4.0
     +--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0
     |    \--- org.jetbrains:annotations:13.0
     +--- org.jetbrains.kotlin:kotlin-build-tools-cri-impl:2.4.0
     |    +--- org.jetbrains.kotlin:kotlin-build-tools-api:2.4.0
     |    \--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
     +--- org.jetbrains.kotlin:kotlin-compiler-embeddable:2.4.0
     |    +--- org.jetbrains.kotlin:kotlin-build-tools-api:2.4.0
     |    +--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
     |    +--- org.jetbrains.kotlin:kotlin-script-runtime:2.4.0
     |    +--- org.jetbrains.kotlin:kotlin-reflect:1.6.10 -> 2.4.0
     |    +--- org.jetbrains.kotlin:kotlin-daemon-embeddable:2.4.0
     |    \--- org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.8.0 -> 1.10.2
     \--- org.jetbrains.kotlin:kotlin-compiler-runner:2.4.0
          +--- org.jetbrains.kotlin:kotlin-daemon-client:2.4.0
          |    \--- org.jetbrains.kotlin:kotlin-stdlib:2.2.21 -> 2.4.0 (*)
          +--- org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.8.0 -> 1.10.2
          \--- org.jetbrains.kotlin:kotlin-compiler-embeddable:2.4.0 (*)

kotlinCompilerPluginClasspath
No dependencies

kotlinCompilerPluginClasspathMain - Kotlin compiler plugins for compilation
+--- org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:2.4.0
|    +--- org.jetbrains.kotlin:kotlin-scripting-common:2.4.0
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0
|    |         \--- org.jetbrains:annotations:13.0
|    +--- org.jetbrains.kotlin:kotlin-scripting-jvm:2.4.0
|    |    +--- org.jetbrains.kotlin:kotlin-script-runtime:2.4.0
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
|    |    \--- org.jetbrains.kotlin:kotlin-scripting-common:2.4.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-scripting-compiler-impl-embeddable:2.4.0
|    |    +--- org.jetbrains.kotlin:kotlin-scripting-common:2.4.0 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-scripting-jvm:2.4.0 (*)
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
|    \--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
+--- org.jetbrains.kotlin:kotlin-allopen-compiler-plugin-embeddable:2.4.0
\--- org.jetbrains.kotlin:kotlin-noarg-compiler-plugin-embeddable:2.4.0

kotlinCompilerPluginClasspathTest - Kotlin compiler plugins for compilation
+--- org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:2.4.0
|    +--- org.jetbrains.kotlin:kotlin-scripting-common:2.4.0
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0
|    |         \--- org.jetbrains:annotations:13.0
|    +--- org.jetbrains.kotlin:kotlin-scripting-jvm:2.4.0
|    |    +--- org.jetbrains.kotlin:kotlin-script-runtime:2.4.0
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
|    |    \--- org.jetbrains.kotlin:kotlin-scripting-common:2.4.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-scripting-compiler-impl-embeddable:2.4.0
|    |    +--- org.jetbrains.kotlin:kotlin-scripting-common:2.4.0 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-scripting-jvm:2.4.0 (*)
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
|    \--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
+--- org.jetbrains.kotlin:kotlin-allopen-compiler-plugin-embeddable:2.4.0
\--- org.jetbrains.kotlin:kotlin-noarg-compiler-plugin-embeddable:2.4.0

kotlinKlibCommonizerClasspath
\--- org.jetbrains.kotlin:kotlin-klib-commonizer-embeddable:2.4.0
     +--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0
     |    \--- org.jetbrains:annotations:13.0
     \--- org.jetbrains.kotlin:kotlin-compiler-embeddable:2.4.0
          +--- org.jetbrains.kotlin:kotlin-build-tools-api:2.4.0
          +--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
          +--- org.jetbrains.kotlin:kotlin-script-runtime:2.4.0
          +--- org.jetbrains.kotlin:kotlin-reflect:1.6.10 -> 2.4.0
          +--- org.jetbrains.kotlin:kotlin-daemon-embeddable:2.4.0
          \--- org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.8.0 -> 1.10.2

kotlinNativeCompilerPluginClasspath
No dependencies

kotlinScriptDef - Script filename extensions discovery classpath configuration (n)
No dependencies

kotlinScriptDefExtensions
No dependencies

mainSourceElements - List of source directories contained in the Main SourceSet. (n)
No dependencies

productionRuntimeClasspath
+--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0
|    +--- org.jetbrains:annotations:13.0
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0 -> 2.4.0 (c)
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.0 -> 2.4.0 (c)
|    \--- org.jetbrains.kotlin:kotlin-stdlib-common:2.4.0 (c)
+--- no.nav.pdl.libs:contract-pdl-avro:18
|    \--- io.confluent:kafka-avro-serializer:7.7.1 -> 8.3.1
|         +--- org.apache.avro:avro:1.12.1
|         |    +--- com.fasterxml.jackson.core:jackson-core:2.20.0 -> 2.21.5
|         |    |    \--- com.fasterxml.jackson:jackson-bom:2.21.5
|         |    |         +--- com.fasterxml.jackson.core:jackson-annotations:2.21 (c)
|         |    |         +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (c)
|         |    |         +--- com.fasterxml.jackson.core:jackson-databind:2.21.5 (c)
|         |    |         +--- com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.21.5 (c)
|         |    |         +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.21.5 (c)
|         |    |         +--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.21.5 (c)
|         |    |         \--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.21.5 (c)
|         |    +--- com.fasterxml.jackson.core:jackson-databind:2.20.0 -> 2.21.5
|         |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|         |    |    +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (*)
|         |    |    \--- com.fasterxml.jackson:jackson-bom:2.21.5 (*)
|         |    +--- org.apache.commons:commons-compress:1.28.0
|         |    |    +--- commons-codec:commons-codec:1.19.0 -> 1.21.0
|         |    |    +--- commons-io:commons-io:2.20.0
|         |    |    \--- org.apache.commons:commons-lang3:3.18.0 -> 3.20.0
|         |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|         +--- org.apache.commons:commons-compress:1.26.1 -> 1.28.0 (*)
|         +--- io.confluent:kafka-schema-serializer:8.3.1
|         |    +--- io.confluent:kafka-schema-registry-client:8.3.1
|         |    |    +--- io.confluent:kafka-avro-types:8.3.1
|         |    |    |    +--- io.confluent:kafka-schema-types:8.3.1
|         |    |    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.2 -> 2.21.5 (*)
|         |    |    |    |    \--- io.confluent:common-utils:8.3.1
|         |    |    |    |         \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    |    |    +--- org.apache.avro:avro:1.12.1 (*)
|         |    |    |    \--- io.confluent:common-utils:8.3.1 (*)
|         |    |    +--- org.apache.kafka:kafka-clients:8.3.1-ccs -> 4.2.1
|         |    |    |    +--- com.github.luben:zstd-jni:1.5.6-10
|         |    |    |    +--- at.yawk.lz4:lz4-java:1.10.1 -> 1.11.1
|         |    |    |    +--- org.xerial.snappy:snappy-java:1.1.10.7
|         |    |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    |    +--- org.apache.avro:avro:1.12.1 (*)
|         |    |    +--- org.apache.commons:commons-compress:1.26.1 -> 1.28.0 (*)
|         |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.2 -> 2.21.5 (*)
|         |    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.21.2 -> 2.21.5
|         |    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (*)
|         |    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.5 (*)
|         |    |    |    \--- com.fasterxml.jackson:jackson-bom:2.21.5 (*)
|         |    |    +--- org.yaml:snakeyaml:2.0 -> 2.6
|         |    |    +--- io.swagger.core.v3:swagger-annotations-jakarta:2.2.42 -> 2.2.52
|         |    |    +--- com.google.guava:guava:32.0.1-jre
|         |    |    |    +--- com.google.guava:failureaccess:1.0.1
|         |    |    |    +--- com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava
|         |    |    |    +--- com.google.code.findbugs:jsr305:3.0.2
|         |    |    |    +--- org.checkerframework:checker-qual:3.33.0 -> 3.55.1
|         |    |    |    +--- com.google.errorprone:error_prone_annotations:2.18.0 -> 2.41.0
|         |    |    |    \--- com.google.j2objc:j2objc-annotations:2.8
|         |    |    +--- org.apache.httpcomponents.client5:httpclient5:5.5 -> 5.6.4
|         |    |    |    +--- org.apache.httpcomponents.core5:httpcore5:5.4.3
|         |    |    |    +--- org.apache.httpcomponents.core5:httpcore5-h2:5.4.3
|         |    |    |    |    \--- org.apache.httpcomponents.core5:httpcore5:5.4.3
|         |    |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    |    \--- io.confluent:common-utils:8.3.1 (*)
|         |    +--- com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.21.2 -> 2.21.5
|         |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.5 (*)
|         |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|         |    |    +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (*)
|         |    |    \--- com.fasterxml.jackson:jackson-bom:2.21.5 (*)
|         |    \--- io.confluent:common-utils:8.3.1 (*)
|         +--- io.confluent:kafka-schema-registry-client:8.3.1 (*)
|         +--- com.google.guava:guava:32.0.1-jre (*)
|         +--- io.confluent:logredactor:1.0.18
|         |    +--- com.google.re2j:re2j:1.6
|         |    +--- io.confluent:logredactor-metrics:1.0.18
|         |    +--- com.eclipsesource.minimal-json:minimal-json:0.9.5
|         |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         \--- io.confluent:common-utils:8.3.1 (*)
+--- no.nav.boot:boot-conditionals:6.0.7
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.4.0
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.4.0
|    |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:2.4.0
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
|    +--- ch.qos.logback:logback-core:1.5.34 -> 1.5.38
|    +--- org.slf4j:slf4j-api:2.0.18
|    \--- org.springframework.boot:spring-boot-autoconfigure:4.1.0 -> 4.1.1
|         \--- org.springframework.boot:spring-boot:4.1.1
|              +--- org.springframework:spring-core:7.0.9
|              |    +--- commons-logging:commons-logging:1.3.5 -> 1.3.6
|              |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|              \--- org.springframework:spring-context:7.0.9
|                   +--- org.springframework:spring-aop:7.0.9
|                   |    +--- org.springframework:spring-beans:7.0.9
|                   |    |    \--- org.springframework:spring-core:7.0.9 (*)
|                   |    \--- org.springframework:spring-core:7.0.9 (*)
|                   +--- org.springframework:spring-beans:7.0.9 (*)
|                   +--- org.springframework:spring-core:7.0.9 (*)
|                   +--- org.springframework:spring-expression:7.0.9
|                   |    \--- org.springframework:spring-core:7.0.9 (*)
|                   \--- io.micrometer:micrometer-observation:1.16.7 -> 1.17.1
|                        +--- org.jspecify:jspecify:1.0.1
|                        \--- io.micrometer:micrometer-commons:1.17.1
|                             \--- org.jspecify:jspecify:1.0.1
+--- io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations -> 2.30.0
|    \--- io.opentelemetry:opentelemetry-api:1.64.0
|         \--- io.opentelemetry:opentelemetry-context:1.64.0
|              \--- io.opentelemetry:opentelemetry-common:1.64.0
+--- io.opentelemetry.instrumentation:opentelemetry-logback-mdc-1.0:2.30.0-alpha
|    +--- io.opentelemetry.instrumentation:opentelemetry-instrumentation-api:2.30.0
|    |    +--- io.opentelemetry:opentelemetry-api-incubator:1.64.0-alpha
|    |    |    \--- io.opentelemetry:opentelemetry-api:1.64.0 (*)
|    |    +--- io.opentelemetry.semconv:opentelemetry-semconv:1.43.0
|    |    \--- io.opentelemetry:opentelemetry-api:1.64.0 (*)
|    +--- io.opentelemetry.instrumentation:opentelemetry-instrumentation-api-incubator:2.30.0-alpha
|    |    +--- io.opentelemetry.semconv:opentelemetry-semconv:1.43.0
|    |    +--- io.opentelemetry.instrumentation:opentelemetry-instrumentation-api:2.30.0 (*)
|    |    \--- io.opentelemetry:opentelemetry-api-incubator:1.64.0-alpha (*)
|    \--- io.opentelemetry:opentelemetry-api:1.64.0 (*)
+--- io.micrometer:micrometer-registry-prometheus -> 1.17.1
|    +--- org.jspecify:jspecify:1.0.1
|    +--- io.micrometer:micrometer-core:1.17.1
|    |    +--- org.jspecify:jspecify:1.0.1
|    |    +--- io.micrometer:micrometer-commons:1.17.1 (*)
|    |    +--- io.micrometer:micrometer-observation:1.17.1 (*)
|    |    \--- org.hdrhistogram:HdrHistogram:2.2.2
|    +--- io.prometheus:prometheus-metrics-core:1.7.0
|    |    +--- io.prometheus:prometheus-metrics-model:1.7.0
|    |    |    \--- io.prometheus:prometheus-metrics-config:1.7.0
|    |    \--- io.prometheus:prometheus-metrics-config:1.7.0
|    +--- io.prometheus:prometheus-metrics-tracer-common:1.7.0
|    \--- io.prometheus:prometheus-metrics-exposition-formats:1.7.0
|         \--- io.prometheus:prometheus-metrics-exposition-textformats:1.7.0
|              +--- io.prometheus:prometheus-metrics-model:1.7.0 (*)
|              \--- io.prometheus:prometheus-metrics-config:1.7.0
+--- com.slack.api:slack-api-client-kotlin-extension:1.49.0
|    +--- com.slack.api:slack-api-model-kotlin-extension:1.49.0
|    |    +--- com.slack.api:slack-api-model:1.49.0
|    |    |    \--- com.google.code.gson:gson:2.12.1 -> 2.13.2
|    |    |         \--- com.google.errorprone:error_prone_annotations:2.41.0
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.24 -> 2.4.0 (*)
|    +--- com.slack.api:slack-api-client:1.49.0
|    |    +--- com.slack.api:slack-api-model:1.49.0 (*)
|    |    +--- com.squareup.okhttp3:okhttp:4.12.0
|    |    |    +--- com.squareup.okio:okio:3.6.0
|    |    |    |    \--- com.squareup.okio:okio-jvm:3.6.0
|    |    |    |         +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.10 -> 2.4.0 (*)
|    |    |    |         \--- org.jetbrains.kotlin:kotlin-stdlib-common:1.9.10 -> 2.4.0
|    |    |    |              \--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
|    |    |    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.21 -> 2.4.0 (*)
|    |    +--- com.google.code.gson:gson:2.12.1 -> 2.13.2 (*)
|    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|    \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.24 -> 2.4.0 (*)
+--- org.apache.commons:commons-pool2:2.13.1
+--- io.confluent:kafka-avro-serializer:8.3.1 (*)
+--- org.flywaydb:flyway-database-postgresql -> 12.4.0
|    \--- org.flywaydb:flyway-core:12.4.0
|         \--- tools.jackson.core:jackson-databind:3.1.1 -> 3.1.5
|              +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|              +--- tools.jackson.core:jackson-core:3.1.5
|              |    \--- tools.jackson:jackson-bom:3.1.5
|              |         +--- com.fasterxml.jackson.core:jackson-annotations:2.21 (c)
|              |         +--- tools.jackson.core:jackson-core:3.1.5 (c)
|              |         +--- tools.jackson.core:jackson-databind:3.1.5 (c)
|              |         \--- tools.jackson.module:jackson-module-kotlin:3.1.5 (c)
|              \--- tools.jackson:jackson-bom:3.1.5 (*)
+--- org.hibernate.orm:hibernate-micrometer -> 7.4.5.Final
|    +--- org.jboss.logging:jboss-logging:3.6.1.Final -> 3.6.3.Final
|    +--- org.hibernate.orm:hibernate-core:7.4.5.Final
|    |    +--- org.jboss.logging:jboss-logging:3.6.1.Final -> 3.6.3.Final
|    |    +--- org.hibernate.models:hibernate-models:1.1.1
|    |    |    \--- org.jboss.logging:jboss-logging:3.6.3.Final
|    |    +--- net.bytebuddy:byte-buddy:1.18.8 -> 1.18.11
|    |    +--- jakarta.xml.bind:jakarta.xml.bind-api:4.0.4 -> 4.0.5
|    |    |    \--- jakarta.activation:jakarta.activation-api:2.1.4
|    |    +--- org.glassfish.jaxb:jaxb-runtime:4.0.7 -> 4.0.9
|    |    |    \--- org.glassfish.jaxb:jaxb-core:4.0.9
|    |    |         +--- jakarta.xml.bind:jakarta.xml.bind-api:4.0.5 (*)
|    |    |         +--- jakarta.activation:jakarta.activation-api:2.1.4
|    |    |         +--- org.eclipse.angus:angus-activation:2.0.3
|    |    |         |    \--- jakarta.activation:jakarta.activation-api:2.1.4
|    |    |         +--- org.glassfish.jaxb:txw2:4.0.9
|    |    |         \--- com.sun.istack:istack-commons-runtime:4.1.2
|    |    +--- jakarta.inject:jakarta.inject-api:2.0.1
|    |    +--- org.antlr:antlr4-runtime:4.13.2
|    |    +--- org.hibernate.orm:hibernate-platform:7.4.5.Final
|    |    |    +--- org.antlr:antlr4-runtime:4.13.2 (c)
|    |    |    +--- org.jboss.logging:jboss-logging:3.6.1.Final -> 3.6.3.Final (c)
|    |    |    +--- net.bytebuddy:byte-buddy:1.18.8 -> 1.18.11 (c)
|    |    |    +--- org.glassfish.jaxb:jaxb-runtime:4.0.7 -> 4.0.9 (c)
|    |    |    +--- jakarta.xml.bind:jakarta.xml.bind-api:4.0.4 -> 4.0.5 (c)
|    |    |    +--- jakarta.inject:jakarta.inject-api:2.0.1 (c)
|    |    |    +--- io.micrometer:micrometer-core:1.16.0 -> 1.17.1 (c)
|    |    |    +--- org.hibernate.orm:hibernate-core:7.4.5.Final (c)
|    |    |    +--- org.hibernate.orm:hibernate-micrometer:7.4.5.Final (c)
|    |    |    +--- org.hibernate.models:hibernate-models:1.1.1 (c)
|    |    |    +--- jakarta.persistence:jakarta.persistence-api:3.2.0 (c)
|    |    |    +--- jakarta.transaction:jakarta.transaction-api:2.0.1 (c)
|    |    |    \--- com.zaxxer:HikariCP:7.0.2 (c)
|    |    +--- jakarta.persistence:jakarta.persistence-api:3.2.0
|    |    \--- jakarta.transaction:jakarta.transaction-api:2.0.1
|    +--- io.micrometer:micrometer-core:1.16.0 -> 1.17.1 (*)
|    \--- org.hibernate.orm:hibernate-platform:7.4.5.Final (*)
+--- tools.jackson.module:jackson-module-kotlin -> 3.1.5
|    +--- tools.jackson.core:jackson-databind:3.1.5 (*)
|    +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|    +--- org.jetbrains.kotlin:kotlin-reflect:2.1.21 -> 2.4.0 (*)
|    \--- tools.jackson:jackson-bom:3.1.5 (*)
+--- org.zalando:logbook-spring-boot-starter:4.1.0
|    +--- org.zalando:logbook-spring-boot-autoconfigure:4.1.0
|    |    +--- org.zalando:logbook-core:4.1.0
|    |    |    +--- org.zalando:logbook-api:4.1.0
|    |    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    |    +--- org.zalando:faux-pas:0.9.0
|    |    |    |    |    +--- com.google.code.findbugs:jsr305:3.0.2
|    |    |    |    |    \--- org.slf4j:slf4j-api:1.7.30 -> 2.0.18
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    |    +--- org.zalando:logbook-common:4.1.0
|    |    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.zalando:logbook-json:4.1.0
|    |    |    +--- org.zalando:logbook-api:4.1.0 (*)
|    |    |    +--- org.zalando:logbook-common:4.1.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.22 -> 2.21
|    |    |    +--- com.jayway.jsonpath:json-path:3.0.0 -> 2.10.0
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.zalando:logbook-json-jackson2:4.1.0
|    |    |    +--- org.zalando:logbook-api:4.1.0 (*)
|    |    |    +--- org.zalando:logbook-common:4.1.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.22 -> 2.21
|    |    |    +--- com.jayway.jsonpath:json-path:3.0.0 -> 2.10.0 (*)
|    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.zalando:logbook-spring:4.1.0
|    |    |    +--- org.zalando:logbook-core:4.1.0 (*)
|    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.zalando:logbook-servlet:4.1.0
|    |    |    +--- org.zalando:logbook-api:4.1.0 (*)
|    |    |    +--- org.zalando:logbook-core:4.1.0 (*)
|    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    +--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    +--- org.zalando:logbook-spring-boot-ecs-autoconfigure:4.1.0
|    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    +--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    +--- org.apiguardian:apiguardian-api:1.1.2
|    +--- org.zalando:faux-pas:0.9.0 (*)
|    \--- org.slf4j:slf4j-api:2.0.18
+--- net.logstash.logback:logstash-logback-encoder:9.0
|    \--- tools.jackson.core:jackson-databind:3.0.1 -> 3.1.5 (*)
+--- org.postgresql:postgresql -> 42.7.13
|    \--- org.checkerframework:checker-qual:3.55.1
+--- org.springframework.boot:spring-boot-starter-actuator -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter-logging:4.1.1
|    |    |    +--- ch.qos.logback:logback-classic:1.5.38
|    |    |    |    +--- ch.qos.logback:logback-core:1.5.38
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    +--- org.apache.logging.log4j:log4j-to-slf4j:2.25.5
|    |    |    |    +--- org.apache.logging.log4j:log4j-api:2.25.5
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    \--- org.slf4j:jul-to-slf4j:2.0.18
|    |    |         \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.springframework.boot:spring-boot-autoconfigure:4.1.1 (*)
|    |    +--- jakarta.annotation:jakarta.annotation-api:3.0.0
|    |    \--- org.yaml:snakeyaml:2.6
|    +--- org.springframework.boot:spring-boot-starter-micrometer-metrics:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-micrometer-metrics:4.1.1
|    |         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |         +--- org.springframework.boot:spring-boot-micrometer-observation:4.1.1
|    |         |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |         |    \--- io.micrometer:micrometer-observation:1.17.1 (*)
|    |         \--- io.micrometer:micrometer-core:1.17.1 (*)
|    +--- org.springframework.boot:spring-boot-actuator-autoconfigure:4.1.1
|    |    +--- org.springframework.boot:spring-boot-autoconfigure:4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-actuator:4.1.1
|    |         \--- org.springframework.boot:spring-boot:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-health:4.1.1
|    |    \--- org.springframework.boot:spring-boot:4.1.1 (*)
|    +--- io.micrometer:micrometer-observation:1.17.1 (*)
|    \--- io.micrometer:micrometer-jakarta9:1.17.1
|         +--- org.jspecify:jspecify:1.0.1
|         +--- io.micrometer:micrometer-core:1.17.1 (*)
|         +--- io.micrometer:micrometer-commons:1.17.1 (*)
|         \--- io.micrometer:micrometer-observation:1.17.1 (*)
+--- org.springframework.boot:spring-boot-starter-cache -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-cache:4.1.1
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         \--- org.springframework:spring-context-support:7.0.9
|              +--- org.springframework:spring-beans:7.0.9 (*)
|              +--- org.springframework:spring-context:7.0.9 (*)
|              \--- org.springframework:spring-core:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-data-jpa -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jdbc:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-jdbc:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    +--- org.springframework.boot:spring-boot-sql:4.1.1
|    |    |    |    \--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    +--- org.springframework.boot:spring-boot-transaction:4.1.1
|    |    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    |    +--- org.springframework.boot:spring-boot-persistence:4.1.1
|    |    |    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    |    |    \--- org.springframework:spring-tx:7.0.9
|    |    |    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    |    |         \--- org.springframework:spring-core:7.0.9 (*)
|    |    |    |    \--- org.springframework:spring-tx:7.0.9 (*)
|    |    |    \--- org.springframework:spring-jdbc:7.0.9
|    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |    |         \--- org.springframework:spring-tx:7.0.9 (*)
|    |    \--- com.zaxxer:HikariCP:7.0.2
|    |         \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    +--- org.springframework.boot:spring-boot-data-jpa:4.1.1
|    |    +--- org.springframework.boot:spring-boot-data-commons:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    +--- org.springframework.boot:spring-boot-persistence:4.1.1 (*)
|    |    |    \--- org.springframework.data:spring-data-commons:4.1.1
|    |    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |         \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.springframework.boot:spring-boot-hibernate:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot-jpa:4.1.1
|    |    |    |    +--- org.springframework.boot:spring-boot-jdbc:4.1.1 (*)
|    |    |    |    +--- org.springframework.boot:spring-boot-transaction:4.1.1 (*)
|    |    |    |    +--- jakarta.persistence:jakarta.persistence-api:3.2.0
|    |    |    |    \--- org.springframework:spring-orm:7.0.9
|    |    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |    |    |         +--- org.springframework:spring-jdbc:7.0.9 (*)
|    |    |    |         \--- org.springframework:spring-tx:7.0.9 (*)
|    |    |    +--- org.hibernate.orm:hibernate-core:7.4.5.Final (*)
|    |    |    \--- org.springframework:spring-orm:7.0.9 (*)
|    |    +--- org.springframework.data:spring-data-jpa:4.1.1
|    |    |    +--- org.springframework.data:spring-data-commons:4.1.1 (*)
|    |    |    +--- org.springframework:spring-orm:7.0.9 (*)
|    |    |    +--- org.springframework:spring-context:7.0.9 (*)
|    |    |    +--- org.springframework:spring-aop:7.0.9 (*)
|    |    |    +--- org.springframework:spring-tx:7.0.9 (*)
|    |    |    +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    +--- org.springframework:spring-core:7.0.9 (*)
|    |    |    +--- org.antlr:antlr4-runtime:4.13.2
|    |    |    +--- jakarta.annotation:jakarta.annotation-api:2.0.0 -> 3.0.0
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    \--- org.springframework:spring-aspects:7.0.9
|    |         \--- org.aspectj:aspectjweaver:1.9.25 -> 1.9.25.1
|    \--- org.springframework.boot:spring-boot-jdbc:4.1.1 (*)
+--- org.springframework.boot:spring-boot-starter-data-redis -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-data-redis:4.1.1
|    |    +--- org.springframework.boot:spring-boot-netty:4.1.1
|    |    |    +--- io.netty:netty-common:4.2.17.Final
|    |    |    \--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-data-commons:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-transaction:4.1.1 (*)
|    |    +--- io.lettuce:lettuce-core:7.5.2.RELEASE
|    |    |    +--- redis.clients.authentication:redis-authx-core:0.1.1-beta2
|    |    |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|    |    |    +--- io.netty:netty-common:4.2.13.Final -> 4.2.17.Final
|    |    |    +--- io.netty:netty-handler:4.2.13.Final -> 4.2.17.Final
|    |    |    |    +--- io.netty:netty-common:4.2.17.Final
|    |    |    |    +--- io.netty:netty-resolver:4.2.17.Final
|    |    |    |    |    \--- io.netty:netty-common:4.2.17.Final
|    |    |    |    +--- io.netty:netty-buffer:4.2.17.Final
|    |    |    |    |    \--- io.netty:netty-common:4.2.17.Final
|    |    |    |    +--- io.netty:netty-transport:4.2.17.Final
|    |    |    |    |    +--- io.netty:netty-common:4.2.17.Final
|    |    |    |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|    |    |    |    |    \--- io.netty:netty-resolver:4.2.17.Final (*)
|    |    |    |    +--- io.netty:netty-transport-native-unix-common:4.2.17.Final
|    |    |    |    |    +--- io.netty:netty-common:4.2.17.Final
|    |    |    |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|    |    |    |    |    \--- io.netty:netty-transport:4.2.17.Final (*)
|    |    |    |    \--- io.netty:netty-codec-base:4.2.17.Final
|    |    |    |         +--- io.netty:netty-common:4.2.17.Final
|    |    |    |         +--- io.netty:netty-buffer:4.2.17.Final (*)
|    |    |    |         \--- io.netty:netty-transport:4.2.17.Final (*)
|    |    |    +--- io.netty:netty-transport:4.2.13.Final -> 4.2.17.Final (*)
|    |    |    +--- io.projectreactor:reactor-core:3.6.6 -> 3.8.7
|    |    |    |    +--- org.reactivestreams:reactive-streams:1.0.4
|    |    |    |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|    |    |    \--- io.netty:netty-resolver-dns:4.2.13.Final -> 4.2.17.Final
|    |    |         +--- io.netty:netty-common:4.2.17.Final
|    |    |         +--- io.netty:netty-buffer:4.2.17.Final (*)
|    |    |         +--- io.netty:netty-resolver:4.2.17.Final (*)
|    |    |         +--- io.netty:netty-transport:4.2.17.Final (*)
|    |    |         +--- io.netty:netty-codec-base:4.2.17.Final (*)
|    |    |         +--- io.netty:netty-codec-dns:4.2.17.Final
|    |    |         |    +--- io.netty:netty-common:4.2.17.Final
|    |    |         |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|    |    |         |    +--- io.netty:netty-transport:4.2.17.Final (*)
|    |    |         |    \--- io.netty:netty-codec-base:4.2.17.Final (*)
|    |    |         \--- io.netty:netty-handler:4.2.17.Final (*)
|    |    \--- org.springframework.data:spring-data-redis:4.1.1
|    |         +--- org.springframework.data:spring-data-keyvalue:4.1.1
|    |         |    +--- org.springframework.data:spring-data-commons:4.1.1 (*)
|    |         |    +--- org.springframework:spring-context:7.0.9 (*)
|    |         |    +--- org.springframework:spring-tx:7.0.9 (*)
|    |         |    \--- org.slf4j:slf4j-api:2.0.18
|    |         +--- org.springframework:spring-tx:7.0.9 (*)
|    |         +--- org.springframework:spring-oxm:7.0.9
|    |         |    +--- jakarta.xml.bind:jakarta.xml.bind-api:3.0.1 -> 4.0.5 (*)
|    |         |    +--- org.springframework:spring-beans:7.0.9 (*)
|    |         |    \--- org.springframework:spring-core:7.0.9 (*)
|    |         +--- org.springframework:spring-aop:7.0.9 (*)
|    |         +--- org.springframework:spring-context-support:7.0.9 (*)
|    |         \--- org.slf4j:slf4j-api:2.0.18
|    \--- org.springframework:spring-messaging:7.0.9
|         +--- org.springframework:spring-beans:7.0.9 (*)
|         \--- org.springframework:spring-core:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-flyway -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jdbc:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-flyway:4.1.1
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-jdbc:4.1.1 (*)
|    |    \--- org.flywaydb:flyway-core:12.4.0 (*)
|    \--- org.springframework.boot:spring-boot-jdbc:4.1.1 (*)
+--- org.springframework.boot:spring-boot-starter-graphql -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jackson:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-jackson:4.1.1
|    |         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |         \--- tools.jackson.core:jackson-databind:3.1.5 (*)
|    +--- org.springframework.boot:spring-boot-reactor:4.1.1
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    \--- io.projectreactor:reactor-core:3.8.7 (*)
|    \--- org.springframework.boot:spring-boot-graphql:4.1.1
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         \--- org.springframework.graphql:spring-graphql:2.0.5
|              +--- io.micrometer:context-propagation:1.2.1
|              |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|              +--- com.graphql-java:graphql-java:25.0
|              |    +--- com.graphql-java:java-dataloader:6.0.0
|              |    |    +--- org.reactivestreams:reactive-streams:1.0.3 -> 1.0.4
|              |    |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|              |    +--- org.reactivestreams:reactive-streams:1.0.3 -> 1.0.4
|              |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|              +--- io.projectreactor:reactor-core:3.8.7 (*)
|              \--- org.springframework:spring-context:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-jetty -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jetty-runtime:4.1.1
|    |    +--- org.springframework.boot:spring-boot-jetty:4.1.1
|    |    |    +--- org.eclipse.jetty.compression:jetty-compression-server:12.1.12
|    |    |    |    +--- org.eclipse.jetty:jetty-server:12.1.12
|    |    |    |    |    +--- org.eclipse.jetty:jetty-http:12.1.12
|    |    |    |    |    |    +--- org.eclipse.jetty:jetty-io:12.1.12
|    |    |    |    |    |    |    +--- org.eclipse.jetty:jetty-util:12.1.12
|    |    |    |    |    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    |    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    |    |    |    +--- org.eclipse.jetty:jetty-util:12.1.12 (*)
|    |    |    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    |    |    +--- org.eclipse.jetty:jetty-io:12.1.12 (*)
|    |    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    |    \--- org.eclipse.jetty.compression:jetty-compression-common:12.1.12
|    |    |    |         +--- org.eclipse.jetty:jetty-http:12.1.12 (*)
|    |    |    |         +--- org.eclipse.jetty:jetty-io:12.1.12 (*)
|    |    |    |         \--- org.eclipse.jetty:jetty-util:12.1.12 (*)
|    |    |    +--- org.eclipse.jetty.compression:jetty-compression-gzip:12.1.12
|    |    |    |    \--- org.eclipse.jetty.compression:jetty-compression-common:12.1.12 (*)
|    |    |    +--- org.springframework.boot:spring-boot-web-server:4.1.1
|    |    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    |    \--- org.springframework:spring-web:7.0.9
|    |    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |    |    |         \--- io.micrometer:micrometer-observation:1.16.7 -> 1.17.1 (*)
|    |    |    \--- org.eclipse.jetty.ee11:jetty-ee11-webapp:12.1.12
|    |    |         +--- org.eclipse.jetty:jetty-session:12.1.12
|    |    |         |    +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |         |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         +--- org.eclipse.jetty:jetty-xml:12.1.12
|    |    |         |    +--- org.eclipse.jetty:jetty-util:12.1.12 (*)
|    |    |         |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         +--- org.eclipse.jetty.ee:jetty-ee-webapp:12.1.12
|    |    |         |    +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |         |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         +--- org.eclipse.jetty.ee11:jetty-ee11-servlet:12.1.12
|    |    |         |    +--- jakarta.servlet:jakarta.servlet-api:6.1.0
|    |    |         |    +--- org.eclipse.jetty:jetty-security:12.1.12
|    |    |         |    |    +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |         |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         |    +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |         |    +--- org.eclipse.jetty:jetty-session:12.1.12 (*)
|    |    |         |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    +--- org.springframework.boot:spring-boot-web-server:4.1.1 (*)
|    |    +--- jakarta.servlet:jakarta.servlet-api:6.1.0
|    |    +--- jakarta.websocket:jakarta.websocket-api:2.2.0
|    |    +--- jakarta.websocket:jakarta.websocket-client-api:2.2.0
|    |    +--- org.apache.tomcat.embed:tomcat-embed-el:11.0.24
|    |    +--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-jakarta-server:12.1.12
|    |    |    +--- jakarta.websocket:jakarta.websocket-api:2.2.0
|    |    |    +--- org.eclipse.jetty.ee11:jetty-ee11-annotations:12.1.12
|    |    |    |    +--- jakarta.annotation:jakarta.annotation-api:3.0.0
|    |    |    |    +--- jakarta.servlet:jakarta.servlet-api:6.1.0
|    |    |    |    +--- org.eclipse.jetty:jetty-annotations:12.1.12
|    |    |    |    |    +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |    |    |    +--- org.ow2.asm:asm:9.10.1
|    |    |    |    |    \--- org.ow2.asm:asm-commons:9.10.1
|    |    |    |    |         +--- org.ow2.asm:asm:9.10.1
|    |    |    |    |         \--- org.ow2.asm:asm-tree:9.10.1
|    |    |    |    |              \--- org.ow2.asm:asm:9.10.1
|    |    |    |    +--- org.eclipse.jetty.ee11:jetty-ee11-plus:12.1.12
|    |    |    |    |    +--- jakarta.enterprise:jakarta.enterprise.cdi-api:4.1.0
|    |    |    |    |    |    +--- jakarta.enterprise:jakarta.enterprise.lang-model:4.1.0
|    |    |    |    |    |    +--- jakarta.annotation:jakarta.annotation-api:3.0.0
|    |    |    |    |    |    +--- jakarta.interceptor:jakarta.interceptor-api:2.2.0
|    |    |    |    |    |    |    \--- jakarta.annotation:jakarta.annotation-api:3.0.0
|    |    |    |    |    |    \--- jakarta.inject:jakarta.inject-api:2.0.1
|    |    |    |    |    +--- jakarta.enterprise:jakarta.enterprise.lang-model:4.1.0
|    |    |    |    |    +--- jakarta.interceptor:jakarta.interceptor-api:2.2.0 (*)
|    |    |    |    |    +--- jakarta.transaction:jakarta.transaction-api:2.0.1
|    |    |    |    |    +--- org.eclipse.jetty:jetty-plus:12.1.12
|    |    |    |    |    |    +--- org.eclipse.jetty:jetty-security:12.1.12 (*)
|    |    |    |    |    |    \--- org.eclipse.jetty:jetty-util:12.1.12 (*)
|    |    |    |    |    \--- org.eclipse.jetty.ee11:jetty-ee11-webapp:12.1.12 (*)
|    |    |    |    +--- org.eclipse.jetty.ee11:jetty-ee11-webapp:12.1.12 (*)
|    |    |    |    +--- org.ow2.asm:asm:9.10.1
|    |    |    |    \--- org.ow2.asm:asm-commons:9.10.1 (*)
|    |    |    +--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-jakarta-client:12.1.12
|    |    |    |    +--- jakarta.websocket:jakarta.websocket-api:2.2.0
|    |    |    |    +--- jakarta.websocket:jakarta.websocket-client-api:2.2.0
|    |    |    |    +--- org.eclipse.jetty:jetty-client:12.1.12
|    |    |    |    |    +--- org.eclipse.jetty:jetty-alpn-client:12.1.12
|    |    |    |    |    |    \--- org.eclipse.jetty:jetty-io:12.1.12 (*)
|    |    |    |    |    +--- org.eclipse.jetty:jetty-http:12.1.12 (*)
|    |    |    |    |    +--- org.eclipse.jetty:jetty-io:12.1.12 (*)
|    |    |    |    |    \--- org.eclipse.jetty.compression:jetty-compression-gzip:12.1.12 (*)
|    |    |    |    +--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-jakarta-common:12.1.12
|    |    |    |    |    +--- jakarta.websocket:jakarta.websocket-api:2.2.0
|    |    |    |    |    +--- jakarta.websocket:jakarta.websocket-client-api:2.2.0
|    |    |    |    |    \--- org.eclipse.jetty.websocket:jetty-websocket-core-client:12.1.12
|    |    |    |    |         +--- org.eclipse.jetty:jetty-client:12.1.12 (*)
|    |    |    |    |         \--- org.eclipse.jetty.websocket:jetty-websocket-core-common:12.1.12
|    |    |    |    |              +--- org.eclipse.jetty:jetty-http:12.1.12 (*)
|    |    |    |    |              \--- org.eclipse.jetty:jetty-io:12.1.12 (*)
|    |    |    |    \--- org.eclipse.jetty.websocket:jetty-websocket-core-client:12.1.12 (*)
|    |    |    \--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-servlet:12.1.12
|    |    |         +--- org.eclipse.jetty.ee11:jetty-ee11-servlet:12.1.12 (*)
|    |    |         \--- org.eclipse.jetty.websocket:jetty-websocket-core-server:12.1.12
|    |    |              +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |              \--- org.eclipse.jetty.websocket:jetty-websocket-core-common:12.1.12 (*)
|    |    \--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-jetty-server:12.1.12
|    |         +--- jakarta.servlet:jakarta.servlet-api:6.1.0
|    |         +--- org.eclipse.jetty.ee11:jetty-ee11-annotations:12.1.12 (*)
|    |         +--- org.eclipse.jetty.ee11:jetty-ee11-servlet:12.1.12 (*)
|    |         +--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-servlet:12.1.12 (*)
|    |         +--- org.eclipse.jetty.websocket:jetty-websocket-jetty-api:12.1.12
|    |         +--- org.eclipse.jetty.websocket:jetty-websocket-jetty-common:12.1.12
|    |         |    +--- org.eclipse.jetty.websocket:jetty-websocket-core-common:12.1.12 (*)
|    |         |    \--- org.eclipse.jetty.websocket:jetty-websocket-jetty-api:12.1.12
|    |         \--- org.eclipse.jetty.websocket:jetty-websocket-jetty-server:12.1.12
|    |              +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |              +--- org.eclipse.jetty.websocket:jetty-websocket-core-server:12.1.12 (*)
|    |              \--- org.eclipse.jetty.websocket:jetty-websocket-jetty-common:12.1.12 (*)
|    +--- org.springframework.boot:spring-boot-jetty:4.1.1 (*)
|    +--- org.slf4j:slf4j-api:2.0.18
|    \--- jakarta.annotation:jakarta.annotation-api:3.0.0
+--- org.springframework.boot:spring-boot-starter-kafka -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-kafka:4.1.1
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         +--- org.springframework.boot:spring-boot-transaction:4.1.1 (*)
|         \--- org.springframework.kafka:spring-kafka:4.1.1
|              +--- org.springframework:spring-context:7.0.9 (*)
|              +--- org.springframework:spring-messaging:7.0.9 (*)
|              +--- org.springframework:spring-tx:7.0.9 (*)
|              +--- org.apache.kafka:kafka-clients:4.2.1 (*)
|              \--- io.micrometer:micrometer-observation:1.17.1 (*)
+--- org.springframework.boot:spring-boot-starter-oauth2-client -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-security:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-security:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    +--- org.springframework.security:spring-security-config:7.1.1
|    |    |    |    +--- org.springframework.security:spring-security-core:7.1.1
|    |    |    |    |    +--- org.springframework.security:spring-security-crypto:7.1.1
|    |    |    |    |    +--- org.springframework:spring-aop:7.0.9 (*)
|    |    |    |    |    +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    |    |    +--- org.springframework:spring-context:7.0.9 (*)
|    |    |    |    |    +--- org.springframework:spring-core:7.0.9 (*)
|    |    |    |    |    +--- org.springframework:spring-expression:7.0.9 (*)
|    |    |    |    |    \--- io.micrometer:micrometer-observation:1.17.1 (*)
|    |    |    |    +--- org.springframework:spring-aop:7.0.9 (*)
|    |    |    |    +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    |    +--- org.springframework:spring-context:7.0.9 (*)
|    |    |    |    \--- org.springframework:spring-core:7.0.9 (*)
|    |    |    \--- org.springframework.security:spring-security-web:7.1.1
|    |    |         +--- org.springframework.security:spring-security-core:7.1.1 (*)
|    |    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |    |         +--- org.springframework:spring-aop:7.0.9 (*)
|    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |         +--- org.springframework:spring-context:7.0.9 (*)
|    |    |         +--- org.springframework:spring-expression:7.0.9 (*)
|    |    |         \--- org.springframework:spring-web:7.0.9 (*)
|    |    \--- org.springframework:spring-aop:7.0.9 (*)
|    +--- org.springframework.boot:spring-boot-security-oauth2-client:4.1.1
|    |    +--- org.springframework.boot:spring-boot-security:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    \--- org.springframework.security:spring-security-oauth2-client:7.1.1
|    |         +--- org.springframework.security:spring-security-core:7.1.1 (*)
|    |         +--- org.springframework.security:spring-security-oauth2-core:7.1.1
|    |         |    +--- org.springframework.security:spring-security-core:7.1.1 (*)
|    |         |    +--- org.springframework:spring-core:7.0.9 (*)
|    |         |    \--- org.springframework:spring-web:7.0.9 (*)
|    |         +--- org.springframework.security:spring-security-web:7.1.1 (*)
|    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |         \--- com.nimbusds:oauth2-oidc-sdk:11.38.2
|    |              +--- com.github.stephenc.jcip:jcip-annotations:1.0-1
|    |              +--- com.nimbusds:content-type:2.3
|    |              +--- net.minidev:json-smart:2.6.0
|    |              |    \--- net.minidev:accessors-smart:2.6.0
|    |              |         \--- org.ow2.asm:asm:9.7.1 -> 9.10.1
|    |              +--- com.nimbusds:lang-tag:1.7
|    |              \--- com.nimbusds:nimbus-jose-jwt:10.9.1
|    \--- org.springframework.security:spring-security-oauth2-jose:7.1.1
|         +--- org.springframework.security:spring-security-core:7.1.1 (*)
|         +--- org.springframework.security:spring-security-oauth2-core:7.1.1 (*)
|         +--- org.springframework:spring-core:7.0.9 (*)
|         \--- com.nimbusds:nimbus-jose-jwt:10.9.1
+--- org.springframework.boot:spring-boot-starter-oauth2-resource-server -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-security:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-security-oauth2-resource-server:4.1.1
|         +--- org.springframework.boot:spring-boot-security:4.1.1 (*)
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         +--- org.springframework.security:spring-security-oauth2-jose:7.1.1 (*)
|         \--- org.springframework.security:spring-security-oauth2-resource-server:7.1.1
|              +--- org.springframework.security:spring-security-core:7.1.1 (*)
|              +--- org.springframework.security:spring-security-oauth2-core:7.1.1 (*)
|              +--- org.springframework.security:spring-security-web:7.1.1 (*)
|              \--- org.springframework:spring-core:7.0.9 (*)
+--- org.springframework.boot:spring-boot-restclient -> 4.1.1
|    +--- org.springframework.boot:spring-boot-http-converter:4.1.1
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    \--- org.springframework:spring-web:7.0.9 (*)
|    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-http-client:4.1.1
|         +--- org.springframework.boot:spring-boot-http-converter:4.1.1 (*)
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         \--- org.springframework:spring-web:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-validation -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-validation:4.1.1
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         +--- org.apache.tomcat.embed:tomcat-embed-el:11.0.24
|         \--- org.hibernate.validator:hibernate-validator:9.1.3.Final
|              +--- jakarta.validation:jakarta.validation-api:3.1.1
|              +--- org.jboss.logging:jboss-logging:3.6.3.Final
|              \--- com.fasterxml:classmate:1.7.1 -> 1.7.3
+--- org.springframework.boot:spring-boot-starter-web -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter-jackson:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-http-converter:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-webmvc:4.1.1
|         +--- org.springframework.boot:spring-boot-http-converter:4.1.1 (*)
|         +--- org.springframework.boot:spring-boot-servlet:4.1.1
|         |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         |    \--- org.springframework:spring-web:7.0.9 (*)
|         +--- org.springframework:spring-web:7.0.9 (*)
|         \--- org.springframework:spring-webmvc:7.0.9
|              +--- org.springframework:spring-aop:7.0.9 (*)
|              +--- org.springframework:spring-beans:7.0.9 (*)
|              +--- org.springframework:spring-context:7.0.9 (*)
|              +--- org.springframework:spring-core:7.0.9 (*)
|              +--- org.springframework:spring-expression:7.0.9 (*)
|              \--- org.springframework:spring-web:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-webclient -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jackson:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-reactor:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-webclient:4.1.1
|    |    +--- org.springframework.boot:spring-boot-http-codec:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    \--- org.springframework:spring-web:7.0.9 (*)
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-http-client:4.1.1 (*)
|    |    \--- org.springframework:spring-webflux:7.0.9
|    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |         +--- org.springframework:spring-web:7.0.9 (*)
|    |         \--- io.projectreactor:reactor-core:3.8.7 (*)
|    \--- io.projectreactor.netty:reactor-netty-http:1.3.7
|         +--- io.netty:netty-codec-http:4.2.17.Final
|         |    +--- io.netty:netty-common:4.2.17.Final
|         |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-compression:4.2.17.Final
|         |    |    +--- io.netty:netty-common:4.2.17.Final
|         |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    |    \--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    \--- io.netty:netty-handler:4.2.17.Final (*)
|         +--- io.netty:netty-codec-http2:4.2.17.Final
|         |    +--- io.netty:netty-common:4.2.17.Final
|         |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    +--- io.netty:netty-handler:4.2.17.Final (*)
|         |    \--- io.netty:netty-codec-http:4.2.17.Final (*)
|         +--- io.netty:netty-codec-http3:4.2.17.Final
|         |    +--- io.netty:netty-common:4.2.17.Final
|         |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-http:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-compression:4.2.17.Final (*)
|         |    +--- io.netty:netty-handler:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport-native-unix-common:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    +--- io.netty:netty-resolver:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-classes-quic:4.2.17.Final
|         |    |    +--- io.netty:netty-common:4.2.17.Final
|         |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    |    \--- io.netty:netty-handler:4.2.17.Final (*)
|         |    \--- io.netty:netty-codec-native-quic:4.2.17.Final
|         |         \--- io.netty:netty-codec-classes-quic:4.2.17.Final (*)
|         +--- io.netty:netty-resolver-dns:4.2.17.Final (*)
|         +--- io.netty:netty-resolver-dns-native-macos:4.2.17.Final
|         |    \--- io.netty:netty-resolver-dns-classes-macos:4.2.17.Final
|         |         +--- io.netty:netty-common:4.2.17.Final
|         |         +--- io.netty:netty-resolver-dns:4.2.17.Final (*)
|         |         \--- io.netty:netty-transport-native-unix-common:4.2.17.Final (*)
|         +--- io.netty:netty-transport-native-epoll:4.2.17.Final
|         |    +--- io.netty:netty-common:4.2.17.Final
|         |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport-native-unix-common:4.2.17.Final (*)
|         |    \--- io.netty:netty-transport-classes-epoll:4.2.17.Final
|         |         +--- io.netty:netty-common:4.2.17.Final
|         |         +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |         +--- io.netty:netty-transport:4.2.17.Final (*)
|         |         \--- io.netty:netty-transport-native-unix-common:4.2.17.Final (*)
|         +--- io.projectreactor.netty:reactor-netty-core:1.3.7
|         |    +--- io.netty:netty-handler:4.2.17.Final (*)
|         |    +--- io.netty:netty-handler-proxy:4.2.17.Final
|         |    |    +--- io.netty:netty-common:4.2.17.Final
|         |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-codec-socks:4.2.17.Final
|         |    |    |    +--- io.netty:netty-common:4.2.17.Final
|         |    |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    |    |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    |    |    \--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-codec-http:4.2.17.Final (*)
|         |    |    \--- io.netty:netty-handler:4.2.17.Final (*)
|         |    +--- io.netty:netty-resolver-dns:4.2.17.Final (*)
|         |    +--- io.netty:netty-resolver-dns-native-macos:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport-native-epoll:4.2.17.Final (*)
|         |    +--- io.projectreactor:reactor-core:3.8.7 (*)
|         |    \--- org.jspecify:jspecify:1.0.1
|         +--- io.projectreactor:reactor-core:3.8.7 (*)
|         \--- org.jspecify:jspecify:1.0.1
+--- org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.0
|    +--- org.springdoc:springdoc-openapi-starter-webmvc-api:3.1.0
|    |    +--- org.springdoc:springdoc-openapi-starter-common:3.1.0
|    |    |    +--- org.springframework.boot:spring-boot-starter:4.1.0 -> 4.1.1 (*)
|    |    |    +--- org.springframework.boot:spring-boot-autoconfigure:4.1.0 -> 4.1.1 (*)
|    |    |    +--- org.springframework.boot:spring-boot-validation:4.1.0 -> 4.1.1 (*)
|    |    |    +--- io.swagger.core.v3:swagger-core-jakarta:2.2.52
|    |    |    |    +--- org.apache.commons:commons-lang3:3.20.0
|    |    |    |    +--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    |    +--- io.swagger.core.v3:swagger-annotations-jakarta:2.2.52
|    |    |    |    +--- io.swagger.core.v3:swagger-models-jakarta:2.2.52
|    |    |    |    |    \--- com.fasterxml.jackson.core:jackson-annotations:2.21
|    |    |    |    +--- org.yaml:snakeyaml:2.6
|    |    |    |    +--- jakarta.xml.bind:jakarta.xml.bind-api:3.0.1 -> 4.0.5 (*)
|    |    |    |    +--- jakarta.validation:jakarta.validation-api:3.0.2 -> 3.1.1
|    |    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|    |    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.1 -> 2.21.5 (*)
|    |    |    |    +--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.22.0 -> 2.21.5
|    |    |    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.5 (*)
|    |    |    |    |    +--- org.yaml:snakeyaml:2.5 -> 2.6
|    |    |    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (*)
|    |    |    |    |    \--- com.fasterxml.jackson:jackson-bom:2.21.5 (*)
|    |    |    |    \--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.22.0 -> 2.21.5
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (*)
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-databind:2.21.5 (*)
|    |    |    |         \--- com.fasterxml.jackson:jackson-bom:2.21.5 (*)
|    |    |    \--- org.springframework.boot:spring-boot-jackson:4.1.0 -> 4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-webmvc:4.1.0 -> 4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-web-server:4.1.0 -> 4.1.1 (*)
|    +--- org.webjars:swagger-ui:5.32.11
|    \--- org.webjars:webjars-locator-lite:1.1.3 -> 1.1.4
|         \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
+--- com.slack.api:slack-api-client-kotlin-extension:{strictly 1.49.0} -> 1.49.0 (c)
+--- no.nav.boot:boot-conditionals:{strictly 6.0.7} -> 6.0.7 (c)
+--- net.logstash.logback:logstash-logback-encoder:{strictly 9.0} -> 9.0 (c)
+--- org.flywaydb:flyway-database-postgresql:{strictly 12.4.0} -> 12.4.0 (c)
+--- org.springframework.boot:spring-boot-starter-flyway:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-starter-graphql:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-starter-web:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-starter-webclient:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springdoc:springdoc-openapi-starter-webmvc-ui:{strictly 3.1.0} -> 3.1.0 (c)
+--- tools.jackson.module:jackson-module-kotlin:{strictly 3.1.5} -> 3.1.5 (c)
+--- org.jetbrains.kotlin:kotlin-stdlib:{strictly 2.4.0} -> 2.4.0 (c)
+--- no.nav.pdl.libs:contract-pdl-avro:{strictly 18} -> 18 (c)
+--- io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:{strictly 2.30.0} -> 2.30.0 (c)
+--- io.opentelemetry.instrumentation:opentelemetry-logback-mdc-1.0:{strictly 2.30.0-alpha} -> 2.30.0-alpha (c)
+--- io.micrometer:micrometer-registry-prometheus:{strictly 1.17.1} -> 1.17.1 (c)
+--- org.apache.commons:commons-pool2:{strictly 2.13.1} -> 2.13.1 (c)
+--- io.confluent:kafka-avro-serializer:{strictly 8.3.1} -> 8.3.1 (c)
+--- org.springframework.boot:spring-boot-starter-data-jpa:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.hibernate.orm:hibernate-micrometer:{strictly 7.4.5.Final} -> 7.4.5.Final (c)
+--- org.zalando:logbook-spring-boot-starter:{strictly 4.1.0} -> 4.1.0 (c)
+--- org.postgresql:postgresql:{strictly 42.7.13} -> 42.7.13 (c)
+--- org.springframework.boot:spring-boot-starter-actuator:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-starter-cache:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-starter-data-redis:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-starter-jetty:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-starter-kafka:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-starter-oauth2-client:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-starter-oauth2-resource-server:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-restclient:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-starter-validation:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.jetbrains:annotations:{strictly 13.0} -> 13.0 (c)
+--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:{strictly 2.4.0} -> 2.4.0 (c)
+--- org.jetbrains.kotlin:kotlin-reflect:{strictly 2.4.0} -> 2.4.0 (c)
+--- ch.qos.logback:logback-core:{strictly 1.5.38} -> 1.5.38 (c)
+--- org.slf4j:slf4j-api:{strictly 2.0.18} -> 2.0.18 (c)
+--- org.springframework.boot:spring-boot-autoconfigure:{strictly 4.1.1} -> 4.1.1 (c)
+--- io.opentelemetry:opentelemetry-api:{strictly 1.64.0} -> 1.64.0 (c)
+--- io.opentelemetry.instrumentation:opentelemetry-instrumentation-api:{strictly 2.30.0} -> 2.30.0 (c)
+--- io.opentelemetry.instrumentation:opentelemetry-instrumentation-api-incubator:{strictly 2.30.0-alpha} -> 2.30.0-alpha (c)
+--- org.jspecify:jspecify:{strictly 1.0.1} -> 1.0.1 (c)
+--- io.micrometer:micrometer-core:{strictly 1.17.1} -> 1.17.1 (c)
+--- io.prometheus:prometheus-metrics-core:{strictly 1.7.0} -> 1.7.0 (c)
+--- io.prometheus:prometheus-metrics-tracer-common:{strictly 1.7.0} -> 1.7.0 (c)
+--- io.prometheus:prometheus-metrics-exposition-formats:{strictly 1.7.0} -> 1.7.0 (c)
+--- com.slack.api:slack-api-model-kotlin-extension:{strictly 1.49.0} -> 1.49.0 (c)
+--- com.slack.api:slack-api-client:{strictly 1.49.0} -> 1.49.0 (c)
+--- org.apache.avro:avro:{strictly 1.12.1} -> 1.12.1 (c)
+--- org.apache.commons:commons-compress:{strictly 1.28.0} -> 1.28.0 (c)
+--- io.confluent:kafka-schema-serializer:{strictly 8.3.1} -> 8.3.1 (c)
+--- io.confluent:kafka-schema-registry-client:{strictly 8.3.1} -> 8.3.1 (c)
+--- com.google.guava:guava:{strictly 32.0.1-jre} -> 32.0.1-jre (c)
+--- io.confluent:logredactor:{strictly 1.0.18} -> 1.0.18 (c)
+--- io.confluent:common-utils:{strictly 8.3.1} -> 8.3.1 (c)
+--- org.flywaydb:flyway-core:{strictly 12.4.0} -> 12.4.0 (c)
+--- org.jboss.logging:jboss-logging:{strictly 3.6.3.Final} -> 3.6.3.Final (c)
+--- org.hibernate.orm:hibernate-core:{strictly 7.4.5.Final} -> 7.4.5.Final (c)
+--- org.hibernate.orm:hibernate-platform:{strictly 7.4.5.Final} -> 7.4.5.Final (c)
+--- tools.jackson.core:jackson-databind:{strictly 3.1.5} -> 3.1.5 (c)
+--- com.fasterxml.jackson.core:jackson-annotations:{strictly 2.21} -> 2.21 (c)
+--- tools.jackson:jackson-bom:{strictly 3.1.5} -> 3.1.5 (c)
+--- org.zalando:logbook-spring-boot-autoconfigure:{strictly 4.1.0} -> 4.1.0 (c)
+--- org.zalando:logbook-spring-boot-ecs-autoconfigure:{strictly 4.1.0} -> 4.1.0 (c)
+--- org.apiguardian:apiguardian-api:{strictly 1.1.2} -> 1.1.2 (c)
+--- org.zalando:faux-pas:{strictly 0.9.0} -> 0.9.0 (c)
+--- org.checkerframework:checker-qual:{strictly 3.55.1} -> 3.55.1 (c)
+--- org.springframework.boot:spring-boot-starter:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-starter-micrometer-metrics:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-actuator-autoconfigure:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-health:{strictly 4.1.1} -> 4.1.1 (c)
+--- io.micrometer:micrometer-observation:{strictly 1.17.1} -> 1.17.1 (c)
+--- io.micrometer:micrometer-jakarta9:{strictly 1.17.1} -> 1.17.1 (c)
+--- org.springframework.boot:spring-boot-cache:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-starter-jdbc:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-data-jpa:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-jdbc:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-data-redis:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework:spring-messaging:{strictly 7.0.9} -> 7.0.9 (c)
+--- org.springframework.boot:spring-boot-flyway:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-starter-jackson:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-reactor:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-graphql:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-starter-jetty-runtime:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-jetty:{strictly 4.1.1} -> 4.1.1 (c)
+--- jakarta.annotation:jakarta.annotation-api:{strictly 3.0.0} -> 3.0.0 (c)
+--- org.springframework.boot:spring-boot-kafka:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-starter-security:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-security-oauth2-client:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.security:spring-security-oauth2-jose:{strictly 7.1.1} -> 7.1.1 (c)
+--- org.springframework.boot:spring-boot-security-oauth2-resource-server:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-http-converter:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-http-client:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-validation:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-webmvc:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-webclient:{strictly 4.1.1} -> 4.1.1 (c)
+--- io.projectreactor.netty:reactor-netty-http:{strictly 1.3.7} -> 1.3.7 (c)
+--- org.springdoc:springdoc-openapi-starter-webmvc-api:{strictly 3.1.0} -> 3.1.0 (c)
+--- org.webjars:swagger-ui:{strictly 5.32.11} -> 5.32.11 (c)
+--- org.webjars:webjars-locator-lite:{strictly 1.1.4} -> 1.1.4 (c)
+--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:{strictly 2.4.0} -> 2.4.0 (c)
+--- io.opentelemetry:opentelemetry-context:{strictly 1.64.0} -> 1.64.0 (c)
+--- io.opentelemetry:opentelemetry-api-incubator:{strictly 1.64.0-alpha} -> 1.64.0-alpha (c)
+--- io.opentelemetry.semconv:opentelemetry-semconv:{strictly 1.43.0} -> 1.43.0 (c)
+--- io.micrometer:micrometer-commons:{strictly 1.17.1} -> 1.17.1 (c)
+--- org.hdrhistogram:HdrHistogram:{strictly 2.2.2} -> 2.2.2 (c)
+--- io.prometheus:prometheus-metrics-model:{strictly 1.7.0} -> 1.7.0 (c)
+--- io.prometheus:prometheus-metrics-config:{strictly 1.7.0} -> 1.7.0 (c)
+--- io.prometheus:prometheus-metrics-exposition-textformats:{strictly 1.7.0} -> 1.7.0 (c)
+--- com.slack.api:slack-api-model:{strictly 1.49.0} -> 1.49.0 (c)
+--- com.squareup.okhttp3:okhttp:{strictly 4.12.0} -> 4.12.0 (c)
+--- com.google.code.gson:gson:{strictly 2.13.2} -> 2.13.2 (c)
+--- com.fasterxml.jackson.core:jackson-core:{strictly 2.21.5} -> 2.21.5 (c)
+--- com.fasterxml.jackson.core:jackson-databind:{strictly 2.21.5} -> 2.21.5 (c)
+--- commons-codec:commons-codec:{strictly 1.21.0} -> 1.21.0 (c)
+--- commons-io:commons-io:{strictly 2.20.0} -> 2.20.0 (c)
+--- org.apache.commons:commons-lang3:{strictly 3.20.0} -> 3.20.0 (c)
+--- com.fasterxml.jackson.dataformat:jackson-dataformat-csv:{strictly 2.21.5} -> 2.21.5 (c)
+--- io.confluent:kafka-avro-types:{strictly 8.3.1} -> 8.3.1 (c)
+--- org.apache.kafka:kafka-clients:{strictly 4.2.1} -> 4.2.1 (c)
+--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:{strictly 2.21.5} -> 2.21.5 (c)
+--- org.yaml:snakeyaml:{strictly 2.6} -> 2.6 (c)
+--- io.swagger.core.v3:swagger-annotations-jakarta:{strictly 2.2.52} -> 2.2.52 (c)
+--- org.apache.httpcomponents.client5:httpclient5:{strictly 5.6.4} -> 5.6.4 (c)
+--- com.google.guava:failureaccess:{strictly 1.0.1} -> 1.0.1 (c)
+--- com.google.guava:listenablefuture:{strictly 9999.0-empty-to-avoid-conflict-with-guava} -> 9999.0-empty-to-avoid-conflict-with-guava (c)
+--- com.google.code.findbugs:jsr305:{strictly 3.0.2} -> 3.0.2 (c)
+--- com.google.errorprone:error_prone_annotations:{strictly 2.41.0} -> 2.41.0 (c)
+--- com.google.j2objc:j2objc-annotations:{strictly 2.8} -> 2.8 (c)
+--- com.google.re2j:re2j:{strictly 1.6} -> 1.6 (c)
+--- io.confluent:logredactor-metrics:{strictly 1.0.18} -> 1.0.18 (c)
+--- com.eclipsesource.minimal-json:minimal-json:{strictly 0.9.5} -> 0.9.5 (c)
+--- org.hibernate.models:hibernate-models:{strictly 1.1.1} -> 1.1.1 (c)
+--- net.bytebuddy:byte-buddy:{strictly 1.18.11} -> 1.18.11 (c)
+--- jakarta.xml.bind:jakarta.xml.bind-api:{strictly 4.0.5} -> 4.0.5 (c)
+--- org.glassfish.jaxb:jaxb-runtime:{strictly 4.0.9} -> 4.0.9 (c)
+--- jakarta.inject:jakarta.inject-api:{strictly 2.0.1} -> 2.0.1 (c)
+--- org.antlr:antlr4-runtime:{strictly 4.13.2} -> 4.13.2 (c)
+--- jakarta.persistence:jakarta.persistence-api:{strictly 3.2.0} -> 3.2.0 (c)
+--- jakarta.transaction:jakarta.transaction-api:{strictly 2.0.1} -> 2.0.1 (c)
+--- tools.jackson.core:jackson-core:{strictly 3.1.5} -> 3.1.5 (c)
+--- org.zalando:logbook-core:{strictly 4.1.0} -> 4.1.0 (c)
+--- org.zalando:logbook-json:{strictly 4.1.0} -> 4.1.0 (c)
+--- org.zalando:logbook-json-jackson2:{strictly 4.1.0} -> 4.1.0 (c)
+--- org.zalando:logbook-spring:{strictly 4.1.0} -> 4.1.0 (c)
+--- org.zalando:logbook-servlet:{strictly 4.1.0} -> 4.1.0 (c)
+--- org.springframework.boot:spring-boot-starter-logging:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-micrometer-metrics:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-actuator:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework:spring-context-support:{strictly 7.0.9} -> 7.0.9 (c)
+--- com.zaxxer:HikariCP:{strictly 7.0.2} -> 7.0.2 (c)
+--- org.springframework.boot:spring-boot-data-commons:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-hibernate:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.data:spring-data-jpa:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework:spring-aspects:{strictly 7.0.9} -> 7.0.9 (c)
+--- org.springframework.boot:spring-boot-sql:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-transaction:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework:spring-jdbc:{strictly 7.0.9} -> 7.0.9 (c)
+--- org.springframework.boot:spring-boot-netty:{strictly 4.1.1} -> 4.1.1 (c)
+--- io.lettuce:lettuce-core:{strictly 7.5.2.RELEASE} -> 7.5.2.RELEASE (c)
+--- org.springframework.data:spring-data-redis:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework:spring-beans:{strictly 7.0.9} -> 7.0.9 (c)
+--- org.springframework:spring-core:{strictly 7.0.9} -> 7.0.9 (c)
+--- org.springframework.boot:spring-boot-jackson:{strictly 4.1.1} -> 4.1.1 (c)
+--- io.projectreactor:reactor-core:{strictly 3.8.7} -> 3.8.7 (c)
+--- org.springframework.graphql:spring-graphql:{strictly 2.0.5} -> 2.0.5 (c)
+--- org.springframework.boot:spring-boot-web-server:{strictly 4.1.1} -> 4.1.1 (c)
+--- jakarta.servlet:jakarta.servlet-api:{strictly 6.1.0} -> 6.1.0 (c)
+--- jakarta.websocket:jakarta.websocket-api:{strictly 2.2.0} -> 2.2.0 (c)
+--- jakarta.websocket:jakarta.websocket-client-api:{strictly 2.2.0} -> 2.2.0 (c)
+--- org.apache.tomcat.embed:tomcat-embed-el:{strictly 11.0.24} -> 11.0.24 (c)
+--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-jakarta-server:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-jetty-server:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.eclipse.jetty.compression:jetty-compression-server:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.eclipse.jetty.compression:jetty-compression-gzip:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.eclipse.jetty.ee11:jetty-ee11-webapp:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.springframework.kafka:spring-kafka:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-security:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework:spring-aop:{strictly 7.0.9} -> 7.0.9 (c)
+--- org.springframework.security:spring-security-oauth2-client:{strictly 7.1.1} -> 7.1.1 (c)
+--- org.springframework.security:spring-security-core:{strictly 7.1.1} -> 7.1.1 (c)
+--- org.springframework.security:spring-security-oauth2-core:{strictly 7.1.1} -> 7.1.1 (c)
+--- com.nimbusds:nimbus-jose-jwt:{strictly 10.9.1} -> 10.9.1 (c)
+--- org.springframework.security:spring-security-oauth2-resource-server:{strictly 7.1.1} -> 7.1.1 (c)
+--- org.springframework:spring-web:{strictly 7.0.9} -> 7.0.9 (c)
+--- org.springframework:spring-context:{strictly 7.0.9} -> 7.0.9 (c)
+--- org.hibernate.validator:hibernate-validator:{strictly 9.1.3.Final} -> 9.1.3.Final (c)
+--- org.springframework.boot:spring-boot-servlet:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework:spring-webmvc:{strictly 7.0.9} -> 7.0.9 (c)
+--- org.springframework.boot:spring-boot-http-codec:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework:spring-webflux:{strictly 7.0.9} -> 7.0.9 (c)
+--- io.netty:netty-codec-http:{strictly 4.2.17.Final} -> 4.2.17.Final (c)
+--- io.netty:netty-codec-http2:{strictly 4.2.17.Final} -> 4.2.17.Final (c)
+--- io.netty:netty-codec-http3:{strictly 4.2.17.Final} -> 4.2.17.Final (c)
+--- io.netty:netty-resolver-dns:{strictly 4.2.17.Final} -> 4.2.17.Final (c)
+--- io.netty:netty-resolver-dns-native-macos:{strictly 4.2.17.Final} -> 4.2.17.Final (c)
+--- io.netty:netty-transport-native-epoll:{strictly 4.2.17.Final} -> 4.2.17.Final (c)
+--- io.projectreactor.netty:reactor-netty-core:{strictly 1.3.7} -> 1.3.7 (c)
+--- org.springdoc:springdoc-openapi-starter-common:{strictly 3.1.0} -> 3.1.0 (c)
+--- io.opentelemetry:opentelemetry-common:{strictly 1.64.0} -> 1.64.0 (c)
+--- com.squareup.okio:okio:{strictly 3.6.0} -> 3.6.0 (c)
+--- com.fasterxml.jackson:jackson-bom:{strictly 2.21.5} -> 2.21.5 (c)
+--- io.confluent:kafka-schema-types:{strictly 8.3.1} -> 8.3.1 (c)
+--- com.github.luben:zstd-jni:{strictly 1.5.6-10} -> 1.5.6-10 (c)
+--- at.yawk.lz4:lz4-java:1.11.1 (c)
+--- at.yawk.lz4:lz4-java:{strictly 1.11.1} -> 1.11.1 (c)
+--- org.xerial.snappy:snappy-java:{strictly 1.1.10.7} -> 1.1.10.7 (c)
+--- org.apache.httpcomponents.core5:httpcore5:{strictly 5.4.3} -> 5.4.3 (c)
+--- org.apache.httpcomponents.core5:httpcore5-h2:{strictly 5.4.3} -> 5.4.3 (c)
+--- jakarta.activation:jakarta.activation-api:{strictly 2.1.4} -> 2.1.4 (c)
+--- org.glassfish.jaxb:jaxb-core:{strictly 4.0.9} -> 4.0.9 (c)
+--- org.zalando:logbook-api:{strictly 4.1.0} -> 4.1.0 (c)
+--- org.zalando:logbook-common:{strictly 4.1.0} -> 4.1.0 (c)
+--- com.jayway.jsonpath:json-path:{strictly 2.10.0} -> 2.10.0 (c)
+--- ch.qos.logback:logback-classic:{strictly 1.5.38} -> 1.5.38 (c)
+--- org.apache.logging.log4j:log4j-to-slf4j:{strictly 2.25.5} -> 2.25.5 (c)
+--- org.slf4j:jul-to-slf4j:{strictly 2.0.18} -> 2.0.18 (c)
+--- org.springframework.boot:spring-boot-micrometer-observation:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-persistence:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.data:spring-data-commons:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework.boot:spring-boot-jpa:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework:spring-orm:{strictly 7.0.9} -> 7.0.9 (c)
+--- org.springframework:spring-tx:{strictly 7.0.9} -> 7.0.9 (c)
+--- org.aspectj:aspectjweaver:{strictly 1.9.25.1} -> 1.9.25.1 (c)
+--- io.netty:netty-common:{strictly 4.2.17.Final} -> 4.2.17.Final (c)
+--- redis.clients.authentication:redis-authx-core:{strictly 0.1.1-beta2} -> 0.1.1-beta2 (c)
+--- io.netty:netty-handler:{strictly 4.2.17.Final} -> 4.2.17.Final (c)
+--- io.netty:netty-transport:{strictly 4.2.17.Final} -> 4.2.17.Final (c)
+--- org.springframework.data:spring-data-keyvalue:{strictly 4.1.1} -> 4.1.1 (c)
+--- org.springframework:spring-oxm:{strictly 7.0.9} -> 7.0.9 (c)
+--- commons-logging:commons-logging:{strictly 1.3.6} -> 1.3.6 (c)
+--- org.reactivestreams:reactive-streams:{strictly 1.0.4} -> 1.0.4 (c)
+--- io.micrometer:context-propagation:{strictly 1.2.1} -> 1.2.1 (c)
+--- com.graphql-java:graphql-java:{strictly 25.0} -> 25.0 (c)
+--- org.eclipse.jetty.ee11:jetty-ee11-annotations:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-jakarta-client:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-servlet:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.eclipse.jetty.ee11:jetty-ee11-servlet:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.eclipse.jetty.websocket:jetty-websocket-jetty-api:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.eclipse.jetty.websocket:jetty-websocket-jetty-common:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.eclipse.jetty.websocket:jetty-websocket-jetty-server:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.eclipse.jetty:jetty-server:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.eclipse.jetty.compression:jetty-compression-common:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.eclipse.jetty:jetty-session:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.eclipse.jetty:jetty-xml:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.eclipse.jetty.ee:jetty-ee-webapp:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.springframework.security:spring-security-config:{strictly 7.1.1} -> 7.1.1 (c)
+--- org.springframework.security:spring-security-web:{strictly 7.1.1} -> 7.1.1 (c)
+--- com.nimbusds:oauth2-oidc-sdk:{strictly 11.38.2} -> 11.38.2 (c)
+--- org.springframework.security:spring-security-crypto:{strictly 7.1.1} -> 7.1.1 (c)
+--- org.springframework:spring-expression:{strictly 7.0.9} -> 7.0.9 (c)
+--- jakarta.validation:jakarta.validation-api:{strictly 3.1.1} -> 3.1.1 (c)
+--- com.fasterxml:classmate:{strictly 1.7.3} -> 1.7.3 (c)
+--- io.netty:netty-buffer:{strictly 4.2.17.Final} -> 4.2.17.Final (c)
+--- io.netty:netty-codec-base:{strictly 4.2.17.Final} -> 4.2.17.Final (c)
+--- io.netty:netty-codec-compression:{strictly 4.2.17.Final} -> 4.2.17.Final (c)
+--- io.netty:netty-transport-native-unix-common:{strictly 4.2.17.Final} -> 4.2.17.Final (c)
+--- io.netty:netty-resolver:{strictly 4.2.17.Final} -> 4.2.17.Final (c)
+--- io.netty:netty-codec-classes-quic:{strictly 4.2.17.Final} -> 4.2.17.Final (c)
+--- io.netty:netty-codec-native-quic:{strictly 4.2.17.Final} -> 4.2.17.Final (c)
+--- io.netty:netty-codec-dns:{strictly 4.2.17.Final} -> 4.2.17.Final (c)
+--- io.netty:netty-resolver-dns-classes-macos:{strictly 4.2.17.Final} -> 4.2.17.Final (c)
+--- io.netty:netty-transport-classes-epoll:{strictly 4.2.17.Final} -> 4.2.17.Final (c)
+--- io.netty:netty-handler-proxy:{strictly 4.2.17.Final} -> 4.2.17.Final (c)
+--- io.swagger.core.v3:swagger-core-jakarta:{strictly 2.2.52} -> 2.2.52 (c)
+--- com.squareup.okio:okio-jvm:{strictly 3.6.0} -> 3.6.0 (c)
+--- org.eclipse.angus:angus-activation:{strictly 2.0.3} -> 2.0.3 (c)
+--- org.glassfish.jaxb:txw2:{strictly 4.0.9} -> 4.0.9 (c)
+--- com.sun.istack:istack-commons-runtime:{strictly 4.1.2} -> 4.1.2 (c)
+--- org.apache.logging.log4j:log4j-api:{strictly 2.25.5} -> 2.25.5 (c)
+--- com.graphql-java:java-dataloader:{strictly 6.0.0} -> 6.0.0 (c)
+--- org.eclipse.jetty:jetty-annotations:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.eclipse.jetty.ee11:jetty-ee11-plus:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.ow2.asm:asm:{strictly 9.10.1} -> 9.10.1 (c)
+--- org.ow2.asm:asm-commons:{strictly 9.10.1} -> 9.10.1 (c)
+--- org.eclipse.jetty:jetty-client:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-jakarta-common:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.eclipse.jetty.websocket:jetty-websocket-core-client:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.eclipse.jetty.websocket:jetty-websocket-core-server:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.eclipse.jetty:jetty-security:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.eclipse.jetty.websocket:jetty-websocket-core-common:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.eclipse.jetty:jetty-http:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.eclipse.jetty:jetty-io:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.eclipse.jetty:jetty-util:{strictly 12.1.12} -> 12.1.12 (c)
+--- com.github.stephenc.jcip:jcip-annotations:{strictly 1.0-1} -> 1.0-1 (c)
+--- com.nimbusds:content-type:{strictly 2.3} -> 2.3 (c)
+--- net.minidev:json-smart:{strictly 2.6.0} -> 2.6.0 (c)
+--- com.nimbusds:lang-tag:{strictly 1.7} -> 1.7 (c)
+--- io.netty:netty-codec-socks:{strictly 4.2.17.Final} -> 4.2.17.Final (c)
+--- io.swagger.core.v3:swagger-models-jakarta:{strictly 2.2.52} -> 2.2.52 (c)
+--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:{strictly 2.21.5} -> 2.21.5 (c)
+--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:{strictly 2.21.5} -> 2.21.5 (c)
+--- org.jetbrains.kotlin:kotlin-stdlib-common:{strictly 2.4.0} -> 2.4.0 (c)
+--- jakarta.enterprise:jakarta.enterprise.cdi-api:{strictly 4.1.0} -> 4.1.0 (c)
+--- jakarta.enterprise:jakarta.enterprise.lang-model:{strictly 4.1.0} -> 4.1.0 (c)
+--- jakarta.interceptor:jakarta.interceptor-api:{strictly 2.2.0} -> 2.2.0 (c)
+--- org.eclipse.jetty:jetty-plus:{strictly 12.1.12} -> 12.1.12 (c)
+--- org.ow2.asm:asm-tree:{strictly 9.10.1} -> 9.10.1 (c)
+--- org.eclipse.jetty:jetty-alpn-client:{strictly 12.1.12} -> 12.1.12 (c)
\--- net.minidev:accessors-smart:{strictly 2.6.0} -> 2.6.0 (c)

runtimeClasspath - Runtime classpath of 'main'.
+--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0
|    +--- org.jetbrains:annotations:13.0
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0 -> 2.4.0 (c)
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.0 -> 2.4.0 (c)
|    \--- org.jetbrains.kotlin:kotlin-stdlib-common:2.4.0 (c)
+--- no.nav.pdl.libs:contract-pdl-avro:18
|    \--- io.confluent:kafka-avro-serializer:7.7.1 -> 8.3.1
|         +--- org.apache.avro:avro:1.12.1
|         |    +--- com.fasterxml.jackson.core:jackson-core:2.20.0 -> 2.21.5
|         |    |    \--- com.fasterxml.jackson:jackson-bom:2.21.5
|         |    |         +--- com.fasterxml.jackson.core:jackson-annotations:2.21 (c)
|         |    |         +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (c)
|         |    |         +--- com.fasterxml.jackson.core:jackson-databind:2.21.5 (c)
|         |    |         +--- com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.21.5 (c)
|         |    |         +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.21.5 (c)
|         |    |         +--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.21.5 (c)
|         |    |         \--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.21.5 (c)
|         |    +--- com.fasterxml.jackson.core:jackson-databind:2.20.0 -> 2.21.5
|         |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|         |    |    +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (*)
|         |    |    \--- com.fasterxml.jackson:jackson-bom:2.21.5 (*)
|         |    +--- org.apache.commons:commons-compress:1.28.0
|         |    |    +--- commons-codec:commons-codec:1.19.0 -> 1.21.0
|         |    |    +--- commons-io:commons-io:2.20.0
|         |    |    \--- org.apache.commons:commons-lang3:3.18.0 -> 3.20.0
|         |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|         +--- org.apache.commons:commons-compress:1.26.1 -> 1.28.0 (*)
|         +--- io.confluent:kafka-schema-serializer:8.3.1
|         |    +--- io.confluent:kafka-schema-registry-client:8.3.1
|         |    |    +--- io.confluent:kafka-avro-types:8.3.1
|         |    |    |    +--- io.confluent:kafka-schema-types:8.3.1
|         |    |    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.2 -> 2.21.5 (*)
|         |    |    |    |    \--- io.confluent:common-utils:8.3.1
|         |    |    |    |         \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    |    |    +--- org.apache.avro:avro:1.12.1 (*)
|         |    |    |    \--- io.confluent:common-utils:8.3.1 (*)
|         |    |    +--- org.apache.kafka:kafka-clients:8.3.1-ccs -> 4.2.1
|         |    |    |    +--- com.github.luben:zstd-jni:1.5.6-10
|         |    |    |    +--- at.yawk.lz4:lz4-java:1.10.1 -> 1.11.1
|         |    |    |    +--- org.xerial.snappy:snappy-java:1.1.10.7
|         |    |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    |    +--- org.apache.avro:avro:1.12.1 (*)
|         |    |    +--- org.apache.commons:commons-compress:1.26.1 -> 1.28.0 (*)
|         |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.2 -> 2.21.5 (*)
|         |    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.21.2 -> 2.21.5
|         |    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (*)
|         |    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.5 (*)
|         |    |    |    \--- com.fasterxml.jackson:jackson-bom:2.21.5 (*)
|         |    |    +--- org.yaml:snakeyaml:2.0 -> 2.6
|         |    |    +--- io.swagger.core.v3:swagger-annotations-jakarta:2.2.42 -> 2.2.52
|         |    |    +--- com.google.guava:guava:32.0.1-jre
|         |    |    |    +--- com.google.guava:failureaccess:1.0.1
|         |    |    |    +--- com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava
|         |    |    |    +--- com.google.code.findbugs:jsr305:3.0.2
|         |    |    |    +--- org.checkerframework:checker-qual:3.33.0 -> 3.55.1
|         |    |    |    +--- com.google.errorprone:error_prone_annotations:2.18.0 -> 2.41.0
|         |    |    |    \--- com.google.j2objc:j2objc-annotations:2.8
|         |    |    +--- org.apache.httpcomponents.client5:httpclient5:5.5 -> 5.6.4
|         |    |    |    +--- org.apache.httpcomponents.core5:httpcore5:5.4.3
|         |    |    |    +--- org.apache.httpcomponents.core5:httpcore5-h2:5.4.3
|         |    |    |    |    \--- org.apache.httpcomponents.core5:httpcore5:5.4.3
|         |    |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    |    \--- io.confluent:common-utils:8.3.1 (*)
|         |    +--- com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.21.2 -> 2.21.5
|         |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.5 (*)
|         |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|         |    |    +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (*)
|         |    |    \--- com.fasterxml.jackson:jackson-bom:2.21.5 (*)
|         |    \--- io.confluent:common-utils:8.3.1 (*)
|         +--- io.confluent:kafka-schema-registry-client:8.3.1 (*)
|         +--- com.google.guava:guava:32.0.1-jre (*)
|         +--- io.confluent:logredactor:1.0.18
|         |    +--- com.google.re2j:re2j:1.6
|         |    +--- io.confluent:logredactor-metrics:1.0.18
|         |    +--- com.eclipsesource.minimal-json:minimal-json:0.9.5
|         |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         \--- io.confluent:common-utils:8.3.1 (*)
+--- no.nav.boot:boot-conditionals:6.0.7
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.4.0
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.4.0
|    |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:2.4.0
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
|    +--- ch.qos.logback:logback-core:1.5.34 -> 1.5.38
|    +--- org.slf4j:slf4j-api:2.0.18
|    \--- org.springframework.boot:spring-boot-autoconfigure:4.1.0 -> 4.1.1
|         \--- org.springframework.boot:spring-boot:4.1.1
|              +--- org.springframework:spring-core:7.0.9
|              |    +--- commons-logging:commons-logging:1.3.5 -> 1.3.6
|              |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|              \--- org.springframework:spring-context:7.0.9
|                   +--- org.springframework:spring-aop:7.0.9
|                   |    +--- org.springframework:spring-beans:7.0.9
|                   |    |    \--- org.springframework:spring-core:7.0.9 (*)
|                   |    \--- org.springframework:spring-core:7.0.9 (*)
|                   +--- org.springframework:spring-beans:7.0.9 (*)
|                   +--- org.springframework:spring-core:7.0.9 (*)
|                   +--- org.springframework:spring-expression:7.0.9
|                   |    \--- org.springframework:spring-core:7.0.9 (*)
|                   \--- io.micrometer:micrometer-observation:1.16.7 -> 1.17.1
|                        +--- org.jspecify:jspecify:1.0.1
|                        \--- io.micrometer:micrometer-commons:1.17.1
|                             \--- org.jspecify:jspecify:1.0.1
+--- io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations -> 2.30.0
|    \--- io.opentelemetry:opentelemetry-api:1.64.0
|         \--- io.opentelemetry:opentelemetry-context:1.64.0
|              \--- io.opentelemetry:opentelemetry-common:1.64.0
+--- io.opentelemetry.instrumentation:opentelemetry-logback-mdc-1.0:2.30.0-alpha
|    +--- io.opentelemetry.instrumentation:opentelemetry-instrumentation-api:2.30.0
|    |    +--- io.opentelemetry:opentelemetry-api-incubator:1.64.0-alpha
|    |    |    \--- io.opentelemetry:opentelemetry-api:1.64.0 (*)
|    |    +--- io.opentelemetry.semconv:opentelemetry-semconv:1.43.0
|    |    \--- io.opentelemetry:opentelemetry-api:1.64.0 (*)
|    +--- io.opentelemetry.instrumentation:opentelemetry-instrumentation-api-incubator:2.30.0-alpha
|    |    +--- io.opentelemetry.semconv:opentelemetry-semconv:1.43.0
|    |    +--- io.opentelemetry.instrumentation:opentelemetry-instrumentation-api:2.30.0 (*)
|    |    \--- io.opentelemetry:opentelemetry-api-incubator:1.64.0-alpha (*)
|    \--- io.opentelemetry:opentelemetry-api:1.64.0 (*)
+--- io.micrometer:micrometer-registry-prometheus -> 1.17.1
|    +--- org.jspecify:jspecify:1.0.1
|    +--- io.micrometer:micrometer-core:1.17.1
|    |    +--- org.jspecify:jspecify:1.0.1
|    |    +--- io.micrometer:micrometer-commons:1.17.1 (*)
|    |    +--- io.micrometer:micrometer-observation:1.17.1 (*)
|    |    \--- org.hdrhistogram:HdrHistogram:2.2.2
|    +--- io.prometheus:prometheus-metrics-core:1.7.0
|    |    +--- io.prometheus:prometheus-metrics-model:1.7.0
|    |    |    \--- io.prometheus:prometheus-metrics-config:1.7.0
|    |    \--- io.prometheus:prometheus-metrics-config:1.7.0
|    +--- io.prometheus:prometheus-metrics-tracer-common:1.7.0
|    \--- io.prometheus:prometheus-metrics-exposition-formats:1.7.0
|         \--- io.prometheus:prometheus-metrics-exposition-textformats:1.7.0
|              +--- io.prometheus:prometheus-metrics-model:1.7.0 (*)
|              \--- io.prometheus:prometheus-metrics-config:1.7.0
+--- com.slack.api:slack-api-client-kotlin-extension:1.49.0
|    +--- com.slack.api:slack-api-model-kotlin-extension:1.49.0
|    |    +--- com.slack.api:slack-api-model:1.49.0
|    |    |    \--- com.google.code.gson:gson:2.12.1 -> 2.13.2
|    |    |         \--- com.google.errorprone:error_prone_annotations:2.41.0
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.24 -> 2.4.0 (*)
|    +--- com.slack.api:slack-api-client:1.49.0
|    |    +--- com.slack.api:slack-api-model:1.49.0 (*)
|    |    +--- com.squareup.okhttp3:okhttp:4.12.0
|    |    |    +--- com.squareup.okio:okio:3.6.0
|    |    |    |    \--- com.squareup.okio:okio-jvm:3.6.0
|    |    |    |         +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.10 -> 2.4.0 (*)
|    |    |    |         \--- org.jetbrains.kotlin:kotlin-stdlib-common:1.9.10 -> 2.4.0
|    |    |    |              \--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
|    |    |    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.21 -> 2.4.0 (*)
|    |    +--- com.google.code.gson:gson:2.12.1 -> 2.13.2 (*)
|    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|    \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.24 -> 2.4.0 (*)
+--- org.apache.commons:commons-pool2:2.13.1
+--- io.confluent:kafka-avro-serializer:8.3.1 (*)
+--- org.flywaydb:flyway-database-postgresql -> 12.4.0
|    \--- org.flywaydb:flyway-core:12.4.0
|         \--- tools.jackson.core:jackson-databind:3.1.1 -> 3.1.5
|              +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|              +--- tools.jackson.core:jackson-core:3.1.5
|              |    \--- tools.jackson:jackson-bom:3.1.5
|              |         +--- com.fasterxml.jackson.core:jackson-annotations:2.21 (c)
|              |         +--- tools.jackson.core:jackson-core:3.1.5 (c)
|              |         +--- tools.jackson.core:jackson-databind:3.1.5 (c)
|              |         \--- tools.jackson.module:jackson-module-kotlin:3.1.5 (c)
|              \--- tools.jackson:jackson-bom:3.1.5 (*)
+--- org.hibernate.orm:hibernate-micrometer -> 7.4.5.Final
|    +--- org.jboss.logging:jboss-logging:3.6.1.Final -> 3.6.3.Final
|    +--- org.hibernate.orm:hibernate-core:7.4.5.Final
|    |    +--- org.jboss.logging:jboss-logging:3.6.1.Final -> 3.6.3.Final
|    |    +--- org.hibernate.models:hibernate-models:1.1.1
|    |    |    \--- org.jboss.logging:jboss-logging:3.6.3.Final
|    |    +--- net.bytebuddy:byte-buddy:1.18.8 -> 1.18.11
|    |    +--- jakarta.xml.bind:jakarta.xml.bind-api:4.0.4 -> 4.0.5
|    |    |    \--- jakarta.activation:jakarta.activation-api:2.1.4
|    |    +--- org.glassfish.jaxb:jaxb-runtime:4.0.7 -> 4.0.9
|    |    |    \--- org.glassfish.jaxb:jaxb-core:4.0.9
|    |    |         +--- jakarta.xml.bind:jakarta.xml.bind-api:4.0.5 (*)
|    |    |         +--- jakarta.activation:jakarta.activation-api:2.1.4
|    |    |         +--- org.eclipse.angus:angus-activation:2.0.3
|    |    |         |    \--- jakarta.activation:jakarta.activation-api:2.1.4
|    |    |         +--- org.glassfish.jaxb:txw2:4.0.9
|    |    |         \--- com.sun.istack:istack-commons-runtime:4.1.2
|    |    +--- jakarta.inject:jakarta.inject-api:2.0.1
|    |    +--- org.antlr:antlr4-runtime:4.13.2
|    |    +--- org.hibernate.orm:hibernate-platform:7.4.5.Final
|    |    |    +--- org.antlr:antlr4-runtime:4.13.2 (c)
|    |    |    +--- org.jboss.logging:jboss-logging:3.6.1.Final -> 3.6.3.Final (c)
|    |    |    +--- net.bytebuddy:byte-buddy:1.18.8 -> 1.18.11 (c)
|    |    |    +--- org.glassfish.jaxb:jaxb-runtime:4.0.7 -> 4.0.9 (c)
|    |    |    +--- jakarta.xml.bind:jakarta.xml.bind-api:4.0.4 -> 4.0.5 (c)
|    |    |    +--- jakarta.inject:jakarta.inject-api:2.0.1 (c)
|    |    |    +--- io.micrometer:micrometer-core:1.16.0 -> 1.17.1 (c)
|    |    |    +--- org.hibernate.orm:hibernate-core:7.4.5.Final (c)
|    |    |    +--- org.hibernate.orm:hibernate-micrometer:7.4.5.Final (c)
|    |    |    +--- org.hibernate.models:hibernate-models:1.1.1 (c)
|    |    |    +--- jakarta.persistence:jakarta.persistence-api:3.2.0 (c)
|    |    |    +--- jakarta.transaction:jakarta.transaction-api:2.0.1 (c)
|    |    |    \--- com.zaxxer:HikariCP:7.0.2 (c)
|    |    +--- jakarta.persistence:jakarta.persistence-api:3.2.0
|    |    \--- jakarta.transaction:jakarta.transaction-api:2.0.1
|    +--- io.micrometer:micrometer-core:1.16.0 -> 1.17.1 (*)
|    \--- org.hibernate.orm:hibernate-platform:7.4.5.Final (*)
+--- tools.jackson.module:jackson-module-kotlin -> 3.1.5
|    +--- tools.jackson.core:jackson-databind:3.1.5 (*)
|    +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|    +--- org.jetbrains.kotlin:kotlin-reflect:2.1.21 -> 2.4.0 (*)
|    \--- tools.jackson:jackson-bom:3.1.5 (*)
+--- org.zalando:logbook-spring-boot-starter:4.1.0
|    +--- org.zalando:logbook-spring-boot-autoconfigure:4.1.0
|    |    +--- org.zalando:logbook-core:4.1.0
|    |    |    +--- org.zalando:logbook-api:4.1.0
|    |    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    |    +--- org.zalando:faux-pas:0.9.0
|    |    |    |    |    +--- com.google.code.findbugs:jsr305:3.0.2
|    |    |    |    |    \--- org.slf4j:slf4j-api:1.7.30 -> 2.0.18
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    |    +--- org.zalando:logbook-common:4.1.0
|    |    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.zalando:logbook-json:4.1.0
|    |    |    +--- org.zalando:logbook-api:4.1.0 (*)
|    |    |    +--- org.zalando:logbook-common:4.1.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.22 -> 2.21
|    |    |    +--- com.jayway.jsonpath:json-path:3.0.0 -> 2.10.0
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.zalando:logbook-json-jackson2:4.1.0
|    |    |    +--- org.zalando:logbook-api:4.1.0 (*)
|    |    |    +--- org.zalando:logbook-common:4.1.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.22 -> 2.21
|    |    |    +--- com.jayway.jsonpath:json-path:3.0.0 -> 2.10.0 (*)
|    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.zalando:logbook-spring:4.1.0
|    |    |    +--- org.zalando:logbook-core:4.1.0 (*)
|    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.zalando:logbook-servlet:4.1.0
|    |    |    +--- org.zalando:logbook-api:4.1.0 (*)
|    |    |    +--- org.zalando:logbook-core:4.1.0 (*)
|    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    +--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    +--- org.zalando:logbook-spring-boot-ecs-autoconfigure:4.1.0
|    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    +--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    +--- org.apiguardian:apiguardian-api:1.1.2
|    +--- org.zalando:faux-pas:0.9.0 (*)
|    \--- org.slf4j:slf4j-api:2.0.18
+--- net.logstash.logback:logstash-logback-encoder:9.0
|    \--- tools.jackson.core:jackson-databind:3.0.1 -> 3.1.5 (*)
+--- org.postgresql:postgresql -> 42.7.13
|    \--- org.checkerframework:checker-qual:3.55.1
+--- org.springframework.boot:spring-boot-starter-actuator -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter-logging:4.1.1
|    |    |    +--- ch.qos.logback:logback-classic:1.5.38
|    |    |    |    +--- ch.qos.logback:logback-core:1.5.38
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    +--- org.apache.logging.log4j:log4j-to-slf4j:2.25.5
|    |    |    |    +--- org.apache.logging.log4j:log4j-api:2.25.5
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    \--- org.slf4j:jul-to-slf4j:2.0.18
|    |    |         \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.springframework.boot:spring-boot-autoconfigure:4.1.1 (*)
|    |    +--- jakarta.annotation:jakarta.annotation-api:3.0.0
|    |    \--- org.yaml:snakeyaml:2.6
|    +--- org.springframework.boot:spring-boot-starter-micrometer-metrics:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-micrometer-metrics:4.1.1
|    |         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |         +--- org.springframework.boot:spring-boot-micrometer-observation:4.1.1
|    |         |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |         |    \--- io.micrometer:micrometer-observation:1.17.1 (*)
|    |         \--- io.micrometer:micrometer-core:1.17.1 (*)
|    +--- org.springframework.boot:spring-boot-actuator-autoconfigure:4.1.1
|    |    +--- org.springframework.boot:spring-boot-autoconfigure:4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-actuator:4.1.1
|    |         \--- org.springframework.boot:spring-boot:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-health:4.1.1
|    |    \--- org.springframework.boot:spring-boot:4.1.1 (*)
|    +--- io.micrometer:micrometer-observation:1.17.1 (*)
|    \--- io.micrometer:micrometer-jakarta9:1.17.1
|         +--- org.jspecify:jspecify:1.0.1
|         +--- io.micrometer:micrometer-core:1.17.1 (*)
|         +--- io.micrometer:micrometer-commons:1.17.1 (*)
|         \--- io.micrometer:micrometer-observation:1.17.1 (*)
+--- org.springframework.boot:spring-boot-starter-cache -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-cache:4.1.1
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         \--- org.springframework:spring-context-support:7.0.9
|              +--- org.springframework:spring-beans:7.0.9 (*)
|              +--- org.springframework:spring-context:7.0.9 (*)
|              \--- org.springframework:spring-core:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-data-jpa -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jdbc:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-jdbc:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    +--- org.springframework.boot:spring-boot-sql:4.1.1
|    |    |    |    \--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    +--- org.springframework.boot:spring-boot-transaction:4.1.1
|    |    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    |    +--- org.springframework.boot:spring-boot-persistence:4.1.1
|    |    |    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    |    |    \--- org.springframework:spring-tx:7.0.9
|    |    |    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    |    |         \--- org.springframework:spring-core:7.0.9 (*)
|    |    |    |    \--- org.springframework:spring-tx:7.0.9 (*)
|    |    |    \--- org.springframework:spring-jdbc:7.0.9
|    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |    |         \--- org.springframework:spring-tx:7.0.9 (*)
|    |    \--- com.zaxxer:HikariCP:7.0.2
|    |         \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    +--- org.springframework.boot:spring-boot-data-jpa:4.1.1
|    |    +--- org.springframework.boot:spring-boot-data-commons:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    +--- org.springframework.boot:spring-boot-persistence:4.1.1 (*)
|    |    |    \--- org.springframework.data:spring-data-commons:4.1.1
|    |    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |         \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.springframework.boot:spring-boot-hibernate:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot-jpa:4.1.1
|    |    |    |    +--- org.springframework.boot:spring-boot-jdbc:4.1.1 (*)
|    |    |    |    +--- org.springframework.boot:spring-boot-transaction:4.1.1 (*)
|    |    |    |    +--- jakarta.persistence:jakarta.persistence-api:3.2.0
|    |    |    |    \--- org.springframework:spring-orm:7.0.9
|    |    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |    |    |         +--- org.springframework:spring-jdbc:7.0.9 (*)
|    |    |    |         \--- org.springframework:spring-tx:7.0.9 (*)
|    |    |    +--- org.hibernate.orm:hibernate-core:7.4.5.Final (*)
|    |    |    \--- org.springframework:spring-orm:7.0.9 (*)
|    |    +--- org.springframework.data:spring-data-jpa:4.1.1
|    |    |    +--- org.springframework.data:spring-data-commons:4.1.1 (*)
|    |    |    +--- org.springframework:spring-orm:7.0.9 (*)
|    |    |    +--- org.springframework:spring-context:7.0.9 (*)
|    |    |    +--- org.springframework:spring-aop:7.0.9 (*)
|    |    |    +--- org.springframework:spring-tx:7.0.9 (*)
|    |    |    +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    +--- org.springframework:spring-core:7.0.9 (*)
|    |    |    +--- org.antlr:antlr4-runtime:4.13.2
|    |    |    +--- jakarta.annotation:jakarta.annotation-api:2.0.0 -> 3.0.0
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    \--- org.springframework:spring-aspects:7.0.9
|    |         \--- org.aspectj:aspectjweaver:1.9.25 -> 1.9.25.1
|    \--- org.springframework.boot:spring-boot-jdbc:4.1.1 (*)
+--- org.springframework.boot:spring-boot-starter-data-redis -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-data-redis:4.1.1
|    |    +--- org.springframework.boot:spring-boot-netty:4.1.1
|    |    |    +--- io.netty:netty-common:4.2.17.Final
|    |    |    \--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-data-commons:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-transaction:4.1.1 (*)
|    |    +--- io.lettuce:lettuce-core:7.5.2.RELEASE
|    |    |    +--- redis.clients.authentication:redis-authx-core:0.1.1-beta2
|    |    |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|    |    |    +--- io.netty:netty-common:4.2.13.Final -> 4.2.17.Final
|    |    |    +--- io.netty:netty-handler:4.2.13.Final -> 4.2.17.Final
|    |    |    |    +--- io.netty:netty-common:4.2.17.Final
|    |    |    |    +--- io.netty:netty-resolver:4.2.17.Final
|    |    |    |    |    \--- io.netty:netty-common:4.2.17.Final
|    |    |    |    +--- io.netty:netty-buffer:4.2.17.Final
|    |    |    |    |    \--- io.netty:netty-common:4.2.17.Final
|    |    |    |    +--- io.netty:netty-transport:4.2.17.Final
|    |    |    |    |    +--- io.netty:netty-common:4.2.17.Final
|    |    |    |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|    |    |    |    |    \--- io.netty:netty-resolver:4.2.17.Final (*)
|    |    |    |    +--- io.netty:netty-transport-native-unix-common:4.2.17.Final
|    |    |    |    |    +--- io.netty:netty-common:4.2.17.Final
|    |    |    |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|    |    |    |    |    \--- io.netty:netty-transport:4.2.17.Final (*)
|    |    |    |    \--- io.netty:netty-codec-base:4.2.17.Final
|    |    |    |         +--- io.netty:netty-common:4.2.17.Final
|    |    |    |         +--- io.netty:netty-buffer:4.2.17.Final (*)
|    |    |    |         \--- io.netty:netty-transport:4.2.17.Final (*)
|    |    |    +--- io.netty:netty-transport:4.2.13.Final -> 4.2.17.Final (*)
|    |    |    +--- io.projectreactor:reactor-core:3.6.6 -> 3.8.7
|    |    |    |    +--- org.reactivestreams:reactive-streams:1.0.4
|    |    |    |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|    |    |    \--- io.netty:netty-resolver-dns:4.2.13.Final -> 4.2.17.Final
|    |    |         +--- io.netty:netty-common:4.2.17.Final
|    |    |         +--- io.netty:netty-buffer:4.2.17.Final (*)
|    |    |         +--- io.netty:netty-resolver:4.2.17.Final (*)
|    |    |         +--- io.netty:netty-transport:4.2.17.Final (*)
|    |    |         +--- io.netty:netty-codec-base:4.2.17.Final (*)
|    |    |         +--- io.netty:netty-codec-dns:4.2.17.Final
|    |    |         |    +--- io.netty:netty-common:4.2.17.Final
|    |    |         |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|    |    |         |    +--- io.netty:netty-transport:4.2.17.Final (*)
|    |    |         |    \--- io.netty:netty-codec-base:4.2.17.Final (*)
|    |    |         \--- io.netty:netty-handler:4.2.17.Final (*)
|    |    \--- org.springframework.data:spring-data-redis:4.1.1
|    |         +--- org.springframework.data:spring-data-keyvalue:4.1.1
|    |         |    +--- org.springframework.data:spring-data-commons:4.1.1 (*)
|    |         |    +--- org.springframework:spring-context:7.0.9 (*)
|    |         |    +--- org.springframework:spring-tx:7.0.9 (*)
|    |         |    \--- org.slf4j:slf4j-api:2.0.18
|    |         +--- org.springframework:spring-tx:7.0.9 (*)
|    |         +--- org.springframework:spring-oxm:7.0.9
|    |         |    +--- jakarta.xml.bind:jakarta.xml.bind-api:3.0.1 -> 4.0.5 (*)
|    |         |    +--- org.springframework:spring-beans:7.0.9 (*)
|    |         |    \--- org.springframework:spring-core:7.0.9 (*)
|    |         +--- org.springframework:spring-aop:7.0.9 (*)
|    |         +--- org.springframework:spring-context-support:7.0.9 (*)
|    |         \--- org.slf4j:slf4j-api:2.0.18
|    \--- org.springframework:spring-messaging:7.0.9
|         +--- org.springframework:spring-beans:7.0.9 (*)
|         \--- org.springframework:spring-core:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-flyway -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jdbc:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-flyway:4.1.1
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-jdbc:4.1.1 (*)
|    |    \--- org.flywaydb:flyway-core:12.4.0 (*)
|    \--- org.springframework.boot:spring-boot-jdbc:4.1.1 (*)
+--- org.springframework.boot:spring-boot-starter-graphql -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jackson:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-jackson:4.1.1
|    |         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |         \--- tools.jackson.core:jackson-databind:3.1.5 (*)
|    +--- org.springframework.boot:spring-boot-reactor:4.1.1
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    \--- io.projectreactor:reactor-core:3.8.7 (*)
|    \--- org.springframework.boot:spring-boot-graphql:4.1.1
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         \--- org.springframework.graphql:spring-graphql:2.0.5
|              +--- io.micrometer:context-propagation:1.2.1
|              |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|              +--- com.graphql-java:graphql-java:25.0
|              |    +--- com.graphql-java:java-dataloader:6.0.0
|              |    |    +--- org.reactivestreams:reactive-streams:1.0.3 -> 1.0.4
|              |    |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|              |    +--- org.reactivestreams:reactive-streams:1.0.3 -> 1.0.4
|              |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|              +--- io.projectreactor:reactor-core:3.8.7 (*)
|              \--- org.springframework:spring-context:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-jetty -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jetty-runtime:4.1.1
|    |    +--- org.springframework.boot:spring-boot-jetty:4.1.1
|    |    |    +--- org.eclipse.jetty.compression:jetty-compression-server:12.1.12
|    |    |    |    +--- org.eclipse.jetty:jetty-server:12.1.12
|    |    |    |    |    +--- org.eclipse.jetty:jetty-http:12.1.12
|    |    |    |    |    |    +--- org.eclipse.jetty:jetty-io:12.1.12
|    |    |    |    |    |    |    +--- org.eclipse.jetty:jetty-util:12.1.12
|    |    |    |    |    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    |    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    |    |    |    +--- org.eclipse.jetty:jetty-util:12.1.12 (*)
|    |    |    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    |    |    +--- org.eclipse.jetty:jetty-io:12.1.12 (*)
|    |    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    |    \--- org.eclipse.jetty.compression:jetty-compression-common:12.1.12
|    |    |    |         +--- org.eclipse.jetty:jetty-http:12.1.12 (*)
|    |    |    |         +--- org.eclipse.jetty:jetty-io:12.1.12 (*)
|    |    |    |         \--- org.eclipse.jetty:jetty-util:12.1.12 (*)
|    |    |    +--- org.eclipse.jetty.compression:jetty-compression-gzip:12.1.12
|    |    |    |    \--- org.eclipse.jetty.compression:jetty-compression-common:12.1.12 (*)
|    |    |    +--- org.springframework.boot:spring-boot-web-server:4.1.1
|    |    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    |    \--- org.springframework:spring-web:7.0.9
|    |    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |    |    |         \--- io.micrometer:micrometer-observation:1.16.7 -> 1.17.1 (*)
|    |    |    \--- org.eclipse.jetty.ee11:jetty-ee11-webapp:12.1.12
|    |    |         +--- org.eclipse.jetty:jetty-session:12.1.12
|    |    |         |    +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |         |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         +--- org.eclipse.jetty:jetty-xml:12.1.12
|    |    |         |    +--- org.eclipse.jetty:jetty-util:12.1.12 (*)
|    |    |         |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         +--- org.eclipse.jetty.ee:jetty-ee-webapp:12.1.12
|    |    |         |    +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |         |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         +--- org.eclipse.jetty.ee11:jetty-ee11-servlet:12.1.12
|    |    |         |    +--- jakarta.servlet:jakarta.servlet-api:6.1.0
|    |    |         |    +--- org.eclipse.jetty:jetty-security:12.1.12
|    |    |         |    |    +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |         |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         |    +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |         |    +--- org.eclipse.jetty:jetty-session:12.1.12 (*)
|    |    |         |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    +--- org.springframework.boot:spring-boot-web-server:4.1.1 (*)
|    |    +--- jakarta.servlet:jakarta.servlet-api:6.1.0
|    |    +--- jakarta.websocket:jakarta.websocket-api:2.2.0
|    |    +--- jakarta.websocket:jakarta.websocket-client-api:2.2.0
|    |    +--- org.apache.tomcat.embed:tomcat-embed-el:11.0.24
|    |    +--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-jakarta-server:12.1.12
|    |    |    +--- jakarta.websocket:jakarta.websocket-api:2.2.0
|    |    |    +--- org.eclipse.jetty.ee11:jetty-ee11-annotations:12.1.12
|    |    |    |    +--- jakarta.annotation:jakarta.annotation-api:3.0.0
|    |    |    |    +--- jakarta.servlet:jakarta.servlet-api:6.1.0
|    |    |    |    +--- org.eclipse.jetty:jetty-annotations:12.1.12
|    |    |    |    |    +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |    |    |    +--- org.ow2.asm:asm:9.10.1
|    |    |    |    |    \--- org.ow2.asm:asm-commons:9.10.1
|    |    |    |    |         +--- org.ow2.asm:asm:9.10.1
|    |    |    |    |         \--- org.ow2.asm:asm-tree:9.10.1
|    |    |    |    |              \--- org.ow2.asm:asm:9.10.1
|    |    |    |    +--- org.eclipse.jetty.ee11:jetty-ee11-plus:12.1.12
|    |    |    |    |    +--- jakarta.enterprise:jakarta.enterprise.cdi-api:4.1.0
|    |    |    |    |    |    +--- jakarta.enterprise:jakarta.enterprise.lang-model:4.1.0
|    |    |    |    |    |    +--- jakarta.annotation:jakarta.annotation-api:3.0.0
|    |    |    |    |    |    +--- jakarta.interceptor:jakarta.interceptor-api:2.2.0
|    |    |    |    |    |    |    \--- jakarta.annotation:jakarta.annotation-api:3.0.0
|    |    |    |    |    |    \--- jakarta.inject:jakarta.inject-api:2.0.1
|    |    |    |    |    +--- jakarta.enterprise:jakarta.enterprise.lang-model:4.1.0
|    |    |    |    |    +--- jakarta.interceptor:jakarta.interceptor-api:2.2.0 (*)
|    |    |    |    |    +--- jakarta.transaction:jakarta.transaction-api:2.0.1
|    |    |    |    |    +--- org.eclipse.jetty:jetty-plus:12.1.12
|    |    |    |    |    |    +--- org.eclipse.jetty:jetty-security:12.1.12 (*)
|    |    |    |    |    |    \--- org.eclipse.jetty:jetty-util:12.1.12 (*)
|    |    |    |    |    \--- org.eclipse.jetty.ee11:jetty-ee11-webapp:12.1.12 (*)
|    |    |    |    +--- org.eclipse.jetty.ee11:jetty-ee11-webapp:12.1.12 (*)
|    |    |    |    +--- org.ow2.asm:asm:9.10.1
|    |    |    |    \--- org.ow2.asm:asm-commons:9.10.1 (*)
|    |    |    +--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-jakarta-client:12.1.12
|    |    |    |    +--- jakarta.websocket:jakarta.websocket-api:2.2.0
|    |    |    |    +--- jakarta.websocket:jakarta.websocket-client-api:2.2.0
|    |    |    |    +--- org.eclipse.jetty:jetty-client:12.1.12
|    |    |    |    |    +--- org.eclipse.jetty:jetty-alpn-client:12.1.12
|    |    |    |    |    |    \--- org.eclipse.jetty:jetty-io:12.1.12 (*)
|    |    |    |    |    +--- org.eclipse.jetty:jetty-http:12.1.12 (*)
|    |    |    |    |    +--- org.eclipse.jetty:jetty-io:12.1.12 (*)
|    |    |    |    |    \--- org.eclipse.jetty.compression:jetty-compression-gzip:12.1.12 (*)
|    |    |    |    +--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-jakarta-common:12.1.12
|    |    |    |    |    +--- jakarta.websocket:jakarta.websocket-api:2.2.0
|    |    |    |    |    +--- jakarta.websocket:jakarta.websocket-client-api:2.2.0
|    |    |    |    |    \--- org.eclipse.jetty.websocket:jetty-websocket-core-client:12.1.12
|    |    |    |    |         +--- org.eclipse.jetty:jetty-client:12.1.12 (*)
|    |    |    |    |         \--- org.eclipse.jetty.websocket:jetty-websocket-core-common:12.1.12
|    |    |    |    |              +--- org.eclipse.jetty:jetty-http:12.1.12 (*)
|    |    |    |    |              \--- org.eclipse.jetty:jetty-io:12.1.12 (*)
|    |    |    |    \--- org.eclipse.jetty.websocket:jetty-websocket-core-client:12.1.12 (*)
|    |    |    \--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-servlet:12.1.12
|    |    |         +--- org.eclipse.jetty.ee11:jetty-ee11-servlet:12.1.12 (*)
|    |    |         \--- org.eclipse.jetty.websocket:jetty-websocket-core-server:12.1.12
|    |    |              +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |              \--- org.eclipse.jetty.websocket:jetty-websocket-core-common:12.1.12 (*)
|    |    \--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-jetty-server:12.1.12
|    |         +--- jakarta.servlet:jakarta.servlet-api:6.1.0
|    |         +--- org.eclipse.jetty.ee11:jetty-ee11-annotations:12.1.12 (*)
|    |         +--- org.eclipse.jetty.ee11:jetty-ee11-servlet:12.1.12 (*)
|    |         +--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-servlet:12.1.12 (*)
|    |         +--- org.eclipse.jetty.websocket:jetty-websocket-jetty-api:12.1.12
|    |         +--- org.eclipse.jetty.websocket:jetty-websocket-jetty-common:12.1.12
|    |         |    +--- org.eclipse.jetty.websocket:jetty-websocket-core-common:12.1.12 (*)
|    |         |    \--- org.eclipse.jetty.websocket:jetty-websocket-jetty-api:12.1.12
|    |         \--- org.eclipse.jetty.websocket:jetty-websocket-jetty-server:12.1.12
|    |              +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |              +--- org.eclipse.jetty.websocket:jetty-websocket-core-server:12.1.12 (*)
|    |              \--- org.eclipse.jetty.websocket:jetty-websocket-jetty-common:12.1.12 (*)
|    +--- org.springframework.boot:spring-boot-jetty:4.1.1 (*)
|    +--- org.slf4j:slf4j-api:2.0.18
|    \--- jakarta.annotation:jakarta.annotation-api:3.0.0
+--- org.springframework.boot:spring-boot-starter-kafka -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-kafka:4.1.1
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         +--- org.springframework.boot:spring-boot-transaction:4.1.1 (*)
|         \--- org.springframework.kafka:spring-kafka:4.1.1
|              +--- org.springframework:spring-context:7.0.9 (*)
|              +--- org.springframework:spring-messaging:7.0.9 (*)
|              +--- org.springframework:spring-tx:7.0.9 (*)
|              +--- org.apache.kafka:kafka-clients:4.2.1 (*)
|              \--- io.micrometer:micrometer-observation:1.17.1 (*)
+--- org.springframework.boot:spring-boot-starter-oauth2-client -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-security:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-security:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    +--- org.springframework.security:spring-security-config:7.1.1
|    |    |    |    +--- org.springframework.security:spring-security-core:7.1.1
|    |    |    |    |    +--- org.springframework.security:spring-security-crypto:7.1.1
|    |    |    |    |    +--- org.springframework:spring-aop:7.0.9 (*)
|    |    |    |    |    +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    |    |    +--- org.springframework:spring-context:7.0.9 (*)
|    |    |    |    |    +--- org.springframework:spring-core:7.0.9 (*)
|    |    |    |    |    +--- org.springframework:spring-expression:7.0.9 (*)
|    |    |    |    |    \--- io.micrometer:micrometer-observation:1.17.1 (*)
|    |    |    |    +--- org.springframework:spring-aop:7.0.9 (*)
|    |    |    |    +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    |    +--- org.springframework:spring-context:7.0.9 (*)
|    |    |    |    \--- org.springframework:spring-core:7.0.9 (*)
|    |    |    \--- org.springframework.security:spring-security-web:7.1.1
|    |    |         +--- org.springframework.security:spring-security-core:7.1.1 (*)
|    |    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |    |         +--- org.springframework:spring-aop:7.0.9 (*)
|    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |         +--- org.springframework:spring-context:7.0.9 (*)
|    |    |         +--- org.springframework:spring-expression:7.0.9 (*)
|    |    |         \--- org.springframework:spring-web:7.0.9 (*)
|    |    \--- org.springframework:spring-aop:7.0.9 (*)
|    +--- org.springframework.boot:spring-boot-security-oauth2-client:4.1.1
|    |    +--- org.springframework.boot:spring-boot-security:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    \--- org.springframework.security:spring-security-oauth2-client:7.1.1
|    |         +--- org.springframework.security:spring-security-core:7.1.1 (*)
|    |         +--- org.springframework.security:spring-security-oauth2-core:7.1.1
|    |         |    +--- org.springframework.security:spring-security-core:7.1.1 (*)
|    |         |    +--- org.springframework:spring-core:7.0.9 (*)
|    |         |    \--- org.springframework:spring-web:7.0.9 (*)
|    |         +--- org.springframework.security:spring-security-web:7.1.1 (*)
|    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |         \--- com.nimbusds:oauth2-oidc-sdk:11.38.2
|    |              +--- com.github.stephenc.jcip:jcip-annotations:1.0-1
|    |              +--- com.nimbusds:content-type:2.3
|    |              +--- net.minidev:json-smart:2.6.0
|    |              |    \--- net.minidev:accessors-smart:2.6.0
|    |              |         \--- org.ow2.asm:asm:9.7.1 -> 9.10.1
|    |              +--- com.nimbusds:lang-tag:1.7
|    |              \--- com.nimbusds:nimbus-jose-jwt:10.9.1
|    \--- org.springframework.security:spring-security-oauth2-jose:7.1.1
|         +--- org.springframework.security:spring-security-core:7.1.1 (*)
|         +--- org.springframework.security:spring-security-oauth2-core:7.1.1 (*)
|         +--- org.springframework:spring-core:7.0.9 (*)
|         \--- com.nimbusds:nimbus-jose-jwt:10.9.1
+--- org.springframework.boot:spring-boot-starter-oauth2-resource-server -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-security:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-security-oauth2-resource-server:4.1.1
|         +--- org.springframework.boot:spring-boot-security:4.1.1 (*)
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         +--- org.springframework.security:spring-security-oauth2-jose:7.1.1 (*)
|         \--- org.springframework.security:spring-security-oauth2-resource-server:7.1.1
|              +--- org.springframework.security:spring-security-core:7.1.1 (*)
|              +--- org.springframework.security:spring-security-oauth2-core:7.1.1 (*)
|              +--- org.springframework.security:spring-security-web:7.1.1 (*)
|              \--- org.springframework:spring-core:7.0.9 (*)
+--- org.springframework.boot:spring-boot-restclient -> 4.1.1
|    +--- org.springframework.boot:spring-boot-http-converter:4.1.1
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    \--- org.springframework:spring-web:7.0.9 (*)
|    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-http-client:4.1.1
|         +--- org.springframework.boot:spring-boot-http-converter:4.1.1 (*)
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         \--- org.springframework:spring-web:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-validation -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-validation:4.1.1
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         +--- org.apache.tomcat.embed:tomcat-embed-el:11.0.24
|         \--- org.hibernate.validator:hibernate-validator:9.1.3.Final
|              +--- jakarta.validation:jakarta.validation-api:3.1.1
|              +--- org.jboss.logging:jboss-logging:3.6.3.Final
|              \--- com.fasterxml:classmate:1.7.1 -> 1.7.3
+--- org.springframework.boot:spring-boot-starter-web -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter-jackson:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-http-converter:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-webmvc:4.1.1
|         +--- org.springframework.boot:spring-boot-http-converter:4.1.1 (*)
|         +--- org.springframework.boot:spring-boot-servlet:4.1.1
|         |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         |    \--- org.springframework:spring-web:7.0.9 (*)
|         +--- org.springframework:spring-web:7.0.9 (*)
|         \--- org.springframework:spring-webmvc:7.0.9
|              +--- org.springframework:spring-aop:7.0.9 (*)
|              +--- org.springframework:spring-beans:7.0.9 (*)
|              +--- org.springframework:spring-context:7.0.9 (*)
|              +--- org.springframework:spring-core:7.0.9 (*)
|              +--- org.springframework:spring-expression:7.0.9 (*)
|              \--- org.springframework:spring-web:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-webclient -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jackson:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-reactor:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-webclient:4.1.1
|    |    +--- org.springframework.boot:spring-boot-http-codec:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    \--- org.springframework:spring-web:7.0.9 (*)
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-http-client:4.1.1 (*)
|    |    \--- org.springframework:spring-webflux:7.0.9
|    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |         +--- org.springframework:spring-web:7.0.9 (*)
|    |         \--- io.projectreactor:reactor-core:3.8.7 (*)
|    \--- io.projectreactor.netty:reactor-netty-http:1.3.7
|         +--- io.netty:netty-codec-http:4.2.17.Final
|         |    +--- io.netty:netty-common:4.2.17.Final
|         |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-compression:4.2.17.Final
|         |    |    +--- io.netty:netty-common:4.2.17.Final
|         |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    |    \--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    \--- io.netty:netty-handler:4.2.17.Final (*)
|         +--- io.netty:netty-codec-http2:4.2.17.Final
|         |    +--- io.netty:netty-common:4.2.17.Final
|         |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    +--- io.netty:netty-handler:4.2.17.Final (*)
|         |    \--- io.netty:netty-codec-http:4.2.17.Final (*)
|         +--- io.netty:netty-codec-http3:4.2.17.Final
|         |    +--- io.netty:netty-common:4.2.17.Final
|         |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-http:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-compression:4.2.17.Final (*)
|         |    +--- io.netty:netty-handler:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport-native-unix-common:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    +--- io.netty:netty-resolver:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-classes-quic:4.2.17.Final
|         |    |    +--- io.netty:netty-common:4.2.17.Final
|         |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    |    \--- io.netty:netty-handler:4.2.17.Final (*)
|         |    \--- io.netty:netty-codec-native-quic:4.2.17.Final
|         |         \--- io.netty:netty-codec-classes-quic:4.2.17.Final (*)
|         +--- io.netty:netty-resolver-dns:4.2.17.Final (*)
|         +--- io.netty:netty-resolver-dns-native-macos:4.2.17.Final
|         |    \--- io.netty:netty-resolver-dns-classes-macos:4.2.17.Final
|         |         +--- io.netty:netty-common:4.2.17.Final
|         |         +--- io.netty:netty-resolver-dns:4.2.17.Final (*)
|         |         \--- io.netty:netty-transport-native-unix-common:4.2.17.Final (*)
|         +--- io.netty:netty-transport-native-epoll:4.2.17.Final
|         |    +--- io.netty:netty-common:4.2.17.Final
|         |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport-native-unix-common:4.2.17.Final (*)
|         |    \--- io.netty:netty-transport-classes-epoll:4.2.17.Final
|         |         +--- io.netty:netty-common:4.2.17.Final
|         |         +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |         +--- io.netty:netty-transport:4.2.17.Final (*)
|         |         \--- io.netty:netty-transport-native-unix-common:4.2.17.Final (*)
|         +--- io.projectreactor.netty:reactor-netty-core:1.3.7
|         |    +--- io.netty:netty-handler:4.2.17.Final (*)
|         |    +--- io.netty:netty-handler-proxy:4.2.17.Final
|         |    |    +--- io.netty:netty-common:4.2.17.Final
|         |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-codec-socks:4.2.17.Final
|         |    |    |    +--- io.netty:netty-common:4.2.17.Final
|         |    |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    |    |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    |    |    \--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-codec-http:4.2.17.Final (*)
|         |    |    \--- io.netty:netty-handler:4.2.17.Final (*)
|         |    +--- io.netty:netty-resolver-dns:4.2.17.Final (*)
|         |    +--- io.netty:netty-resolver-dns-native-macos:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport-native-epoll:4.2.17.Final (*)
|         |    +--- io.projectreactor:reactor-core:3.8.7 (*)
|         |    \--- org.jspecify:jspecify:1.0.1
|         +--- io.projectreactor:reactor-core:3.8.7 (*)
|         \--- org.jspecify:jspecify:1.0.1
+--- org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.0
|    +--- org.springdoc:springdoc-openapi-starter-webmvc-api:3.1.0
|    |    +--- org.springdoc:springdoc-openapi-starter-common:3.1.0
|    |    |    +--- org.springframework.boot:spring-boot-starter:4.1.0 -> 4.1.1 (*)
|    |    |    +--- org.springframework.boot:spring-boot-autoconfigure:4.1.0 -> 4.1.1 (*)
|    |    |    +--- org.springframework.boot:spring-boot-validation:4.1.0 -> 4.1.1 (*)
|    |    |    +--- io.swagger.core.v3:swagger-core-jakarta:2.2.52
|    |    |    |    +--- org.apache.commons:commons-lang3:3.20.0
|    |    |    |    +--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    |    +--- io.swagger.core.v3:swagger-annotations-jakarta:2.2.52
|    |    |    |    +--- io.swagger.core.v3:swagger-models-jakarta:2.2.52
|    |    |    |    |    \--- com.fasterxml.jackson.core:jackson-annotations:2.21
|    |    |    |    +--- org.yaml:snakeyaml:2.6
|    |    |    |    +--- jakarta.xml.bind:jakarta.xml.bind-api:3.0.1 -> 4.0.5 (*)
|    |    |    |    +--- jakarta.validation:jakarta.validation-api:3.0.2 -> 3.1.1
|    |    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|    |    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.1 -> 2.21.5 (*)
|    |    |    |    +--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.22.0 -> 2.21.5
|    |    |    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.5 (*)
|    |    |    |    |    +--- org.yaml:snakeyaml:2.5 -> 2.6
|    |    |    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (*)
|    |    |    |    |    \--- com.fasterxml.jackson:jackson-bom:2.21.5 (*)
|    |    |    |    \--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.22.0 -> 2.21.5
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (*)
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-databind:2.21.5 (*)
|    |    |    |         \--- com.fasterxml.jackson:jackson-bom:2.21.5 (*)
|    |    |    \--- org.springframework.boot:spring-boot-jackson:4.1.0 -> 4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-webmvc:4.1.0 -> 4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-web-server:4.1.0 -> 4.1.1 (*)
|    +--- org.webjars:swagger-ui:5.32.11
|    \--- org.webjars:webjars-locator-lite:1.1.3 -> 1.1.4
|         \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
\--- at.yawk.lz4:lz4-java:1.11.1 (c)

runtimeElements - Runtime elements for the 'main' feature. (n)
No dependencies

runtimeElements-published (n)
No dependencies

runtimeOnly - Runtime only dependencies for 'main'. (n)
No dependencies

testAndDevelopmentOnly - Configuration for test and development-only dependencies such as Spring Boot's DevTools.
No dependencies

testAnnotationProcessor - Annotation processors and their dependencies for source set 'test'.
No dependencies

testApi - API dependencies for 'test'. (n)
No dependencies

testCompileClasspath - Compile classpath for 'test'.
+--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0
|    +--- org.jetbrains:annotations:13.0 -> 23.0.0
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0 -> 2.4.0 (c)
|    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.0 -> 2.4.0 (c)
+--- no.nav.pdl.libs:contract-pdl-avro:18
|    \--- io.confluent:kafka-avro-serializer:7.7.1 -> 8.3.1
|         +--- org.apache.avro:avro:1.12.1
|         |    +--- com.fasterxml.jackson.core:jackson-core:2.20.0 -> 2.21.5
|         |    |    \--- com.fasterxml.jackson:jackson-bom:2.21.5
|         |    |         +--- com.fasterxml.jackson.core:jackson-annotations:2.21 (c)
|         |    |         +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (c)
|         |    |         +--- com.fasterxml.jackson.core:jackson-databind:2.21.5 (c)
|         |    |         +--- com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.21.5 (c)
|         |    |         +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.21.5 (c)
|         |    |         +--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.21.5 (c)
|         |    |         \--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.21.5 (c)
|         |    +--- com.fasterxml.jackson.core:jackson-databind:2.20.0 -> 2.21.5
|         |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|         |    |    +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (*)
|         |    |    \--- com.fasterxml.jackson:jackson-bom:2.21.5 (*)
|         |    +--- org.apache.commons:commons-compress:1.28.0
|         |    |    +--- commons-codec:commons-codec:1.19.0 -> 1.21.0
|         |    |    +--- commons-io:commons-io:2.20.0
|         |    |    \--- org.apache.commons:commons-lang3:3.18.0 -> 3.20.0
|         |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|         +--- org.apache.commons:commons-compress:1.26.1 -> 1.28.0 (*)
|         +--- io.confluent:kafka-schema-serializer:8.3.1
|         |    +--- io.confluent:kafka-schema-registry-client:8.3.1
|         |    |    +--- io.confluent:kafka-avro-types:8.3.1
|         |    |    |    +--- io.confluent:kafka-schema-types:8.3.1
|         |    |    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.2 -> 2.21.5 (*)
|         |    |    |    |    \--- io.confluent:common-utils:8.3.1
|         |    |    |    |         \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    |    |    +--- org.apache.avro:avro:1.12.1 (*)
|         |    |    |    \--- io.confluent:common-utils:8.3.1 (*)
|         |    |    +--- org.apache.kafka:kafka-clients:8.3.1-ccs -> 4.2.1
|         |    |    +--- org.apache.avro:avro:1.12.1 (*)
|         |    |    +--- org.apache.commons:commons-compress:1.26.1 -> 1.28.0 (*)
|         |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.2 -> 2.21.5 (*)
|         |    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.21.2 -> 2.21.5
|         |    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (*)
|         |    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.5 (*)
|         |    |    |    \--- com.fasterxml.jackson:jackson-bom:2.21.5 (*)
|         |    |    +--- org.yaml:snakeyaml:2.0 -> 2.6
|         |    |    +--- io.swagger.core.v3:swagger-annotations-jakarta:2.2.42 -> 2.2.52
|         |    |    +--- com.google.guava:guava:32.0.1-jre
|         |    |    |    +--- com.google.guava:failureaccess:1.0.1
|         |    |    |    +--- com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava
|         |    |    |    +--- com.google.code.findbugs:jsr305:3.0.2
|         |    |    |    +--- org.checkerframework:checker-qual:3.33.0
|         |    |    |    +--- com.google.errorprone:error_prone_annotations:2.18.0 -> 2.49.0
|         |    |    |    \--- com.google.j2objc:j2objc-annotations:2.8
|         |    |    +--- org.apache.httpcomponents.client5:httpclient5:5.5 -> 5.6.4
|         |    |    |    +--- org.apache.httpcomponents.core5:httpcore5:5.4.3
|         |    |    |    +--- org.apache.httpcomponents.core5:httpcore5-h2:5.4.3
|         |    |    |    |    \--- org.apache.httpcomponents.core5:httpcore5:5.4.3
|         |    |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    |    \--- io.confluent:common-utils:8.3.1 (*)
|         |    +--- com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.21.2 -> 2.21.5
|         |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.5 (*)
|         |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|         |    |    +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (*)
|         |    |    \--- com.fasterxml.jackson:jackson-bom:2.21.5 (*)
|         |    \--- io.confluent:common-utils:8.3.1 (*)
|         +--- io.confluent:kafka-schema-registry-client:8.3.1 (*)
|         +--- com.google.guava:guava:32.0.1-jre (*)
|         +--- io.confluent:logredactor:1.0.18
|         |    +--- com.google.re2j:re2j:1.6
|         |    +--- io.confluent:logredactor-metrics:1.0.18
|         |    +--- com.eclipsesource.minimal-json:minimal-json:0.9.5
|         |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         \--- io.confluent:common-utils:8.3.1 (*)
+--- no.nav.boot:boot-conditionals:6.0.7
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.4.0
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.4.0
|    |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:2.4.0
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
|    +--- ch.qos.logback:logback-core:1.5.34 -> 1.5.38
|    +--- org.slf4j:slf4j-api:2.0.18
|    \--- org.springframework.boot:spring-boot-autoconfigure:4.1.0 -> 4.1.1
|         \--- org.springframework.boot:spring-boot:4.1.1
|              +--- org.springframework:spring-core:7.0.9
|              |    +--- commons-logging:commons-logging:1.3.5 -> 1.3.6
|              |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|              \--- org.springframework:spring-context:7.0.9
|                   +--- org.springframework:spring-aop:7.0.9
|                   |    +--- org.springframework:spring-beans:7.0.9
|                   |    |    \--- org.springframework:spring-core:7.0.9 (*)
|                   |    \--- org.springframework:spring-core:7.0.9 (*)
|                   +--- org.springframework:spring-beans:7.0.9 (*)
|                   +--- org.springframework:spring-core:7.0.9 (*)
|                   +--- org.springframework:spring-expression:7.0.9
|                   |    \--- org.springframework:spring-core:7.0.9 (*)
|                   \--- io.micrometer:micrometer-observation:1.16.7 -> 1.17.1
|                        +--- org.jspecify:jspecify:1.0.1
|                        \--- io.micrometer:micrometer-commons:1.17.1
|                             \--- org.jspecify:jspecify:1.0.1
+--- io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations -> 2.30.0
|    \--- io.opentelemetry:opentelemetry-api:1.64.0
|         \--- io.opentelemetry:opentelemetry-context:1.64.0
|              \--- io.opentelemetry:opentelemetry-common:1.64.0
+--- io.opentelemetry.instrumentation:opentelemetry-logback-mdc-1.0:2.30.0-alpha
|    +--- io.opentelemetry.instrumentation:opentelemetry-instrumentation-api:2.30.0
|    |    \--- io.opentelemetry:opentelemetry-api:1.64.0 (*)
|    +--- io.opentelemetry.instrumentation:opentelemetry-instrumentation-api-incubator:2.30.0-alpha
|    |    +--- io.opentelemetry.semconv:opentelemetry-semconv:1.43.0
|    |    +--- io.opentelemetry.instrumentation:opentelemetry-instrumentation-api:2.30.0 (*)
|    |    \--- io.opentelemetry:opentelemetry-api-incubator:1.64.0-alpha
|    |         \--- io.opentelemetry:opentelemetry-api:1.64.0 (*)
|    \--- io.opentelemetry:opentelemetry-api:1.64.0 (*)
+--- io.micrometer:micrometer-registry-prometheus -> 1.17.1
|    +--- org.jspecify:jspecify:1.0.1
|    +--- io.micrometer:micrometer-core:1.17.1
|    |    +--- org.jspecify:jspecify:1.0.1
|    |    +--- io.micrometer:micrometer-commons:1.17.1 (*)
|    |    \--- io.micrometer:micrometer-observation:1.17.1 (*)
|    +--- io.prometheus:prometheus-metrics-core:1.7.0
|    |    +--- io.prometheus:prometheus-metrics-model:1.7.0
|    |    |    \--- io.prometheus:prometheus-metrics-config:1.7.0
|    |    \--- io.prometheus:prometheus-metrics-config:1.7.0
|    \--- io.prometheus:prometheus-metrics-tracer-common:1.7.0
+--- com.slack.api:slack-api-client-kotlin-extension:1.49.0
|    +--- com.slack.api:slack-api-model-kotlin-extension:1.49.0
|    |    +--- com.slack.api:slack-api-model:1.49.0
|    |    |    \--- com.google.code.gson:gson:2.12.1 -> 2.13.2
|    |    |         \--- com.google.errorprone:error_prone_annotations:2.41.0 -> 2.49.0
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.24 -> 2.4.0 (*)
|    +--- com.slack.api:slack-api-client:1.49.0
|    |    +--- com.slack.api:slack-api-model:1.49.0 (*)
|    |    +--- com.squareup.okhttp3:okhttp:4.12.0 -> 5.4.0
|    |    |    \--- com.squareup.okhttp3:okhttp-jvm:5.4.0
|    |    |         +--- org.jetbrains.kotlin:kotlin-stdlib:2.1.21 -> 2.4.0 (*)
|    |    |         +--- com.squareup.okio:okio:3.17.0
|    |    |         |    \--- com.squareup.okio:okio-jvm:3.17.0
|    |    |         |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.1.21 -> 2.4.0 (*)
|    |    |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.2.21 -> 2.4.0 (*)
|    |    +--- com.google.code.gson:gson:2.12.1 -> 2.13.2 (*)
|    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|    \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.24 -> 2.4.0 (*)
+--- org.apache.commons:commons-pool2:2.13.1
+--- io.confluent:kafka-avro-serializer:8.3.1 (*)
+--- org.flywaydb:flyway-database-postgresql -> 12.4.0
|    \--- org.flywaydb:flyway-core:12.4.0
|         \--- tools.jackson.core:jackson-databind:3.1.1 -> 3.1.5
|              +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|              +--- tools.jackson.core:jackson-core:3.1.5
|              |    \--- tools.jackson:jackson-bom:3.1.5
|              |         +--- com.fasterxml.jackson.core:jackson-annotations:2.21 (c)
|              |         +--- tools.jackson.core:jackson-core:3.1.5 (c)
|              |         +--- tools.jackson.core:jackson-databind:3.1.5 (c)
|              |         \--- tools.jackson.module:jackson-module-kotlin:3.1.5 (c)
|              \--- tools.jackson:jackson-bom:3.1.5 (*)
+--- org.hibernate.orm:hibernate-micrometer -> 7.4.5.Final
|    \--- org.hibernate.orm:hibernate-platform:7.4.5.Final
|         +--- org.hibernate.orm:hibernate-micrometer:7.4.5.Final (c)
|         +--- org.hibernate.orm:hibernate-core:7.4.5.Final (c)
|         +--- jakarta.persistence:jakarta.persistence-api:3.2.0 (c)
|         \--- jakarta.transaction:jakarta.transaction-api:2.0.1 (c)
+--- tools.jackson.module:jackson-module-kotlin -> 3.1.5
|    +--- tools.jackson.core:jackson-databind:3.1.5 (*)
|    +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|    +--- org.jetbrains.kotlin:kotlin-reflect:2.1.21 -> 2.4.0 (*)
|    \--- tools.jackson:jackson-bom:3.1.5 (*)
+--- org.zalando:logbook-spring-boot-starter:4.1.0
|    +--- org.zalando:logbook-spring-boot-autoconfigure:4.1.0
|    |    +--- org.zalando:logbook-core:4.1.0
|    |    |    +--- org.zalando:logbook-api:4.1.0
|    |    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    |    +--- org.zalando:faux-pas:0.9.0
|    |    |    |    |    +--- com.google.code.findbugs:jsr305:3.0.2
|    |    |    |    |    \--- org.slf4j:slf4j-api:1.7.30 -> 2.0.18
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    |    +--- org.zalando:logbook-common:4.1.0
|    |    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.zalando:logbook-json:4.1.0
|    |    |    +--- org.zalando:logbook-api:4.1.0 (*)
|    |    |    +--- org.zalando:logbook-common:4.1.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.22 -> 2.21
|    |    |    +--- com.jayway.jsonpath:json-path:3.0.0 -> 2.10.0
|    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.zalando:logbook-json-jackson2:4.1.0
|    |    |    +--- org.zalando:logbook-api:4.1.0 (*)
|    |    |    +--- org.zalando:logbook-common:4.1.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.22 -> 2.21
|    |    |    +--- com.jayway.jsonpath:json-path:3.0.0 -> 2.10.0
|    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.zalando:logbook-spring:4.1.0
|    |    |    +--- org.zalando:logbook-core:4.1.0 (*)
|    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.zalando:logbook-servlet:4.1.0
|    |    |    +--- org.zalando:logbook-api:4.1.0 (*)
|    |    |    +--- org.zalando:logbook-core:4.1.0 (*)
|    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    +--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    +--- org.zalando:logbook-spring-boot-ecs-autoconfigure:4.1.0
|    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    +--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    +--- org.apiguardian:apiguardian-api:1.1.2
|    +--- org.zalando:faux-pas:0.9.0 (*)
|    \--- org.slf4j:slf4j-api:2.0.18
+--- net.logstash.logback:logstash-logback-encoder:9.0
|    \--- tools.jackson.core:jackson-databind:3.0.1 -> 3.1.5 (*)
+--- org.postgresql:postgresql -> 42.7.13
+--- org.springframework.boot:spring-boot-starter-actuator -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter-logging:4.1.1
|    |    |    +--- ch.qos.logback:logback-classic:1.5.38
|    |    |    |    +--- ch.qos.logback:logback-core:1.5.38
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    +--- org.apache.logging.log4j:log4j-to-slf4j:2.25.5
|    |    |    |    +--- org.apache.logging.log4j:log4j-api:2.25.5
|    |    |    |    |    +--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|    |    |    |    |    +--- biz.aQute.bnd:biz.aQute.bnd.annotation:7.1.0
|    |    |    |    |    |    +--- org.osgi:org.osgi.resource:1.0.0
|    |    |    |    |    |    \--- org.osgi:org.osgi.service.serviceloader:1.0.0
|    |    |    |    |    +--- com.google.errorprone:error_prone_annotations:2.38.0 -> 2.49.0
|    |    |    |    |    +--- org.osgi:org.osgi.annotation.bundle:2.0.0
|    |    |    |    |    |    \--- org.osgi:org.osgi.annotation.versioning:1.1.2
|    |    |    |    |    \--- org.osgi:org.osgi.annotation.versioning:1.1.2
|    |    |    |    +--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    |    +--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|    |    |    |    +--- biz.aQute.bnd:biz.aQute.bnd.annotation:7.1.0 (*)
|    |    |    |    +--- com.google.errorprone:error_prone_annotations:2.38.0 -> 2.49.0
|    |    |    |    +--- org.osgi:org.osgi.annotation.bundle:2.0.0 (*)
|    |    |    |    \--- org.osgi:org.osgi.annotation.versioning:1.1.2
|    |    |    \--- org.slf4j:jul-to-slf4j:2.0.18
|    |    |         \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.springframework.boot:spring-boot-autoconfigure:4.1.1 (*)
|    |    +--- jakarta.annotation:jakarta.annotation-api:3.0.0
|    |    \--- org.yaml:snakeyaml:2.6
|    +--- org.springframework.boot:spring-boot-starter-micrometer-metrics:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-micrometer-metrics:4.1.1
|    |         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |         +--- org.springframework.boot:spring-boot-micrometer-observation:4.1.1
|    |         |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |         |    \--- io.micrometer:micrometer-observation:1.17.1 (*)
|    |         \--- io.micrometer:micrometer-core:1.17.1 (*)
|    +--- org.springframework.boot:spring-boot-actuator-autoconfigure:4.1.1
|    |    +--- org.springframework.boot:spring-boot-autoconfigure:4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-actuator:4.1.1
|    |         \--- org.springframework.boot:spring-boot:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-health:4.1.1
|    |    \--- org.springframework.boot:spring-boot:4.1.1 (*)
|    +--- io.micrometer:micrometer-observation:1.17.1 (*)
|    \--- io.micrometer:micrometer-jakarta9:1.17.1
|         +--- org.jspecify:jspecify:1.0.1
|         +--- io.micrometer:micrometer-core:1.17.1 (*)
|         +--- io.micrometer:micrometer-commons:1.17.1 (*)
|         \--- io.micrometer:micrometer-observation:1.17.1 (*)
+--- org.springframework.boot:spring-boot-starter-cache -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-cache:4.1.1
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         \--- org.springframework:spring-context-support:7.0.9
|              +--- org.springframework:spring-beans:7.0.9 (*)
|              +--- org.springframework:spring-context:7.0.9 (*)
|              \--- org.springframework:spring-core:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-data-jpa -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jdbc:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-jdbc:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    +--- org.springframework.boot:spring-boot-sql:4.1.1
|    |    |    |    \--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    +--- org.springframework.boot:spring-boot-transaction:4.1.1
|    |    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    |    +--- org.springframework.boot:spring-boot-persistence:4.1.1
|    |    |    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    |    |    \--- org.springframework:spring-tx:7.0.9
|    |    |    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    |    |         \--- org.springframework:spring-core:7.0.9 (*)
|    |    |    |    \--- org.springframework:spring-tx:7.0.9 (*)
|    |    |    \--- org.springframework:spring-jdbc:7.0.9
|    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |    |         \--- org.springframework:spring-tx:7.0.9 (*)
|    |    \--- com.zaxxer:HikariCP:7.0.2
|    |         \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    +--- org.springframework.boot:spring-boot-data-jpa:4.1.1
|    |    +--- org.springframework.boot:spring-boot-data-commons:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    +--- org.springframework.boot:spring-boot-persistence:4.1.1 (*)
|    |    |    \--- org.springframework.data:spring-data-commons:4.1.1
|    |    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |         \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.springframework.boot:spring-boot-hibernate:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot-jpa:4.1.1
|    |    |    |    +--- org.springframework.boot:spring-boot-jdbc:4.1.1 (*)
|    |    |    |    +--- org.springframework.boot:spring-boot-transaction:4.1.1 (*)
|    |    |    |    +--- jakarta.persistence:jakarta.persistence-api:3.2.0
|    |    |    |    \--- org.springframework:spring-orm:7.0.9
|    |    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |    |    |         +--- org.springframework:spring-jdbc:7.0.9 (*)
|    |    |    |         \--- org.springframework:spring-tx:7.0.9 (*)
|    |    |    +--- org.hibernate.orm:hibernate-core:7.4.5.Final
|    |    |    |    +--- org.hibernate.orm:hibernate-platform:7.4.5.Final (*)
|    |    |    |    +--- jakarta.persistence:jakarta.persistence-api:3.2.0
|    |    |    |    \--- jakarta.transaction:jakarta.transaction-api:2.0.1
|    |    |    \--- org.springframework:spring-orm:7.0.9 (*)
|    |    +--- org.springframework.data:spring-data-jpa:4.1.1
|    |    |    +--- org.springframework.data:spring-data-commons:4.1.1 (*)
|    |    |    +--- org.springframework:spring-orm:7.0.9 (*)
|    |    |    +--- org.springframework:spring-context:7.0.9 (*)
|    |    |    +--- org.springframework:spring-aop:7.0.9 (*)
|    |    |    +--- org.springframework:spring-tx:7.0.9 (*)
|    |    |    +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    +--- org.springframework:spring-core:7.0.9 (*)
|    |    |    +--- org.antlr:antlr4-runtime:4.13.2
|    |    |    +--- jakarta.annotation:jakarta.annotation-api:2.0.0 -> 3.0.0
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    \--- org.springframework:spring-aspects:7.0.9
|    |         \--- org.aspectj:aspectjweaver:1.9.25 -> 1.9.25.1
|    \--- org.springframework.boot:spring-boot-jdbc:4.1.1 (*)
+--- org.springframework.boot:spring-boot-starter-data-redis -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-data-redis:4.1.1
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-data-commons:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-transaction:4.1.1 (*)
|    |    +--- io.lettuce:lettuce-core:7.5.2.RELEASE
|    |    |    +--- redis.clients.authentication:redis-authx-core:0.1.1-beta2
|    |    |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|    |    |    +--- io.netty:netty-common:4.2.13.Final -> 4.2.17.Final
|    |    |    +--- io.netty:netty-handler:4.2.13.Final -> 4.2.17.Final
|    |    |    |    +--- io.netty:netty-common:4.2.17.Final
|    |    |    |    +--- io.netty:netty-resolver:4.2.17.Final
|    |    |    |    |    \--- io.netty:netty-common:4.2.17.Final
|    |    |    |    +--- io.netty:netty-buffer:4.2.17.Final
|    |    |    |    |    \--- io.netty:netty-common:4.2.17.Final
|    |    |    |    +--- io.netty:netty-transport:4.2.17.Final
|    |    |    |    |    +--- io.netty:netty-common:4.2.17.Final
|    |    |    |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|    |    |    |    |    \--- io.netty:netty-resolver:4.2.17.Final (*)
|    |    |    |    +--- io.netty:netty-transport-native-unix-common:4.2.17.Final
|    |    |    |    |    +--- io.netty:netty-common:4.2.17.Final
|    |    |    |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|    |    |    |    |    \--- io.netty:netty-transport:4.2.17.Final (*)
|    |    |    |    \--- io.netty:netty-codec-base:4.2.17.Final
|    |    |    |         +--- io.netty:netty-common:4.2.17.Final
|    |    |    |         +--- io.netty:netty-buffer:4.2.17.Final (*)
|    |    |    |         \--- io.netty:netty-transport:4.2.17.Final (*)
|    |    |    +--- io.netty:netty-transport:4.2.13.Final -> 4.2.17.Final (*)
|    |    |    +--- io.projectreactor:reactor-core:3.6.6 -> 3.8.7
|    |    |    |    +--- org.reactivestreams:reactive-streams:1.0.4
|    |    |    |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|    |    |    \--- io.netty:netty-resolver-dns:4.2.13.Final -> 4.2.17.Final
|    |    |         +--- io.netty:netty-common:4.2.17.Final
|    |    |         +--- io.netty:netty-buffer:4.2.17.Final (*)
|    |    |         +--- io.netty:netty-resolver:4.2.17.Final (*)
|    |    |         +--- io.netty:netty-transport:4.2.17.Final (*)
|    |    |         +--- io.netty:netty-codec-base:4.2.17.Final (*)
|    |    |         +--- io.netty:netty-codec-dns:4.2.17.Final
|    |    |         |    +--- io.netty:netty-common:4.2.17.Final
|    |    |         |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|    |    |         |    +--- io.netty:netty-transport:4.2.17.Final (*)
|    |    |         |    \--- io.netty:netty-codec-base:4.2.17.Final (*)
|    |    |         \--- io.netty:netty-handler:4.2.17.Final (*)
|    |    \--- org.springframework.data:spring-data-redis:4.1.1
|    |         +--- org.springframework.data:spring-data-keyvalue:4.1.1
|    |         |    +--- org.springframework.data:spring-data-commons:4.1.1 (*)
|    |         |    +--- org.springframework:spring-context:7.0.9 (*)
|    |         |    +--- org.springframework:spring-tx:7.0.9 (*)
|    |         |    \--- org.slf4j:slf4j-api:2.0.18
|    |         +--- org.springframework:spring-tx:7.0.9 (*)
|    |         +--- org.springframework:spring-oxm:7.0.9
|    |         |    +--- org.springframework:spring-beans:7.0.9 (*)
|    |         |    \--- org.springframework:spring-core:7.0.9 (*)
|    |         +--- org.springframework:spring-aop:7.0.9 (*)
|    |         +--- org.springframework:spring-context-support:7.0.9 (*)
|    |         \--- org.slf4j:slf4j-api:2.0.18
|    \--- org.springframework:spring-messaging:7.0.9
|         +--- org.springframework:spring-beans:7.0.9 (*)
|         \--- org.springframework:spring-core:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-flyway -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jdbc:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-flyway:4.1.1
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-jdbc:4.1.1 (*)
|    |    \--- org.flywaydb:flyway-core:12.4.0 (*)
|    \--- org.springframework.boot:spring-boot-jdbc:4.1.1 (*)
+--- org.springframework.boot:spring-boot-starter-graphql -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jackson:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-jackson:4.1.1
|    |         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |         \--- tools.jackson.core:jackson-databind:3.1.5 (*)
|    +--- org.springframework.boot:spring-boot-reactor:4.1.1
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    \--- io.projectreactor:reactor-core:3.8.7 (*)
|    \--- org.springframework.boot:spring-boot-graphql:4.1.1
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         \--- org.springframework.graphql:spring-graphql:2.0.5
|              +--- com.graphql-java:graphql-java:25.0
|              |    +--- com.graphql-java:java-dataloader:6.0.0
|              |    |    +--- org.reactivestreams:reactive-streams:1.0.3 -> 1.0.4
|              |    |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|              |    +--- org.reactivestreams:reactive-streams:1.0.3 -> 1.0.4
|              |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|              +--- io.projectreactor:reactor-core:3.8.7 (*)
|              \--- org.springframework:spring-context:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-jetty -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jetty-runtime:4.1.1
|    |    +--- org.springframework.boot:spring-boot-jetty:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot-web-server:4.1.1
|    |    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    |    \--- org.springframework:spring-web:7.0.9
|    |    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |    |    |         \--- io.micrometer:micrometer-observation:1.16.7 -> 1.17.1 (*)
|    |    |    \--- org.eclipse.jetty.ee11:jetty-ee11-webapp:12.1.12
|    |    |         +--- org.eclipse.jetty:jetty-session:12.1.12
|    |    |         |    +--- org.eclipse.jetty:jetty-server:12.1.12
|    |    |         |    |    +--- org.eclipse.jetty:jetty-http:12.1.12
|    |    |         |    |    |    +--- org.eclipse.jetty:jetty-io:12.1.12
|    |    |         |    |    |    |    +--- org.eclipse.jetty:jetty-util:12.1.12
|    |    |         |    |    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         |    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         |    |    |    +--- org.eclipse.jetty:jetty-util:12.1.12 (*)
|    |    |         |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         |    |    +--- org.eclipse.jetty:jetty-io:12.1.12 (*)
|    |    |         |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         +--- org.eclipse.jetty:jetty-xml:12.1.12
|    |    |         |    +--- org.eclipse.jetty:jetty-util:12.1.12 (*)
|    |    |         |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         +--- org.eclipse.jetty.ee:jetty-ee-webapp:12.1.12
|    |    |         |    +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |         |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         +--- org.eclipse.jetty.ee11:jetty-ee11-servlet:12.1.12
|    |    |         |    +--- jakarta.servlet:jakarta.servlet-api:6.1.0
|    |    |         |    +--- org.eclipse.jetty:jetty-security:12.1.12
|    |    |         |    |    +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |         |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         |    +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |         |    +--- org.eclipse.jetty:jetty-session:12.1.12 (*)
|    |    |         |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    +--- org.springframework.boot:spring-boot-web-server:4.1.1 (*)
|    |    +--- jakarta.servlet:jakarta.servlet-api:6.1.0
|    |    +--- jakarta.websocket:jakarta.websocket-api:2.2.0
|    |    +--- jakarta.websocket:jakarta.websocket-client-api:2.2.0
|    |    +--- org.apache.tomcat.embed:tomcat-embed-el:11.0.24
|    |    +--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-jakarta-server:12.1.12
|    |    |    +--- jakarta.websocket:jakarta.websocket-api:2.2.0
|    |    |    +--- org.eclipse.jetty.ee11:jetty-ee11-annotations:12.1.12
|    |    |    |    +--- jakarta.annotation:jakarta.annotation-api:3.0.0
|    |    |    |    +--- jakarta.servlet:jakarta.servlet-api:6.1.0
|    |    |    |    +--- org.eclipse.jetty:jetty-annotations:12.1.12
|    |    |    |    |    +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |    |    |    +--- org.ow2.asm:asm:9.10.1
|    |    |    |    |    \--- org.ow2.asm:asm-commons:9.10.1
|    |    |    |    |         +--- org.ow2.asm:asm:9.10.1
|    |    |    |    |         \--- org.ow2.asm:asm-tree:9.10.1
|    |    |    |    |              \--- org.ow2.asm:asm:9.10.1
|    |    |    |    +--- org.eclipse.jetty.ee11:jetty-ee11-plus:12.1.12
|    |    |    |    |    +--- jakarta.enterprise:jakarta.enterprise.cdi-api:4.1.0
|    |    |    |    |    |    +--- jakarta.enterprise:jakarta.enterprise.lang-model:4.1.0
|    |    |    |    |    |    +--- jakarta.annotation:jakarta.annotation-api:3.0.0
|    |    |    |    |    |    +--- jakarta.interceptor:jakarta.interceptor-api:2.2.0
|    |    |    |    |    |    |    \--- jakarta.annotation:jakarta.annotation-api:3.0.0
|    |    |    |    |    |    \--- jakarta.inject:jakarta.inject-api:2.0.1
|    |    |    |    |    +--- jakarta.enterprise:jakarta.enterprise.lang-model:4.1.0
|    |    |    |    |    +--- jakarta.interceptor:jakarta.interceptor-api:2.2.0 (*)
|    |    |    |    |    +--- jakarta.transaction:jakarta.transaction-api:2.0.1
|    |    |    |    |    +--- org.eclipse.jetty:jetty-plus:12.1.12
|    |    |    |    |    |    +--- org.eclipse.jetty:jetty-security:12.1.12 (*)
|    |    |    |    |    |    \--- org.eclipse.jetty:jetty-util:12.1.12 (*)
|    |    |    |    |    \--- org.eclipse.jetty.ee11:jetty-ee11-webapp:12.1.12 (*)
|    |    |    |    +--- org.eclipse.jetty.ee11:jetty-ee11-webapp:12.1.12 (*)
|    |    |    |    +--- org.ow2.asm:asm:9.10.1
|    |    |    |    \--- org.ow2.asm:asm-commons:9.10.1 (*)
|    |    |    +--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-jakarta-client:12.1.12
|    |    |    |    +--- jakarta.websocket:jakarta.websocket-api:2.2.0
|    |    |    |    +--- jakarta.websocket:jakarta.websocket-client-api:2.2.0
|    |    |    |    +--- org.eclipse.jetty:jetty-client:12.1.12
|    |    |    |    |    +--- org.eclipse.jetty:jetty-alpn-client:12.1.12
|    |    |    |    |    |    \--- org.eclipse.jetty:jetty-io:12.1.12 (*)
|    |    |    |    |    +--- org.eclipse.jetty:jetty-http:12.1.12 (*)
|    |    |    |    |    +--- org.eclipse.jetty:jetty-io:12.1.12 (*)
|    |    |    |    |    \--- org.eclipse.jetty.compression:jetty-compression-gzip:12.1.12
|    |    |    |    |         \--- org.eclipse.jetty.compression:jetty-compression-common:12.1.12
|    |    |    |    |              +--- org.eclipse.jetty:jetty-http:12.1.12 (*)
|    |    |    |    |              +--- org.eclipse.jetty:jetty-io:12.1.12 (*)
|    |    |    |    |              \--- org.eclipse.jetty:jetty-util:12.1.12 (*)
|    |    |    |    +--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-jakarta-common:12.1.12
|    |    |    |    |    +--- jakarta.websocket:jakarta.websocket-api:2.2.0
|    |    |    |    |    +--- jakarta.websocket:jakarta.websocket-client-api:2.2.0
|    |    |    |    |    \--- org.eclipse.jetty.websocket:jetty-websocket-core-client:12.1.12
|    |    |    |    |         +--- org.eclipse.jetty:jetty-client:12.1.12 (*)
|    |    |    |    |         \--- org.eclipse.jetty.websocket:jetty-websocket-core-common:12.1.12
|    |    |    |    |              +--- org.eclipse.jetty:jetty-http:12.1.12 (*)
|    |    |    |    |              \--- org.eclipse.jetty:jetty-io:12.1.12 (*)
|    |    |    |    \--- org.eclipse.jetty.websocket:jetty-websocket-core-client:12.1.12 (*)
|    |    |    \--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-servlet:12.1.12
|    |    |         +--- org.eclipse.jetty.ee11:jetty-ee11-servlet:12.1.12 (*)
|    |    |         \--- org.eclipse.jetty.websocket:jetty-websocket-core-server:12.1.12
|    |    |              +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |              \--- org.eclipse.jetty.websocket:jetty-websocket-core-common:12.1.12 (*)
|    |    \--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-jetty-server:12.1.12
|    |         +--- jakarta.servlet:jakarta.servlet-api:6.1.0
|    |         +--- org.eclipse.jetty.ee11:jetty-ee11-annotations:12.1.12 (*)
|    |         +--- org.eclipse.jetty.ee11:jetty-ee11-servlet:12.1.12 (*)
|    |         +--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-servlet:12.1.12 (*)
|    |         +--- org.eclipse.jetty.websocket:jetty-websocket-jetty-api:12.1.12
|    |         +--- org.eclipse.jetty.websocket:jetty-websocket-jetty-common:12.1.12
|    |         |    +--- org.eclipse.jetty.websocket:jetty-websocket-core-common:12.1.12 (*)
|    |         |    \--- org.eclipse.jetty.websocket:jetty-websocket-jetty-api:12.1.12
|    |         \--- org.eclipse.jetty.websocket:jetty-websocket-jetty-server:12.1.12
|    |              +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |              +--- org.eclipse.jetty.websocket:jetty-websocket-core-server:12.1.12 (*)
|    |              \--- org.eclipse.jetty.websocket:jetty-websocket-jetty-common:12.1.12 (*)
|    +--- org.springframework.boot:spring-boot-jetty:4.1.1 (*)
|    +--- org.slf4j:slf4j-api:2.0.18
|    \--- jakarta.annotation:jakarta.annotation-api:3.0.0
+--- org.springframework.boot:spring-boot-starter-kafka -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-kafka:4.1.1
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         +--- org.springframework.boot:spring-boot-transaction:4.1.1 (*)
|         \--- org.springframework.kafka:spring-kafka:4.1.1
|              +--- org.springframework:spring-context:7.0.9 (*)
|              +--- org.springframework:spring-messaging:7.0.9 (*)
|              +--- org.springframework:spring-tx:7.0.9 (*)
|              +--- org.apache.kafka:kafka-clients:4.2.1
|              \--- io.micrometer:micrometer-observation:1.17.1 (*)
+--- org.springframework.boot:spring-boot-starter-oauth2-client -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-security:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-security:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    +--- org.springframework.security:spring-security-config:7.1.1
|    |    |    |    +--- org.springframework.security:spring-security-core:7.1.1
|    |    |    |    |    +--- org.springframework.security:spring-security-crypto:7.1.1
|    |    |    |    |    +--- org.springframework:spring-aop:7.0.9 (*)
|    |    |    |    |    +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    |    |    +--- org.springframework:spring-context:7.0.9 (*)
|    |    |    |    |    +--- org.springframework:spring-core:7.0.9 (*)
|    |    |    |    |    +--- org.springframework:spring-expression:7.0.9 (*)
|    |    |    |    |    \--- io.micrometer:micrometer-observation:1.17.1 (*)
|    |    |    |    +--- org.springframework:spring-aop:7.0.9 (*)
|    |    |    |    +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    |    +--- org.springframework:spring-context:7.0.9 (*)
|    |    |    |    \--- org.springframework:spring-core:7.0.9 (*)
|    |    |    \--- org.springframework.security:spring-security-web:7.1.1
|    |    |         +--- org.springframework.security:spring-security-core:7.1.1 (*)
|    |    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |    |         +--- org.springframework:spring-aop:7.0.9 (*)
|    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |         +--- org.springframework:spring-context:7.0.9 (*)
|    |    |         +--- org.springframework:spring-expression:7.0.9 (*)
|    |    |         \--- org.springframework:spring-web:7.0.9 (*)
|    |    \--- org.springframework:spring-aop:7.0.9 (*)
|    +--- org.springframework.boot:spring-boot-security-oauth2-client:4.1.1
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    \--- org.springframework.security:spring-security-oauth2-client:7.1.1
|    |         +--- org.springframework.security:spring-security-core:7.1.1 (*)
|    |         +--- org.springframework.security:spring-security-oauth2-core:7.1.1
|    |         |    +--- org.springframework.security:spring-security-core:7.1.1 (*)
|    |         |    +--- org.springframework:spring-core:7.0.9 (*)
|    |         |    \--- org.springframework:spring-web:7.0.9 (*)
|    |         +--- org.springframework.security:spring-security-web:7.1.1 (*)
|    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |         \--- com.nimbusds:oauth2-oidc-sdk:11.38.2
|    |              +--- com.github.stephenc.jcip:jcip-annotations:1.0-1
|    |              +--- com.nimbusds:content-type:2.3
|    |              +--- net.minidev:json-smart:2.6.0
|    |              |    \--- net.minidev:accessors-smart:2.6.0
|    |              |         \--- org.ow2.asm:asm:9.7.1 -> 9.10.1
|    |              +--- com.nimbusds:lang-tag:1.7
|    |              \--- com.nimbusds:nimbus-jose-jwt:10.9.1
|    \--- org.springframework.security:spring-security-oauth2-jose:7.1.1
|         +--- org.springframework.security:spring-security-core:7.1.1 (*)
|         +--- org.springframework.security:spring-security-oauth2-core:7.1.1 (*)
|         +--- org.springframework:spring-core:7.0.9 (*)
|         \--- com.nimbusds:nimbus-jose-jwt:10.9.1
+--- org.springframework.boot:spring-boot-starter-oauth2-resource-server -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-security:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-security-oauth2-resource-server:4.1.1
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         +--- org.springframework.security:spring-security-oauth2-jose:7.1.1 (*)
|         \--- org.springframework.security:spring-security-oauth2-resource-server:7.1.1
|              +--- org.springframework.security:spring-security-core:7.1.1 (*)
|              +--- org.springframework.security:spring-security-oauth2-core:7.1.1 (*)
|              +--- org.springframework.security:spring-security-web:7.1.1 (*)
|              \--- org.springframework:spring-core:7.0.9 (*)
+--- org.springframework.boot:spring-boot-restclient -> 4.1.1
|    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-http-client:4.1.1
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         \--- org.springframework:spring-web:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-validation -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-validation:4.1.1
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         +--- org.apache.tomcat.embed:tomcat-embed-el:11.0.24
|         \--- org.hibernate.validator:hibernate-validator:9.1.3.Final
|              +--- jakarta.validation:jakarta.validation-api:3.1.1
|              +--- org.jboss.logging:jboss-logging:3.6.3.Final
|              \--- com.fasterxml:classmate:1.7.1 -> 1.7.3
+--- org.springframework.boot:spring-boot-starter-web -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter-jackson:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-http-converter:4.1.1
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    \--- org.springframework:spring-web:7.0.9 (*)
|    \--- org.springframework.boot:spring-boot-webmvc:4.1.1
|         +--- org.springframework.boot:spring-boot-servlet:4.1.1
|         |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         |    \--- org.springframework:spring-web:7.0.9 (*)
|         +--- org.springframework:spring-web:7.0.9 (*)
|         \--- org.springframework:spring-webmvc:7.0.9
|              +--- org.springframework:spring-aop:7.0.9 (*)
|              +--- org.springframework:spring-beans:7.0.9 (*)
|              +--- org.springframework:spring-context:7.0.9 (*)
|              +--- org.springframework:spring-core:7.0.9 (*)
|              +--- org.springframework:spring-expression:7.0.9 (*)
|              \--- org.springframework:spring-web:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-webclient -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jackson:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-reactor:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-webclient:4.1.1
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-http-client:4.1.1 (*)
|    |    \--- org.springframework:spring-webflux:7.0.9
|    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |         +--- org.springframework:spring-web:7.0.9 (*)
|    |         \--- io.projectreactor:reactor-core:3.8.7 (*)
|    \--- io.projectreactor.netty:reactor-netty-http:1.3.7
|         +--- io.netty:netty-codec-http:4.2.17.Final
|         |    +--- io.netty:netty-common:4.2.17.Final
|         |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-compression:4.2.17.Final
|         |    |    +--- io.netty:netty-common:4.2.17.Final
|         |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    |    \--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    \--- io.netty:netty-handler:4.2.17.Final (*)
|         +--- io.netty:netty-codec-http2:4.2.17.Final
|         |    +--- io.netty:netty-common:4.2.17.Final
|         |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    +--- io.netty:netty-handler:4.2.17.Final (*)
|         |    \--- io.netty:netty-codec-http:4.2.17.Final (*)
|         +--- io.netty:netty-codec-http3:4.2.17.Final
|         |    +--- io.netty:netty-common:4.2.17.Final
|         |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-http:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-compression:4.2.17.Final (*)
|         |    +--- io.netty:netty-handler:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport-native-unix-common:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    +--- io.netty:netty-resolver:4.2.17.Final (*)
|         |    \--- io.netty:netty-codec-classes-quic:4.2.17.Final
|         +--- io.netty:netty-resolver-dns:4.2.17.Final (*)
|         +--- io.netty:netty-resolver-dns-native-macos:4.2.17.Final
|         |    \--- io.netty:netty-resolver-dns-classes-macos:4.2.17.Final
|         |         +--- io.netty:netty-common:4.2.17.Final
|         |         +--- io.netty:netty-resolver-dns:4.2.17.Final (*)
|         |         \--- io.netty:netty-transport-native-unix-common:4.2.17.Final (*)
|         +--- io.netty:netty-transport-native-epoll:4.2.17.Final
|         |    +--- io.netty:netty-common:4.2.17.Final
|         |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport-native-unix-common:4.2.17.Final (*)
|         |    \--- io.netty:netty-transport-classes-epoll:4.2.17.Final
|         |         +--- io.netty:netty-common:4.2.17.Final
|         |         +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |         +--- io.netty:netty-transport:4.2.17.Final (*)
|         |         \--- io.netty:netty-transport-native-unix-common:4.2.17.Final (*)
|         +--- io.projectreactor.netty:reactor-netty-core:1.3.7
|         |    +--- io.netty:netty-handler:4.2.17.Final (*)
|         |    +--- io.netty:netty-handler-proxy:4.2.17.Final
|         |    |    +--- io.netty:netty-common:4.2.17.Final
|         |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-codec-socks:4.2.17.Final
|         |    |    |    +--- io.netty:netty-common:4.2.17.Final
|         |    |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    |    |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    |    |    \--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-codec-http:4.2.17.Final (*)
|         |    |    \--- io.netty:netty-handler:4.2.17.Final (*)
|         |    +--- io.netty:netty-resolver-dns:4.2.17.Final (*)
|         |    +--- io.netty:netty-resolver-dns-native-macos:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport-native-epoll:4.2.17.Final (*)
|         |    +--- io.projectreactor:reactor-core:3.8.7 (*)
|         |    \--- org.jspecify:jspecify:1.0.1
|         +--- io.projectreactor:reactor-core:3.8.7 (*)
|         \--- org.jspecify:jspecify:1.0.1
+--- org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.0
|    +--- org.springdoc:springdoc-openapi-starter-webmvc-api:3.1.0
|    |    +--- org.springdoc:springdoc-openapi-starter-common:3.1.0
|    |    |    +--- org.springframework.boot:spring-boot-starter:4.1.0 -> 4.1.1 (*)
|    |    |    +--- org.springframework.boot:spring-boot-autoconfigure:4.1.0 -> 4.1.1 (*)
|    |    |    +--- org.springframework.boot:spring-boot-validation:4.1.0 -> 4.1.1 (*)
|    |    |    +--- io.swagger.core.v3:swagger-core-jakarta:2.2.52
|    |    |    |    +--- org.apache.commons:commons-lang3:3.20.0
|    |    |    |    +--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    |    +--- io.swagger.core.v3:swagger-annotations-jakarta:2.2.52
|    |    |    |    +--- io.swagger.core.v3:swagger-models-jakarta:2.2.52
|    |    |    |    |    \--- com.fasterxml.jackson.core:jackson-annotations:2.21
|    |    |    |    +--- org.yaml:snakeyaml:2.6
|    |    |    |    +--- jakarta.xml.bind:jakarta.xml.bind-api:3.0.1 -> 4.0.5
|    |    |    |    |    \--- jakarta.activation:jakarta.activation-api:2.1.4
|    |    |    |    +--- jakarta.validation:jakarta.validation-api:3.0.2 -> 3.1.1
|    |    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|    |    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.1 -> 2.21.5 (*)
|    |    |    |    +--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.22.0 -> 2.21.5
|    |    |    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.5 (*)
|    |    |    |    |    +--- org.yaml:snakeyaml:2.5 -> 2.6
|    |    |    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (*)
|    |    |    |    |    \--- com.fasterxml.jackson:jackson-bom:2.21.5 (*)
|    |    |    |    \--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.22.0 -> 2.21.5
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (*)
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-databind:2.21.5 (*)
|    |    |    |         \--- com.fasterxml.jackson:jackson-bom:2.21.5 (*)
|    |    |    \--- org.springframework.boot:spring-boot-jackson:4.1.0 -> 4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-webmvc:4.1.0 -> 4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-web-server:4.1.0 -> 4.1.1 (*)
|    +--- org.webjars:swagger-ui:5.32.11
|    \--- org.webjars:webjars-locator-lite:1.1.3 -> 1.1.4
|         \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
+--- com.github.ben-manes.caffeine:caffeine -> 3.2.4
|    +--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|    \--- com.google.errorprone:error_prone_annotations:2.49.0
+--- io.kotest:kotest-runner-junit5:6.2.3
|    \--- io.kotest:kotest-runner-junit5-jvm:6.2.3
|         +--- io.kotest:kotest-common:6.2.3
|         |    \--- io.kotest:kotest-common-jvm:6.2.3
|         |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.2.21 -> 2.4.0 (*)
|         +--- io.kotest:kotest-framework-engine:6.2.3
|         |    \--- io.kotest:kotest-framework-engine-jvm:6.2.3
|         |         +--- org.opentest4j:opentest4j:1.3.0
|         |         +--- org.jetbrains.kotlinx:kotlinx-coroutines-debug:1.10.2
|         |         |    +--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2
|         |         |    |    \--- org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.10.2
|         |         |    |         +--- org.jetbrains:annotations:23.0.0
|         |         |    |         +--- org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.10.2
|         |         |    |         |    +--- org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.10.2 (c)
|         |         |    |         |    +--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2 (c)
|         |         |    |         |    \--- org.jetbrains.kotlinx:kotlinx-coroutines-debug:1.10.2 (c)
|         |         |    |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.1.0 -> 2.4.0 (*)
|         |         |    +--- org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.10.2 (*)
|         |         |    +--- net.java.dev.jna:jna:5.9.0 -> 5.18.1
|         |         |    +--- net.java.dev.jna:jna-platform:5.9.0
|         |         |    |    \--- net.java.dev.jna:jna:5.9.0 -> 5.18.1
|         |         |    \--- org.jetbrains.kotlin:kotlin-stdlib:2.1.0 -> 2.4.0 (*)
|         |         +--- io.kotest:kotest-common:6.2.3 (*)
|         |         +--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2 (*)
|         |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.2.21 -> 2.4.0 (*)
|         +--- io.kotest:kotest-runner-junit-platform:6.2.3
|         |    \--- io.kotest:kotest-runner-junit-platform-jvm:6.2.3
|         |         +--- io.kotest:kotest-common:6.2.3 (*)
|         |         +--- io.kotest:kotest-framework-engine:6.2.3 (*)
|         |         +--- io.kotest:kotest-assertions-core:6.2.3
|         |         |    \--- io.kotest:kotest-assertions-core-jvm:6.2.3
|         |         |         +--- io.kotest:kotest-assertions-shared:6.2.3
|         |         |         |    \--- io.kotest:kotest-assertions-shared-jvm:6.2.3
|         |         |         |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.2.21 -> 2.4.0 (*)
|         |         |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.2.21 -> 2.4.0 (*)
|         |         +--- io.kotest:kotest-extensions:6.2.3
|         |         |    \--- io.kotest:kotest-extensions-jvm:6.2.3
|         |         |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.2.21 -> 2.4.0 (*)
|         |         +--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2 (*)
|         |         +--- org.junit.platform:junit-platform-suite-api:1.13.4 -> 6.0.3
|         |         |    +--- org.junit:junit-bom:6.0.3
|         |         |    |    +--- org.junit.jupiter:junit-jupiter:6.0.3 (c)
|         |         |    |    +--- org.junit.jupiter:junit-jupiter-api:6.0.3 (c)
|         |         |    |    +--- org.junit.jupiter:junit-jupiter-params:6.0.3 (c)
|         |         |    |    +--- org.junit.platform:junit-platform-commons:6.0.3 (c)
|         |         |    |    +--- org.junit.platform:junit-platform-engine:6.0.3 (c)
|         |         |    |    +--- org.junit.platform:junit-platform-launcher:6.0.3 (c)
|         |         |    |    \--- org.junit.platform:junit-platform-suite-api:6.0.3 (c)
|         |         |    +--- org.junit.platform:junit-platform-commons:6.0.3
|         |         |    |    +--- org.junit:junit-bom:6.0.3 (*)
|         |         |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|         |         |    |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|         |         |    +--- org.apiguardian:apiguardian-api:1.1.2
|         |         |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|         |         +--- org.junit.platform:junit-platform-launcher:1.13.4 -> 6.0.3
|         |         |    +--- org.junit:junit-bom:6.0.3 (*)
|         |         |    +--- org.junit.platform:junit-platform-engine:6.0.3
|         |         |    |    +--- org.junit:junit-bom:6.0.3 (*)
|         |         |    |    +--- org.opentest4j:opentest4j:1.3.0
|         |         |    |    +--- org.junit.platform:junit-platform-commons:6.0.3 (*)
|         |         |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|         |         |    |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|         |         |    +--- org.apiguardian:apiguardian-api:1.1.2
|         |         |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|         |         +--- org.junit.platform:junit-platform-engine:1.13.4 -> 6.0.3 (*)
|         |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.2.21 -> 2.4.0 (*)
|         +--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2 (*)
|         +--- org.junit.platform:junit-platform-engine:1.13.4 -> 6.0.3 (*)
|         +--- org.junit.platform:junit-platform-suite-api:1.13.4 -> 6.0.3 (*)
|         +--- org.junit.platform:junit-platform-launcher:1.13.4 -> 6.0.3 (*)
|         +--- org.junit.jupiter:junit-jupiter-api:5.13.4 -> 6.0.3
|         |    +--- org.junit:junit-bom:6.0.3 (*)
|         |    +--- org.opentest4j:opentest4j:1.3.0
|         |    +--- org.junit.platform:junit-platform-commons:6.0.3 (*)
|         |    +--- org.apiguardian:apiguardian-api:1.1.2
|         |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|         \--- org.jetbrains.kotlin:kotlin-stdlib:2.2.21 -> 2.4.0 (*)
+--- io.kotest:kotest-assertions-core:6.2.3 (*)
+--- io.kotest:kotest-extensions-spring:6.2.3
|    \--- io.kotest:kotest-extensions-spring-jvm:6.2.3
|         \--- org.jetbrains.kotlin:kotlin-stdlib:2.2.21 -> 2.4.0 (*)
+--- org.springframework.boot:spring-boot-micrometer-metrics-test -> 4.1.1
|    +--- org.springframework.boot:spring-boot-test-autoconfigure:4.1.1
|    |    \--- org.springframework.boot:spring-boot-test:4.1.1
|    |         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |         \--- org.springframework:spring-test:7.0.9
|    |              \--- org.springframework:spring-core:7.0.9 (*)
|    +--- org.springframework.boot:spring-boot-micrometer-metrics:4.1.1 (*)
|    \--- io.micrometer:micrometer-observation-test:1.17.1
|         +--- org.jspecify:jspecify:1.0.1
|         +--- io.micrometer:micrometer-observation:1.17.1 (*)
|         \--- org.assertj:assertj-core:3.27.7
|              \--- net.bytebuddy:byte-buddy:1.18.3 -> 1.18.11
+--- com.redis:testcontainers-redis -> 2.2.4
|    +--- org.testcontainers:testcontainers:1.20.4 -> 2.0.5
|    |    +--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|    |    +--- org.apache.commons:commons-compress:1.28.0 (*)
|    |    +--- org.rnorth.duct-tape:duct-tape:1.0.8
|    |    |    \--- org.jetbrains:annotations:17.0.0 -> 23.0.0
|    |    +--- com.github.docker-java:docker-java-api:3.7.1
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.20 -> 2.21
|    |    |    \--- org.slf4j:slf4j-api:1.7.30 -> 2.0.18
|    |    \--- com.github.docker-java:docker-java-transport-zerodep:3.7.1
|    |         +--- com.github.docker-java:docker-java-transport:3.7.1
|    |         +--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|    |         \--- net.java.dev.jna:jna:5.18.1
|    \--- com.redis:testcontainers-redis-common:2.2.4
+--- org.testcontainers:testcontainers-junit-jupiter -> 2.0.5
|    \--- org.testcontainers:testcontainers:2.0.5 (*)
+--- org.testcontainers:testcontainers-postgresql -> 2.0.5
|    \--- org.testcontainers:testcontainers-jdbc:2.0.5
|         \--- org.testcontainers:testcontainers-database-commons:2.0.5
|              \--- org.testcontainers:testcontainers:2.0.5 (*)
+--- org.springframework.boot:spring-boot-starter-data-jpa-test -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter-data-jpa:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-test:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-test:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-test-autoconfigure:4.1.1 (*)
|    |    +--- com.jayway.jsonpath:json-path:2.10.0
|    |    +--- jakarta.xml.bind:jakarta.xml.bind-api:4.0.5 (*)
|    |    +--- net.minidev:json-smart:2.6.0 (*)
|    |    +--- org.assertj:assertj-core:3.27.7 (*)
|    |    +--- org.awaitility:awaitility:4.3.0
|    |    |    \--- org.hamcrest:hamcrest:2.1 -> 3.0
|    |    +--- org.hamcrest:hamcrest:3.0
|    |    +--- org.junit.jupiter:junit-jupiter:6.0.3
|    |    |    +--- org.junit:junit-bom:6.0.3 (*)
|    |    |    +--- org.junit.jupiter:junit-jupiter-api:6.0.3 (*)
|    |    |    \--- org.junit.jupiter:junit-jupiter-params:6.0.3
|    |    |         +--- org.junit:junit-bom:6.0.3 (*)
|    |    |         +--- org.junit.jupiter:junit-jupiter-api:6.0.3 (*)
|    |    |         +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |         \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|    |    +--- org.mockito:mockito-core:5.23.0
|    |    |    +--- net.bytebuddy:byte-buddy:1.17.7 -> 1.18.11
|    |    |    \--- net.bytebuddy:byte-buddy-agent:1.17.7 -> 1.18.11
|    |    +--- org.mockito:mockito-junit-jupiter:5.23.0
|    |    |    \--- org.mockito:mockito-core:5.23.0 (*)
|    |    +--- org.skyscreamer:jsonassert:1.5.3
|    |    |    \--- com.vaadin.external.google:android-json:0.0.20131108.vaadin1
|    |    +--- org.springframework:spring-core:7.0.9 (*)
|    |    +--- org.springframework:spring-test:7.0.9 (*)
|    |    \--- org.xmlunit:xmlunit-core:2.11.0
|    +--- org.springframework.boot:spring-boot-starter-jdbc-test:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter-jdbc:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-starter-test:4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-jdbc-test:4.1.1
|    |         +--- org.springframework.boot:spring-boot-test-autoconfigure:4.1.1 (*)
|    |         \--- org.springframework.boot:spring-boot-jdbc:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-data-jpa-test:4.1.1
|         +--- org.springframework.boot:spring-boot-test-autoconfigure:4.1.1 (*)
|         +--- org.springframework.boot:spring-boot-data-jpa:4.1.1 (*)
|         \--- org.springframework.boot:spring-boot-jpa-test:4.1.1
|              +--- org.springframework.boot:spring-boot-test-autoconfigure:4.1.1 (*)
|              +--- org.springframework.boot:spring-boot-jdbc-test:4.1.1 (*)
|              \--- org.springframework.boot:spring-boot-jpa:4.1.1 (*)
+--- org.springframework.boot:spring-boot-starter-data-redis-test -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter-data-redis:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-test:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-data-redis-test:4.1.1
|         +--- org.springframework.boot:spring-boot-test-autoconfigure:4.1.1 (*)
|         \--- org.springframework.boot:spring-boot-data-redis:4.1.1 (*)
+--- org.springframework.boot:spring-boot-starter-kafka-test -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter-kafka:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-test:4.1.1 (*)
|    \--- org.springframework.kafka:spring-kafka-test:4.1.1
|         +--- org.slf4j:slf4j-api:2.0.18
|         +--- org.springframework:spring-context:7.0.9 (*)
|         +--- org.springframework:spring-test:7.0.9 (*)
|         +--- org.apache.kafka:kafka-clients:4.2.1
|         +--- org.apache.kafka:kafka-server:4.2.1
|         +--- org.apache.kafka:kafka-test-common-runtime:4.2.1
|         |    +--- org.apache.kafka:kafka_2.13:4.2.1
|         |    |    +--- org.apache.kafka:kafka-clients:4.2.1
|         |    |    \--- org.scala-lang:scala-library:2.13.17
|         |    \--- org.apache.kafka:kafka-clients:4.2.1
|         +--- org.apache.kafka:kafka-metadata:4.2.1
|         +--- org.apache.kafka:kafka-server-common:4.2.1
|         |    \--- org.apache.kafka:kafka-clients:4.2.1
|         +--- org.apache.kafka:kafka-streams-test-utils:4.2.1
|         |    +--- org.apache.kafka:kafka-streams:4.2.1
|         |    |    +--- org.apache.kafka:kafka-clients:4.2.1
|         |    |    \--- org.rocksdb:rocksdbjni:10.1.3
|         |    \--- org.apache.kafka:kafka-clients:4.2.1
|         +--- org.junit.jupiter:junit-jupiter-api:6.0.3 (*)
|         \--- org.junit.platform:junit-platform-launcher:6.0.3 (*)
+--- org.springframework.boot:spring-boot-starter-restclient-test -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter-restclient:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-starter-jackson:4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-restclient:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jackson-test:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter-jackson:4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-starter-test:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-test:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-restclient-test:4.1.1
|         +--- org.springframework.boot:spring-boot-test-autoconfigure:4.1.1 (*)
|         \--- org.springframework.boot:spring-boot-restclient:4.1.1 (*)
+--- org.springframework.boot:spring-boot-starter-webmvc-test -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter-jackson-test:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-test:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-webmvc:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-starter-jackson:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-starter-tomcat:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    |    +--- org.springframework.boot:spring-boot-starter-tomcat-runtime:4.1.1
|    |    |    |    +--- org.springframework.boot:spring-boot-tomcat:4.1.1
|    |    |    |    |    +--- org.springframework.boot:spring-boot-web-server:4.1.1 (*)
|    |    |    |    |    \--- org.apache.tomcat.embed:tomcat-embed-core:11.0.24
|    |    |    |    +--- org.springframework.boot:spring-boot-web-server:4.1.1 (*)
|    |    |    |    +--- jakarta.annotation:jakarta.annotation-api:3.0.0
|    |    |    |    +--- org.apache.tomcat.embed:tomcat-embed-core:11.0.24
|    |    |    |    +--- org.apache.tomcat.embed:tomcat-embed-el:11.0.24
|    |    |    |    \--- org.apache.tomcat.embed:tomcat-embed-websocket:11.0.24
|    |    |    |         \--- org.apache.tomcat.embed:tomcat-embed-core:11.0.24
|    |    |    \--- org.springframework.boot:spring-boot-tomcat:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-http-converter:4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-webmvc:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-webmvc-test:4.1.1
|    |    +--- org.springframework.boot:spring-boot-test-autoconfigure:4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-webmvc:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-resttestclient:4.1.1
|         +--- org.springframework.boot:spring-boot-test:4.1.1 (*)
|         +--- org.springframework.boot:spring-boot-http-converter:4.1.1 (*)
|         \--- org.springframework:spring-web:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-test -> 4.1.1 (*)
+--- org.springframework.boot:spring-boot-testcontainers -> 4.1.1
|    +--- org.springframework.boot:spring-boot-autoconfigure:4.1.1 (*)
|    \--- org.testcontainers:testcontainers:2.0.5 (*)
+--- org.springframework.security:spring-security-test -> 7.1.1
|    +--- org.springframework.security:spring-security-core:7.1.1 (*)
|    +--- org.springframework.security:spring-security-web:7.1.1 (*)
|    +--- org.springframework:spring-core:7.0.9 (*)
|    \--- org.springframework:spring-test:7.0.9 (*)
+--- no.nav.security:mock-oauth2-server:6.0.0
|    +--- com.squareup.okhttp3:mockwebserver:5.4.0
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:2.1.21 -> 2.4.0 (*)
|    |    +--- com.squareup.okhttp3:mockwebserver3:5.4.0
|    |    |    +--- org.jetbrains.kotlin:kotlin-stdlib:2.1.21 -> 2.4.0 (*)
|    |    |    \--- com.squareup.okhttp3:okhttp:5.4.0 (*)
|    |    +--- junit:junit:4.13.2
|    |    |    \--- org.hamcrest:hamcrest-core:1.3 -> 3.0
|    |    |         \--- org.hamcrest:hamcrest:3.0
|    |    +--- com.squareup.okio:okio:3.17.0 (*)
|    |    \--- com.squareup.okhttp3:okhttp:5.4.0 (*)
|    +--- com.nimbusds:oauth2-oidc-sdk:11.38.2 (*)
|    \--- com.squareup.okio:okio:3.4.0 -> 3.17.0 (c)
+--- org.springframework.restdocs:spring-restdocs-mockmvc -> 4.0.1
|    +--- org.springframework.restdocs:spring-restdocs-core:4.0.1
|    +--- org.springframework:spring-webmvc:7.0.8 -> 7.0.9 (*)
|    \--- org.springframework:spring-test:7.0.8 -> 7.0.9 (*)
\--- com.ninja-squad:springmockk:5.0.1
     +--- io.mockk:mockk-jvm:1.14.6
     |    +--- io.mockk:mockk-dsl:1.14.6
     |    |    \--- io.mockk:mockk-dsl-jvm:1.14.6
     |    |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.1.20 -> 2.4.0 (*)
     |    +--- io.mockk:mockk-agent:1.14.6
     |    |    \--- io.mockk:mockk-agent-jvm:1.14.6
     |    |         +--- org.objenesis:objenesis:3.3
     |    |         +--- net.bytebuddy:byte-buddy:1.15.11 -> 1.18.11
     |    |         +--- net.bytebuddy:byte-buddy-agent:1.15.11 -> 1.18.11
     |    |         +--- io.mockk:mockk-agent-api:1.14.6
     |    |         |    \--- io.mockk:mockk-agent-api-jvm:1.14.6
     |    |         |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.1.20 -> 2.4.0 (*)
     |    |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.1.20 -> 2.4.0 (*)
     |    +--- io.mockk:mockk-agent-api:1.14.6 (*)
     |    +--- io.mockk:mockk-core:1.14.6
     |    |    \--- io.mockk:mockk-core-jvm:1.14.6
     |    |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.1.20 -> 2.4.0 (*)
     |    \--- org.jetbrains.kotlin:kotlin-stdlib:2.1.20 -> 2.4.0 (*)
     \--- org.jetbrains.kotlin:kotlin-stdlib:2.2.21 -> 2.4.0 (*)

testCompileOnly - Compile only dependencies for 'test'. (n)
No dependencies

testImplementation - Implementation only dependencies for 'test'. (n)
+--- com.github.ben-manes.caffeine:caffeine (n)
+--- io.kotest:kotest-runner-junit5:6.2.3 (n)
+--- io.kotest:kotest-assertions-core:6.2.3 (n)
+--- io.kotest:kotest-extensions-spring:6.2.3 (n)
+--- org.springframework.boot:spring-boot-micrometer-metrics-test (n)
+--- com.redis:testcontainers-redis (n)
+--- org.testcontainers:testcontainers-junit-jupiter (n)
+--- org.testcontainers:testcontainers-postgresql (n)
+--- org.springframework.boot:spring-boot-starter-data-jpa-test (n)
+--- org.springframework.boot:spring-boot-starter-data-redis-test (n)
+--- org.springframework.boot:spring-boot-starter-kafka-test (n)
+--- org.springframework.boot:spring-boot-starter-restclient-test (n)
+--- org.springframework.boot:spring-boot-starter-webmvc-test (n)
+--- org.springframework.boot:spring-boot-starter-test (n)
+--- org.springframework.boot:spring-boot-testcontainers (n)
+--- org.springframework.security:spring-security-test (n)
+--- no.nav.security:mock-oauth2-server:6.0.0 (n)
+--- org.springframework.restdocs:spring-restdocs-mockmvc (n)
\--- com.ninja-squad:springmockk:5.0.1 (n)

testImplementationDependenciesMetadata
No dependencies

testKotlinScriptDef - Script filename extensions discovery classpath configuration (n)
No dependencies

testKotlinScriptDefExtensions
No dependencies

testRuntimeClasspath - Runtime classpath of 'test'.
+--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0
|    +--- org.jetbrains:annotations:13.0 -> 23.0.0
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0 -> 2.4.0 (c)
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.0 -> 2.4.0 (c)
|    \--- org.jetbrains.kotlin:kotlin-stdlib-common:2.4.0 (c)
+--- no.nav.pdl.libs:contract-pdl-avro:18
|    \--- io.confluent:kafka-avro-serializer:7.7.1 -> 8.3.1
|         +--- org.apache.avro:avro:1.12.1
|         |    +--- com.fasterxml.jackson.core:jackson-core:2.20.0 -> 2.21.5
|         |    |    \--- com.fasterxml.jackson:jackson-bom:2.21.5
|         |    |         +--- com.fasterxml.jackson.core:jackson-annotations:2.21 (c)
|         |    |         +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (c)
|         |    |         +--- com.fasterxml.jackson.core:jackson-databind:2.21.5 (c)
|         |    |         +--- com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.21.5 (c)
|         |    |         +--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.21.5 (c)
|         |    |         +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.21.5 (c)
|         |    |         \--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.21.5 (c)
|         |    +--- com.fasterxml.jackson.core:jackson-databind:2.20.0 -> 2.21.5
|         |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|         |    |    +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (*)
|         |    |    \--- com.fasterxml.jackson:jackson-bom:2.21.5 (*)
|         |    +--- org.apache.commons:commons-compress:1.28.0
|         |    |    +--- commons-codec:commons-codec:1.19.0 -> 1.21.0
|         |    |    +--- commons-io:commons-io:2.20.0
|         |    |    \--- org.apache.commons:commons-lang3:3.18.0 -> 3.20.0
|         |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|         +--- org.apache.commons:commons-compress:1.26.1 -> 1.28.0 (*)
|         +--- io.confluent:kafka-schema-serializer:8.3.1
|         |    +--- io.confluent:kafka-schema-registry-client:8.3.1
|         |    |    +--- io.confluent:kafka-avro-types:8.3.1
|         |    |    |    +--- io.confluent:kafka-schema-types:8.3.1
|         |    |    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.2 -> 2.21.5 (*)
|         |    |    |    |    \--- io.confluent:common-utils:8.3.1
|         |    |    |    |         \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    |    |    +--- org.apache.avro:avro:1.12.1 (*)
|         |    |    |    \--- io.confluent:common-utils:8.3.1 (*)
|         |    |    +--- org.apache.kafka:kafka-clients:8.3.1-ccs -> 4.2.1
|         |    |    |    +--- com.github.luben:zstd-jni:1.5.6-10
|         |    |    |    +--- at.yawk.lz4:lz4-java:1.10.1 -> 1.11.1
|         |    |    |    +--- org.xerial.snappy:snappy-java:1.1.10.7
|         |    |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    |    +--- org.apache.avro:avro:1.12.1 (*)
|         |    |    +--- org.apache.commons:commons-compress:1.26.1 -> 1.28.0 (*)
|         |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.2 -> 2.21.5 (*)
|         |    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.21.2 -> 2.21.5
|         |    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (*)
|         |    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.5 (*)
|         |    |    |    \--- com.fasterxml.jackson:jackson-bom:2.21.5 (*)
|         |    |    +--- org.yaml:snakeyaml:2.0 -> 2.6
|         |    |    +--- io.swagger.core.v3:swagger-annotations-jakarta:2.2.42 -> 2.2.52
|         |    |    +--- com.google.guava:guava:32.0.1-jre
|         |    |    |    +--- com.google.guava:failureaccess:1.0.1
|         |    |    |    +--- com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava
|         |    |    |    +--- com.google.code.findbugs:jsr305:3.0.2
|         |    |    |    +--- org.checkerframework:checker-qual:3.33.0 -> 3.55.1
|         |    |    |    +--- com.google.errorprone:error_prone_annotations:2.18.0 -> 2.49.0
|         |    |    |    \--- com.google.j2objc:j2objc-annotations:2.8
|         |    |    +--- org.apache.httpcomponents.client5:httpclient5:5.5 -> 5.6.4
|         |    |    |    +--- org.apache.httpcomponents.core5:httpcore5:5.4.3
|         |    |    |    +--- org.apache.httpcomponents.core5:httpcore5-h2:5.4.3
|         |    |    |    |    \--- org.apache.httpcomponents.core5:httpcore5:5.4.3
|         |    |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    |    \--- io.confluent:common-utils:8.3.1 (*)
|         |    +--- com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.21.2 -> 2.21.5
|         |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.5 (*)
|         |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|         |    |    +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (*)
|         |    |    \--- com.fasterxml.jackson:jackson-bom:2.21.5 (*)
|         |    \--- io.confluent:common-utils:8.3.1 (*)
|         +--- io.confluent:kafka-schema-registry-client:8.3.1 (*)
|         +--- com.google.guava:guava:32.0.1-jre (*)
|         +--- io.confluent:logredactor:1.0.18
|         |    +--- com.google.re2j:re2j:1.6 -> 1.8
|         |    +--- io.confluent:logredactor-metrics:1.0.18
|         |    +--- com.eclipsesource.minimal-json:minimal-json:0.9.5
|         |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         \--- io.confluent:common-utils:8.3.1 (*)
+--- no.nav.boot:boot-conditionals:6.0.7
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.4.0
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.4.0
|    |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:2.4.0
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
|    +--- ch.qos.logback:logback-core:1.5.34 -> 1.5.38
|    +--- org.slf4j:slf4j-api:2.0.18
|    \--- org.springframework.boot:spring-boot-autoconfigure:4.1.0 -> 4.1.1
|         \--- org.springframework.boot:spring-boot:4.1.1
|              +--- org.springframework:spring-core:7.0.9
|              |    +--- commons-logging:commons-logging:1.3.5 -> 1.3.6
|              |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|              \--- org.springframework:spring-context:7.0.9
|                   +--- org.springframework:spring-aop:7.0.9
|                   |    +--- org.springframework:spring-beans:7.0.9
|                   |    |    \--- org.springframework:spring-core:7.0.9 (*)
|                   |    \--- org.springframework:spring-core:7.0.9 (*)
|                   +--- org.springframework:spring-beans:7.0.9 (*)
|                   +--- org.springframework:spring-core:7.0.9 (*)
|                   +--- org.springframework:spring-expression:7.0.9
|                   |    \--- org.springframework:spring-core:7.0.9 (*)
|                   \--- io.micrometer:micrometer-observation:1.16.7 -> 1.17.1
|                        +--- org.jspecify:jspecify:1.0.1
|                        \--- io.micrometer:micrometer-commons:1.17.1
|                             \--- org.jspecify:jspecify:1.0.1
+--- io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations -> 2.30.0
|    \--- io.opentelemetry:opentelemetry-api:1.64.0
|         \--- io.opentelemetry:opentelemetry-context:1.64.0
|              \--- io.opentelemetry:opentelemetry-common:1.64.0
+--- io.opentelemetry.instrumentation:opentelemetry-logback-mdc-1.0:2.30.0-alpha
|    +--- io.opentelemetry.instrumentation:opentelemetry-instrumentation-api:2.30.0
|    |    +--- io.opentelemetry:opentelemetry-api-incubator:1.64.0-alpha
|    |    |    \--- io.opentelemetry:opentelemetry-api:1.64.0 (*)
|    |    +--- io.opentelemetry.semconv:opentelemetry-semconv:1.43.0
|    |    \--- io.opentelemetry:opentelemetry-api:1.64.0 (*)
|    +--- io.opentelemetry.instrumentation:opentelemetry-instrumentation-api-incubator:2.30.0-alpha
|    |    +--- io.opentelemetry.semconv:opentelemetry-semconv:1.43.0
|    |    +--- io.opentelemetry.instrumentation:opentelemetry-instrumentation-api:2.30.0 (*)
|    |    \--- io.opentelemetry:opentelemetry-api-incubator:1.64.0-alpha (*)
|    \--- io.opentelemetry:opentelemetry-api:1.64.0 (*)
+--- io.micrometer:micrometer-registry-prometheus -> 1.17.1
|    +--- org.jspecify:jspecify:1.0.1
|    +--- io.micrometer:micrometer-core:1.17.1
|    |    +--- org.jspecify:jspecify:1.0.1
|    |    +--- io.micrometer:micrometer-commons:1.17.1 (*)
|    |    +--- io.micrometer:micrometer-observation:1.17.1 (*)
|    |    \--- org.hdrhistogram:HdrHistogram:2.2.2
|    +--- io.prometheus:prometheus-metrics-core:1.7.0
|    |    +--- io.prometheus:prometheus-metrics-model:1.7.0
|    |    |    \--- io.prometheus:prometheus-metrics-config:1.7.0
|    |    \--- io.prometheus:prometheus-metrics-config:1.7.0
|    +--- io.prometheus:prometheus-metrics-tracer-common:1.7.0
|    \--- io.prometheus:prometheus-metrics-exposition-formats:1.7.0
|         \--- io.prometheus:prometheus-metrics-exposition-textformats:1.7.0
|              +--- io.prometheus:prometheus-metrics-model:1.7.0 (*)
|              \--- io.prometheus:prometheus-metrics-config:1.7.0
+--- com.slack.api:slack-api-client-kotlin-extension:1.49.0
|    +--- com.slack.api:slack-api-model-kotlin-extension:1.49.0
|    |    +--- com.slack.api:slack-api-model:1.49.0
|    |    |    \--- com.google.code.gson:gson:2.12.1 -> 2.13.2
|    |    |         \--- com.google.errorprone:error_prone_annotations:2.41.0 -> 2.49.0
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.24 -> 2.4.0 (*)
|    +--- com.slack.api:slack-api-client:1.49.0
|    |    +--- com.slack.api:slack-api-model:1.49.0 (*)
|    |    +--- com.squareup.okhttp3:okhttp:4.12.0 -> 5.4.0
|    |    |    \--- com.squareup.okhttp3:okhttp-jvm:5.4.0
|    |    |         +--- org.jetbrains.kotlin:kotlin-stdlib:2.1.21 -> 2.4.0 (*)
|    |    |         +--- com.squareup.okio:okio:3.17.0
|    |    |         |    \--- com.squareup.okio:okio-jvm:3.17.0
|    |    |         |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.1.21 -> 2.4.0 (*)
|    |    |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.2.21 -> 2.4.0 (*)
|    |    +--- com.google.code.gson:gson:2.12.1 -> 2.13.2 (*)
|    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|    \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.24 -> 2.4.0 (*)
+--- org.apache.commons:commons-pool2:2.13.1
+--- io.confluent:kafka-avro-serializer:8.3.1 (*)
+--- org.flywaydb:flyway-database-postgresql -> 12.4.0
|    \--- org.flywaydb:flyway-core:12.4.0
|         \--- tools.jackson.core:jackson-databind:3.1.1 -> 3.1.5
|              +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|              +--- tools.jackson.core:jackson-core:3.1.5
|              |    \--- tools.jackson:jackson-bom:3.1.5
|              |         +--- com.fasterxml.jackson.core:jackson-annotations:2.21 (c)
|              |         +--- tools.jackson.core:jackson-core:3.1.5 (c)
|              |         +--- tools.jackson.core:jackson-databind:3.1.5 (c)
|              |         \--- tools.jackson.module:jackson-module-kotlin:3.1.5 (c)
|              \--- tools.jackson:jackson-bom:3.1.5 (*)
+--- org.hibernate.orm:hibernate-micrometer -> 7.4.5.Final
|    +--- org.jboss.logging:jboss-logging:3.6.1.Final -> 3.6.3.Final
|    +--- org.hibernate.orm:hibernate-core:7.4.5.Final
|    |    +--- org.jboss.logging:jboss-logging:3.6.1.Final -> 3.6.3.Final
|    |    +--- org.hibernate.models:hibernate-models:1.1.1
|    |    |    \--- org.jboss.logging:jboss-logging:3.6.3.Final
|    |    +--- net.bytebuddy:byte-buddy:1.18.8 -> 1.18.11
|    |    +--- jakarta.xml.bind:jakarta.xml.bind-api:4.0.4 -> 4.0.5
|    |    |    \--- jakarta.activation:jakarta.activation-api:2.1.4
|    |    +--- org.glassfish.jaxb:jaxb-runtime:4.0.7 -> 4.0.9
|    |    |    \--- org.glassfish.jaxb:jaxb-core:4.0.9
|    |    |         +--- jakarta.xml.bind:jakarta.xml.bind-api:4.0.5 (*)
|    |    |         +--- jakarta.activation:jakarta.activation-api:2.1.4
|    |    |         +--- org.eclipse.angus:angus-activation:2.0.3
|    |    |         |    \--- jakarta.activation:jakarta.activation-api:2.1.4
|    |    |         +--- org.glassfish.jaxb:txw2:4.0.9
|    |    |         \--- com.sun.istack:istack-commons-runtime:4.1.2
|    |    +--- jakarta.inject:jakarta.inject-api:2.0.1
|    |    +--- org.antlr:antlr4-runtime:4.13.2
|    |    +--- org.hibernate.orm:hibernate-platform:7.4.5.Final
|    |    |    +--- org.antlr:antlr4-runtime:4.13.2 (c)
|    |    |    +--- org.jboss.logging:jboss-logging:3.6.1.Final -> 3.6.3.Final (c)
|    |    |    +--- net.bytebuddy:byte-buddy:1.18.8 -> 1.18.11 (c)
|    |    |    +--- org.glassfish.jaxb:jaxb-runtime:4.0.7 -> 4.0.9 (c)
|    |    |    +--- jakarta.xml.bind:jakarta.xml.bind-api:4.0.4 -> 4.0.5 (c)
|    |    |    +--- jakarta.inject:jakarta.inject-api:2.0.1 (c)
|    |    |    +--- io.micrometer:micrometer-core:1.16.0 -> 1.17.1 (c)
|    |    |    +--- org.hibernate.orm:hibernate-core:7.4.5.Final (c)
|    |    |    +--- org.hibernate.orm:hibernate-micrometer:7.4.5.Final (c)
|    |    |    +--- org.hibernate.models:hibernate-models:1.1.1 (c)
|    |    |    +--- jakarta.persistence:jakarta.persistence-api:3.2.0 (c)
|    |    |    +--- jakarta.transaction:jakarta.transaction-api:2.0.1 (c)
|    |    |    +--- com.zaxxer:HikariCP:7.0.2 (c)
|    |    |    \--- net.bytebuddy:byte-buddy-agent:1.18.8 -> 1.18.11 (c)
|    |    +--- jakarta.persistence:jakarta.persistence-api:3.2.0
|    |    +--- jakarta.transaction:jakarta.transaction-api:2.0.1
|    |    \--- org.apache.logging.log4j:log4j-core:{strictly [2.17.1, 3[; prefer 2.17.1} -> 2.25.5 (c)
|    +--- io.micrometer:micrometer-core:1.16.0 -> 1.17.1 (*)
|    +--- org.hibernate.orm:hibernate-platform:7.4.5.Final (*)
|    \--- org.apache.logging.log4j:log4j-core:{strictly [2.17.1, 3[; prefer 2.17.1} -> 2.25.5 (c)
+--- tools.jackson.module:jackson-module-kotlin -> 3.1.5
|    +--- tools.jackson.core:jackson-databind:3.1.5 (*)
|    +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|    +--- org.jetbrains.kotlin:kotlin-reflect:2.1.21 -> 2.4.0 (*)
|    \--- tools.jackson:jackson-bom:3.1.5 (*)
+--- org.zalando:logbook-spring-boot-starter:4.1.0
|    +--- org.zalando:logbook-spring-boot-autoconfigure:4.1.0
|    |    +--- org.zalando:logbook-core:4.1.0
|    |    |    +--- org.zalando:logbook-api:4.1.0
|    |    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    |    +--- org.zalando:faux-pas:0.9.0
|    |    |    |    |    +--- com.google.code.findbugs:jsr305:3.0.2
|    |    |    |    |    \--- org.slf4j:slf4j-api:1.7.30 -> 2.0.18
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    |    +--- org.zalando:logbook-common:4.1.0
|    |    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.zalando:logbook-json:4.1.0
|    |    |    +--- org.zalando:logbook-api:4.1.0 (*)
|    |    |    +--- org.zalando:logbook-common:4.1.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.22 -> 2.21
|    |    |    +--- com.jayway.jsonpath:json-path:3.0.0 -> 2.10.0
|    |    |    |    +--- net.minidev:json-smart:2.6.0
|    |    |    |    |    \--- net.minidev:accessors-smart:2.6.0
|    |    |    |    |         \--- org.ow2.asm:asm:9.7.1 -> 9.10.1
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.zalando:logbook-json-jackson2:4.1.0
|    |    |    +--- org.zalando:logbook-api:4.1.0 (*)
|    |    |    +--- org.zalando:logbook-common:4.1.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.22 -> 2.21
|    |    |    +--- com.jayway.jsonpath:json-path:3.0.0 -> 2.10.0 (*)
|    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.zalando:logbook-spring:4.1.0
|    |    |    +--- org.zalando:logbook-core:4.1.0 (*)
|    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.zalando:logbook-servlet:4.1.0
|    |    |    +--- org.zalando:logbook-api:4.1.0 (*)
|    |    |    +--- org.zalando:logbook-core:4.1.0 (*)
|    |    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    +--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    +--- org.zalando:logbook-spring-boot-ecs-autoconfigure:4.1.0
|    |    +--- org.apiguardian:apiguardian-api:1.1.2
|    |    +--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|    |    +--- org.zalando:faux-pas:0.9.0 (*)
|    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    +--- org.apiguardian:apiguardian-api:1.1.2
|    +--- org.zalando:faux-pas:0.9.0 (*)
|    \--- org.slf4j:slf4j-api:2.0.18
+--- net.logstash.logback:logstash-logback-encoder:9.0
|    \--- tools.jackson.core:jackson-databind:3.0.1 -> 3.1.5 (*)
+--- org.postgresql:postgresql -> 42.7.13
|    \--- org.checkerframework:checker-qual:3.55.1
+--- org.springframework.boot:spring-boot-starter-actuator -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter-logging:4.1.1
|    |    |    +--- ch.qos.logback:logback-classic:1.5.38
|    |    |    |    +--- ch.qos.logback:logback-core:1.5.38
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    +--- org.apache.logging.log4j:log4j-to-slf4j:2.25.5
|    |    |    |    +--- org.apache.logging.log4j:log4j-api:2.25.5
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    \--- org.slf4j:jul-to-slf4j:2.0.18
|    |    |         \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.springframework.boot:spring-boot-autoconfigure:4.1.1 (*)
|    |    +--- jakarta.annotation:jakarta.annotation-api:3.0.0
|    |    \--- org.yaml:snakeyaml:2.6
|    +--- org.springframework.boot:spring-boot-starter-micrometer-metrics:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-micrometer-metrics:4.1.1
|    |         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |         +--- org.springframework.boot:spring-boot-micrometer-observation:4.1.1
|    |         |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |         |    \--- io.micrometer:micrometer-observation:1.17.1 (*)
|    |         \--- io.micrometer:micrometer-core:1.17.1 (*)
|    +--- org.springframework.boot:spring-boot-actuator-autoconfigure:4.1.1
|    |    +--- org.springframework.boot:spring-boot-autoconfigure:4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-actuator:4.1.1
|    |         \--- org.springframework.boot:spring-boot:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-health:4.1.1
|    |    \--- org.springframework.boot:spring-boot:4.1.1 (*)
|    +--- io.micrometer:micrometer-observation:1.17.1 (*)
|    \--- io.micrometer:micrometer-jakarta9:1.17.1
|         +--- org.jspecify:jspecify:1.0.1
|         +--- io.micrometer:micrometer-core:1.17.1 (*)
|         +--- io.micrometer:micrometer-commons:1.17.1 (*)
|         \--- io.micrometer:micrometer-observation:1.17.1 (*)
+--- org.springframework.boot:spring-boot-starter-cache -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-cache:4.1.1
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         \--- org.springframework:spring-context-support:7.0.9
|              +--- org.springframework:spring-beans:7.0.9 (*)
|              +--- org.springframework:spring-context:7.0.9 (*)
|              \--- org.springframework:spring-core:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-data-jpa -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jdbc:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-jdbc:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    +--- org.springframework.boot:spring-boot-sql:4.1.1
|    |    |    |    \--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    +--- org.springframework.boot:spring-boot-transaction:4.1.1
|    |    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    |    +--- org.springframework.boot:spring-boot-persistence:4.1.1
|    |    |    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    |    |    \--- org.springframework:spring-tx:7.0.9
|    |    |    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    |    |         \--- org.springframework:spring-core:7.0.9 (*)
|    |    |    |    \--- org.springframework:spring-tx:7.0.9 (*)
|    |    |    \--- org.springframework:spring-jdbc:7.0.9
|    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |    |         \--- org.springframework:spring-tx:7.0.9 (*)
|    |    \--- com.zaxxer:HikariCP:7.0.2
|    |         \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    +--- org.springframework.boot:spring-boot-data-jpa:4.1.1
|    |    +--- org.springframework.boot:spring-boot-data-commons:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    +--- org.springframework.boot:spring-boot-persistence:4.1.1 (*)
|    |    |    \--- org.springframework.data:spring-data-commons:4.1.1
|    |    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |         \--- org.slf4j:slf4j-api:2.0.18
|    |    +--- org.springframework.boot:spring-boot-hibernate:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot-jpa:4.1.1
|    |    |    |    +--- org.springframework.boot:spring-boot-jdbc:4.1.1 (*)
|    |    |    |    +--- org.springframework.boot:spring-boot-transaction:4.1.1 (*)
|    |    |    |    +--- jakarta.persistence:jakarta.persistence-api:3.2.0
|    |    |    |    \--- org.springframework:spring-orm:7.0.9
|    |    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |    |    |         +--- org.springframework:spring-jdbc:7.0.9 (*)
|    |    |    |         \--- org.springframework:spring-tx:7.0.9 (*)
|    |    |    +--- org.hibernate.orm:hibernate-core:7.4.5.Final (*)
|    |    |    \--- org.springframework:spring-orm:7.0.9 (*)
|    |    +--- org.springframework.data:spring-data-jpa:4.1.1
|    |    |    +--- org.springframework.data:spring-data-commons:4.1.1 (*)
|    |    |    +--- org.springframework:spring-orm:7.0.9 (*)
|    |    |    +--- org.springframework:spring-context:7.0.9 (*)
|    |    |    +--- org.springframework:spring-aop:7.0.9 (*)
|    |    |    +--- org.springframework:spring-tx:7.0.9 (*)
|    |    |    +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    +--- org.springframework:spring-core:7.0.9 (*)
|    |    |    +--- org.antlr:antlr4-runtime:4.13.2
|    |    |    +--- jakarta.annotation:jakarta.annotation-api:2.0.0 -> 3.0.0
|    |    |    \--- org.slf4j:slf4j-api:2.0.18
|    |    \--- org.springframework:spring-aspects:7.0.9
|    |         \--- org.aspectj:aspectjweaver:1.9.25 -> 1.9.25.1
|    \--- org.springframework.boot:spring-boot-jdbc:4.1.1 (*)
+--- org.springframework.boot:spring-boot-starter-data-redis -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-data-redis:4.1.1
|    |    +--- org.springframework.boot:spring-boot-netty:4.1.1
|    |    |    +--- io.netty:netty-common:4.2.17.Final
|    |    |    \--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-data-commons:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-transaction:4.1.1 (*)
|    |    +--- io.lettuce:lettuce-core:7.5.2.RELEASE
|    |    |    +--- redis.clients.authentication:redis-authx-core:0.1.1-beta2
|    |    |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|    |    |    +--- io.netty:netty-common:4.2.13.Final -> 4.2.17.Final
|    |    |    +--- io.netty:netty-handler:4.2.13.Final -> 4.2.17.Final
|    |    |    |    +--- io.netty:netty-common:4.2.17.Final
|    |    |    |    +--- io.netty:netty-resolver:4.2.17.Final
|    |    |    |    |    \--- io.netty:netty-common:4.2.17.Final
|    |    |    |    +--- io.netty:netty-buffer:4.2.17.Final
|    |    |    |    |    \--- io.netty:netty-common:4.2.17.Final
|    |    |    |    +--- io.netty:netty-transport:4.2.17.Final
|    |    |    |    |    +--- io.netty:netty-common:4.2.17.Final
|    |    |    |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|    |    |    |    |    \--- io.netty:netty-resolver:4.2.17.Final (*)
|    |    |    |    +--- io.netty:netty-transport-native-unix-common:4.2.17.Final
|    |    |    |    |    +--- io.netty:netty-common:4.2.17.Final
|    |    |    |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|    |    |    |    |    \--- io.netty:netty-transport:4.2.17.Final (*)
|    |    |    |    \--- io.netty:netty-codec-base:4.2.17.Final
|    |    |    |         +--- io.netty:netty-common:4.2.17.Final
|    |    |    |         +--- io.netty:netty-buffer:4.2.17.Final (*)
|    |    |    |         \--- io.netty:netty-transport:4.2.17.Final (*)
|    |    |    +--- io.netty:netty-transport:4.2.13.Final -> 4.2.17.Final (*)
|    |    |    +--- io.projectreactor:reactor-core:3.6.6 -> 3.8.7
|    |    |    |    +--- org.reactivestreams:reactive-streams:1.0.4
|    |    |    |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|    |    |    \--- io.netty:netty-resolver-dns:4.2.13.Final -> 4.2.17.Final
|    |    |         +--- io.netty:netty-common:4.2.17.Final
|    |    |         +--- io.netty:netty-buffer:4.2.17.Final (*)
|    |    |         +--- io.netty:netty-resolver:4.2.17.Final (*)
|    |    |         +--- io.netty:netty-transport:4.2.17.Final (*)
|    |    |         +--- io.netty:netty-codec-base:4.2.17.Final (*)
|    |    |         +--- io.netty:netty-codec-dns:4.2.17.Final
|    |    |         |    +--- io.netty:netty-common:4.2.17.Final
|    |    |         |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|    |    |         |    +--- io.netty:netty-transport:4.2.17.Final (*)
|    |    |         |    \--- io.netty:netty-codec-base:4.2.17.Final (*)
|    |    |         \--- io.netty:netty-handler:4.2.17.Final (*)
|    |    \--- org.springframework.data:spring-data-redis:4.1.1
|    |         +--- org.springframework.data:spring-data-keyvalue:4.1.1
|    |         |    +--- org.springframework.data:spring-data-commons:4.1.1 (*)
|    |         |    +--- org.springframework:spring-context:7.0.9 (*)
|    |         |    +--- org.springframework:spring-tx:7.0.9 (*)
|    |         |    \--- org.slf4j:slf4j-api:2.0.18
|    |         +--- org.springframework:spring-tx:7.0.9 (*)
|    |         +--- org.springframework:spring-oxm:7.0.9
|    |         |    +--- jakarta.xml.bind:jakarta.xml.bind-api:3.0.1 -> 4.0.5 (*)
|    |         |    +--- org.springframework:spring-beans:7.0.9 (*)
|    |         |    \--- org.springframework:spring-core:7.0.9 (*)
|    |         +--- org.springframework:spring-aop:7.0.9 (*)
|    |         +--- org.springframework:spring-context-support:7.0.9 (*)
|    |         \--- org.slf4j:slf4j-api:2.0.18
|    \--- org.springframework:spring-messaging:7.0.9
|         +--- org.springframework:spring-beans:7.0.9 (*)
|         \--- org.springframework:spring-core:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-flyway -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jdbc:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-flyway:4.1.1
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-jdbc:4.1.1 (*)
|    |    \--- org.flywaydb:flyway-core:12.4.0 (*)
|    \--- org.springframework.boot:spring-boot-jdbc:4.1.1 (*)
+--- org.springframework.boot:spring-boot-starter-graphql -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jackson:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-jackson:4.1.1
|    |         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |         \--- tools.jackson.core:jackson-databind:3.1.5 (*)
|    +--- org.springframework.boot:spring-boot-reactor:4.1.1
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    \--- io.projectreactor:reactor-core:3.8.7 (*)
|    \--- org.springframework.boot:spring-boot-graphql:4.1.1
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         \--- org.springframework.graphql:spring-graphql:2.0.5
|              +--- io.micrometer:context-propagation:1.2.1
|              |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|              +--- com.graphql-java:graphql-java:25.0
|              |    +--- com.graphql-java:java-dataloader:6.0.0
|              |    |    +--- org.reactivestreams:reactive-streams:1.0.3 -> 1.0.4
|              |    |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|              |    +--- org.reactivestreams:reactive-streams:1.0.3 -> 1.0.4
|              |    \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|              +--- io.projectreactor:reactor-core:3.8.7 (*)
|              \--- org.springframework:spring-context:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-jetty -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jetty-runtime:4.1.1
|    |    +--- org.springframework.boot:spring-boot-jetty:4.1.1
|    |    |    +--- org.eclipse.jetty.compression:jetty-compression-server:12.1.12
|    |    |    |    +--- org.eclipse.jetty:jetty-server:12.1.12
|    |    |    |    |    +--- org.eclipse.jetty:jetty-http:12.1.12
|    |    |    |    |    |    +--- org.eclipse.jetty:jetty-io:12.1.12
|    |    |    |    |    |    |    +--- org.eclipse.jetty:jetty-util:12.1.12
|    |    |    |    |    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    |    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    |    |    |    +--- org.eclipse.jetty:jetty-util:12.1.12 (*)
|    |    |    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    |    |    +--- org.eclipse.jetty:jetty-io:12.1.12 (*)
|    |    |    |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    |    \--- org.eclipse.jetty.compression:jetty-compression-common:12.1.12
|    |    |    |         +--- org.eclipse.jetty:jetty-http:12.1.12 (*)
|    |    |    |         +--- org.eclipse.jetty:jetty-io:12.1.12 (*)
|    |    |    |         \--- org.eclipse.jetty:jetty-util:12.1.12 (*)
|    |    |    +--- org.eclipse.jetty.compression:jetty-compression-gzip:12.1.12
|    |    |    |    \--- org.eclipse.jetty.compression:jetty-compression-common:12.1.12 (*)
|    |    |    +--- org.springframework.boot:spring-boot-web-server:4.1.1
|    |    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    |    \--- org.springframework:spring-web:7.0.9
|    |    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |    |    |         \--- io.micrometer:micrometer-observation:1.16.7 -> 1.17.1 (*)
|    |    |    \--- org.eclipse.jetty.ee11:jetty-ee11-webapp:12.1.12
|    |    |         +--- org.eclipse.jetty:jetty-session:12.1.12
|    |    |         |    +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |         |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         +--- org.eclipse.jetty:jetty-xml:12.1.12
|    |    |         |    +--- org.eclipse.jetty:jetty-util:12.1.12 (*)
|    |    |         |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         +--- org.eclipse.jetty.ee:jetty-ee-webapp:12.1.12
|    |    |         |    +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |         |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         +--- org.eclipse.jetty.ee11:jetty-ee11-servlet:12.1.12
|    |    |         |    +--- jakarta.servlet:jakarta.servlet-api:6.1.0
|    |    |         |    +--- org.eclipse.jetty:jetty-security:12.1.12
|    |    |         |    |    +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |         |    |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         |    +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |         |    +--- org.eclipse.jetty:jetty-session:12.1.12 (*)
|    |    |         |    \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |         \--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    +--- org.springframework.boot:spring-boot-web-server:4.1.1 (*)
|    |    +--- jakarta.servlet:jakarta.servlet-api:6.1.0
|    |    +--- jakarta.websocket:jakarta.websocket-api:2.2.0
|    |    +--- jakarta.websocket:jakarta.websocket-client-api:2.2.0
|    |    +--- org.apache.tomcat.embed:tomcat-embed-el:11.0.24
|    |    +--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-jakarta-server:12.1.12
|    |    |    +--- jakarta.websocket:jakarta.websocket-api:2.2.0
|    |    |    +--- org.eclipse.jetty.ee11:jetty-ee11-annotations:12.1.12
|    |    |    |    +--- jakarta.annotation:jakarta.annotation-api:3.0.0
|    |    |    |    +--- jakarta.servlet:jakarta.servlet-api:6.1.0
|    |    |    |    +--- org.eclipse.jetty:jetty-annotations:12.1.12
|    |    |    |    |    +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |    |    |    +--- org.ow2.asm:asm:9.10.1
|    |    |    |    |    \--- org.ow2.asm:asm-commons:9.10.1
|    |    |    |    |         +--- org.ow2.asm:asm:9.10.1
|    |    |    |    |         \--- org.ow2.asm:asm-tree:9.10.1
|    |    |    |    |              \--- org.ow2.asm:asm:9.10.1
|    |    |    |    +--- org.eclipse.jetty.ee11:jetty-ee11-plus:12.1.12
|    |    |    |    |    +--- jakarta.enterprise:jakarta.enterprise.cdi-api:4.1.0
|    |    |    |    |    |    +--- jakarta.enterprise:jakarta.enterprise.lang-model:4.1.0
|    |    |    |    |    |    +--- jakarta.annotation:jakarta.annotation-api:3.0.0
|    |    |    |    |    |    +--- jakarta.interceptor:jakarta.interceptor-api:2.2.0
|    |    |    |    |    |    |    \--- jakarta.annotation:jakarta.annotation-api:3.0.0
|    |    |    |    |    |    \--- jakarta.inject:jakarta.inject-api:2.0.1
|    |    |    |    |    +--- jakarta.enterprise:jakarta.enterprise.lang-model:4.1.0
|    |    |    |    |    +--- jakarta.interceptor:jakarta.interceptor-api:2.2.0 (*)
|    |    |    |    |    +--- jakarta.transaction:jakarta.transaction-api:2.0.1
|    |    |    |    |    +--- org.eclipse.jetty:jetty-plus:12.1.12
|    |    |    |    |    |    +--- org.eclipse.jetty:jetty-security:12.1.12 (*)
|    |    |    |    |    |    \--- org.eclipse.jetty:jetty-util:12.1.12 (*)
|    |    |    |    |    \--- org.eclipse.jetty.ee11:jetty-ee11-webapp:12.1.12 (*)
|    |    |    |    +--- org.eclipse.jetty.ee11:jetty-ee11-webapp:12.1.12 (*)
|    |    |    |    +--- org.ow2.asm:asm:9.10.1
|    |    |    |    \--- org.ow2.asm:asm-commons:9.10.1 (*)
|    |    |    +--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-jakarta-client:12.1.12
|    |    |    |    +--- jakarta.websocket:jakarta.websocket-api:2.2.0
|    |    |    |    +--- jakarta.websocket:jakarta.websocket-client-api:2.2.0
|    |    |    |    +--- org.eclipse.jetty:jetty-client:12.1.12
|    |    |    |    |    +--- org.eclipse.jetty:jetty-alpn-client:12.1.12
|    |    |    |    |    |    \--- org.eclipse.jetty:jetty-io:12.1.12 (*)
|    |    |    |    |    +--- org.eclipse.jetty:jetty-http:12.1.12 (*)
|    |    |    |    |    +--- org.eclipse.jetty:jetty-io:12.1.12 (*)
|    |    |    |    |    \--- org.eclipse.jetty.compression:jetty-compression-gzip:12.1.12 (*)
|    |    |    |    +--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-jakarta-common:12.1.12
|    |    |    |    |    +--- jakarta.websocket:jakarta.websocket-api:2.2.0
|    |    |    |    |    +--- jakarta.websocket:jakarta.websocket-client-api:2.2.0
|    |    |    |    |    \--- org.eclipse.jetty.websocket:jetty-websocket-core-client:12.1.12
|    |    |    |    |         +--- org.eclipse.jetty:jetty-client:12.1.12 (*)
|    |    |    |    |         \--- org.eclipse.jetty.websocket:jetty-websocket-core-common:12.1.12
|    |    |    |    |              +--- org.eclipse.jetty:jetty-http:12.1.12 (*)
|    |    |    |    |              \--- org.eclipse.jetty:jetty-io:12.1.12 (*)
|    |    |    |    \--- org.eclipse.jetty.websocket:jetty-websocket-core-client:12.1.12 (*)
|    |    |    \--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-servlet:12.1.12
|    |    |         +--- org.eclipse.jetty.ee11:jetty-ee11-servlet:12.1.12 (*)
|    |    |         \--- org.eclipse.jetty.websocket:jetty-websocket-core-server:12.1.12
|    |    |              +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |    |              \--- org.eclipse.jetty.websocket:jetty-websocket-core-common:12.1.12 (*)
|    |    \--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-jetty-server:12.1.12
|    |         +--- jakarta.servlet:jakarta.servlet-api:6.1.0
|    |         +--- org.eclipse.jetty.ee11:jetty-ee11-annotations:12.1.12 (*)
|    |         +--- org.eclipse.jetty.ee11:jetty-ee11-servlet:12.1.12 (*)
|    |         +--- org.eclipse.jetty.ee11.websocket:jetty-ee11-websocket-servlet:12.1.12 (*)
|    |         +--- org.eclipse.jetty.websocket:jetty-websocket-jetty-api:12.1.12
|    |         +--- org.eclipse.jetty.websocket:jetty-websocket-jetty-common:12.1.12
|    |         |    +--- org.eclipse.jetty.websocket:jetty-websocket-core-common:12.1.12 (*)
|    |         |    \--- org.eclipse.jetty.websocket:jetty-websocket-jetty-api:12.1.12
|    |         \--- org.eclipse.jetty.websocket:jetty-websocket-jetty-server:12.1.12
|    |              +--- org.eclipse.jetty:jetty-server:12.1.12 (*)
|    |              +--- org.eclipse.jetty.websocket:jetty-websocket-core-server:12.1.12 (*)
|    |              \--- org.eclipse.jetty.websocket:jetty-websocket-jetty-common:12.1.12 (*)
|    +--- org.springframework.boot:spring-boot-jetty:4.1.1 (*)
|    +--- org.slf4j:slf4j-api:2.0.18
|    \--- jakarta.annotation:jakarta.annotation-api:3.0.0
+--- org.springframework.boot:spring-boot-starter-kafka -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-kafka:4.1.1
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         +--- org.springframework.boot:spring-boot-transaction:4.1.1 (*)
|         \--- org.springframework.kafka:spring-kafka:4.1.1
|              +--- org.springframework:spring-context:7.0.9 (*)
|              +--- org.springframework:spring-messaging:7.0.9 (*)
|              +--- org.springframework:spring-tx:7.0.9 (*)
|              +--- org.apache.kafka:kafka-clients:4.2.1 (*)
|              \--- io.micrometer:micrometer-observation:1.17.1 (*)
+--- org.springframework.boot:spring-boot-starter-oauth2-client -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-security:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-security:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    +--- org.springframework.security:spring-security-config:7.1.1
|    |    |    |    +--- org.springframework.security:spring-security-core:7.1.1
|    |    |    |    |    +--- org.springframework.security:spring-security-crypto:7.1.1
|    |    |    |    |    +--- org.springframework:spring-aop:7.0.9 (*)
|    |    |    |    |    +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    |    |    +--- org.springframework:spring-context:7.0.9 (*)
|    |    |    |    |    +--- org.springframework:spring-core:7.0.9 (*)
|    |    |    |    |    +--- org.springframework:spring-expression:7.0.9 (*)
|    |    |    |    |    \--- io.micrometer:micrometer-observation:1.17.1 (*)
|    |    |    |    +--- org.springframework:spring-aop:7.0.9 (*)
|    |    |    |    +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |    |    +--- org.springframework:spring-context:7.0.9 (*)
|    |    |    |    \--- org.springframework:spring-core:7.0.9 (*)
|    |    |    \--- org.springframework.security:spring-security-web:7.1.1
|    |    |         +--- org.springframework.security:spring-security-core:7.1.1 (*)
|    |    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |    |         +--- org.springframework:spring-aop:7.0.9 (*)
|    |    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |    |         +--- org.springframework:spring-context:7.0.9 (*)
|    |    |         +--- org.springframework:spring-expression:7.0.9 (*)
|    |    |         \--- org.springframework:spring-web:7.0.9 (*)
|    |    \--- org.springframework:spring-aop:7.0.9 (*)
|    +--- org.springframework.boot:spring-boot-security-oauth2-client:4.1.1
|    |    +--- org.springframework.boot:spring-boot-security:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    \--- org.springframework.security:spring-security-oauth2-client:7.1.1
|    |         +--- org.springframework.security:spring-security-core:7.1.1 (*)
|    |         +--- org.springframework.security:spring-security-oauth2-core:7.1.1
|    |         |    +--- org.springframework.security:spring-security-core:7.1.1 (*)
|    |         |    +--- org.springframework:spring-core:7.0.9 (*)
|    |         |    \--- org.springframework:spring-web:7.0.9 (*)
|    |         +--- org.springframework.security:spring-security-web:7.1.1 (*)
|    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |         \--- com.nimbusds:oauth2-oidc-sdk:11.38.2
|    |              +--- com.github.stephenc.jcip:jcip-annotations:1.0-1
|    |              +--- com.nimbusds:content-type:2.3
|    |              +--- net.minidev:json-smart:2.6.0 (*)
|    |              +--- com.nimbusds:lang-tag:1.7
|    |              \--- com.nimbusds:nimbus-jose-jwt:10.9.1
|    \--- org.springframework.security:spring-security-oauth2-jose:7.1.1
|         +--- org.springframework.security:spring-security-core:7.1.1 (*)
|         +--- org.springframework.security:spring-security-oauth2-core:7.1.1 (*)
|         +--- org.springframework:spring-core:7.0.9 (*)
|         \--- com.nimbusds:nimbus-jose-jwt:10.9.1
+--- org.springframework.boot:spring-boot-starter-oauth2-resource-server -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-security:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-security-oauth2-resource-server:4.1.1
|         +--- org.springframework.boot:spring-boot-security:4.1.1 (*)
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         +--- org.springframework.security:spring-security-oauth2-jose:7.1.1 (*)
|         \--- org.springframework.security:spring-security-oauth2-resource-server:7.1.1
|              +--- org.springframework.security:spring-security-core:7.1.1 (*)
|              +--- org.springframework.security:spring-security-oauth2-core:7.1.1 (*)
|              +--- org.springframework.security:spring-security-web:7.1.1 (*)
|              \--- org.springframework:spring-core:7.0.9 (*)
+--- org.springframework.boot:spring-boot-restclient -> 4.1.1
|    +--- org.springframework.boot:spring-boot-http-converter:4.1.1
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    \--- org.springframework:spring-web:7.0.9 (*)
|    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-http-client:4.1.1
|         +--- org.springframework.boot:spring-boot-http-converter:4.1.1 (*)
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         \--- org.springframework:spring-web:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-validation -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-validation:4.1.1
|         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         +--- org.apache.tomcat.embed:tomcat-embed-el:11.0.24
|         \--- org.hibernate.validator:hibernate-validator:9.1.3.Final
|              +--- jakarta.validation:jakarta.validation-api:3.1.1
|              +--- org.jboss.logging:jboss-logging:3.6.3.Final
|              \--- com.fasterxml:classmate:1.7.1 -> 1.7.3
+--- org.springframework.boot:spring-boot-starter-web -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter-jackson:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-http-converter:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-webmvc:4.1.1
|         +--- org.springframework.boot:spring-boot-http-converter:4.1.1 (*)
|         +--- org.springframework.boot:spring-boot-servlet:4.1.1
|         |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|         |    \--- org.springframework:spring-web:7.0.9 (*)
|         +--- org.springframework:spring-web:7.0.9 (*)
|         \--- org.springframework:spring-webmvc:7.0.9
|              +--- org.springframework:spring-aop:7.0.9 (*)
|              +--- org.springframework:spring-beans:7.0.9 (*)
|              +--- org.springframework:spring-context:7.0.9 (*)
|              +--- org.springframework:spring-core:7.0.9 (*)
|              +--- org.springframework:spring-expression:7.0.9 (*)
|              \--- org.springframework:spring-web:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-webclient -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jackson:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-reactor:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-webclient:4.1.1
|    |    +--- org.springframework.boot:spring-boot-http-codec:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    |    \--- org.springframework:spring-web:7.0.9 (*)
|    |    +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-http-client:4.1.1 (*)
|    |    \--- org.springframework:spring-webflux:7.0.9
|    |         +--- org.springframework:spring-beans:7.0.9 (*)
|    |         +--- org.springframework:spring-core:7.0.9 (*)
|    |         +--- org.springframework:spring-web:7.0.9 (*)
|    |         \--- io.projectreactor:reactor-core:3.8.7 (*)
|    \--- io.projectreactor.netty:reactor-netty-http:1.3.7
|         +--- io.netty:netty-codec-http:4.2.17.Final
|         |    +--- io.netty:netty-common:4.2.17.Final
|         |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-compression:4.2.17.Final
|         |    |    +--- io.netty:netty-common:4.2.17.Final
|         |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    |    \--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    \--- io.netty:netty-handler:4.2.17.Final (*)
|         +--- io.netty:netty-codec-http2:4.2.17.Final
|         |    +--- io.netty:netty-common:4.2.17.Final
|         |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    +--- io.netty:netty-handler:4.2.17.Final (*)
|         |    \--- io.netty:netty-codec-http:4.2.17.Final (*)
|         +--- io.netty:netty-codec-http3:4.2.17.Final
|         |    +--- io.netty:netty-common:4.2.17.Final
|         |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-http:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-compression:4.2.17.Final (*)
|         |    +--- io.netty:netty-handler:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport-native-unix-common:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    +--- io.netty:netty-resolver:4.2.17.Final (*)
|         |    +--- io.netty:netty-codec-classes-quic:4.2.17.Final
|         |    |    +--- io.netty:netty-common:4.2.17.Final
|         |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    |    \--- io.netty:netty-handler:4.2.17.Final (*)
|         |    \--- io.netty:netty-codec-native-quic:4.2.17.Final
|         |         \--- io.netty:netty-codec-classes-quic:4.2.17.Final (*)
|         +--- io.netty:netty-resolver-dns:4.2.17.Final (*)
|         +--- io.netty:netty-resolver-dns-native-macos:4.2.17.Final
|         |    \--- io.netty:netty-resolver-dns-classes-macos:4.2.17.Final
|         |         +--- io.netty:netty-common:4.2.17.Final
|         |         +--- io.netty:netty-resolver-dns:4.2.17.Final (*)
|         |         \--- io.netty:netty-transport-native-unix-common:4.2.17.Final (*)
|         +--- io.netty:netty-transport-native-epoll:4.2.17.Final
|         |    +--- io.netty:netty-common:4.2.17.Final
|         |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport-native-unix-common:4.2.17.Final (*)
|         |    \--- io.netty:netty-transport-classes-epoll:4.2.17.Final
|         |         +--- io.netty:netty-common:4.2.17.Final
|         |         +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |         +--- io.netty:netty-transport:4.2.17.Final (*)
|         |         \--- io.netty:netty-transport-native-unix-common:4.2.17.Final (*)
|         +--- io.projectreactor.netty:reactor-netty-core:1.3.7
|         |    +--- io.netty:netty-handler:4.2.17.Final (*)
|         |    +--- io.netty:netty-handler-proxy:4.2.17.Final
|         |    |    +--- io.netty:netty-common:4.2.17.Final
|         |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-codec-socks:4.2.17.Final
|         |    |    |    +--- io.netty:netty-common:4.2.17.Final
|         |    |    |    +--- io.netty:netty-buffer:4.2.17.Final (*)
|         |    |    |    +--- io.netty:netty-transport:4.2.17.Final (*)
|         |    |    |    \--- io.netty:netty-codec-base:4.2.17.Final (*)
|         |    |    +--- io.netty:netty-codec-http:4.2.17.Final (*)
|         |    |    \--- io.netty:netty-handler:4.2.17.Final (*)
|         |    +--- io.netty:netty-resolver-dns:4.2.17.Final (*)
|         |    +--- io.netty:netty-resolver-dns-native-macos:4.2.17.Final (*)
|         |    +--- io.netty:netty-transport-native-epoll:4.2.17.Final (*)
|         |    +--- io.projectreactor:reactor-core:3.8.7 (*)
|         |    \--- org.jspecify:jspecify:1.0.1
|         +--- io.projectreactor:reactor-core:3.8.7 (*)
|         \--- org.jspecify:jspecify:1.0.1
+--- org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.0
|    +--- org.springdoc:springdoc-openapi-starter-webmvc-api:3.1.0
|    |    +--- org.springdoc:springdoc-openapi-starter-common:3.1.0
|    |    |    +--- org.springframework.boot:spring-boot-starter:4.1.0 -> 4.1.1 (*)
|    |    |    +--- org.springframework.boot:spring-boot-autoconfigure:4.1.0 -> 4.1.1 (*)
|    |    |    +--- org.springframework.boot:spring-boot-validation:4.1.0 -> 4.1.1 (*)
|    |    |    +--- io.swagger.core.v3:swagger-core-jakarta:2.2.52
|    |    |    |    +--- org.apache.commons:commons-lang3:3.20.0
|    |    |    |    +--- org.slf4j:slf4j-api:2.0.17 -> 2.0.18
|    |    |    |    +--- io.swagger.core.v3:swagger-annotations-jakarta:2.2.52
|    |    |    |    +--- io.swagger.core.v3:swagger-models-jakarta:2.2.52
|    |    |    |    |    \--- com.fasterxml.jackson.core:jackson-annotations:2.21
|    |    |    |    +--- org.yaml:snakeyaml:2.6
|    |    |    |    +--- jakarta.xml.bind:jakarta.xml.bind-api:3.0.1 -> 4.0.5 (*)
|    |    |    |    +--- jakarta.validation:jakarta.validation-api:3.0.2 -> 3.1.1
|    |    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|    |    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.1 -> 2.21.5 (*)
|    |    |    |    +--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.22.0 -> 2.21.5
|    |    |    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.5 (*)
|    |    |    |    |    +--- org.yaml:snakeyaml:2.5 -> 2.6
|    |    |    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (*)
|    |    |    |    |    \--- com.fasterxml.jackson:jackson-bom:2.21.5 (*)
|    |    |    |    \--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.22.0 -> 2.21.5
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-core:2.21.5 (*)
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-databind:2.21.5 (*)
|    |    |    |         \--- com.fasterxml.jackson:jackson-bom:2.21.5 (*)
|    |    |    \--- org.springframework.boot:spring-boot-jackson:4.1.0 -> 4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-webmvc:4.1.0 -> 4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-web-server:4.1.0 -> 4.1.1 (*)
|    +--- org.webjars:swagger-ui:5.32.11
|    \--- org.webjars:webjars-locator-lite:1.1.3 -> 1.1.4
|         \--- org.jspecify:jspecify:1.0.0 -> 1.0.1
+--- com.github.ben-manes.caffeine:caffeine -> 3.2.4
|    +--- org.jspecify:jspecify:1.0.0 -> 1.0.1
|    \--- com.google.errorprone:error_prone_annotations:2.49.0
+--- io.kotest:kotest-runner-junit5:6.2.3
|    \--- io.kotest:kotest-runner-junit5-jvm:6.2.3
|         +--- io.kotest:kotest-common:6.2.3
|         |    \--- io.kotest:kotest-common-jvm:6.2.3
|         |         +--- org.jetbrains.kotlin:kotlin-stdlib:2.2.21 -> 2.4.0 (*)
|         |         +--- org.jetbrains.kotlin:kotlin-reflect:2.2.21 -> 2.4.0 (*)
|         |         +--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2
|         |         |    \--- org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.10.2
|         |         |         +--- org.jetbrains:annotations:23.0.0
|         |         |         +--- org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.10.2
|         |         |         |    +--- org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.10.2 (c)
|         |         |         |    +--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2 (c)
|         |         |         |    +--- org.jetbrains.kotlinx:kotlinx-coroutines-debug:1.10.2 (c)
|         |         |         |    +--- org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.10.2 (c)
|         |         |         |    +--- org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2 (c)
|         |         |         |    \--- org.jetbrains.kotlinx:kotlinx-coroutines-test-jvm:1.10.2 (c)
|         |         |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.1.0 -> 2.4.0 (*)
|         |         \--- org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2
|         |              \--- org.jetbrains.kotlinx:kotlinx-coroutines-test-jvm:1.10.2
|         |                   +--- org.jetbrains:annotations:23.0.0
|         |                   +--- org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.10.2 (*)
|         |                   +--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2 (*)
|         |                   \--- org.jetbrains.kotlin:kotlin-stdlib:2.1.0 -> 2.4.0 (*)
|         +--- io.kotest:kotest-framework-engine:6.2.3
|         |    \--- io.kotest:kotest-framework-engine-jvm:6.2.3
|         |         +--- org.opentest4j:opentest4j:1.3.0
|         |         +--- org.jetbrains.kotlinx:kotlinx-coroutines-debug:1.10.2
|         |         |    +--- net.bytebuddy:byte-buddy:1.10.9 -> 1.18.11
|         |         |    +--- net.bytebuddy:byte-buddy-agent:1.10.9 -> 1.18.11
|         |         |    +--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2 (*)
|         |         |    +--- org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.10.2 (*)
|         |         |    +--- net.java.dev.jna:jna:5.9.0 -> 5.18.1
|         |         |    +--- net.java.dev.jna:jna-platform:5.9.0
|         |         |    |    \--- net.java.dev.jna:jna:5.9.0 -> 5.18.1
|         |         |    \--- org.jetbrains.kotlin:kotlin-stdlib:2.1.0 -> 2.4.0 (*)
|         |         +--- io.kotest:kotest-common:6.2.3 (*)
|         |         +--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2 (*)
|         |         +--- org.jetbrains.kotlin:kotlin-stdlib:2.2.21 -> 2.4.0 (*)
|         |         +--- io.github.classgraph:classgraph:4.8.184
|         |         +--- io.kotest:kotest-assertions-shared:6.2.3
|         |         |    \--- io.kotest:kotest-assertions-shared-jvm:6.2.3
|         |         |         +--- org.jetbrains.kotlin:kotlin-stdlib:2.2.21 -> 2.4.0 (*)
|         |         |         +--- org.opentest4j:opentest4j:1.3.0
|         |         |         +--- io.kotest:kotest-common:6.2.3 (*)
|         |         |         \--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2 (*)
|         |         +--- org.jetbrains.kotlin:kotlin-reflect:2.2.21 -> 2.4.0 (*)
|         |         +--- org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2 (*)
|         |         +--- io.github.pdvrieze.xmlutil:serialization:0.91.3
|         |         |    \--- io.github.pdvrieze.xmlutil:serialization-jvm:0.91.3
|         |         |         +--- io.github.pdvrieze.xmlutil:core:0.91.3
|         |         |         |    \--- io.github.pdvrieze.xmlutil:core-jvmcommon:0.91.3
|         |         |         |         +--- org.jetbrains.kotlin:kotlin-stdlib:2.2.21 -> 2.4.0 (*)
|         |         |         |         \--- org.jetbrains.kotlinx:kotlinx-serialization-core:1.9.0 -> 1.11.0
|         |         |         |              \--- org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.11.0
|         |         |         |                   +--- org.jetbrains.kotlinx:kotlinx-serialization-bom:1.11.0
|         |         |         |                   |    +--- org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.11.0 (c)
|         |         |         |                   |    \--- org.jetbrains.kotlinx:kotlinx-serialization-core:1.11.0 (c)
|         |         |         |                   \--- org.jetbrains.kotlin:kotlin-stdlib:2.3.20 -> 2.4.0 (*)
|         |         |         +--- org.jetbrains.kotlinx:kotlinx-serialization-core:1.9.0 -> 1.11.0 (*)
|         |         |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.2.21 -> 2.4.0 (*)
|         |         \--- org.jetbrains.kotlinx:kotlinx-io-core:0.8.2
|         |              \--- org.jetbrains.kotlinx:kotlinx-io-core-jvm:0.8.2
|         |                   +--- org.jetbrains.kotlinx:kotlinx-io-bytestring:0.8.2
|         |                   |    \--- org.jetbrains.kotlinx:kotlinx-io-bytestring-jvm:0.8.2
|         |                   |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.2.0 -> 2.4.0 (*)
|         |                   \--- org.jetbrains.kotlin:kotlin-stdlib:2.2.0 -> 2.4.0 (*)
|         +--- io.kotest:kotest-runner-junit-platform:6.2.3
|         |    \--- io.kotest:kotest-runner-junit-platform-jvm:6.2.3
|         |         +--- io.kotest:kotest-common:6.2.3 (*)
|         |         +--- io.kotest:kotest-framework-engine:6.2.3 (*)
|         |         +--- io.kotest:kotest-assertions-core:6.2.3
|         |         |    \--- io.kotest:kotest-assertions-core-jvm:6.2.3
|         |         |         +--- io.kotest:kotest-assertions-shared:6.2.3 (*)
|         |         |         +--- org.jetbrains.kotlin:kotlin-stdlib:2.2.21 -> 2.4.0 (*)
|         |         |         +--- org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.10.2
|         |         |         |    +--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2 (*)
|         |         |         |    +--- org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.10.2 (*)
|         |         |         |    \--- org.jetbrains.kotlin:kotlin-stdlib:2.1.0 -> 2.4.0 (*)
|         |         |         +--- io.github.java-diff-utils:java-diff-utils:4.16
|         |         |         +--- org.jetbrains.kotlin:kotlin-reflect:2.2.21 -> 2.4.0 (*)
|         |         |         +--- io.kotest:kotest-common:6.2.3 (*)
|         |         |         \--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2 (*)
|         |         +--- io.kotest:kotest-extensions:6.2.3
|         |         |    \--- io.kotest:kotest-extensions-jvm:6.2.3
|         |         |         +--- org.jetbrains.kotlin:kotlin-stdlib:2.2.21 -> 2.4.0 (*)
|         |         |         +--- org.jetbrains.kotlin:kotlin-reflect:2.2.21 -> 2.4.0 (*)
|         |         |         +--- io.kotest:kotest-framework-engine:6.2.3 (*)
|         |         |         \--- io.kotest:kotest-common:6.2.3 (*)
|         |         +--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2 (*)
|         |         +--- org.junit.platform:junit-platform-suite-api:1.13.4 -> 6.0.3
|         |         |    +--- org.junit:junit-bom:6.0.3
|         |         |    |    +--- org.junit.jupiter:junit-jupiter:6.0.3 (c)
|         |         |    |    +--- org.junit.jupiter:junit-jupiter-api:6.0.3 (c)
|         |         |    |    +--- org.junit.jupiter:junit-jupiter-engine:6.0.3 (c)
|         |         |    |    +--- org.junit.jupiter:junit-jupiter-params:6.0.3 (c)
|         |         |    |    +--- org.junit.platform:junit-platform-commons:6.0.3 (c)
|         |         |    |    +--- org.junit.platform:junit-platform-engine:6.0.3 (c)
|         |         |    |    +--- org.junit.platform:junit-platform-launcher:6.0.3 (c)
|         |         |    |    \--- org.junit.platform:junit-platform-suite-api:6.0.3 (c)
|         |         |    \--- org.junit.platform:junit-platform-commons:6.0.3
|         |         |         \--- org.junit:junit-bom:6.0.3 (*)
|         |         +--- org.junit.platform:junit-platform-launcher:1.13.4 -> 6.0.3
|         |         |    +--- org.junit:junit-bom:6.0.3 (*)
|         |         |    \--- org.junit.platform:junit-platform-engine:6.0.3
|         |         |         +--- org.junit:junit-bom:6.0.3 (*)
|         |         |         +--- org.opentest4j:opentest4j:1.3.0
|         |         |         \--- org.junit.platform:junit-platform-commons:6.0.3 (*)
|         |         +--- org.junit.platform:junit-platform-engine:1.13.4 -> 6.0.3 (*)
|         |         +--- org.jetbrains.kotlin:kotlin-stdlib:2.2.21 -> 2.4.0 (*)
|         |         \--- org.jetbrains.kotlin:kotlin-reflect:2.2.21 -> 2.4.0 (*)
|         +--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2 (*)
|         +--- org.junit.platform:junit-platform-engine:1.13.4 -> 6.0.3 (*)
|         +--- org.junit.platform:junit-platform-suite-api:1.13.4 -> 6.0.3 (*)
|         +--- org.junit.platform:junit-platform-launcher:1.13.4 -> 6.0.3 (*)
|         +--- org.junit.jupiter:junit-jupiter-api:5.13.4 -> 6.0.3
|         |    +--- org.junit:junit-bom:6.0.3 (*)
|         |    +--- org.opentest4j:opentest4j:1.3.0
|         |    \--- org.junit.platform:junit-platform-commons:6.0.3 (*)
|         +--- org.jetbrains.kotlin:kotlin-stdlib:2.2.21 -> 2.4.0 (*)
|         \--- org.jetbrains.kotlin:kotlin-reflect:2.2.21 -> 2.4.0 (*)
+--- io.kotest:kotest-assertions-core:6.2.3 (*)
+--- io.kotest:kotest-extensions-spring:6.2.3
|    \--- io.kotest:kotest-extensions-spring-jvm:6.2.3
|         +--- org.jetbrains.kotlin:kotlin-stdlib:2.2.21 -> 2.4.0 (*)
|         +--- io.kotest:kotest-framework-engine:6.2.3 (*)
|         +--- org.jetbrains.kotlin:kotlin-reflect:2.2.21 -> 2.4.0 (*)
|         +--- org.springframework:spring-context:5.3.39 -> 7.0.9 (*)
|         +--- org.springframework:spring-test:5.3.39 -> 7.0.9
|         |    \--- org.springframework:spring-core:7.0.9 (*)
|         \--- net.bytebuddy:byte-buddy:1.18.8 -> 1.18.11
+--- org.springframework.boot:spring-boot-micrometer-metrics-test -> 4.1.1
|    +--- org.springframework.boot:spring-boot-test-autoconfigure:4.1.1
|    |    \--- org.springframework.boot:spring-boot-test:4.1.1
|    |         +--- org.springframework.boot:spring-boot:4.1.1 (*)
|    |         \--- org.springframework:spring-test:7.0.9 (*)
|    +--- org.springframework.boot:spring-boot-micrometer-metrics:4.1.1 (*)
|    \--- io.micrometer:micrometer-observation-test:1.17.1
|         +--- org.jspecify:jspecify:1.0.1
|         +--- io.micrometer:micrometer-observation:1.17.1 (*)
|         +--- org.assertj:assertj-core:3.27.7
|         |    \--- net.bytebuddy:byte-buddy:1.18.3 -> 1.18.11
|         +--- org.junit.jupiter:junit-jupiter:5.14.4 -> 6.0.3
|         |    +--- org.junit.jupiter:junit-jupiter-engine:6.0.3
|         |    |    +--- org.junit:junit-bom:6.0.3 (*)
|         |    |    +--- org.junit.platform:junit-platform-engine:6.0.3 (*)
|         |    |    \--- org.junit.jupiter:junit-jupiter-api:6.0.3 (*)
|         |    +--- org.junit:junit-bom:6.0.3 (*)
|         |    +--- org.junit.jupiter:junit-jupiter-api:6.0.3 (*)
|         |    \--- org.junit.jupiter:junit-jupiter-params:6.0.3
|         |         +--- org.junit:junit-bom:6.0.3 (*)
|         |         \--- org.junit.jupiter:junit-jupiter-api:6.0.3 (*)
|         \--- org.mockito:mockito-core:4.11.0 -> 5.23.0
|              +--- net.bytebuddy:byte-buddy:1.17.7 -> 1.18.11
|              +--- net.bytebuddy:byte-buddy-agent:1.17.7 -> 1.18.11
|              \--- org.objenesis:objenesis:3.3
+--- com.redis:testcontainers-redis -> 2.2.4
|    +--- org.testcontainers:testcontainers:1.20.4 -> 2.0.5
|    |    +--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|    |    +--- org.apache.commons:commons-compress:1.28.0 (*)
|    |    +--- org.rnorth.duct-tape:duct-tape:1.0.8
|    |    |    \--- org.jetbrains:annotations:17.0.0 -> 23.0.0
|    |    +--- com.github.docker-java:docker-java-api:3.7.1
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.20 -> 2.21
|    |    |    \--- org.slf4j:slf4j-api:1.7.30 -> 2.0.18
|    |    \--- com.github.docker-java:docker-java-transport-zerodep:3.7.1
|    |         +--- com.github.docker-java:docker-java-transport:3.7.1
|    |         +--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|    |         \--- net.java.dev.jna:jna:5.18.1
|    \--- com.redis:testcontainers-redis-common:2.2.4
+--- org.testcontainers:testcontainers-junit-jupiter -> 2.0.5
|    \--- org.testcontainers:testcontainers:2.0.5 (*)
+--- org.testcontainers:testcontainers-postgresql -> 2.0.5
|    \--- org.testcontainers:testcontainers-jdbc:2.0.5
|         \--- org.testcontainers:testcontainers-database-commons:2.0.5
|              \--- org.testcontainers:testcontainers:2.0.5 (*)
+--- org.springframework.boot:spring-boot-starter-data-jpa-test -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter-data-jpa:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-test:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-test:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-test-autoconfigure:4.1.1 (*)
|    |    +--- com.jayway.jsonpath:json-path:2.10.0 (*)
|    |    +--- jakarta.xml.bind:jakarta.xml.bind-api:4.0.5 (*)
|    |    +--- net.minidev:json-smart:2.6.0 (*)
|    |    +--- org.assertj:assertj-core:3.27.7 (*)
|    |    +--- org.awaitility:awaitility:4.3.0
|    |    |    \--- org.hamcrest:hamcrest:2.1 -> 3.0
|    |    +--- org.hamcrest:hamcrest:3.0
|    |    +--- org.junit.jupiter:junit-jupiter:6.0.3 (*)
|    |    +--- org.mockito:mockito-core:5.23.0 (*)
|    |    +--- org.mockito:mockito-junit-jupiter:5.23.0
|    |    |    +--- org.mockito:mockito-core:5.23.0 (*)
|    |    |    \--- org.junit.jupiter:junit-jupiter-api:5.13.4 -> 6.0.3 (*)
|    |    +--- org.skyscreamer:jsonassert:1.5.3
|    |    |    \--- com.vaadin.external.google:android-json:0.0.20131108.vaadin1
|    |    +--- org.springframework:spring-core:7.0.9 (*)
|    |    +--- org.springframework:spring-test:7.0.9 (*)
|    |    +--- org.xmlunit:xmlunit-core:2.11.0
|    |    \--- org.junit.platform:junit-platform-launcher -> 6.0.3 (*)
|    +--- org.springframework.boot:spring-boot-starter-jdbc-test:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter-jdbc:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-starter-test:4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-jdbc-test:4.1.1
|    |         +--- org.springframework.boot:spring-boot-test-autoconfigure:4.1.1 (*)
|    |         \--- org.springframework.boot:spring-boot-jdbc:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-data-jpa-test:4.1.1
|         +--- org.springframework.boot:spring-boot-test-autoconfigure:4.1.1 (*)
|         +--- org.springframework.boot:spring-boot-data-jpa:4.1.1 (*)
|         \--- org.springframework.boot:spring-boot-jpa-test:4.1.1
|              +--- org.springframework.boot:spring-boot-test-autoconfigure:4.1.1 (*)
|              +--- org.springframework.boot:spring-boot-jdbc-test:4.1.1 (*)
|              \--- org.springframework.boot:spring-boot-jpa:4.1.1 (*)
+--- org.springframework.boot:spring-boot-starter-data-redis-test -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter-data-redis:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-test:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-data-redis-test:4.1.1
|         +--- org.springframework.boot:spring-boot-test-autoconfigure:4.1.1 (*)
|         \--- org.springframework.boot:spring-boot-data-redis:4.1.1 (*)
+--- org.springframework.boot:spring-boot-starter-kafka-test -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter-kafka:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-test:4.1.1 (*)
|    \--- org.springframework.kafka:spring-kafka-test:4.1.1
|         +--- org.slf4j:slf4j-api:2.0.18
|         +--- org.springframework:spring-context:7.0.9 (*)
|         +--- org.springframework:spring-test:7.0.9 (*)
|         +--- org.apache.kafka:kafka-clients:4.2.1 (*)
|         +--- org.apache.kafka:kafka-server:4.2.1
|         |    +--- org.apache.kafka:kafka-clients:4.2.1 (*)
|         |    +--- org.apache.kafka:kafka-metadata:4.2.1
|         |    |    +--- org.apache.kafka:kafka-server-common:4.2.1
|         |    |    |    +--- com.yammer.metrics:metrics-core:2.2.0
|         |    |    |    |    \--- org.slf4j:slf4j-api:1.7.2 -> 2.0.18
|         |    |    |    +--- net.sf.jopt-simple:jopt-simple:5.0.4
|         |    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.2 -> 2.21.5 (*)
|         |    |    |    +--- org.pcollections:pcollections:4.0.2
|         |    |    |    +--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    |    |    \--- org.apache.kafka:kafka-clients:4.2.1 (*)
|         |    |    +--- org.apache.kafka:kafka-clients:4.2.1 (*)
|         |    |    +--- org.apache.kafka:kafka-raft:4.2.1
|         |    |    |    +--- org.apache.kafka:kafka-server-common:4.2.1 (*)
|         |    |    |    +--- org.apache.kafka:kafka-clients:4.2.1 (*)
|         |    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.2 -> 2.21.5 (*)
|         |    |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.2 -> 2.21.5 (*)
|         |    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.21.2 -> 2.21.5 (*)
|         |    |    +--- com.yammer.metrics:metrics-core:2.2.0 (*)
|         |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    +--- org.apache.kafka:kafka-server-common:4.2.1 (*)
|         |    +--- org.apache.kafka:kafka-storage:4.2.1
|         |    |    +--- org.apache.kafka:kafka-metadata:4.2.1 (*)
|         |    |    +--- org.apache.kafka:kafka-storage-api:4.2.1
|         |    |    |    +--- org.apache.kafka:kafka-clients:4.2.1 (*)
|         |    |    |    +--- org.apache.kafka:kafka-server-common:4.2.1 (*)
|         |    |    |    +--- com.yammer.metrics:metrics-core:2.2.0 (*)
|         |    |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    |    +--- org.apache.kafka:kafka-server-common:4.2.1 (*)
|         |    |    +--- org.apache.kafka:kafka-clients:4.2.1 (*)
|         |    |    +--- com.github.ben-manes.caffeine:caffeine:3.2.0 -> 3.2.4 (*)
|         |    |    +--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.2 -> 2.21.5 (*)
|         |    |    \--- com.yammer.metrics:metrics-core:2.2.0 (*)
|         |    +--- org.apache.kafka:kafka-group-coordinator:4.2.1
|         |    |    +--- org.apache.kafka:kafka-server-common:4.2.1 (*)
|         |    |    +--- org.apache.kafka:kafka-clients:4.2.1 (*)
|         |    |    +--- org.apache.kafka:kafka-metadata:4.2.1 (*)
|         |    |    +--- org.apache.kafka:kafka-group-coordinator-api:4.2.1
|         |    |    |    \--- org.apache.kafka:kafka-clients:4.2.1 (*)
|         |    |    +--- org.apache.kafka:kafka-storage:4.2.1 (*)
|         |    |    +--- org.apache.kafka:kafka-coordinator-common:4.2.1
|         |    |    |    +--- org.apache.kafka:kafka-clients:4.2.1 (*)
|         |    |    |    +--- org.apache.kafka:kafka-server-common:4.2.1 (*)
|         |    |    |    +--- org.apache.kafka:kafka-metadata:4.2.1 (*)
|         |    |    |    +--- org.apache.kafka:kafka-storage:4.2.1 (*)
|         |    |    |    +--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    |    |    +--- com.yammer.metrics:metrics-core:2.2.0 (*)
|         |    |    |    \--- org.hdrhistogram:HdrHistogram:2.2.2
|         |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.2 -> 2.21.5 (*)
|         |    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.21.2 -> 2.21.5 (*)
|         |    |    +--- com.yammer.metrics:metrics-core:2.2.0 (*)
|         |    |    +--- org.hdrhistogram:HdrHistogram:2.2.2
|         |    |    +--- com.google.re2j:re2j:1.8
|         |    |    +--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    |    \--- com.dynatrace.hash4j:hash4j:0.22.0
|         |    +--- org.apache.kafka:kafka-transaction-coordinator:4.2.1
|         |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.2 -> 2.21.5 (*)
|         |    |    +--- org.apache.kafka:kafka-clients:4.2.1 (*)
|         |    |    +--- org.apache.kafka:kafka-server-common:4.2.1 (*)
|         |    |    +--- org.apache.kafka:kafka-coordinator-common:4.2.1 (*)
|         |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    +--- org.apache.kafka:kafka-raft:4.2.1 (*)
|         |    +--- org.apache.kafka:kafka-share-coordinator:4.2.1
|         |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.2 -> 2.21.5 (*)
|         |    |    +--- org.apache.kafka:kafka-clients:4.2.1 (*)
|         |    |    +--- org.apache.kafka:kafka-coordinator-common:4.2.1 (*)
|         |    |    +--- org.apache.kafka:kafka-metadata:4.2.1 (*)
|         |    |    +--- org.apache.kafka:kafka-server-common:4.2.1 (*)
|         |    |    +--- com.yammer.metrics:metrics-core:2.2.0 (*)
|         |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    +--- org.apache.kafka:kafka-storage-api:4.2.1 (*)
|         |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.2 -> 2.21.5 (*)
|         |    +--- com.yammer.metrics:metrics-core:2.2.0 (*)
|         |    +--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    +--- org.apache.logging.log4j:log4j-api:2.25.4 -> 2.25.5
|         |    \--- org.apache.logging.log4j:log4j-core:2.25.4 -> 2.25.5
|         |         \--- org.apache.logging.log4j:log4j-api:2.25.5
|         +--- org.apache.kafka:kafka-test-common-runtime:4.2.1
|         |    +--- org.apache.kafka:kafka-server:4.2.1 (*)
|         |    +--- org.apache.kafka:kafka-server-common:4.2.1 (*)
|         |    +--- org.apache.kafka:kafka-group-coordinator:4.2.1 (*)
|         |    +--- org.apache.kafka:kafka-test-common-internal-api:4.2.1
|         |    |    +--- org.apache.kafka:kafka-server-common:4.2.1 (*)
|         |    |    \--- org.junit.jupiter:junit-jupiter-api:5.13.1 -> 6.0.3 (*)
|         |    +--- org.apache.kafka:kafka-metadata:4.2.1 (*)
|         |    +--- org.apache.kafka:kafka-raft:4.2.1 (*)
|         |    +--- org.apache.kafka:kafka-storage:4.2.1 (*)
|         |    +--- org.junit.platform:junit-platform-launcher:1.13.1 -> 6.0.3 (*)
|         |    +--- org.junit.jupiter:junit-jupiter:5.13.1 -> 6.0.3 (*)
|         |    +--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.21.2 -> 2.21.5 (*)
|         |    +--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    +--- org.apache.kafka:kafka_2.13:4.2.1
|         |    |    +--- org.apache.kafka:kafka-server-common:4.2.1 (*)
|         |    |    +--- org.apache.kafka:kafka-group-coordinator-api:4.2.1 (*)
|         |    |    +--- org.apache.kafka:kafka-group-coordinator:4.2.1 (*)
|         |    |    +--- org.apache.kafka:kafka-transaction-coordinator:4.2.1 (*)
|         |    |    +--- org.apache.kafka:kafka-metadata:4.2.1 (*)
|         |    |    +--- org.apache.kafka:kafka-storage-api:4.2.1 (*)
|         |    |    +--- org.apache.kafka:kafka-tools-api:4.2.1
|         |    |    |    \--- org.apache.kafka:kafka-clients:4.2.1 (*)
|         |    |    +--- org.apache.kafka:kafka-raft:4.2.1 (*)
|         |    |    +--- org.apache.kafka:kafka-storage:4.2.1 (*)
|         |    |    +--- org.apache.kafka:kafka-server:4.2.1 (*)
|         |    |    +--- org.apache.kafka:kafka-coordinator-common:4.2.1 (*)
|         |    |    +--- org.apache.kafka:kafka-share-coordinator:4.2.1 (*)
|         |    |    +--- net.sourceforge.argparse4j:argparse4j:0.7.0
|         |    |    +--- commons-validator:commons-validator:1.10.1
|         |    |    |    +--- commons-beanutils:commons-beanutils:1.11.0
|         |    |    |    |    +--- commons-logging:commons-logging:1.3.5 -> 1.3.6
|         |    |    |    |    \--- commons-collections:commons-collections:3.2.2
|         |    |    |    +--- commons-digester:commons-digester:2.1
|         |    |    |    +--- commons-logging:commons-logging:1.3.5 -> 1.3.6
|         |    |    |    \--- commons-collections:commons-collections:3.2.2
|         |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.2 -> 2.21.5 (*)
|         |    |    +--- com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.21.2 -> 2.21.5 (*)
|         |    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.21.2 -> 2.21.5 (*)
|         |    |    +--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.21.2 -> 2.21.5 (*)
|         |    |    +--- net.sf.jopt-simple:jopt-simple:5.0.4
|         |    |    +--- org.bitbucket.b_c:jose4j:0.9.6
|         |    |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    |    +--- com.yammer.metrics:metrics-core:2.2.0 (*)
|         |    |    +--- org.scala-lang:scala-reflect:2.13.17
|         |    |    |    \--- org.scala-lang:scala-library:2.13.17
|         |    |    +--- com.typesafe.scala-logging:scala-logging_2.13:3.9.6
|         |    |    |    +--- org.scala-lang:scala-library:2.13.16 -> 2.13.17
|         |    |    |    +--- org.scala-lang:scala-reflect:2.13.16 -> 2.13.17 (*)
|         |    |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    |    +--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    |    +--- com.google.re2j:re2j:1.8
|         |    |    +--- org.apache.kafka:kafka-clients:4.2.1 (*)
|         |    |    \--- org.scala-lang:scala-library:2.13.17
|         |    \--- org.apache.kafka:kafka-clients:4.2.1 (*)
|         +--- org.apache.kafka:kafka-metadata:4.2.1 (*)
|         +--- org.apache.kafka:kafka-server-common:4.2.1 (*)
|         +--- org.apache.kafka:kafka-streams-test-utils:4.2.1
|         |    +--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    +--- org.apache.kafka:kafka-streams:4.2.1
|         |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.21
|         |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.21.2 -> 2.21.5 (*)
|         |    |    +--- org.slf4j:slf4j-api:1.7.36 -> 2.0.18
|         |    |    +--- org.apache.kafka:kafka-clients:4.2.1 (*)
|         |    |    \--- org.rocksdb:rocksdbjni:10.1.3
|         |    \--- org.apache.kafka:kafka-clients:4.2.1 (*)
|         +--- org.junit.jupiter:junit-jupiter-api:6.0.3 (*)
|         \--- org.junit.platform:junit-platform-launcher:6.0.3 (*)
+--- org.springframework.boot:spring-boot-starter-restclient-test -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter-restclient:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-starter-jackson:4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-restclient:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-jackson-test:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter-jackson:4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-starter-test:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-test:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-restclient-test:4.1.1
|         +--- org.springframework.boot:spring-boot-test-autoconfigure:4.1.1 (*)
|         \--- org.springframework.boot:spring-boot-restclient:4.1.1 (*)
+--- org.springframework.boot:spring-boot-starter-webmvc-test -> 4.1.1
|    +--- org.springframework.boot:spring-boot-starter-jackson-test:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-test:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-starter-webmvc:4.1.1
|    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-starter-jackson:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-starter-tomcat:4.1.1
|    |    |    +--- org.springframework.boot:spring-boot-starter:4.1.1 (*)
|    |    |    +--- org.springframework.boot:spring-boot-starter-tomcat-runtime:4.1.1
|    |    |    |    +--- org.springframework.boot:spring-boot-tomcat:4.1.1
|    |    |    |    |    +--- org.springframework.boot:spring-boot-web-server:4.1.1 (*)
|    |    |    |    |    +--- org.apache.tomcat.embed:tomcat-embed-core:11.0.24
|    |    |    |    |    \--- jakarta.annotation:jakarta.annotation-api:3.0.0
|    |    |    |    +--- org.springframework.boot:spring-boot-web-server:4.1.1 (*)
|    |    |    |    +--- jakarta.annotation:jakarta.annotation-api:3.0.0
|    |    |    |    +--- org.apache.tomcat.embed:tomcat-embed-core:11.0.24
|    |    |    |    +--- org.apache.tomcat.embed:tomcat-embed-el:11.0.24
|    |    |    |    \--- org.apache.tomcat.embed:tomcat-embed-websocket:11.0.24
|    |    |    |         \--- org.apache.tomcat.embed:tomcat-embed-core:11.0.24
|    |    |    \--- org.springframework.boot:spring-boot-tomcat:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-http-converter:4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-webmvc:4.1.1 (*)
|    +--- org.springframework.boot:spring-boot-webmvc-test:4.1.1
|    |    +--- org.springframework.boot:spring-boot-http-converter:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-web-server:4.1.1 (*)
|    |    +--- org.springframework.boot:spring-boot-test-autoconfigure:4.1.1 (*)
|    |    \--- org.springframework.boot:spring-boot-webmvc:4.1.1 (*)
|    \--- org.springframework.boot:spring-boot-resttestclient:4.1.1
|         +--- org.springframework.boot:spring-boot-test:4.1.1 (*)
|         +--- org.springframework.boot:spring-boot-http-converter:4.1.1 (*)
|         \--- org.springframework:spring-web:7.0.9 (*)
+--- org.springframework.boot:spring-boot-starter-test -> 4.1.1 (*)
+--- org.springframework.boot:spring-boot-testcontainers -> 4.1.1
|    +--- org.springframework.boot:spring-boot-autoconfigure:4.1.1 (*)
|    \--- org.testcontainers:testcontainers:2.0.5 (*)
+--- org.springframework.security:spring-security-test -> 7.1.1
|    +--- org.springframework.security:spring-security-core:7.1.1 (*)
|    +--- org.springframework.security:spring-security-web:7.1.1 (*)
|    +--- org.springframework:spring-core:7.0.9 (*)
|    \--- org.springframework:spring-test:7.0.9 (*)
+--- no.nav.security:mock-oauth2-server:6.0.0
|    +--- org.jetbrains.kotlin:kotlin-stdlib -> 2.4.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect -> 2.4.0 (*)
|    +--- tools.jackson.core:jackson-databind:3.2.1 -> 3.1.5 (*)
|    +--- io.netty:netty-codec-http:4.2.16.Final -> 4.2.17.Final (*)
|    +--- io.github.microutils:kotlin-logging:3.0.5
|    |    \--- io.github.microutils:kotlin-logging-jvm:3.0.5
|    |         +--- org.slf4j:slf4j-api:2.0.3 -> 2.0.18
|    |         +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0 -> 2.4.0 (*)
|    |         \--- org.jetbrains.kotlin:kotlin-stdlib-common:1.8.0 -> 2.4.0
|    |              \--- org.jetbrains.kotlin:kotlin-stdlib:2.4.0 (*)
|    +--- tools.jackson.module:jackson-module-kotlin:3.2.1 -> 3.1.5 (*)
|    +--- org.freemarker:freemarker:2.3.34
|    +--- org.bouncycastle:bcpkix-jdk18on:1.85
|    |    \--- org.bouncycastle:bcutil-jdk18on:1.85
|    |         \--- org.bouncycastle:bcprov-jdk18on:1.85
|    +--- com.squareup.okhttp3:mockwebserver:5.4.0
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:2.1.21 -> 2.4.0 (*)
|    |    +--- com.squareup.okhttp3:mockwebserver3:5.4.0
|    |    |    +--- org.jetbrains.kotlin:kotlin-stdlib:2.1.21 -> 2.4.0 (*)
|    |    |    \--- com.squareup.okhttp3:okhttp:5.4.0 (*)
|    |    +--- junit:junit:4.13.2
|    |    |    \--- org.hamcrest:hamcrest-core:1.3 -> 3.0
|    |    |         \--- org.hamcrest:hamcrest:3.0
|    |    +--- com.squareup.okio:okio:3.17.0 (*)
|    |    \--- com.squareup.okhttp3:okhttp:5.4.0 (*)
|    +--- com.nimbusds:oauth2-oidc-sdk:11.38.2 (*)
|    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.0 -> 2.4.0 (c)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.0 -> 2.4.0 (c)
|    \--- com.squareup.okio:okio:3.4.0 -> 3.17.0 (c)
+--- org.springframework.restdocs:spring-restdocs-mockmvc -> 4.0.1
|    +--- org.springframework.restdocs:spring-restdocs-core:4.0.1
|    |    +--- tools.jackson.core:jackson-databind:3.1.3 -> 3.1.5 (*)
|    |    +--- org.springframework:spring-web:7.0.8 -> 7.0.9 (*)
|    |    \--- com.samskivert:jmustache:1.16
|    +--- org.springframework:spring-webmvc:7.0.8 -> 7.0.9 (*)
|    +--- org.springframework:spring-test:7.0.8 -> 7.0.9 (*)
|    \--- jakarta.servlet:jakarta.servlet-api:6.1.0
+--- com.ninja-squad:springmockk:5.0.1
|    +--- org.jetbrains.kotlin:kotlin-reflect -> 2.4.0 (*)
|    +--- org.springframework:spring-test:7.0.1 -> 7.0.9 (*)
|    +--- org.springframework:spring-context:7.0.1 -> 7.0.9 (*)
|    +--- io.mockk:mockk-jvm:1.14.6
|    |    +--- io.mockk:mockk-dsl:1.14.6
|    |    |    \--- io.mockk:mockk-dsl-jvm:1.14.6
|    |    |         +--- org.jetbrains.kotlin:kotlin-stdlib:2.1.20 -> 2.4.0 (*)
|    |    |         +--- org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.10.1 -> 1.10.2 (*)
|    |    |         +--- org.jetbrains.kotlinx:kotlinx-coroutines-core -> 1.10.2 (*)
|    |    |         +--- org.jetbrains.kotlin:kotlin-reflect -> 2.4.0 (*)
|    |    |         +--- io.mockk:mockk-core:1.14.6
|    |    |         |    \--- io.mockk:mockk-core-jvm:1.14.6
|    |    |         |         +--- org.jetbrains.kotlin:kotlin-stdlib:2.1.20 -> 2.4.0 (*)
|    |    |         |         +--- org.jetbrains.kotlin:kotlin-reflect -> 2.4.0 (*)
|    |    |         |         \--- org.jetbrains.kotlin:kotlin-reflect:2.1.20 -> 2.4.0 (c)
|    |    |         \--- org.jetbrains.kotlin:kotlin-reflect:2.1.20 -> 2.4.0 (c)
|    |    +--- io.mockk:mockk-agent:1.14.6
|    |    |    \--- io.mockk:mockk-agent-jvm:1.14.6
|    |    |         +--- org.objenesis:objenesis:3.3
|    |    |         +--- net.bytebuddy:byte-buddy:1.15.11 -> 1.18.11
|    |    |         +--- net.bytebuddy:byte-buddy-agent:1.15.11 -> 1.18.11
|    |    |         +--- io.mockk:mockk-agent-api:1.14.6
|    |    |         |    \--- io.mockk:mockk-agent-api-jvm:1.14.6
|    |    |         |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.1.20 -> 2.4.0 (*)
|    |    |         +--- org.jetbrains.kotlin:kotlin-stdlib:2.1.20 -> 2.4.0 (*)
|    |    |         +--- org.jetbrains.kotlin:kotlin-reflect -> 2.4.0 (*)
|    |    |         +--- io.mockk:mockk-core:1.14.6 (*)
|    |    |         \--- org.jetbrains.kotlin:kotlin-reflect:2.1.20 -> 2.4.0 (c)
|    |    +--- io.mockk:mockk-agent-api:1.14.6 (*)
|    |    +--- io.mockk:mockk-core:1.14.6 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:2.1.20 -> 2.4.0 (*)
|    |    +--- junit:junit:4.13.2 (*)
|    |    +--- org.junit.jupiter:junit-jupiter:5.12.2 -> 6.0.3 (*)
|    |    +--- org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.10.1 -> 1.10.2 (*)
|    |    +--- org.jetbrains.kotlinx:kotlinx-coroutines-core -> 1.10.2 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-reflect -> 2.4.0 (*)
|    |    \--- org.jetbrains.kotlin:kotlin-reflect:2.1.20 -> 2.4.0 (c)
|    +--- org.jetbrains.kotlin:kotlin-stdlib:2.2.21 -> 2.4.0 (*)
|    \--- org.jetbrains.kotlin:kotlin-reflect:2.2.21 -> 2.4.0 (c)
\--- at.yawk.lz4:lz4-java:1.11.1 (c)

testRuntimeOnly - Runtime only dependencies for 'test'. (n)
No dependencies

(c) - A dependency constraint, not a dependency. The dependency affected by the constraint occurs elsewhere in the tree.
(*) - Indicates repeated occurrences of a transitive dependency subtree. Gradle expands transitive dependency subtrees only once per project; repeat occurrences only display the root of the subtree, followed by this annotation.

(n) - A dependency or dependency configuration that cannot be resolved.

A web-based, searchable dependency report is available by adding the --scan option.

[Incubating] Problems report is available at: file:///Users/Jan-Olav.Eide/workspaces/populasjonstilgangskontroll/build/reports/problems/problems-report.html

Deprecated Gradle features were used in this build, making it incompatible with Gradle 10.

You can use '--warning-mode all' to show the individual deprecation warnings and determine if they come from your own scripts or plugins.

For more on this, please refer to https://docs.gradle.org/9.7.1/userguide/command_line_interface.html#sec:command_line_warnings in the Gradle documentation.

BUILD SUCCESSFUL in 709ms
4 actionable tasks: 1 executed, 3 up-to-date
