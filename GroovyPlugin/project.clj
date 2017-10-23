(defproject org.soulspace.baumeister/ClojurePlugin "0.7.0"
  :description "Groovy compiler plugin for the Baumeister build system"
  :url "https://github.com/lsolbach/Baumeister"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 ["org.codehaus.groovy/groovy-all, 2.2.0"]]
  :dev-dependencies [[org.soulspace.baumeister/Baumeister "0.7.0"]]
  :test-paths ["unittest"])
