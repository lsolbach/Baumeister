(ns org.soulspace.build.baumeister.plugins.global
  (:use [clojure.java.io :exclude [delete-file]] 
        [org.soulspace.clj file]
        [org.soulspace.build.baumeister.utils ant-utils log]
        [org.soulspace.build.baumeister.config registry plugin-registry]))

(defn global-clean
  "Global clean"
  []
  (log :info "cleaning globally...")
  (delete-dir (as-file (param :lib-dir)))
  (delete-dir (as-file (param :dist-dir)))
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
  (create-dir (as-file (param :dist-dir))))

(def config
  {:params [[:module-dir "."]
            [:build-dir "${module-dir}/build"]
            [:lib-dir "${build-dir}/lib"]
            [:dist-dir "${build-dir}/dist"]
            [:build-classes-dir "${build-dir}/classes"]
            [:build-unittest-classes-dir "${build-dir}/unittest/classes"]
            [:build-integrationtest-classes-dir "${build-dir}/integrationtest/classes"]
            [:build-acceptancetest-classes-dir "${build-dir}/acceptancetest/classes"]
            [:build-report-dir "${build-dir}/report"]]
   :functions [[:clean global-clean]
               [:init global-init]]})
