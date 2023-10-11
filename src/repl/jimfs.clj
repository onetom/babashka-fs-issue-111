(ns repl.jimfs
  (:require
    [babashka.fs :as fs]
    [ginoco.fs :as gfs]
    [ginoco.jimfs :as jimfs]
    [repl.common :refer :all])
  (:import (java.nio.file StandardOpenOption)))

(def content "Babashka fs!\n")

(comment
  ;;
  (-> (jimfs/mk)
      (gfs/get-path "/asd/qwe/")
      (doto fs/create-dirs)
      (.resolve "zxc.txt")
      (doto (gfs/write-str content StandardOpenOption/CREATE))
      gfs/root gfs/walk)
  ; =>
  ; (#object[com.google.common.jimfs.JimfsPath 0x2cef747d "/"]
  ;  #object[com.google.common.jimfs.JimfsPath 0x17a697e5 "/asd"]
  ;  #object[com.google.common.jimfs.JimfsPath 0x688907ca "/asd/qwe"]
  ;  #object[com.google.common.jimfs.JimfsPath 0x67342c3 "/asd/qwe/zxc.txt"])

  (-> (jimfs/mk)
      (gfs/get-path "/asd/qwe/")
      (doto fs/create-dirs)
      (fs/path "zxc.txt"))
  ; Execution error (UnsupportedOperationException) at com.google.common.jimfs.JimfsPath/toFile (JimfsPath.java:386).
  ; null
  ; java.lang.UnsupportedOperationException
  ; 	at com.google.common.jimfs.JimfsPath.toFile(JimfsPath.java:386)
  ; 	at babashka.fs$as_file.invokeStatic(fs.cljc:45)
  ; 	at babashka.fs$as_file.invoke(fs.cljc:42)
  ; 	at babashka.fs$path.invokeStatic(fs.cljc:57)
  ; 	at babashka.fs$path.invoke(fs.cljc:51)
  )
