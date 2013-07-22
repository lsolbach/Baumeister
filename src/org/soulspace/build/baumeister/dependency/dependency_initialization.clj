(ns org.soulspace.build.baumeister.dependency.dependency-initialization
  (:use [clojure.java.io :only [as-file copy]]
        [org.soulspace.build.baumeister.config registry]
        [org.soulspace.build.baumeister.repository repositories artifact distribution]
        [org.soulspace.build.baumeister.dependency dependency dependency-initialization]
        [org.soulspace.build.baumeister.utils ant-utils checks log]))

(defn init-dependency [dependency]
  "Initialize the dependency for the build by copying or unzipping the referenced artifact."
  (let [artifact (:artifact dependency)
        src (query-artifact (param :deps-repositories) artifact)
        tgt (param (keyword (str "lib-" (:target dependency) "-dir")))]
    (log :debug "Copying" src " -> " tgt)
    (if (nil? src)
      (do
        (log :error (artifact-name artifact) "not found in repositories!"))
      ; (throw (RuntimeException. (str "Error: " (artifact-name artifact) " not found!")))
      (cond
        (copy? dependency)
        (copy src (as-file (str tgt "/" (artifact-name artifact))))
        (unzip? dependency)
        (ant-unzip {:src src :dest tgt :overwrite "true"})
        :default
        (log :error "Could not handle dependency " dependency)))))

