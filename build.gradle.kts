plugins {
    id("java")
}

group = "org.ops"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation ("org.apache.httpcomponents.client5:httpclient5:5.3")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("commons-net:commons-net:3.9.0")
    implementation ("com.jcraft:jsch:0.1.55")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}