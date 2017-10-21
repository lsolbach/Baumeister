(defproject org.soulspace.baumeister/CheckstylePlugin "0.7.0"
  :description "CheckStyle statical code analysis plugin for the Baumeister build system"
  :url "https://github.com/lsolbach/Baumeister"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.puppycrawl.tools/checkstyle "5.5"]]
  :dev-dependencies [["org.soulspace.baumeister/Baumeister, 0.7.0"]]
  :test-paths ["unittest"])