(ns repl.s3
  (:require
    [babashka.fs :as fs]
    [ginoco.fs :as gfs]
    [repl.common :refer :all])
  (:import (java.nio.file Path StandardOpenOption)))

(def bucket
  (-> "%s-babashka-fs-issue-111"
      (format (System/getenv "USER"))))

(def ^Path s3-path
  (-> "s3://%s/" (format bucket) gfs/uri->path))

(def make-bucket-cmd
  (str "aws s3 mb s3://" bucket))

(comment
  ;; Per-path file system path-separator
  (-> s3-path .getFileSystem .getSeparator)
  ; => "/"

  ;; Create an S3 bucket for testing,
  ; If aws cli 2 is on the PATH:
  (sh make-bucket-cmd)

  ; If you have Nix:
  (nix-run make-bucket-cmd)

  (-> s3-path
      (.resolve "test.txt")
      (gfs/write-str "Babashka fs!\n" StandardOpenOption/CREATE))

  (-> s3-path gfs/list-dir)
  (-> s3-path gfs/walk)

  ;; babashka.fs operations
  (-> s3-path vector (fs/list-dirs (constantly true)))
  ; => (#object[software.amazon.nio.spi.s3.S3Path 0x1443fc35 "test.txt"])

  (-> s3-path (fs/path "test.txt"))
  ; Execution error (UnsupportedOperationException)
  ;   at software.amazon.nio.spi.s3.S3Path/toFile (S3Path.java:729).
  ; S3 Objects cannot be represented in the local (default) file system

  )
