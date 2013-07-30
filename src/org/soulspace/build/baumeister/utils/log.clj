(ns org.soulspace.build.baumeister.utils.log
  (:use [clojure.string :only [join]]
        [org.soulspace.build.baumeister.config registry]))

; TODO finer grained log levels?
(def log-levels {:fatal 0 :error 1 :warn 2 :info 3 :debug 4 :trace 5})

(defn log [level & msgs]
  (let [msg-lvl (get log-levels (keyword level) 3)
        lvl (get log-levels (keyword (param :log-level)) 3)]
    (when (<= msg-lvl lvl)
      (println (keyword level) (join " " msgs)))))