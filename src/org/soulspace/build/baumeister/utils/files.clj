(ns org.soulspace.build.baumeister.utils.files
  (:use [clojure.java.io :exclude [delete-file]]
        [clojure.string :only [split join]]
        [org.soulspace.clj.lib file file-search function]
        [org.soulspace.build.baumeister.utils checks]
        [org.soulspace.build.baumeister.config registry]))

(defn jar-path [dir-path]
  "Builds a path with all jar files in the directory."
  (build-path (existing-files-on-path "jar" dir-path)))

(defn lib-path [coll]
  (join ":" (map #(str (get-lib-dir) "/" % ".jar") coll)))

(defn class-path [coll]
  "Builds a path by joining all non empty path components separated with ':'."
  (join ":" (filter #(not (empty? %)) coll)))

(defn source-path []
  (cond
    (plugin? "aspectj") (param :aspectj-source-path)
    (plugin? "clojure") (param :clojure-source-path)
    (plugin? "java") (param :java-source-path)))
