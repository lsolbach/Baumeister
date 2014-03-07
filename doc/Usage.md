Baumeister Usage
================

Command Line Usage

> baumeister 

baumeister [options] [actions]

Command Line Options

-D	--define	Define a parameter (-Dx=y)
-f	--file		Use the given file instead of module.clj (-fmyfile.clj)
-h	--help		Display help
-v	--version	Display the version of this Baumeister instance

Module Configuration
--------------------

The configuration file per module is module.clj in the root directory of the module.

Parameters are specified as key value pairs.

The required parameters are 

* module	Identifier of this module.
* project	Identifier of the project of this module
* version	Version of this module
* plugins 	List of plugins for this module

Workflows
---------
Each workflow consists of phases (which can be other workflows)

Phases
------
Each phase consists of 3 steps
 * pre-<phase-name>
 * <phase-name>
 * post-<phase-name>

Steps
-----
The registered functions get executed in order of the plugin declaration for pre-phase step and phase step.
For post-phase steps the registered functions get executed in reverse order of the plugin declaration.


Artifacts
---------
Artifact
project module version name type

Dependency Management
---------------------

Dependency
artifact target optional exclusions

Plugins
-------
A plugin registers parameters and functions for steps of workflow phases.

Existing Plugins

Dependency Resolution
* org.soulspace.baumeister/DependencyPlugin

Compiler
 * org.soulspace.baumeister/AspectJPlugin
 * org.soulspace.baumeister/ClojurePlugin
 * org.soulspace.baumeister/GroovyPlugin
 * org.soulspace.baumeister/JavaPlugin
 * org.soulspace.baumeister/ScalaPlugin

API Documentation
 * org.soulspace.baumeister/AspectJDocPlugin
 * org.soulspace.baumeister/JavaDocPlugin

Packaging
 * org.soulspace.baumeister/PackagePlugin

Testing
 * org.soulspace.baumeister/ClojureTestPlugin
 * org.soulspace.baumeister/JUnitPlugin

Code Coverage
 * org.soulspace.baumeister/CoberturaPlugin

Static Code Analysis
 * org.soulspace.baumeister/CheckstylePlugin
 * org.soulspace.baumeister/FindbugsPlugin
 * org.soulspace.baumeister/JDependPlugin
 * org.soulspace.baumeister/PMDPlugin

Generators
 * org.soulspace.baumeister/MDDGeneratorPlugin
 * org.soulspace.baumeister/PlantUMLPlugin

Integration
 * org.soulspace.baumeister/EclipsePlugin
 * org.soulspace.baumeister/MavenPlugin
 * org.soulspace.baumeister/MarkdownPlugin

Work in progress, not yet functional
 * org.soulspace.baumeister/ClojureDocPlugin
 * org.soulspace.baumeister/DistributionPlugin
 * org.soulspace.baumeister/JythonPlugin
 * org.soulspace.baumeister/SitePlugin
