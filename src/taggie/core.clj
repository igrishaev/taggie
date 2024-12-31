(ns taggie.core
  (:require
   [clojure.java.io :as io])
  (:import
   (java.util HexFormat
              Date)
   (java.util.regex Pattern)
   (java.net URL
             URI)
   (java.time LocalDate
              LocalDateTime

              Clock
              Duration
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
              ZoneOffset

              )
   (java.io File
            Writer)

   ))

;; error!
;; Queue
;; DefRecord

;; UUID?

;; sql.Timestamp
;; sql.Time
;; sql.Date

;; byte

(set! *warn-on-reflection* true)


(defmacro print-as-str [Type]
  `(defmethod print-method ~Type
     [x# ^Writer w#]
     (.write w# (format "#%s \"%s\""
                        (.getSimpleName ~Type)
                        #_(.getCanonicalName ~Type)
                        (str x#)))))

;; uuid regex
;;


;; URL URI

;; byte[] char[] int[] float[] boolp[] short[] byte[] double[]
;; object[] long[]

;; byte buffer
;; regex

;; Date
;; BigDecimal
;; BigInteger
;; BigInt
;; Ratio

;; Atom
;; Ref

;; InputStream?
;; Reader?


(print-as-str File)

(print-as-str Pattern)

(print-as-str URL)
(print-as-str URI)

(print-as-str Clock)
(print-as-str Duration)
(print-as-str Instant)
(print-as-str LocalDate)
(print-as-str LocalDateTime)
(print-as-str LocalTime)
(print-as-str MonthDay)
(print-as-str OffsetDateTime)
(print-as-str OffsetTime)
(print-as-str Period)
(print-as-str Year)
(print-as-str YearMonth)
(print-as-str ZonedDateTime)
(print-as-str ZoneId)
(print-as-str ZoneOffset)


(def ^HexFormat -HEX
  (HexFormat/of))


(def TYPE_ARRAY_BYTE
  (Class/forName "[B"))

(defmethod print-method TYPE_ARRAY_BYTE
  [x ^Writer w]
  (.write w "#array/byte \"")
  (.write w (.formatHex -HEX x))
  (.write w "\""))


;;
;; Readers
;;

(defmacro reader-DT-parse [Type]
  (let [func-name (symbol (format "reader-%s" Type))
        method (symbol (format "%s/parse" Type))]
    `(defn ~func-name [x#]
       (~method x#))))

(defmacro reader-new [Type]
  (let [func-name (symbol (format "reader-%s" Type))
        arg (with-meta (gensym "line") {:tag 'java.lang.String})]
    `(defn ~func-name [~arg]
       (new ~Type ~arg))))


(defn reader-Array-Int [items]
  (int-array items))

(defn reader-Array-Float [items]
  (float-array items))


(reader-DT-parse LocalDate)

(reader-new File)
(reader-new URI)
(reader-new URL)


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
