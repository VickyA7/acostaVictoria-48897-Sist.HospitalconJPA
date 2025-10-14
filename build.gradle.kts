plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // JPA API
    //Necesario para usar EntityManager @Entity
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")

// Implementación de JPA (Hibernate), en tiempo de compilacion y ejecucion
    implementation("org.hibernate.orm:hibernate-core:6.4.4.Final")

    //testImplementation("com.h2database:h2:2.4.240") No lo utilizo
    //Necesito h2 como base de datos en tiempo de ejecucion
    runtimeOnly("com.h2database:h2:2.2.224")

    //Lombok
    compileOnly ("org.projectlombok:lombok:1.18.32")
    annotationProcessor ("org.projectlombok:lombok:1.18.32")


}

tasks.test {
    useJUnitPlatform()
}