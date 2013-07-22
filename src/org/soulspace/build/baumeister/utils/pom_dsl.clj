(ns org.soulspace.build.baumeister.utils.pom-dsl
  (:refer-clojure :exclude [filter name type])
  (:use [org.soulspace.clj.xml dsl-builder]))

(deftags "pom"
  ["activation" "activeByDefault" "address" "arch" "archive" "artifactId"
   "build" "checksumPolicy" "ciManagement" "classifier" "comments"
   "configuration" "contributor" "contributors" "defaultGoal" "dependencies"
   "dependency" "dependencyManagement" "description" "developer"
   "developerConnection" "developers" "directory" "distribution"
   "distributionManagement" "downloadUrl" "email" "enabled" "exclude"
   "excludeDefaults" "excludes" "exclusion" "exclusions" "execution"
   "executions" "exists" "extension" "extensions" "family" "file" "filter"
   "filtering" "filters" "finalName" "goal" "goals" "groupId" "id"
   "inceptionYear" "include" "includes" "inherited" "issueManagement" "jdk"
   "layout" "license" "licenses" "mailingList" "mailingLists" "maven"
   "message" "missing" "modelVersion" "module" "modules" "name" "notifier"
   "notifiers" "optional" "organization" "organizationUrl" "os"
   "otherArchive" "otherArchives" "outputDirectory" "packaging" "parent"
   "phase" "plugin" "pluginManagement" "pluginRepositories"
   "pluginRepository" "plugins" "post" "prerequisites" "profile" "profiles"
   "properties" "relativePath" "releases" "relocation" "reporting"
   "reportSet" "reportSets" "repositories" "repository" "resource"
   "resources" "role" "roles" "scm" "scope" "scriptSourceDirectory"
   "sendOnError" "sendOnFailure" "sendOnSuccess" "sendOnWarning" "site"
   "snapshotRepository" "snapshots" "sourceDirectory" "status" "subscribe"
   "system" "systemPath" "tag" "targetPath" "testOutputDirectory" "testResource"
   "testResources" "testSourceDirectory" "timezone" "type" "uniqueVersion"
   "unsubscribe" "updatePolicy" "url" "value" "version"])

(defroottags "pom" "http://maven.apache.org/POM/4.0.0" ["project"])
