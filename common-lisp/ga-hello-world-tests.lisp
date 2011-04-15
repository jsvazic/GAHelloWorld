;;;; The MIT License
;;;;
;;;; Copyright (c) 2011 John Svazic, Erik Winkels
;;;;
;;;; Permission is hereby granted, free of charge, to any person
;;;; obtaining a copy of this software and associated documentation
;;;; files (the "Software"), to deal in the Software without
;;;; restriction, including without limitation the rights to use,
;;;; copy, modify, merge, publish, distribute, sublicense, and/or sell
;;;; copies of the Software, and to permit persons to whom the
;;;; Software is furnished to do so, subject to the following
;;;; conditions:
;;;;
;;;; The above copyright notice and this permission notice shall be
;;;; included in all copies or substantial portions of the Software.
;;;;
;;;; THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
;;;; EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
;;;; OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
;;;; NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
;;;; HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
;;;; WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
;;;; FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
;;;; OTHER DEALINGS IN THE SOFTWARE.

;;;; author: Erik Winkels <aerique@xs4all.nl>
;;;;
;;;; See README.md for documentation.

(asdf:oos 'asdf:load-op :lisp-unit)
(use-package :lisp-unit)

(load "ga-hello-world.lisp")


;;; Specials

(defvar *target* "Hello, world!")


;;; Tests

(define-test fitness
  (let ((c1 (make-chromosome *target* *target*))
        (c2 (make-chromosome *target* "H5p&J;!l<X\\7l"))
        (c3 (make-chromosome *target* "Vc;fx#QRP8V\\$"))
        (c4 (make-chromosome *target* "t\\O`E_Jx$n=NF")))
    (assert-equal   0 (fitness c1))
    (assert-equal 399 (fitness c2))
    (assert-equal 297 (fitness c3))
    (assert-equal 415 (fitness c4))))


(define-test random-gene
  (loop repeat 1000
        for c = (make-chromosome *target*)
        do (assert-true (>= (fitness c) 0))
           (assert-equal 13 (length (gene c)))
           (loop for ch across (gene c)  ; immons
                 do (assert-true (>= (char-code ch) 32))
                    (assert-true (<= (char-code ch) 122)))))


(define-test mutate
  (loop repeat 1000
        for c1 = (make-chromosome *target*)
        for c2 = (mutate c1)
        do (assert-equal (length (gene c1)) (length (gene c2)))))
  ; skipping from Python tests, since I don't know what they do:
  ;
  ;     s1 = set(c1.gene)
  ;     s2 = set(c2.gene)
  ;     self.assertTrue(len(s1 - s2) <= 1)


(define-test mate
  (let* ((c1 (make-chromosome *target*))
         (c2 (make-chromosome *target*))
         (children (mate c1 c2)))
    (assert-equal 2 (length children))
    (assert-equal 13 (length (gene (elt children 0))))
    (assert-equal 13 (length (gene (elt children 1))))))
  ; needs test to check the genes


(define-test crossover
  (let ((p1 (make-population :crossover 0.8))
        (p2 (make-population :crossover 0.0))
        (p3 (make-population :crossover 1.0)))
    (assert-equal 0.8 (crossover p1))
    (assert-equal 0.0 (crossover p2))
    (assert-equal 1.0 (crossover p3))))


(define-test elitism
  (let ((p1 (make-population :elitism 0.1))
        (p2 (make-population :elitism 0.0))
        (p3 (make-population :elitism 0.99)))
    (assert-equal 0.1 (elitism p1))
    (assert-equal 0.0 (elitism p2))
    (assert-equal 0.99 (elitism p3))))


(define-test mutation
  (let ((p1 (make-population :mutation 0.05))
        (p2 (make-population :mutation 0.0))
        (p3 (make-population :mutation 1.0)))
    (assert-equal 0.05 (mutation p1))
    (assert-equal 0.0 (mutation p2))
    (assert-equal 1.0 (mutation p3))))


(define-test population
  (let* ((predicate (lambda (a b) (< (fitness a) (fitness b))))
         (p1 (make-population :size 1024))
         (p1c (copy-seq (chromosomes p1)))
         (p2 (make-population :size 2048))
         (p2c (copy-seq (chromosomes p2))))
    (assert-equal 1024 (size p1))
    (assert-equal 1024 (length (chromosomes p1)))
    (assert-equal (chromosomes p1) (sort p1c predicate))
    (assert-equal 2048 (size p2))
    (assert-equal 2048 (length (chromosomes p2)))
    (assert-equal (chromosomes p2) (sort p2c predicate))))


(define-test evolve
  (let* ((p (make-population :size 1024 :crossover 0.8 :elitism 0.1
                             :mutation 0.05))
         (pc (copy-seq (chromosomes p))))
    (evolve p)
    (assert-equal 0.8 (crossover p))
    (assert-equal 0.1 (elitism p))
    (assert-equal 0.05 (mutation p))
    (let ((elitism-count (floor (* 1024 0.1)))
          (counter 0))
      (loop for c in pc
            do (when (member c (chromosomes p))
                 (incf counter)))
      (assert-true (>= counter elitism-count))
      (assert-true (< counter (length pc))))))


;;; Run the tests.

(format t "--- running tests ---~%")
(run-tests)
(format t "~&")
(quit)
