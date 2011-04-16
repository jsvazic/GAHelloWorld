{-# LANGUAGE NamedFieldPuns, TupleSections #-}
-- |A Haskell program that demonstrates a simple "Hello, world!"
-- application using genetic algorithms. Based on code by John Svazic.
--
-- Author: Anthony Cowley

-- The MIT License
-- 
-- Copyright &copy; 2011 Anthony Cowley
-- 
-- Permission is hereby granted, free of charge, to any person obtaining a copy
-- of this software and associated documentation files (the "Software"), to deal
-- in the Software without restriction, including without limitation the rights
-- to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
-- copies of the Software, and to permit persons to whom the Software is
-- furnished to do so, subject to the following conditions:
-- 
-- The above copyright notice and this permission notice shall be included in
-- all copies or substantial portions of the Software.
-- 
-- THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
-- IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
-- FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
-- AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
-- LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
-- OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
-- THE SOFTWARE.

import Control.Applicative
import Control.Arrow (second)
import Control.Monad (liftM, replicateM)
import Control.Monad.Random
import Data.Function (on)
import Data.List (minimumBy, sortBy, nub, (\\))
import Data.Ord (comparing)
import Text.Printf (printf)
import Test.QuickCheck
import Test.QuickCheck.Monadic
import Test.HUnit hiding (assert)

type Gene = String

target :: Gene
target = "Hello, world!"

-- |Two 'Gene's mate to produce two new 'Gene's.
mate :: RandomGen g => Gene -> Gene -> Rand g Gene
mate g1 g2 = (++) <$> flip take g1 <*> flip drop g2 <$> pivot
  where pivot = getRandomR (0, length g1 - 1)

-- |Change a random character in a 'Gene'.
mutate :: RandomGen g => Gene -> Rand g Gene
mutate g = (uncurry (++) .) . second . (:) <$> delta <*> parts
  where delta = getRandomR (' ', 'z')
        idx = getRandomR (0, length g - 1)
        parts = second tail . flip splitAt g <$> idx

-- |A 'Gene''s fitness is a measure of its distance from the 'target'.
fitness :: Gene -> Int
fitness = sum . map abs . zipWith ((-) `on` fromEnum) target

-- |Utility to produce a random 'Gene'.
randomGene :: RandomGen g => Rand g Gene
randomGene = replicateM (length target) $ getRandomR (' ', 'z')

data PopInfo = PopInfo { size      :: Int
                       , crossover :: Float
                       , elitism   :: Float
                       , mutation  :: Float }

-- |A 'Population' is a pair of a record describing the population,
-- and how it evolves, and a collection of 'Gene's.
type Population = (PopInfo, [Gene])

-- |Default 'PopInfo'.
defaultPop :: PopInfo
defaultPop = PopInfo 1024 0.8 0.1 0.03

tournamentSize :: Int
tournamentSize = 3

-- |Helper to produce a randomized initial population.
randomPop :: RandomGen g => PopInfo -> Rand g Population
randomPop = liftA2 (,) <$> pure <*> flip replicateM randomGene . size

-- |Tournament selection method to select a 'Gene' from a 'Population'.
tournamentSelection :: RandomGen g => Population -> Rand g Gene
tournamentSelection (info, genes) =  
  minimumBy (comparing fitness) .  map (genes !!) <$>
  replicateM tournamentSize (getRandomR (0, size info - 1))

-- Utility for executing a monadic action twice.
twoM :: Monad m => m a -> m (a, a)
twoM = liftM (\[x,y] -> (x,y)) . replicateM 2

-- |Select two parents from a 'Population'.
selectParents :: RandomGen g => Population -> Rand g (Gene, Gene)
selectParents = twoM . tournamentSelection

-- |Run one generation of evolution for a 'Population'.
evolve :: RandomGen g => Population -> Rand g Population
evolve p@(info@(PopInfo {size, crossover, elitism, mutation}), genes) = 
  (info,) . sortBy (comparing fitness) . (take idx genes ++) <$>
  replicateM (size - idx) (twoM getRandom >>= go)
  where idx = round (fromIntegral size * elitism)
        go (r1,r2) | r1 <= crossover = 
                     selectParents p >>= uncurry mate >>= addChild r2
                   | otherwise = addMutation r2
        addChild r c
          | r <= mutation = mutate c
          | otherwise = return c
        addMutation r
          | r <= mutation = mutate . (genes !!) =<< getRandomR (idx, size - 1)
          | otherwise = (genes !!) <$> getRandomR (idx, size - 1)

-- Utility for iterating a monadic function on a seed until the seed
-- passes a predicate.
iterateUntil :: Monad m => (a -> Bool) -> (a -> m a) -> a -> m a
iterateUntil stop f = go
  where go x | stop x = return x
             | otherwise = f x >>= go

maxGenerations :: Int
maxGenerations = 16384

-- Run the genetic algorithm
main = evalRandIO (randomPop defaultPop >>= iterateUntil done step . (, 0))
       >>= result
  where step (p,gen) = (,) <$> evolve p <*> pure (gen+1)
        done ((_, g:_), generation) = 
          generation == maxGenerations || fitness g == 0
        result ((_, g:_), generation)
          | generation == maxGenerations = 
            putStrLn "Maximum generations reached without success."
          | fitness g == 0 = printf "Reached target (%d): %s\n" generation g
          | otherwise = putStrLn "Evolution is hard. Let's go shopping."

--------------------------------- TESTS ----------------------------------------

testGen = run (evalRandIO randomGene) >>= assert . check
  where check g = and $ map ($ g) [ (>= 0) . fitness
                                  , (== 13) . length
                                  , all (between 32 122 . fromEnum) ]
        between l r x = l <= x && x <= r

testMut = run (evalRandIO $ randomGene >>= pairWithMutant) >>= assert . check
  where pairWithMutant = liftA2 (,) <$> pure <*> mutate
        check (g,m) = length g == length m && length (nub g \\ nub m) <= 1

testMate = run (evalRandIO $ twoM randomGene >>= pairWithChild) >>= 
           assert . check
  where pairWithChild (mom,dad) = (mom,dad,) <$> mate mom dad
        check (m,d,c) = length c == 13 && 
                        (and . map (\(_,y,z) -> y == z) . 
                         dropWhile (\(x,y,_) -> x == y) $ zip3 m c d)

unitTests = test [ "fitness1" ~: 0 ~=? fitness "Hello, world!" 
                 , "fitness2" ~: 399 ~=? fitness "H5p&J;!l<X\\7l"
                 , "fitness3" ~: 297 ~=? fitness "Vc;fx#QRP8V\\$"
                 , "fitness4" ~: 415 ~=? fitness "t\\O`E_Jx$n=NF" ]

runTests = do mapM_ (quickCheck . monadicIO) [testGen, testMut, testMate]
              runTestTT unitTests