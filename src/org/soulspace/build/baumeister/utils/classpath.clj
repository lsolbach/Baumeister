(ns org.soulspace.build.baumeister.utils.classpath
  (:require [dynapath.util :as dp])
  (:use [clojure.java.io :only [as-url]]
        [org.soulspace.build.baumeister.utils log]) ; TODO use tools.logging when extracting to lib
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
      (log :debug "CL dynamic" cl)
      (let [dyn-cl (create-dynamic-classloader cl)]
        (log :debug  "CL not dynamic" cl)
        (.setContextClassLoader (java.lang.Thread/currentThread) dyn-cl)
        (log :debug  "CL installed" (context-classloader))))))

