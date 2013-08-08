Plugin Development Guidelines
=============================

* Plugins are components participating in the Baumeister build.

* Standard principles of good software development apply.

* Define a concrete responsibility for the plugin

* Design orthogonal plugins that can be combined in different ways

* Aim for high cohesion and low coupling.

* Minimize inter-plugin dependencies

* Check for plugin registry for other plugin's existence before using stuff from another plugin (e.g. parameters, directories, output)

* Define all reasonable configuration parameters in plugin setup

* Define reasonable default values in plugin setup

* Prefix configuration parameters with the plugin name
