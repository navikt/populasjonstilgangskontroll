
> Task :dependencies

------------------------------------------------------------
Root project 'tilgangsmaskin'
------------------------------------------------------------

annotationProcessor - Annotation processors and their dependencies for source set 'main'.
No dependencies

api - API dependencies for null/main (n)
No dependencies

apiDependenciesMetadata
\--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25

apiElements - API elements for the 'main' feature. (n)
No dependencies

apiElements-published (n)
No dependencies

bootArchives - Configuration for Spring Boot archive artifacts. (n)
No dependencies

compileClasspath - Compile classpath for null/main.
+--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25
|    +--- org.jetbrains:annotations:13.0 -> 23.0.0
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0 -> 1.9.25 (c)
|    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.0 -> 1.9.25 (c)
+--- io.opentelemetry:opentelemetry-api -> 1.50.0
|    \--- io.opentelemetry:opentelemetry-context:1.50.0
+--- io.micrometer:micrometer-tracing -> 1.5.0
|    +--- io.micrometer:micrometer-observation:1.15.0
|    |    \--- io.micrometer:micrometer-commons:1.15.0
|    +--- io.micrometer:context-propagation:1.1.3
|    \--- aopalliance:aopalliance:1.0
+--- org.springframework.boot:spring-boot-starter-webflux -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0
|    |    +--- org.springframework.boot:spring-boot:3.5.0
|    |    |    +--- org.springframework:spring-core:6.2.7
|    |    |    |    \--- org.springframework:spring-jcl:6.2.7
|    |    |    \--- org.springframework:spring-context:6.2.7
|    |    |         +--- org.springframework:spring-aop:6.2.7
|    |    |         |    +--- org.springframework:spring-beans:6.2.7
|    |    |         |    |    \--- org.springframework:spring-core:6.2.7 (*)
|    |    |         |    \--- org.springframework:spring-core:6.2.7 (*)
|    |    |         +--- org.springframework:spring-beans:6.2.7 (*)
|    |    |         +--- org.springframework:spring-core:6.2.7 (*)
|    |    |         +--- org.springframework:spring-expression:6.2.7
|    |    |         |    \--- org.springframework:spring-core:6.2.7 (*)
|    |    |         \--- io.micrometer:micrometer-observation:1.14.7 -> 1.15.0 (*)
|    |    +--- org.springframework.boot:spring-boot-autoconfigure:3.5.0
|    |    |    \--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    +--- org.springframework.boot:spring-boot-starter-logging:3.5.0
|    |    |    +--- ch.qos.logback:logback-classic:1.5.18
|    |    |    |    +--- ch.qos.logback:logback-core:1.5.18
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.17
|    |    |    +--- org.apache.logging.log4j:log4j-to-slf4j:2.24.3
|    |    |    |    +--- org.apache.logging.log4j:log4j-api:2.24.3
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.16 -> 2.0.17
|    |    |    \--- org.slf4j:jul-to-slf4j:2.0.17
|    |    |         \--- org.slf4j:slf4j-api:2.0.17
|    |    +--- jakarta.annotation:jakarta.annotation-api:2.1.1
|    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    \--- org.yaml:snakeyaml:2.4
|    +--- org.springframework.boot:spring-boot-starter-json:3.5.0
|    |    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    |    +--- org.springframework:spring-web:6.2.7
|    |    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    |    \--- io.micrometer:micrometer-observation:1.14.7 -> 1.15.0 (*)
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0
|    |    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.module:jackson-module-kotlin:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.module:jackson-module-parameter-names:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (c)
|    |    |    |         \--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.19.0 (c)
|    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0
|    |    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.19.0
|    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    \--- com.fasterxml.jackson.module:jackson-module-parameter-names:2.19.0
|    |         +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |         +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |         \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-reactor-netty:3.5.0
|    |    \--- io.projectreactor.netty:reactor-netty-http:1.2.6
|    |         +--- io.netty:netty-codec-http:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final
|    |         |    |    \--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-transport:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-resolver:4.1.121.Final
|    |         |    |         \--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-codec:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-handler:4.1.121.Final
|    |         |         +--- io.netty:netty-common:4.1.121.Final
|    |         |         +--- io.netty:netty-resolver:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-transport-native-unix-common:4.1.121.Final
|    |         |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |         |    \--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |         \--- io.netty:netty-codec:4.1.121.Final (*)
|    |         +--- io.netty:netty-codec-http2:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-handler:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-codec-http:4.1.121.Final (*)
|    |         +--- io.netty:netty-resolver-dns:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-resolver:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-codec-dns:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-handler:4.1.121.Final (*)
|    |         +--- io.netty:netty-resolver-dns-native-macos:4.1.121.Final
|    |         |    \--- io.netty:netty-resolver-dns-classes-macos:4.1.121.Final
|    |         |         +--- io.netty:netty-common:4.1.121.Final
|    |         |         +--- io.netty:netty-resolver-dns:4.1.121.Final (*)
|    |         |         \--- io.netty:netty-transport-native-unix-common:4.1.121.Final (*)
|    |         +--- io.netty:netty-transport-native-epoll:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport-native-unix-common:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-transport-classes-epoll:4.1.121.Final
|    |         |         +--- io.netty:netty-common:4.1.121.Final
|    |         |         +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |         \--- io.netty:netty-transport-native-unix-common:4.1.121.Final (*)
|    |         +--- io.projectreactor.netty:reactor-netty-core:1.2.6
|    |         |    +--- io.netty:netty-handler:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-handler-proxy:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-codec-socks:4.1.121.Final
|    |         |    |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    |    |    \--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-codec-http:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-resolver-dns:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-resolver-dns-native-macos:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport-native-epoll:4.1.121.Final (*)
|    |         |    \--- io.projectreactor:reactor-core:3.7.6
|    |         |         \--- org.reactivestreams:reactive-streams:1.0.4
|    |         \--- io.projectreactor:reactor-core:3.7.6 (*)
|    +--- org.springframework:spring-web:6.2.7 (*)
|    \--- org.springframework:spring-webflux:6.2.7
|         +--- org.springframework:spring-beans:6.2.7 (*)
|         +--- org.springframework:spring-core:6.2.7 (*)
|         +--- org.springframework:spring-web:6.2.7 (*)
|         \--- io.projectreactor:reactor-core:3.7.6 (*)
+--- org.apache.httpcomponents.client5:httpclient5 -> 5.4.4
|    +--- org.apache.httpcomponents.core5:httpcore5:5.3.4
|    +--- org.apache.httpcomponents.core5:httpcore5-h2:5.3.4
|    |    \--- org.apache.httpcomponents.core5:httpcore5:5.3.4
|    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.17
+--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0
|    \--- org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.9.0 -> 1.8.1
|         +--- org.jetbrains:annotations:23.0.0
|         +--- org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.8.1
|         |    +--- org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.8.1 (c)
|         |    \--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1 -> 1.9.0 (c)
|         \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.21 -> 1.9.25 (*)
+--- org.jetbrains.kotlin:kotlin-reflect:1.9.25
|    \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
+--- org.springframework.boot:spring-boot-starter-cache -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    \--- org.springframework:spring-context-support:6.2.7
|         +--- org.springframework:spring-beans:6.2.7 (*)
|         +--- org.springframework:spring-context:6.2.7 (*)
|         \--- org.springframework:spring-core:6.2.7 (*)
+--- org.springframework:spring-aspects -> 6.2.7
|    \--- org.aspectj:aspectjweaver:1.9.22.1 -> 1.9.24
+--- org.springframework.retry:spring-retry -> 2.0.12
+--- org.springframework.kafka:spring-kafka -> 3.3.6
|    +--- org.springframework:spring-context:6.2.7 (*)
|    +--- org.springframework:spring-messaging:6.2.7
|    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    \--- org.springframework:spring-core:6.2.7 (*)
|    +--- org.springframework:spring-tx:6.2.7
|    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    \--- org.springframework:spring-core:6.2.7 (*)
|    +--- org.springframework.retry:spring-retry:2.0.12
|    +--- org.apache.kafka:kafka-clients:3.8.1 -> 3.9.1
|    \--- io.micrometer:micrometer-observation:1.14.7 -> 1.15.0 (*)
+--- org.springframework.boot:spring-boot-starter-graphql -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-json:3.5.0 (*)
|    \--- org.springframework.graphql:spring-graphql:1.4.0
|         +--- com.graphql-java:graphql-java:24.0
|         |    +--- com.graphql-java:java-dataloader:5.0.0
|         |    |    +--- org.reactivestreams:reactive-streams:1.0.3 -> 1.0.4
|         |    |    \--- org.jspecify:jspecify:1.0.0
|         |    +--- org.reactivestreams:reactive-streams:1.0.3 -> 1.0.4
|         |    \--- org.jspecify:jspecify:1.0.0
|         +--- io.projectreactor:reactor-core:3.7.6 (*)
|         \--- org.springframework:spring-context:6.2.7 (*)
+--- org.springframework.boot:spring-boot-starter-web -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-json:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-tomcat:3.5.0
|    |    +--- jakarta.annotation:jakarta.annotation-api:2.1.1
|    |    +--- org.apache.tomcat.embed:tomcat-embed-core:10.1.41
|    |    +--- org.apache.tomcat.embed:tomcat-embed-el:10.1.41
|    |    \--- org.apache.tomcat.embed:tomcat-embed-websocket:10.1.41
|    |         \--- org.apache.tomcat.embed:tomcat-embed-core:10.1.41
|    +--- org.springframework:spring-web:6.2.7 (*)
|    \--- org.springframework:spring-webmvc:6.2.7
|         +--- org.springframework:spring-aop:6.2.7 (*)
|         +--- org.springframework:spring-beans:6.2.7 (*)
|         +--- org.springframework:spring-context:6.2.7 (*)
|         +--- org.springframework:spring-core:6.2.7 (*)
|         +--- org.springframework:spring-expression:6.2.7 (*)
|         \--- org.springframework:spring-web:6.2.7 (*)
+--- org.springframework.boot:spring-boot-starter-actuator -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-actuator-autoconfigure:3.5.0
|    |    +--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    +--- org.springframework.boot:spring-boot-actuator:3.5.0
|    |    |    \--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    \--- org.springframework.boot:spring-boot-autoconfigure:3.5.0 (*)
|    +--- io.micrometer:micrometer-observation:1.15.0 (*)
|    \--- io.micrometer:micrometer-jakarta9:1.15.0
|         +--- io.micrometer:micrometer-core:1.15.0
|         |    +--- io.micrometer:micrometer-commons:1.15.0
|         |    \--- io.micrometer:micrometer-observation:1.15.0 (*)
|         +--- io.micrometer:micrometer-commons:1.15.0
|         \--- io.micrometer:micrometer-observation:1.15.0 (*)
+--- org.springframework.boot:spring-boot-starter-validation -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.apache.tomcat.embed:tomcat-embed-el:10.1.41
|    \--- org.hibernate.validator:hibernate-validator:8.0.2.Final
|         +--- jakarta.validation:jakarta.validation-api:3.0.2
|         +--- org.jboss.logging:jboss-logging:3.4.3.Final -> 3.6.1.Final
|         \--- com.fasterxml:classmate:1.5.1 -> 1.7.0
+--- com.fasterxml.jackson.module:jackson-module-kotlin -> 2.19.0
|    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
+--- org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8
|    +--- org.springdoc:springdoc-openapi-starter-webmvc-api:2.8.8
|    |    +--- org.springdoc:springdoc-openapi-starter-common:2.8.8
|    |    |    +--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
|    |    |    +--- org.springframework.boot:spring-boot-starter-validation:3.4.5 -> 3.5.0 (*)
|    |    |    \--- io.swagger.core.v3:swagger-core-jakarta:2.2.30
|    |    |         +--- org.apache.commons:commons-lang3:3.17.0
|    |    |         +--- org.slf4j:slf4j-api:2.0.9 -> 2.0.17
|    |    |         +--- io.swagger.core.v3:swagger-annotations-jakarta:2.2.30
|    |    |         +--- io.swagger.core.v3:swagger-models-jakarta:2.2.30
|    |    |         |    \--- com.fasterxml.jackson.core:jackson-annotations:2.18.2 -> 2.19.0 (*)
|    |    |         +--- org.yaml:snakeyaml:2.3 -> 2.4
|    |    |         +--- jakarta.xml.bind:jakarta.xml.bind-api:3.0.1 -> 4.0.2
|    |    |         |    \--- jakarta.activation:jakarta.activation-api:2.1.3
|    |    |         +--- jakarta.validation:jakarta.validation-api:3.0.2
|    |    |         +--- com.fasterxml.jackson.core:jackson-annotations:2.18.2 -> 2.19.0 (*)
|    |    |         +--- com.fasterxml.jackson.core:jackson-databind:2.18.2 -> 2.19.0 (*)
|    |    |         +--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.18.2 -> 2.19.0
|    |    |         |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    |         |    +--- org.yaml:snakeyaml:2.4
|    |    |         |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    |         |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    |         \--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.2 -> 2.19.0 (*)
|    |    \--- org.springframework:spring-webmvc:6.2.6 -> 6.2.7 (*)
|    +--- org.webjars:swagger-ui:5.21.0
|    \--- org.webjars:webjars-locator-lite:1.0.1 -> 1.1.0
|         \--- org.jspecify:jspecify:1.0.0
+--- net.logstash.logback:logstash-logback-encoder:8.1
|    \--- com.fasterxml.jackson.core:jackson-databind:2.18.3 -> 2.19.0 (*)
+--- io.micrometer:micrometer-registry-prometheus -> 1.15.0
|    +--- io.micrometer:micrometer-core:1.15.0 (*)
|    +--- io.prometheus:prometheus-metrics-core:1.3.6
|    |    +--- io.prometheus:prometheus-metrics-model:1.3.6
|    |    \--- io.prometheus:prometheus-metrics-config:1.3.6
|    \--- io.prometheus:prometheus-metrics-tracer-common:1.3.6
+--- org.hibernate.orm:hibernate-micrometer -> 6.6.15.Final
+--- no.nav.boot:boot-conditionals:5.1.7
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.25
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.25
|    |         \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    +--- ch.qos.logback:logback-core:1.5.18
|    +--- org.slf4j:slf4j-api:2.0.17
|    \--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
+--- org.springframework.boot:spring-boot-starter-data-redis -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- io.lettuce:lettuce-core:6.5.5.RELEASE
|    |    +--- io.netty:netty-common:4.1.118.Final -> 4.1.121.Final
|    |    +--- io.netty:netty-handler:4.1.118.Final -> 4.1.121.Final (*)
|    |    +--- io.netty:netty-transport:4.1.118.Final -> 4.1.121.Final (*)
|    |    \--- io.projectreactor:reactor-core:3.6.6 -> 3.7.6 (*)
|    \--- org.springframework.data:spring-data-redis:3.5.0
|         +--- org.springframework.data:spring-data-keyvalue:3.5.0
|         |    +--- org.springframework.data:spring-data-commons:3.5.0
|         |    |    +--- org.springframework:spring-core:6.2.7 (*)
|         |    |    +--- org.springframework:spring-beans:6.2.7 (*)
|         |    |    \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
|         |    +--- org.springframework:spring-context:6.2.7 (*)
|         |    +--- org.springframework:spring-tx:6.2.7 (*)
|         |    \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
|         +--- org.springframework:spring-tx:6.2.7 (*)
|         +--- org.springframework:spring-oxm:6.2.7
|         |    +--- org.springframework:spring-beans:6.2.7 (*)
|         |    \--- org.springframework:spring-core:6.2.7 (*)
|         +--- org.springframework:spring-aop:6.2.7 (*)
|         +--- org.springframework:spring-context-support:6.2.7 (*)
|         \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
+--- no.nav.security:token-validation-spring:5.0.25
|    +--- no.nav.security:token-validation-core:5.0.25
|    |    +--- com.nimbusds:oauth2-oidc-sdk:11.23.1
|    |    |    +--- com.github.stephenc.jcip:jcip-annotations:1.0-1
|    |    |    +--- com.nimbusds:content-type:2.3
|    |    |    +--- net.minidev:json-smart:2.5.2
|    |    |    |    \--- net.minidev:accessors-smart:2.5.2
|    |    |    |         \--- org.ow2.asm:asm:9.7.1
|    |    |    +--- com.nimbusds:lang-tag:1.7
|    |    |    \--- com.nimbusds:nimbus-jose-jwt:10.0.2 -> 10.2
|    |    |         \--- com.google.code.gson:gson:2.12.1 -> 2.13.1
|    |    |              \--- com.google.errorprone:error_prone_annotations:2.38.0
|    |    +--- org.slf4j:slf4j-api:2.0.17
|    |    +--- jakarta.validation:jakarta.validation-api:3.0.2
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    |    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
|    +--- no.nav.security:token-validation-filter:5.0.25
|    |    +--- no.nav.security:token-validation-core:5.0.25 (*)
|    |    +--- org.slf4j:slf4j-api:2.0.17
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    |    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
|    +--- org.springframework:spring-context:6.2.6 -> 6.2.7 (*)
|    +--- org.springframework.boot:spring-boot:3.4.5 -> 3.5.0 (*)
|    +--- org.springframework:spring-web:6.2.6 -> 6.2.7 (*)
|    +--- org.springframework:spring-webmvc:6.2.6 -> 6.2.7 (*)
|    +--- com.nimbusds:oauth2-oidc-sdk:11.23.1 (*)
|    +--- org.slf4j:slf4j-api:2.0.17
|    +--- jakarta.validation:jakarta.validation-api:3.0.2
|    +--- org.apache.commons:commons-lang3:3.17.0
|    +--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
+--- no.nav.security:token-client-spring:5.0.25
|    +--- no.nav.security:token-validation-core:5.0.25 (*)
|    +--- no.nav.security:token-client-core:5.0.25
|    |    +--- jakarta.validation:jakarta.validation-api:3.0.2
|    |    +--- org.slf4j:slf4j-api:2.0.17
|    |    +--- com.github.ben-manes.caffeine:caffeine:3.2.0
|    |    |    +--- org.jspecify:jspecify:1.0.0
|    |    |    \--- com.google.errorprone:error_prone_annotations:2.36.0 -> 2.38.0
|    |    +--- com.nimbusds:oauth2-oidc-sdk:11.23.1 (*)
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.18.3 -> 2.19.0 (*)
|    |    +--- com.fasterxml.jackson.module:jackson-module-kotlin:2.18.3 -> 2.19.0 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    |    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
|    +--- org.springframework:spring-web:6.2.6 -> 6.2.7 (*)
|    +--- com.fasterxml.jackson.core:jackson-annotations:2.18.3 -> 2.19.0 (*)
|    +--- com.fasterxml.jackson.core:jackson-databind:2.18.3 -> 2.19.0 (*)
|    +--- org.springframework.boot:spring-boot:3.4.5 -> 3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-configuration-processor:3.4.5 -> 3.5.0
|    +--- com.github.ben-manes.caffeine:caffeine:3.2.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
+--- org.springframework.boot:spring-boot-starter-data-jpa -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-jdbc:3.5.0
|    |    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    |    +--- com.zaxxer:HikariCP:6.3.0
|    |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.17
|    |    \--- org.springframework:spring-jdbc:6.2.7
|    |         +--- org.springframework:spring-beans:6.2.7 (*)
|    |         +--- org.springframework:spring-core:6.2.7 (*)
|    |         \--- org.springframework:spring-tx:6.2.7 (*)
|    +--- org.hibernate.orm:hibernate-core:6.6.15.Final
|    |    +--- jakarta.persistence:jakarta.persistence-api:3.1.0
|    |    \--- jakarta.transaction:jakarta.transaction-api:2.0.1
|    +--- org.springframework.data:spring-data-jpa:3.5.0
|    |    +--- org.springframework.data:spring-data-commons:3.5.0 (*)
|    |    +--- org.springframework:spring-orm:6.2.7
|    |    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    |    +--- org.springframework:spring-jdbc:6.2.7 (*)
|    |    |    \--- org.springframework:spring-tx:6.2.7 (*)
|    |    +--- org.springframework:spring-context:6.2.7 (*)
|    |    +--- org.springframework:spring-aop:6.2.7 (*)
|    |    +--- org.springframework:spring-tx:6.2.7 (*)
|    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    +--- org.antlr:antlr4-runtime:4.13.0
|    |    +--- jakarta.annotation:jakarta.annotation-api:2.0.0 -> 2.1.1
|    |    \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
|    \--- org.springframework:spring-aspects:6.2.7 (*)
+--- org.flywaydb:flyway-core -> 11.7.2
|    +--- com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.15.2 -> 2.19.0
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    \--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2 -> 2.19.0 (*)
+--- org.postgresql:postgresql -> 42.7.5
\--- org.flywaydb:flyway-database-postgresql -> 11.7.2
     \--- org.flywaydb:flyway-core:11.7.2 (*)

compileOnly - Compile only dependencies for null/main. (n)
No dependencies

compileOnlyDependenciesMetadata
No dependencies

default - Configuration for default artifacts. (n)
No dependencies

developmentOnly - Configuration for development-only dependencies such as Spring Boot's DevTools.
No dependencies

implementation - Implementation only dependencies for null/main. (n)
+--- io.opentelemetry:opentelemetry-api (n)
+--- io.micrometer:micrometer-tracing (n)
+--- org.springframework.boot:spring-boot-starter-webflux (n)
+--- org.apache.httpcomponents.client5:httpclient5 (n)
+--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0 (n)
+--- org.jetbrains.kotlin:kotlin-reflect (n)
+--- org.springframework.boot:spring-boot-starter-cache (n)
+--- org.springframework:spring-aspects (n)
+--- org.springframework.retry:spring-retry (n)
+--- org.springframework.kafka:spring-kafka (n)
+--- org.springframework.boot:spring-boot-starter-graphql (n)
+--- org.springframework.boot:spring-boot-starter-web (n)
+--- org.springframework.boot:spring-boot-starter-actuator (n)
+--- org.springframework.boot:spring-boot-starter-validation (n)
+--- com.fasterxml.jackson.module:jackson-module-kotlin (n)
+--- org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8 (n)
+--- net.logstash.logback:logstash-logback-encoder:8.1 (n)
+--- io.micrometer:micrometer-registry-prometheus (n)
+--- org.hibernate.orm:hibernate-micrometer (n)
+--- no.nav.boot:boot-conditionals:5.1.7 (n)
+--- org.springframework.boot:spring-boot-starter-data-redis (n)
+--- no.nav.security:token-validation-spring:5.0.25 (n)
+--- no.nav.security:token-client-spring:5.0.25 (n)
+--- org.springframework.boot:spring-boot-starter-data-jpa (n)
+--- org.flywaydb:flyway-core (n)
+--- org.postgresql:postgresql (n)
\--- org.flywaydb:flyway-database-postgresql (n)

implementationDependenciesMetadata
+--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25
|    \--- org.jetbrains.kotlin:kotlin-stdlib-common:1.9.25 (c)
+--- io.opentelemetry:opentelemetry-api -> 1.50.0
|    \--- io.opentelemetry:opentelemetry-context:1.50.0
+--- io.micrometer:micrometer-tracing -> 1.5.0
|    +--- io.micrometer:micrometer-observation:1.15.0
|    |    \--- io.micrometer:micrometer-commons:1.15.0
|    +--- io.micrometer:context-propagation:1.1.3
|    \--- aopalliance:aopalliance:1.0
+--- org.springframework.boot:spring-boot-starter-webflux -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0
|    |    +--- org.springframework.boot:spring-boot:3.5.0
|    |    |    +--- org.springframework:spring-core:6.2.7
|    |    |    |    \--- org.springframework:spring-jcl:6.2.7
|    |    |    \--- org.springframework:spring-context:6.2.7
|    |    |         +--- org.springframework:spring-aop:6.2.7
|    |    |         |    +--- org.springframework:spring-beans:6.2.7
|    |    |         |    |    \--- org.springframework:spring-core:6.2.7 (*)
|    |    |         |    \--- org.springframework:spring-core:6.2.7 (*)
|    |    |         +--- org.springframework:spring-beans:6.2.7 (*)
|    |    |         +--- org.springframework:spring-core:6.2.7 (*)
|    |    |         +--- org.springframework:spring-expression:6.2.7
|    |    |         |    \--- org.springframework:spring-core:6.2.7 (*)
|    |    |         \--- io.micrometer:micrometer-observation:1.14.7 -> 1.15.0 (*)
|    |    +--- org.springframework.boot:spring-boot-autoconfigure:3.5.0
|    |    |    \--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    +--- org.springframework.boot:spring-boot-starter-logging:3.5.0
|    |    |    +--- ch.qos.logback:logback-classic:1.5.18
|    |    |    |    +--- ch.qos.logback:logback-core:1.5.18
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.17
|    |    |    +--- org.apache.logging.log4j:log4j-to-slf4j:2.24.3
|    |    |    |    +--- org.apache.logging.log4j:log4j-api:2.24.3
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.16 -> 2.0.17
|    |    |    \--- org.slf4j:jul-to-slf4j:2.0.17
|    |    |         \--- org.slf4j:slf4j-api:2.0.17
|    |    +--- jakarta.annotation:jakarta.annotation-api:2.1.1
|    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    \--- org.yaml:snakeyaml:2.4
|    +--- org.springframework.boot:spring-boot-starter-json:3.5.0
|    |    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    |    +--- org.springframework:spring-web:6.2.7
|    |    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    |    \--- io.micrometer:micrometer-observation:1.14.7 -> 1.15.0 (*)
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0
|    |    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.module:jackson-module-kotlin:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.module:jackson-module-parameter-names:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (c)
|    |    |    |         \--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.19.0 (c)
|    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0
|    |    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.19.0
|    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    \--- com.fasterxml.jackson.module:jackson-module-parameter-names:2.19.0
|    |         +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |         +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |         \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-reactor-netty:3.5.0
|    |    \--- io.projectreactor.netty:reactor-netty-http:1.2.6
|    |         +--- io.netty:netty-codec-http:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final
|    |         |    |    \--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-transport:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-resolver:4.1.121.Final
|    |         |    |         \--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-codec:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-handler:4.1.121.Final
|    |         |         +--- io.netty:netty-common:4.1.121.Final
|    |         |         +--- io.netty:netty-resolver:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-transport-native-unix-common:4.1.121.Final
|    |         |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |         |    \--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |         \--- io.netty:netty-codec:4.1.121.Final (*)
|    |         +--- io.netty:netty-codec-http2:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-handler:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-codec-http:4.1.121.Final (*)
|    |         +--- io.netty:netty-resolver-dns:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-resolver:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-codec-dns:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-handler:4.1.121.Final (*)
|    |         +--- io.netty:netty-resolver-dns-native-macos:4.1.121.Final
|    |         |    \--- io.netty:netty-resolver-dns-classes-macos:4.1.121.Final
|    |         |         +--- io.netty:netty-common:4.1.121.Final
|    |         |         +--- io.netty:netty-resolver-dns:4.1.121.Final (*)
|    |         |         \--- io.netty:netty-transport-native-unix-common:4.1.121.Final (*)
|    |         +--- io.netty:netty-transport-native-epoll:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport-native-unix-common:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-transport-classes-epoll:4.1.121.Final
|    |         |         +--- io.netty:netty-common:4.1.121.Final
|    |         |         +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |         \--- io.netty:netty-transport-native-unix-common:4.1.121.Final (*)
|    |         +--- io.projectreactor.netty:reactor-netty-core:1.2.6
|    |         |    +--- io.netty:netty-handler:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-handler-proxy:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-codec-socks:4.1.121.Final
|    |         |    |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    |    |    \--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-codec-http:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-resolver-dns:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-resolver-dns-native-macos:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport-native-epoll:4.1.121.Final (*)
|    |         |    \--- io.projectreactor:reactor-core:3.7.6
|    |         |         \--- org.reactivestreams:reactive-streams:1.0.4
|    |         \--- io.projectreactor:reactor-core:3.7.6 (*)
|    +--- org.springframework:spring-web:6.2.7 (*)
|    \--- org.springframework:spring-webflux:6.2.7
|         +--- org.springframework:spring-beans:6.2.7 (*)
|         +--- org.springframework:spring-core:6.2.7 (*)
|         +--- org.springframework:spring-web:6.2.7 (*)
|         \--- io.projectreactor:reactor-core:3.7.6 (*)
+--- org.apache.httpcomponents.client5:httpclient5 -> 5.4.4
|    +--- org.apache.httpcomponents.core5:httpcore5:5.3.4
|    +--- org.apache.httpcomponents.core5:httpcore5-h2:5.3.4
|    |    \--- org.apache.httpcomponents.core5:httpcore5:5.3.4
|    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.17
+--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0
|    +--- org.jetbrains.kotlin:kotlin-stdlib:2.0.0 -> 1.9.25 (*)
|    \--- org.jetbrains.kotlinx:atomicfu:0.23.1
|         +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.21 -> 1.9.25 (*)
|         \--- org.jetbrains.kotlin:kotlin-stdlib-common:1.9.21 -> 1.9.25
|              \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
+--- org.jetbrains.kotlin:kotlin-reflect:1.9.25
|    \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
+--- org.springframework.boot:spring-boot-starter-cache -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    \--- org.springframework:spring-context-support:6.2.7
|         +--- org.springframework:spring-beans:6.2.7 (*)
|         +--- org.springframework:spring-context:6.2.7 (*)
|         \--- org.springframework:spring-core:6.2.7 (*)
+--- org.springframework:spring-aspects -> 6.2.7
|    \--- org.aspectj:aspectjweaver:1.9.22.1 -> 1.9.24
+--- org.springframework.retry:spring-retry -> 2.0.12
+--- org.springframework.kafka:spring-kafka -> 3.3.6
|    +--- org.springframework:spring-context:6.2.7 (*)
|    +--- org.springframework:spring-messaging:6.2.7
|    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    \--- org.springframework:spring-core:6.2.7 (*)
|    +--- org.springframework:spring-tx:6.2.7
|    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    \--- org.springframework:spring-core:6.2.7 (*)
|    +--- org.springframework.retry:spring-retry:2.0.12
|    +--- org.apache.kafka:kafka-clients:3.8.1 -> 3.9.1
|    \--- io.micrometer:micrometer-observation:1.14.7 -> 1.15.0 (*)
+--- org.springframework.boot:spring-boot-starter-graphql -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-json:3.5.0 (*)
|    \--- org.springframework.graphql:spring-graphql:1.4.0
|         +--- com.graphql-java:graphql-java:24.0
|         |    +--- com.graphql-java:java-dataloader:5.0.0
|         |    |    +--- org.reactivestreams:reactive-streams:1.0.3 -> 1.0.4
|         |    |    \--- org.jspecify:jspecify:1.0.0
|         |    +--- org.reactivestreams:reactive-streams:1.0.3 -> 1.0.4
|         |    \--- org.jspecify:jspecify:1.0.0
|         +--- io.projectreactor:reactor-core:3.7.6 (*)
|         \--- org.springframework:spring-context:6.2.7 (*)
+--- org.springframework.boot:spring-boot-starter-web -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-json:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-tomcat:3.5.0
|    |    +--- jakarta.annotation:jakarta.annotation-api:2.1.1
|    |    +--- org.apache.tomcat.embed:tomcat-embed-core:10.1.41
|    |    +--- org.apache.tomcat.embed:tomcat-embed-el:10.1.41
|    |    \--- org.apache.tomcat.embed:tomcat-embed-websocket:10.1.41
|    |         \--- org.apache.tomcat.embed:tomcat-embed-core:10.1.41
|    +--- org.springframework:spring-web:6.2.7 (*)
|    \--- org.springframework:spring-webmvc:6.2.7
|         +--- org.springframework:spring-aop:6.2.7 (*)
|         +--- org.springframework:spring-beans:6.2.7 (*)
|         +--- org.springframework:spring-context:6.2.7 (*)
|         +--- org.springframework:spring-core:6.2.7 (*)
|         +--- org.springframework:spring-expression:6.2.7 (*)
|         \--- org.springframework:spring-web:6.2.7 (*)
+--- org.springframework.boot:spring-boot-starter-actuator -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-actuator-autoconfigure:3.5.0
|    |    +--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    +--- org.springframework.boot:spring-boot-actuator:3.5.0
|    |    |    \--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    \--- org.springframework.boot:spring-boot-autoconfigure:3.5.0 (*)
|    +--- io.micrometer:micrometer-observation:1.15.0 (*)
|    \--- io.micrometer:micrometer-jakarta9:1.15.0
|         +--- io.micrometer:micrometer-core:1.15.0
|         |    +--- io.micrometer:micrometer-commons:1.15.0
|         |    \--- io.micrometer:micrometer-observation:1.15.0 (*)
|         +--- io.micrometer:micrometer-commons:1.15.0
|         \--- io.micrometer:micrometer-observation:1.15.0 (*)
+--- org.springframework.boot:spring-boot-starter-validation -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.apache.tomcat.embed:tomcat-embed-el:10.1.41
|    \--- org.hibernate.validator:hibernate-validator:8.0.2.Final
|         +--- jakarta.validation:jakarta.validation-api:3.0.2
|         +--- org.jboss.logging:jboss-logging:3.4.3.Final -> 3.6.1.Final
|         \--- com.fasterxml:classmate:1.5.1 -> 1.7.0
+--- com.fasterxml.jackson.module:jackson-module-kotlin -> 2.19.0
|    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
+--- org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8
|    +--- org.springdoc:springdoc-openapi-starter-webmvc-api:2.8.8
|    |    +--- org.springdoc:springdoc-openapi-starter-common:2.8.8
|    |    |    +--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
|    |    |    +--- org.springframework.boot:spring-boot-starter-validation:3.4.5 -> 3.5.0 (*)
|    |    |    \--- io.swagger.core.v3:swagger-core-jakarta:2.2.30
|    |    |         +--- org.apache.commons:commons-lang3:3.17.0
|    |    |         +--- org.slf4j:slf4j-api:2.0.9 -> 2.0.17
|    |    |         +--- io.swagger.core.v3:swagger-annotations-jakarta:2.2.30
|    |    |         +--- io.swagger.core.v3:swagger-models-jakarta:2.2.30
|    |    |         |    \--- com.fasterxml.jackson.core:jackson-annotations:2.18.2 -> 2.19.0 (*)
|    |    |         +--- org.yaml:snakeyaml:2.3 -> 2.4
|    |    |         +--- jakarta.xml.bind:jakarta.xml.bind-api:3.0.1 -> 4.0.2
|    |    |         |    \--- jakarta.activation:jakarta.activation-api:2.1.3
|    |    |         +--- jakarta.validation:jakarta.validation-api:3.0.2
|    |    |         +--- com.fasterxml.jackson.core:jackson-annotations:2.18.2 -> 2.19.0 (*)
|    |    |         +--- com.fasterxml.jackson.core:jackson-databind:2.18.2 -> 2.19.0 (*)
|    |    |         +--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.18.2 -> 2.19.0
|    |    |         |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    |         |    +--- org.yaml:snakeyaml:2.4
|    |    |         |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    |         |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    |         \--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.2 -> 2.19.0 (*)
|    |    \--- org.springframework:spring-webmvc:6.2.6 -> 6.2.7 (*)
|    +--- org.webjars:swagger-ui:5.21.0
|    \--- org.webjars:webjars-locator-lite:1.0.1 -> 1.1.0
|         \--- org.jspecify:jspecify:1.0.0
+--- net.logstash.logback:logstash-logback-encoder:8.1
|    \--- com.fasterxml.jackson.core:jackson-databind:2.18.3 -> 2.19.0 (*)
+--- io.micrometer:micrometer-registry-prometheus -> 1.15.0
|    +--- io.micrometer:micrometer-core:1.15.0 (*)
|    +--- io.prometheus:prometheus-metrics-core:1.3.6
|    |    +--- io.prometheus:prometheus-metrics-model:1.3.6
|    |    \--- io.prometheus:prometheus-metrics-config:1.3.6
|    \--- io.prometheus:prometheus-metrics-tracer-common:1.3.6
+--- org.hibernate.orm:hibernate-micrometer -> 6.6.15.Final
+--- no.nav.boot:boot-conditionals:5.1.7
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.25
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.25
|    |         \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    +--- ch.qos.logback:logback-core:1.5.18
|    +--- org.slf4j:slf4j-api:2.0.17
|    \--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
+--- org.springframework.boot:spring-boot-starter-data-redis -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- io.lettuce:lettuce-core:6.5.5.RELEASE
|    |    +--- io.netty:netty-common:4.1.118.Final -> 4.1.121.Final
|    |    +--- io.netty:netty-handler:4.1.118.Final -> 4.1.121.Final (*)
|    |    +--- io.netty:netty-transport:4.1.118.Final -> 4.1.121.Final (*)
|    |    \--- io.projectreactor:reactor-core:3.6.6 -> 3.7.6 (*)
|    \--- org.springframework.data:spring-data-redis:3.5.0
|         +--- org.springframework.data:spring-data-keyvalue:3.5.0
|         |    +--- org.springframework.data:spring-data-commons:3.5.0
|         |    |    +--- org.springframework:spring-core:6.2.7 (*)
|         |    |    +--- org.springframework:spring-beans:6.2.7 (*)
|         |    |    \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
|         |    +--- org.springframework:spring-context:6.2.7 (*)
|         |    +--- org.springframework:spring-tx:6.2.7 (*)
|         |    \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
|         +--- org.springframework:spring-tx:6.2.7 (*)
|         +--- org.springframework:spring-oxm:6.2.7
|         |    +--- org.springframework:spring-beans:6.2.7 (*)
|         |    \--- org.springframework:spring-core:6.2.7 (*)
|         +--- org.springframework:spring-aop:6.2.7 (*)
|         +--- org.springframework:spring-context-support:6.2.7 (*)
|         \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
+--- no.nav.security:token-validation-spring:5.0.25
|    +--- no.nav.security:token-validation-core:5.0.25
|    |    +--- com.nimbusds:oauth2-oidc-sdk:11.23.1
|    |    |    +--- com.github.stephenc.jcip:jcip-annotations:1.0-1
|    |    |    +--- com.nimbusds:content-type:2.3
|    |    |    +--- net.minidev:json-smart:2.5.2
|    |    |    |    \--- net.minidev:accessors-smart:2.5.2
|    |    |    |         \--- org.ow2.asm:asm:9.7.1
|    |    |    +--- com.nimbusds:lang-tag:1.7
|    |    |    \--- com.nimbusds:nimbus-jose-jwt:10.0.2 -> 10.2
|    |    |         \--- com.google.code.gson:gson:2.12.1 -> 2.13.1
|    |    |              \--- com.google.errorprone:error_prone_annotations:2.38.0
|    |    +--- org.slf4j:slf4j-api:2.0.17
|    |    +--- jakarta.validation:jakarta.validation-api:3.0.2
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    |    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
|    +--- no.nav.security:token-validation-filter:5.0.25
|    |    +--- no.nav.security:token-validation-core:5.0.25 (*)
|    |    +--- org.slf4j:slf4j-api:2.0.17
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    |    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
|    +--- org.springframework:spring-context:6.2.6 -> 6.2.7 (*)
|    +--- org.springframework.boot:spring-boot:3.4.5 -> 3.5.0 (*)
|    +--- org.springframework:spring-web:6.2.6 -> 6.2.7 (*)
|    +--- org.springframework:spring-webmvc:6.2.6 -> 6.2.7 (*)
|    +--- com.nimbusds:oauth2-oidc-sdk:11.23.1 (*)
|    +--- org.slf4j:slf4j-api:2.0.17
|    +--- jakarta.validation:jakarta.validation-api:3.0.2
|    +--- org.apache.commons:commons-lang3:3.17.0
|    +--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
+--- no.nav.security:token-client-spring:5.0.25
|    +--- no.nav.security:token-validation-core:5.0.25 (*)
|    +--- no.nav.security:token-client-core:5.0.25
|    |    +--- jakarta.validation:jakarta.validation-api:3.0.2
|    |    +--- org.slf4j:slf4j-api:2.0.17
|    |    +--- com.github.ben-manes.caffeine:caffeine:3.2.0
|    |    |    +--- org.jspecify:jspecify:1.0.0
|    |    |    \--- com.google.errorprone:error_prone_annotations:2.36.0 -> 2.38.0
|    |    +--- com.nimbusds:oauth2-oidc-sdk:11.23.1 (*)
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.18.3 -> 2.19.0 (*)
|    |    +--- com.fasterxml.jackson.module:jackson-module-kotlin:2.18.3 -> 2.19.0 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    |    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
|    +--- org.springframework:spring-web:6.2.6 -> 6.2.7 (*)
|    +--- com.fasterxml.jackson.core:jackson-annotations:2.18.3 -> 2.19.0 (*)
|    +--- com.fasterxml.jackson.core:jackson-databind:2.18.3 -> 2.19.0 (*)
|    +--- org.springframework.boot:spring-boot:3.4.5 -> 3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-configuration-processor:3.4.5 -> 3.5.0
|    +--- com.github.ben-manes.caffeine:caffeine:3.2.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
+--- org.springframework.boot:spring-boot-starter-data-jpa -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-jdbc:3.5.0
|    |    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    |    +--- com.zaxxer:HikariCP:6.3.0
|    |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.17
|    |    \--- org.springframework:spring-jdbc:6.2.7
|    |         +--- org.springframework:spring-beans:6.2.7 (*)
|    |         +--- org.springframework:spring-core:6.2.7 (*)
|    |         \--- org.springframework:spring-tx:6.2.7 (*)
|    +--- org.hibernate.orm:hibernate-core:6.6.15.Final
|    |    +--- jakarta.persistence:jakarta.persistence-api:3.1.0
|    |    \--- jakarta.transaction:jakarta.transaction-api:2.0.1
|    +--- org.springframework.data:spring-data-jpa:3.5.0
|    |    +--- org.springframework.data:spring-data-commons:3.5.0 (*)
|    |    +--- org.springframework:spring-orm:6.2.7
|    |    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    |    +--- org.springframework:spring-jdbc:6.2.7 (*)
|    |    |    \--- org.springframework:spring-tx:6.2.7 (*)
|    |    +--- org.springframework:spring-context:6.2.7 (*)
|    |    +--- org.springframework:spring-aop:6.2.7 (*)
|    |    +--- org.springframework:spring-tx:6.2.7 (*)
|    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    +--- org.antlr:antlr4-runtime:4.13.0
|    |    +--- jakarta.annotation:jakarta.annotation-api:2.0.0 -> 2.1.1
|    |    \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
|    \--- org.springframework:spring-aspects:6.2.7 (*)
+--- org.flywaydb:flyway-core -> 11.7.2
|    +--- com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.15.2 -> 2.19.0
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    \--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2 -> 2.19.0 (*)
+--- org.postgresql:postgresql -> 42.7.5
\--- org.flywaydb:flyway-database-postgresql -> 11.7.2
     \--- org.flywaydb:flyway-core:11.7.2 (*)

intransitiveDependenciesMetadata
No dependencies

kotlinBuildToolsApiClasspath
\--- org.jetbrains.kotlin:kotlin-build-tools-impl:1.9.25
     +--- org.jetbrains.kotlin:kotlin-build-tools-api:1.9.25
     +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25
     |    \--- org.jetbrains:annotations:13.0
     +--- org.jetbrains.kotlin:kotlin-compiler-embeddable:1.9.25
     |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
     |    +--- org.jetbrains.kotlin:kotlin-script-runtime:1.9.25
     |    +--- org.jetbrains.kotlin:kotlin-reflect:1.6.10 -> 1.9.25
     |    +--- org.jetbrains.kotlin:kotlin-daemon-embeddable:1.9.25
     |    \--- org.jetbrains.intellij.deps:trove4j:1.0.20200330
     \--- org.jetbrains.kotlin:kotlin-compiler-runner:1.9.25
          +--- org.jetbrains.kotlin:kotlin-build-common:1.9.25
          +--- org.jetbrains.kotlin:kotlin-daemon-client:1.9.25
          +--- org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.5.0 -> 1.8.1
          \--- org.jetbrains.kotlin:kotlin-compiler-embeddable:1.9.25 (*)

kotlinCompilerClasspath
\--- org.jetbrains.kotlin:kotlin-compiler-embeddable:1.9.25
     +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25
     |    \--- org.jetbrains:annotations:13.0
     +--- org.jetbrains.kotlin:kotlin-script-runtime:1.9.25
     +--- org.jetbrains.kotlin:kotlin-reflect:1.6.10 -> 1.9.25
     +--- org.jetbrains.kotlin:kotlin-daemon-embeddable:1.9.25
     \--- org.jetbrains.intellij.deps:trove4j:1.0.20200330

kotlinCompilerPluginClasspath
No dependencies

kotlinCompilerPluginClasspathMain - Kotlin compiler plugins for compilation
+--- org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:1.9.25
|    +--- org.jetbrains.kotlin:kotlin-scripting-compiler-impl-embeddable:1.9.25
|    |    +--- org.jetbrains.kotlin:kotlin-scripting-common:1.9.25
|    |    |    \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25
|    |    |         \--- org.jetbrains:annotations:13.0
|    |    +--- org.jetbrains.kotlin:kotlin-scripting-jvm:1.9.25
|    |    |    +--- org.jetbrains.kotlin:kotlin-script-runtime:1.9.25
|    |    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    |    \--- org.jetbrains.kotlin:kotlin-scripting-common:1.9.25 (*)
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
+--- org.jetbrains.kotlin:kotlin-allopen-compiler-plugin-embeddable:1.9.25
\--- org.jetbrains.kotlin:kotlin-noarg-compiler-plugin-embeddable:1.9.25

kotlinCompilerPluginClasspathTest - Kotlin compiler plugins for compilation
+--- org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:1.9.25
|    +--- org.jetbrains.kotlin:kotlin-scripting-compiler-impl-embeddable:1.9.25
|    |    +--- org.jetbrains.kotlin:kotlin-scripting-common:1.9.25
|    |    |    \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25
|    |    |         \--- org.jetbrains:annotations:13.0
|    |    +--- org.jetbrains.kotlin:kotlin-scripting-jvm:1.9.25
|    |    |    +--- org.jetbrains.kotlin:kotlin-script-runtime:1.9.25
|    |    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    |    \--- org.jetbrains.kotlin:kotlin-scripting-common:1.9.25 (*)
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
+--- org.jetbrains.kotlin:kotlin-allopen-compiler-plugin-embeddable:1.9.25
\--- org.jetbrains.kotlin:kotlin-noarg-compiler-plugin-embeddable:1.9.25

kotlinKlibCommonizerClasspath
\--- org.jetbrains.kotlin:kotlin-klib-commonizer-embeddable:1.9.25
     +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25
     |    \--- org.jetbrains:annotations:13.0
     \--- org.jetbrains.kotlin:kotlin-compiler-embeddable:1.9.25
          +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
          +--- org.jetbrains.kotlin:kotlin-script-runtime:1.9.25
          +--- org.jetbrains.kotlin:kotlin-reflect:1.6.10 -> 1.9.25
          +--- org.jetbrains.kotlin:kotlin-daemon-embeddable:1.9.25
          \--- org.jetbrains.intellij.deps:trove4j:1.0.20200330

kotlinNativeCompilerPluginClasspath
No dependencies

kotlinScriptDef - Script filename extensions discovery classpath configuration
No dependencies

kotlinScriptDefExtensions
No dependencies

mainSourceElements - List of source directories contained in the Main SourceSet. (n)
No dependencies

productionRuntimeClasspath
+--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25
|    +--- org.jetbrains:annotations:13.0 -> 23.0.0
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0 -> 1.9.25 (c)
|    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.0 -> 1.9.25 (c)
+--- io.opentelemetry:opentelemetry-api -> 1.50.0
|    \--- io.opentelemetry:opentelemetry-context:1.50.0
+--- io.micrometer:micrometer-tracing -> 1.5.0
|    +--- io.micrometer:micrometer-observation:1.15.0
|    |    \--- io.micrometer:micrometer-commons:1.15.0
|    +--- io.micrometer:context-propagation:1.1.3
|    \--- aopalliance:aopalliance:1.0
+--- org.springframework.boot:spring-boot-starter-webflux -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0
|    |    +--- org.springframework.boot:spring-boot:3.5.0
|    |    |    +--- org.springframework:spring-core:6.2.7
|    |    |    |    \--- org.springframework:spring-jcl:6.2.7
|    |    |    \--- org.springframework:spring-context:6.2.7
|    |    |         +--- org.springframework:spring-aop:6.2.7
|    |    |         |    +--- org.springframework:spring-beans:6.2.7
|    |    |         |    |    \--- org.springframework:spring-core:6.2.7 (*)
|    |    |         |    \--- org.springframework:spring-core:6.2.7 (*)
|    |    |         +--- org.springframework:spring-beans:6.2.7 (*)
|    |    |         +--- org.springframework:spring-core:6.2.7 (*)
|    |    |         +--- org.springframework:spring-expression:6.2.7
|    |    |         |    \--- org.springframework:spring-core:6.2.7 (*)
|    |    |         \--- io.micrometer:micrometer-observation:1.14.7 -> 1.15.0 (*)
|    |    +--- org.springframework.boot:spring-boot-autoconfigure:3.5.0
|    |    |    \--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    +--- org.springframework.boot:spring-boot-starter-logging:3.5.0
|    |    |    +--- ch.qos.logback:logback-classic:1.5.18
|    |    |    |    +--- ch.qos.logback:logback-core:1.5.18
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.17
|    |    |    +--- org.apache.logging.log4j:log4j-to-slf4j:2.24.3
|    |    |    |    +--- org.apache.logging.log4j:log4j-api:2.24.3
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.16 -> 2.0.17
|    |    |    \--- org.slf4j:jul-to-slf4j:2.0.17
|    |    |         \--- org.slf4j:slf4j-api:2.0.17
|    |    +--- jakarta.annotation:jakarta.annotation-api:2.1.1
|    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    \--- org.yaml:snakeyaml:2.4
|    +--- org.springframework.boot:spring-boot-starter-json:3.5.0
|    |    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    |    +--- org.springframework:spring-web:6.2.7
|    |    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    |    \--- io.micrometer:micrometer-observation:1.14.7 -> 1.15.0 (*)
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0
|    |    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.module:jackson-module-kotlin:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.module:jackson-module-parameter-names:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (c)
|    |    |    |         \--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.19.0 (c)
|    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0
|    |    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.19.0
|    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    \--- com.fasterxml.jackson.module:jackson-module-parameter-names:2.19.0
|    |         +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |         +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |         \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-reactor-netty:3.5.0
|    |    \--- io.projectreactor.netty:reactor-netty-http:1.2.6
|    |         +--- io.netty:netty-codec-http:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final
|    |         |    |    \--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-transport:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-resolver:4.1.121.Final
|    |         |    |         \--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-codec:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-handler:4.1.121.Final
|    |         |         +--- io.netty:netty-common:4.1.121.Final
|    |         |         +--- io.netty:netty-resolver:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-transport-native-unix-common:4.1.121.Final
|    |         |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |         |    \--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |         \--- io.netty:netty-codec:4.1.121.Final (*)
|    |         +--- io.netty:netty-codec-http2:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-handler:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-codec-http:4.1.121.Final (*)
|    |         +--- io.netty:netty-resolver-dns:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-resolver:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-codec-dns:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-handler:4.1.121.Final (*)
|    |         +--- io.netty:netty-resolver-dns-native-macos:4.1.121.Final
|    |         |    \--- io.netty:netty-resolver-dns-classes-macos:4.1.121.Final
|    |         |         +--- io.netty:netty-common:4.1.121.Final
|    |         |         +--- io.netty:netty-resolver-dns:4.1.121.Final (*)
|    |         |         \--- io.netty:netty-transport-native-unix-common:4.1.121.Final (*)
|    |         +--- io.netty:netty-transport-native-epoll:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport-native-unix-common:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-transport-classes-epoll:4.1.121.Final
|    |         |         +--- io.netty:netty-common:4.1.121.Final
|    |         |         +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |         \--- io.netty:netty-transport-native-unix-common:4.1.121.Final (*)
|    |         +--- io.projectreactor.netty:reactor-netty-core:1.2.6
|    |         |    +--- io.netty:netty-handler:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-handler-proxy:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-codec-socks:4.1.121.Final
|    |         |    |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    |    |    \--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-codec-http:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-resolver-dns:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-resolver-dns-native-macos:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport-native-epoll:4.1.121.Final (*)
|    |         |    \--- io.projectreactor:reactor-core:3.7.6
|    |         |         \--- org.reactivestreams:reactive-streams:1.0.4
|    |         \--- io.projectreactor:reactor-core:3.7.6 (*)
|    +--- org.springframework:spring-web:6.2.7 (*)
|    \--- org.springframework:spring-webflux:6.2.7
|         +--- org.springframework:spring-beans:6.2.7 (*)
|         +--- org.springframework:spring-core:6.2.7 (*)
|         +--- org.springframework:spring-web:6.2.7 (*)
|         \--- io.projectreactor:reactor-core:3.7.6 (*)
+--- org.apache.httpcomponents.client5:httpclient5 -> 5.4.4
|    +--- org.apache.httpcomponents.core5:httpcore5:5.3.4
|    +--- org.apache.httpcomponents.core5:httpcore5-h2:5.3.4
|    |    \--- org.apache.httpcomponents.core5:httpcore5:5.3.4
|    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.17
+--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0
|    \--- org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.9.0 -> 1.8.1
|         +--- org.jetbrains:annotations:23.0.0
|         +--- org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.8.1
|         |    +--- org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.8.1 (c)
|         |    \--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1 -> 1.9.0 (c)
|         \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.21 -> 1.9.25 (*)
+--- org.jetbrains.kotlin:kotlin-reflect:1.9.25
|    \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
+--- org.springframework.boot:spring-boot-starter-cache -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    \--- org.springframework:spring-context-support:6.2.7
|         +--- org.springframework:spring-beans:6.2.7 (*)
|         +--- org.springframework:spring-context:6.2.7 (*)
|         \--- org.springframework:spring-core:6.2.7 (*)
+--- org.springframework:spring-aspects -> 6.2.7
|    \--- org.aspectj:aspectjweaver:1.9.22.1 -> 1.9.24
+--- org.springframework.retry:spring-retry -> 2.0.12
+--- org.springframework.kafka:spring-kafka -> 3.3.6
|    +--- org.springframework:spring-context:6.2.7 (*)
|    +--- org.springframework:spring-messaging:6.2.7
|    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    \--- org.springframework:spring-core:6.2.7 (*)
|    +--- org.springframework:spring-tx:6.2.7
|    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    \--- org.springframework:spring-core:6.2.7 (*)
|    +--- org.springframework.retry:spring-retry:2.0.12
|    +--- org.apache.kafka:kafka-clients:3.8.1 -> 3.9.1
|    |    +--- com.github.luben:zstd-jni:1.5.6-4
|    |    +--- org.lz4:lz4-java:1.8.0
|    |    +--- org.xerial.snappy:snappy-java:1.1.10.5
|    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.17
|    \--- io.micrometer:micrometer-observation:1.14.7 -> 1.15.0 (*)
+--- org.springframework.boot:spring-boot-starter-graphql -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-json:3.5.0 (*)
|    \--- org.springframework.graphql:spring-graphql:1.4.0
|         +--- io.micrometer:context-propagation:1.1.3
|         +--- com.graphql-java:graphql-java:24.0
|         |    +--- com.graphql-java:java-dataloader:5.0.0
|         |    |    +--- org.reactivestreams:reactive-streams:1.0.3 -> 1.0.4
|         |    |    \--- org.jspecify:jspecify:1.0.0
|         |    +--- org.reactivestreams:reactive-streams:1.0.3 -> 1.0.4
|         |    \--- org.jspecify:jspecify:1.0.0
|         +--- io.projectreactor:reactor-core:3.7.6 (*)
|         \--- org.springframework:spring-context:6.2.7 (*)
+--- org.springframework.boot:spring-boot-starter-web -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-json:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-tomcat:3.5.0
|    |    +--- jakarta.annotation:jakarta.annotation-api:2.1.1
|    |    +--- org.apache.tomcat.embed:tomcat-embed-core:10.1.41
|    |    +--- org.apache.tomcat.embed:tomcat-embed-el:10.1.41
|    |    \--- org.apache.tomcat.embed:tomcat-embed-websocket:10.1.41
|    |         \--- org.apache.tomcat.embed:tomcat-embed-core:10.1.41
|    +--- org.springframework:spring-web:6.2.7 (*)
|    \--- org.springframework:spring-webmvc:6.2.7
|         +--- org.springframework:spring-aop:6.2.7 (*)
|         +--- org.springframework:spring-beans:6.2.7 (*)
|         +--- org.springframework:spring-context:6.2.7 (*)
|         +--- org.springframework:spring-core:6.2.7 (*)
|         +--- org.springframework:spring-expression:6.2.7 (*)
|         \--- org.springframework:spring-web:6.2.7 (*)
+--- org.springframework.boot:spring-boot-starter-actuator -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-actuator-autoconfigure:3.5.0
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0 (*)
|    |    +--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    +--- org.springframework.boot:spring-boot-actuator:3.5.0
|    |    |    \--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    \--- org.springframework.boot:spring-boot-autoconfigure:3.5.0 (*)
|    +--- io.micrometer:micrometer-observation:1.15.0 (*)
|    \--- io.micrometer:micrometer-jakarta9:1.15.0
|         +--- io.micrometer:micrometer-core:1.15.0
|         |    +--- io.micrometer:micrometer-commons:1.15.0
|         |    +--- io.micrometer:micrometer-observation:1.15.0 (*)
|         |    +--- org.hdrhistogram:HdrHistogram:2.2.2
|         |    \--- org.latencyutils:LatencyUtils:2.0.3
|         +--- io.micrometer:micrometer-commons:1.15.0
|         \--- io.micrometer:micrometer-observation:1.15.0 (*)
+--- org.springframework.boot:spring-boot-starter-validation -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.apache.tomcat.embed:tomcat-embed-el:10.1.41
|    \--- org.hibernate.validator:hibernate-validator:8.0.2.Final
|         +--- jakarta.validation:jakarta.validation-api:3.0.2
|         +--- org.jboss.logging:jboss-logging:3.4.3.Final -> 3.6.1.Final
|         \--- com.fasterxml:classmate:1.5.1 -> 1.7.0
+--- com.fasterxml.jackson.module:jackson-module-kotlin -> 2.19.0
|    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
+--- org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8
|    +--- org.springdoc:springdoc-openapi-starter-webmvc-api:2.8.8
|    |    +--- org.springdoc:springdoc-openapi-starter-common:2.8.8
|    |    |    +--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
|    |    |    +--- org.springframework.boot:spring-boot-starter-validation:3.4.5 -> 3.5.0 (*)
|    |    |    \--- io.swagger.core.v3:swagger-core-jakarta:2.2.30
|    |    |         +--- org.apache.commons:commons-lang3:3.17.0
|    |    |         +--- org.slf4j:slf4j-api:2.0.9 -> 2.0.17
|    |    |         +--- io.swagger.core.v3:swagger-annotations-jakarta:2.2.30
|    |    |         +--- io.swagger.core.v3:swagger-models-jakarta:2.2.30
|    |    |         |    \--- com.fasterxml.jackson.core:jackson-annotations:2.18.2 -> 2.19.0 (*)
|    |    |         +--- org.yaml:snakeyaml:2.3 -> 2.4
|    |    |         +--- jakarta.xml.bind:jakarta.xml.bind-api:3.0.1 -> 4.0.2
|    |    |         |    \--- jakarta.activation:jakarta.activation-api:2.1.3
|    |    |         +--- jakarta.validation:jakarta.validation-api:3.0.2
|    |    |         +--- com.fasterxml.jackson.core:jackson-annotations:2.18.2 -> 2.19.0 (*)
|    |    |         +--- com.fasterxml.jackson.core:jackson-databind:2.18.2 -> 2.19.0 (*)
|    |    |         +--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.18.2 -> 2.19.0
|    |    |         |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    |         |    +--- org.yaml:snakeyaml:2.4
|    |    |         |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    |         |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    |         \--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.2 -> 2.19.0 (*)
|    |    \--- org.springframework:spring-webmvc:6.2.6 -> 6.2.7 (*)
|    +--- org.webjars:swagger-ui:5.21.0
|    \--- org.webjars:webjars-locator-lite:1.0.1 -> 1.1.0
|         \--- org.jspecify:jspecify:1.0.0
+--- net.logstash.logback:logstash-logback-encoder:8.1
|    \--- com.fasterxml.jackson.core:jackson-databind:2.18.3 -> 2.19.0 (*)
+--- io.micrometer:micrometer-registry-prometheus -> 1.15.0
|    +--- io.micrometer:micrometer-core:1.15.0 (*)
|    +--- io.prometheus:prometheus-metrics-core:1.3.6
|    |    +--- io.prometheus:prometheus-metrics-model:1.3.6
|    |    \--- io.prometheus:prometheus-metrics-config:1.3.6
|    +--- io.prometheus:prometheus-metrics-tracer-common:1.3.6
|    \--- io.prometheus:prometheus-metrics-exposition-formats:1.3.6
|         \--- io.prometheus:prometheus-metrics-exposition-textformats:1.3.6
|              +--- io.prometheus:prometheus-metrics-model:1.3.6
|              \--- io.prometheus:prometheus-metrics-config:1.3.6
+--- org.hibernate.orm:hibernate-micrometer -> 6.6.15.Final
|    +--- org.jboss.logging:jboss-logging:3.5.0.Final -> 3.6.1.Final
|    +--- org.hibernate.orm:hibernate-core:6.6.15.Final
|    |    +--- jakarta.persistence:jakarta.persistence-api:3.1.0
|    |    +--- jakarta.transaction:jakarta.transaction-api:2.0.1
|    |    +--- org.jboss.logging:jboss-logging:3.5.0.Final -> 3.6.1.Final
|    |    +--- org.hibernate.common:hibernate-commons-annotations:7.0.3.Final
|    |    +--- io.smallrye:jandex:3.2.0
|    |    +--- com.fasterxml:classmate:1.5.1 -> 1.7.0
|    |    +--- net.bytebuddy:byte-buddy:1.15.11 -> 1.17.5
|    |    +--- jakarta.xml.bind:jakarta.xml.bind-api:4.0.0 -> 4.0.2 (*)
|    |    +--- org.glassfish.jaxb:jaxb-runtime:4.0.2 -> 4.0.5
|    |    |    \--- org.glassfish.jaxb:jaxb-core:4.0.5
|    |    |         +--- jakarta.xml.bind:jakarta.xml.bind-api:4.0.2 (*)
|    |    |         +--- jakarta.activation:jakarta.activation-api:2.1.3
|    |    |         +--- org.eclipse.angus:angus-activation:2.0.2
|    |    |         |    \--- jakarta.activation:jakarta.activation-api:2.1.3
|    |    |         +--- org.glassfish.jaxb:txw2:4.0.5
|    |    |         \--- com.sun.istack:istack-commons-runtime:4.1.2
|    |    +--- jakarta.inject:jakarta.inject-api:2.0.1
|    |    \--- org.antlr:antlr4-runtime:4.13.0
|    \--- io.micrometer:micrometer-core:1.10.4 -> 1.15.0 (*)
+--- no.nav.boot:boot-conditionals:5.1.7
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.25
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.25
|    |         \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    +--- ch.qos.logback:logback-core:1.5.18
|    +--- org.slf4j:slf4j-api:2.0.17
|    \--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
+--- org.springframework.boot:spring-boot-starter-data-redis -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- io.lettuce:lettuce-core:6.5.5.RELEASE
|    |    +--- io.netty:netty-common:4.1.118.Final -> 4.1.121.Final
|    |    +--- io.netty:netty-handler:4.1.118.Final -> 4.1.121.Final (*)
|    |    +--- io.netty:netty-transport:4.1.118.Final -> 4.1.121.Final (*)
|    |    \--- io.projectreactor:reactor-core:3.6.6 -> 3.7.6 (*)
|    \--- org.springframework.data:spring-data-redis:3.5.0
|         +--- org.springframework.data:spring-data-keyvalue:3.5.0
|         |    +--- org.springframework.data:spring-data-commons:3.5.0
|         |    |    +--- org.springframework:spring-core:6.2.7 (*)
|         |    |    +--- org.springframework:spring-beans:6.2.7 (*)
|         |    |    \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
|         |    +--- org.springframework:spring-context:6.2.7 (*)
|         |    +--- org.springframework:spring-tx:6.2.7 (*)
|         |    \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
|         +--- org.springframework:spring-tx:6.2.7 (*)
|         +--- org.springframework:spring-oxm:6.2.7
|         |    +--- jakarta.xml.bind:jakarta.xml.bind-api:3.0.1 -> 4.0.2 (*)
|         |    +--- org.springframework:spring-beans:6.2.7 (*)
|         |    \--- org.springframework:spring-core:6.2.7 (*)
|         +--- org.springframework:spring-aop:6.2.7 (*)
|         +--- org.springframework:spring-context-support:6.2.7 (*)
|         \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
+--- no.nav.security:token-validation-spring:5.0.25
|    +--- no.nav.security:token-validation-core:5.0.25
|    |    +--- com.nimbusds:oauth2-oidc-sdk:11.23.1
|    |    |    +--- com.github.stephenc.jcip:jcip-annotations:1.0-1
|    |    |    +--- com.nimbusds:content-type:2.3
|    |    |    +--- net.minidev:json-smart:2.5.2
|    |    |    |    \--- net.minidev:accessors-smart:2.5.2
|    |    |    |         \--- org.ow2.asm:asm:9.7.1
|    |    |    +--- com.nimbusds:lang-tag:1.7
|    |    |    \--- com.nimbusds:nimbus-jose-jwt:10.0.2 -> 10.2
|    |    |         \--- com.google.code.gson:gson:2.12.1 -> 2.13.1
|    |    |              \--- com.google.errorprone:error_prone_annotations:2.38.0
|    |    +--- org.slf4j:slf4j-api:2.0.17
|    |    +--- jakarta.validation:jakarta.validation-api:3.0.2
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    |    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
|    +--- no.nav.security:token-validation-filter:5.0.25
|    |    +--- no.nav.security:token-validation-core:5.0.25 (*)
|    |    +--- org.slf4j:slf4j-api:2.0.17
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    |    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
|    +--- org.springframework:spring-context:6.2.6 -> 6.2.7 (*)
|    +--- org.springframework.boot:spring-boot:3.4.5 -> 3.5.0 (*)
|    +--- org.springframework:spring-web:6.2.6 -> 6.2.7 (*)
|    +--- org.springframework:spring-webmvc:6.2.6 -> 6.2.7 (*)
|    +--- com.nimbusds:oauth2-oidc-sdk:11.23.1 (*)
|    +--- org.slf4j:slf4j-api:2.0.17
|    +--- jakarta.validation:jakarta.validation-api:3.0.2
|    +--- org.apache.commons:commons-lang3:3.17.0
|    +--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
+--- no.nav.security:token-client-spring:5.0.25
|    +--- no.nav.security:token-validation-core:5.0.25 (*)
|    +--- no.nav.security:token-client-core:5.0.25
|    |    +--- jakarta.validation:jakarta.validation-api:3.0.2
|    |    +--- org.slf4j:slf4j-api:2.0.17
|    |    +--- com.github.ben-manes.caffeine:caffeine:3.2.0
|    |    |    +--- org.jspecify:jspecify:1.0.0
|    |    |    \--- com.google.errorprone:error_prone_annotations:2.36.0 -> 2.38.0
|    |    +--- com.nimbusds:oauth2-oidc-sdk:11.23.1 (*)
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.18.3 -> 2.19.0 (*)
|    |    +--- com.fasterxml.jackson.module:jackson-module-kotlin:2.18.3 -> 2.19.0 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    |    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
|    +--- org.springframework:spring-web:6.2.6 -> 6.2.7 (*)
|    +--- com.fasterxml.jackson.core:jackson-annotations:2.18.3 -> 2.19.0 (*)
|    +--- com.fasterxml.jackson.core:jackson-databind:2.18.3 -> 2.19.0 (*)
|    +--- org.springframework.boot:spring-boot:3.4.5 -> 3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-configuration-processor:3.4.5 -> 3.5.0
|    +--- com.github.ben-manes.caffeine:caffeine:3.2.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
+--- org.springframework.boot:spring-boot-starter-data-jpa -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-jdbc:3.5.0
|    |    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    |    +--- com.zaxxer:HikariCP:6.3.0
|    |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.17
|    |    \--- org.springframework:spring-jdbc:6.2.7
|    |         +--- org.springframework:spring-beans:6.2.7 (*)
|    |         +--- org.springframework:spring-core:6.2.7 (*)
|    |         \--- org.springframework:spring-tx:6.2.7 (*)
|    +--- org.hibernate.orm:hibernate-core:6.6.15.Final (*)
|    +--- org.springframework.data:spring-data-jpa:3.5.0
|    |    +--- org.springframework.data:spring-data-commons:3.5.0 (*)
|    |    +--- org.springframework:spring-orm:6.2.7
|    |    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    |    +--- org.springframework:spring-jdbc:6.2.7 (*)
|    |    |    \--- org.springframework:spring-tx:6.2.7 (*)
|    |    +--- org.springframework:spring-context:6.2.7 (*)
|    |    +--- org.springframework:spring-aop:6.2.7 (*)
|    |    +--- org.springframework:spring-tx:6.2.7 (*)
|    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    +--- org.antlr:antlr4-runtime:4.13.0
|    |    +--- jakarta.annotation:jakarta.annotation-api:2.0.0 -> 2.1.1
|    |    \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
|    \--- org.springframework:spring-aspects:6.2.7 (*)
+--- org.flywaydb:flyway-core -> 11.7.2
|    +--- com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.15.2 -> 2.19.0
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    \--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2 -> 2.19.0 (*)
+--- org.postgresql:postgresql -> 42.7.5
|    \--- org.checkerframework:checker-qual:3.48.3
\--- org.flywaydb:flyway-database-postgresql -> 11.7.2
     \--- org.flywaydb:flyway-core:11.7.2 (*)

runtimeClasspath - Runtime classpath of null/main.
+--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25
|    +--- org.jetbrains:annotations:13.0 -> 23.0.0
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0 -> 1.9.25 (c)
|    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.0 -> 1.9.25 (c)
+--- io.opentelemetry:opentelemetry-api -> 1.50.0
|    \--- io.opentelemetry:opentelemetry-context:1.50.0
+--- io.micrometer:micrometer-tracing -> 1.5.0
|    +--- io.micrometer:micrometer-observation:1.15.0
|    |    \--- io.micrometer:micrometer-commons:1.15.0
|    +--- io.micrometer:context-propagation:1.1.3
|    \--- aopalliance:aopalliance:1.0
+--- org.springframework.boot:spring-boot-starter-webflux -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0
|    |    +--- org.springframework.boot:spring-boot:3.5.0
|    |    |    +--- org.springframework:spring-core:6.2.7
|    |    |    |    \--- org.springframework:spring-jcl:6.2.7
|    |    |    \--- org.springframework:spring-context:6.2.7
|    |    |         +--- org.springframework:spring-aop:6.2.7
|    |    |         |    +--- org.springframework:spring-beans:6.2.7
|    |    |         |    |    \--- org.springframework:spring-core:6.2.7 (*)
|    |    |         |    \--- org.springframework:spring-core:6.2.7 (*)
|    |    |         +--- org.springframework:spring-beans:6.2.7 (*)
|    |    |         +--- org.springframework:spring-core:6.2.7 (*)
|    |    |         +--- org.springframework:spring-expression:6.2.7
|    |    |         |    \--- org.springframework:spring-core:6.2.7 (*)
|    |    |         \--- io.micrometer:micrometer-observation:1.14.7 -> 1.15.0 (*)
|    |    +--- org.springframework.boot:spring-boot-autoconfigure:3.5.0
|    |    |    \--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    +--- org.springframework.boot:spring-boot-starter-logging:3.5.0
|    |    |    +--- ch.qos.logback:logback-classic:1.5.18
|    |    |    |    +--- ch.qos.logback:logback-core:1.5.18
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.17
|    |    |    +--- org.apache.logging.log4j:log4j-to-slf4j:2.24.3
|    |    |    |    +--- org.apache.logging.log4j:log4j-api:2.24.3
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.16 -> 2.0.17
|    |    |    \--- org.slf4j:jul-to-slf4j:2.0.17
|    |    |         \--- org.slf4j:slf4j-api:2.0.17
|    |    +--- jakarta.annotation:jakarta.annotation-api:2.1.1
|    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    \--- org.yaml:snakeyaml:2.4
|    +--- org.springframework.boot:spring-boot-starter-json:3.5.0
|    |    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    |    +--- org.springframework:spring-web:6.2.7
|    |    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    |    \--- io.micrometer:micrometer-observation:1.14.7 -> 1.15.0 (*)
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0
|    |    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.module:jackson-module-kotlin:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.module:jackson-module-parameter-names:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (c)
|    |    |    |         \--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.19.0 (c)
|    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0
|    |    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.19.0
|    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    \--- com.fasterxml.jackson.module:jackson-module-parameter-names:2.19.0
|    |         +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |         +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |         \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-reactor-netty:3.5.0
|    |    \--- io.projectreactor.netty:reactor-netty-http:1.2.6
|    |         +--- io.netty:netty-codec-http:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final
|    |         |    |    \--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-transport:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-resolver:4.1.121.Final
|    |         |    |         \--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-codec:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-handler:4.1.121.Final
|    |         |         +--- io.netty:netty-common:4.1.121.Final
|    |         |         +--- io.netty:netty-resolver:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-transport-native-unix-common:4.1.121.Final
|    |         |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |         |    \--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |         \--- io.netty:netty-codec:4.1.121.Final (*)
|    |         +--- io.netty:netty-codec-http2:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-handler:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-codec-http:4.1.121.Final (*)
|    |         +--- io.netty:netty-resolver-dns:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-resolver:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-codec-dns:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-handler:4.1.121.Final (*)
|    |         +--- io.netty:netty-resolver-dns-native-macos:4.1.121.Final
|    |         |    \--- io.netty:netty-resolver-dns-classes-macos:4.1.121.Final
|    |         |         +--- io.netty:netty-common:4.1.121.Final
|    |         |         +--- io.netty:netty-resolver-dns:4.1.121.Final (*)
|    |         |         \--- io.netty:netty-transport-native-unix-common:4.1.121.Final (*)
|    |         +--- io.netty:netty-transport-native-epoll:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport-native-unix-common:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-transport-classes-epoll:4.1.121.Final
|    |         |         +--- io.netty:netty-common:4.1.121.Final
|    |         |         +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |         \--- io.netty:netty-transport-native-unix-common:4.1.121.Final (*)
|    |         +--- io.projectreactor.netty:reactor-netty-core:1.2.6
|    |         |    +--- io.netty:netty-handler:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-handler-proxy:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-codec-socks:4.1.121.Final
|    |         |    |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    |    |    \--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-codec-http:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-resolver-dns:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-resolver-dns-native-macos:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport-native-epoll:4.1.121.Final (*)
|    |         |    \--- io.projectreactor:reactor-core:3.7.6
|    |         |         \--- org.reactivestreams:reactive-streams:1.0.4
|    |         \--- io.projectreactor:reactor-core:3.7.6 (*)
|    +--- org.springframework:spring-web:6.2.7 (*)
|    \--- org.springframework:spring-webflux:6.2.7
|         +--- org.springframework:spring-beans:6.2.7 (*)
|         +--- org.springframework:spring-core:6.2.7 (*)
|         +--- org.springframework:spring-web:6.2.7 (*)
|         \--- io.projectreactor:reactor-core:3.7.6 (*)
+--- org.apache.httpcomponents.client5:httpclient5 -> 5.4.4
|    +--- org.apache.httpcomponents.core5:httpcore5:5.3.4
|    +--- org.apache.httpcomponents.core5:httpcore5-h2:5.3.4
|    |    \--- org.apache.httpcomponents.core5:httpcore5:5.3.4
|    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.17
+--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0
|    \--- org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.9.0 -> 1.8.1
|         +--- org.jetbrains:annotations:23.0.0
|         +--- org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.8.1
|         |    +--- org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.8.1 (c)
|         |    \--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1 -> 1.9.0 (c)
|         \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.21 -> 1.9.25 (*)
+--- org.jetbrains.kotlin:kotlin-reflect:1.9.25
|    \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
+--- org.springframework.boot:spring-boot-starter-cache -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    \--- org.springframework:spring-context-support:6.2.7
|         +--- org.springframework:spring-beans:6.2.7 (*)
|         +--- org.springframework:spring-context:6.2.7 (*)
|         \--- org.springframework:spring-core:6.2.7 (*)
+--- org.springframework:spring-aspects -> 6.2.7
|    \--- org.aspectj:aspectjweaver:1.9.22.1 -> 1.9.24
+--- org.springframework.retry:spring-retry -> 2.0.12
+--- org.springframework.kafka:spring-kafka -> 3.3.6
|    +--- org.springframework:spring-context:6.2.7 (*)
|    +--- org.springframework:spring-messaging:6.2.7
|    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    \--- org.springframework:spring-core:6.2.7 (*)
|    +--- org.springframework:spring-tx:6.2.7
|    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    \--- org.springframework:spring-core:6.2.7 (*)
|    +--- org.springframework.retry:spring-retry:2.0.12
|    +--- org.apache.kafka:kafka-clients:3.8.1 -> 3.9.1
|    |    +--- com.github.luben:zstd-jni:1.5.6-4
|    |    +--- org.lz4:lz4-java:1.8.0
|    |    +--- org.xerial.snappy:snappy-java:1.1.10.5
|    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.17
|    \--- io.micrometer:micrometer-observation:1.14.7 -> 1.15.0 (*)
+--- org.springframework.boot:spring-boot-starter-graphql -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-json:3.5.0 (*)
|    \--- org.springframework.graphql:spring-graphql:1.4.0
|         +--- io.micrometer:context-propagation:1.1.3
|         +--- com.graphql-java:graphql-java:24.0
|         |    +--- com.graphql-java:java-dataloader:5.0.0
|         |    |    +--- org.reactivestreams:reactive-streams:1.0.3 -> 1.0.4
|         |    |    \--- org.jspecify:jspecify:1.0.0
|         |    +--- org.reactivestreams:reactive-streams:1.0.3 -> 1.0.4
|         |    \--- org.jspecify:jspecify:1.0.0
|         +--- io.projectreactor:reactor-core:3.7.6 (*)
|         \--- org.springframework:spring-context:6.2.7 (*)
+--- org.springframework.boot:spring-boot-starter-web -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-json:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-tomcat:3.5.0
|    |    +--- jakarta.annotation:jakarta.annotation-api:2.1.1
|    |    +--- org.apache.tomcat.embed:tomcat-embed-core:10.1.41
|    |    +--- org.apache.tomcat.embed:tomcat-embed-el:10.1.41
|    |    \--- org.apache.tomcat.embed:tomcat-embed-websocket:10.1.41
|    |         \--- org.apache.tomcat.embed:tomcat-embed-core:10.1.41
|    +--- org.springframework:spring-web:6.2.7 (*)
|    \--- org.springframework:spring-webmvc:6.2.7
|         +--- org.springframework:spring-aop:6.2.7 (*)
|         +--- org.springframework:spring-beans:6.2.7 (*)
|         +--- org.springframework:spring-context:6.2.7 (*)
|         +--- org.springframework:spring-core:6.2.7 (*)
|         +--- org.springframework:spring-expression:6.2.7 (*)
|         \--- org.springframework:spring-web:6.2.7 (*)
+--- org.springframework.boot:spring-boot-starter-actuator -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-actuator-autoconfigure:3.5.0
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0 (*)
|    |    +--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    +--- org.springframework.boot:spring-boot-actuator:3.5.0
|    |    |    \--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    \--- org.springframework.boot:spring-boot-autoconfigure:3.5.0 (*)
|    +--- io.micrometer:micrometer-observation:1.15.0 (*)
|    \--- io.micrometer:micrometer-jakarta9:1.15.0
|         +--- io.micrometer:micrometer-core:1.15.0
|         |    +--- io.micrometer:micrometer-commons:1.15.0
|         |    +--- io.micrometer:micrometer-observation:1.15.0 (*)
|         |    +--- org.hdrhistogram:HdrHistogram:2.2.2
|         |    \--- org.latencyutils:LatencyUtils:2.0.3
|         +--- io.micrometer:micrometer-commons:1.15.0
|         \--- io.micrometer:micrometer-observation:1.15.0 (*)
+--- org.springframework.boot:spring-boot-starter-validation -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.apache.tomcat.embed:tomcat-embed-el:10.1.41
|    \--- org.hibernate.validator:hibernate-validator:8.0.2.Final
|         +--- jakarta.validation:jakarta.validation-api:3.0.2
|         +--- org.jboss.logging:jboss-logging:3.4.3.Final -> 3.6.1.Final
|         \--- com.fasterxml:classmate:1.5.1 -> 1.7.0
+--- com.fasterxml.jackson.module:jackson-module-kotlin -> 2.19.0
|    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
+--- org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8
|    +--- org.springdoc:springdoc-openapi-starter-webmvc-api:2.8.8
|    |    +--- org.springdoc:springdoc-openapi-starter-common:2.8.8
|    |    |    +--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
|    |    |    +--- org.springframework.boot:spring-boot-starter-validation:3.4.5 -> 3.5.0 (*)
|    |    |    \--- io.swagger.core.v3:swagger-core-jakarta:2.2.30
|    |    |         +--- org.apache.commons:commons-lang3:3.17.0
|    |    |         +--- org.slf4j:slf4j-api:2.0.9 -> 2.0.17
|    |    |         +--- io.swagger.core.v3:swagger-annotations-jakarta:2.2.30
|    |    |         +--- io.swagger.core.v3:swagger-models-jakarta:2.2.30
|    |    |         |    \--- com.fasterxml.jackson.core:jackson-annotations:2.18.2 -> 2.19.0 (*)
|    |    |         +--- org.yaml:snakeyaml:2.3 -> 2.4
|    |    |         +--- jakarta.xml.bind:jakarta.xml.bind-api:3.0.1 -> 4.0.2
|    |    |         |    \--- jakarta.activation:jakarta.activation-api:2.1.3
|    |    |         +--- jakarta.validation:jakarta.validation-api:3.0.2
|    |    |         +--- com.fasterxml.jackson.core:jackson-annotations:2.18.2 -> 2.19.0 (*)
|    |    |         +--- com.fasterxml.jackson.core:jackson-databind:2.18.2 -> 2.19.0 (*)
|    |    |         +--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.18.2 -> 2.19.0
|    |    |         |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    |         |    +--- org.yaml:snakeyaml:2.4
|    |    |         |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    |         |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    |         \--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.2 -> 2.19.0 (*)
|    |    \--- org.springframework:spring-webmvc:6.2.6 -> 6.2.7 (*)
|    +--- org.webjars:swagger-ui:5.21.0
|    \--- org.webjars:webjars-locator-lite:1.0.1 -> 1.1.0
|         \--- org.jspecify:jspecify:1.0.0
+--- net.logstash.logback:logstash-logback-encoder:8.1
|    \--- com.fasterxml.jackson.core:jackson-databind:2.18.3 -> 2.19.0 (*)
+--- io.micrometer:micrometer-registry-prometheus -> 1.15.0
|    +--- io.micrometer:micrometer-core:1.15.0 (*)
|    +--- io.prometheus:prometheus-metrics-core:1.3.6
|    |    +--- io.prometheus:prometheus-metrics-model:1.3.6
|    |    \--- io.prometheus:prometheus-metrics-config:1.3.6
|    +--- io.prometheus:prometheus-metrics-tracer-common:1.3.6
|    \--- io.prometheus:prometheus-metrics-exposition-formats:1.3.6
|         \--- io.prometheus:prometheus-metrics-exposition-textformats:1.3.6
|              +--- io.prometheus:prometheus-metrics-model:1.3.6
|              \--- io.prometheus:prometheus-metrics-config:1.3.6
+--- org.hibernate.orm:hibernate-micrometer -> 6.6.15.Final
|    +--- org.jboss.logging:jboss-logging:3.5.0.Final -> 3.6.1.Final
|    +--- org.hibernate.orm:hibernate-core:6.6.15.Final
|    |    +--- jakarta.persistence:jakarta.persistence-api:3.1.0
|    |    +--- jakarta.transaction:jakarta.transaction-api:2.0.1
|    |    +--- org.jboss.logging:jboss-logging:3.5.0.Final -> 3.6.1.Final
|    |    +--- org.hibernate.common:hibernate-commons-annotations:7.0.3.Final
|    |    +--- io.smallrye:jandex:3.2.0
|    |    +--- com.fasterxml:classmate:1.5.1 -> 1.7.0
|    |    +--- net.bytebuddy:byte-buddy:1.15.11 -> 1.17.5
|    |    +--- jakarta.xml.bind:jakarta.xml.bind-api:4.0.0 -> 4.0.2 (*)
|    |    +--- org.glassfish.jaxb:jaxb-runtime:4.0.2 -> 4.0.5
|    |    |    \--- org.glassfish.jaxb:jaxb-core:4.0.5
|    |    |         +--- jakarta.xml.bind:jakarta.xml.bind-api:4.0.2 (*)
|    |    |         +--- jakarta.activation:jakarta.activation-api:2.1.3
|    |    |         +--- org.eclipse.angus:angus-activation:2.0.2
|    |    |         |    \--- jakarta.activation:jakarta.activation-api:2.1.3
|    |    |         +--- org.glassfish.jaxb:txw2:4.0.5
|    |    |         \--- com.sun.istack:istack-commons-runtime:4.1.2
|    |    +--- jakarta.inject:jakarta.inject-api:2.0.1
|    |    \--- org.antlr:antlr4-runtime:4.13.0
|    \--- io.micrometer:micrometer-core:1.10.4 -> 1.15.0 (*)
+--- no.nav.boot:boot-conditionals:5.1.7
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.25
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.25
|    |         \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    +--- ch.qos.logback:logback-core:1.5.18
|    +--- org.slf4j:slf4j-api:2.0.17
|    \--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
+--- org.springframework.boot:spring-boot-starter-data-redis -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- io.lettuce:lettuce-core:6.5.5.RELEASE
|    |    +--- io.netty:netty-common:4.1.118.Final -> 4.1.121.Final
|    |    +--- io.netty:netty-handler:4.1.118.Final -> 4.1.121.Final (*)
|    |    +--- io.netty:netty-transport:4.1.118.Final -> 4.1.121.Final (*)
|    |    \--- io.projectreactor:reactor-core:3.6.6 -> 3.7.6 (*)
|    \--- org.springframework.data:spring-data-redis:3.5.0
|         +--- org.springframework.data:spring-data-keyvalue:3.5.0
|         |    +--- org.springframework.data:spring-data-commons:3.5.0
|         |    |    +--- org.springframework:spring-core:6.2.7 (*)
|         |    |    +--- org.springframework:spring-beans:6.2.7 (*)
|         |    |    \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
|         |    +--- org.springframework:spring-context:6.2.7 (*)
|         |    +--- org.springframework:spring-tx:6.2.7 (*)
|         |    \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
|         +--- org.springframework:spring-tx:6.2.7 (*)
|         +--- org.springframework:spring-oxm:6.2.7
|         |    +--- jakarta.xml.bind:jakarta.xml.bind-api:3.0.1 -> 4.0.2 (*)
|         |    +--- org.springframework:spring-beans:6.2.7 (*)
|         |    \--- org.springframework:spring-core:6.2.7 (*)
|         +--- org.springframework:spring-aop:6.2.7 (*)
|         +--- org.springframework:spring-context-support:6.2.7 (*)
|         \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
+--- no.nav.security:token-validation-spring:5.0.25
|    +--- no.nav.security:token-validation-core:5.0.25
|    |    +--- com.nimbusds:oauth2-oidc-sdk:11.23.1
|    |    |    +--- com.github.stephenc.jcip:jcip-annotations:1.0-1
|    |    |    +--- com.nimbusds:content-type:2.3
|    |    |    +--- net.minidev:json-smart:2.5.2
|    |    |    |    \--- net.minidev:accessors-smart:2.5.2
|    |    |    |         \--- org.ow2.asm:asm:9.7.1
|    |    |    +--- com.nimbusds:lang-tag:1.7
|    |    |    \--- com.nimbusds:nimbus-jose-jwt:10.0.2 -> 10.2
|    |    |         \--- com.google.code.gson:gson:2.12.1 -> 2.13.1
|    |    |              \--- com.google.errorprone:error_prone_annotations:2.38.0
|    |    +--- org.slf4j:slf4j-api:2.0.17
|    |    +--- jakarta.validation:jakarta.validation-api:3.0.2
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    |    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
|    +--- no.nav.security:token-validation-filter:5.0.25
|    |    +--- no.nav.security:token-validation-core:5.0.25 (*)
|    |    +--- org.slf4j:slf4j-api:2.0.17
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    |    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
|    +--- org.springframework:spring-context:6.2.6 -> 6.2.7 (*)
|    +--- org.springframework.boot:spring-boot:3.4.5 -> 3.5.0 (*)
|    +--- org.springframework:spring-web:6.2.6 -> 6.2.7 (*)
|    +--- org.springframework:spring-webmvc:6.2.6 -> 6.2.7 (*)
|    +--- com.nimbusds:oauth2-oidc-sdk:11.23.1 (*)
|    +--- org.slf4j:slf4j-api:2.0.17
|    +--- jakarta.validation:jakarta.validation-api:3.0.2
|    +--- org.apache.commons:commons-lang3:3.17.0
|    +--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
+--- no.nav.security:token-client-spring:5.0.25
|    +--- no.nav.security:token-validation-core:5.0.25 (*)
|    +--- no.nav.security:token-client-core:5.0.25
|    |    +--- jakarta.validation:jakarta.validation-api:3.0.2
|    |    +--- org.slf4j:slf4j-api:2.0.17
|    |    +--- com.github.ben-manes.caffeine:caffeine:3.2.0
|    |    |    +--- org.jspecify:jspecify:1.0.0
|    |    |    \--- com.google.errorprone:error_prone_annotations:2.36.0 -> 2.38.0
|    |    +--- com.nimbusds:oauth2-oidc-sdk:11.23.1 (*)
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.18.3 -> 2.19.0 (*)
|    |    +--- com.fasterxml.jackson.module:jackson-module-kotlin:2.18.3 -> 2.19.0 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    |    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
|    +--- org.springframework:spring-web:6.2.6 -> 6.2.7 (*)
|    +--- com.fasterxml.jackson.core:jackson-annotations:2.18.3 -> 2.19.0 (*)
|    +--- com.fasterxml.jackson.core:jackson-databind:2.18.3 -> 2.19.0 (*)
|    +--- org.springframework.boot:spring-boot:3.4.5 -> 3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-configuration-processor:3.4.5 -> 3.5.0
|    +--- com.github.ben-manes.caffeine:caffeine:3.2.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
+--- org.springframework.boot:spring-boot-starter-data-jpa -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-jdbc:3.5.0
|    |    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    |    +--- com.zaxxer:HikariCP:6.3.0
|    |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.17
|    |    \--- org.springframework:spring-jdbc:6.2.7
|    |         +--- org.springframework:spring-beans:6.2.7 (*)
|    |         +--- org.springframework:spring-core:6.2.7 (*)
|    |         \--- org.springframework:spring-tx:6.2.7 (*)
|    +--- org.hibernate.orm:hibernate-core:6.6.15.Final (*)
|    +--- org.springframework.data:spring-data-jpa:3.5.0
|    |    +--- org.springframework.data:spring-data-commons:3.5.0 (*)
|    |    +--- org.springframework:spring-orm:6.2.7
|    |    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    |    +--- org.springframework:spring-jdbc:6.2.7 (*)
|    |    |    \--- org.springframework:spring-tx:6.2.7 (*)
|    |    +--- org.springframework:spring-context:6.2.7 (*)
|    |    +--- org.springframework:spring-aop:6.2.7 (*)
|    |    +--- org.springframework:spring-tx:6.2.7 (*)
|    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    +--- org.antlr:antlr4-runtime:4.13.0
|    |    +--- jakarta.annotation:jakarta.annotation-api:2.0.0 -> 2.1.1
|    |    \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
|    \--- org.springframework:spring-aspects:6.2.7 (*)
+--- org.flywaydb:flyway-core -> 11.7.2
|    +--- com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.15.2 -> 2.19.0
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    \--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2 -> 2.19.0 (*)
+--- org.postgresql:postgresql -> 42.7.5
|    \--- org.checkerframework:checker-qual:3.48.3
\--- org.flywaydb:flyway-database-postgresql -> 11.7.2
     \--- org.flywaydb:flyway-core:11.7.2 (*)

runtimeElements - Runtime elements for the 'main' feature. (n)
No dependencies

runtimeElements-published (n)
No dependencies

runtimeOnly - Runtime only dependencies for null/main. (n)
No dependencies

testAndDevelopmentOnly - Configuration for test and development-only dependencies such as Spring Boot's DevTools.
No dependencies

testAnnotationProcessor - Annotation processors and their dependencies for source set 'test'.
No dependencies

testApi - API dependencies for null/test (n)
No dependencies

testApiDependenciesMetadata
No dependencies

testCompileClasspath - Compile classpath for null/test.
+--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25
|    +--- org.jetbrains:annotations:13.0 -> 23.0.0
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0 -> 1.9.25 (c)
|    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.0 -> 1.9.25 (c)
+--- io.opentelemetry:opentelemetry-api -> 1.50.0
|    \--- io.opentelemetry:opentelemetry-context:1.50.0
+--- io.micrometer:micrometer-tracing -> 1.5.0
|    +--- io.micrometer:micrometer-observation:1.15.0
|    |    \--- io.micrometer:micrometer-commons:1.15.0
|    +--- io.micrometer:context-propagation:1.1.3
|    \--- aopalliance:aopalliance:1.0
+--- org.springframework.boot:spring-boot-starter-webflux -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0
|    |    +--- org.springframework.boot:spring-boot:3.5.0
|    |    |    +--- org.springframework:spring-core:6.2.7
|    |    |    |    \--- org.springframework:spring-jcl:6.2.7
|    |    |    \--- org.springframework:spring-context:6.2.7
|    |    |         +--- org.springframework:spring-aop:6.2.7
|    |    |         |    +--- org.springframework:spring-beans:6.2.7
|    |    |         |    |    \--- org.springframework:spring-core:6.2.7 (*)
|    |    |         |    \--- org.springframework:spring-core:6.2.7 (*)
|    |    |         +--- org.springframework:spring-beans:6.2.7 (*)
|    |    |         +--- org.springframework:spring-core:6.2.7 (*)
|    |    |         +--- org.springframework:spring-expression:6.2.7
|    |    |         |    \--- org.springframework:spring-core:6.2.7 (*)
|    |    |         \--- io.micrometer:micrometer-observation:1.14.7 -> 1.15.0 (*)
|    |    +--- org.springframework.boot:spring-boot-autoconfigure:3.5.0
|    |    |    \--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    +--- org.springframework.boot:spring-boot-starter-logging:3.5.0
|    |    |    +--- ch.qos.logback:logback-classic:1.5.18
|    |    |    |    +--- ch.qos.logback:logback-core:1.5.18
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.17
|    |    |    +--- org.apache.logging.log4j:log4j-to-slf4j:2.24.3
|    |    |    |    +--- org.apache.logging.log4j:log4j-api:2.24.3
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.16 -> 2.0.17
|    |    |    \--- org.slf4j:jul-to-slf4j:2.0.17
|    |    |         \--- org.slf4j:slf4j-api:2.0.17
|    |    +--- jakarta.annotation:jakarta.annotation-api:2.1.1
|    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    \--- org.yaml:snakeyaml:2.4
|    +--- org.springframework.boot:spring-boot-starter-json:3.5.0
|    |    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    |    +--- org.springframework:spring-web:6.2.7
|    |    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    |    \--- io.micrometer:micrometer-observation:1.14.7 -> 1.15.0 (*)
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0
|    |    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.module:jackson-module-kotlin:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.module:jackson-module-parameter-names:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (c)
|    |    |    |         \--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.19.0 (c)
|    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0
|    |    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.19.0
|    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    \--- com.fasterxml.jackson.module:jackson-module-parameter-names:2.19.0
|    |         +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |         +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |         \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-reactor-netty:3.5.0
|    |    \--- io.projectreactor.netty:reactor-netty-http:1.2.6
|    |         +--- io.netty:netty-codec-http:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final
|    |         |    |    \--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-transport:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-resolver:4.1.121.Final
|    |         |    |         \--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-codec:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-handler:4.1.121.Final
|    |         |         +--- io.netty:netty-common:4.1.121.Final
|    |         |         +--- io.netty:netty-resolver:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-transport-native-unix-common:4.1.121.Final
|    |         |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |         |    \--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |         \--- io.netty:netty-codec:4.1.121.Final (*)
|    |         +--- io.netty:netty-codec-http2:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-handler:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-codec-http:4.1.121.Final (*)
|    |         +--- io.netty:netty-resolver-dns:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-resolver:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-codec-dns:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-handler:4.1.121.Final (*)
|    |         +--- io.netty:netty-resolver-dns-native-macos:4.1.121.Final
|    |         |    \--- io.netty:netty-resolver-dns-classes-macos:4.1.121.Final
|    |         |         +--- io.netty:netty-common:4.1.121.Final
|    |         |         +--- io.netty:netty-resolver-dns:4.1.121.Final (*)
|    |         |         \--- io.netty:netty-transport-native-unix-common:4.1.121.Final (*)
|    |         +--- io.netty:netty-transport-native-epoll:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport-native-unix-common:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-transport-classes-epoll:4.1.121.Final
|    |         |         +--- io.netty:netty-common:4.1.121.Final
|    |         |         +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |         \--- io.netty:netty-transport-native-unix-common:4.1.121.Final (*)
|    |         +--- io.projectreactor.netty:reactor-netty-core:1.2.6
|    |         |    +--- io.netty:netty-handler:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-handler-proxy:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-codec-socks:4.1.121.Final
|    |         |    |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    |    |    \--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-codec-http:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-resolver-dns:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-resolver-dns-native-macos:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport-native-epoll:4.1.121.Final (*)
|    |         |    \--- io.projectreactor:reactor-core:3.7.6
|    |         |         \--- org.reactivestreams:reactive-streams:1.0.4
|    |         \--- io.projectreactor:reactor-core:3.7.6 (*)
|    +--- org.springframework:spring-web:6.2.7 (*)
|    \--- org.springframework:spring-webflux:6.2.7
|         +--- org.springframework:spring-beans:6.2.7 (*)
|         +--- org.springframework:spring-core:6.2.7 (*)
|         +--- org.springframework:spring-web:6.2.7 (*)
|         \--- io.projectreactor:reactor-core:3.7.6 (*)
+--- org.apache.httpcomponents.client5:httpclient5 -> 5.4.4
|    +--- org.apache.httpcomponents.core5:httpcore5:5.3.4
|    +--- org.apache.httpcomponents.core5:httpcore5-h2:5.3.4
|    |    \--- org.apache.httpcomponents.core5:httpcore5:5.3.4
|    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.17
+--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0
|    \--- org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.9.0 -> 1.8.1
|         +--- org.jetbrains:annotations:23.0.0
|         +--- org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.8.1
|         |    +--- org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.8.1 (c)
|         |    \--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1 -> 1.9.0 (c)
|         \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.21 -> 1.9.25 (*)
+--- org.jetbrains.kotlin:kotlin-reflect:1.9.25
|    \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
+--- org.springframework.boot:spring-boot-starter-cache -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    \--- org.springframework:spring-context-support:6.2.7
|         +--- org.springframework:spring-beans:6.2.7 (*)
|         +--- org.springframework:spring-context:6.2.7 (*)
|         \--- org.springframework:spring-core:6.2.7 (*)
+--- org.springframework:spring-aspects -> 6.2.7
|    \--- org.aspectj:aspectjweaver:1.9.22.1 -> 1.9.24
+--- org.springframework.retry:spring-retry -> 2.0.12
+--- org.springframework.kafka:spring-kafka -> 3.3.6
|    +--- org.springframework:spring-context:6.2.7 (*)
|    +--- org.springframework:spring-messaging:6.2.7
|    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    \--- org.springframework:spring-core:6.2.7 (*)
|    +--- org.springframework:spring-tx:6.2.7
|    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    \--- org.springframework:spring-core:6.2.7 (*)
|    +--- org.springframework.retry:spring-retry:2.0.12
|    +--- org.apache.kafka:kafka-clients:3.8.1 -> 3.9.1
|    \--- io.micrometer:micrometer-observation:1.14.7 -> 1.15.0 (*)
+--- org.springframework.boot:spring-boot-starter-graphql -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-json:3.5.0 (*)
|    \--- org.springframework.graphql:spring-graphql:1.4.0
|         +--- com.graphql-java:graphql-java:24.0
|         |    +--- com.graphql-java:java-dataloader:5.0.0
|         |    |    +--- org.reactivestreams:reactive-streams:1.0.3 -> 1.0.4
|         |    |    \--- org.jspecify:jspecify:1.0.0
|         |    +--- org.reactivestreams:reactive-streams:1.0.3 -> 1.0.4
|         |    \--- org.jspecify:jspecify:1.0.0
|         +--- io.projectreactor:reactor-core:3.7.6 (*)
|         \--- org.springframework:spring-context:6.2.7 (*)
+--- org.springframework.boot:spring-boot-starter-web -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-json:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-tomcat:3.5.0
|    |    +--- jakarta.annotation:jakarta.annotation-api:2.1.1
|    |    +--- org.apache.tomcat.embed:tomcat-embed-core:10.1.41
|    |    +--- org.apache.tomcat.embed:tomcat-embed-el:10.1.41
|    |    \--- org.apache.tomcat.embed:tomcat-embed-websocket:10.1.41
|    |         \--- org.apache.tomcat.embed:tomcat-embed-core:10.1.41
|    +--- org.springframework:spring-web:6.2.7 (*)
|    \--- org.springframework:spring-webmvc:6.2.7
|         +--- org.springframework:spring-aop:6.2.7 (*)
|         +--- org.springframework:spring-beans:6.2.7 (*)
|         +--- org.springframework:spring-context:6.2.7 (*)
|         +--- org.springframework:spring-core:6.2.7 (*)
|         +--- org.springframework:spring-expression:6.2.7 (*)
|         \--- org.springframework:spring-web:6.2.7 (*)
+--- org.springframework.boot:spring-boot-starter-actuator -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-actuator-autoconfigure:3.5.0
|    |    +--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    +--- org.springframework.boot:spring-boot-actuator:3.5.0
|    |    |    \--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    \--- org.springframework.boot:spring-boot-autoconfigure:3.5.0 (*)
|    +--- io.micrometer:micrometer-observation:1.15.0 (*)
|    \--- io.micrometer:micrometer-jakarta9:1.15.0
|         +--- io.micrometer:micrometer-core:1.15.0
|         |    +--- io.micrometer:micrometer-commons:1.15.0
|         |    \--- io.micrometer:micrometer-observation:1.15.0 (*)
|         +--- io.micrometer:micrometer-commons:1.15.0
|         \--- io.micrometer:micrometer-observation:1.15.0 (*)
+--- org.springframework.boot:spring-boot-starter-validation -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.apache.tomcat.embed:tomcat-embed-el:10.1.41
|    \--- org.hibernate.validator:hibernate-validator:8.0.2.Final
|         +--- jakarta.validation:jakarta.validation-api:3.0.2
|         +--- org.jboss.logging:jboss-logging:3.4.3.Final -> 3.6.1.Final
|         \--- com.fasterxml:classmate:1.5.1 -> 1.7.0
+--- com.fasterxml.jackson.module:jackson-module-kotlin -> 2.19.0
|    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
+--- org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8
|    +--- org.springdoc:springdoc-openapi-starter-webmvc-api:2.8.8
|    |    +--- org.springdoc:springdoc-openapi-starter-common:2.8.8
|    |    |    +--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
|    |    |    +--- org.springframework.boot:spring-boot-starter-validation:3.4.5 -> 3.5.0 (*)
|    |    |    \--- io.swagger.core.v3:swagger-core-jakarta:2.2.30
|    |    |         +--- org.apache.commons:commons-lang3:3.17.0
|    |    |         +--- org.slf4j:slf4j-api:2.0.9 -> 2.0.17
|    |    |         +--- io.swagger.core.v3:swagger-annotations-jakarta:2.2.30
|    |    |         +--- io.swagger.core.v3:swagger-models-jakarta:2.2.30
|    |    |         |    \--- com.fasterxml.jackson.core:jackson-annotations:2.18.2 -> 2.19.0 (*)
|    |    |         +--- org.yaml:snakeyaml:2.3 -> 2.4
|    |    |         +--- jakarta.xml.bind:jakarta.xml.bind-api:3.0.1 -> 4.0.2
|    |    |         |    \--- jakarta.activation:jakarta.activation-api:2.1.3
|    |    |         +--- jakarta.validation:jakarta.validation-api:3.0.2
|    |    |         +--- com.fasterxml.jackson.core:jackson-annotations:2.18.2 -> 2.19.0 (*)
|    |    |         +--- com.fasterxml.jackson.core:jackson-databind:2.18.2 -> 2.19.0 (*)
|    |    |         +--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.18.2 -> 2.19.0
|    |    |         |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    |         |    +--- org.yaml:snakeyaml:2.4
|    |    |         |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    |         |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    |         \--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.2 -> 2.19.0 (*)
|    |    \--- org.springframework:spring-webmvc:6.2.6 -> 6.2.7 (*)
|    +--- org.webjars:swagger-ui:5.21.0
|    \--- org.webjars:webjars-locator-lite:1.0.1 -> 1.1.0
|         \--- org.jspecify:jspecify:1.0.0
+--- net.logstash.logback:logstash-logback-encoder:8.1
|    \--- com.fasterxml.jackson.core:jackson-databind:2.18.3 -> 2.19.0 (*)
+--- io.micrometer:micrometer-registry-prometheus -> 1.15.0
|    +--- io.micrometer:micrometer-core:1.15.0 (*)
|    +--- io.prometheus:prometheus-metrics-core:1.3.6
|    |    +--- io.prometheus:prometheus-metrics-model:1.3.6
|    |    \--- io.prometheus:prometheus-metrics-config:1.3.6
|    \--- io.prometheus:prometheus-metrics-tracer-common:1.3.6
+--- org.hibernate.orm:hibernate-micrometer -> 6.6.15.Final
+--- no.nav.boot:boot-conditionals:5.1.7
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.25
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.25
|    |         \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    +--- ch.qos.logback:logback-core:1.5.18
|    +--- org.slf4j:slf4j-api:2.0.17
|    \--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
+--- org.springframework.boot:spring-boot-starter-data-redis -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- io.lettuce:lettuce-core:6.5.5.RELEASE
|    |    +--- io.netty:netty-common:4.1.118.Final -> 4.1.121.Final
|    |    +--- io.netty:netty-handler:4.1.118.Final -> 4.1.121.Final (*)
|    |    +--- io.netty:netty-transport:4.1.118.Final -> 4.1.121.Final (*)
|    |    \--- io.projectreactor:reactor-core:3.6.6 -> 3.7.6 (*)
|    \--- org.springframework.data:spring-data-redis:3.5.0
|         +--- org.springframework.data:spring-data-keyvalue:3.5.0
|         |    +--- org.springframework.data:spring-data-commons:3.5.0
|         |    |    +--- org.springframework:spring-core:6.2.7 (*)
|         |    |    +--- org.springframework:spring-beans:6.2.7 (*)
|         |    |    \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
|         |    +--- org.springframework:spring-context:6.2.7 (*)
|         |    +--- org.springframework:spring-tx:6.2.7 (*)
|         |    \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
|         +--- org.springframework:spring-tx:6.2.7 (*)
|         +--- org.springframework:spring-oxm:6.2.7
|         |    +--- org.springframework:spring-beans:6.2.7 (*)
|         |    \--- org.springframework:spring-core:6.2.7 (*)
|         +--- org.springframework:spring-aop:6.2.7 (*)
|         +--- org.springframework:spring-context-support:6.2.7 (*)
|         \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
+--- no.nav.security:token-validation-spring:5.0.25
|    +--- no.nav.security:token-validation-core:5.0.25
|    |    +--- com.nimbusds:oauth2-oidc-sdk:11.23.1
|    |    |    +--- com.github.stephenc.jcip:jcip-annotations:1.0-1
|    |    |    +--- com.nimbusds:content-type:2.3
|    |    |    +--- net.minidev:json-smart:2.5.2
|    |    |    |    \--- net.minidev:accessors-smart:2.5.2
|    |    |    |         \--- org.ow2.asm:asm:9.7.1
|    |    |    +--- com.nimbusds:lang-tag:1.7
|    |    |    \--- com.nimbusds:nimbus-jose-jwt:10.0.2 -> 10.2
|    |    |         \--- com.google.code.gson:gson:2.12.1 -> 2.13.1
|    |    |              \--- com.google.errorprone:error_prone_annotations:2.38.0
|    |    +--- org.slf4j:slf4j-api:2.0.17
|    |    +--- jakarta.validation:jakarta.validation-api:3.0.2
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    |    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
|    +--- no.nav.security:token-validation-filter:5.0.25
|    |    +--- no.nav.security:token-validation-core:5.0.25 (*)
|    |    +--- org.slf4j:slf4j-api:2.0.17
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    |    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
|    +--- org.springframework:spring-context:6.2.6 -> 6.2.7 (*)
|    +--- org.springframework.boot:spring-boot:3.4.5 -> 3.5.0 (*)
|    +--- org.springframework:spring-web:6.2.6 -> 6.2.7 (*)
|    +--- org.springframework:spring-webmvc:6.2.6 -> 6.2.7 (*)
|    +--- com.nimbusds:oauth2-oidc-sdk:11.23.1 (*)
|    +--- org.slf4j:slf4j-api:2.0.17
|    +--- jakarta.validation:jakarta.validation-api:3.0.2
|    +--- org.apache.commons:commons-lang3:3.17.0
|    +--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
+--- no.nav.security:token-client-spring:5.0.25
|    +--- no.nav.security:token-validation-core:5.0.25 (*)
|    +--- no.nav.security:token-client-core:5.0.25
|    |    +--- jakarta.validation:jakarta.validation-api:3.0.2
|    |    +--- org.slf4j:slf4j-api:2.0.17
|    |    +--- com.github.ben-manes.caffeine:caffeine:3.2.0
|    |    |    +--- org.jspecify:jspecify:1.0.0
|    |    |    \--- com.google.errorprone:error_prone_annotations:2.36.0 -> 2.38.0
|    |    +--- com.nimbusds:oauth2-oidc-sdk:11.23.1 (*)
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.18.3 -> 2.19.0 (*)
|    |    +--- com.fasterxml.jackson.module:jackson-module-kotlin:2.18.3 -> 2.19.0 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    |    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
|    +--- org.springframework:spring-web:6.2.6 -> 6.2.7 (*)
|    +--- com.fasterxml.jackson.core:jackson-annotations:2.18.3 -> 2.19.0 (*)
|    +--- com.fasterxml.jackson.core:jackson-databind:2.18.3 -> 2.19.0 (*)
|    +--- org.springframework.boot:spring-boot:3.4.5 -> 3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-configuration-processor:3.4.5 -> 3.5.0
|    +--- com.github.ben-manes.caffeine:caffeine:3.2.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
+--- org.springframework.boot:spring-boot-starter-data-jpa -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-jdbc:3.5.0
|    |    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    |    +--- com.zaxxer:HikariCP:6.3.0
|    |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.17
|    |    \--- org.springframework:spring-jdbc:6.2.7
|    |         +--- org.springframework:spring-beans:6.2.7 (*)
|    |         +--- org.springframework:spring-core:6.2.7 (*)
|    |         \--- org.springframework:spring-tx:6.2.7 (*)
|    +--- org.hibernate.orm:hibernate-core:6.6.15.Final
|    |    +--- jakarta.persistence:jakarta.persistence-api:3.1.0
|    |    \--- jakarta.transaction:jakarta.transaction-api:2.0.1
|    +--- org.springframework.data:spring-data-jpa:3.5.0
|    |    +--- org.springframework.data:spring-data-commons:3.5.0 (*)
|    |    +--- org.springframework:spring-orm:6.2.7
|    |    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    |    +--- org.springframework:spring-jdbc:6.2.7 (*)
|    |    |    \--- org.springframework:spring-tx:6.2.7 (*)
|    |    +--- org.springframework:spring-context:6.2.7 (*)
|    |    +--- org.springframework:spring-aop:6.2.7 (*)
|    |    +--- org.springframework:spring-tx:6.2.7 (*)
|    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    +--- org.antlr:antlr4-runtime:4.13.0
|    |    +--- jakarta.annotation:jakarta.annotation-api:2.0.0 -> 2.1.1
|    |    \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
|    \--- org.springframework:spring-aspects:6.2.7 (*)
+--- org.flywaydb:flyway-core -> 11.7.2
|    +--- com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.15.2 -> 2.19.0
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    \--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2 -> 2.19.0 (*)
+--- org.postgresql:postgresql -> 42.7.5
+--- org.flywaydb:flyway-database-postgresql -> 11.7.2
|    \--- org.flywaydb:flyway-core:11.7.2 (*)
+--- org.springframework.boot:spring-boot-testcontainers -> 3.5.0
|    +--- org.springframework.boot:spring-boot-autoconfigure:3.5.0 (*)
|    \--- org.testcontainers:testcontainers:1.21.0
|         +--- junit:junit:4.13.2
|         |    \--- org.hamcrest:hamcrest-core:1.3 -> 3.0
|         |         \--- org.hamcrest:hamcrest:3.0
|         +--- org.slf4j:slf4j-api:1.7.36 -> 2.0.17
|         +--- org.apache.commons:commons-compress:1.24.0
|         +--- org.rnorth.duct-tape:duct-tape:1.0.8
|         |    \--- org.jetbrains:annotations:17.0.0 -> 23.0.0
|         +--- com.github.docker-java:docker-java-api:3.4.2
|         |    +--- com.fasterxml.jackson.core:jackson-annotations:2.10.3 -> 2.19.0 (*)
|         |    \--- org.slf4j:slf4j-api:1.7.30 -> 2.0.17
|         \--- com.github.docker-java:docker-java-transport-zerodep:3.4.2
|              +--- com.github.docker-java:docker-java-transport:3.4.2
|              +--- org.slf4j:slf4j-api:1.7.25 -> 2.0.17
|              \--- net.java.dev.jna:jna:5.13.0
+--- org.testcontainers:postgresql -> 1.21.0
|    \--- org.testcontainers:jdbc:1.21.0
|         \--- org.testcontainers:database-commons:1.21.0
|              \--- org.testcontainers:testcontainers:1.21.0 (*)
+--- org.testcontainers:junit-jupiter -> 1.21.0
|    \--- org.testcontainers:testcontainers:1.21.0 (*)
+--- com.github.stefanbirkner:system-lambda:1.2.1
+--- org.junit.jupiter:junit-jupiter -> 5.12.2
|    +--- org.junit:junit-bom:5.12.2
|    |    +--- org.junit.jupiter:junit-jupiter:5.12.2 (c)
|    |    +--- org.junit.jupiter:junit-jupiter-api:5.12.2 (c)
|    |    +--- org.junit.jupiter:junit-jupiter-params:5.12.2 (c)
|    |    \--- org.junit.platform:junit-platform-commons:1.12.2 (c)
|    +--- org.junit.jupiter:junit-jupiter-api:5.12.2
|    |    +--- org.junit:junit-bom:5.12.2 (*)
|    |    +--- org.opentest4j:opentest4j:1.3.0
|    |    +--- org.junit.platform:junit-platform-commons:1.12.2
|    |    |    +--- org.junit:junit-bom:5.12.2 (*)
|    |    |    \--- org.apiguardian:apiguardian-api:1.1.2
|    |    \--- org.apiguardian:apiguardian-api:1.1.2
|    \--- org.junit.jupiter:junit-jupiter-params:5.12.2
|         +--- org.junit:junit-bom:5.12.2 (*)
|         +--- org.junit.jupiter:junit-jupiter-api:5.12.2 (*)
|         \--- org.apiguardian:apiguardian-api:1.1.2
+--- org.springframework.boot:spring-boot-starter-test -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-test:3.5.0
|    |    +--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    \--- org.springframework:spring-test:6.2.7
|    |         \--- org.springframework:spring-core:6.2.7 (*)
|    +--- org.springframework.boot:spring-boot-test-autoconfigure:3.5.0
|    |    +--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    +--- org.springframework.boot:spring-boot-test:3.5.0 (*)
|    |    \--- org.springframework.boot:spring-boot-autoconfigure:3.5.0 (*)
|    +--- com.jayway.jsonpath:json-path:2.9.0
|    +--- jakarta.xml.bind:jakarta.xml.bind-api:4.0.2 (*)
|    +--- net.minidev:json-smart:2.5.2 (*)
|    +--- org.assertj:assertj-core:3.27.3
|    |    \--- net.bytebuddy:byte-buddy:1.15.11 -> 1.17.5
|    +--- org.awaitility:awaitility:4.3.0
|    |    \--- org.hamcrest:hamcrest:2.1 -> 3.0
|    +--- org.hamcrest:hamcrest:3.0
|    +--- org.junit.jupiter:junit-jupiter:5.12.2 (*)
|    +--- org.mockito:mockito-core:5.17.0
|    |    +--- net.bytebuddy:byte-buddy:1.15.11 -> 1.17.5
|    |    \--- net.bytebuddy:byte-buddy-agent:1.15.11 -> 1.17.5
|    +--- org.mockito:mockito-junit-jupiter:5.17.0
|    |    \--- org.mockito:mockito-core:5.17.0 (*)
|    +--- org.skyscreamer:jsonassert:1.5.3
|    |    \--- com.vaadin.external.google:android-json:0.0.20131108.vaadin1
|    +--- org.springframework:spring-core:6.2.7 (*)
|    +--- org.springframework:spring-test:6.2.7 (*)
|    \--- org.xmlunit:xmlunit-core:2.10.1
+--- io.mockk:mockk:1.14.2
|    \--- io.mockk:mockk-jvm:1.14.2
|         +--- io.mockk:mockk-dsl:1.14.2
|         |    \--- io.mockk:mockk-dsl-jvm:1.14.2
|         |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.0.0 -> 1.9.25 (*)
|         +--- io.mockk:mockk-agent:1.14.2
|         |    \--- io.mockk:mockk-agent-jvm:1.14.2
|         |         +--- org.objenesis:objenesis:3.3
|         |         +--- net.bytebuddy:byte-buddy:1.14.17 -> 1.17.5
|         |         +--- net.bytebuddy:byte-buddy-agent:1.14.17 -> 1.17.5
|         |         +--- io.mockk:mockk-agent-api:1.14.2
|         |         |    \--- io.mockk:mockk-agent-api-jvm:1.14.2
|         |         |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.0.0 -> 1.9.25 (*)
|         |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.0.0 -> 1.9.25 (*)
|         +--- io.mockk:mockk-agent-api:1.14.2 (*)
|         +--- io.mockk:mockk-core:1.14.2
|         |    \--- io.mockk:mockk-core-jvm:1.14.2
|         |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.0.0 -> 1.9.25 (*)
|         \--- org.jetbrains.kotlin:kotlin-stdlib:2.0.0 -> 1.9.25 (*)
+--- com.ninja-squad:springmockk:4.0.2
|    +--- io.mockk:mockk-jvm:1.13.3 -> 1.14.2 (*)
|    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.21 -> 1.9.25 (*)
+--- org.jetbrains.kotlin:kotlin-test:1.9.25
|    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    \--- org.jetbrains.kotlin:kotlin-test-junit5:1.9.25
|         +--- org.jetbrains.kotlin:kotlin-test:1.9.25 (*)
|         \--- org.junit.jupiter:junit-jupiter-api:5.6.3 -> 5.12.2 (*)
\--- org.jetbrains.kotlin:kotlin-test:1.9.25 (*)

testCompileOnly - Compile only dependencies for null/test. (n)
No dependencies

testCompileOnlyDependenciesMetadata
No dependencies

testImplementation - Implementation only dependencies for null/test. (n)
+--- org.springframework.boot:spring-boot-testcontainers (n)
+--- org.testcontainers:postgresql (n)
+--- org.testcontainers:junit-jupiter (n)
+--- com.github.stefanbirkner:system-lambda:1.2.1 (n)
+--- org.junit.jupiter:junit-jupiter (n)
+--- org.springframework.boot:spring-boot-starter-test (n)
+--- io.mockk:mockk:1.14.2 (n)
+--- com.ninja-squad:springmockk:4.0.2 (n)
\--- org.jetbrains.kotlin:kotlin-test (n)

testImplementationDependenciesMetadata
+--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25
|    \--- org.jetbrains.kotlin:kotlin-stdlib-common:1.9.25 (c)
+--- io.opentelemetry:opentelemetry-api -> 1.50.0
|    \--- io.opentelemetry:opentelemetry-context:1.50.0
+--- io.micrometer:micrometer-tracing -> 1.5.0
|    +--- io.micrometer:micrometer-observation:1.15.0
|    |    \--- io.micrometer:micrometer-commons:1.15.0
|    +--- io.micrometer:context-propagation:1.1.3
|    \--- aopalliance:aopalliance:1.0
+--- org.springframework.boot:spring-boot-starter-webflux -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0
|    |    +--- org.springframework.boot:spring-boot:3.5.0
|    |    |    +--- org.springframework:spring-core:6.2.7
|    |    |    |    \--- org.springframework:spring-jcl:6.2.7
|    |    |    \--- org.springframework:spring-context:6.2.7
|    |    |         +--- org.springframework:spring-aop:6.2.7
|    |    |         |    +--- org.springframework:spring-beans:6.2.7
|    |    |         |    |    \--- org.springframework:spring-core:6.2.7 (*)
|    |    |         |    \--- org.springframework:spring-core:6.2.7 (*)
|    |    |         +--- org.springframework:spring-beans:6.2.7 (*)
|    |    |         +--- org.springframework:spring-core:6.2.7 (*)
|    |    |         +--- org.springframework:spring-expression:6.2.7
|    |    |         |    \--- org.springframework:spring-core:6.2.7 (*)
|    |    |         \--- io.micrometer:micrometer-observation:1.14.7 -> 1.15.0 (*)
|    |    +--- org.springframework.boot:spring-boot-autoconfigure:3.5.0
|    |    |    \--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    +--- org.springframework.boot:spring-boot-starter-logging:3.5.0
|    |    |    +--- ch.qos.logback:logback-classic:1.5.18
|    |    |    |    +--- ch.qos.logback:logback-core:1.5.18
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.17
|    |    |    +--- org.apache.logging.log4j:log4j-to-slf4j:2.24.3
|    |    |    |    +--- org.apache.logging.log4j:log4j-api:2.24.3
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.16 -> 2.0.17
|    |    |    \--- org.slf4j:jul-to-slf4j:2.0.17
|    |    |         \--- org.slf4j:slf4j-api:2.0.17
|    |    +--- jakarta.annotation:jakarta.annotation-api:2.1.1
|    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    \--- org.yaml:snakeyaml:2.4
|    +--- org.springframework.boot:spring-boot-starter-json:3.5.0
|    |    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    |    +--- org.springframework:spring-web:6.2.7
|    |    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    |    \--- io.micrometer:micrometer-observation:1.14.7 -> 1.15.0 (*)
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0
|    |    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.module:jackson-module-kotlin:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.module:jackson-module-parameter-names:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (c)
|    |    |    |         \--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.19.0 (c)
|    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0
|    |    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.19.0
|    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    \--- com.fasterxml.jackson.module:jackson-module-parameter-names:2.19.0
|    |         +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |         +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |         \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-reactor-netty:3.5.0
|    |    \--- io.projectreactor.netty:reactor-netty-http:1.2.6
|    |         +--- io.netty:netty-codec-http:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final
|    |         |    |    \--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-transport:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-resolver:4.1.121.Final
|    |         |    |         \--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-codec:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-handler:4.1.121.Final
|    |         |         +--- io.netty:netty-common:4.1.121.Final
|    |         |         +--- io.netty:netty-resolver:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-transport-native-unix-common:4.1.121.Final
|    |         |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |         |    \--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |         \--- io.netty:netty-codec:4.1.121.Final (*)
|    |         +--- io.netty:netty-codec-http2:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-handler:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-codec-http:4.1.121.Final (*)
|    |         +--- io.netty:netty-resolver-dns:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-resolver:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-codec-dns:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-handler:4.1.121.Final (*)
|    |         +--- io.netty:netty-resolver-dns-native-macos:4.1.121.Final
|    |         |    \--- io.netty:netty-resolver-dns-classes-macos:4.1.121.Final
|    |         |         +--- io.netty:netty-common:4.1.121.Final
|    |         |         +--- io.netty:netty-resolver-dns:4.1.121.Final (*)
|    |         |         \--- io.netty:netty-transport-native-unix-common:4.1.121.Final (*)
|    |         +--- io.netty:netty-transport-native-epoll:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport-native-unix-common:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-transport-classes-epoll:4.1.121.Final
|    |         |         +--- io.netty:netty-common:4.1.121.Final
|    |         |         +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |         \--- io.netty:netty-transport-native-unix-common:4.1.121.Final (*)
|    |         +--- io.projectreactor.netty:reactor-netty-core:1.2.6
|    |         |    +--- io.netty:netty-handler:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-handler-proxy:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-codec-socks:4.1.121.Final
|    |         |    |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    |    |    \--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-codec-http:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-resolver-dns:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-resolver-dns-native-macos:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport-native-epoll:4.1.121.Final (*)
|    |         |    \--- io.projectreactor:reactor-core:3.7.6
|    |         |         \--- org.reactivestreams:reactive-streams:1.0.4
|    |         \--- io.projectreactor:reactor-core:3.7.6 (*)
|    +--- org.springframework:spring-web:6.2.7 (*)
|    \--- org.springframework:spring-webflux:6.2.7
|         +--- org.springframework:spring-beans:6.2.7 (*)
|         +--- org.springframework:spring-core:6.2.7 (*)
|         +--- org.springframework:spring-web:6.2.7 (*)
|         \--- io.projectreactor:reactor-core:3.7.6 (*)
+--- org.apache.httpcomponents.client5:httpclient5 -> 5.4.4
|    +--- org.apache.httpcomponents.core5:httpcore5:5.3.4
|    +--- org.apache.httpcomponents.core5:httpcore5-h2:5.3.4
|    |    \--- org.apache.httpcomponents.core5:httpcore5:5.3.4
|    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.17
+--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0
|    +--- org.jetbrains.kotlin:kotlin-stdlib:2.0.0 -> 1.9.25 (*)
|    \--- org.jetbrains.kotlinx:atomicfu:0.23.1
|         +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.21 -> 1.9.25 (*)
|         \--- org.jetbrains.kotlin:kotlin-stdlib-common:1.9.21 -> 1.9.25
|              \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
+--- org.jetbrains.kotlin:kotlin-reflect:1.9.25
|    \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
+--- org.springframework.boot:spring-boot-starter-cache -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    \--- org.springframework:spring-context-support:6.2.7
|         +--- org.springframework:spring-beans:6.2.7 (*)
|         +--- org.springframework:spring-context:6.2.7 (*)
|         \--- org.springframework:spring-core:6.2.7 (*)
+--- org.springframework:spring-aspects -> 6.2.7
|    \--- org.aspectj:aspectjweaver:1.9.22.1 -> 1.9.24
+--- org.springframework.retry:spring-retry -> 2.0.12
+--- org.springframework.kafka:spring-kafka -> 3.3.6
|    +--- org.springframework:spring-context:6.2.7 (*)
|    +--- org.springframework:spring-messaging:6.2.7
|    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    \--- org.springframework:spring-core:6.2.7 (*)
|    +--- org.springframework:spring-tx:6.2.7
|    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    \--- org.springframework:spring-core:6.2.7 (*)
|    +--- org.springframework.retry:spring-retry:2.0.12
|    +--- org.apache.kafka:kafka-clients:3.8.1 -> 3.9.1
|    \--- io.micrometer:micrometer-observation:1.14.7 -> 1.15.0 (*)
+--- org.springframework.boot:spring-boot-starter-graphql -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-json:3.5.0 (*)
|    \--- org.springframework.graphql:spring-graphql:1.4.0
|         +--- com.graphql-java:graphql-java:24.0
|         |    +--- com.graphql-java:java-dataloader:5.0.0
|         |    |    +--- org.reactivestreams:reactive-streams:1.0.3 -> 1.0.4
|         |    |    \--- org.jspecify:jspecify:1.0.0
|         |    +--- org.reactivestreams:reactive-streams:1.0.3 -> 1.0.4
|         |    \--- org.jspecify:jspecify:1.0.0
|         +--- io.projectreactor:reactor-core:3.7.6 (*)
|         \--- org.springframework:spring-context:6.2.7 (*)
+--- org.springframework.boot:spring-boot-starter-web -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-json:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-tomcat:3.5.0
|    |    +--- jakarta.annotation:jakarta.annotation-api:2.1.1
|    |    +--- org.apache.tomcat.embed:tomcat-embed-core:10.1.41
|    |    +--- org.apache.tomcat.embed:tomcat-embed-el:10.1.41
|    |    \--- org.apache.tomcat.embed:tomcat-embed-websocket:10.1.41
|    |         \--- org.apache.tomcat.embed:tomcat-embed-core:10.1.41
|    +--- org.springframework:spring-web:6.2.7 (*)
|    \--- org.springframework:spring-webmvc:6.2.7
|         +--- org.springframework:spring-aop:6.2.7 (*)
|         +--- org.springframework:spring-beans:6.2.7 (*)
|         +--- org.springframework:spring-context:6.2.7 (*)
|         +--- org.springframework:spring-core:6.2.7 (*)
|         +--- org.springframework:spring-expression:6.2.7 (*)
|         \--- org.springframework:spring-web:6.2.7 (*)
+--- org.springframework.boot:spring-boot-starter-actuator -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-actuator-autoconfigure:3.5.0
|    |    +--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    +--- org.springframework.boot:spring-boot-actuator:3.5.0
|    |    |    \--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    \--- org.springframework.boot:spring-boot-autoconfigure:3.5.0 (*)
|    +--- io.micrometer:micrometer-observation:1.15.0 (*)
|    \--- io.micrometer:micrometer-jakarta9:1.15.0
|         +--- io.micrometer:micrometer-core:1.15.0
|         |    +--- io.micrometer:micrometer-commons:1.15.0
|         |    \--- io.micrometer:micrometer-observation:1.15.0 (*)
|         +--- io.micrometer:micrometer-commons:1.15.0
|         \--- io.micrometer:micrometer-observation:1.15.0 (*)
+--- org.springframework.boot:spring-boot-starter-validation -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.apache.tomcat.embed:tomcat-embed-el:10.1.41
|    \--- org.hibernate.validator:hibernate-validator:8.0.2.Final
|         +--- jakarta.validation:jakarta.validation-api:3.0.2
|         +--- org.jboss.logging:jboss-logging:3.4.3.Final -> 3.6.1.Final
|         \--- com.fasterxml:classmate:1.5.1 -> 1.7.0
+--- com.fasterxml.jackson.module:jackson-module-kotlin -> 2.19.0
|    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
+--- org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8
|    +--- org.springdoc:springdoc-openapi-starter-webmvc-api:2.8.8
|    |    +--- org.springdoc:springdoc-openapi-starter-common:2.8.8
|    |    |    +--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
|    |    |    +--- org.springframework.boot:spring-boot-starter-validation:3.4.5 -> 3.5.0 (*)
|    |    |    \--- io.swagger.core.v3:swagger-core-jakarta:2.2.30
|    |    |         +--- org.apache.commons:commons-lang3:3.17.0
|    |    |         +--- org.slf4j:slf4j-api:2.0.9 -> 2.0.17
|    |    |         +--- io.swagger.core.v3:swagger-annotations-jakarta:2.2.30
|    |    |         +--- io.swagger.core.v3:swagger-models-jakarta:2.2.30
|    |    |         |    \--- com.fasterxml.jackson.core:jackson-annotations:2.18.2 -> 2.19.0 (*)
|    |    |         +--- org.yaml:snakeyaml:2.3 -> 2.4
|    |    |         +--- jakarta.xml.bind:jakarta.xml.bind-api:3.0.1 -> 4.0.2
|    |    |         |    \--- jakarta.activation:jakarta.activation-api:2.1.3
|    |    |         +--- jakarta.validation:jakarta.validation-api:3.0.2
|    |    |         +--- com.fasterxml.jackson.core:jackson-annotations:2.18.2 -> 2.19.0 (*)
|    |    |         +--- com.fasterxml.jackson.core:jackson-databind:2.18.2 -> 2.19.0 (*)
|    |    |         +--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.18.2 -> 2.19.0
|    |    |         |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    |         |    +--- org.yaml:snakeyaml:2.4
|    |    |         |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    |         |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    |         \--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.2 -> 2.19.0 (*)
|    |    \--- org.springframework:spring-webmvc:6.2.6 -> 6.2.7 (*)
|    +--- org.webjars:swagger-ui:5.21.0
|    \--- org.webjars:webjars-locator-lite:1.0.1 -> 1.1.0
|         \--- org.jspecify:jspecify:1.0.0
+--- net.logstash.logback:logstash-logback-encoder:8.1
|    \--- com.fasterxml.jackson.core:jackson-databind:2.18.3 -> 2.19.0 (*)
+--- io.micrometer:micrometer-registry-prometheus -> 1.15.0
|    +--- io.micrometer:micrometer-core:1.15.0 (*)
|    +--- io.prometheus:prometheus-metrics-core:1.3.6
|    |    +--- io.prometheus:prometheus-metrics-model:1.3.6
|    |    \--- io.prometheus:prometheus-metrics-config:1.3.6
|    \--- io.prometheus:prometheus-metrics-tracer-common:1.3.6
+--- org.hibernate.orm:hibernate-micrometer -> 6.6.15.Final
+--- no.nav.boot:boot-conditionals:5.1.7
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.25
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.25
|    |         \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    +--- ch.qos.logback:logback-core:1.5.18
|    +--- org.slf4j:slf4j-api:2.0.17
|    \--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
+--- org.springframework.boot:spring-boot-starter-data-redis -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- io.lettuce:lettuce-core:6.5.5.RELEASE
|    |    +--- io.netty:netty-common:4.1.118.Final -> 4.1.121.Final
|    |    +--- io.netty:netty-handler:4.1.118.Final -> 4.1.121.Final (*)
|    |    +--- io.netty:netty-transport:4.1.118.Final -> 4.1.121.Final (*)
|    |    \--- io.projectreactor:reactor-core:3.6.6 -> 3.7.6 (*)
|    \--- org.springframework.data:spring-data-redis:3.5.0
|         +--- org.springframework.data:spring-data-keyvalue:3.5.0
|         |    +--- org.springframework.data:spring-data-commons:3.5.0
|         |    |    +--- org.springframework:spring-core:6.2.7 (*)
|         |    |    +--- org.springframework:spring-beans:6.2.7 (*)
|         |    |    \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
|         |    +--- org.springframework:spring-context:6.2.7 (*)
|         |    +--- org.springframework:spring-tx:6.2.7 (*)
|         |    \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
|         +--- org.springframework:spring-tx:6.2.7 (*)
|         +--- org.springframework:spring-oxm:6.2.7
|         |    +--- org.springframework:spring-beans:6.2.7 (*)
|         |    \--- org.springframework:spring-core:6.2.7 (*)
|         +--- org.springframework:spring-aop:6.2.7 (*)
|         +--- org.springframework:spring-context-support:6.2.7 (*)
|         \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
+--- no.nav.security:token-validation-spring:5.0.25
|    +--- no.nav.security:token-validation-core:5.0.25
|    |    +--- com.nimbusds:oauth2-oidc-sdk:11.23.1
|    |    |    +--- com.github.stephenc.jcip:jcip-annotations:1.0-1
|    |    |    +--- com.nimbusds:content-type:2.3
|    |    |    +--- net.minidev:json-smart:2.5.2
|    |    |    |    \--- net.minidev:accessors-smart:2.5.2
|    |    |    |         \--- org.ow2.asm:asm:9.7.1
|    |    |    +--- com.nimbusds:lang-tag:1.7
|    |    |    \--- com.nimbusds:nimbus-jose-jwt:10.0.2 -> 10.2
|    |    |         \--- com.google.code.gson:gson:2.12.1 -> 2.13.1
|    |    |              \--- com.google.errorprone:error_prone_annotations:2.38.0
|    |    +--- org.slf4j:slf4j-api:2.0.17
|    |    +--- jakarta.validation:jakarta.validation-api:3.0.2
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    |    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
|    +--- no.nav.security:token-validation-filter:5.0.25
|    |    +--- no.nav.security:token-validation-core:5.0.25 (*)
|    |    +--- org.slf4j:slf4j-api:2.0.17
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    |    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
|    +--- org.springframework:spring-context:6.2.6 -> 6.2.7 (*)
|    +--- org.springframework.boot:spring-boot:3.4.5 -> 3.5.0 (*)
|    +--- org.springframework:spring-web:6.2.6 -> 6.2.7 (*)
|    +--- org.springframework:spring-webmvc:6.2.6 -> 6.2.7 (*)
|    +--- com.nimbusds:oauth2-oidc-sdk:11.23.1 (*)
|    +--- org.slf4j:slf4j-api:2.0.17
|    +--- jakarta.validation:jakarta.validation-api:3.0.2
|    +--- org.apache.commons:commons-lang3:3.17.0
|    +--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
+--- no.nav.security:token-client-spring:5.0.25
|    +--- no.nav.security:token-validation-core:5.0.25 (*)
|    +--- no.nav.security:token-client-core:5.0.25
|    |    +--- jakarta.validation:jakarta.validation-api:3.0.2
|    |    +--- org.slf4j:slf4j-api:2.0.17
|    |    +--- com.github.ben-manes.caffeine:caffeine:3.2.0
|    |    |    +--- org.jspecify:jspecify:1.0.0
|    |    |    \--- com.google.errorprone:error_prone_annotations:2.36.0 -> 2.38.0
|    |    +--- com.nimbusds:oauth2-oidc-sdk:11.23.1 (*)
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.18.3 -> 2.19.0 (*)
|    |    +--- com.fasterxml.jackson.module:jackson-module-kotlin:2.18.3 -> 2.19.0 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    |    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
|    +--- org.springframework:spring-web:6.2.6 -> 6.2.7 (*)
|    +--- com.fasterxml.jackson.core:jackson-annotations:2.18.3 -> 2.19.0 (*)
|    +--- com.fasterxml.jackson.core:jackson-databind:2.18.3 -> 2.19.0 (*)
|    +--- org.springframework.boot:spring-boot:3.4.5 -> 3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-configuration-processor:3.4.5 -> 3.5.0
|    +--- com.github.ben-manes.caffeine:caffeine:3.2.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
+--- org.springframework.boot:spring-boot-starter-data-jpa -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-jdbc:3.5.0
|    |    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    |    +--- com.zaxxer:HikariCP:6.3.0
|    |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.17
|    |    \--- org.springframework:spring-jdbc:6.2.7
|    |         +--- org.springframework:spring-beans:6.2.7 (*)
|    |         +--- org.springframework:spring-core:6.2.7 (*)
|    |         \--- org.springframework:spring-tx:6.2.7 (*)
|    +--- org.hibernate.orm:hibernate-core:6.6.15.Final
|    |    +--- jakarta.persistence:jakarta.persistence-api:3.1.0
|    |    \--- jakarta.transaction:jakarta.transaction-api:2.0.1
|    +--- org.springframework.data:spring-data-jpa:3.5.0
|    |    +--- org.springframework.data:spring-data-commons:3.5.0 (*)
|    |    +--- org.springframework:spring-orm:6.2.7
|    |    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    |    +--- org.springframework:spring-jdbc:6.2.7 (*)
|    |    |    \--- org.springframework:spring-tx:6.2.7 (*)
|    |    +--- org.springframework:spring-context:6.2.7 (*)
|    |    +--- org.springframework:spring-aop:6.2.7 (*)
|    |    +--- org.springframework:spring-tx:6.2.7 (*)
|    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    +--- org.antlr:antlr4-runtime:4.13.0
|    |    +--- jakarta.annotation:jakarta.annotation-api:2.0.0 -> 2.1.1
|    |    \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
|    \--- org.springframework:spring-aspects:6.2.7 (*)
+--- org.flywaydb:flyway-core -> 11.7.2
|    +--- com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.15.2 -> 2.19.0
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    \--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2 -> 2.19.0 (*)
+--- org.postgresql:postgresql -> 42.7.5
+--- org.flywaydb:flyway-database-postgresql -> 11.7.2
|    \--- org.flywaydb:flyway-core:11.7.2 (*)
+--- org.springframework.boot:spring-boot-testcontainers -> 3.5.0
|    +--- org.springframework.boot:spring-boot-autoconfigure:3.5.0 (*)
|    \--- org.testcontainers:testcontainers:1.21.0
|         +--- junit:junit:4.13.2
|         |    \--- org.hamcrest:hamcrest-core:1.3 -> 3.0
|         |         \--- org.hamcrest:hamcrest:3.0
|         +--- org.slf4j:slf4j-api:1.7.36 -> 2.0.17
|         +--- org.apache.commons:commons-compress:1.24.0
|         +--- org.rnorth.duct-tape:duct-tape:1.0.8
|         |    \--- org.jetbrains:annotations:17.0.0
|         +--- com.github.docker-java:docker-java-api:3.4.2
|         |    +--- com.fasterxml.jackson.core:jackson-annotations:2.10.3 -> 2.19.0 (*)
|         |    \--- org.slf4j:slf4j-api:1.7.30 -> 2.0.17
|         \--- com.github.docker-java:docker-java-transport-zerodep:3.4.2
|              +--- com.github.docker-java:docker-java-transport:3.4.2
|              +--- org.slf4j:slf4j-api:1.7.25 -> 2.0.17
|              \--- net.java.dev.jna:jna:5.13.0
+--- org.testcontainers:postgresql -> 1.21.0
|    \--- org.testcontainers:jdbc:1.21.0
|         \--- org.testcontainers:database-commons:1.21.0
|              \--- org.testcontainers:testcontainers:1.21.0 (*)
+--- org.testcontainers:junit-jupiter -> 1.21.0
|    \--- org.testcontainers:testcontainers:1.21.0 (*)
+--- com.github.stefanbirkner:system-lambda:1.2.1
+--- org.junit.jupiter:junit-jupiter -> 5.12.2
|    +--- org.junit:junit-bom:5.12.2
|    |    +--- org.junit.jupiter:junit-jupiter:5.12.2 (c)
|    |    +--- org.junit.jupiter:junit-jupiter-api:5.12.2 (c)
|    |    +--- org.junit.jupiter:junit-jupiter-params:5.12.2 (c)
|    |    \--- org.junit.platform:junit-platform-commons:1.12.2 (c)
|    +--- org.junit.jupiter:junit-jupiter-api:5.12.2
|    |    +--- org.junit:junit-bom:5.12.2 (*)
|    |    +--- org.opentest4j:opentest4j:1.3.0
|    |    +--- org.junit.platform:junit-platform-commons:1.12.2
|    |    |    +--- org.junit:junit-bom:5.12.2 (*)
|    |    |    \--- org.apiguardian:apiguardian-api:1.1.2
|    |    \--- org.apiguardian:apiguardian-api:1.1.2
|    \--- org.junit.jupiter:junit-jupiter-params:5.12.2
|         +--- org.junit:junit-bom:5.12.2 (*)
|         +--- org.junit.jupiter:junit-jupiter-api:5.12.2 (*)
|         \--- org.apiguardian:apiguardian-api:1.1.2
+--- org.springframework.boot:spring-boot-starter-test -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-test:3.5.0
|    |    +--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    \--- org.springframework:spring-test:6.2.7
|    |         \--- org.springframework:spring-core:6.2.7 (*)
|    +--- org.springframework.boot:spring-boot-test-autoconfigure:3.5.0
|    |    +--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    +--- org.springframework.boot:spring-boot-test:3.5.0 (*)
|    |    \--- org.springframework.boot:spring-boot-autoconfigure:3.5.0 (*)
|    +--- com.jayway.jsonpath:json-path:2.9.0
|    +--- jakarta.xml.bind:jakarta.xml.bind-api:4.0.2 (*)
|    +--- net.minidev:json-smart:2.5.2 (*)
|    +--- org.assertj:assertj-core:3.27.3
|    |    \--- net.bytebuddy:byte-buddy:1.15.11 -> 1.17.5
|    +--- org.awaitility:awaitility:4.3.0
|    |    \--- org.hamcrest:hamcrest:2.1 -> 3.0
|    +--- org.hamcrest:hamcrest:3.0
|    +--- org.junit.jupiter:junit-jupiter:5.12.2 (*)
|    +--- org.mockito:mockito-core:5.17.0
|    |    +--- net.bytebuddy:byte-buddy:1.15.11 -> 1.17.5
|    |    \--- net.bytebuddy:byte-buddy-agent:1.15.11 -> 1.17.5
|    +--- org.mockito:mockito-junit-jupiter:5.17.0
|    |    \--- org.mockito:mockito-core:5.17.0 (*)
|    +--- org.skyscreamer:jsonassert:1.5.3
|    |    \--- com.vaadin.external.google:android-json:0.0.20131108.vaadin1
|    +--- org.springframework:spring-core:6.2.7 (*)
|    +--- org.springframework:spring-test:6.2.7 (*)
|    \--- org.xmlunit:xmlunit-core:2.10.1
+--- io.mockk:mockk:1.14.2
|    +--- io.mockk:mockk-dsl:1.14.2
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib:2.0.0 -> 1.9.25 (*)
|    +--- io.mockk:mockk-agent:1.14.2
|    |    +--- io.mockk:mockk-agent-api:1.14.2
|    |    |    \--- org.jetbrains.kotlin:kotlin-stdlib:2.0.0 -> 1.9.25 (*)
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib:2.0.0 -> 1.9.25 (*)
|    +--- io.mockk:mockk-agent-api:1.14.2 (*)
|    +--- io.mockk:mockk-core:1.14.2
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib:2.0.0 -> 1.9.25 (*)
|    \--- org.jetbrains.kotlin:kotlin-stdlib:2.0.0 -> 1.9.25 (*)
+--- com.ninja-squad:springmockk:4.0.2
|    +--- io.mockk:mockk-jvm:1.13.3
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.20 -> 1.9.25 (*)
|    |    +--- io.mockk:mockk-dsl:1.13.3 -> 1.14.2 (*)
|    |    +--- io.mockk:mockk-agent:1.13.3 -> 1.14.2 (*)
|    |    +--- io.mockk:mockk-agent-api:1.13.3 -> 1.14.2 (*)
|    |    \--- io.mockk:mockk-core:1.13.3 -> 1.14.2 (*)
|    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.21 -> 1.9.25 (*)
+--- org.jetbrains.kotlin:kotlin-test:1.9.25
|    +--- org.jetbrains.kotlin:kotlin-test-common:1.9.25
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    \--- org.jetbrains.kotlin:kotlin-test-annotations-common:1.9.25
|         \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
\--- org.jetbrains.kotlin:kotlin-test:1.9.25 (*)

testIntransitiveDependenciesMetadata
No dependencies

testKotlinScriptDef - Script filename extensions discovery classpath configuration
No dependencies

testKotlinScriptDefExtensions
No dependencies

testRuntimeClasspath - Runtime classpath of null/test.
+--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25
|    +--- org.jetbrains:annotations:13.0 -> 23.0.0
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0 -> 1.9.25 (c)
|    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.0 -> 1.9.25 (c)
+--- io.opentelemetry:opentelemetry-api -> 1.50.0
|    \--- io.opentelemetry:opentelemetry-context:1.50.0
+--- io.micrometer:micrometer-tracing -> 1.5.0
|    +--- io.micrometer:micrometer-observation:1.15.0
|    |    \--- io.micrometer:micrometer-commons:1.15.0
|    +--- io.micrometer:context-propagation:1.1.3
|    \--- aopalliance:aopalliance:1.0
+--- org.springframework.boot:spring-boot-starter-webflux -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0
|    |    +--- org.springframework.boot:spring-boot:3.5.0
|    |    |    +--- org.springframework:spring-core:6.2.7
|    |    |    |    \--- org.springframework:spring-jcl:6.2.7
|    |    |    \--- org.springframework:spring-context:6.2.7
|    |    |         +--- org.springframework:spring-aop:6.2.7
|    |    |         |    +--- org.springframework:spring-beans:6.2.7
|    |    |         |    |    \--- org.springframework:spring-core:6.2.7 (*)
|    |    |         |    \--- org.springframework:spring-core:6.2.7 (*)
|    |    |         +--- org.springframework:spring-beans:6.2.7 (*)
|    |    |         +--- org.springframework:spring-core:6.2.7 (*)
|    |    |         +--- org.springframework:spring-expression:6.2.7
|    |    |         |    \--- org.springframework:spring-core:6.2.7 (*)
|    |    |         \--- io.micrometer:micrometer-observation:1.14.7 -> 1.15.0 (*)
|    |    +--- org.springframework.boot:spring-boot-autoconfigure:3.5.0
|    |    |    \--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    +--- org.springframework.boot:spring-boot-starter-logging:3.5.0
|    |    |    +--- ch.qos.logback:logback-classic:1.5.18
|    |    |    |    +--- ch.qos.logback:logback-core:1.5.18
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.17
|    |    |    +--- org.apache.logging.log4j:log4j-to-slf4j:2.24.3
|    |    |    |    +--- org.apache.logging.log4j:log4j-api:2.24.3
|    |    |    |    \--- org.slf4j:slf4j-api:2.0.16 -> 2.0.17
|    |    |    \--- org.slf4j:jul-to-slf4j:2.0.17
|    |    |         \--- org.slf4j:slf4j-api:2.0.17
|    |    +--- jakarta.annotation:jakarta.annotation-api:2.1.1
|    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    \--- org.yaml:snakeyaml:2.4
|    +--- org.springframework.boot:spring-boot-starter-json:3.5.0
|    |    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    |    +--- org.springframework:spring-web:6.2.7
|    |    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    |    \--- io.micrometer:micrometer-observation:1.14.7 -> 1.15.0 (*)
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0
|    |    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.module:jackson-module-kotlin:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.module:jackson-module-parameter-names:2.19.0 (c)
|    |    |    |         +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (c)
|    |    |    |         \--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.19.0 (c)
|    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0
|    |    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.19.0
|    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    \--- com.fasterxml.jackson.module:jackson-module-parameter-names:2.19.0
|    |         +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |         +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |         \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-reactor-netty:3.5.0
|    |    \--- io.projectreactor.netty:reactor-netty-http:1.2.6
|    |         +--- io.netty:netty-codec-http:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final
|    |         |    |    \--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-transport:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-resolver:4.1.121.Final
|    |         |    |         \--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-codec:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-handler:4.1.121.Final
|    |         |         +--- io.netty:netty-common:4.1.121.Final
|    |         |         +--- io.netty:netty-resolver:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-transport-native-unix-common:4.1.121.Final
|    |         |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |         |    \--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |         \--- io.netty:netty-codec:4.1.121.Final (*)
|    |         +--- io.netty:netty-codec-http2:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-handler:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-codec-http:4.1.121.Final (*)
|    |         +--- io.netty:netty-resolver-dns:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-resolver:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-codec-dns:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-handler:4.1.121.Final (*)
|    |         +--- io.netty:netty-resolver-dns-native-macos:4.1.121.Final
|    |         |    \--- io.netty:netty-resolver-dns-classes-macos:4.1.121.Final
|    |         |         +--- io.netty:netty-common:4.1.121.Final
|    |         |         +--- io.netty:netty-resolver-dns:4.1.121.Final (*)
|    |         |         \--- io.netty:netty-transport-native-unix-common:4.1.121.Final (*)
|    |         +--- io.netty:netty-transport-native-epoll:4.1.121.Final
|    |         |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport-native-unix-common:4.1.121.Final (*)
|    |         |    \--- io.netty:netty-transport-classes-epoll:4.1.121.Final
|    |         |         +--- io.netty:netty-common:4.1.121.Final
|    |         |         +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |         +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |         \--- io.netty:netty-transport-native-unix-common:4.1.121.Final (*)
|    |         +--- io.projectreactor.netty:reactor-netty-core:1.2.6
|    |         |    +--- io.netty:netty-handler:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-handler-proxy:4.1.121.Final
|    |         |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    |    +--- io.netty:netty-codec-socks:4.1.121.Final
|    |         |    |    |    +--- io.netty:netty-common:4.1.121.Final
|    |         |    |    |    +--- io.netty:netty-buffer:4.1.121.Final (*)
|    |         |    |    |    +--- io.netty:netty-transport:4.1.121.Final (*)
|    |         |    |    |    \--- io.netty:netty-codec:4.1.121.Final (*)
|    |         |    |    \--- io.netty:netty-codec-http:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-resolver-dns:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-resolver-dns-native-macos:4.1.121.Final (*)
|    |         |    +--- io.netty:netty-transport-native-epoll:4.1.121.Final (*)
|    |         |    \--- io.projectreactor:reactor-core:3.7.6
|    |         |         \--- org.reactivestreams:reactive-streams:1.0.4
|    |         \--- io.projectreactor:reactor-core:3.7.6 (*)
|    +--- org.springframework:spring-web:6.2.7 (*)
|    \--- org.springframework:spring-webflux:6.2.7
|         +--- org.springframework:spring-beans:6.2.7 (*)
|         +--- org.springframework:spring-core:6.2.7 (*)
|         +--- org.springframework:spring-web:6.2.7 (*)
|         \--- io.projectreactor:reactor-core:3.7.6 (*)
+--- org.apache.httpcomponents.client5:httpclient5 -> 5.4.4
|    +--- org.apache.httpcomponents.core5:httpcore5:5.3.4
|    +--- org.apache.httpcomponents.core5:httpcore5-h2:5.3.4
|    |    \--- org.apache.httpcomponents.core5:httpcore5:5.3.4
|    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.17
+--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0
|    \--- org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.9.0 -> 1.8.1
|         +--- org.jetbrains:annotations:23.0.0
|         +--- org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.8.1
|         |    +--- org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.8.1 (c)
|         |    \--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1 -> 1.9.0 (c)
|         \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.21 -> 1.9.25 (*)
+--- org.jetbrains.kotlin:kotlin-reflect:1.9.25
|    \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
+--- org.springframework.boot:spring-boot-starter-cache -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    \--- org.springframework:spring-context-support:6.2.7
|         +--- org.springframework:spring-beans:6.2.7 (*)
|         +--- org.springframework:spring-context:6.2.7 (*)
|         \--- org.springframework:spring-core:6.2.7 (*)
+--- org.springframework:spring-aspects -> 6.2.7
|    \--- org.aspectj:aspectjweaver:1.9.22.1 -> 1.9.24
+--- org.springframework.retry:spring-retry -> 2.0.12
+--- org.springframework.kafka:spring-kafka -> 3.3.6
|    +--- org.springframework:spring-context:6.2.7 (*)
|    +--- org.springframework:spring-messaging:6.2.7
|    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    \--- org.springframework:spring-core:6.2.7 (*)
|    +--- org.springframework:spring-tx:6.2.7
|    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    \--- org.springframework:spring-core:6.2.7 (*)
|    +--- org.springframework.retry:spring-retry:2.0.12
|    +--- org.apache.kafka:kafka-clients:3.8.1 -> 3.9.1
|    |    +--- com.github.luben:zstd-jni:1.5.6-4
|    |    +--- org.lz4:lz4-java:1.8.0
|    |    +--- org.xerial.snappy:snappy-java:1.1.10.5
|    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.17
|    \--- io.micrometer:micrometer-observation:1.14.7 -> 1.15.0 (*)
+--- org.springframework.boot:spring-boot-starter-graphql -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-json:3.5.0 (*)
|    \--- org.springframework.graphql:spring-graphql:1.4.0
|         +--- io.micrometer:context-propagation:1.1.3
|         +--- com.graphql-java:graphql-java:24.0
|         |    +--- com.graphql-java:java-dataloader:5.0.0
|         |    |    +--- org.reactivestreams:reactive-streams:1.0.3 -> 1.0.4
|         |    |    \--- org.jspecify:jspecify:1.0.0
|         |    +--- org.reactivestreams:reactive-streams:1.0.3 -> 1.0.4
|         |    \--- org.jspecify:jspecify:1.0.0
|         +--- io.projectreactor:reactor-core:3.7.6 (*)
|         \--- org.springframework:spring-context:6.2.7 (*)
+--- org.springframework.boot:spring-boot-starter-web -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-json:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-tomcat:3.5.0
|    |    +--- jakarta.annotation:jakarta.annotation-api:2.1.1
|    |    +--- org.apache.tomcat.embed:tomcat-embed-core:10.1.41
|    |    +--- org.apache.tomcat.embed:tomcat-embed-el:10.1.41
|    |    \--- org.apache.tomcat.embed:tomcat-embed-websocket:10.1.41
|    |         \--- org.apache.tomcat.embed:tomcat-embed-core:10.1.41
|    +--- org.springframework:spring-web:6.2.7 (*)
|    \--- org.springframework:spring-webmvc:6.2.7
|         +--- org.springframework:spring-aop:6.2.7 (*)
|         +--- org.springframework:spring-beans:6.2.7 (*)
|         +--- org.springframework:spring-context:6.2.7 (*)
|         +--- org.springframework:spring-core:6.2.7 (*)
|         +--- org.springframework:spring-expression:6.2.7 (*)
|         \--- org.springframework:spring-web:6.2.7 (*)
+--- org.springframework.boot:spring-boot-starter-actuator -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-actuator-autoconfigure:3.5.0
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0 (*)
|    |    +--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    +--- org.springframework.boot:spring-boot-actuator:3.5.0
|    |    |    \--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    \--- org.springframework.boot:spring-boot-autoconfigure:3.5.0 (*)
|    +--- io.micrometer:micrometer-observation:1.15.0 (*)
|    \--- io.micrometer:micrometer-jakarta9:1.15.0
|         +--- io.micrometer:micrometer-core:1.15.0
|         |    +--- io.micrometer:micrometer-commons:1.15.0
|         |    +--- io.micrometer:micrometer-observation:1.15.0 (*)
|         |    +--- org.hdrhistogram:HdrHistogram:2.2.2
|         |    \--- org.latencyutils:LatencyUtils:2.0.3
|         +--- io.micrometer:micrometer-commons:1.15.0
|         \--- io.micrometer:micrometer-observation:1.15.0 (*)
+--- org.springframework.boot:spring-boot-starter-validation -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.apache.tomcat.embed:tomcat-embed-el:10.1.41
|    \--- org.hibernate.validator:hibernate-validator:8.0.2.Final
|         +--- jakarta.validation:jakarta.validation-api:3.0.2
|         +--- org.jboss.logging:jboss-logging:3.4.3.Final -> 3.6.1.Final
|         \--- com.fasterxml:classmate:1.5.1 -> 1.7.0
+--- com.fasterxml.jackson.module:jackson-module-kotlin -> 2.19.0
|    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    +--- com.fasterxml.jackson.core:jackson-annotations:2.19.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
+--- org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8
|    +--- org.springdoc:springdoc-openapi-starter-webmvc-api:2.8.8
|    |    +--- org.springdoc:springdoc-openapi-starter-common:2.8.8
|    |    |    +--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
|    |    |    +--- org.springframework.boot:spring-boot-starter-validation:3.4.5 -> 3.5.0 (*)
|    |    |    \--- io.swagger.core.v3:swagger-core-jakarta:2.2.30
|    |    |         +--- org.apache.commons:commons-lang3:3.17.0
|    |    |         +--- org.slf4j:slf4j-api:2.0.9 -> 2.0.17
|    |    |         +--- io.swagger.core.v3:swagger-annotations-jakarta:2.2.30
|    |    |         +--- io.swagger.core.v3:swagger-models-jakarta:2.2.30
|    |    |         |    \--- com.fasterxml.jackson.core:jackson-annotations:2.18.2 -> 2.19.0 (*)
|    |    |         +--- org.yaml:snakeyaml:2.3 -> 2.4
|    |    |         +--- jakarta.xml.bind:jakarta.xml.bind-api:3.0.1 -> 4.0.2
|    |    |         |    \--- jakarta.activation:jakarta.activation-api:2.1.3
|    |    |         +--- jakarta.validation:jakarta.validation-api:3.0.2
|    |    |         +--- com.fasterxml.jackson.core:jackson-annotations:2.18.2 -> 2.19.0 (*)
|    |    |         +--- com.fasterxml.jackson.core:jackson-databind:2.18.2 -> 2.19.0 (*)
|    |    |         +--- com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.18.2 -> 2.19.0
|    |    |         |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    |         |    +--- org.yaml:snakeyaml:2.4
|    |    |         |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    |         |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    |    |         \--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.2 -> 2.19.0 (*)
|    |    \--- org.springframework:spring-webmvc:6.2.6 -> 6.2.7 (*)
|    +--- org.webjars:swagger-ui:5.21.0
|    \--- org.webjars:webjars-locator-lite:1.0.1 -> 1.1.0
|         \--- org.jspecify:jspecify:1.0.0
+--- net.logstash.logback:logstash-logback-encoder:8.1
|    \--- com.fasterxml.jackson.core:jackson-databind:2.18.3 -> 2.19.0 (*)
+--- io.micrometer:micrometer-registry-prometheus -> 1.15.0
|    +--- io.micrometer:micrometer-core:1.15.0 (*)
|    +--- io.prometheus:prometheus-metrics-core:1.3.6
|    |    +--- io.prometheus:prometheus-metrics-model:1.3.6
|    |    \--- io.prometheus:prometheus-metrics-config:1.3.6
|    +--- io.prometheus:prometheus-metrics-tracer-common:1.3.6
|    \--- io.prometheus:prometheus-metrics-exposition-formats:1.3.6
|         \--- io.prometheus:prometheus-metrics-exposition-textformats:1.3.6
|              +--- io.prometheus:prometheus-metrics-model:1.3.6
|              \--- io.prometheus:prometheus-metrics-config:1.3.6
+--- org.hibernate.orm:hibernate-micrometer -> 6.6.15.Final
|    +--- org.jboss.logging:jboss-logging:3.5.0.Final -> 3.6.1.Final
|    +--- org.hibernate.orm:hibernate-core:6.6.15.Final
|    |    +--- jakarta.persistence:jakarta.persistence-api:3.1.0
|    |    +--- jakarta.transaction:jakarta.transaction-api:2.0.1
|    |    +--- org.jboss.logging:jboss-logging:3.5.0.Final -> 3.6.1.Final
|    |    +--- org.hibernate.common:hibernate-commons-annotations:7.0.3.Final
|    |    +--- io.smallrye:jandex:3.2.0
|    |    +--- com.fasterxml:classmate:1.5.1 -> 1.7.0
|    |    +--- net.bytebuddy:byte-buddy:1.15.11 -> 1.17.5
|    |    +--- jakarta.xml.bind:jakarta.xml.bind-api:4.0.0 -> 4.0.2 (*)
|    |    +--- org.glassfish.jaxb:jaxb-runtime:4.0.2 -> 4.0.5
|    |    |    \--- org.glassfish.jaxb:jaxb-core:4.0.5
|    |    |         +--- jakarta.xml.bind:jakarta.xml.bind-api:4.0.2 (*)
|    |    |         +--- jakarta.activation:jakarta.activation-api:2.1.3
|    |    |         +--- org.eclipse.angus:angus-activation:2.0.2
|    |    |         |    \--- jakarta.activation:jakarta.activation-api:2.1.3
|    |    |         +--- org.glassfish.jaxb:txw2:4.0.5
|    |    |         \--- com.sun.istack:istack-commons-runtime:4.1.2
|    |    +--- jakarta.inject:jakarta.inject-api:2.0.1
|    |    \--- org.antlr:antlr4-runtime:4.13.0
|    \--- io.micrometer:micrometer-core:1.10.4 -> 1.15.0 (*)
+--- no.nav.boot:boot-conditionals:5.1.7
|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.25
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.25
|    |         \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    +--- ch.qos.logback:logback-core:1.5.18
|    +--- org.slf4j:slf4j-api:2.0.17
|    \--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
+--- org.springframework.boot:spring-boot-starter-data-redis -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- io.lettuce:lettuce-core:6.5.5.RELEASE
|    |    +--- io.netty:netty-common:4.1.118.Final -> 4.1.121.Final
|    |    +--- io.netty:netty-handler:4.1.118.Final -> 4.1.121.Final (*)
|    |    +--- io.netty:netty-transport:4.1.118.Final -> 4.1.121.Final (*)
|    |    \--- io.projectreactor:reactor-core:3.6.6 -> 3.7.6 (*)
|    \--- org.springframework.data:spring-data-redis:3.5.0
|         +--- org.springframework.data:spring-data-keyvalue:3.5.0
|         |    +--- org.springframework.data:spring-data-commons:3.5.0
|         |    |    +--- org.springframework:spring-core:6.2.7 (*)
|         |    |    +--- org.springframework:spring-beans:6.2.7 (*)
|         |    |    \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
|         |    +--- org.springframework:spring-context:6.2.7 (*)
|         |    +--- org.springframework:spring-tx:6.2.7 (*)
|         |    \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
|         +--- org.springframework:spring-tx:6.2.7 (*)
|         +--- org.springframework:spring-oxm:6.2.7
|         |    +--- jakarta.xml.bind:jakarta.xml.bind-api:3.0.1 -> 4.0.2 (*)
|         |    +--- org.springframework:spring-beans:6.2.7 (*)
|         |    \--- org.springframework:spring-core:6.2.7 (*)
|         +--- org.springframework:spring-aop:6.2.7 (*)
|         +--- org.springframework:spring-context-support:6.2.7 (*)
|         \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
+--- no.nav.security:token-validation-spring:5.0.25
|    +--- no.nav.security:token-validation-core:5.0.25
|    |    +--- com.nimbusds:oauth2-oidc-sdk:11.23.1
|    |    |    +--- com.github.stephenc.jcip:jcip-annotations:1.0-1
|    |    |    +--- com.nimbusds:content-type:2.3
|    |    |    +--- net.minidev:json-smart:2.5.2
|    |    |    |    \--- net.minidev:accessors-smart:2.5.2
|    |    |    |         \--- org.ow2.asm:asm:9.7.1
|    |    |    +--- com.nimbusds:lang-tag:1.7
|    |    |    \--- com.nimbusds:nimbus-jose-jwt:10.0.2 -> 10.2
|    |    |         \--- com.google.code.gson:gson:2.12.1 -> 2.13.1
|    |    |              \--- com.google.errorprone:error_prone_annotations:2.38.0
|    |    +--- org.slf4j:slf4j-api:2.0.17
|    |    +--- jakarta.validation:jakarta.validation-api:3.0.2
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    |    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
|    +--- no.nav.security:token-validation-filter:5.0.25
|    |    +--- no.nav.security:token-validation-core:5.0.25 (*)
|    |    +--- org.slf4j:slf4j-api:2.0.17
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    |    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
|    +--- org.springframework:spring-context:6.2.6 -> 6.2.7 (*)
|    +--- org.springframework.boot:spring-boot:3.4.5 -> 3.5.0 (*)
|    +--- org.springframework:spring-web:6.2.6 -> 6.2.7 (*)
|    +--- org.springframework:spring-webmvc:6.2.6 -> 6.2.7 (*)
|    +--- com.nimbusds:oauth2-oidc-sdk:11.23.1 (*)
|    +--- org.slf4j:slf4j-api:2.0.17
|    +--- jakarta.validation:jakarta.validation-api:3.0.2
|    +--- org.apache.commons:commons-lang3:3.17.0
|    +--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
+--- no.nav.security:token-client-spring:5.0.25
|    +--- no.nav.security:token-validation-core:5.0.25 (*)
|    +--- no.nav.security:token-client-core:5.0.25
|    |    +--- jakarta.validation:jakarta.validation-api:3.0.2
|    |    +--- org.slf4j:slf4j-api:2.0.17
|    |    +--- com.github.ben-manes.caffeine:caffeine:3.2.0
|    |    |    +--- org.jspecify:jspecify:1.0.0
|    |    |    \--- com.google.errorprone:error_prone_annotations:2.36.0 -> 2.38.0
|    |    +--- com.nimbusds:oauth2-oidc-sdk:11.23.1 (*)
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.18.3 -> 2.19.0 (*)
|    |    +--- com.fasterxml.jackson.module:jackson-module-kotlin:2.18.3 -> 2.19.0 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    |    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    |    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
|    +--- org.springframework:spring-web:6.2.6 -> 6.2.7 (*)
|    +--- com.fasterxml.jackson.core:jackson-annotations:2.18.3 -> 2.19.0 (*)
|    +--- com.fasterxml.jackson.core:jackson-databind:2.18.3 -> 2.19.0 (*)
|    +--- org.springframework.boot:spring-boot:3.4.5 -> 3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-autoconfigure:3.4.5 -> 3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-configuration-processor:3.4.5 -> 3.5.0
|    +--- com.github.ben-manes.caffeine:caffeine:3.2.0 (*)
|    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.9.25 (*)
|    \--- com.nimbusds:nimbus-jose-jwt:10.2 (*)
+--- org.springframework.boot:spring-boot-starter-data-jpa -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-starter-jdbc:3.5.0
|    |    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    |    +--- com.zaxxer:HikariCP:6.3.0
|    |    |    \--- org.slf4j:slf4j-api:1.7.36 -> 2.0.17
|    |    \--- org.springframework:spring-jdbc:6.2.7
|    |         +--- org.springframework:spring-beans:6.2.7 (*)
|    |         +--- org.springframework:spring-core:6.2.7 (*)
|    |         \--- org.springframework:spring-tx:6.2.7 (*)
|    +--- org.hibernate.orm:hibernate-core:6.6.15.Final (*)
|    +--- org.springframework.data:spring-data-jpa:3.5.0
|    |    +--- org.springframework.data:spring-data-commons:3.5.0 (*)
|    |    +--- org.springframework:spring-orm:6.2.7
|    |    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    |    +--- org.springframework:spring-jdbc:6.2.7 (*)
|    |    |    \--- org.springframework:spring-tx:6.2.7 (*)
|    |    +--- org.springframework:spring-context:6.2.7 (*)
|    |    +--- org.springframework:spring-aop:6.2.7 (*)
|    |    +--- org.springframework:spring-tx:6.2.7 (*)
|    |    +--- org.springframework:spring-beans:6.2.7 (*)
|    |    +--- org.springframework:spring-core:6.2.7 (*)
|    |    +--- org.antlr:antlr4-runtime:4.13.0
|    |    +--- jakarta.annotation:jakarta.annotation-api:2.0.0 -> 2.1.1
|    |    \--- org.slf4j:slf4j-api:2.0.2 -> 2.0.17
|    \--- org.springframework:spring-aspects:6.2.7 (*)
+--- org.flywaydb:flyway-core -> 11.7.2
|    +--- com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.15.2 -> 2.19.0
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.19.0 (*)
|    |    +--- com.fasterxml.jackson.core:jackson-core:2.19.0 (*)
|    |    \--- com.fasterxml.jackson:jackson-bom:2.19.0 (*)
|    \--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2 -> 2.19.0 (*)
+--- org.postgresql:postgresql -> 42.7.5
|    \--- org.checkerframework:checker-qual:3.48.3
+--- org.flywaydb:flyway-database-postgresql -> 11.7.2
|    \--- org.flywaydb:flyway-core:11.7.2 (*)
+--- org.springframework.boot:spring-boot-testcontainers -> 3.5.0
|    +--- org.springframework.boot:spring-boot-autoconfigure:3.5.0 (*)
|    \--- org.testcontainers:testcontainers:1.21.0
|         +--- junit:junit:4.13.2
|         |    \--- org.hamcrest:hamcrest-core:1.3 -> 3.0
|         |         \--- org.hamcrest:hamcrest:3.0
|         +--- org.slf4j:slf4j-api:1.7.36 -> 2.0.17
|         +--- org.apache.commons:commons-compress:1.24.0
|         +--- org.rnorth.duct-tape:duct-tape:1.0.8
|         |    \--- org.jetbrains:annotations:17.0.0 -> 23.0.0
|         +--- com.github.docker-java:docker-java-api:3.4.2
|         |    +--- com.fasterxml.jackson.core:jackson-annotations:2.10.3 -> 2.19.0 (*)
|         |    \--- org.slf4j:slf4j-api:1.7.30 -> 2.0.17
|         \--- com.github.docker-java:docker-java-transport-zerodep:3.4.2
|              +--- com.github.docker-java:docker-java-transport:3.4.2
|              +--- org.slf4j:slf4j-api:1.7.25 -> 2.0.17
|              \--- net.java.dev.jna:jna:5.13.0
+--- org.testcontainers:postgresql -> 1.21.0
|    \--- org.testcontainers:jdbc:1.21.0
|         \--- org.testcontainers:database-commons:1.21.0
|              \--- org.testcontainers:testcontainers:1.21.0 (*)
+--- org.testcontainers:junit-jupiter -> 1.21.0
|    \--- org.testcontainers:testcontainers:1.21.0 (*)
+--- com.github.stefanbirkner:system-lambda:1.2.1
+--- org.junit.jupiter:junit-jupiter -> 5.12.2
|    +--- org.junit:junit-bom:5.12.2
|    |    +--- org.junit.jupiter:junit-jupiter:5.12.2 (c)
|    |    +--- org.junit.jupiter:junit-jupiter-api:5.12.2 (c)
|    |    +--- org.junit.jupiter:junit-jupiter-engine:5.12.2 (c)
|    |    +--- org.junit.jupiter:junit-jupiter-params:5.12.2 (c)
|    |    +--- org.junit.platform:junit-platform-launcher:1.12.2 (c)
|    |    +--- org.junit.platform:junit-platform-commons:1.12.2 (c)
|    |    \--- org.junit.platform:junit-platform-engine:1.12.2 (c)
|    +--- org.junit.jupiter:junit-jupiter-api:5.12.2
|    |    +--- org.junit:junit-bom:5.12.2 (*)
|    |    +--- org.opentest4j:opentest4j:1.3.0
|    |    \--- org.junit.platform:junit-platform-commons:1.12.2
|    |         \--- org.junit:junit-bom:5.12.2 (*)
|    +--- org.junit.jupiter:junit-jupiter-params:5.12.2
|    |    +--- org.junit:junit-bom:5.12.2 (*)
|    |    \--- org.junit.jupiter:junit-jupiter-api:5.12.2 (*)
|    \--- org.junit.jupiter:junit-jupiter-engine:5.12.2
|         +--- org.junit:junit-bom:5.12.2 (*)
|         +--- org.junit.platform:junit-platform-engine:1.12.2
|         |    +--- org.junit:junit-bom:5.12.2 (*)
|         |    +--- org.opentest4j:opentest4j:1.3.0
|         |    \--- org.junit.platform:junit-platform-commons:1.12.2 (*)
|         \--- org.junit.jupiter:junit-jupiter-api:5.12.2 (*)
+--- org.springframework.boot:spring-boot-starter-test -> 3.5.0
|    +--- org.springframework.boot:spring-boot-starter:3.5.0 (*)
|    +--- org.springframework.boot:spring-boot-test:3.5.0
|    |    +--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    \--- org.springframework:spring-test:6.2.7
|    |         \--- org.springframework:spring-core:6.2.7 (*)
|    +--- org.springframework.boot:spring-boot-test-autoconfigure:3.5.0
|    |    +--- org.springframework.boot:spring-boot:3.5.0 (*)
|    |    +--- org.springframework.boot:spring-boot-test:3.5.0 (*)
|    |    \--- org.springframework.boot:spring-boot-autoconfigure:3.5.0 (*)
|    +--- com.jayway.jsonpath:json-path:2.9.0
|    |    +--- net.minidev:json-smart:2.5.0 -> 2.5.2 (*)
|    |    \--- org.slf4j:slf4j-api:2.0.11 -> 2.0.17
|    +--- jakarta.xml.bind:jakarta.xml.bind-api:4.0.2 (*)
|    +--- net.minidev:json-smart:2.5.2 (*)
|    +--- org.assertj:assertj-core:3.27.3
|    |    \--- net.bytebuddy:byte-buddy:1.15.11 -> 1.17.5
|    +--- org.awaitility:awaitility:4.3.0
|    |    \--- org.hamcrest:hamcrest:2.1 -> 3.0
|    +--- org.hamcrest:hamcrest:3.0
|    +--- org.junit.jupiter:junit-jupiter:5.12.2 (*)
|    +--- org.mockito:mockito-core:5.17.0
|    |    +--- net.bytebuddy:byte-buddy:1.15.11 -> 1.17.5
|    |    +--- net.bytebuddy:byte-buddy-agent:1.15.11 -> 1.17.5
|    |    \--- org.objenesis:objenesis:3.3
|    +--- org.mockito:mockito-junit-jupiter:5.17.0
|    |    +--- org.mockito:mockito-core:5.17.0 (*)
|    |    \--- org.junit.jupiter:junit-jupiter-api:5.11.4 -> 5.12.2 (*)
|    +--- org.skyscreamer:jsonassert:1.5.3
|    |    \--- com.vaadin.external.google:android-json:0.0.20131108.vaadin1
|    +--- org.springframework:spring-core:6.2.7 (*)
|    +--- org.springframework:spring-test:6.2.7 (*)
|    +--- org.xmlunit:xmlunit-core:2.10.1
|    \--- org.junit.platform:junit-platform-launcher -> 1.12.2
|         +--- org.junit:junit-bom:5.12.2 (*)
|         \--- org.junit.platform:junit-platform-engine:1.12.2 (*)
+--- io.mockk:mockk:1.14.2
|    \--- io.mockk:mockk-jvm:1.14.2
|         +--- io.mockk:mockk-dsl:1.14.2
|         |    \--- io.mockk:mockk-dsl-jvm:1.14.2
|         |         +--- org.jetbrains.kotlin:kotlin-stdlib:2.0.0 -> 1.9.25 (*)
|         |         +--- org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.6.4 -> 1.8.1 (*)
|         |         +--- org.jetbrains.kotlinx:kotlinx-coroutines-core -> 1.9.0 (*)
|         |         +--- org.jetbrains.kotlin:kotlin-reflect:2.0.0 -> 1.9.25 (*)
|         |         \--- io.mockk:mockk-core:1.14.2
|         |              \--- io.mockk:mockk-core-jvm:1.14.2
|         |                   +--- org.jetbrains.kotlin:kotlin-stdlib:2.0.0 -> 1.9.25 (*)
|         |                   \--- org.jetbrains.kotlin:kotlin-reflect:2.0.0 -> 1.9.25 (*)
|         +--- io.mockk:mockk-agent:1.14.2
|         |    \--- io.mockk:mockk-agent-jvm:1.14.2
|         |         +--- org.objenesis:objenesis:3.3
|         |         +--- net.bytebuddy:byte-buddy:1.14.17 -> 1.17.5
|         |         +--- net.bytebuddy:byte-buddy-agent:1.14.17 -> 1.17.5
|         |         +--- io.mockk:mockk-agent-api:1.14.2
|         |         |    \--- io.mockk:mockk-agent-api-jvm:1.14.2
|         |         |         \--- org.jetbrains.kotlin:kotlin-stdlib:2.0.0 -> 1.9.25 (*)
|         |         +--- org.jetbrains.kotlin:kotlin-stdlib:2.0.0 -> 1.9.25 (*)
|         |         +--- org.jetbrains.kotlin:kotlin-reflect:2.0.0 -> 1.9.25 (*)
|         |         \--- io.mockk:mockk-core:1.14.2 (*)
|         +--- io.mockk:mockk-agent-api:1.14.2 (*)
|         +--- io.mockk:mockk-core:1.14.2 (*)
|         +--- org.jetbrains.kotlin:kotlin-stdlib:2.0.0 -> 1.9.25 (*)
|         +--- junit:junit:4.13.2 (*)
|         +--- org.junit.jupiter:junit-jupiter:5.8.2 -> 5.12.2 (*)
|         +--- org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.6.4 -> 1.8.1 (*)
|         +--- org.jetbrains.kotlinx:kotlinx-coroutines-core -> 1.9.0 (*)
|         \--- org.jetbrains.kotlin:kotlin-reflect:2.0.0 -> 1.9.25 (*)
+--- com.ninja-squad:springmockk:4.0.2
|    +--- org.jetbrains.kotlin:kotlin-reflect:1.7.21 -> 1.9.25 (*)
|    +--- org.springframework.boot:spring-boot-test -> 3.5.0 (*)
|    +--- org.springframework:spring-test -> 6.2.7 (*)
|    +--- org.springframework:spring-context -> 6.2.7 (*)
|    +--- io.mockk:mockk-jvm:1.13.3 -> 1.14.2 (*)
|    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.21 -> 1.9.25 (*)
+--- org.jetbrains.kotlin:kotlin-test:1.9.25
|    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.25 (*)
|    \--- org.jetbrains.kotlin:kotlin-test-junit5:1.9.25
|         +--- org.junit.jupiter:junit-jupiter-engine:5.6.3 -> 5.12.2 (*)
|         +--- org.jetbrains.kotlin:kotlin-test:1.9.25 (*)
|         \--- org.junit.jupiter:junit-jupiter-api:5.6.3 -> 5.12.2 (*)
\--- org.jetbrains.kotlin:kotlin-test:1.9.25 (*)

testRuntimeOnly - Runtime only dependencies for null/test. (n)
No dependencies

(c) - A dependency constraint, not a dependency. The dependency affected by the constraint occurs elsewhere in the tree.
(*) - Indicates repeated occurrences of a transitive dependency subtree. Gradle expands transitive dependency subtrees only once per project; repeat occurrences only display the root of the subtree, followed by this annotation.

(n) - A dependency or dependency configuration that cannot be resolved.

A web-based, searchable dependency report is available by adding the --scan option.

BUILD SUCCESSFUL in 478ms
1 actionable task: 1 executed
