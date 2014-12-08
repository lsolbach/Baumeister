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
  (:use [clojure.java.io]
        [org.soulspace.clj file]
        [baumeister.config parameter-registry]))

; TODO extract templates into (data) modules and use the dependency mechanisms to resolve them

(defn target-filename
  "Returns the file name of the target file."
  [filename]
  (if (starts-with "dot." filename)
    (substring 3 filename)
    filename))

(defn process-template-file
  "Processes a template file by reading it and replacing the variables."
  [file]
  )

(defn create-folder
  "Creates a folder."
  [file]
  (mkdir (as-file (path))))

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

(defn genesis-new
  "Creates a new module."
  []
  (mkdir (param :module))
  (process-templates (param :templates))
  )

(def config
  {:params []
   :steps [[:init genesis-init]
           [:new genesis-new]]
   :functions []})
