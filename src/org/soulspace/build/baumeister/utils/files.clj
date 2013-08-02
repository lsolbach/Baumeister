(ns org.soulspace.build.baumeister.utils.files
  (:use [clojure.java.io :exclude [delete-file]]
        [clojure.string :only [split join]]
        [org.soulspace.clj file file-search function]
        [org.soulspace.build.baumeister.utils checks]
        [org.soulspace.build.baumeister.config registry]))

(defn existing-dirs
  "Returns only the existing directories of a sequence of directories."
  [dirs]
  (remove #(not (exists? %)) dirs))

(defn jar-path [dir-path]
  "Builds a path with all jar files in the directory."
  (build-path (existing-files-on-path "jar" dir-path)))

(defn class-path [coll]
  "Builds a path by joining all non empty path components separated with ':'."
  (join ":" (filter #(not (empty? %)) coll)))

(defn dir-path [coll]
  "Builds a path by joining all non empty path components separated with ':'."
  (join ":" (filter #(not (empty? %)) coll)))

(defn source-dirs
  "Returns a list of source dirs for a given directory parameter key."
  [dir-key]
  (let [source-dir-name (param dir-key)]
    (println "Source Dir Name" source-dir-name)
    (existing-dirs [source-dir-name (str (param :generation-dir) "/" source-dir-name)])))

(defn source-path []
  (cond
    (plugin? "aspectj") (param :aspectj-source-path)
    (plugin? "clojure") (param :clojure-source-path)
    (plugin? "java") (param :java-source-path)))
