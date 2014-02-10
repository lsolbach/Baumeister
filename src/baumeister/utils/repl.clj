;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.utils.repl
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
       repl/response-values)))
