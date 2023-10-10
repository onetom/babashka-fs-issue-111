(ns repl.zip
  "WARNING!
   The examples delete and re-create a /tmp/x.zip file!"
  (:require
    [ginoco.fs :as gfs]
    [repl.common :refer :all])
  (:import
    (java.net URI)
    (java.nio.file FileSystem FileSystems)
    (java.util.zip ZipEntry)))

(def zip-path (gfs/path "/tmp/x.zip"))

(def content "Babashka fs!\n")

#_@(def zip-path (URI. "jar:file" "/tmp/x.zip" nil))

; Create a ZIP file, using low-level ZipOutputStream API
(comment
  ; https://www.baeldung.com/java-compress-and-uncompress

  (with-open [os (-> zip-path gfs/path gfs/output-stream)
              zos (-> os gfs/zip-output-stream)]
    (doto zos
      (.putNextEntry (ZipEntry. "x.txt"))
      (.write (.getBytes content))))

  (bash (str "ls -lah " zip-path))
  (bash (str "unzip -p " zip-path " x.txt"))

  )

; Create a ZIP file using the `jar:file` scheme
(comment
  ; Example taken from:
  ; 1. https://gist.github.com/lenborje/6d2f92430abe4ba881e3c5ff83736923
  ; 2. https://gist.github.com/rupert-ong/1aa04253d2553ac3d25c5a86cc1293d1

  (do (gfs/delete-if-exists zip-path)

      (with-open [zip-fs ^FileSystem
                         (FileSystems/newFileSystem
                           (URI. "jar:file" (str zip-path) nil)
                           {"create" "true"})]

        (-> zip-fs
            (gfs/get-path "/x.txt")
            (doto (-> .toUri ?)
                  (-> gfs/parent ? gfs/list-dir ?)
                  (-> gfs/parent gfs/create-dirs))
            (gfs/write-str content))))
  ; #object[java.net.URI 0x2ed2686d "jar:file:///tmp/x.zip!/x.txt"]
  ; #object[jdk.nio.zipfs.ZipPath 0xbd84fbb "/"]
  ; nil
  ; Execution error (NoSuchFileException) at jdk.nio.zipfs.ZipFileSystem/newOutputStream (ZipFileSystem.java:842).
  ; /x.txt
  ; java.nio.file.NoSuchFileException: /x.txt
  ; 	at jdk.zipfs/jdk.nio.zipfs.ZipFileSystem.newOutputStream(ZipFileSystem.java:842)
  ; 	at jdk.zipfs/jdk.nio.zipfs.ZipPath.newOutputStream(ZipPath.java:915)
  ; 	at jdk.zipfs/jdk.nio.zipfs.ZipFileSystemProvider.newOutputStream(ZipFileSystemProvider.java:277)
  ; 	at java.base/java.nio.file.Files.newOutputStream(Files.java:227)
  ; 	at java.base/java.nio.file.Files.write(Files.java:3487)
  ; 	at java.base/java.nio.file.Files.writeString(Files.java:3709)
  ; 	at java.base/java.nio.file.Files.writeString(Files.java:3649)

  )
