(ns taggie.tags
  (:require
   [clojure.string :as str]
   [taggie.readers]))

(set! *warn-on-reflection* true)

(def TAGS
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
  (let [syms (-> TAGS keys sort)]
    (doseq [s syms
            :let [f (get TAGS s)
                  fname (as-> f *
                          (str *)
                          (re-find #"\$(.+)@" *)
                          (second *)
                          (str/replace * #"_" "-"))]]
      (println (format "    %20s taggie.readers/%s" s fname))))
  (println \}))
