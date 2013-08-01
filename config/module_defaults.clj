;
; In this file all the default settings for Baumeister are defined 
; These settings are merged with the settings in the module.clj files
;
[
 :system-version "0.4.1"
 ;
 ; default project directory layout
 ;
 :module-dir "."
 :build-dir "${module-dir}/build"
 :build-classes-dir "${build-dir}/classes"
 :build-report-dir "${build-dir}/report"
 :build-doc-dir "${build-dir}/doc"
 :build-javadoc-dir "${build-doc-dir}/javadoc"
 :dist-dir "${build-dir}/dist"
 :lib-dir "${build-dir}/lib"
 :source-config-dir "${module-dir}/config"
 :source-script-dir "${module-dir}/scripts"
 :source-webcontent-dir "${module-dir}/WebContent"
 ;
 ; sets of architectural module type classifications
 ;
 :module-types #{:library :framework :component :application :domain :integration :presentation
                  :webservice :webfrontend :consolefrontend :appfrontend :data}
 :code-module-types #{:library :framework :component :application :domain :integration :presentation
                      :webservice :webfrontend :consolefrontend :appfrontend}
 :library-module-types #{:library}
 :framework-module-types #{:framework}
 :component-module-types #{:component :application :domain :integration :presentation}
 :web-module-types #{:webfrontend :webservice}
 :console-module-types #{:consolefrontend}
 :app-module-types #{:appfrontend}
 :data-module-types #{:data}
 ;
 ; Set of test classifications
 ;
 :test-types #{:unittest :integrationtest :acceptancetest}
 ;
 ; default compiler config
 ;
 :compiler-fork "true" ; TODO string because it is fed into ant, but true/false should work too
 :compiler-maxmem "256m"
 :compile-debug "true"
 :source-encoding "UTF-8"
 :source-version "1.6"
 :target-version "1.6"
 ;
 ; default repository config
 ;
 ; default repository root dir
 :repository-root-dir "${baumeister-home-dir}/../repositories" ; FIXME use $HOME here
 ; default repositories
 :repositories [[:file :release "${repository-root-dir}/release"]
                [:file :development "${repository-root-dir}/development"]
                [:file :thirdparty "${repository-root-dir}/thirdparty"]
                [:maven-proxy :thirdparty "http://repo1.maven.org/maven2" "${repository-root-dir}/repo1.maven.org"]
                [:maven-proxy :thirdparty "http://clojars.org/repo" "${repository-root-dir}/clojars.org"]
                [:maven-proxy :thirdparty "https://maven.java.net/content/repositories/releases/" "${repository-root-dir}/java.net"]
                ]
 ;
 ; dependency management configuration
 ;
 :dependency-transitive true ; TODO "true"/"false" strings should work, too
 ; :dependency-targets defines the valid dependency targets
 :dependency-targets #{:root ; root project dependency, not a target!
                       :runtime ; runtime dependency, used on compile and runtime classpaths
                       :dev ; dev dependency, used on compile and test classpaths
                       :aspect ; aspect dependency, used on aspect compile and runtime classpaths
                       :aspectin ; aspectin dependency, used on aspectin compile classpath
                       :model ; model dependency, used on generation profile path
                       :generator ; generator dependency, used on generation template and profile path
                       :dependency ; dependency only, no artifacts ???
                       :meta ; meta dependency, no artifacts ???
                       }
 ; :dependency-target-mapping defines the target of a transitive dependency according to the target of the parent dependency
 ; if there is no mapping here, the transitive dependency will not be included in the build
 :dependency-target-mapping {:root {:runtime :runtime
                                    :dev :dev
                                    :aspect :aspect
                                    :aspectin :aspectin
                                    :model :model
                                    :generator :generator}
                             :runtime {:runtime :runtime
                                       :aspect :runtime}
                             :dev {:dev :dev} ; TODO check if transitive dev dependencies have to be included
                             :aspect {:runtime :runtime
                                      :aspect :runtime}
                             :aspectin {:dev :dev
                                        :runtime :runtime
                                        :aspect :runtime}
                             :model {:model :model}
                             :generator {:generator :generator}
                             } ; TODO dependency/meta/exclude?
 ; :dependency-actions defines the actions for the initialization of the dependencies in the build process
 :dependency-actions {:copy #{:runtime :dev :aspect :aspectin :model} ; copy the artifact to the specified lib target dir
                      :unzip #{:generator} ; unzip the artifact to ${lib-dir}
                      }
 ;
 ; maven dependency management compatibility
 ;
 ; :maven-scopes-to-targets contains the mapping of maven scopes to Baumeister targets
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
 ;
 ; definition of the default workflows
 ;
 :workflow-definitions
 {
  :prerequisites-workflow [:prerequisites] ; verify prerequisites for the build
  :clean-workflow [:clean] ; remove any build artifats and directories from the module
  :init-workflow [:init :dependencies] ; initialize the module, create required directories for a build and resolve dependencies
  :compile-workflow [:init-workflow :generate :compile] ; compile the module
  :package-workflow [:compile-workflow :unittest :sourcedoc :package] ; package the module
  :integrationtest-workflow [:package-workflow :integrationtest] ; run the integration tests
  :acceptancetest-workflow [:package-workflow :acceptancetest] ; run the acceptance test
  :unittest-workflow [:compile-workflow :unittest] ; run the unit tests
  :coverage-workflow [:package-workflow :coverage] ; run tests with code coverage
  :analyse-workflow [:package-workflow :analyse] ; perform static code analysis
  :build-workflow [:clean :package-workflow :unittest :package :coverage :analyse :distribute]
  :release-workflow [:build-workflow :generate-release :package-release :distribute-release] ; build distribution packages
  :architecture-workflow [:clean :init :dependencies :generate-architecture] ; generate modules from an architecture model
  :module-workflow [:create-module]
  }
 ; default log-level
 :log-level :info
 :message-level :normal
 ]
