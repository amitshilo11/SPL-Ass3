
#ifndef CLIENT_KEYBOARDINPUT_H
#define CLIENT_KEYBOARDINPUT_H

#include <mutex>
#include "ConnectionHandler.h"
using namespace std;


class keyBoardInput {
public:
    keyBoardInput(ConnectionHandler& connectionHandler, mutex& mutex);
    void run();
    short determineOperation(string);

private:
    ConnectionHandler& connectionHandler;
    mutex& threadLocker;
};
#endif //CLIENT_KEYBOARDINPUT_H
