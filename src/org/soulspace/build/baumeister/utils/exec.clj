(ns org.soulspace.build.baumeister.utils.exec
    (:use [clojure.java.shell]))

(defn execute [& args]
  (apply sh args))