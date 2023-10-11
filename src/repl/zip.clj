(ns repl.zip
  "WARNING!
   The examples delete and re-create a /tmp/x.zip file!"
  (:require
    [ginoco.fs :as gfs]
    [repl.common :refer :all])
  (:import
    (java.nio.file StandardOpenOption)
    (java.util.zip ZipEntry)))

(def zip-path (gfs/path "/tmp/x.zip"))

(def content "Babashka fs!\n")

;;; Create a ZIP file using the `jar:file://` scheme
(comment
  ; Example taken from:
  ; 1. https://gist.github.com/lenborje/6d2f92430abe4ba881e3c5ff83736923
  ; 2. https://gist.github.com/rupert-ong/1aa04253d2553ac3d25c5a86cc1293d1

  (with-open [zip-fs (-> zip-path gfs/jar-uri (gfs/new-fs :create true))]
    (-> zip-fs (gfs/get-path "/x.txt")
        (doto (gfs/write-str content StandardOpenOption/CREATE)))

    (bash (str "unzip -v " zip-path))
    (bash (str "unzip -p " zip-path " x.txt"))
    )

  ; Debug
  (gfs/delete-if-exists zip-path)
  @(def zip-fs (-> zip-path gfs/jar-uri (gfs/new-fs :create true)))
  (.close zip-fs)

  )

;;; Create a ZIP file, using low-level ZipOutputStream API
(comment
  ; https://www.baeldung.com/java-compress-and-uncompress

  (with-open [os (-> zip-path gfs/path gfs/output-stream)
              zos (-> os gfs/zip-output-stream)]
    (doto zos
      (.putNextEntry (ZipEntry. "x.txt"))
      (.write (.getBytes content))))

  (bash (str "ls -lah " zip-path))
  (bash (str "unzip -v " zip-path))
  (bash (str "unzip -p " zip-path " x.txt"))

  )
