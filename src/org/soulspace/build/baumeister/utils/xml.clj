(ns org.soulspace.build.baumeister.utils.xml
  (:require [clojure.zip :as zip]
            [clojure.data.xml :as xml]
            [clojure.data.zip :as zf]
            [clojure.data.zip.xml :as zx])
  (:use [clojure.java.io :exclude [delete-file]]
        [org.soulspace.clj string]))


(defmulti xml-zipper class)
(defmethod xml-zipper java.io.File [pom-file]
  (zip/xml-zip (xml/parse (reader pom-file))))
(defmethod xml-zipper String [str]
  (if (starts-with "<" str)
    (zip/xml-zip (xml/parse-str str)) ; handle as xml string
    (zip/xml-zip (xml/parse (reader str))))); haandle as file name
