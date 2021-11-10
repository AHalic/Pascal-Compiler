OBJS = ./build/main.o ./build/scanner.o

SOURCE = client/main.c src/scanner.c

OUT = compiler
CC	 = gcc
FLAGS = -c -DNDEBUG -std=gnu11 -I include
LFLAGS	 = -lm

all: | mkBuildDir $(OBJS)
	$(CC) $(OBJS) -o $(OUT) $(LFLAGS)

build/main.o: client/main.c
	$(CC) $(FLAGS) client/main.c -o ./build/main.o

build/scanner.o: src/scanner.c
	$(CC) $(FLAGS) src/scanner.c -o ./build/scanner.o

clean:
	rm -rf $(OUT) build/

mkBuildDir:
	@if [ ! -d "./build/" ]; then \
		mkdir build; \
	fi

run:
	./$(OUT)
