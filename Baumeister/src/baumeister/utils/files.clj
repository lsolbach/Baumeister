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
  (:require [clojure.string :as str]
            [org.soulspace.clj.file :as file]
            [baumeister.config.registry :as reg])
  (:use [baumeister.utils checks]
        ))

(defn existing-dirs
  "Returns only the existing directories of a sequence of directories."
  [dirs]
  (remove #(not (file/exists? %)) dirs))

(defn jar-path
  "Builds a path with all jar files in the directory."
  [dir-path]
  (file/build-path (file/existing-files-on-path "jar" dir-path)))

(defn class-path
  "Builds a path by joining all non empty path components separated with ':'."
  [coll]
  (str/join ":" (filter #(not (empty? %)) coll)))

(defn dir-path
  "Builds a path by joining all non empty path components separated with ':'."
  [coll]
  (str/join ":" (filter #(not (empty? %)) coll)))

(defn source-dirs
  "Returns a list of source dirs for a given directory parameter key."
  [dir-key]
  (let [source-dir-name (reg/param dir-key)]
    (existing-dirs [source-dir-name (str (reg/param :generation-dir) "/" source-dir-name)])))

(defn source-path
  []
  (cond
    (plugin? "aspectj") (reg/param :aspectj-source-path)
    (plugin? "clojure") (reg/param :clojure-source-path)
    (plugin? "java") (reg/param :java-source-path)))
