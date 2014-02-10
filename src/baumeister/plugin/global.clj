;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.plugin.global
  (:use [clojure.java.io :exclude [delete-file]] 
        [org.soulspace.clj file]
        [baumeister.utils ant-utils log]
        [baumeister.config registry plugin-registry]))

; TODO handle lib-target-dirs gracefully
(defn global-clean
  "Global clean"
  []
  (log :info "cleaning globally...")
  (delete-dir (as-file (param :build-dir))))

(defn global-init
  "Global initialization"
  []
  (log :info "initializing globally...")
  (create-dir (as-file (param :build-dir)))
  (create-dir (as-file (param :build-classes-dir)))
  (create-dir (as-file (param :build-unittest-classes-dir)))
  (create-dir (as-file (param :build-integrationtest-classes-dir)))
  (create-dir (as-file (param :build-acceptancetest-classes-dir)))
  (create-dir (as-file (param :build-report-dir)))
  (create-dir (as-file (param :lib-dir)))
  (create-dir (as-file (param :lib-runtime-dir)))
  (create-dir (as-file (param :lib-dev-dir)))
  (create-dir (as-file (param :dist-dir))))

(def config
  {:params [[:module-dir "."]
            [:build-dir "${module-dir}/build"]
            [:lib-dir "${build-dir}/lib"]
            [:lib-runtime-dir "${lib-dir}/runtime"]
            [:lib-dev-dir "${lib-dir}/dev"]
            [:dist-dir "${build-dir}/dist"]
            [:build-classes-dir "${build-dir}/classes"]
            [:build-unittest-classes-dir "${build-dir}/unittest/classes"]
            [:build-integrationtest-classes-dir "${build-dir}/integrationtest/classes"]
            [:build-acceptancetest-classes-dir "${build-dir}/acceptancetest/classes"]
            [:build-report-dir "${build-dir}/report"]]
   :steps [[:clean global-clean]
           [:init global-init]]
   :functions []})
