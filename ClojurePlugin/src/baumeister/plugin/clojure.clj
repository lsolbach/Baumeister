;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.plugin.clojure
  (:require [clojure.string :as str]
            [clojure.java.io :as io])
  (:use [clojure.java.io :exclude [delete-file]]
        [clojure.string :only [join ends-with?]]
        [org.soulspace.clj file file-search namespace string ]
        [baumeister.utils ant-utils files checks log]
        [baumeister.config registry]))

(defn remove-clj-ext [clj-path]
  (if (str/ends-with? clj-path ".clj")
    (substring 0 (- (count clj-path) 4) clj-path)
    clj-path))

(defn collect-namespaces [src-path]
  (map file-to-ns (map normalize-path (map remove-clj-ext (map (partial relative-path src-path) (existing-files-on-path "clj" src-path))))))

(defn compile-clojure [dest-dir src-path class-path]
  (ant-java {:classname "clojure.lang.Compile" :fork (param :clojure-compiler-fork)
             :failonerror "true" :classpath class-path}
            (ant-variable {:key "clojure.compile.path" :value dest-dir})
            {:line (str/join " " (collect-namespaces src-path))}))

(defn clojure-init
  "clojure init"
  []
  (create-dir (as-file (param :build-classes-dir)))
  (create-dir (as-file (param :build-unittest-classes-dir)))
  (create-dir (as-file (param :build-integrationtest-classes-dir)))
  (create-dir (as-file (param :build-acceptancetest-classes-dir))))

(defn clojure-compile
  "clojure compile"
  []
  ; compute classpaths before compilation after dependencies have been initialized
  (when-let [source-dirs (seq (source-dirs :clojure-source-dir))]
    (let [source-path (dir-path source-dirs)]
      (compile-clojure (param :build-classes-dir) source-path
                       (class-path [(param :build-classes-dir) source-path
                                    (jar-path (param :clojure-lib-path))]))))

  (when-let [source-dirs (seq (source-dirs :clojure-source-unittest-dir))]
    (let [source-path (dir-path source-dirs)]
    (compile-clojure (param :build-unittest-classes-dir) source-path
                     (class-path [(param :build-unittest-classes-dir) source-path
                                  (param :build-classes-dir) (jar-path (param :clojure-unittest-lib-path))]))))

  (when-let [source-dirs (seq (source-dirs :clojure-integrationtest-source-dir))]
    (let [source-path (dir-path source-dirs)]
    (compile-clojure (param :build-integrationtest-classes-dir) source-path
                     (class-path [(param :build-integrationtest-classes-dir) source-path
                                  (param :build-classes-dir) (jar-path (param :clojure-integrationtest-lib-path))]))))
  
  (when-let [source-dirs (seq (source-dirs :clojure-acceptancetest-source-dir))]
    (let [source-path (dir-path source-dirs)]
      (compile-clojure (param :build-acceptancetest-classes-dir) source-path
                       (class-path [(param :build-acceptancetest-classes-dir) source-path
                                    (param :build-classes-dir) (jar-path (param :clojure-acceptancetest-lib-path))])))))

(def config
  {:params [[:lib-runtime-dir "${lib-dir}/runtime"]
            [:lib-dev-dir "${lib-dir}/dev"]
            [:clojure-compiler-fork "${compiler-fork}"]
            [:clojure-source-dir "${source-dir}"]
            [:clojure-source-unittest-dir "${source-unittest-dir}"]
            [:clojure-source-integrationtest-dir "${source-integrationtest-dir}"]
            [:clojure-acceptancetest-source-dir "${source-acceptancetest-dir}"]
            [:clojure-lib-path "${lib-runtime-dir}:${lib-dev-dir}"]
            [:clojure-unittest-lib-path "${lib-runtime-dir}:${lib-dev-dir}"]
            [:clojure-integrationtest-lib-path "${lib-runtime-dir}:${lib-dev-dir}"]
            [:clojure-acceptancetest-lib-path "${lib-runtime-dir}:${lib-dev-dir}"]]
   :steps [[:init clojure-init]
           [:compile clojure-compile]]
   :functions []})
