;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns org.soulspace.build.baumeister.plugins.release
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.build.baumeister.utils ant-utils checks log message]
        [org.soulspace.build.baumeister.config registry plugin-registry]))

; TODO rethink: binary release package?!
; TODO for what module types do we need a binary distribution (webfrontends, appfrontends, webservices, consolefrontends)?
(defn release-package []
  (message :fine "packaging release")
  (apply
    (partial ant-zip {:destFile (str (param :dist-dir) "/" (param :release-file))})
    (filter (complement nil?)
            [(ant-zipfileset {:dir (param :dist-dir) :includes "*.jar" :prefix (param "${release-name}/lib")})
             (ant-zipfileset {:dir (param :lib-runtime-dir) :includes "*.jar" :prefix (param "${release-name}/lib")})
             (when (plugin? "aspectj") (ant-zipfileset {:dir (param :lib-aspect-dir) :includes "*.jar" :prefix (param "${release-name}/lib")}))
             (ant-zipfileset {:dir (param :source-config-dir) :prefix (param "${release-name}/config")})
             (when (console-module?) (ant-zipfileset {:dir (param :source-script-dir) :prefix (param "${release-name}")}))
             (ant-zipfileset {:dir (param :module-dir) :includes "module.clj" :prefix (param "${release-name}")})
             (ant-zipfileset {:dir (param :module-dir) :includes "README.md" :prefix (param "${release-name}")})])))

(defn release-distribute []
  (message :fine "distributing release package")
  (let [filename (param :release-file)]
    ; FIXME copy binary release package to some useful repository
    (copy (as-file (str (param :dist-dir) "/" filename)) (as-file (str "../" filename)))))

(def config
  {:params [[:release-name "${module}-${version}"]
            [:release-file "${release-name}.zip"]]
   :functions [[:package-release release-package]
               [:distribute-release release-distribute]]})
