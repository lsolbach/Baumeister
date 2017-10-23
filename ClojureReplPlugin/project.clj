(defproject org.soulspace.baumeister/ClojureReplPlugin "0.7.0"
  :description "Plugin providing a Clojure REPL for the Baumeister build system"
  :url "https://github.com/lsolbach/Baumeister"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.nrepl "0.2.3"]]
  :dev-dependencies [[org.soulspace.baumeister/Baumeister "0.7.0"]]
  :test-paths ["unittest"])
