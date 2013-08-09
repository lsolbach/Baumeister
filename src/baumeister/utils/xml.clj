;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.utils.xml
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
