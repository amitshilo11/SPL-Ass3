package bgu.spl.net.api;

public interface DefinitionsAndVariablesSet {


    enum MessageType{
        // Opcode 1 - Admin register
        // Opcode 2 - Student register
        // Opcode 3 - Login request
        UsernameAndPass(2),

        // Opcode 5 - Register to course
        // Opcode 6 - Check Kdam course
        // Opcode 7 - (Admin)Print course status
        // Opcode 9 - check if registered
        // Opcode 10 - Unregister to course
        CourseNum(1),

        // Opcode 4 - Logout request
        // Opcode 11 - Check my current courses
        NoAdditionalFields(0),

        // Opcode 8 - (Admin)Print student status
        UsernameString(1),

        // Opcode 12 - Acknowledgement
        // Opcode 13 - Error
        Notice(2);

        public final int numOfContent;

        private MessageType(int numOfContent) {
            this.numOfContent = numOfContent;
        }
    }

    enum PermissionLevel {
        UNKNOWN,
        STUDENT,
        ADMIN
    }

    final short
            UNDEFINE = 0,       //null value
            ADMINREG = 1,       //Admin register
            STUDENTREG = 2,     //Student register
            LOGIN = 3,          //Login request
            LOGOUT = 4,         //Logout request
            COURSEREG = 5,      //Register to course
            KDAMCHECK = 6,      //Check Kdam course
            COURSESTAT = 7,     //(Admin)Print course status
            STUDENTSTAT = 8,    //(Admin)Print student status
            ISREGISTERED = 9,   //check if registered
            UNREGISTER = 10,    //Unregister to course
            MYCOURSES = 11,     //Check my current courses
            ACK = 12,           //Acknowledgement Message
            ERR = 13,           //Error Message
            MESSAGE = -1;       //Message


    // Defines Level of permissions access
    final int
            UNSIGNED = 0,
            STUDENT = 1,
            ADMIN = 2;

    final int OPCODE_LENGTH = 2;


}
