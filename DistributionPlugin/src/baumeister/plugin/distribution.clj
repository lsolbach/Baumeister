;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.plugin.distribution
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj file]
        [baumeister.utils ant-utils checks log]
        [baumeister.config registry]))

; TODO rethink: binary distribution package?!
; TODO for what module types do we need a binary distribution (webfrontends, appfrontends, webservices, consolefrontends)?
(defn distribution-package []
  (message :fine "packaging distribution")
  (apply
    (partial ant-zip {:destFile (str (param :dist-dir) "/" (param :distribution-file))})
    (filter (complement nil?)
            [(ant-zipfileset {:dir (param :dist-dir) :includes "*.jar" :prefix (param "${distribution-name}/lib")})
             (ant-zipfileset {:dir (param :lib-runtime-dir) :includes "*.jar" :excludes "*Javadoc.jar" :prefix (param "${distribution-name}/lib")})
             (when (plugin? "aspectj")
               (ant-zipfileset {:dir (param :lib-aspect-dir) :includes "*.jar" :prefix (param "${distribution-name}/lib")}))
             (when (console-module?)
               (ant-zipfileset {:dir (param :source-script-dir) :prefix (param "${distribution-name}")}))
             (when (exists? (param :doc-dir))
               (ant-zipfileset {:dir (param :doc-dir) :prefix (param "${distribution-name}/doc")}))
             (when (exists? (param :build-sourcedoc-dir))
               (ant-zipfileset {:dir (param :build-sourcedoc-dir) :prefix (param "${distribution-name}/doc/api")}))
             (when (exists? (param :source-config-dir))
               (ant-zipfileset {:dir (param :source-config-dir) :prefix (param "${distribution-name}/config")}))
             (ant-zipfileset {:dir (param :module-dir) :includes "license.txt" :prefix (param "${distribution-name}")})
             ;(ant-zipfileset {:dir (param :module-dir) :includes "module.clj" :prefix (param "${distribution-name}")})
             (ant-zipfileset {:dir (param :module-dir) :includes "README.md" :prefix (param "${distribution-name}")})])))

(defn distribution-distribute []
  (message :fine "distributing distribution packages")
  (let [filename (param :distribution-file)]
    ; FIXME copy binary distribution package to some useful repository
    (create-dir (as-file (param "${distribution-dir}/${module}/")))
    (copy (as-file (str (param :dist-dir) "/" filename)) (as-file (str (param "${distribution-dir}/${module}/") filename)))))

; TODO use distribution spec for package content a la ant zip? e.g. [:dir :includes :excludes :prefix]
; :generate-distribution :package-distribution :distribute-distribution
(def config
  {:params [[:distribution-dir "${user-home-dir}"]
            [:distribution-name "${module}-${version}"]
            [:distribution-file "${distribution-name}.zip"]]
   :steps [[:package-distribution distribution-package]
           [:distribute-distribution distribution-distribute]]
   :functions []})
