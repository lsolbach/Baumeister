;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.config.registry
  (:require [clojure.string :as str :only [join]])
  (:use [clojure.java.io :only [as-url]]
        [org.soulspace.clj.application classpath]
        [org.soulspace.clj file]
        [baumeister.config parameter-registry]))

(defn get-classpath-urls
  "Returns the registered urls of the classpath."
  []
  (urls))

(defn register-classpath-urls
  "Register classpath urls."
  [cl-urls]
  (let [urls (into #{} (get-classpath-urls))]
    (doseq [url cl-urls]
      (if-not (contains? urls url)
        (add-url url)))))

(defn register-classpath-entries
  "Register classpath entries."
  [cl-entries]
  (let [urls (into #{} (get-classpath-urls))]
    (doseq [entry cl-entries]
      (let [url (as-url (canonical-file entry))]
        (if-not (contains? urls url)
          (add-url url))))))
  
; TODO returns a parameter as-is without property replacement. still needed? if so, choose new fn name
(defn get-var
  "Get parameter without replacements."
  ([name] (get (get-param-registry) name ""))
  ([name default] (get (get-param-registry) name default)))

(defn param
  "Get parameter with replacements."
  ([k]
    (if (keyword? k)
      (get-var k)
      (replace-vars k)))
  ([k default]
    (if (keyword? k)
      (get-var k default)
      (replace-vars k))))

(defn register-val [key value]
  "Register a value."
  (register-param-as-is key value))

(defn register-var
  "Register a variable."
  [key value]
  (register-param key value))

(defn update-var
  "Update a variable by adding the values."
  [key value]
  (let [var (param key)]
    (cond
      (vector? var)
      (register-var key (into var value))
      (set? var)
      (register-var key (into var value))
      )
  ))

(defn register-vars
  "Register variables."
  [vars]
  (register-params vars))
