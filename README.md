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

Run that string in a repl, and it won't understand you:

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

- read
- write
- options

## Supported Types

- this
- that

## Adding Your Types

- example

## Misc

~~~
©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©
Ivan Grishaev, 2025. © UNLICENSE ©
©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©
~~~
