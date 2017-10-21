(defproject org.soulspace.baumeister/CoberturaPlugin "0.7.0"
  :description "Cobertura plugin for measuring the code coverage with the Baumeister build system"
  :url "https://github.com/lsolbach/Baumeister"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [net.sourceforge.cobertura/cobertura "1.9.4.1" :exclusions [org.apache.ant/ant]]]
  :dev-dependencies [["org.soulspace.baumeister/Baumeister, 0.7.0"]]
  :test-paths ["unittest"])
