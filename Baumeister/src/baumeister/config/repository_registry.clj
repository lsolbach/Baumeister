;
;   Copyright (c) Ludger Solbach. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file license.txt at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.
;
(ns baumeister.config.repository-registry
  (:use [baumeister.repository protocol file httpproxy mavenproxy]
        [baumeister.config registry])
  (:import [baumeister.repository.file FileArtifactRepositoryImpl]
           [baumeister.repository.httpproxy HttpProxyArtifactRepositoryImpl]
           [baumeister.repository.mavenproxy MavenProxyArtifactRepositoryImpl]))

(def ^{:dynamic true :private true} repositories [])

;
; repository registry
;
(defmulti create-repository first)
(defmethod create-repository :file [opts]
  (let [[_ usage path] opts]
    (FileArtifactRepositoryImpl. usage (param path))))
(defmethod create-repository :http-proxy [opts]
  (let [[_ usage url path] opts] 
    (HttpProxyArtifactRepositoryImpl. usage url (param path))))
(defmethod create-repository :maven-proxy [opts]
  (let [[_ usage url path] opts] 
    (MavenProxyArtifactRepositoryImpl. usage url (param path))))

(defn create-repositories [v]
  (map create-repository v))
