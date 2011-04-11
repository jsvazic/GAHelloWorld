;; The MIT License
;; 
;; Copyright (c) 2011 John Svazic
;; 
;; Permission is hereby granted, free of charge, to any person obtaining a copy
;; of this software and associated documentation files (the "Software"), to deal
;; in the Software without restriction, including without limitation the rights
;; to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
;; copies of the Software, and to permit persons to whom the Software is
;; furnished to do so, subject to the following conditions:
;; 
;; The above copyright notice and this permission notice shall be included in
;; all copies or substantial portions of the Software.
;; 
;; THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
;; IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
;; FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
;; AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
;; LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
;; OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
;; THE SOFTWARE.

(ns net.auxesia.core
  "This namespace defines the main driver for the 'Hello, world!'
   genetic algorithm application."
  (:require [net.auxesia.population :as population]
            [net.auxesia.chromosome :as chromosome])
  (:gen-class))

(defn -main
  "This is the main function used for command-line execution."
  [& args]
  (let [size (int 2048)
        crossover (float 0.8)
		elitism (float 0.01)
		mutation (float 0.03)
		max-gen (int 16384)]
    (loop [g (int 0)
	       p (population/generate size crossover elitism mutation)]
      (let [best (first (:population p))]
	    (do
	      (println "Generation" g (apply str (:gene best)))
	      (cond
	        (== 0 (:fitness best)) best
	        (>= g max-gen) best
	        :default (recur (inc g) (population/evolve p))))))))