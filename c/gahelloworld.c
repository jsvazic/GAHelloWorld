// Inspired by rheide/Hello-genetic-algorithm

#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <unistd.h>

#define POP_SZ 2048
#define ELITE_RATE 0.1
#define MUTATION_RATE 0.25
#define TOURN_SZ 3
#define EXAMPLE "Hello, world!"
#define CHARMAP "abcdefghijklmnopqrstuvwxyz"\
		"ABCDEFGHIJKLMNOPQRSTUVWXYZ"\
		"0123456789"\
		"!@#$%%^&*(_-)+=[]{}<>|\\;:'\",./?~` "

#define RANDBETWEEN(A,B) A + rand()/(RAND_MAX/(B - A))

static char* target;
static size_t el_sz;
static size_t total_sz;

static char
rndchr(char* map)
{
	return *(map+RANDBETWEEN(0, strlen(map)));
}

static char
randchar()
{
	return rndchr(CHARMAP);
}

static char*
rndstr(char* map, size_t strsize)
{
	char *result = malloc(strsize);
	size_t i;

	for (i = 0; i < strsize; i++) {
		*(result+i) = rndchr(map);
	};

	return result;
}

static int
fitness(char* str, char* gauge, size_t n)
{
	int i;
	int result = 0;

	for (i = n-1; i >= 0; i--) {
		result += abs(str[i]-gauge[i]);
	}

	return result;
}

static int
_fitness(char* str)
{
	return fitness(str, target, el_sz);
}

static int
fit_cmp(const void *el1, const void *el2)
{
	int a = _fitness((char*)el1);
	int b = _fitness((char*)el2);

	if (a > b) return 1;
	if (a < b) return -1;
	return 0;
}

static void
mutate(char *p)
{
	*(p + RANDBETWEEN(0, el_sz)) = randchar();
}

static char*
rnd_el(char *p)
{
	return p + el_sz * (int)(RANDBETWEEN(0, POP_SZ));
}

static char*
trnmnt(char *p)
{
	size_t i;
	char* winner = rnd_el(p);
	char* challenger;
	int f1 = _fitness(winner);
	int f2;

	for (i = TOURN_SZ; i > 0; i--) {
		challenger = rnd_el(p);
		f2 = _fitness(challenger);
		if (f2 < f1) {
			f1 = f2;
			winner = challenger;
		}
	}

	return winner;
}

static void
mate(char *p, char *buffer)
{
	char *a, *b;
	size_t i, pivot;
	size_t skip = (size_t)(ELITE_RATE * POP_SZ) * el_sz;
	memcpy(buffer, p, total_sz);

	for (i = skip; i <= total_sz-el_sz; i += el_sz) {
		a = trnmnt(p);
		b = trnmnt(p);
		pivot = RANDBETWEEN(0, el_sz);

		strncpy(buffer + i, a, el_sz);
		strncpy(buffer + i, b, pivot);

		if (i < total_sz - el_sz) {
			i += el_sz;
			strncpy(buffer + i, b, el_sz);
			strncpy(buffer + i, a, pivot);
		}

		if (rand() < MUTATION_RATE * RAND_MAX) {
			mutate(buffer + i);
		}
	}

	memcpy(p, buffer, total_sz);
}

static void
run_tests(void)
{
	assert(000 == fitness("Hello, world!", "Hello, world!", 13));

	assert(399 == fitness("H5p&J;!l<X\\7l", "Hello, world!", 13));
	assert(297 == fitness("Vc;fx#QRP8V\\$", "Hello, world!", 13));
	assert(415 == fitness("t\\O`E_Jx$n=NF", "Hello, world!", 13));
}

int main(int argc, char **argv)
{
	int i = 0;
	int bestfit = RAND_MAX;

	run_tests();

	srand((unsigned)time(NULL));
	target = (argc == 2) ? argv[1] : EXAMPLE;
	el_sz = strlen(target);
	total_sz = POP_SZ * el_sz;
	char *p = rndstr(CHARMAP, total_sz);
	char *b = malloc(total_sz);

	while (bestfit) {
		qsort(p, POP_SZ, el_sz, fit_cmp);
		i += 1;

		if (bestfit != _fitness(p)) {
			bestfit = _fitness(p);
			printf("[%03d] Best: (%04d)\t%.*s\n", i,
				bestfit, (int)el_sz, p);
		}

		mate(p, b);
	}

	free(p);
	free(b);

	return 0;
}
