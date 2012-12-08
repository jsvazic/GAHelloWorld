module GAHelloWorld
  RAND_SEED=Time.now.to_f
  TARGET_GENE='Hello World!'
  ALLOWED_LETTERS = (32.chr..122.chr).to_a

  class Chromosome
    attr_reader :gene_ary, :target_ary, :gene

    class << self
      def gen_random()
        str=''
        TARGET_GENE.size.times do |i|
          str << ALLOWED_LETTERS[rand(ALLOWED_LETTERS.size)]
        end
        Chromosome.new(str)
      end 
      def to_int_array(str)
        #convenience method to get an array of strings for any string, compatible with ruby 1.9.3 and 1.8.7
        out=[]
          str.each_byte do |c| out << c end 
        out 
      end
    end

    def initialize(str)
      @gene=str
      @gene_ary ||= Chromosome.to_int_array(@gene)
      @target_ary ||= Chromosome.to_int_array(TARGET_GENE)
    end 

    def fitness
      @fitness ||= 
        begin
          diff=0
          gene_ary.size.times do |i| diff += (gene_ary[i].to_i - target_ary[i].to_i).abs  end
          diff
        end
    end 

    def mate partner
      #split the chromosome at some random point. 
      #create two new chromosomes and return them. 
      # chrom1 gets the first half from itself and the second from the partner
      # chrom2 gets the first half from the partner and the second from itself
      pivot = rand( gene_ary.size -1 )
      ng1= gene_ary[0..pivot] + partner.gene_ary[pivot+1..gene_ary.size]
      ng2= partner.gene_ary[0..pivot] + gene_ary[pivot+1..gene_ary.size]
      ng1 = ng1.map do |i| i.chr end.join 
      ng2 = ng2.map do |i| i.chr end.join
      [ Chromosome.new(ng1) , Chromosome.new(ng2) ]
    end 

    def mutate
      newstr=@gene.clone
      newstr[rand(@gene.size)] = ALLOWED_LETTERS[ rand(ALLOWED_LETTERS.size) ]
      Chromosome.new newstr
    end     
    

  end

  class Population
    attr_accessor :population
    
    def each(&block)
      @population.each do |i| 
        block.call(i)
      end
    end 

    def initialize(size=1024, crossover=0.8, elitism=0.1, mutation=0.03)
      @@tournamentSize = 3
      @size = size
      @crossover=crossover
      @elitism=elitism
      @mutation=mutation
      buf = []
      @size.times do |i|
        buf << Chromosome::gen_random()
      end 
      srand RAND_SEED
      @population = buf.sort!{ |a,b| a.fitness <=> b.fitness }
    end 

    def tournament_selection
      best = @population[rand(@population.size)]
      tournament_selection.times do |i|
        cont = @population[rand(@population.size)]
        best = cont if cont.fitness < best.fitness
      end 
      best
    end 

    def evolve
      sub_pop=@population[0..(@elitism*@population.size).to_i]
      sub_pop.each do |chrom|
        if rand <= @crossover
          parent1=tournament_selection
          parent2=tournament_selection
          children = parent1.mate parent2
          children.first = children.first.mutate if rand < @mutation
          children.last = children.last.mutate if rand < @mutation
        else
          sub_pop = sub_pop.mutate if rand < @mutation
        end       
      end  
      sub_pop.sort!{|a,b| a.fitness <=> b.fitness}     
    end 
  end 
end 

max_generations = 16384
pop = GAHelloWorld::Population.new(size=2048, crossover=0.8, elitism=0.1, mutation=0.3)
  curgen = 1
  begin
    finished=false
    # print("Generation #{curgen}: #{pop.first.gene}" 

    curgen += 1
  end while curgen <= 16384 && !finished

  # puts pop.population.inspect
  
  ind ||= -1 
  pop.each do |chrome|
    ind += 1  
    puts "[" + ind.to_s + "] "+chrome.gene + ": fitness => " + chrome.fitness.to_s
  end