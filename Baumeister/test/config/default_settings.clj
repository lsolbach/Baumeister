;
; *Please don't change this file!*
;
; If you want to override configuration parameters, use $(HOME)/.Baumeister/settings.clj or module.clj
;
; This file defines the default configuration for Baumeister.
; These settings are merged with the settings in the user specific settings.clj file and the module.clj files
;
[
 :system-version "0.6.10"
 ;
 ; default log and message levels
 ;
 :log-level :info
 :message-level :info
 ;
 ; definition of the default workflows
 ;
 :workflow-definitions
 {
  ; :prerequisites-workflow [:prerequisites] ; verify prerequisites for the build
  :clean-workflow [:clean] ; remove any build artifats and directories from the module
  :init-workflow [:init :dependencies] ; initialize the module, create required directories for a build and resolve dependencies
  :architecture-workflow [:clean :init :dependencies :generate-architecture] ; generate modules from an architecture model
  :compile-workflow [:init-workflow :generate :compile] ; compile the module
  :package-workflow [:compile-workflow :sourcedoc :package] ; package the module
  :integrationtest-workflow [:package-workflow :integrationtest] ; run the integration tests
  :acceptancetest-workflow [:package-workflow :acceptancetest] ; run the acceptance test
  :unittest-workflow [:compile-workflow :unittest] ; run the unit tests
  :coverage-workflow [:package-workflow :coverage] ; run tests with code coverage
  :analyse-workflow [:package-workflow :analyse] ; perform static code analysis
  :build-workflow [:clean :package-workflow :unittest :coverage :analyse :distribute]
  :distribute-workflow [:build-workflow :generate-distribution :package-distribution :distribute-distribution] ; build distribution packages
  :new-workflow [:init :dependencies :new]}

 ; the default action to take, if no workflow or phase is provided
 :default-action :package-workflow
 ;
 ; default project directory layout
 ;
 :module-dir "."
 :source-dir "${module-dir}/src"
 :source-unittest-dir "${module-dir}/unittest"
 :source-integrationtest-dir "${module-dir}/integrationtest"
 :source-acceptancetest-dir "${module-dir}/acceptancetest"
 :source-config-dir "${module-dir}/config"
 :source-script-dir "${module-dir}/scripts"
 :source-webcontent-dir "${module-dir}/WebContent"
 :build-dir "${module-dir}/build"
 :build-classes-dir "${build-dir}/classes"
 :build-unittest-classes-dir "${build-dir}/unittest/classes"
 :build-integrationtest-classes-dir "${build-dir}/integrationtest/classes"
 :build-acceptancetest-classes-dir "${build-dir}/acceptancetest/classes"
 :build-report-dir "${build-dir}/report"
 :build-doc-dir "${build-dir}/doc"
 :build-sourcedoc-dir "${build-doc-dir}/sourcedoc"
 :lib-dir "${build-dir}/lib"
 :dist-dir "${build-dir}/dist"
 :doc-dir "${module-dir}/doc"
 :generation-dir "${build-dir}/generated"
 :generation-source-dir "${generation-dir}/src"
 ;
 ;
 ; sets of architectural module type classifications
 ;
 ; TODO integrate :enterprise-application :web-application :console-application :enterprise-module
 ; TODO rething layered components (e.g. :domain-component, :integration-component, :presentation-component)
 ; TODO think about hexagonal architecture
 ; TODO define packaging for module types
 :module-types #{:library :framework :component :application :domain :integration :presentation
                  :webservice :webfrontend :consolefrontend :appfrontend
                  :architecture :analysis :data :baumeister-plugin :baumeister-template}
 :data-module-types #{:architecture :analysis :data :baumeister-template}
 :code-module-types #{:library :framework :component :application :domain :integration :presentation
                      :webservice :webfrontend :consolefrontend :appfrontend :baumeister-plugin}
 :library-module-types #{:library}
 :framework-module-types #{:framework}
 :component-module-types #{:component :application :domain :integration :presentation}
 :web-module-types #{:presentation :webfrontend :webservice}
 :frontend-types #{:consolefrontend :appfrontend :webfrontend :webservice}
 :web-frontend-types #{:webfrontend :webservice}
 :console-frontend-types #{:consolefrontend}
 :app-frontend-types #{:appfrontend}
 :baumeister-plugin-types #{:baumeister-plugin}
 :baumeister-template-types #{:baumeister-template}
 ;
 ; Set of test classifications
 ;
 :test-types #{:unittest :integrationtest :acceptancetest}
 ;
 ; Set of language classifications
 ; TODO use language classifications?! Could be used for default plugins, packaging, etc
 :language-types #{:none :java :aspectj :clojure :clojurescript :groovy :jython :jruby :scala}
 ;
 ; Set of packaging types
 ; TODO use packaging-type in PackagePlugin, implement mappings of language-types/module-types to packaging-types
 :packaging-types #{:jar :war :ear :zip}
 ;
 ; default compiler config
 ;
 :compiler-fork "false" ; TODO string because it is fed into ant, but true/false should work too
 :compiler-maxmem "256m"
 :compile-debug "true"
 :source-encoding "UTF-8"
 :source-version "1.6" ; used for Java and AspectJ
 :target-version "1.6" ; used for Java and AspectJ
 ;
 ; dependency management configuration
 ;
 ; :dependency-targets defines the valid dependency targets
 :dependency-targets #{:plugin-root ; root plugin dependency
                       :root ; root project dependency
                       :runtime ; runtime dependency, used on compile and runtime classpaths
                       :dev ; dev dependency, used on compile and test classpaths
                       :aspect ; aspect dependency, used on aspect compile and runtime classpaths
                       :aspectin ; aspectin dependency, used on aspectin compile classpath
                       :model ; model dependency, used on generation profile path
                       :generator ; generator dependency, used on generation template and profile path
                       :data ; data dependency
                       :virtual} ; meta dependency, no artifacts ???

 ; :dependency-target-mapping defines the target of a transitive dependency according to the target of the parent dependency
 ; if there is no mapping here, the transitive dependency will not be included in the build
 :dependency-target-mapping {:plugin-root {:runtime :runtime
                                           :aspect :runtime
                                           :data :data}
                             :root {:runtime :runtime
                                    :dev :dev
                                    :aspect :aspect
                                    :aspectin :aspectin
                                    :model :model
                                    :generator :generator
                                    :data :data}
                             :runtime {:runtime :runtime
                                       :aspect :runtime}
                             :dev {:runtime :dev} ; TODO check if transitive dev dependencies have to be included
                             :aspect {:runtime :runtime
                                      :aspect :runtime}
                             :aspectin {:dev :dev
                                        :runtime :runtime
                                        :aspect :runtime} ; TODO check which transitive dependencies have to be included
                             :model {:model :model}
                             :generator {:generator :generator}
                             :data {:data :data}}
                              ; TODO virtual target
 ;
 ; :dependency-actions defines the actions for the initialization of the dependencies in the build process
 :dependency-actions {:copy #{:runtime :dev :aspect :aspectin :model} ; copy the artifact to the specified lib target dir
                      :unzip #{:generator :data} ; unzip the artifact to ${lib-dir}
                      :follow #{:root}} ; don't use the dependency, just follow for transitive dependencies

 ;
 ; maven dependency management compatibility
 ;
 ; maven-scope-to-target contains the mapping of maven scopes to Baumeister targets
 :maven-scope-to-target {"runtime" :runtime
                         "compile" :runtime
                         "test" :dev
                         "provided" :dev
                         "system" :dev
                         nil :runtime}
 ; mapping of the maven type/packaging to Baumeister types
 :maven-type-to-type {"jar" "jar"
                      "war" "war"
                      "ear" "ear"
                      "ejb-client" "jar"
                      "pom" "pom"
                      nil "jar"}
 ;
 ; default repository config
 ;
 ; default repository root dir
 :repository-root-dir "${baumeister-home-dir}/../repositories" ; FIXME use $HOME here
 ; default repositories
 :repositories [[:file :release "${repository-root-dir}/release"]
                [:file :development "${repository-root-dir}/development"]
                [:file :thirdparty "${repository-root-dir}/thirdparty"]
                [:http-proxy :thirdparty "http://repo.soulspace.org/baumeister" "${repository-root-dir}/soulspace.org"]
                [:maven-proxy :thirdparty "http://repo1.maven.org/maven2" "${repository-root-dir}/repo1.maven.org"]
                [:maven-proxy :thirdparty "http://clojars.org/repo" "${repository-root-dir}/clojars.org"]
                [:maven-proxy :thirdparty "https://maven.java.net/content/repositories/releases/" "${repository-root-dir}/java.net"]]
;
; default plugin config
;
 :plugins ["global"
           ["org.soulspace.baumeister/DependencyPlugin"]
           ["org.soulspace.baumeister/GenesisPlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister" :dev]
                ["org.soulspace.baumeister/AspectJTemplate, 0.1.0, AspectJTemplate, zip" :data]
                ["org.soulspace.baumeister/BaumeisterPluginTemplate, 0.1.0, BaumeisterPluginTemplate, zip" :data]
                ["org.soulspace.baumeister/BaumeisterTemplateTemplate, 0.1.0, BaumeisterTemplateTemplate, zip" :data]
                ["org.soulspace.baumeister/ClojureTemplate, 0.1.0, ClojureTemplate, zip" :data]
                ["org.soulspace.baumeister/DataTemplate, 0.1.0, DataTemplate, zip" :data]
                ["org.soulspace.baumeister/JavaTemplate, 0.1.0, JavaTemplate, zip" :data]]

 :language-default-plugins {:clojure
                            [["org.soulspace.baumeister/DependencyPlugin"]
                             ["org.soulspace.baumeister/ClojurePlugin"]
                             ["org.soulspace.baumeister/ClojureTestPlugin"]
                             ["org.soulspace.baumeister/PackagePlugin"]]
                            :java
                            [["org.soulspace.baumeister/DependencyPlugin"]
                             ["org.soulspace.baumeister/JavaPlugin"]
                             ["org.soulspace.baumeister/PackagePlugin"]]
                            :aspectj
                            [["org.soulspace.baumeister/DependencyPlugin"]
                             ["org.soulspace.baumeister/AspectJPlugin"]
                             ["org.soulspace.baumeister/PackagePlugin"]]
                            :clojurescript
                            [["org.soulspace.baumeister/DependencyPlugin"]]

                            :groovy
                            [["org.soulspace.baumeister/DependencyPlugin"]
                             ["org.soulspace.baumeister/PackagePlugin"]]

                            :jython
                            [["org.soulspace.baumeister/DependencyPlugin"]
                             ["org.soulspace.baumeister/PackagePlugin"]]

                            :jruby
                            [["org.soulspace.baumeister/DependencyPlugin"]
                             ["org.soulspace.baumeister/PackagePlugin"]]

                            :scala
                            [["org.soulspace.baumeister/DependencyPlugin"]
                             ["org.soulspace.baumeister/ScalaPlugin"]
                             ["org.soulspace.baumeister/PackagePlugin"]]}]
