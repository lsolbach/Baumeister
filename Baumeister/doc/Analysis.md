Baumeister Build System Analysis
================================

Value proposition
-----------------
 * software build (transformation) automatization
   * repeatable, fast, error free, efficient
   * enablement
     * continuous integration, documentation, statistics and quality metrics
   * structural clearness
     * explicit specification/documentation of dependencies and workflows
   * standardization
 * automation of non software build transformation processes (e.g. PDF generation from xml source)!?

(is the sole focus on software builds or can other types of structured transformation workflows be handled too?
 (e.g. document generation processes from input content source to pdf or static site generation from input content source))

 
Software build tasks to automate
--------------------------------
 * development builds
 * release builds
 * project/module setup
 * tests
 * documentation generation
 * project distribution
 * deployments

Other build tasks with could be automated with Baumeister
---------------------------------------------------------


Dimensions
----------
 Build System Dimensions
 * process
   * workflows, phases, steps, standalone functions
 * functionality (functional orthogonality)
   * plugins (compile, test, doc, analyse, ...)
 
 Module
 * source types (code, models, data)
 * source (code/test) types (unit, integration, acceptance, performance, load, stress, ...)
 * implementation languages
 	(java, aspectj, clojure, ...)
 * architectural module types
 	(library, framework, component (integration layer, domain layer, application layer, presentation layer), web service, frontend (web, console, app))
 * packaging (jar, war, ear, ...)

 * environments (servers, ...)?
 * (development, test, staging, production, ...)
 * build classification (development snapshots, releases)

Dependency Dimensions
 * dependencies (inclusion/exclusion)
 * usage types (model, runtime, dev, aspect, aspectin)
 * artifacts, versions
 

Possible build environments
---------------------------
 * local developer pc
   * command line
   * IDE (eclipse, netbeans, idea, ...)
 * build server (continuous integration server) (jenkins, cruise control)
 * test/production servers?


Demarcation of Build Tools and Build Servers
--------------------------------------------
 * Build Tools can run locally and on a Build Server,
 * Build Servers normally run on a "centralized" server
 * Build Servers can distribute builds to Build machines
 * Who checks out the code from the scm? 
 * Publishing of artifacts (Build Tool or Build Server?)
