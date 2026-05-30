# AGENTS.md

Guidance for AI agents working in this repository.

## Cursor Cloud specific instructions

### Product

**MOKKA Budget** is a local-first Java 21 + JavaFX desktop household budget app (German UI). There is no separate backend, Docker stack, or `package.json`. Persistence is embedded SQLite at `~/.mokkabudget/sqlite.db` (Flyway runs on first GUI start).

### Prerequisites

- **JDK 21** (Temurin recommended; CI uses `actions/setup-java` with Java 21)
- **Apache Maven** 3.x (`mvn` on `PATH`). The Cloud VM image may ship Java without Maven; install with `sudo apt-get install -y maven` if `mvn` is missing.

### Maven modules (build order)

Root aggregator `pom.xml` → `bb.contract`, `bb.domain`, `bb.business-logic`, `bb.ui-infrastructure`, `bb.ui`, `bb.application`.

### Common commands (from repo root `/workspace`)

| Task | Command |
|------|---------|
| Build + unit tests (matches CI) | `mvn -B package` |
| Tests only | `mvn -B test` |
| Full verify + JaCoCo | `mvn -B verify` then `mvn jacoco:report` |
| Install SNAPSHOTs to `~/.m2` (needed before GUI run) | `mvn -B install -DskipTests` |
| Run desktop app | `mvn -B javafx:run -pl bb.application` |

**Do not** run `mvn javafx:run` on the root POM alone — the javafx plugin fails with “Output directory is empty”. Always use `-pl bb.application` after `mvn install` (or `package` + `install`).

IntelliJ uses `de.timkodiert.mokka.Launch` with `--mode=DEV`. VS Code: see `.vscode/launch.json` and `mvn clean javafx:run@debug` (JDWP port 8001).

### Lint / format

No Maven lint goal (no Checkstyle/SpotBugs in POM). Java formatting is IDE-only via `.vscode/java-formatter.xml`.

### GUI / display (Cloud VM)

Manual testing requires a graphical session (`DISPLAY` set, e.g. `:1`). If no display is available, use `xvfb-run` (installed on typical Cloud VMs) — e.g. `xvfb-run mvn -B javafx:run -pl bb.application`.

On first launch, a **Flyway migration** dialog may appear; confirm it before the main **Monatsübersicht** loads.

### Data directory

- DB: `~/.mokkabudget/sqlite.db`
- Log: `~/.mokkabudget/mokka-application.log`
- Config: `~/.mokkabudget/application.properties`

### Optional (not required for dev)

- Native installers: `./build_app.sh` (needs `jpackage`, OS-specific tools)
- User manual PDF: `./build_user_manual.sh` or `mvn -P build-manual`
- SonarCloud: `.github/workflows/sonarcloud.yml` (secrets on `develop`)

### Services

| Service | Required? |
|---------|-----------|
| MOKKA Budget JVM (JavaFX) | Yes, for GUI E2E |
| Embedded SQLite | Yes (in-process; no separate DB server) |
| X11/Wayland or Xvfb | Yes, for GUI |

No pre-commit hooks or `docker-compose` in this repo.
