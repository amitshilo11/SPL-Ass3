#include <iostream>
#include <string>
#include <future>
#include <chrono>
#include <thread>
#include <stdlib.h>
#include "../include/keyBoardInput.h"
#include "../include/readFromSocket.h"
#include "../include/ConnectionHandler.h"

using namespace std;
using std::cout;
using std::cin;
using std::thread;
using std::string;
using std::endl;
/*
int stopflag = 0;

class inTask{
private:
    ConnectionHandler &connectionHandler;
public:
    inTask (ConnectionHandler &connectionHandler) : connectionHandler(connectionHandler) {}

    void run(){
        while(true && !stopflag)
        {
            string input;
            if (!connectionHandler.getLine(input)) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
            cout << "Input: " << input << endl;
        }
    }
};

class outTask{
private:
    ConnectionHandler &connectionHandler;
public:
    outTask (ConnectionHandler &connectionHandler) : connectionHandler(connectionHandler) {}

    void run(){
        while (true && !stopflag) {
            std::this_thread::sleep_for(std::chrono::seconds(1));
            //cout << "Output thread\n";
            const short bufsize = 1024;
            char buf[bufsize]; //need to be byte buffer
            std::cin.getline(buf, bufsize);
            std::string line(buf);
            int len = line.length();
            if (!connectionHandler.sendLine(line)) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
            std::cout << "Sent " << len+1 << " bytes to server" << std::endl;
        }
    }
};
*/
/*
void input_func(ConnectionHandler &connectionHandler)
{
    while(true && !stopflag)
    {
        string input;
        if (!connectionHandler.getLine(input)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        cout << "Input: " << input << endl;
    }
}

void output_func(ConnectionHandler& connectionHandler) {
    while (true && !stopflag) {
        std::this_thread::sleep_for(std::chrono::seconds(1));
        //cout << "Output thread\n";
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        int len = line.length();
        if (!connectionHandler.sendLine(line)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        std::cout << "Sent " << len+1 << " bytes to server" << std::endl;
    }
}
*/



int main (int argc, char *argv[]) {
    // Set console code page to UTF-8 so console known how to interpret string data

    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    mutex threadLocker;
    keyBoardInput keyBoardInput(connectionHandler, threadLocker);
    readFromSocket readFromSocket(connectionHandler, threadLocker);
    thread thread1(&keyBoardInput::run, &keyBoardInput);
    thread thread2(&readFromSocket::run, &readFromSocket);
    thread1.join();
    thread2.join();
    return 0;
}
    /*

    thread inp(input_func, &connectionHandler);
    thread outp(output_func, connectionHandler);

    std::this_thread::sleep_for (std::chrono::seconds(10));
    //stopflag = 1;
    outp.join();
    cout << "Joined output thread\n";
    inp.join();

    cout << "End of main, all threads joined.\n";

     */
/*
    inTask task1(connectionHandler);
    outTask task2(connectionHandler);

    std::thread th1(&inTask::run, &task1);
    std::thread th2(&outTask::run, &task2);
    th1.join();
    th2.join();
    return 0;
}
 */