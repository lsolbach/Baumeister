;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.plugin.genesis
  (:require [clojure.edn :as edn])
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file string]
        [org.soulspace.clj.application.string-property]
        [baumeister.utils log]
        [baumeister.config registry parameter-registry]))

; TODO extract templates into (data) modules and use the dependency mechanisms to resolve them

(def ^:dynamic *module*)

(defn module-path
  "Returns the relative path with regard to the working directory."
  [file]
  (str *module* "/" file))

(defmulti entry-action :type)
(defmethod entry-action :directory
  [entry]
  (create-dir (as-file (module-path (:name entry)))))

(defmethod entry-action :file
  [entry]
  (spit (module-path (:name entry)) (replace-properties (get-param-registry) (:content entry))))

(defn process-template
  "Process a module template."
  [template]
  (binding [*module* (param :module "NewModule")]
    (log :trace "creating new project processing template" template "for module" *module*)
    (create-dir (as-file (str *module*))) ; create new module directory
    (let [entries (read-string (slurp (str "build/template/" template "-template.clj")))]
      (doseq [entry entries]
            (entry-action entry)))
    ; remove build directory
    (delete-dir (as-file (param :build-dir)))))

(defn genesis-new
  "Creates a new module."
  []
  (process-template (param :template)))

(defn genesis-post-new
  "Creates a new module."
  []
  (delete-dir (as-file (param :build-dir))))

(def config
  {:params []
   :steps [[:new genesis-new]
           [:post-new genesis-post-new]]
   :functions []})
