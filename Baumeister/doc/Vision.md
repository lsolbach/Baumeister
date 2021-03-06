Baumeister Build System Vision
==============================

Situation
---------
Build systems are used to build software projects. A software build is
the manufacturing process for the production of deployable software
products.

There are many existing build systems available, e.g. make, ant, maven,
gradle, leiningen and others.

Some of them are targeted for specific
languages, some of them support many languages.

Some implement a build workflow while with others the build workflow
has to be implemented by the user.

Some favour convention over configuration while others don't have any
conventions built in at all.

With the convention based approach you have to model your projects
after the conventions of the build system.

With the configuration based approach you can customize the build
system to match your projects.

Some build systems provide support for multi module projects while
others target single module projects only.

Some build systems provide dependency management, others do not.

Simple Dependency management is a crucial feature even for smaller
projects. Bigger projects or multi module projects need Transitive
Dependency Management.

None of the existing projects is aware of common software
architectures.


Competition
-----------
### Make ###
Make is the grandfather of all build systems.
o implemented in C 
- external build language (white space relevant, cryptical)
- no dependency management
- no architecture awareness
- no tool support for jvm projects

### Ant ###
+ flexible workflows
+ many existing tasks
o implemented in Java
- xml configuration
- inflexible control structures
- no workflow framework
- inflexible support for conditional build steps
- no integrated dependency management (but available with ant ivy)
- no architecture awareness

### Maven ###
+ site generation, reporting
+ project generation
+ transitive dependency management
+ tool support
o implemented in Java
o convention over configuration paradigma, simple config if conventions are followed
o extensible build process via plugins
- inflexible project structure
- inflexible artifact concept, only one artifact per project/module
- no architecture awareness

### Gradle ###
+ flexible
+ reuse of existing ant tasks and targets 
o implemented in Groovy
o some integration of maven
- no architecture awareness
???

### Leiningen(2) ###
+ simple project configuration
+ maven repository support
o implemented in Clojure
- mainly clojure focused
- no architecture awareness

### Others ###
Most other build systems (like rake for ruby) primarily target specific languages.


Opportunity
-----------
A build system combining the simplicity of the convention based approach with the power of a
configuration based approach and the extensibility provided by Clojure. 

 * simplicity of the convention based approach
 * power of the configuration based approach
 * support for an architecture and model centric development process
 * flexible module structure
 * extensible plugin system
 * extensible build workflows
 * flexible control structures
 * high level programming of build functionality
 * transitive dependency management
 * maven style repository support
 * flexible repositories, multiple artifacts per module
 * multi language builds
 * reuse of existing ant tasks


Stakeholders
------------
 * Software Developers
 * Software Architects
 * Configuration Managers
 * Project Managers


Users
-----
 * Configuration Managers
 * Software Developers
 * Software Architects


Guidelines
----------
 * Make common tasks easy
 * Make uncommon tasks possible
 * Make everything as simple as possible but not simpler


Proposed Features
-----------------
 * multi language builds (Java, AspectJ, Clojure, ...)
 * architecture awareness
 * suport for model driven software development
 * dependency management (compatibility with existing repositories)
 * extensible plugin architecture
 * configurable modules
 * flexible artifact generation (multiple artifacts per module)
 * customizable per module


Plugin architecture
-------------------
 * plugins for the orthogonalization of build functionality
 * shipped plugins for core build features
 * extensible via clojure plugins
 * usage of ant tasks in plugins where feasible


Language support via plugins
----------------------------
 * Shipped compiler plugins 
   * Java
   * AspectJ
   * Clojure
 * additional languages via plugins
 * support for languages for which an ant task is available should be straight forward


Module configuration
--------------------
Minimal configuration in a concise way
 * project name
 * module name
 * module type
 * version
 * license
 * vendor
 * plugins
 * dependencies
 * configuration vars


Module types
------------
 * architecture
 * library
 * framework
 * integration
 * domain
 * application
 * presentation
 * console frontend?
 * app frontend?
 * web frontend
 * web service
 * data
 * extensible with new module types


Build workflows
---------------
Predefined workflows with phases, steps and hooks for extension and module specific customization
 * workflows
 * phases
 * steps
 * tasks

 * extensible workflows via configuration
 * embedding of workflows as subworkflows in other workflows

### Build Steps ###


### Build Tasks ###
 * depending on the module type

### Workflow Examples ###

Module creation workflow

Create a new module from scratch
[create-module]

Project architecture workflow
Create new modules based on an architectural model of the project.
[clean, init, dependencies, generate-architecture]

Initialize workflow
[clean, init, dependencies]

Build workflow
[clean, init, dependencies, generate, compile, unittest, package, integrationtest, acceptancetest, doc, distribute]?

Deploy workfloy
[build workflow, deploy]

Release workflow
[build workflow, release]

Project build workflow?


Transitive dependency management
--------------------------------
Dependencies form a directed graph (the graph should be acyclic too,
but that could not be guaranteed for 3rd party dependencies)

Transformation to a dependency tree via breadth search
Tree nodes contain Artifact(s?), root distance, excludes for subtree ...
