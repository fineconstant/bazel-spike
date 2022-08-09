# Bazel Spike project

## Setup

1. `brew install bazelisk coursier`  
   `bazelisk` will manage Bazel versions for you  
   `coursier` is required to set up Bazel BSP
2. `cs launch org.jetbrains.bsp:bazel-bsp:2.2.0 -M org.jetbrains.bsp.bazel.install.Install` - enable Bazel BSP
3. Import existing project to IntelliJ as a BSP project

## Useful commands

- `cs launch org.jetbrains.bsp:bazel-bsp:2.2.0 -M org.jetbrains.bsp.bazel.install.Install` - enable Bazel BSP
- `bazel clean` - clean all
- `bazel build //...` / `bazel build //:all` - build all targets
- `bazel build //project-application:all` - build all targets within `project-application` build
- `bazel build //project-application:app` - build `app` target within `project-application`
  build
- `bazel run //project-application:app` - run binary target `app` within `project-application`
  build
- `bazel build //project-application:app_deploy.jar` - build fat-jar with all dependencies so that it can
  be run with `java -jar ...`
- `bazel run @maven//:pin` - initialize pinning artifacts and their SHA-256 checksums into `maven_install.json`
- `bazel run @unpinned_maven//:pin` - update maven_install.json
- `bazel run @maven//:outdated` - show outdated dependencies
- `bazel build --remote_cache=[http/grpc]://your.host:port` - build using remote cache
- `bazel query --noimplicit_deps --notool_deps 'deps(//...)' --output graph | dot -Tpng > graph.png` - build dependency
  graph

## Transitive dependencies

`rules_jvm_external` automatically downloads transitive dependencies e.g. `akka-http-core` of `akka-http`
To use a class from `akka-http-core` it's not sufficient to just depend on `@maven//:akka_http` even though Akka Http
depends on Akka HTTP Core.
The compiled classes are not exported transitively, so you should also depend on `@maven//:akka_http_core`.

## Remote cache using bazel-remote

[bazel-remote](https://github.com/buchgr/bazel-remote) used as a remote cache implementation.

Bazel Remote is an HTTP/1.1 and gRPC server that is intended to be used as a remote build cache for REAPI clients like
Bazel or as a component of a remote execution service.

The cache contents are stored in a directory on disk with a maximum cache size, and bazel-remote will automatically
enforce this limit as needed, by deleting the least recently used files.
S3, GCS and experimental **Azure blob storage** proxy backends are also supported.

### Setup

1. `docker pull buchgr/bazel-remote-cache`
2. `docker run -u 1000:1000 -v /path/to/cache/dir:/data -p 9090:8080 -p 9092:9092 --name bazel-remote-cache -d buchgr/bazel-remote-cache`

## Definitions

### WORKSPACE file

Defines a directory to be a workspace.
The file can be empty, although it usually contains external repository declarations to fetch additional dependencies
from the network or local filesystem.
Labels that start with `//`are relative to the workspace directory.

### BUILD File

A BUILD file is the main configuration file that tells Bazel what software outputs to build, what their dependencies
are, and how to build them.
Bazel takes a BUILD file as input and uses the file to create a graph of dependencies and to derive the actions that
must be completed to build intermediate and final software outputs.

### Rule

A function implementation that registers a series of actions to be performed on input artifacts to produce a set of
output artifacts.

### Target

A buildable unit.
Can be a rule target, file target, or a package group.
Rule targets are instantiated from rule declarations in BUILD files.
Depending on the rule implementation, rule targets can also be testable or runnable.
Every file used in BUILD files is a file target.
Targets can consist of multiple actions.

### Action

A command to run during the build, for example, a call to a compiler that takes artifacts as inputs and produces other
artifacts as outputs.

## Demo

### Cache

1. Setup `bazel-remote`
2. Clean local build  
   `bazel clean; bazel build //...`  
   Elapsed time: 20.515s
3. Populate remote cache  
   `bazel build --remote_cache=http://localhost:9090 //...`
4. Clean local build with remote cache  
   `bazel clean; bazel build --remote_cache=http://localhost:9090 //...`  
   Elapsed time: 7.646s,
5. Change `application.Main`
6. Clean local build with remote cache after file change (should recompile file and fetch the rest from cache)  
   `bazel clean; bazel build --remote_cache=http://localhost:9090 //...`  
   Elapsed time: 10.012s

### Tests for changed sources

1. Run tests twice - results should be cached  
   `bazel test //...`
2. Change `application.domain.greeter.Greeter`
3. Run tests again  
   Notice that only tests from `greeter` package were run.
4. Generate dependency graph  
   `bazel query --noimplicit_deps --notool_deps 'deps(//...)' --output graph | dot -Tpng > graph.png`  
   Notice that `//project-applicaiton:app` depends on `greeter` and `sequence` separately.  
   Additionally, `greeter` consists of two sources.

## To do:

- Scalafmt, possibly using
  [higherkindness/rules_scala](https://github.com/higherkindness/rules_scala/blob/master/docs/scalafmt.md)
    - How to integrate it with `bazelbuild/rules_scala`?
- Docker image - manual build using bash script - put compiled package into an image
