plugins {
    id("distributor.parent-conventions")
}

version = "3.0.0-rc.3" + if (indraGit.headTag() == null) "-SNAPSHOT" else ""
group = "fr.xpdustry"
description = "The Mindustry plugin of ur dreams..."
