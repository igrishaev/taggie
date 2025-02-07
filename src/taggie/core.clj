(ns taggie.core
  "
  The core namespace that enables all tags and readers.
  "
  (:require
   [taggie.edn :as edn]
   [taggie.readers]
   [taggie.print]
   [taggie.pprint]))

(set! *warn-on-reflection* true)
