package main

type population []chromosome

func (p population) Len() int {
	return len(p)
}

func (p population) Swap(i, j int) {
	p[i], p[j] = p[j], p[i]
}

func (p population) Less(i, j int) bool {
	return p[i].fitness < p[j].fitness
}
