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

(ns gahelloworld.chromosome
  (:gen-class))

(def *target-str* (vec "Hello, world!"))

(defn- fitness [str]
  (let [len (count str)]
    (loop [fitness 0 idx 0]
	  (if (== idx len)
	    fitness
	    (recur (+ fitness (Math/abs (- (int (nth str idx))
					   (int (nth *target-str* idx)))))
		   (inc idx))))))

(defn- rand-gene [len]
  (vec (repeatedly len (fn [] (char (+ 32 (rand-int 90)))))))
	  
(defn mutate [c]
  (let [old (:gene c)
	idx (rand-int (count old))
	new (assoc old idx (char (mod (+ (int (nth old idx))
					 (+ 32 (rand-int 90))) 122)))]
    (assoc c :gene new
	     :fitness (fitness new))))

(defn mate [c1 c2]
  (let [gene1 (:gene c1)
	gene2 (:gene c2)
	pivot (rand-int (count gene1))
	child1 (into (drop pivot gene2) (reverse (take pivot gene1)))
	child2 (into (drop pivot gene1) (reverse (take pivot gene2)))]
    (vector
     (hash-map :gene child1 :fitness (fitness child1))
     (hash-map :gene child2 :fitness (fitness child2)))))

(defn generate
  ([]
     (let [gene (rand-gene (count *target-str*))]
       (generate gene)))
  ([gene]
     (hash-map :gene gene :fitness (fitness gene))))