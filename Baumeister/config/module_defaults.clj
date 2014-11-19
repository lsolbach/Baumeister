;
; *Please don't change this file!*
;
; This file defines the default configuration for Baumeister. 
; These settings are merged with the settings in other the module.clj files
;
; If you want to override configuration parameters, use $(HOME)/.Baumeister/settings.clj or module.clj
;
[
 :system-version "0.6.3"
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
  :compile-workflow [:init-workflow :generate :compile] ; compile the module
  :package-workflow [:compile-workflow :sourcedoc :package] ; package the module
  :integrationtest-workflow [:package-workflow :integrationtest] ; run the integration tests
  :acceptancetest-workflow [:package-workflow :acceptancetest] ; run the acceptance test
  :unittest-workflow [:compile-workflow :unittest] ; run the unit tests
  :coverage-workflow [:package-workflow :coverage] ; run tests with code coverage
  :analyse-workflow [:package-workflow :analyse] ; perform static code analysis
  :build-workflow [:clean :package-workflow :unittest :coverage :analyse :distribute]
  :distribute-workflow [:build-workflow :generate-distribution :package-distribution :distribute-distribution] ; build distribution packages
  :architecture-workflow [:clean :init :dependencies :generate-architecture] ; generate modules from an architecture model
  :module-workflow [:create-module]
  }
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
 :build-report-dir "${build-dir}/report"
 :build-doc-dir "${build-dir}/doc"
 :build-sourcedoc-dir "${build-doc-dir}/sourcedoc"
 :dist-dir "${build-dir}/dist"
 :lib-dir "${build-dir}/lib"
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
                  :architecture :analysis :data :baumeister-plugin}
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
 :data-module-types #{:architecture :analysis :data}
 :baumeister-plugin-types #{:baumeister-plugin}
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
                       :virtual ; meta dependency, no artifacts ???
                       }
 ; :dependency-target-mapping defines the target of a transitive dependency according to the target of the parent dependency
 ; if there is no mapping here, the transitive dependency will not be included in the build
 :dependency-target-mapping {:plugin-root {:runtime :runtime
                                           :aspect :runtime}
                             :root {:runtime :runtime
                                    :dev :dev
                                    :aspect :aspect
                                    :aspectin :aspectin
                                    :model :model
                                    :generator :generator}
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
                             } ; TODO virtual target
 ;
 ; :dependency-actions defines the actions for the initialization of the dependencies in the build process
 :dependency-actions {:copy #{:runtime :dev :aspect :aspectin :model} ; copy the artifact to the specified lib target dir
                      :unzip #{:generator} ; unzip the artifact to ${lib-dir}
                      :follow #{:root} ; don't use the dependency, just follow for transitive dependencies
                      }
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
                      "ejb-client" "jar"
                      "pom" "pom"
                      nil "jar"}
 ]
