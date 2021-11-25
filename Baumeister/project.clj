(defproject org.soulspace.baumeister/Baumeister "0.7.0"
  :description "Baumeister Build System"
  :url "https://github.com/lsolbach/Baumeister"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.soulspace.clj/CljApplicationLibrary "0.6.0"]
                 [org.soulspace.clj/CljArtifactLibrary "0.4.5"]
                 [org.soulspace.clj/CljMavenLibrary "0.5.3"]
                 [org.soulspace.clj/CljJavaLibrary "0.7.0"]
                 [org.soulspace.clj/CljXmlLibrary "0.4.3"] ; xml generation, maven pom, eclipse
                 [org.clojure/data.zip "0.1.1"] ; maven pom support
                 [org.clojure/tools.reader "0.8.3"] ; edn reader
                 [org.apache.ant/ant-junit "1.8.3"]]
  :test-paths ["unittest"])
