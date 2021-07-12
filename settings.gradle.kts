rootProject.name = "botlin-scripts"

include(":testing")
include(":flipper")
include(":crabs")
include(":misc")
include(":pvp")


for (project in rootProject.children) {
    project.apply {
        projectDir = file(name)
        buildFileName = "${name.toLowerCase()}.gradle.kts"

        require(projectDir.isDirectory) { "Project '${project.path} must have a $projectDir directory" }
        require(buildFile.isFile) { "Project '${project.path} must have a $buildFile build script" }
    }
}
