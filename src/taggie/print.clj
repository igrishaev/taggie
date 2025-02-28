(ns taggie.print
  "
  Custom printing rules for standard objects.
  "
  (:require
   [taggie.array :as arr])
  (:import
   (clojure.lang Atom
                 Ref
                 Agent
                 Volatile
                 Namespace)
   (java.io File
            Writer
            PushbackReader)
   (java.nio.file Path
                  Paths)
   (java.net URL
             URI
             InetAddress)
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
   (java.util Date
              Arrays)
   (java.util.regex Pattern)))

(set! *warn-on-reflection* true)

(defmacro defprint
  "
  Two in one: define a custom printing logic for both
  `print-method` and `print-dup` multimethods.

  - Type is class;
  - value is a binding symbol for a value of this type;
  - writer is a binding symbol for a Writer instance;
  - body is the writing logic.
  "
  [Type value writer & body]
  `(do
     (defmethod print-method ~Type
       [~value
        ~(with-meta writer {:tag 'java.io.Writer})]
       ~@body)
     (defmethod print-dup ~Type
       [~value
        ~(with-meta writer {:tag 'java.io.Writer})]
       ~@body)))


(defmacro print-str-class
  "
  A shortcut that produces a string like:
  #SomeClass 'string value'
  "
  [Type]
  `(defprint ~Type x# w#
     (.write w# "#")
     (.write w# (.getSimpleName ~Type))
     (.write w# " \"")
     (.write w# (str x#))
     (.write w# "\"")))

(defmacro print-str-tag
  "
  Like `print-str-class` but accepts a custom tag.
  "
  [Type tag]
  `(defprint ~Type x# w#
     (.write w# "#")
     (.write w# ~tag)
     (.write w# " \"")
     (.write w# (str x#))
     (.write w# "\"")))

;; io

(print-str-class File)
(print-str-class Path)

;; net

(print-str-class URL)
(print-str-class URI)

(defprint InetAddress ^InetAddress ia w
  (.write w "#InetAddress \"")
  (.write w (.getHostName ia))
  (.write w "\""))

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

(defprint ByteBuffer ^ByteBuffer bb w
  (.write w "#ByteBuffer ")
  (print-method (-> bb .array vec) w))

;; sql

(defprint Timestamp ^Timestamp t w
  (.write w "#sql/Timestamp \"")
  (.write w (-> t .toInstant str))
  (.write w "\""))

;; arrays

(defprint arr/TYPE_BOOL arr w
  (.write w "#booleans ")
  (print-method (vec arr) w))

(defprint arr/TYPE_BYTE arr w
  (.write w "#bytes ")
  (print-method (vec arr) w))

(defprint arr/TYPE_CHAR arr w
  (.write w "#chars ")
  (print-method (vec arr) w))

(defprint arr/TYPE_DOUBLE arr w
  (.write w "#doubles ")
  (print-method (vec arr) w))

(defprint arr/TYPE_FLOAT arr w
  (.write w "#floats ")
  (print-method (vec arr) w))

(defprint arr/TYPE_INT arr w
  (.write w "#ints ")
  (print-method (vec arr) w))

(defprint arr/TYPE_LONG arr w
  (.write w "#longs ")
  (print-method (vec arr) w))

(defprint arr/TYPE_OBJ arr w
  (.write w "#objects ")
  (print-method (vec arr) w))

;; clojure

(defprint Atom ^Atom a w
  (.write w "#atom ")
  (print-method @a w))

(defprint Ref ^Ref r w
  (.write w "#ref ")
  (print-method @r w))

(defprint Agent ^Agent a w
  (.write w "#agent ")
  (print-method @a w))

(defprint Volatile ^Volatile v w
  (.write w "#volatile ")
  (print-method @v w))

#_
(print-str-tag Namespace "ns")

(defprint Namespace ^Namespace n w
  (.write w "#ns ")
  (.write w (str n)))
