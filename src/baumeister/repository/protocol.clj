;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.repository.protocol)

;
; Repository protocols
;

; TODO add usage type of repository ("dev" "release" "thirdparty"), add (usage-type [repo] "Returns the usage type of the repository") 
(defprotocol ArtifactRepository
  "Protocol for an artifact repository."
  (artifact-folder [repo artifact] "Get the folder of this artifact in this repository.")
  (get-artifact [repo artifact] "Get an artifact from the artifact repository.")
  (get-dependencies-for-artifact [repo artifact] "Get the dependencies for an artifact from the artifact repository.")
  (put-artifact [repo artifact artifact-src] "Put an artifact-src as an artifact into the artifact repository."))

(defprotocol FileArtifactRepository
  "Protocol for a file based repository."
  (project-dir [repo artifact] "Get the directory of the project of this artifact in this repository.")
  (module-dir [repo artifact] "Get the directory of the module of this artifact in this repository.")
  (artifact-dir [repo artifact] "Get the directory of this artifact in this repository.")
  (artifact-file [repo artifact] "Get the file for the artifact in this repository."))

(defprotocol HttpArtifactRepository
  "Protocol for a http based repository."
  (project-dir-url [repo artifact] "Get the url for the project of the artifact in this repository.")
  (module-dir-url [repo artifact] "Get the url for the module of the artifact in this repository.")
  (artifact-dir-url [repo artifact] "Get the url for directory of the artifact in this repository.")
  (artifact-file-url [repo artifact] "Get the url for the artifact in this repository."))

(defprotocol ProxyArtifactRepository
  "Protocol for a proxy/cache repository."
  (local-hit? [repo artifact] "Test for a local hit for this artifact.")
  (remote-hit? [repo artifact] "Test for a remote hit for this artifact.")
  (cache-artifact [repo artifact] "Copies the artifact from the remote site to the local cache."))

(defprotocol BaumeisterArtifactRepository
  "Protocol for a Baumeister artifact repository."
  (module-artifact [repo artifact] "Get the module artifact for this artifact."))

(defprotocol MavenArtifactRepository
  "Protocol for a maven2 artifact repository."
  (maven-name [repo artifact] "Get the maven name of this artifact.")
  (pom-artifact [repo artifact] "Create the maven POM artifact for this artifact.")
  (get-pom [repo artifact] "Get the POM with parent POMs if any.")
  (metadata-folder [repo artifact] "Get the folder for the metadata of this artifact in this repository.")
  (get-metadata [repo artifact] "Get the repositor metadata for this artifact.")
  )

(defprotocol VersionedArtifactRepository
  "Protocol for a repository which stores versioned artifacts."
  (versions [repo artifact] "Get all versions of this artifact.")
  (latest? [repo artifact] "True, if no newer version of this artifact is available.")
  (latest-version [repo artifact] "Get the latest version for this artifact.")
  (latest-artifact [repo artifact] "Get the artifact with the latest version, returns the artifact.")
  (find-artifact [repo artifact] "Find an artifact in the the artifact repository. Returns the artifact with the latest matching version.")
  )
