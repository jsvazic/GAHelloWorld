# Genetic Algorithm Hello World!

This is a simple project intended to showcase genetic algorithms with a well 
known example for all new developers; namely the classic "Hello, world!" 
example!

## Overview

The application simply "evolves" the string "Hello, world!" from a population 
of random strings.  It is intended to be a gentle introduction into the world
of genetic algorithms, specifically with Java.  The program itself is really 
simple, and more complex topics like crossover selection using roulette wheel
algorithms, etc, have not been included (yet).

### History

I've been working with Genetic Algorithms for a little while now and I
stubmled across a 
[C++ implemetation](http://www.generation5.org/content/2003/gahelloworld.asp) 
a while ago.  I decided to bring it back to life and migrate it to Java with 
my own enhancements.  This is far from ideal code, but it was designed to be a 
gentle introduction for newcomers to genetic algorithms.

### But why the <i>net.auxesia</i> package?

[Auxesia](http://www.theoi.com/Ouranios/HoraAuxesia.html) is the greek
goddess of spring growth, so when dealing with evolutionary programming like
genetic algorithms, the name just seemed to fit.  That and I was trying to be
witty with my naming, and [Dalek](http://en.wikipedia.org/wiki/Dalek) just 
didn't seem right.

## Usage

The project currently uses [Maven](http://maven.apache.org) for the build.
To build it locally, run the following from a shell:

> mvn clean install

To execute the simulation, change to the <i>target</i> directory and run:

> java -jar gahelloworld-1.0-SNAPSHOT.jar

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