(ns repl.zip
  (:require
    [ginoco.fs :as gfs]
    [repl.common :refer :all])
  (:import
    (java.util.zip ZipEntry)))

(comment
  ; https://www.baeldung.com/java-compress-and-uncompress

  (with-open [os (-> "/tmp/x.zip" gfs/path gfs/output-stream)
              zos (-> os gfs/zip-output-stream)]
    (doto zos
      (.putNextEntry (ZipEntry. "x.txt"))
      (.write (.getBytes "Babashka fs!\n"))))

  (bash "ls -lah /tmp/x.zip")
  (bash "unzip -p /tmp/x.zip x.txt")

  )
