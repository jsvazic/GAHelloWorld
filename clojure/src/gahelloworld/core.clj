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

(ns gahelloworld.core
  (:require [gahelloworld.population :as population]
	    [gahelloworld.chromosome :as chromosome])
  (:gen-class))

(defn -main [& args]
  (let [size 2048
	crossover-ratio 0.8
	elitism-ratio 0.1
	mutation-ratio 0.03
	max-generations 16384]
    (loop [generation 0
	   pop (population/generate size
				    crossover-ratio
				    elitism-ratio
				    mutation-ratio)]
      (let [best (population/best pop)]
	(do
	  (println "Generation" generation (:gene best))
	  (cond
	   (= 0 (:fitness best)) best
	   (>= generation max-generations) best
	   :default (recur (inc generation) (population/evolve pop))))))))