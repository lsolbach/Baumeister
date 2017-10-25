(defproject org.soulspace.baumeister/PlantUMLPlugin "0.7.0"
  :description "Clojure compiler plugin for the Baumeister build system"
  :url "https://github.com/lsolbach/Baumeister"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [net.sourceforge.plantuml/plantuml "7991" :excludes [org.apache.ant/ant]]]
  :dev-dependencies [[org.soulspace.baumeister/Baumeister "0.7.0"]]
  :test-paths ["unittest"])
