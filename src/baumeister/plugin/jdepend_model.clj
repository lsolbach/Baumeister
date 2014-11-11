;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.plugin.jdepend-model
    (:require [clojure.zip :as zip]
            [clojure.data.xml :as xml]
            [clojure.data.zip :as zf]
            [clojure.data.zip.xml :as zx]))

(defn parse-stats
  "Returns a map with the statistics data."
  [zipper]
  {:total-classes (zx/xml1-> zipper :TotalClasses zx/text)
   :concrete-classes (zx/xml1-> zipper :ConcreteClasses zx/text)
   :abstract-classes (zx/xml1-> zipper :AbstractClasses zx/text)
   :ca (zx/xml1-> zipper :Ca zx/text)
   :ce (zx/xml1-> zipper :Ce zx/text)
   :a (zx/xml1-> zipper :A zx/text)
   :i (zx/xml1-> zipper :I zx/text)
   :d (zx/xml1-> zipper :D zx/text)
   :v (zx/xml1-> zipper :V zx/text)})

(defn parse-class
  "Returns a map with the class data."
  [zipper]
  {:source-file (zx/attr zipper :sourceFile)
   :class (zx/xml1-> zipper zx/text)})

(defn parse-abstract-classes
  "Returns a sequence with the abstract classes of the current package."
  [zipper]
  (map parse-class (zx/xml-> zipper :Class)))

(defn parse-concrete-classes
  "Returns a sequence with the concrete classes of the current package."
  [zipper]
  (map parse-class (zx/xml-> zipper :Class)))

(defn parse-depends-upon
  "Returns a sequence with the packages the current package depends upon."
  [zipper]
  (zx/xml-> zipper :Package zx/text))

(defn parse-used-by
  "Returns a sequence with the packages that use the current package."
  [zipper]
  (zx/xml-> zipper :Package zx/text))

(defn parse-package
  "Returns a map with the package data."
  [zipper]
  {:error (zx/xml1-> zipper :error zx/text)
   :stats (parse-stats (zx/xml1-> :Stats))
   :abstract-classes (parse-class (zx/xml1-> :AbstractClasses))
   :concrete-classes (parse-class (zx/xml1-> :ConcreteClasses))
   :depends-uppon (parse-depends-upon (zx/xml1-> :DependsUpon))
   :used-by (parse-used-by (zx/xml1-> :UsedBy))})

(defn parse-packages
  "Returns a sequence of packages."
  [zipper]
  (map parse-package (zx/xml-> :Package)))

(defn parse-package-cycle
  "Returns a map with the package cycle."
  [zipper]
  {:name (zx/attr zipper :Name)
   :packages (zx/xml-> zipper zx/text)})

(defn parse-cycles
  "Returns a sequence of the cycles."
  [zipper]
  (map parse-package-cycle (zx/xml-> :Package)))

(defn parse-jdepend
  "Returns a map with the jdepend data."
  [zipper]
  {:packages (parse-packages (zx/xml1-> zipper :Packages))
   :cycles (parse-cycles (zx/xml1-> zipper :Cycles))})
