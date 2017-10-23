(defproject org.soulspace.baumeister/FindbugsPlugin "0.7.0"
  :description "FindBugs statical code analysis plugin for the Baumeister build system"
  :url "https://github.com/lsolbach/Baumeister"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.google.code.findbugs/findbugs-ant "2.0.0" :excludes [org.apache.ant/ant]]]
  :dev-dependencies [["org.soulspace.baumeister/Baumeister, 0.7.0"]]
  :test-paths ["unittest"])
