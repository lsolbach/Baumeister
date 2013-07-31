(ns org.soulspace.build.baumeister.plugins.depsdot
  (:use [org.soulspace.build.baumeister.utils log]
        [org.soulspace.build.baumeister.config registry plugin-registry]
        [org.soulspace.build.baumeister.dependency dependency dependency-node dependency-dot]
        ))

;
; generate dot graphs for the configured dependency tree
;
(defn depsdot-dependencies []
  (log :debug "doing depsdot-dependencies")
  (let [writer (java.io.StringWriter.)]
    (if-not (nil? (param :dependencies-tree))
      (dependencies-dot writer (param :dependencies-tree))
      (dependencies-dot writer (build-dependency-tree)))
    (spit (param "${deps-report-dir}/dependencies.dot") (str writer))))

(def depsdot-config
  {:params [[:deps-report true]
            [:deps-report-dir "${build-report-dir}/dependencies"]]
   :functions [[:dependencies depsdot-dependencies]]})

;
; plugin initialization
;
(defn plugin-init []
  (log :info "initializing plugin depsdot")
  (register-plugin "depsdot")
  (register-vars (:params depsdot-config))
  (register-fns (:functions depsdot-config)))
