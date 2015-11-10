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
    (let [entries (read-string (slurp (str "templates/" template "-template.clj")))]
      (create-dir (as-file *module*))
      (doseq [entry entries]
        (entry-action entry)))))

(defn genesis-init
  "Initialize genesis plugin."
  []
  )

(defn genesis-new
  "Creates a new module."
  []
  (process-template (param :template)))

(def config
  {:params []
   :steps [[:init genesis-init]
           [:new genesis-new]]
   :functions []})
