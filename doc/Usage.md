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

Plugins
-------
A plugin registers parameters and functions for steps of workflow phases.


Artifacts
---------
Artifact
project module version name type

Dependency Management
---------------------

Dependency
artifact target optional exclusions