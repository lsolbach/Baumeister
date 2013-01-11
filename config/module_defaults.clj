[
 :name "DefaultModule"
 :project "DefaultProject"
 :version "0.1.0"
 :module-dir "."
 :log-level :warn
 :build-dir "${module-dir}/build"
 :build-classes-dir "${build-dir}/classes"
 :dist-dir "${module-dir}/dist"
 :lib-dir "${module-dir}/lib"
 :code-module-types #{"library" "framework" "application" "domain" "integration" "presentation" "webfrontend" "webservice" "consolefrontend" "appfrontend"}
 :library-module-types #{"library"} ; TODO use keywords for module-types?
 :framework-module-types #{"framework"}
 :component-module-types #{"application" "domain" "integration" "presentation"}
 :web-module-types #{"webfrontend" "webservice"}
 :console-module-types #{"consolefrontend"}
 :app-module-types #{"appfrontend"}
 :data-module-types #{"data"}
 :test-types #{"unittest" "integrationtest" "acceptancetest"}
 :repositories [[:file :release "${baumeister-home-dir}/../repositories/release"] ; TODO use keywords instead of strings for the type?
                [:file :development "${baumeister-home-dir}/../repositories/development"]
                [:file :thirdparty "${baumeister-home-dir}/../repositories/thirdparty"]
                [:maven-proxy :thirdparty "http://repo1.maven.org/maven2" "${baumeister-home-dir}/../repositories/repo1.maven.org"]
                [:maven-proxy :thirdparty "http://clojars.org/repo" "${baumeister-home-dir}/../repositories/clojars.org"]
;                [:maven-proxy :thirdparty "http://maven.alfresco.com/nexus/content/groups/public" "${baumeister-home-dir}/../repositories/maven.alfresco.com"]
                ]
 ]