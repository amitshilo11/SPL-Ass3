
#ifndef CLIENT_READFROMSOCKET_H
#define CLIENT_READFROMSOCKET_H

#include <mutex>
#include "ConnectionHandler.h"
using namespace std;

class readFromSocket {
public:
    readFromSocket(ConnectionHandler& connectionHandler, mutex& threadLocker);
    void run();

private:
    ConnectionHandler& connectionHandler;
    mutex& threadLocker;
};
#endif //CLIENT_READFROMSOCKET_H