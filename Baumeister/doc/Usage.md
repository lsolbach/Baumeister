Baumeister Usage
================

Command Line Usage
------------------

> baumeister 

baumeister [options] [actions]

Command Line Options:

-D	--define	Define a parameter (-Dx=y)
-f	--file		Use the given file instead of module.clj (-fmyfile.clj)
-h	--help		Display help
-v	--version	Display the version of this Baumeister instance

Actions:

As action you can specify workflows (e.g. compile-workflow) or phases (e.g. compile).
See entry :default-workflows in config/default_settings.clj for the default workflow definitions.


Creating New Modules
--------------------

> baumeister -Dmodule=<module name> -Dtemplate=<template name> new-workflow

You can set parameters which are used in the templates with the command line switch --define or -D.

Example:

> baumeister -Dmodule=MyProject -Dtemplate=java new-workflow new-workflow

Supported templates: java, aspectj, clojure, baumeister-plugin, baumeister-template


Module Configuration
--------------------

The configuration file per module is module.clj in the root directory of the module.
It contains a vector of parameters, which are specified as key value pairs.

The required parameters are 

 * module	Identifier of this module.
 * project	Identifier of the project of this module
 * version	Version of this module
 * plugins 	List of plugins for this module

You can find the default configuration in config/module_defaults.clj.
In this file the default workflows, repositories and various other
parameters are defined.

If you want to overwrite some of the parameters on a global basis,
please don't change the module_defaults.clj file. Instead copy the
file config/settings.clj to $HOME/.Baumeister/settings.clj and change
it to your needs.


Workflows
---------
Each workflow consists of phases (which can be other workflows).


Phases
------
Each phase consists of 3 steps
 * pre-<phase-name>
 * <phase-name>
 * post-<phase-name>


Steps
-----
The registered functions get executed in order of the plugin declaration for pre-phase step and phase step. For post-phase steps the registered functions get executed in reverse order of the plugin declaration.


Dependency Management
---------------------
Baumeister provides transitive dependency management and can use Baumeister and Maven repositories for dependency resolution.

Dependency properties:

artifact target optional exclusions

Artifact properties:

project module version name type


Plugins
-------
A plugin registers parameters and functions for steps of workflow phases.

Existing Plugins:

Module Creation
 * org.soulspace.baumeister/GenesisPlugin

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


