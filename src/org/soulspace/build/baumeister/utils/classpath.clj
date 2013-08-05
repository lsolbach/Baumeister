;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns org.soulspace.build.baumeister.utils.classpath
  (:require [dynapath.util :as dp])
  (:use [clojure.java.io :only [as-url]])
  (:import [java.net URL URLClassLoader]
           [clojure.lang DynamicClassLoader]))

; TODO move into CljAppFramework

(defn context-classloader []
  "Returns the context class loader of the current thread."
  (.getContextClassLoader (java.lang.Thread/currentThread)))

(defn system-classloader []
  "Returns the system class loader."
  (ClassLoader/getSystemClassLoader))

(defn classloader-hierarchy
  "Returns the sequence of classloaders."
  ([]
    (classloader-hierarchy (context-classloader)))
  ([cl]
    (loop [current-cl cl cl-seq []]
      (if-not (nil? current-cl)
        (recur (.getParent current-cl) (conj cl-seq current-cl))))))

(defn create-url-classloader
  "Creates an URLClassLoader with the given array of URLs."
  ([]
    (URLClassLoader. (make-array URL 0)))
  ([urls]
    (URLClassLoader. urls))
  ([urls parent-cl]
    (URLClassLoader. urls parent-cl)))

(defn create-dynamic-classloader
  "Creates an URLClassLoader with the given array of URLs."
  ([]
    (DynamicClassLoader.))
  ([parent-cl]
    (DynamicClassLoader. parent-cl)))

(defn add-url
  "Adds a classpath URL to a dynamic classloader."
  ([url]
    (add-url (context-classloader) url))
  ([cl url]
    (dp/add-classpath-url cl (as-url url))))

(defn urls
  "Returns the classpath URLs of a dynamic classloader."
  ([]
    (urls (context-classloader)))
  ([cl]
    (dp/classpath-urls cl)))


(defn set-dynamic-classloader
  "Sets a dynamic context classloader on the current thread,
if it is not dynamic already."
  []
  (let [cl (context-classloader)]
    (if (or (= (type cl) URLClassLoader) (= (type cl) DynamicClassLoader))
      (let [dyn-cl (create-dynamic-classloader cl)]
        (.setContextClassLoader (java.lang.Thread/currentThread) dyn-cl)))))

