(ns repl.jimfs
  (:require
    [ginoco.fs :as gfs]
    [ginoco.jimfs :as jimfs]
    [repl.common :refer :all])
  (:import (java.nio.file StandardOpenOption)))

(def content "Babashka fs!\n")

(comment
  (-> (jimfs/mk) (gfs/get-path "/x.txt")
      (doto (gfs/write-str content StandardOpenOption/CREATE))
      gfs/parent gfs/walk)
  ; =>
  ; (#object[com.google.common.jimfs.JimfsPath 0x641fd25c "/"]
  ;  #object[com.google.common.jimfs.JimfsPath 0x3b7803d9 "/x.txt"])
  )
