(ns taggie.readers
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

(defn reader-ref ^Ref [content]
  `(ref ~content))

;; exceptions

(defn reader-error [error]
  `(do ~error))
