(ns ^:experimental ginoco.fs
  "Clojure wrapper for Java NIO.2.
   https://docs.oracle.com/javase/tutorial/essential/io/fileio.html

   It provides similar features as `babashka.fs`, but unlike `babashka.fs`,
   it works with the s3:// & jimfs:// schemes correctly.

   It doesn't aim to exhaustively cover the `java.nio.file` features, instead
   it tries to provide type hints on return values, to make aid Cursive
   auto-completion for Java interop.

   It also doesn't try to provide a lot of conveniences for dealing with the
   various `java.nio.files` enums, but takes away some of the inconvenience of
   using methods with varargs."
  (:import
    (java.io InputStream OutputStream)
    (java.net URI)
    (java.nio.charset Charset)
    (java.nio.file DirectoryStream FileSystem FileVisitOption Files
                   LinkOption OpenOption Path StandardOpenOption)
    (java.nio.file.attribute FileAttribute)
    (java.util.zip ZipInputStream ZipOutputStream)))

(defn uri->path
  "`uri-str` can be either a `String` or a `java.net.URI`."
  ^Path [^String uri-str]
  (Path/of (cond-> uri-str
                   (not (uri? uri-str))
                   URI.)))

(defn path
  "Create a path, using the default filesystem."
  ^Path [^String p & segments]
  (Path/of p (into-array String segments)))

(defn get-path
  "Create a path, using the `fs` file system"
  ^Path [^FileSystem fs ^String p & segments]
  (-> fs (.getPath p (into-array String segments))))

(defn parent ^Path [^Path p]
  (.getParent p))

(defn exists
  (^Boolean [^Path p*] (exists p* true))
  (^Boolean [^Path p follow-links?]
   (Files/exists p (if follow-links?
                     (make-array LinkOption 0)
                     (into-array [LinkOption/NOFOLLOW_LINKS])))))

(defn create-dir
  (^Path [^Path dir] (create-dir dir nil))
  (^Path [^Path dir file-attrs]
   (Files/createDirectory dir (into-array FileAttribute file-attrs))))

(defn create-dirs
  (^Path [^Path dir] (create-dirs dir nil))
  (^Path [^Path dir file-attrs]
   (Files/createDirectories dir (into-array FileAttribute file-attrs))))

(defn list-dir
  "Non-recursively list `dir`, returning a lazy sequence of `Path`s."
  [^Path dir]
  (-> dir Files/list .iterator iterator-seq))

(defn walk
  "Recursively walk `dir`, returning a lazy sequence of `Path`s."
  ([^Path dir] (walk dir false))
  ([^Path dir follow-links?] (walk dir follow-links? Integer/MAX_VALUE))
  ([^Path dir follow-links? depth]
   (-> (Files/walk dir depth (if follow-links?
                               (into-array [FileVisitOption/FOLLOW_LINKS])
                               (make-array FileVisitOption 0)))
       .iterator iterator-seq)))

(defn dir-stream
  "Returns a non-recursive `java.lang.Iterable` of `Path`s within `dir`,
   optionally filtered by `glob`."
  (^DirectoryStream [^Path dir]
   (Files/newDirectoryStream dir))

  (^DirectoryStream [^Path dir ^String glob]
   (Files/newDirectoryStream dir glob)))

(defn input-stream
  ([^Path p*] (input-stream p* [StandardOpenOption/READ]))
  ([^Path p opts]
   (Files/newInputStream p (into-array OpenOption opts))))

(defn output-stream
  ([^Path p*]
   (output-stream p* [StandardOpenOption/CREATE
                      StandardOpenOption/WRITE
                      StandardOpenOption/TRUNCATE_EXISTING]))
  ([^Path p opts]
   (Files/newOutputStream p (into-array OpenOption opts))))

(defn zip-input-stream ^ZipInputStream [^InputStream is]
  (ZipInputStream. is))

(defn zip-output-stream ^ZipOutputStream [^OutputStream os]
  (ZipOutputStream. os))

(defn write-str
  ([^Path p ^String s] (write-str p s nil))
  ([^Path p ^String s & open-opts]
   (Files/writeString p s (into-array OpenOption open-opts))))

(defn charset ^Charset [cs]
  (if (string? cs) (Charset/forName cs) cs))

(defn read-str
  "Read the content of a Path as a string, assuming UTF-8 encoding by default.
   The 2-arity version accepts strings as `a-charset` or a
   `java.nio.charset.Charset` instance."
  ([^Path p]
   (Files/readString p))
  ([^Path p a-charset]
   (Files/readString p (charset a-charset))))

(defn delete [^Path p]
  (Files/delete p))

(defn delete-if-exists ^Boolean [^Path p]
  (Files/deleteIfExists p))
