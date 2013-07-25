;
; In this file all the default settings for Baumeister are defined 
; These settings are merged with the settings in the module.clj files
;
[
 :module "DefaultModule"
 :project "DefaultProject"
 :version "0.1.0"
 ; default project directory layout
 :module-dir "."
 :build-dir "${module-dir}/build"
 :build-classes-dir "${build-dir}/classes"
 :dist-dir "${module-dir}/dist"
 :lib-dir "${module-dir}/lib"
 :log-level :warn
 ;
 ; Sets of architectural module type classifications
 ;
 ; TODO use keywords for module-types?
 :module-types #{:library :framework :application :domain :integration :presentation
                  :webservice :webfrontend :consolefrontend :appfrontend :data}
 :code-module-types #{:library :framework :application :domain :integration :presentation
                      :webservice :webfrontend :consolefrontend :appfrontend}
 :library-module-types #{:library}
 :framework-module-types #{:framework}
 :component-module-types #{:application :domain :integration :presentation}
 :web-module-types #{:webfrontend :webservice}
 :console-module-types #{:consolefrontend}
 :app-module-types #{:appfrontend}
 :data-module-types #{:data}
 ;
 ; Set of test classifications
 ;
 :test-types #{:unittest :integrationtest :acceptancetest}
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
                [:maven-proxy :thirdparty "http://clojars.org/repo" "${repository-root-dir}/clojars.org"]]
 ;
 ; dependency management configuration
 ;
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
 :workflow-definitions {
                        :clean-workflow [:clean]
                        :init-workflow [:clean :init]
                        :compile-workflow [:init :dependencies :generate :compile]
                        :package-workflow [:build-workflow :package]
                        :release-workflow [:build-workflow :release :distribute-release]
                        :integrationtest-workflow [:integrationtest]
                        :acceptancetest-workflow [:acceptancetest]
                        :build-workflow [:clean :compile-workflow :unittest :package
                                         :coverage :analyse :distribute]
                        :unittest-workflow [:unittest]
                        :architecture-workflow [:clean :init :dependencies :generate-architecture]
                        :coverage-workflow [:coverage] 
                        :analyse-workflow [:analyse]
                        ; :create-module-workflow [:create-module]
                        }
 ]
