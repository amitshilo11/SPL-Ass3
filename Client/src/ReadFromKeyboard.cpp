
#include "../include/connectionHandler.h"
#include "../include/ReadFromKeyboard.h"


ReadFromKeyboard::ReadFromKeyboard(std::mutex& mutex, ConnectionHandler& connectionHandler): mutex(mutex), cHandler(connectionHandler){}

void ReadFromKeyboard::run() {
    while (1) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        int len = line.length();
        int i = 0;
        int d = 0;
        std::vector<std::string> strings;
        while (i < len) {
            if (line.at(i) == ' ') {
                strings.push_back(line.substr(d, i - d));
                d = i + 1;
            }
            if (i == len - 1) {
                strings.push_back(line.substr(d));
            }
            i++;
        }
        short operation = defineOp(strings[0]);
        char byteArr[2];
        byteArr[0] = ((operation >> 8) & 0xFF);
        byteArr[1] = (operation & 0xFF);
        std::string line2;
        line2 = byteArr[0];
        line2 = line2 + byteArr[1];
        if((operation==5) || (operation==6) || (operation==7) || (operation==9) || (operation==10)){
            std::string line3;
            char byteArr2[2];
            int e = stoi(strings[1]);
            short o = (short)e;
            byteArr2[0] = ((o >> 8) & 0xFF);
            byteArr2[1] = (o & 0xFF);
            line3 = line3 + byteArr2[0];
            line3 = line3 + byteArr2[1];
            line2 = line2 + line3;
        }
        else{
            for (int j = 1; (unsigned)j < strings.size(); j++) {
                line2.append(strings[j] + " ");
            }
            line2.resize(len - 1); //deleting ' ' from the end
        }
        if(!cHandler.sendLine(line2)){
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        if(line=="LOGOUT"){
            while(!mutex.try_lock()){}
            break;
        }
    }
}

short ReadFromKeyboard::defineOp(std::string command){
    if(command == "ADMINREG"){
        return (short)1;
    }
    if(command == "STUDENTREG"){
        return (short)2;
    }
    if(command == "LOGIN"){
        return (short)3;
    }
    if(command == "LOGOUT"){
        return (short)4;
    }
    if(command == "COURSEREG"){
        return (short)5;
    }
    if(command == "KDAMCHECK"){
        return (short)6;
    }
    if(command == "COURSESTAT"){
        return (short)7;
    }
    if(command == "STUDENTSTAT"){
        return (short)8;
    }
    if(command == "ISREGISTERED"){
        return (short)9;
    }
    if(command == "UNREGISTER"){
        return (short)10;
    }
    if(command == "MYCOURSES"){
        return (short)11;
    }
    return (short)0;
}