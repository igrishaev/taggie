(ns taggie.edn
  (:refer-clojure :exclude [read read-string])
  (:require
   [clojure.pprint :as pprint]
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [taggie.tags :as tags])
  (:import
   (java.io PushbackReader)))

(defn update-readers [opts]
  (update opts :readers merge tags/TAGS))

(defn read
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
  ([string]
   (read-string string nil))

  ([string opt]
   (edn/read-string (update-readers opt) string)))

(defn write-string
  [data]
  (with-out-str
    (pprint/pprint data)))

(defn write
  [dest data]
  (with-open [out (-> dest
                      io/output-stream
                      io/writer)]
    (binding [*out* out]
      (pprint/pprint data))))
