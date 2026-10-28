# Xpdustry

Everything you need for the modern Mindustry server, every project lives in its `projects/*` directory.

- `projects/foundation`: the API covering everything a good Mindustry plugin needs.
- `projects/nucleus`: the main plugin of the xpdustry servers, and the reference on how to use foundation.
- `projects/sql-postgresql`: the plugin providing the PostgreSQL JDBC driver to the other plugins.
- `projects/sql-postgresql-embedded`: the plugin providing an embedded PostgreSQL server to the other plugins.
- `projects/build-logic`: the Gradle conventions of the projects.

## Developing

Requires Java 25. The usual tasks are `./gradlew build`, `./gradlew test` and `./gradlew spotlessApply`.

- `./gradlew :<project>:runMindustryServer` runs a local server with the plugin and the plugins it depends on,
  in `projects/<project>/build/mindustry-server`.
- `./gradlew :<project>:runMindustryClient` runs a local client, with its data in
  `projects/<project>/build/mindustry-client`.

## Releasing

The projects are released together, by running the `Release` workflow, which builds them, publishes the libraries to
Maven and creates the GitHub release with the plugin jars.

- The version is `YEAR.MONTH.BUILD`, derived from the nearest release tag and the date of the commit, so a commit
  always builds the same version. The other builds are snapshots of the next release.
- The release notes are generated from the titles of the merged pull requests, which follow the
  [conventional commits](https://www.conventionalcommits.org), and grouped by the labels they get from them.

Everything is under the [MIT license](LICENSE.md).
