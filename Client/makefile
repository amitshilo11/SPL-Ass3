CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -pthread -lboost_filesystem -lboost_thread

all:BGRSclient
	g++ -o bin/BGRSclient bin/ConnectionHandler.o bin/main.o bin/keyBoardInput.o bin/readFromSocket.o $(LDFLAGS)

BGRSclient : bin/ConnectionHandler.o bin/main.o bin/keyBoardInput.o bin/readFromSocket.o

bin/readFromSocket.o : src/readFromSocket.cpp
	g++ $(CFLAGS)  -o bin/readFromSocket.o src/readFromSocket.cpp

bin/keyBoardInput.o :  src/keyBoardInput.cpp
	g++ $(CFLAGS)  -o bin/keyBoardInput.o src/keyBoardInput.cpp

bin/main.o : src/main.cpp
	g++ $(CFLAGS)  -o bin/main.o src/main.cpp

bin/ConnectionHandler.o : src/ConnectionHandler.cpp
	g++ $(CFLAGS)  -o bin/ConnectionHandler.o src/ConnectionHandler.cpp

.PHONY: clean
clean:
	rm -f bin/*
