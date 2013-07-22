[
 :log-level "info"
 :name "Baumeister"
 :project "org.soulspace.build"
 :project-lead "Ludger Solbach"
 :description "Baumeister Build System"
 :type "consolefrontend"
 :version "0.2.0"
 :author "Ludger Solbach"
 :provider "soulspace.org"
 :plugins ["global" "sdeps" "clojure" "package"]
; :license ["Eclipse Public License 1.0" "http://www.eclipse.org/legal/epl-v10.html"]
; :extra-repositories [[:maven-proxy :thirdparty "http://maven.alfresco.com/nexus/content/groups/public" "${baumeister-home-dir}/../repositories/maven.alfresco.com"]]
 :dependencies [[["org.clojure" "clojure" "1.5.1"] "runtime"]
                [["org.clojure" "data.xml" "0.0.7"]] ; maven support
                [["org.clojure" "data.zip" "0.1.1"]] ; maven support
                [["org.apache.ant" "ant-launcher" "1.8.3"]]
                [["org.apache.ant" "ant" "1.8.3"]]
                [["org.apache.ant" "ant-junit" "1.8.3"]] ; junit plugin
                [["org.apache.ant" "ant-jdepend" "1.8.3"]] ; jdepend plugin
                [["org.aspectj" "aspectjrt" "1.6.11"]] ; aspectj plugin
                [["net.sourceforge.cobertura" "cobertura" "1.9.4.1"]] ; cobertura plugin
                [["jdepend" "jdepend" "2.9.1"]] ; jdepend plugin
                [["net.sourceforge.pmd" "pmd" "5.0.0"]] ; pmd plugin
                [["jaxen" "jaxen" "1.1.4"]] ; transitive, pmd plugin
                [["asm" "asm" "3.3.1"]] ; transitive, cobertura/pmd/findbugs plugins
                [["asm" "asm-tree" "3.3.1"]] ; transitive, cobertura/findbugs plugins
                ; add additional findbugs dependencies (findbugs, findbugs-ant, ...)
                [["com.google.code.findbugs" "findbugs" "2.0.0"]] ; findbugs plugin
                [["com.google.code.findbugs" "findbugs-ant" "2.0.0"]] ; findbugs plugin
                [["com.google.code.findbugs" "jsr305" "2.0.0"]] ; findbugs plugin
                [["org.apache.bcel" "bcel" "5.2"]] ; transitive, findbugs plugin
                [["dom4j" "dom4j" "1.6.1"]] ; transitive, findbugs plugin
                [["com.puppycrawl.tools" "checkstyle" "5.5"]] ; checkstyle plugin
                [["oro" "oro" "2.0.8"]] ; transitive, mdsd/architecture/cobertura plugins
                [["org.soulspace.clj" "CljLibrary" "0.3.0"]] ; transitive?
                [["org.soulspace.clj" "CljJavaLibrary" "0.2.0"]]
                [["org.soulspace.clj" "CljXmlLibrary" "0.2.0"]] ; maven support
                [["org.soulspace.clj" "CljModelGenerator" "0.3.0"]] ; mdsd/architecture plugins
                [["org.soulspace.template" "TemplateEngine" "1.0.0"]] ; transitive, mdsd/architecture plugins
                [["org.soulspace.modelling" "ModelRepository2" "0.3.0"]] ; transitive, mdsd/architecture plugins
                [["org.soulspace.modelling" "UML14Repository" "0.3.0"]] ; transitive, mdsd/architecture plugins
                [["org.soulspace.modelling" "UML14ModelBuilder" "0.3.0"]]] ; transitive, mdsd/architecture plugins
 ]
