# AGENTS.md

## Cursor Cloud specific instructions

### What this is
A small standalone **Java** library modeling in-memory **inventory & order management** (package `collections`). There is **no build tool** (no Maven/Gradle), **no services/database**, and **no `main` method** committed in the repo. The only dependency is a JDK (JDK 21 is preinstalled).

### Non-obvious gotchas
- **Package vs. directory mismatch**: every `*.java` file declares `package collections;` but the files physically live in the repo root. You **cannot** compile them in place with `javac collections.Item` style references. Copy the sources into a matching `collections/` directory first, then compile. Example:
  ```bash
  mkdir -p /tmp/inv-build/src/collections /tmp/inv-build/out
  cp *.java /tmp/inv-build/src/collections/
  javac -d /tmp/inv-build/out /tmp/inv-build/src/collections/*.java
  ```
- **No entry point**: there is nothing to "run" out of the box. To exercise behavior, add a class in package `collections` with a `main` method (constructors like `Inventory()` and `OrderProcessor()` and several fields are package-private, so the runner must live in `collections`), then `java -cp out collections.<YourClass>`. Keep such demo/runner classes out of the repo unless they are intentionally being added.

### Lint / test / build / run
- **Lint**: none configured.
- **Tests**: none committed.
- **Build**: `javac` as shown above (no build system).
- **Run**: no committed entry point; build a runner in package `collections` as described.
