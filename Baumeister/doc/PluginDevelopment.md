Plugin Development
==================

Plugins are components that provide the functionality for the Baumeister build.
A plugin registers configuration parameters and functions for the steps of workflow phases.


Plugin Development Guidelines
-----------------------------
* Standard principles of good software design apply
* Define a concrete responsibility for the plugin
* Design orthogonal plugins that can be combined in different ways
* Aim for high cohesion and low coupling.
* Minimize inter-plugin dependencies
* Check for plugin registry for other plugin's existence before using stuff from another plugin (e.g. parameters, directories, output)
* Define all reasonable configuration parameters in the plugin config
* Define reasonable default values in the plugin config
* Prefix plugin configuration parameters with the plugin name

Plugin conventions
------------------
Plugin namespace resolution by plugin dependency
 * plugin namespace package is derived from the project name
   * use the project name as package name
 * plugin namespace name is derived from the module name
   * lower case name of the module without the "Plugin" suffix (as extracted by the regex: (.*)Plugin)


Plugin configuration
--------------------
The configuration of a plugin consists of two parts
 * defining configuration parameters (with default values)
 * registration of function to workflow steps

Plugin api
----------

Baumeister plugins can use the configuration registries and the utils of the main Baumeister module.

