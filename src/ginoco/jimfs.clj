(ns ginoco.jimfs
  (:import
    (com.google.common.jimfs Configuration Jimfs)
    (java.nio.file FileSystem)))

(defn unix-config []
  (-> (Configuration/unix)
      ; Prevent creating a "/work" directory
      .toBuilder (.setWorkingDirectory "/") .build))

(defn mk
  "Create a new in-memory file system"
  (^FileSystem [] (mk (unix-config)))
  (^FileSystem [^Configuration fs-config]
   (Jimfs/newFileSystem fs-config)))
