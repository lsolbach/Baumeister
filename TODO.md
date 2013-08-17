TODO's
======

Guidelines
----------

* Make everything as simple as possible but not simpler
* Make common tasks easy
* Make uncommon tasks possible

* Multi project builds

MDSD
----
* use src/model for input models?
* use xmi directly by default, add config parameters for extraction from zargo
* extract model xmi to build/model instead of model?


Think About
===========

Transitive Dependency Management
--------------------------------

Artifact
A resource generated or used by the build process,
Properties: ProjectName, ModuleName, ArtifactName, ArtifactType (extension?), ArtifactVersion

Dependency
A dependency is a reference to an existing artifact (from outside the current module) or to an abstract artifact pattern
(e.g with open version, or just project and module configured).
A dependency can be scoped for the usage in the current module.
Properties: ArtifactUsage (scope, target, exclusion), ArtifactReference(Pattern)

What is the difference between an artifact and a dependency. What are the properties of a dependency and an artifact.
dependency: target/scope/usage

Dependency Plugin
Feed the deps plugin with a dependency tree, but build the dependency tree outside of the plugin
Move the dependency management into an extra module?


Plugins
-------
Plugin storage, user plugins, plugin dependencies, multi plugin dependencies, version conflicts for plugin and plugin dependencies
Plugin orthogonalization!

#### Orthogonality
How to cut through the different dimensions?
[workflows, phases, steps, plugins, module types]
e.g.
mdsd initializing, cleaning, dependencies
aspectj initializing, cleaning, dependencies


#### Build listeners/notifiers?
Listener/Notifier plugin to register functions on build phases?

Global initialization (apart from plugins)?
build-dir
