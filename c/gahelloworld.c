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
	int i;

	for (i = 0; i < strsize; i++) {
		*(result+i) = randchar();
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
fit_cmp(const void *el1, const void *el2)
{
	int a = fitness((char*)el1, target, el_sz);
	int b = fitness((char*)el2, target, el_sz);

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
mate(char *p)
{
	char *buffer = malloc(total_sz);
	size_t i, i1, i2;
	size_t skip = ELITE_RATE * POP_SZ * el_sz;

	memcpy(buffer, p, total_sz);

	for (i = skip; i <= total_sz-el_sz; i += el_sz) {
		i1 = el_sz * RANDBETWEEN(0, POP_SZ/2);
		i2 = el_sz * RANDBETWEEN(0, POP_SZ/2);

		strncpy(buffer + i, p + i1, el_sz);
		strncpy(buffer + i, p + i2, RANDBETWEEN(0, el_sz));

		if (rand() < MUTATION_RATE * RAND_MAX) {
			mutate(buffer + i);
		}
	}

	free(p);

	return buffer;
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

	srand((unsigned)time(NULL));
	target = EXAMPLE;

	run_tests();

	if (argc == 2) {
		target = argv[1];
	}

	int bestfit = RAND_MAX;

	size_t pop_sz = POP_SZ;
	el_sz = strlen(target);
	total_sz = pop_sz * el_sz;

	char *p = rndstr(CHARMAP, total_sz);

 
	while (bestfit) {
		qsort(p, total_sz/el_sz, el_sz, fit_cmp);
		i += 1;

		if (bestfit != fitness(p, target, el_sz)) {
			bestfit = fitness(p, target, el_sz);
			printf("[%03d] Best: (%04d)\t%.*s\n", i,
				bestfit, (int)el_sz, p);
		}

		p = mate(p);
	}
	return 0;
}
