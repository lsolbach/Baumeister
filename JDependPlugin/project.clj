(defproject org.soulspace.baumeister/JDependPlugin "0.7.0"
  :description "JDepend statical code analysis plugin for the Baumeister build system"
  :url "https://github.com/lsolbach/Baumeister"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.apache.ant/ant-jdepend "1.8.3" :excludes [org.apache.ant/ant]]
                 [jdepend/jdepend "2.9.1"]]
  :dev-dependencies [[org.soulspace.baumeister/Baumeister "0.7.0"]]
  :test-paths ["unittest"])
