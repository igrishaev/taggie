(ns taggie.edn
  (:refer-clojure :exclude [read read-string])
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.pprint :as pprint])
  (:import
   (clojure.lang Atom
                 Ref)
   (java.io File
            PushbackReader)
   (java.net URL
             URI)
   (java.nio ByteBuffer)
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
   (java.util Date)
   (java.util.regex Pattern)))


;; arrays

(defn reader-bools [items]
  (boolean-array items))

(defn reader-bytes [items]
  (byte-array items))

(defn reader-chars [items]
  (char-array items))

(defn reader-doubles [items]
  (double-array items))

(defn reader-floats [items]
  (float-array items))

(defn reader-ints [items]
  (int-array items))

(defn reader-longs [items]
  (long-array items))

(defn reader-objects [items]
  (object-array items))

;; io

(defn reader-File [^String line]
  (new File line))

;; net

(defn reader-URI [^String line]
  (new URI line))

(defn reader-URL [^String line]
  (new URL line))

;; java.time

(defn reader-Duration [line]
  (Duration/parse line))

(defn reader-Instant [line]
  (Instant/parse line))

(defn reader-LocalDate [line]
  (LocalDate/parse line))

(defn reader-LocalDateTime [line]
  (LocalDateTime/parse line))

(defn reader-LocalTime [line]
  (LocalTime/parse line))

(defn reader-MonthDay [line]
  (MonthDay/parse line))

(defn reader-OffsetDateTime [line]
  (OffsetDateTime/parse line))

(defn reader-OffsetTime [line]
  (OffsetTime/parse line))

(defn reader-Period [line]
  (Period/parse line))

(defn reader-Year [line]
  (Year/parse line))

(defn reader-YearMonth [line]
  (YearMonth/parse line))

(defn reader-ZonedDateTime [line]
  (ZonedDateTime/parse line))

(defn reader-ZoneId [line]
  (ZoneId/of line))

(defn reader-ZoneOffset [^String line]
  (ZoneOffset/of line))

;; util

(defn reader-regex ^Pattern [string]
  (Pattern/compile string))

(defn reader-Date ^Date [string]
  (-> string
      Instant/parse
      Date/from))

(defn reader-ByteBuffer [seq-of-bytes]
  (-> seq-of-bytes
      byte-array
      ByteBuffer/wrap))

;; sql

(defn reader-sql-Timestamp [string]
  (-> string
      Instant/parse
      Timestamp/from))

;; clojure

(defn reader-atom [content]
  (atom content))

(defn reader-ref [content]
  (ref content))

;; exceptions

(defn reader-error [error]
  (do error))


;;
;; EDN readers (return values)
;;

(def READERS
  {
   ;; arrays

   'booleans reader-bools
   'bytes    reader-bytes
   'chars    reader-chars
   'doubles  reader-doubles
   'floats   reader-floats
   'ints     reader-ints
   'longs    reader-longs
   'objects  reader-objects

   ;; io

   'File reader-File

   ;; bb

   'bb reader-ByteBuffer

   ;; net

   'URI reader-URI
   'URL reader-URL

   ;; java.time

   'Duration          reader-Duration
   'Instant           reader-Instant
   'LocalDate         reader-LocalDate
   'LocalDateTime     reader-LocalDateTime
   'LocalTime         reader-LocalTime
   'MonthDay          reader-MonthDay
   'OffsetDateTime    reader-OffsetDateTime
   'OffsetTime        reader-OffsetTime
   'Period            reader-Period
   'Year              reader-Year
   'YearMonth         reader-YearMonth
   'ZonedDateTime     reader-ZonedDateTime
   'ZoneId            reader-ZoneId
   'ZoneOffset        reader-ZoneOffset

   ;; util

   'regex reader-regex
   'Date  reader-Date

   ;; clojure

   'atom  reader-atom
   'ref   reader-ref

   ;; sql

   'sql/Timestamp reader-sql-Timestamp

   ;; exceptions

   'error reader-error})


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
