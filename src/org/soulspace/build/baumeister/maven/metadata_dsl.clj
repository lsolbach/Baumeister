(ns org.soulspace.build.baumeister.maven.metadata-dsl
  (:refer-clojure :exclude [filter name type])
  (:use [org.soulspace.clj.xml dsl-builder]))

;
; DSL for Maven metadata model 1.1.0
;
(deftags "md" ["artifactId" "buildNumber" "classifier" "extension" "groupId" "lastUpdated" "latest"
               "localCopy" "name" "plugin" "plugins" "prefix" "release" "snapshot" "snapshotVersion"
               "snapshotVersions" "timestamp" "updated" "value" "version" "versioning" "versions"])

(defroottags "md" "http://maven.apache.org/METADATA/1.1.0" ["metadata"])

