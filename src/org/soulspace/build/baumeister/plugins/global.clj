(ns org.soulspace.build.baumeister.plugins.global
  (:use [clojure.java.io :exclude [delete-file]] 
        [org.soulspace.clj file]
        [org.soulspace.build.baumeister.utils ant-utils log]
        [org.soulspace.build.baumeister.config registry]))

(defn global-clean []
  (log :info "cleaning globally...")
  (delete-dir (as-file (param :lib-dir)))
  (delete-dir (as-file (param :dist-dir)))
  (delete-dir (as-file (param :build-dir))))

(defn global-init []
  (log :info "initializing globally...")
  (create-dir (as-file (param :build-dir)))
  (create-dir (as-file (param :build-classes-dir)))
  (create-dir (as-file (param :build-unittest-classes-dir)))
  (create-dir (as-file (param :build-integrationtest-classes-dir)))
  (create-dir (as-file (param :build-acceptancetest-classes-dir)))
  (create-dir (as-file (param :build-report-dir)))
  (create-dir (as-file (param :lib-dir)))
  (create-dir (as-file (param :dist-dir))))

(defn plugin-init []
  (log :info "initializing plugin global")
  (register-vars [[:module-dir "."]
                  [:lib-dir "${module-dir}/lib"]
                  [:dist-dir "${module-dir}/dist"]
                  [:build-dir "${module-dir}/build"]
                  [:build-classes-dir "${build-dir}/classes"]
                  [:build-unittest-classes-dir "${build-dir}/unittest/classes"]
                  [:build-integrationtest-classes-dir "${build-dir}/integrationtest/classes"]
                  [:build-acceptancetest-classes-dir "${build-dir}/acceptancetest/classes"]
                  [:build-report-dir "${build-dir}/report"]])
  (register-fns [[:clean global-clean]
                 [:init global-init]]))
