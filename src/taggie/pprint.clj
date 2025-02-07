(ns taggie.pprint
  (:require
   [taggie.array :as arr]
   [clojure.pprint :as pprint])
  (:import
   (clojure.lang Atom Ref)
   (java.nio ByteBuffer)
   (java.io Writer)))


(defmacro simple-dispatch-deref [Class tag]
  `(defmethod pprint/simple-dispatch ~Class
     [x#]
     (.write ^Writer *out* "#")
     (.write ^Writer *out* ~tag)
     (.write ^Writer *out* " ")
     (pprint/simple-dispatch @x#)))


(defmethod pprint/simple-dispatch ByteBuffer
  [^ByteBuffer bb]
  (.write ^Writer *out* "#ByteBuffer ")
  (pprint/simple-dispatch (-> bb .array vec)))


(simple-dispatch-deref Atom "atom")
(simple-dispatch-deref Ref "ref")


(defmacro simple-dispatch-array [Class tag]
  `(defmethod pprint/simple-dispatch ~Class
     [x#]
     (.write ^Writer *out* "#")
     (.write ^Writer *out* ~tag)
     (.write ^Writer *out* " ")
     ((var pprint/pprint-array) x#)))

(simple-dispatch-array arr/TYPE_BOOL   "booleans")
(simple-dispatch-array arr/TYPE_BYTE   "bytes")
(simple-dispatch-array arr/TYPE_CHAR   "chars")
(simple-dispatch-array arr/TYPE_DOUBLE "doubles")
(simple-dispatch-array arr/TYPE_FLOAT  "floats")
(simple-dispatch-array arr/TYPE_INT    "ints")
(simple-dispatch-array arr/TYPE_LONG   "longs")
(simple-dispatch-array arr/TYPE_OBJ    "objects")
