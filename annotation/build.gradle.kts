fun properties(key: String) = project.findProperty(key).toString()

plugins {
    id("java")
    id("maven-publish")
}

repositories {
    mavenCentral()
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/apflieger/dsl-in-color")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = "com.meetinclass.dslincolor"
            artifactId = "annotations"
            version = properties("pluginVersion")

            from(components["java"])
        }
    }
}