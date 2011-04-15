# Genetic Algorithm Hello World! (Common Lisp Edition)

This is a simple project intended to showcase genetic algorithms with a well
known example for all new developers; namely the classic "Hello, World!"
example, written in Common Lisp.

## Overview

The application simply "evolves" the string "Hello, world!" from a population
of random strings.  It is intended to be a gentle introduction into the world
of genetic algorithms, specifically using Common Lisp.  The program itself is
really simple, and is contained within a single file.

The source is a pretty direct translation of the Python version using
Common Lisp idiom, barring some parts that weren't understood.  It has
not been optimized for speed.

You'll notice some repetition of TARGET and "Hello, World!", this is
my personal preference for (and verbose combination of) default values
and being explicit when calling functions.  It also helps for testing
on the REPL.

## Usage

The project is completely self contained.  To execute the application, run
the following command from a shell (assuming `clisp`, `ecl` or `sbcl`
is on your system path):

- CCL: my old 1.4 version fails, don't know why
- CLISP: `clisp -i ga-hello-world.lisp -x "(progn (main) (quit))"`
- ECL: `ecl -l ga-hello-world.lisp -eval "(progn (main) (quit))"`
- SBCL: `sbcl --load ga-hello-world.lisp --eval "(progn (main) (quit))"`

The more 'Lispy' way would be just starting you CL implementation and
issuing `(load "ga-hello-world.lisp")` and then calling the MAIN
function.

To run the tests you need the `lisp-unit` package.  To run them from
the commandline issue:

- CLISP: `clisp -i ga-hello-world-tests.lisp`
- ECL: `ecl -l ga-hello-world-tests.lisp`
- SBCL: `sbcl --load ga-hello-world-tests.lisp`

## Copyright and License

The MIT License

Copyright &copy; 2011 John Svazic, Erik Winkels

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
