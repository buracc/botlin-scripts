tasks {
    jar {
        manifest {
            attributes(mutableMapOf("Main-Class" to "flipper.Flipper"))
        }
    }
}