import java.time.Duration
import nebula.plugin.release.git.opinion.Strategies

plugins {
    val kotlinVersion = "1.5.21"
    id("idea")
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion apply false
    id("io.github.gradle-nexus.publish-plugin")
    id("nebula.release")
}

release {
    defaultVersionStrategy = Strategies.getSNAPSHOT()
}

nebulaRelease {
    addReleaseBranchPattern("""v\d+\.\d+\.x""")
}

nexusPublishing {
    packageGroup.set("cloud.djet")

    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            //username.set(System.getenv("SONATYPE_USER"))
            //password.set(System.getenv("SONATYPE_KEY"))
        }
    }

    connectTimeout.set(Duration.ofMinutes(5))
    clientTimeout.set(Duration.ofMinutes(5))

    transitionCheckOptions {
        // We have many artifacts so Maven Central takes a long time on its compliance checks. This sets
        // the timeout for waiting for the repository to close to a comfortable 50 minutes.
        maxRetries.set(300)
        delayBetween.set(Duration.ofSeconds(10))
    }
}

description = "Libraries for the DJet template with SpringBoot / Kotlin "