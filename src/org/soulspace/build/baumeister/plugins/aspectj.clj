(ns org.soulspace.build.baumeister.plugins.aspectj
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file function]
        [org.soulspace.build.baumeister.config registry plugin-registry]
        [org.soulspace.build.baumeister.utils ant-utils files checks log]))

; TODO fix classpath, add aspectjtools.jar to plugin dependencies
(def baumeister-classpath (get-env "BAUMEISTER_CLASSPATH"))
(println "ASPECTJ CP" (param "${aspectj-home}/lib/aspectjtools.jar"))
(ant-taskdef {:classpath (param "${aspectj-home}/lib/aspectjtools.jar")
              :resource "org/aspectj/tools/ant/taskdefs/aspectjTaskdefs.properties"})
(define-ant-task ant-iajc iajc)

(defn aspectj-task [dest-dir src-path class-path in-path aspect-path]
  (log :debug "iajc" dest-dir src-path class-path in-path aspect-path)
  (ant-iajc {:fork (param :aspectj-compiler-fork)
             :sourceRoots src-path
             :destdir dest-dir
             :classpath class-path
             :inpath in-path :aspectpath aspect-path
             :source (param :aspectj-source-version "1.6")
             :debug (param :aspectj-compile-debug)
             :encoding (param :aspectj-source-encoding) ;:verbose "true"
             :sourceRootCopyFilter "**/CVS/*,**/*.java,**/*.aj,**/.clj"
             :inpathDirCopyFilter "**/CVS/*,**/*.java,**/*.aj,**/.clj,**/*.class,**/*.jar,**/*.txt"}))

(defn aspectj-clean []
  "aspectj clean"
  (delete-dir (as-file (param :lib-runtime-dir)))
  (delete-dir (as-file (param :lib-aspectin-dir)))
  (delete-dir (as-file (param :lib-aspect-dir)))
  (delete-dir (as-file (param :lib-dev-dir))))

(defn aspectj-init []
  "aspectj init"
  (create-dir (as-file (param :lib-runtime-dir)))
  (create-dir (as-file (param :lib-aspectin-dir)))
  (create-dir (as-file (param :lib-aspect-dir)))
  (create-dir (as-file (param :lib-dev-dir))))

(defn aspectj-pre-compile []
  "aspectj pre-compile"
  ; handle a missing generation dirs (TODO check if necessary?!)
  (when (plugin? "mdsd")
    (create-dir (as-file (str (param :mdsd-generation-dir) "/unittest")))
    (create-dir (as-file (str (param :mdsd-generation-dir) "/integrationtest")))
    (create-dir (as-file (str (param :mdsd-generation-dir) "/acceptancetest")))))

(defn aspectj-compile []
  "aspectj compile"
  ; compute classpaths before compilation after dependencies have been initialized
  (aspectj-task (param :build-classes-dir) (param :aspectj-source-path)
                (jar-path (param :aspectj-lib-path))
                (jar-path (param :aspectj-aspectin-path)) (jar-path (param :aspectj-aspect-path)))
  (when (unittest?)
    (aspectj-task (param :build-unittest-classes-dir) (param :aspectj-unittest-source-path)
                (jar-path (param :aspectj-unittest-lib-path))
                (jar-path (param :aspectj-unittest-aspectin-path))
                (str (param :build-classes-dir) ":" (jar-path (param :aspectj-unittest-aspect-path)))))
  (when (integrationtest?)
    (aspectj-task (param :build-integrationtest-classes-dir) (param :aspectj-integrationtest-source-path)
                (jar-path (param :aspectj-integrationtest-lib-path))
                (jar-path (param :aspectj-integrationtest-aspectin-path))
                (str (param :build-classes-dir) ":" (jar-path (param :aspectj-integrationtest-aspect-path)))))
  (when (acceptancetest?)
    (aspectj-task (param :build-acceptancetest-classes-dir) (param :aspectj-acceptancetest-source-path)
                (jar-path (param :aspectj-acceptancetest-lib-path))
                (jar-path (param :aspectj-acceptancetest-aspectin-path))
                (str (param :build-classes-dir) ":" (jar-path (param :aspectj-acceptancetest-aspect-path))))))

(defn register-source-paths []
  (if (has-plugin? "mdsd")
    (register-vars [[:aspectj-source-path "src:${mdsd-generation-dir}/src"]
                    [:aspectj-unittest-source-path "unittest:${mdsd-generation-dir}/unittest"]
                    [:aspectj-integrationtest-source-path "integrationtest:${mdsd-generation-dir}/integrationtest"]
                    [:aspectj-acceptancetest-source-path "acceptancetest:${mdsd-generation-dir}/acceptancetest"]])
    (register-vars [[:aspectj-source-path "src"]
                    [:aspectj-unittest-source-path "unittest"]
                    [:aspectj-integrationtest-source-path "integrationtest"]
                    [:aspectj-acceptancetest-source-path "acceptancetest"]])))

(def aspectj-config
  {:params [[:lib-runtime-dir "${lib-dir}/runtime"]
            [:lib-dev-dir "${lib-dir}/dev"]
            [:lib-aspect-dir "${lib-dir}/aspect"]
            [:lib-aspectin-dir "${lib-dir}/aspectin"]
            [:aspectj-compiler-fork "${compiler-fork}"]
            [:aspectj-compile-debug "${compile-debug}"]
            [:aspectj-source-encoding "${source-encoding}"]
            [:aspectj-source-version "${source-version}"]
            [:aspectj-target-version "${target-version}"]
            [:aspectj-lib-path "${lib-runtime-dir}:${lib-dev-dir}"]
            [:aspectj-unittest-lib-path "${lib-runtime-dir}:${lib-dev-dir}"]
            [:aspectj-integrationtest-lib-path "${lib-runtime-dir}:${lib-dev-dir}"]
            [:aspectj-acceptancetest-lib-path "${lib-runtime-dir}:${lib-dev-dir}"]
            [:aspectj-aspect-path "${lib-aspect-dir}"]
            [:aspectj-unittest-aspect-path "${lib-aspect-dir}"]
            [:aspectj-integrationtest-aspect-path "${lib-aspect-dir}"]
            [:aspectj-acceptancetest-aspect-path "${lib-aspect-dir}"]
            [:aspectj-aspectin-path "${lib-aspectin-dir}"]
            [:aspectj-unittest-aspectin-path "${lib-aspectin-dir}"]
            [:aspectj-integrationtest-aspectin-path "${lib-aspectin-dir}"]
            [:aspectj-acceptancetest-aspectin-path "${lib-aspectin-dir}"]]
   :functions [[:clean aspectj-clean]
               [:init aspectj-init]
               [:pre-compile aspectj-pre-compile]
               [:compile aspectj-compile]]})

(defn plugin-init []
  (log :info "initializing plugin aspectj")
  ; FIXME compute classpath after deps and before compilation
  (register-vars (:params aspectj-config))
  (register-source-paths)
  (register-fns (:functions aspectj-config)))
