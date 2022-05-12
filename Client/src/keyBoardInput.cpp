#include "../include/keyBoardInput.h"
using namespace std;
keyBoardInput::keyBoardInput(ConnectionHandler& connectionHandler, std::mutex& mutex): connectionHandler(connectionHandler), threadLocker(mutex){}

void keyBoardInput::run() {
    while (1) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        int len = line.length();
        vector<string> format;
        int a = 0;
        int d = 0;
        int c = 0;
        while (a < len) {
            if (line.at(a) == ' ') {
                format.push_back(line.substr(c, d));
                c = a + 1;
                d = -1;
            }
            a++;
            d++;
        }
        format.push_back(line.substr(c));

        short operation = determineOperation(format[0]);
        char bytesArr[2];
        bytesArr[0] = ((operation >> 8) & 0xFF);
        bytesArr[1] = (operation & 0xFF);
        string message;
        message.append(1, bytesArr[0]);
        message.append(1, bytesArr[1]);
        if (operation == 5 || operation == 6 || operation == 7 || operation == 9 || operation == 10) {
            short b;
            b = (short) stoi(format[1]);
            bytesArr[0] = ((b >> 8) & 0xFF);
            bytesArr[1] = (b & 0xFF);
            message.append(1, bytesArr[0]);
            message.append(1, bytesArr[1]);
        } else {
            message = message + format.at(1);
            for (int i = 2; (unsigned) i < format.size(); ++i)
                message = message + '\0' + format.at(i);
        }
        if (!connectionHandler.sendLine(message)) {
            cout << "Disconnected. Exiting.../n" << endl;
            break;
        }
        if (message == "LOGOUT") {
            while (!threadLocker.try_lock()) {}
            break;
        }

    }
}

short keyBoardInput::determineOperation(string operation) {

    if (operation == "ADMINREG")
        return (short) 1;
    if (operation == "STUDENTREG")
        return (short) 2;
    if (operation == "LOGIN")
        return (short) 3;
    if (operation == "LOGOUT")
        return (short) 4;
    if (operation == "COURSEREG")
        return (short) 5;
    if (operation == "KDAMCHECK")
        return (short) 6;
    if (operation == "COURSESTAT")
        return (short) 7;
    if (operation == "STUDENTSTAT")
        return (short) 8;
    if (operation == "ISREGISTERED")
        return (short) 9;
    if (operation == "UNREGISTER")
        return (short) 10;
    if (operation == "MYCOURSES")
        return (short) 11;

    return (short) 0;
}

