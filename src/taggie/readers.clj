(ns taggie.readers
  "
  Define EDN and Clojure reader functions.
  EDN functions accept a value and parse it into a final
  object. Clojure functions accept a value and produce
  a _form_ which, when evaluated, produces a value.
  This is to prevent double evaluation of an object.
  For details, see that thread:
  https://clojurians.slack.com/archives/C03S1KBA2/p1735626685896359
  "
  (:require
   [clojure.string :as str])
  (:import
   (clojure.lang Atom
                 Ref
                 Symbol)
   (java.io File)
   (java.net URL
             URI
             InetAddress)
   (java.nio.file Path
                  Paths)
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

(def EDN_READERS
  (atom {}))

(def CLJ_READERS
  (atom {}))

(defn edn-readers []
  @EDN_READERS)

(defn tag->name
  "
  Correct some characters when declaring a reader function.
  "
  ^String [^Symbol tag]
  (str/replace (str tag) #"/" "_SLASH_"))

(defmacro defreader
  "
  A macro to do many things in one step, namely:
  - define an EDN reader function;
  - add it to the global map of EDN readers;
  - add an entry into the global map of Clojure readers;
  - define a Clojure reader function that relies on the edn reader.
  "
  [tag [bind] & body]
  (let [name-clj (symbol (format "__reader-%s-clj" (tag->name tag)))
        name-clj-fq (symbol (str (ns-name *ns*)) (name name-clj))
        name-edn (symbol (format "__reader-%s-edn" (tag->name tag)))
        name-edn-fq (symbol (str (ns-name *ns*)) (name name-edn))]
    `(do
       (defn ~name-edn [~bind]
         ~@body)
       (swap! EDN_READERS assoc '~tag ~name-edn)
       (swap! CLJ_READERS assoc '~tag '~name-clj-fq)
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

(defreader Path [^String line]
  (Paths/get line (into-array String [])))

;; net

(defreader URI [^String line]
  (new URI line))

(defreader URL [^String line]
  (new URL line))

(defreader InetAddress [^String line]
  (InetAddress/getByName line))

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

(defreader sql/Timestamp [string]
  (-> string
       Instant/parse
       Timestamp/from))

;; clojure

(defreader atom [content]
  (atom content))

(defreader ref [content]
  (ref content))

(defreader agent [content]
  (agent content))

(defreader volatile [content]
  (volatile! content))

(defn __reader-ns-clj [ns-sym]
  `(find-ns (quote ~ns-sym)))

(swap! CLJ_READERS assoc 'ns `__reader-ns-clj)
(swap! EDN_READERS assoc 'ns find-ns)

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
  (let [readers @CLJ_READERS
        syms (-> readers keys sort)]
    (doseq [s syms
            :let [fname (get readers s)]]
      (println (format "    %20s %s" s fname))))
  (println \}))
