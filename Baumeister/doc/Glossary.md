Glossary
========

This glossary defines some terms that are used in the description of
the Baumeister build system. The definitions describe the use of these
terms in the context of the Baumeister system. 

Acceptance Tests
----------------
Acceptance Tests test the whole software system from a user
perspective. The focus of acceptance tests is to verify the
conformity of the software to the specified requirements.

Architecture
------------


Artifact
--------
Depending on the context the term Artifact is used with different meanings.
 * Artifact (General)
   * Source or input Artifacts are intrinsic input for a transformation of
     the build process. They are contained in the module that is build.
   * Build or target Artifacts are the result of a transfomation
     (e.g. generation, compilation) of the build process.
 * Artifact (Dependency Management)
   * An Artifact in the context of Depenency Management is a resource generated
     or used by the build process and is stored or retrieved from an Artifact
     Repository.
   * The common qualifying properties (coordinates) of an Artifact in the
     context of the Baumeister Dependency Management are
     * project
     * module
     * version
     * name
     * type

Artifact Repository
-------------------
An Artifact Repository is a service which stores artifacts.
Artifacts can be stored in the repository and retrieved from it.

Build
-----
A software Build is the manufacturing process for the production of
deployable software products from source artifacts.

Build Phase
-----------
A Build phase is an identifiable phase in the build workflow.
(e.g. Compilation or Packaging)

Build Step
----------


Build System
------------
A build system is a 'configurable/programmable' software production
line. It is responsible for the execution of a software Build.

Build Workflow
--------------
A build workflow describes the process with the succession of steps
required for (parts of) a software Build. The build workflow should be
automated with a Build System.

Dependency
----------
A Dependency is a reference to an existing Artifact (from outside the
current Module) that is needed for the build of the current Module. A
Dependency can be scoped for the usage in the current Module.

The common properties of a Dependency in the context of the Baumeister build
system are
 * target
 * scope
 * transitivity/exclusions

Dependency Management
---------------------


Developer Tests
---------------
A test that is written and executed by developers during the development of
a module.

Generator
---------
A Generator generates output artifact transforms some source/input artifacts
to output artifacts.

Generator Framework
-------------------
A Generator Framework provides generic generators where the
transformation process is driven by a transformation configuration.

Integration Tests
-----------------


Model
-----
A model is an abstract representation of some (real world) domain.
(e.g UML models, use case models, business process models)

Model Driven Software Development
---------------------------------


Module
------
A Module is a unit of software?!.

Plugin
------
A Plugin is a component of the build system that encapsulates some
specific functionality for the build.

Transformation
--------------
generation, compilation

Transitive Dependency
---------------------
A transitive dependency is a dependency of a dependent artifact, which
is required for the usage of the dependent artifact.

Unit Tests
----------
Unit Tests test a single aspect of a unit of software (e.g. a class or a
function) in isolation.

Workflow
--------
A sequence of steps that are executed in a defined order to produce a
result.
