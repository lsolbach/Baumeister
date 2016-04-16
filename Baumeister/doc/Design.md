Baumeister Design
=================

This document describes the main design ideas of the Baumeister Build System

Logical Core Components
-----------------------

 * Configuration
 * Registries
 * Workflow Engine
 * Repositories
 * Dependency Management
 * Startup, Command Line Interface
 * Plugin API


Configuration
-------------
Tasks
 * internal setup
 * loading of configuration files
 * registration and initialization of plugins

Registries
----------

 * parameter registry
 * function registry
 * plugin registry
 * repository registy

Workflow Engine
---------------

 * execute the requested workflows
 * call the registered functions for the different steps of the executed workflows

Repositories
------------

 * provide access to artifacts
 * provide interoperability with maven repositories

Dependency Management
---------------------

 * resolve transitive dependencies

Startup, Command Line Interface
-------------------------------


Plugin API
----------

 * provide access to the baumeister configuration

 