;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.plugin.java
  (:use [clojure.java.io :exclude [delete-file]]
        [clojure.string :only [split join]]
        [org.soulspace.clj file function]
        [baumeister.utils files ant-utils checks log message]
        [baumeister.config registry plugin-registry]))

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
  ; (remove #(not (exists? %)) source-dirs)
  (when-let [source-dirs (seq (source-dirs :java-source-dir))]
    (compile-java (param :build-classes-dir) (dir-path source-dirs) (jar-path (param :java-lib-path))))
  (when-let [source-dirs (seq (source-dirs :java-source-unittest-dir))]
    (compile-java (param :build-unittest-classes-dir) (dir-path source-dirs)
                  (str (param :build-classes-dir) ":" (jar-path (param :java-unittest-lib-path)))))
  (when-let [source-dirs (seq (source-dirs :java-source-integrationtest-dir))]
    (compile-java (param :build-integrationtest-classes-dir) (dir-path source-dirs)
                  (str (param :build-classes-dir) ":" (jar-path (param :java-integrationtest-lib-path)))))
  (when-let [source-dirs (seq (source-dirs :java-source-acceptancetest-dir))]
    (compile-java (param :build-acceptancetest-classes-dir) (dir-path source-dirs)
                  (str (param :build-classes-dir) ":" (jar-path (param :java-acceptancetest-lib-path))))))

(defn java-sourcedoc
  []
  (when-let [source-dirs (seq (source-dirs :java-source-dir))]
    (message :fine "generating javadoc...")
    (ant-javadoc {:destdir (param "${java-javadoc-dir}")
                :sourcepath (dir-path source-dirs)
                :windowtitle (param :java-javadoc-windowtitle)
                :doctitle (param :java-javadoc-doctitle)
                :header (param :java-javadoc-header)
                :footer (param :java-javadoc-footer)
                :source (param :java-source-version)
                })))

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
            [:java-source-dir "${source-dir}"]
            [:java-source-unittest-dir "${source-unittest-dir}"]
            [:java-source-integrationtest-dir "${source-integrationtest-dir}"]
            [:java-source-acceptancetest-dir "${source-acceptancetest-dir}"]
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
