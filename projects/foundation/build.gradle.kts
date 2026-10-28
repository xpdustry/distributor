plugins {
    alias(conventions.plugins.xpdustry.mindustry)
    alias(conventions.plugins.xpdustry.publishing)
}

description = "The foundation of every xpdustry plugin."

xpdustry {
    plugin {
        mainClass = "com.xpdustry.foundation.FoundationPlugin"
    }
}
