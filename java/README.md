# Genetic Algorithm Hello World! (Java Edition)

This is a simple project intended to showcase genetic algorithms with a well 
known example for all new developers; namely the classic "Hello, world!" 
example, written in Java.

## Overview

The application simply "evolves" the string "Hello, world!" from a population 
of random strings.  It is intended to be a gentle introduction into the world
of genetic algorithms, specifically with Java.  The program itself is really 
simple, and spans 3 classes.  Only two of them do any real interesting work;
the third one, <i>GAHelloWorld</i>, only exists as a driver for the 
application.

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