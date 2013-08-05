;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns org.soulspace.build.baumeister.plugins.aspectj
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file function]
        [org.soulspace.build.baumeister.config registry plugin-registry]
        [org.soulspace.build.baumeister.utils ant-utils files checks log]))

; TODO fix classpath, add aspectjtools.jar to plugin dependencies
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
  (when-let [source-dirs (seq (source-dirs :aspectj-source-dir))]
    (println source-dirs)
    (aspectj-task (param :build-classes-dir) (dir-path source-dirs)
                  (jar-path (param :aspectj-lib-path))
                  (jar-path (param :aspectj-aspectin-path)) (jar-path (param :aspectj-aspect-path))))
  
  (when-let [source-dirs (seq (source-dirs :aspectj-source-unittest-dir))]
    (aspectj-task (param :build-unittest-classes-dir) (dir-path source-dirs)
                (jar-path (param :aspectj-unittest-lib-path))
                (jar-path (param :aspectj-unittest-aspectin-path))
                (str (param :build-classes-dir) ":" (jar-path (param :aspectj-unittest-aspect-path)))))

  (when-let [source-dirs (seq (source-dirs :aspectj-source-integrationtest-dir))]
    (aspectj-task (param :build-integrationtest-classes-dir) (dir-path source-dirs)
                (jar-path (param :aspectj-integrationtest-lib-path))
                (jar-path (param :aspectj-integrationtest-aspectin-path))
                (str (param :build-classes-dir) ":" (jar-path (param :aspectj-integrationtest-aspect-path)))))

  (when-let [source-dirs (seq (source-dirs :aspectj-source-acceptancetest-dir))]
    (aspectj-task (param :build-acceptancetest-classes-dir) (dir-path source-dirs)
                (jar-path (param :aspectj-acceptancetest-lib-path))
                (jar-path (param :aspectj-acceptancetest-aspectin-path))
                (str (param :build-classes-dir) ":" (jar-path (param :aspectj-acceptancetest-aspect-path))))))
  
(def config
  {:params [[:lib-runtime-dir "${lib-dir}/runtime"]
            [:lib-dev-dir "${lib-dir}/dev"]
            [:lib-aspect-dir "${lib-dir}/aspect"]
            [:lib-aspectin-dir "${lib-dir}/aspectin"]
            [:aspectj-compiler-fork "${compiler-fork}"]
            [:aspectj-compile-debug "${compile-debug}"]
            [:aspectj-source-encoding "${source-encoding}"]
            [:aspectj-source-version "${source-version}"]
            [:aspectj-target-version "${target-version}"]
            [:aspectj-source-dir "${source-dir}"]
            [:aspectj-source-unittest-dir "${source-unittest-dir}"]
            [:aspectj-source-integrationtest-dir "${source-integrationtest-dir}"]
            [:aspectj-source-acceptancetest-dir "${source-acceptancetest-dir}"]
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
