
#include "../include/readFromSocket.h"
using namespace std;

readFromSocket::readFromSocket(ConnectionHandler& connectionHandler, mutex& threadLocker): connectionHandler(connectionHandler), threadLocker(threadLocker){}

void readFromSocket::run(){
    threadLocker.lock();
    while(1){
        string message;
        if(!(connectionHandler.getLine(message))) {
            cout << "Disconnected. Exiting.../n" << endl;
            break;
        }

        string subMessage = message.substr(0,2);
        int i = 0;
        if (subMessage == "12" || subMessage == "13") {
            i = i + 2;
            if (subMessage == "12")
                subMessage = "ACK ";
            if (subMessage == "13")
                subMessage = "ERROR ";
            if (message.at(2) == '1') {
                if (message.size() == 3) {
                    subMessage.append("1");
                    i++;
                }
                else {
                    subMessage.append(message.substr(2, 2));
                    i = i + 2;
                }
            } else {
                subMessage.append(message.substr(2, 1));
                i++;
            }
            cout << subMessage << endl;
        }
        if (!message.substr(i).empty())
            cout << subMessage << endl;
        if (subMessage == "ACK 4") {
            threadLocker.unlock();
            break;
        }

    }

}
