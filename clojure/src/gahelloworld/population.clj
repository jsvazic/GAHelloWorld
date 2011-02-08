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

(ns gahelloworld.population
  (:require [gahelloworld.chromosome :as chromosome])
  (:gen-class))

(def *tournament-size* 64)

(defn generate [size crossover elitism mutation]
  (let [chromosomes (sort-by
		     (fn [x] (:fitness x))
		     (vec (repeatedly size chromosome/generate)))]
    (hash-map :crossover crossover
	      :elitism elitism
	      :mutation mutation
	      :population chromosomes)))

(defn best [p]
  (first (:population p)))

(defn- tournament-selection [population]
  (let [pop-size (count (:population population))]
    (loop [best (nth (:population population) (rand-int pop-size))
	   i 0]
      (if (= i *tournament-size*)
	best
	(let [contender (nth (:population population) (rand-int pop-size))
	      new-best (if (<= (:fitness best) (:fitness contender))
			  best
			  contender)]
	  (recur new-best (inc i)))))))

(defn evolve [population]
  (let [chromosomes (:population population)
        size (count chromosomes)
	elitism-size (int (Math/round (* (:elitisim population) size)))
	r-mutate (fn [x] (if (<= (rand) (:mutation population))
			   (chromosome/mutate x)
			   x))]
    (loop [buffer (take elitism-size chromosomes)
	   idx (inc elitism-size)]
      (if (<= size (count buffer))
	(assoc population :population
	       (sort-by (fn [x] (:fitness x)) (doall (take size buffer))))
	(if (<= (rand) (:crossover population))
	  ;; Perform a crossover
	  (let [c1 (tournament-selection chromosomes)
		c2 (tournament-selection chromosomes)
		children (chromosome/mate c1 c2)]
	    (recur (conj buffer (doall (map #(r-mutate %) children)))
		   (inc (inc idx))))
	  ;; Perform a straight copy with mutation
	  (recur (conj buffer (r-mutate (nth chromosomes idx)))
		 (inc idx)))))))