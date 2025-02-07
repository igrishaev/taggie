# Taggie

An experimental library to find an answer for a strange question: is it possible
to benefit from Clojure tags and readers, and how?

**Table of Contents**

<!-- toc -->

- [WTF](#wtf)
- [Installation and Usage](#installation-and-usage)
- [EDN Support](#edn-support)
- [Supported Types](#supported-types)
- [Adding Your Types](#adding-your-types)
- [Misc](#misc)

<!-- tocstop -->

---

## WTF

Taggie overrides various printing methods such that types that cannot be read
from their representation **now can** be read. A quick example: if you print an
atom, you'll get a weird string:

~~~clojure
(atom 42)
#<Atom@7fea5978: 42>
~~~

Run that string, and REPL won't understand you:

~~~clojure
#<Atom@7fea5978: 42>
Syntax error reading source at (REPL:962:5).
Unreadable form
~~~

With Taggie, it goes this way:

~~~clojure
(atom 42)
#atom 42 ;; represented with a tag
~~~

And vice versa:

~~~clojure
#atom 42 ;; run it in repl
#atom 42 ;; the result
~~~

The value is an atom indeed, you can check it:

~~~clojure
(deref #atom 42)
42
~~~

Tags can be nested. Let's try some madness:

~~~clojure
(def omg #atom #atom #atom #atom #atom #atom 42)

(println omg)
#atom #atom #atom #atom #atom #atom 42

@@@@@@omg
42
~~~

And this is not only about atoms! Taggie extends many types, e.g. refs, native
Java arrays, `File`, `URI`, `URL`, `Date`, `java.time.*` classes, and something
else. See the corresponding section below.

## Installation and Usage

Add this to your project:

~~~clojure
;; lein
[com.github.igrishaev/taggie "0.1.0-SNAPSHOT"]

;; deps
com.github.igrishaev/taggie {:mvn/version "0.1.0-SNAPSHOT"}
~~~

Then import the core namespace:

~~~clojure
(ns com.acme.server
  (:require
    taggie.core))
~~~

Now type tags in the repl:

~~~clojure
#LocalDate "2025-01-01"
#Instant "2025-01-01T23:59:59Z"
#File "/path/to/a/file.txt"
#URL "https://clojure.org"
#bytes [0x00 0xff]
#ints [1 2 3]
#floats [1 2 3]
#ByteBuffer [0 1 2 3 4]
...
~~~

For each expression, you'll get an instance of a corresponding type: A
`LocalDate`, an `Instane`, a `File`, etc... `#bytes`, `#ints` and similar
produce native Java arrays.

You can pass tagged values into functions as usual:

~~~clojure
(deref #atom 42)
42

(alength #longs [1 2 3])
3
~~~

To observe what happends under the hood, prepend your expression with a
backtick:

~~~clojure
`(alength #longs [1 2 3])

(clojure.core/alength (taggie.readers/__reader-longs-edn [1 2 3]))
~~~

Internally, all tags expand into an invocation of an EDN reader. Namely, `#longs
items` becomes `(taggie.readers/__reader-longs-edn items)`, and when evaluated,
it returs a native array if longs.

## EDN Support

Taggie provides functions to read and write EDN with these tags. They live in
the `taggie.edn` namespace. Use it as follows:

~~~clojure
(def edn-dump
  (taggie.edn/write-string #atom {:test 1
                                  :values #longs [1 2 3]
                                  :created-at #LocalDate "2025-01-01"}))

(println edn-dump)

;; #atom {:test 1,
;;        :values #longs [1, 2, 3],
;;        :created-at #LocalDate "2025-01-01"}
~~~

It produces a string with custom tags and data being pretty printed. Let's read
it back:

~~~clojure
(taggie.edn/read-string edn-dump)

#atom {:test 1,
       :values #longs [1, 2, 3],
       :created-at #LocalDate "2025-01-01"}
~~~

The `write` function writes EDN into a destination which might be a file path, a
file, an output stream, a writer, etc:

~~~clojure
(taggie.edn/write (clojure.java.io/file "data.edn")
                  {:test (atom (ref (atom :secret)))})
~~~

The `read` function reads from any kind of source: a file path, a file, in input
stream, a reader, etc. Internally, a source is transformed into the
`PushbackReader` instance:

~~~clojure
(taggie.edn/read (clojure.java.io/file "data.edn"))

{:test #atom #ref #atom :secret}
~~~

Both `read` and `read-string` accept standard `clojure.edn/read` options,
e.g. `:readers`, `:eof`, etc. The `:readers` map gets merged with a global map
of custom tags.

## Motivation

Aside from jokes, this library might save your day. I often see how people dump
data into .edn files, and the data has atoms, regular expressions, exceptions,
and other unreadable types:

~~~clojure
(spit "data.edn"
      (with-out-str
        (clojure.pprint/pprint
          {:regex #"foobar"
           :atom (atom 42)
           :error (ex-info "boom" {:test 1})})))

(println (slurp "data.edn"))

{:regex #"foobar", :atom #<Atom@4f7aa8aa: 42>, :error #error {
 :cause "boom"
 :data {:test 1}
 :via
 [{:type clojure.lang.ExceptionInfo
   :message "boom"
   :data {:test 1}
   :at [user$eval43373$fn__43374 invoke "form-init6283045849674730121.clj" 2248]}]
 :trace
 [[user$eval43373$fn__43374 invoke "form-init6283045849674730121.clj" 2248]
  [user$eval43373 invokeStatic "form-init6283045849674730121.clj" 2244]
  ;; truncated
  [clojure.lang.AFn run "AFn.java" 22]
  [java.lang.Thread run "Thread.java" 833]]}}
~~~

This dump cannot be read back due to:

1. unknown `#"foobar"` tag (EDN doesn't support regex);
2. broken `#<Atom@4f7aa8aa: 42>` expression;
3. unknown `#error` tag.

But with Taggie, the same data will produce tagged fields that can be read back.

## Supported Types

In alphabetic order:

| Type                       | Example                                                           |
|----------------------------|-------------------------------------------------------------------|
| `java.nio.ByteBuffer`      | `#ByteBuffer [0 1 2]`                                             |
| `java.util.Date`           | `#Date "2025-01-06T14:03:23.819Z"`                                |
| `java.time.Duration`       | `#Duration "PT72H"`                                               |
| `java.io.File`             | `#File "/path/to/file.txt"`                                       |
| `java.time.Instant`        | `#Instant "2025-01-06T14:03:23.819994Z"`                          |
| `java.time.LocalDate`      | `#LocalDate "2034-01-30"`                                         |
| `java.time.LocalDateTime`  | `#LocalDateTime "2025-01-08T11:08:13.232516"`                     |
| `java.time.LocalTime`      | `#LocalTime "20:30:56.928424"`                                    |
| `java.time.MonthDay`       | `#MonthDay "--02-07"`                                             |
| `java.time.OffsetDateTime` | `#OffsetDateTime "2025-02-07T20:31:22.513785+04:00"`              |
| `java.time.OffsetTime`     | `#OffsetTime "20:31:39.516036+03:00"`                             |
| `java.time.Period`         | `#Period "P1Y2M3D"`                                               |
| `java.net.URI`             | `#URI "foobar://test.com/path?foo=1"`                             |
| `java.net.URL`             | `#URL "https://clojure.org"`                                      |
| `java.time.Year`           | `#Year "2025"`                                                    |
| `java.time.YearMonth`      | `#YearMonth "2025-02"`                                            |
| `java.time.ZoneId`         | `#ZoneId "Europe/Paris"`                                          |
| `java.time.ZoneOffset`     | `#ZoneOffset "-08:00"`                                            |
| `java.time.ZonedDateTime`  | `#ZonedDateTime "2025-02-07T20:32:33.309294+01:00[Europe/Paris]"` |
| `clojure.lang.Atom`        | `#atom {:inner 'state}`                                           |
| `boolean[]`                | `#booleans [true false]`                                          |
| `byte[]`                   | `#bytes [1 2 3]`                                                  |
| `char[]`                   | `#chars [\a \b \c]`                                               |
| `double[]`                 | `#doubles [1.1 2.2 3.3]`                                          |
| `Throwable->map`           | `#error <result of Throwable->map>` (see below)                   |
| `float[]`                  | `#floats [1.1 2.2 3.3]`                                           |
| `int[]`                    | `#ints [1 2 3]`                                                   |
| `long[]`                   | `#longs [1 2 3]`                                                  |
| `Object[]`                 | `#objects ["test" :foo 42 #atom false]`                           |
| `clojure.lang.Ref`         | `#ref {:test true}`                                               |
| `java.util.regex.Pattern`  | `#regex "vesion: \d+"`                                            |
| `java.sql.Timestamp`       | `#sql/Timestamp "2025-01-06T14:03:23.819Z"`                       |

The `#error` tag is a bit special: it returns a value as is with no parsing. It
serves to prevent an error when reading the result of printing of an exception:

~~~clojure
(println (ex-info "boom" {:test 123}))

#error {
 :cause boom
 :data {:test 123}
 :via
 [{:type clojure.lang.ExceptionInfo
   :message boom
   :data {:test 123}
   :at [taggie.edn$eval9263 invokeStatic form-init2367470449524935680.clj 97]}]
 :trace
 [[taggie.edn$eval9263 invokeStatic form-init2367470449524935680.clj 97]
  [taggie.edn$eval9263 invoke form-init2367470449524935680.clj 97]
  ;; truncated
  [java.lang.Thread run Thread.java 833]]}
~~~

When reading such data from EDN, you'll get a regular map.

## Adding Your Types

- example
- add test

## Misc

~~~
©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©
Ivan Grishaev, 2025. © UNLICENSE ©
©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©
~~~
