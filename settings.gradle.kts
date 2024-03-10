import java.net.URI

include(":feature:profileView")


include(":core")


include(":feature:auth")


include(":feature:friendsView")



include(":theme")


include(":data")



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
        maven { url = URI("https://artifactory-external.vkpartner.ru/artifactory/vkid-sdk-andorid/") }
    }
}

rootProject.name = "VK Friends"
include(":app")
