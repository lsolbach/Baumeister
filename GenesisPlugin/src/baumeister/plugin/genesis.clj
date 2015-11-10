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
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file string]
        [baumeister.config registry parameter-registry]))

; TODO extract templates into (data) modules and use the dependency mechanisms to resolve them

(def root-dir) ; FIXME fix process-project-template and remove

(defn create-folder
  "Creates a folder."
  [file]
  (create-dir (as-file (path))))

(defn create-file
  "Creates a file."
  [file]
  ()
  )

(defn process-project-template
  "Process a project template."
  [template]
  (->>
    (str root-dir template)
    (as-file)
    (all-files))
  )

(defn process-project-templates
  "Process the collection of project templates."
  [coll]
  )

(defn genesis-init
  "Initialize genesis plugin."
  []
  )

(defn genesis-new
  "Creates a new module."
  []
  (create-dir (param :module))
  (process-project-templates (param :templates))
  )

(def config
  {:params []
   :steps [[:init genesis-init]
           [:new genesis-new]]
   :functions []})
