(ns taggie.edn
  "
  Edn facilities: like the standard clojure.edn
  but powered with custom readers.
  "
  (:refer-clojure :exclude [read read-string])
  (:require
   [taggie.readers :as readers]
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.pprint :as pprint])
  (:import
   (java.io PushbackReader)))


(def READERS
  (into {} readers/EDN_READERS))

(defn update-readers [opts]
  (update opts :readers merge READERS))

(defn read
  "
  Read EDN from a source which can be a file path,
  an input stream, a reader, etc (anything that
  can be courced with `clojure.java.io/input-stream`).

  Accepts the standard EDN options. The `:readers` map
  gets merged with the custom global readers.
  "
  ([src]
   (read src nil))

  ([src opt]
   (let [options
         (update-readers opt)]
     (with-open [in (-> src
                        io/input-stream
                        io/reader
                        PushbackReader.)]
       (edn/read options in)))))

(defn read-string
  "
  Like `read` but accepts a string.
  "
  ([string]
   (read-string string nil))

  ([string opt]
   (edn/read-string (update-readers opt) string)))

(defn write-string
  "
  Write data into an EDN string (with pretty printing.)
  "
  [data]
  (with-out-str
    (pprint/pprint data)))

(defn write
  "
  Like `write-string` but accepts a destination: a file path
  a file, an output stream, a writer, etc (anything that
  `clojure.java.io/output-stream` accepts).
  "
  [dest data]
  (with-open [out (-> dest
                      io/output-stream
                      io/writer)]
    (binding [*out* out]
      (pprint/pprint data))))
