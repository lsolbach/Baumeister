(ns org.soulspace.build.baumeister.repository.repository-protocol)

;
; Repository protocols
;

; TODO add usage type of repository ("dev" "release" "thirdparty"), add (usage-type [repo] "Returns the usage type of the repository") 
(defprotocol ArtifactRepository
  "Protocol for an artifact repository."
  (artifact-folder [repo artifact] "Get the folder of this artifact in this repository.")
  (get-artifact [repo artifact] "Get an artifact from the artifact repository.")
;  (module-definition [repo artifact] "Check for dependencies")
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
  (module-artifact [repo artifact] "Get the module artifact for this artifact."))

(defprotocol MavenArtifactRepository
  "Protocol for a maven2 artifact repository."
  (artifact-mvn-name [repo artifact] "Get the maven name of this artifact.")
  (pom-artifact [repo artifact] "Create the maven POM artifact for this artifact.")
  (get-pom-artifact [repo artifact] "Parse and return the POM")
  (has-parent-pom? [repo artifact] "Returns true if the POM has defined a parent POM.")
  )

(defprotocol VersionedArtifactRepository
  "Protocol for a repository which stores versioned artifacts."
  (get-versions-for-artifact [repo artifact] "Get all versions of this artifact.")
  ;(newer-artifact-available? [repo artifact] "True, if a newer version of this artifact is available.")
  ;(get-latest-version-of-artifact [repo artifact] "Get the latest version for this artifact, returns a version.")
  ;(get-latest-version-of-artifact [repo artifact] "Get the latest version of this artifact, returns the artifact.")
  )

;(defn newer-artifact-available? [repo artifact])
;(defn get-latest-version-for-artifact [repo artifact])
;(defn get-latest-version-of-artifact [repo artifact])

(defmulti create-repository first)
