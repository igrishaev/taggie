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

(def ^java.util.Map EDN_READERS
  (new java.util.HashMap))

(def ^java.util.Map CLJ_READERS
  (new java.util.HashMap))

(defmacro defreader [tag [bind] & body]
  (let [name-clj (symbol (format "__reader-%s-clj" tag))
        name-clj-fq (symbol (str (ns-name *ns*)) (name name-clj))
        name-edn (symbol (format "__reader-%s-edn" tag))
        name-edn-fq (symbol (str (ns-name *ns*)) (name name-edn))]
    `(do
       (defn ~name-edn [~bind]
         ~@body)
       (.put EDN_READERS '~tag ~name-edn)
       (.put CLJ_READERS '~tag '~name-clj-fq)
       (defn ~name-clj [~bind]
         (clojure.core/list '~name-edn-fq ~bind)))))


;; arrays

(defreader booleans [items]
  (boolean-array items))

(defreader bytes [items]
  (byte-array items))

(defreader chars [items]
  (char-array items))

(defreader doubles [items]
  (double-array items))

(defreader floats [items]
  (float-array items))

(defreader ints [items]
  (int-array items))

(defreader longs [items]
  (long-array items))

(defreader objects [items]
  (object-array items))

;; io

(defreader File [^String line]
  (new File line))

;; net

(defreader URI [^String line]
  (new URI line))

(defreader URL [^String line]
  (new URL line))

;; java.time

(defreader Duration [line]
  (Duration/parse line))

(defreader Instant [line]
  (Instant/parse line))

(defreader LocalDate [line]
  (LocalDate/parse line))

(defreader LocalDateTime [line]
  (LocalDateTime/parse line))

(defreader LocalTime [line]
  (LocalTime/parse line))

(defreader MonthDay [line]
  (MonthDay/parse line))

(defreader OffsetDateTime [line]
  (OffsetDateTime/parse line))

(defreader OffsetTime [line]
  (OffsetTime/parse line))

(defreader Period [line]
  (Period/parse line))

(defreader Year [line]
  (Year/parse line))

(defreader YearMonth [line]
  (YearMonth/parse line))

(defreader ZonedDateTime [line]
  (ZonedDateTime/parse line))

(defreader ZoneId [line]
  (ZoneId/of line))

(defreader ZoneOffset [^String line]
  (ZoneOffset/of line))

;; util

(defreader regex ^Pattern [string]
  (Pattern/compile string))

(defreader Date ^Date [string]
  (-> string
       Instant/parse
       Date/from))

(defreader ByteBuffer [seq-of-bytes]
  (-> seq-of-bytes
       byte-array
       ByteBuffer/wrap))

;; sql

(defreader sql-Timestamp [string]
  (-> string
       Instant/parse
       Timestamp/from))

;; clojure

(defreader atom [content]
  (atom content))

(defreader ref [content]
  (ref content))

;; exceptions

(defreader error [error]
  (do error))


;;
;; data_readers.clj generator
;;

(defn generate-data-readers []
  (println ";;")
  (println ";; generated")
  (println ";;")
  (println \{)
  (let [syms (-> CLJ_READERS keys sort)]
    (doseq [s syms
            :let [fname (get CLJ_READERS s)]]
      (println (format "    %20s %s" s fname))))
  (println \}))
