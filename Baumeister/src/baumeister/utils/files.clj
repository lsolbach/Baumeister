;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.utils.files
  (:use [clojure.java.io :exclude [delete-file]]
        [clojure.string :only [split join]]
        [org.soulspace.clj file file-search]
        [baumeister.utils checks]
        [baumeister.config registry]))

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
    (existing-dirs [source-dir-name (str (param :generation-dir) "/" source-dir-name)])))

(defn source-path []
  (cond
    (plugin? "aspectj") (param :aspectj-source-path)
    (plugin? "clojure") (param :clojure-source-path)
    (plugin? "java") (param :java-source-path)))
