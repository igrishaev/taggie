(ns taggie.readers
  (:require
   [clojure.string :as str])
  (:import
   (clojure.lang Atom
                 Ref)
   (java.io File)
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

(set! *warn-on-reflection* true)

;; arrays

(defn reader-bools [items]
  `(boolean-array ~items))

(defn reader-bytes [items]
  `(byte-array ~items))

(defn reader-chars [items]
  `(char-array ~items))

(defn reader-doubles [items]
  `(double-array ~items))

(defn reader-floats [items]
  `(float-array ~items))

(defn reader-ints [items]
  `(int-array ~items))

(defn reader-longs [items]
  `(long-array ~items))

(defn reader-objects [items]
  `(object-array ~items))

;; io

(defn reader-File [^String line]
  `(new File ~line))

;; net

(defn reader-URI [^String line]
  `(new URI ~line))

(defn reader-URL [^String line]
  `(new URL ~line))

;; java.time

(defn reader-Duration [line]
  `(Duration/parse ~line))

(defn reader-Instant [line]
  `(Instant/parse ~line))

(defn reader-LocalDate [line]
  `(LocalDate/parse ~line))

(defn reader-LocalDateTime [line]
  `(LocalDateTime/parse ~line))

(defn reader-LocalTime [line]
  `(LocalTime/parse ~line))

(defn reader-MonthDay [line]
  `(MonthDay/parse ~line))

(defn reader-OffsetDateTime [line]
  `(OffsetDateTime/parse ~line))

(defn reader-OffsetTime [line]
  `(OffsetTime/parse ~line))

(defn reader-Period [line]
  `(Period/parse ~line))

(defn reader-Year [line]
  `(Year/parse ~line))

(defn reader-YearMonth [line]
  `(YearMonth/parse ~line))

(defn reader-ZonedDateTime [line]
  `(ZonedDateTime/parse ~line))

(defn reader-ZoneId [line]
  `(ZoneId/of ~line))

(defn reader-ZoneOffset [line]
  `(ZoneOffset/of ~line))

;; util

(defn reader-regex ^Pattern [string]
  `(Pattern/compile ~string))

(defn reader-Date ^Date [string]
  `(-> ~string
       Instant/parse
       Date/from))

(defn reader-ByteBuffer [seq-of-bytes]
  `(-> ~seq-of-bytes
       byte-array
       ByteBuffer/wrap))

;; sql

(defn reader-sql-Timestamp [string]
  `(-> ~string
       Instant/parse
       Timestamp/from))

;; clojure

(defn reader-atom [content]
  `(atom ~content))

(defn reader-ref [content]
  `(ref ~content))

;; exceptions

(defn reader-error [error]
  `(do ~error))

;;
;; Clojure readers (return forms)
;;

(def READERS
  {
   ;; arrays

   'booleans taggie.readers/reader-bools
   'bytes    taggie.readers/reader-bytes
   'chars    taggie.readers/reader-chars
   'doubles  taggie.readers/reader-doubles
   'floats   taggie.readers/reader-floats
   'ints     taggie.readers/reader-ints
   'longs    taggie.readers/reader-longs
   'objects  taggie.readers/reader-objects

   ;; io

   'File taggie.readers/reader-File

   ;; bb

   'bb taggie.readers/reader-ByteBuffer

   ;; net

   'URI taggie.readers/reader-URI
   'URL taggie.readers/reader-URL

   ;; java.time

   'Duration          taggie.readers/reader-Duration
   'Instant           taggie.readers/reader-Instant
   'LocalDate         taggie.readers/reader-LocalDate
   'LocalDateTime     taggie.readers/reader-LocalDateTime
   'LocalTime         taggie.readers/reader-LocalTime
   'MonthDay          taggie.readers/reader-MonthDay
   'OffsetDateTime    taggie.readers/reader-OffsetDateTime
   'OffsetTime        taggie.readers/reader-OffsetTime
   'Period            taggie.readers/reader-Period
   'Year              taggie.readers/reader-Year
   'YearMonth         taggie.readers/reader-YearMonth
   'ZonedDateTime     taggie.readers/reader-ZonedDateTime
   'ZoneId            taggie.readers/reader-ZoneId
   'ZoneOffset        taggie.readers/reader-ZoneOffset

   ;; util

   'regex taggie.readers/reader-regex
   'Date  taggie.readers/reader-Date

   ;; clojure

   'atom  taggie.readers/reader-atom
   'ref   taggie.readers/reader-ref

   ;; sql

   'sql/Timestamp taggie.readers/reader-sql-Timestamp

   ;; exceptions

   'error taggie.readers/reader-error})


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
      (println (format "    %20s taggie.readers/%s" s fname))))
  (println \}))
