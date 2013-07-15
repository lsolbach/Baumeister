(ns org.soulspace.build.baumeister.utils.ant-utils
  (:use [clojure.java.io :only [as-file]]
        [org.soulspace.clj.java type-conversion beans]
        [org.soulspace.build.baumeister.utils log])
  (:import [java.util.concurrent CountDownLatch]
           [org.apache.tools.ant.types Path]
           [org.apache.tools.ant.taskdefs Manifest$Attribute]
           [java.util Map]))

(def #^{:doc "Dummy ant project to keep Ant tasks happy"}
  ant-project
  (let [proj (org.apache.tools.ant.Project.)
        logger (org.apache.tools.ant.NoBannerLogger.)]
    (doto logger
      (.setMessageOutputLevel org.apache.tools.ant.Project/MSG_INFO)
      (.setEmacsMode true)
      (.setOutputPrintStream System/out)
      (.setErrorPrintStream System/err))
    (doto proj
      (.init)
      (.addBuildListener logger))))

; extend coerce from type-conversion
(defmethod coerce [Boolean/TYPE String] [_ str]
  (contains? #{"on" "yes" "true"} (.toLowerCase str)))
(defmethod coerce [Path String] [_ str]
  (Path. ant-project str))

(def ant-task-hierarchy
  (atom (-> (make-hierarchy)
          (derive ::exec ::has-args)
          (derive ::java ::has-args)
          (derive ::war ::zip)
          (derive ::ear ::zip))))

;(def ant-task-hierarchy
;  (atom (-> (make-hierarchy)
;            (derive ::exec ::has-args))))

(defmulti add-nested
  "Adds a nested element to ant task.
Elements are added in a different way for each type.
Task name keywords are connected into a hierarchy which can
be used to extensively add other types to this method.
The default behaviour is to add an element as a fileset."
  (fn [name task nested]
    (log :debug "dispatch" (keyword "org.soulspace.build.baumeister.utils.ant-utils" name) (class nested))
    [(keyword "org.soulspace.build.baumeister.utils.ant-utils" name) (class nested)])
  :hierarchy ant-task-hierarchy)

(defmethod add-nested [::manifest Map]
  [_ task props]
  (doseq [[n v] props] (.addConfiguredAttribute task
                         (Manifest$Attribute. n v))))

(defmethod add-nested [::has-args String]
  [_ task arg]
  (doto  (.createArg task) (.setValue arg)))

(defmethod add-nested [::has-args Map]
  [_ task props]
  (set-properties! (.createArg task) props))

(defmethod add-nested [::java org.apache.tools.ant.types.Environment$Variable]
  [_ task variable]
  (.addSysproperty task variable))

(defmethod add-nested [::zip org.apache.tools.ant.types.ZipFileSet]
  [_ task zipfileset]
  (.addZipfileset task zipfileset))

(defmethod add-nested [::unzip org.apache.tools.ant.types.PatternSet]
  [_ task patternset]
  (.addPatternset task patternset))

(defmethod add-nested :default [_ task nested] 
  ;(println task nested)
  (.addFileset task nested))

(defn instantiate-task [project name props & nested]
  (let [task (.createTask project name)]
    (when-not task
      (throw (Exception. (format "No task named %s." name))))
    (doto task
      (.init)
      (.setProject project)
      (set-properties! props))
    (doseq [n nested]
      (add-nested name task n))
    task))

(defmacro define-ant-task [clj-name ant-name]
  `(defn ~clj-name [& props#]
     (let [task# (apply instantiate-task ant-project ~(name ant-name) props#)]
       (.execute task#)
       task#)))

(defmacro define-ant-type [clj-name ant-name]
  `(defn ~clj-name [props# & nested#]
     (let [bean# (new ~ant-name)]
       (set-properties! bean# props#)
       (when (has-set-method? (class bean#) "project")
         (set-property! bean# "project" ant-project))
       ; TODO add nested elements
       ; (doseq [n nested#]
       ;   )
       bean#)))

(defmacro define-ant-type-with-project [clj-name ant-name project]
  `(defn ~clj-name [props# & nested#]
     (let [bean# (new ~ant-name ant-project)]
       (set-properties! bean# props#)
       ; TODO add nested elements
       ; (doseq [n nested#]
       ;   )
       bean#)))

(defn task-names [] (map symbol (seq (.. ant-project getTaskDefinitions keySet))))

(defn safe-ant-name [n]
  (symbol (str "ant-" n)))

(defmacro define-all-ant-tasks []
  `(do ~@(map (fn [n] `(define-ant-task ~(safe-ant-name n) ~n)) (task-names))))

(define-all-ant-tasks)

(defn ant-path
  "Constructs an ant Path object from Files and strings."
  [& paths]
  (let [ant-path (Path. nil)]
    (doseq [path paths]
      (.addExisting ant-path (Path. nil (str path))))
    ant-path))

; Newer versions of ant don't have this class:
; (define-ant-type files org.apache.tools.ant.types.resources.Files)
(define-ant-type ant-fileset org.apache.tools.ant.types.FileSet)
(define-ant-type ant-dirset org.apache.tools.ant.types.DirSet)
(define-ant-type ant-patternset org.apache.tools.ant.types.PatternSet)
(define-ant-type ant-zipfileset org.apache.tools.ant.types.ZipFileSet)
(define-ant-type ant-variable org.apache.tools.ant.types.Environment$Variable)
(define-ant-type ant-arg org.apache.tools.ant.types.Commandline$Argument)

; JUnit support
(define-ant-type ant-formatter org.apache.tools.ant.taskdefs.optional.junit.FormatterElement)
(define-ant-type-with-project ant-batchtest org.apache.tools.ant.taskdefs.optional.junit.BatchTest ant-project)

(defmethod coerce [org.apache.tools.ant.taskdefs.optional.junit.FormatterElement$TypeAttribute String] [_ str]
  (org.apache.tools.ant.taskdefs.optional.junit.FormatterElement$TypeAttribute/getInstance
    org.apache.tools.ant.taskdefs.optional.junit.FormatterElement$TypeAttribute str))

(defmethod coerce [org.apache.tools.ant.taskdefs.optional.junit.JUnitTask$ForkMode String] [_ str]
  (org.apache.tools.ant.taskdefs.optional.junit.JUnitTask$ForkMode/getInstance
    org.apache.tools.ant.taskdefs.optional.junit.JUnitTask$ForkMode str))

(defmethod coerce [org.apache.tools.ant.taskdefs.optional.junit.JUnitTask$SummaryAttribute String] [_ str]
  (org.apache.tools.ant.taskdefs.optional.junit.JUnitTask$SummaryAttribute/getInstance
    org.apache.tools.ant.taskdefs.optional.junit.JUnitTask$SummaryAttribute str))

(defmethod add-nested [:org.soulspace.build.baumeister.utils.ant-utils/junit org.apache.tools.ant.types.Path]
  [_ task path] (doto (.createClasspath task) (.add path)))

(defmethod add-nested [:org.soulspace.build.baumeister.utils.ant-utils/junit org.apache.tools.ant.types.Environment$Variable]
  [_ task variable] (.addSysproperty task variable))

(defmethod add-nested [:org.soulspace.build.baumeister.utils.ant-utils/junit org.apache.tools.ant.taskdefs.optional.junit.FormatterElement]
  [_ task formatter] (.addFormatter task formatter))

(defmethod add-nested [:org.soulspace.build.baumeister.utils.ant-utils/junit java.util.Map]
  [_ task props] (doto (.createBatchTest task) (.setTodir (as-file (:todir props))) (.addFileSet (:fileset props))))

