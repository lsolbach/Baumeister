(ns org.soulspace.build.baumeister.plugins.aspectj
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file function]
        [org.soulspace.build.baumeister.config.registry]
        [org.soulspace.build.baumeister.utils ant-utils files checks log]))

; TODO think of a mechanism for specifing different aspectj versions?
(def aspectj-home (get-env "ASPECTJ_HOME" (str home-dir "/devel/java/aspectj1.6")))
(ant-taskdef {:classpath (str aspectj-home "/lib/aspectjtools.jar:lib/runtime/ant.jar:lib/runtime/ant-launcher.jar")
              :resource "org/aspectj/tools/ant/taskdefs/aspectjTaskdefs.properties"})
(define-ant-task ant-iajc iajc)

(defn aspectj-task [dest-dir src-path class-path in-path aspect-path]
  (log :debug "iajc" dest-dir src-path class-path in-path aspect-path)
  (ant-iajc {:sourceRoots src-path  :destdir dest-dir :classpath class-path
             :inpath in-path :aspectpath aspect-path :source (get-var :aspectj-version "1.6")
             :debug (get-var :debug "false") :encoding (param :source-encoding "UTF-8") ;:verbose "true"
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

(defn plugin-init []
  (log :info "initializing plugin aspectj")
  ; FIXME compute classpath after deps and before compilation
  (register-vars [[:lib-runtime-dir "${lib-dir}/runtime"]
                  [:lib-dev-dir "${lib-dir}/dev"]
                  [:lib-aspect-dir "${lib-dir}/aspect"]
                  [:lib-aspectin-dir "${lib-dir}/aspectin"]
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
                  [:aspectj-acceptancetest-aspectin-path "${lib-aspectin-dir}"]])
  (register-source-paths)
  (register-fns [[:clean aspectj-clean]
                 [:init aspectj-init]
                 [:pre-compile aspectj-pre-compile]
                 [:compile aspectj-compile]]))
