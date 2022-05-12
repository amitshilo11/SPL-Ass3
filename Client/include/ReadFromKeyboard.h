
#ifndef CLIENT_READFROMKEYBOARD_H
#define CLIENT_READFROMKEYBOARD_H

#include "connectionHandler.h"
#include <mutex>
class ReadFromKeyboard {
public:
    ReadFromKeyboard(std::mutex& mutex, ConnectionHandler& connectionHandler);
    void run();
    short defineOp(std::string);

private:
    std::mutex& mutex;
    ConnectionHandler& cHandler;
};


#endif //CLIENT_READFROMKEYBOARD_H