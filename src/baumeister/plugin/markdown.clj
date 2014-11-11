;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.plugin.markdown
  (:use [clojure.java.io :only [as-file reader]]
        [org.soulspace.clj file]
        [org.soulspace.clj.markdown markdown]
        [baumeister.config registry]))

(defn readme
  []
  (spit (str (param :markdown-build-dir) "/README.md")
        (str
          (h1 (param "${module}"))
          (p (param "${description}"))
          (h2 "Version")
          (p (param :version))
          (h2 "Author")
          (p (param :author))
          (h2 "Copyright")
          (p (param :copyright))
          (h2 "License")
          (str (link (param :license)) "\n")
          (h2 "Documentation")
          (h2 "Code Repository"))))

(defn version-history
  []
  )

(defn markdown-init
  []
  (create-dir (as-file (str (param :markdown-build-dir)))))

(defn markdown-generate-distribution
  []
  (readme))

(def config
  {:params [[:markdown-build-dir "${build-dir}/markdown"]]
   :steps [[:init markdown-init]
           [:generate-distribution markdown-generate-distribution]]
   :functions []})
