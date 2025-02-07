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

Taggie overrides various printing methods such that types which cannot be read
from their representation *now can* be read. A quick example: if you print an
atom, you'll get weird string:

~~~clojure
(atom 42)
#<Atom@7fea5978: 42>
~~~

Paste that string into the repl, and it will end up with an exception:

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
#atom 42
#atom 42
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

And it's not only about atoms! Taggie extends many times, e.g. refs, native Java
arrays, File, URI, URL, Date, java.time.* classes, and something else. See the
corresponding section below.

## Installation and Usage

## EDN Support

## Supported Types

## Adding Your Types

## Misc

©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©
Ivan Grishaev, 2025. © UNLICENSE ©
©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©
