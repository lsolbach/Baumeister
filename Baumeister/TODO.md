TODO's
======

Use clojure.edn for reading config files

Guidelines
----------
 * Make common tasks easy
 * Make uncommon tasks possible
 * Make everything as simple as possible but not simpler

Build
-----
 * Multi module builds
   * topological sorting of the modules based on the module dependencies
   * call baumeister on multiple modules
     * handle multiple configurations
 * parent module configurations?


Think About
===========

Repositories
------------
Differences between baumeister and maven repositories
 * structural repository layout
 * handling of local/remote repositories
 * copying artifacts vs. referencing artifacts from the repository
 * dev/release/thirdparty repositories vs. snapshot/release
 * artifact deployment to repositories
   * baumeister repositories (remote dev/release)
   * maven repositories (maven install/release)

### Maven ###
Maven 3 way repository structure
 * local repository cache
   * visible only for the developer
 * snapshot repository
   * local and/or remote
 * release repository
   * local and/or remote

Maven build lifecycle phases
 * package
   * packages the artifact (and copies it to the local repository cache?)
 * install
   * installs the artifact into the snapshot repository
 * release 
   * releases the artifact into the release repository


Transitive Dependency Management
--------------------------------
What is the difference between an artifact and a dependency. What are the properties of a dependency and an artifact.

Artifact
A resource generated or used by the build process,
Properties: ProjectName, ModuleName, ArtifactName, ArtifactType (extension?), ArtifactVersion

Dependency
A dependency is a reference to an existing artifact (from outside the current module) or to an abstract artifact pattern
(e.g with open version, or just project and module configured).
A dependency can be scoped for the usage in the current module.
Properties: ArtifactUsage (scope, target, exclusion), ArtifactReference(Pattern)

Plugins
-------
 * plugin storage
 * plugin dependencies
 * user plugins
 * functional inter-plugin dependencies
 * plugin orthogonalization!
 * version conflicts for plugin and plugin dependencies
 * internal/default plugins
   * global initialization
   * dependency management

Orthogonality
-------------
How to cut through the different dimensions?
[workflows, phases, steps, plugins, module types]
e.g.
 * mdsd initializing, cleaning, dependencies
 * aspectj initializing, cleaning, dependencies

Build listeners/notifiers
-------------------------
Listener/Notifier plugin to register functions on build phases?


Command line parameters
-----------------------
How to start workflows, workflow phases and plugin functions?

* <workflow-name>-workflow
* <phase-name>
* <plugin-name>:<function-name>

