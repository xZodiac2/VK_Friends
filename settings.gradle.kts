import java.net.URI

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = URI("https://artifactory-external.vkpartner.ru/artifactory/vkid-sdk-andorid/")
        }
        maven {
            url = URI("https://jitpack.io")
        }
    }
}

rootProject.name = "VK Friends"

include(":app")
include(":core")
include(":data")
include(":theme")
include(":domain:profileViewDomain")
include(":feature:auth")
include(":feature:friendsView")
include(":feature:search")
include(":feature:profileView")
