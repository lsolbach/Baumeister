(ns org.soulspace.build.baumeister.plugins.java
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file function]
        [org.soulspace.build.baumeister.utils files ant-utils checks log message]
        [org.soulspace.build.baumeister.config registry plugin-registry]))

; TODO is at least access to the dependency tree needed?
(defn compile-java [destdir srcdir class-path]
  (log :debug "java compile classpath" class-path) ; FIXME needs ${build-*-dirs}:(jar-path ${lib-*-dirs})
  (if (has-plugin? "aspectj")
    (log :warn "aspectj plugin enabled, using aspectj to compile java classes")  
    (ant-javac {:executable (param :java-compiler)
                :fork "true"
                :includeantruntime "false"
                :destdir destdir
                :debug (param :java-compile-debug)
                :encoding (param :java-source-encoding)
                :source (param :java-source-version)
                :target (param :java-target-version)
                :srcdir srcdir
                :classpath class-path})))

(defn java-clean
  "java clean"
  []
  (delete-file (as-file (param :lib-runtime-dir)))
  (delete-file (as-file (param :lib-dev-dir))))

(defn java-init
  "java init"
  []
  (create-dir (as-file (param :lib-runtime-dir)))
  (create-dir (as-file (param :lib-dev-dir))))

(defn java-compile
  "java compile"
  []
  ; compute classpaths before compilation after dependencies have been initialized
  (compile-java (param :build-classes-dir) (param :java-source-path) (jar-path (param :java-lib-path)))
  (when (unittest?)
    (compile-java (param :build-unittest-classes-dir) (param :java-unittest-source-path)
                  (str (param :build-classes-dir) ":" (jar-path (param :java-unittest-lib-path)))))
  (when (integrationtest?)
    (compile-java (param :build-integrationtest-classes-dir) (param :java-integrationtest-source-path)
                  (str (param :build-classes-dir) ":" (jar-path (param :java-integrationtest-lib-path)))))
  (when (acceptancetest?)
    (compile-java (param :build-acceptancetest-classes-dir) (param :java-acceptancetest-source-path)
                  (str (param :build-classes-dir) ":" (jar-path (param :java-acceptancetest-lib-path))))))

(defn java-sourcedoc
  []
  (message :fine "generating javadoc...")
  (ant-javadoc {:destdir (param "${java-javadoc-dir}")
                :sourcepath (param :java-source-path)
                :windowtitle (param :java-javadoc-windowtitle)
                :doctitle (param :java-javadoc-doctitle)
                :header (param :java-javadoc-header)
                :footer (param :java-javadoc-footer)
                :source (param :java-source-version)
                }))

; TODO check if we compute classpath after deps and before compilation
(def config
  {:params [[:lib-runtime-dir "${lib-dir}/runtime"]
            [:lib-dev-dir "${lib-dir}/dev"]
            [:java-home "${java-home}"]
            [:java-compiler "${java-home}/bin/javac"]
            [:java-source-encoding "${source-encoding}"]
            [:java-source-version "${source-version}"]
            [:java-target-version "${target-version}"]
            [:java-compiler-fork "${compiler-fork}"]
            [:java-compiler-maxmem "${compiler-maxmem}"]
            [:java-compile-debug "${compile-debug}"]
            [:java-lib-path "${lib-runtime-dir}:${lib-dev-dir}"]
            [:java-unittest-lib-path "${lib-runtime-dir}:${lib-dev-dir}"]
            [:java-integrationtest-lib-path "${lib-runtime-dir}:${lib-dev-dir}"]
            [:java-acceptancetest-lib-path "${lib-runtime-dir}:${lib-dev-dir}"]
            [:java-javadoc-dir "${build-sourcedoc-dir}/java"]
            ;[:java-javadoc-windowtitle "${module} ${version}"]
            ;[:java-javadoc-doctitle "${module} ${version}"]
            ;[:java-javadoc-header "${module} ${version}"]
            ;[:java-javadoc-footer ""]
            ]
   :functions [[:clean java-clean]
               [:init java-init]
               [:compile java-compile]
               [:sourcedoc java-sourcedoc]]})

(defn register-source-paths []
  (if (has-plugin? "mdsd")
    (register-vars [[:java-source-path "src:generated/src"]
                    [:java-unittest-source-path "unittest:${mdsd-generation-dir}/unittest"]
                    [:java-integration-test-source-path "integrationtest:${mdsd-generation-dir}/integrationtest"]
                    [:java-acceptancetest-source-path "acceptancetest:${mdsd-generation-dir}/acceptancetest"]])
    (register-vars [[:java-source-path "src"]
                    [:java-unittest-source-path "unittest"]
                    [:java-integration-test-source-path "integrationtest"]
                    [:java-acceptancetest-source-path "acceptancetest"]])))

(defn plugin-init []
  (message :fine "initializing plugin java")
  (register-vars (:params config))
  (register-source-paths)
  (register-fns (:functions config)))
