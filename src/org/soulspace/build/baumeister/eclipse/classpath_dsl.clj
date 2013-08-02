(ns org.soulspace.build.baumeister.eclipse.classpath-dsl
  (:use [org.soulspace.clj.xml dsl-builder]))

(deftags "cp" ["attribute" "attributes" "classpathentry"])
(defroottags "cp" "" ["classpath"])


