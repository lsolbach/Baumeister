(ns org.soulspace.build.baumeister.utils.repl
  (:require [clojure.tools.nrepl :as repl])
  (:use [clojure.tools.nrepl.server :only [start-server stop-server]]))
  
(def port 59258)
(def ^{:dynamic true :private true} repl-server)

(defn start-repl 
  []
  (def repl-server (start-server :port port)))

(defn stop-repl 
  []
  (stop-server repl-server))


(defn repl-eval
  [& forms]
  (with-open [conn (repl/connect :port port)]
     (-> (repl/client conn 1000)    ; message receive timeout required
       (repl/message {:op "eval" :code (str forms)})
       repl/response-values))
  )