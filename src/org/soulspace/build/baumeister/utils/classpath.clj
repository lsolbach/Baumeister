(ns org.soulspace.build.baumeister.utils.classpath
  )

(defn context-classloader []
  "Returns the context class loader of the current thread."
  (.getContextClassLoader (java.lang.Thread/currentThread)))

(defn system-classloader []
  "Returns the system class loader."
  (ClassLoader/getSystemClassLoader))

