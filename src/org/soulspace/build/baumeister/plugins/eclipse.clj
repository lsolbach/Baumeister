(ns org.soulspace.build.baumeister.plugins.eclipse
  (:require [clojure.zip :as zip]
            [clojure.data.xml :as xml]
            [clojure.data.zip :as zf]
            [clojure.data.zip.xml :as zx]
            [org.soulspace.build.baumeister.eclipse.classpath-dsl :as cp])
  (:use [clojure.java.io :only [as-file]]
        [org.soulspace.clj file]
        [org.soulspace.clj.xml dsl-builder xml-util]
        [org.soulspace.build.baumeister.config registry plugin-registry]
        [org.soulspace.build.baumeister.repository artifact]
        [org.soulspace.build.baumeister.utils checks log message xml]
        ))

(def eclipse-source-dirs
  [(param "${source-dir}")
   (param "${source-unittest-dir}")
   (param "${source-integrationtest-dir}")
   (param "${source-acceptancetest-dir}")
   (param "${mdsd-generation-dir}/${source-dir}")
   (param "${mdsd-generation-dir}/${source-unittest-dir}")
   (param "${mdsd-generation-dir}/${source-integrationtest-dir}")
   (param "${mdsd-generation-dir}/${source-acceptancetest-dir}")                        
   ])

(def eclipse-containers
  [["org.eclipse.jdt.launching.JRE_CONTAINER"]
   ["org.eclipse.ajdt.core.ASPECTJRT_CONTAINER"]
   ["org.eclipse.jdt.junit.JUNIT_CONTAINER/4"]
   ["org.eclipse.jst.j2ee.internal.web.container"]
   ["org.eclipse.jst.j2ee.internal.module.container"]
   ["org.eclipse.jst.server.core.container/org.eclipse.jst.server.tomcat.runtimeTarget/Apache Tomcat v7.0"
    [{:name "owner.project.facets" :value "jst.web"}]]
   ])

(def cpxml
  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<classpath>
	<classpathentry kind=\"src\" path=\"src\"/>
	<classpathentry kind=\"src\" path=\"unittest\"/>
	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>
	<classpathentry kind=\"lib\" path=\"build/lib/runtime/ant-jdepend.jar\"/>
	<classpathentry kind=\"lib\" path=\"build/lib/runtime/ant-junit.jar\"/>
	<classpathentry kind=\"lib\" path=\"build/lib/runtime/ant-launcher.jar\"/>
	<classpathentry kind=\"output\" path=\"bin\"/>
</classpath>
")

(def cpzip (zip/xml-zip (xml/parse-str cpxml)))

(println "ROOT" (zx/xml-> cpzip))
(println "CP" (zx/xml-> cpzip :classpath))

(defn build-container-entries []
  ; TODO zip from .classpath and include again?
  (let [zipper (zip/xml-zip (xml/parse-str cpxml))
        ;zipper (xml-zipper (param "${module-dir}/data/.classpath"))
        conts (zx/xml-> zipper :classpath :classpathentry [(zx/attr= :kind "con")])]
    (println "ROOT" (zx/xml-> zipper))
    (println "CP" (zx/xml-> zipper :classpath))
    (println "CONTS" conts)
    )
  )

(defn build-source-entries []
    (doall
      (->>
        (remove #(not (exists? %)) eclipse-source-dirs)
        (map #(apply hash-map [:kind "src" :path %]))
        (map #(cp/classpathentry %)))))
    
(defn code-dependency?
  "Returns true, if the depedency is a code library."
  [dep]
  (contains? #{:runtime :aspect :dev :aspectin} (:target dep)))

(defn lib-target-path [dep]
  (str (param (keyword (str "lib-" (name (:target dep)) "-dir"))) "/" (artifact-name (:artifact dep))))

(defn web-lib-entry [dep]
  (cp/classpathentry
    {:kind "lib"
     :path (lib-target-path dep)}
    (cp/attributes
      {}
      (cp/attribute {:name "org.eclipse.jst.component.dependency" :value "WEB-INF/lib"}))))

(defn lib-entry [dep]
  (cp/classpathentry {:kind "lib" :path (lib-target-path dep)}))

(defn build-lib-entries []
  (let [deps (filter code-dependency? (param :dependencies-processed))]
    (if (web-module?)
      (map web-lib-entry deps)
      (map lib-entry deps))))

(defn build-classpath []
  (cp/classpath
    {}
    (build-source-entries)
    (build-lib-entries)
    (cp/classpathentry {:kind "output" :path "bin"})
    ))

(defn eclipse-init []
  (create-dir (as-file (param "${build-eclipse-dir}"))))

(defn eclipse-post-dependencies
  []
  (build-container-entries)
  ;(println (emit-str (build-classpath)))
  (spit (param "${build-eclipse-dir}/classpath.xml") (xml/emit-str (build-classpath))))

(def config 
  {:params [[:build-eclipse-dir "${build-dir}/eclipse"]]
   :functions [[:init eclipse-init]
               [:post-dependencies eclipse-post-dependencies]]})
