# Genetic Algorithm Hello World!

This is a simple project intended to showcase genetic algorithms with a well 
known example for all new developers; namely the classic "Hello, world!" 
example!

## Overview

The application simply "evolves" the string "Hello, world!" from a population 
of random strings.  It is intended to be a gentle introduction into the world
of genetic algorithms, using both Java and Clojure.  The programs themselves 
are really quite simple, and more complex topics like crossover selection 
using roulette wheel algorithms, insertion/deletion mutation, etc, have not 
been included.

### History

I've been working with Genetic Algorithms for a little while now and I
stubmled across a 
[C++ implemetation](http://www.generation5.org/content/2003/gahelloworld.asp) 
a while ago.  I decided to bring it back to life and migrate it to Java with 
my own enhancements.  This is far from ideal code, but it was designed to be 
a gentle introduction for newcomers to genetic algorithms.

### But why the <i>net.auxesia</i> package/namespace?

[Auxesia](http://www.theoi.com/Ouranios/HoraAuxesia.html) is the greek
goddess of spring growth, so when dealing with evolutionary programming like
genetic algorithms, the name just seemed to fit.  That and I was trying to be
witty with my naming, and [Dalek](http://en.wikipedia.org/wiki/Dalek) just 
didn't seem right.

## Usage

Take a look at the README files in [Java](GAHelloWorld/tree/master/java) and 
[Clojure](GAHelloWorld/tree/master/clojure) for the specifics for each language.

## TODO

*   Unit testing.  Lots of it, for both Clojure and Java.
*   Fix an outstanding bug in the Clojure code.  My Clojure kung fu is weak 
    here, so I'll be seeking assistance.

## Copyright and License

The MIT License

Copyright &copy; 2011 John Svazic

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