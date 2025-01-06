(ns taggie.core
  (:refer-clojure :exclude [read-string read])
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.pprint :as pprint]
   [clojure.string :as str])
  (:import
   (clojure.lang Atom
                 Ref)
   (java.io File
            Writer
            PushbackReader)
   (java.net URL
             URI)
   (java.sql Timestamp)
   (java.time Duration
              Instant
              LocalDate
              LocalDateTime
              LocalTime
              MonthDay
              OffsetDateTime
              OffsetTime
              Period
              Year
              YearMonth
              ZonedDateTime
              ZoneId
              ZoneOffset)
   (java.util Date
              Arrays)
   (java.util.regex Pattern)))

(set! *warn-on-reflection* true)

;;
;; printers
;;

(defmacro defprint [Type value writer & body]
  `(do
     (defmethod print-method ~Type
       [~value
        ~(with-meta writer {:tag 'java.io.Writer})]
       ~@body)
     (defmethod print-dup ~Type
       [~value
        ~(with-meta writer {:tag 'java.io.Writer})]
       ~@body)))

(defmacro print-str-class [Type]
  `(defprint ~Type x# w#
     (.write w# "#")
     (.write w# (.getSimpleName ~Type))
     (.write w# " \"")
     (.write w# (str x#))
     (.write w# "\"")))

(defmacro print-str-tag [Type tag]
  `(defprint ~Type x# w#
     (.write w# "#")
     (.write w# ~tag)
     (.write w# " \"")
     (.write w# (str x#))
     (.write w# "\"")))

;; io

(print-str-class File)

;; net

(print-str-class URL)
(print-str-class URI)

;; java.time

(print-str-class Duration)
(print-str-class Instant)
(print-str-class LocalDate)
(print-str-class LocalDateTime)
(print-str-class LocalTime)
(print-str-class MonthDay)
(print-str-class OffsetDateTime)
(print-str-class OffsetTime)
(print-str-class Period)
(print-str-class Year)
(print-str-class YearMonth)
(print-str-class ZonedDateTime)
(print-str-class ZoneId)
(print-str-class ZoneOffset)

;; util

(print-str-tag Pattern "regex")

(defprint Date ^Date d w
  (.write w "#Date \"")
  (.write w (-> d .toInstant str))
  (.write w "\""))

;; sql

(defprint Timestamp ^Timestamp t w
  (.write w "#sql.Timestamp \"")
  (.write w (-> t .toInstant str))
  (.write w "\""))

;; arrays

(def TYPE_ARRAY_BOOL
  (Class/forName "[Z"))

(def TYPE_ARRAY_BYTE
  (Class/forName "[B"))

(def TYPE_ARRAY_CHAR
  (Class/forName "[C"))

(def TYPE_ARRAY_DOUBLE
  (Class/forName "[D"))

(def TYPE_ARRAY_FLOAT
  (Class/forName "[F"))

(def TYPE_ARRAY_INT
  (Class/forName "[I"))

(def TYPE_ARRAY_LONG
  (Class/forName "[J"))

(def TYPE_ARRAY_OBJ
  (Class/forName "[Ljava.lang.Object;"))

(defprint TYPE_ARRAY_BOOL ^booleans arr w
  (.write w "#booleans ")
  (.write w (Arrays/toString arr)))

(defprint TYPE_ARRAY_BYTE ^bytes arr w
  (.write w "#bytes ")
  (.write w (Arrays/toString arr)))

(defprint TYPE_ARRAY_CHAR ^chars arr w
  (.write w "#chars ")
  (.write w (Arrays/toString arr)))

(defprint TYPE_ARRAY_DOUBLE ^doubles arr w
  (.write w "#doubles ")
  (.write w (Arrays/toString arr)))

(defprint TYPE_ARRAY_FLOAT ^floats arr w
  (.write w "#floats ")
  (.write w (Arrays/toString arr)))

(defprint TYPE_ARRAY_INT ^ints arr w
  (.write w "#ints ")
  (.write w (Arrays/toString arr)))

(defprint TYPE_ARRAY_LONG ^longs arr w
  (.write w "#longs ")
  (.write w (Arrays/toString arr)))

(defprint TYPE_ARRAY_OBJ ^objects arr w
  (.write w "#objects ")
  (.write w (Arrays/toString arr)))

;; clojure

(def pr-on (-> 'clojure.core/pr-on
               resolve
               deref))

(defprint Atom ^Atom a w
  (.write w "#atom ")
  (pr-on @a w))

(defprint Ref ^Ref r w
  (.write w "#ref ")
  (pr-on @r w))

;;
;; Readers
;;

(defmacro reader-parse [Type]
  (let [func-name (symbol (format "reader-%s" Type))
        method (symbol (format "%s/parse" Type))]
    `(defn ~func-name [x#]
       (~method x#))))

(defmacro reader-new [Type]
  (let [func-name (symbol (format "reader-%s" Type))
        arg (with-meta (gensym "line") {:tag 'java.lang.String})]
    `(defn ~func-name [~arg]
       (new ~Type ~arg))))

(defmacro reader-of [Type]
  (let [func-name (symbol (format "reader-%s" Type))
        method (symbol (format "%s/of" Type))
        arg (with-meta (gensym "line") {:tag 'java.lang.String})]
    `(defn ~func-name [~arg]
       (~method ~arg))))

;; arrays

(defn reader-bools ^booleans [items]
  (boolean-array items))

(defn reader-bytes ^bytes [items]
  (byte-array items))

(defn reader-chars ^chars [items]
  (char-array items))

(defn reader-doubles ^doubles [items]
  (double-array items))

(defn reader-floats ^floats [items]
  (float-array items))

(defn reader-ints ^ints [items]
  (int-array items))

(defn reader-longs
  ^longs [items]
  (long-array items))

(defn reader-objects ^objects [items]
  (object-array items))

;; io

(reader-new File)

;; net

(reader-new URI)
(reader-new URL)

;; java.time

(reader-parse Duration)
(reader-parse Instant)
(reader-parse LocalDate)
(reader-parse LocalDateTime)
(reader-parse LocalTime)
(reader-parse MonthDay)
(reader-parse OffsetDateTime)
(reader-parse OffsetTime)
(reader-parse Period)
(reader-parse Year)
(reader-parse YearMonth)
(reader-parse ZonedDateTime)
(reader-of ZoneId)
(reader-of ZoneOffset)

;; util

(defn reader-regex ^Pattern [string]
  (Pattern/compile string))

(defn reader-Date ^Date [string]
  (-> string
      Instant/parse
      Date/from))

;; sql

(defn reader-sql-Timestamp ^Timestamp [string]
  (-> string
      Instant/parse
      Timestamp/from))

;; clojure

(defn reader-atom ^Atom [string]
  (atom string))

(defn reader-ref ^Ref [string]
  (ref string))

;; exceptions

(defn reader-error [error]
  (identity error))


;;
;; readers
;;

(def READERS
  {
   ;; arrays

   'bools   taggie.core/reader-bools
   'bytes   taggie.core/reader-bytes
   'chars   taggie.core/reader-chars
   'doubles taggie.core/reader-doubles
   'floats  taggie.core/reader-floats
   'ints    taggie.core/reader-ints
   'longs   taggie.core/reader-longs
   'objects taggie.core/reader-objects

   ;; io

   'File taggie.core/reader-File

   ;; net

   'URI taggie.core/reader-URI
   'URL taggie.core/reader-URL

   ;; java.time

   'Duration          taggie.core/reader-Duration
   'Instant           taggie.core/reader-Instant
   'LocalDate         taggie.core/reader-LocalDate
   'LocalDateTime     taggie.core/reader-LocalDateTime
   'LocalTime         taggie.core/reader-LocalTime
   'MonthDay          taggie.core/reader-MonthDay
   'OffsetDateTime    taggie.core/reader-OffsetDateTime
   'OffsetTime        taggie.core/reader-OffsetTime
   'Period            taggie.core/reader-Period
   'Year              taggie.core/reader-Year
   'YearMonth         taggie.core/reader-YearMonth
   'ZonedDateTime     taggie.core/reader-ZonedDateTime
   'ZoneId            taggie.core/reader-ZoneId
   'ZoneOffset        taggie.core/reader-ZoneOffset

   ;; util

   'regex taggie.core/reader-regex
   'Date  taggie.core/reader-Date

   ;; clojure

   'atom  taggie.core/reader-atom
   'ref   taggie.core/reader-ref

   ;; sql

   'sql.Timestamp taggie.core/reader-sql-Timestamp

   ;; exceptions

   'error taggie.core/reader-error})

(defn generate-data-readers []
  (println ";;")
  (println ";; generated")
  (println ";;")
  (println \{)
  (let [syms (-> READERS keys sort)]
    (doseq [s syms
            :let [f (get READERS s)
                  fname (as-> f *
                          (str *)
                          (re-find #"\$(.+)@" *)
                          (second *)
                          (str/replace * #"_" "-"))]]
      (println (format "    %20s taggie.core/%s" s fname))))
  (println \}))


;;
;; pretty print
;;


;; todo macro

(defmethod pprint/simple-dispatch Atom
  [x]
  (.write ^Writer *out* "#atom ")
  (pprint/simple-dispatch @x))

(defmethod pprint/simple-dispatch Ref
  [x]
  (.write ^Writer *out* "#ref ")
  (pprint/simple-dispatch @x))

;; todo macro
;; todo bool byte char double float int log obj

(defmethod pprint/simple-dispatch TYPE_ARRAY_BYTE
  [x]
  (.write ^Writer *out* "#bytes ")
  ((var pprint/pprint-array) x))


;;
;; read/write EDN
;;

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
