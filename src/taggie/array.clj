(ns taggie.array)

(set! *warn-on-reflection* true)

(def TYPE_BOOL
  (Class/forName "[Z"))

(def TYPE_BYTE
  (Class/forName "[B"))

(def TYPE_CHAR
  (Class/forName "[C"))

(def TYPE_DOUBLE
  (Class/forName "[D"))

(def TYPE_FLOAT
  (Class/forName "[F"))

(def TYPE_INT
  (Class/forName "[I"))

(def TYPE_LONG
  (Class/forName "[J"))

(def TYPE_OBJ
  (Class/forName "[Ljava.lang.Object;"))
