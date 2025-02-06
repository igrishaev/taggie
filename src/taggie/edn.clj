(ns taggie.edn
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
