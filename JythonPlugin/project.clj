(defproject org.soulspace.baumeister/JythonPlugin "0.7.0"
  :description "Jython plugin for the Baumeister build system"
  :url "https://github.com/lsolbach/Baumeister"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.python/jython "2.7.0" :excludes [org.apache.ant/ant]]]
  :dev-dependencies [[org.soulspace.baumeister/Baumeister "0.7.0"]]
  :test-paths ["unittest"])
