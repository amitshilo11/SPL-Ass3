package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.DefinitionsAndVariablesSet;
import bgu.spl.net.api.MessagingProtocol;

public class CourseProtocol implements MessagingProtocol<Message>, DefinitionsAndVariablesSet {
    private boolean shouldTerminate = false;
    private UserObj currUser;
    private Database database= Database.getInstance();

    @Override
    public Message process(Message msg) {

        switch (msg.getOpcode()){
            case ADMINREG:
                if (database.setNewStudentUser(msg.getFirstString(), msg.getSecondString()))
                    return new Message(ACK, ADMINREG, "Registration Succeeded");
                return new Message(ERR, ADMINREG);

            case STUDENTREG:
                if (database.setNewStudentUser(msg.getFirstString(), msg.getSecondString()))
                    return new Message(ACK, STUDENTREG, "Registration Succeeded");
                return new Message(ERR, STUDENTREG);

            case LOGIN:
                currUser = database.login(msg.getFirstString(), msg.getSecondString());
                if(currUser == null)
                    return new Message(ERR, LOGIN);
                return new Message(ACK, LOGIN, "Login Succeeded");

            case LOGOUT:
                if (database.logout(currUser))
                    return new Message(ACK,LOGOUT, "Logout Succeeded");
                return new Message(ERR, LOGOUT);

            case COURSEREG:
                if (database.courseReg(msg.getSecondOpcode(), this.currUser))
                    return new Message(ACK,COURSEREG, "Registration to course ("
                            + msg.getSecondOpcode() + ") succeeded.");
                return new Message(ERR, COURSEREG);

            case KDAMCHECK:
                String kdamCoursesNeeded = database.kdamCheck(msg.getSecondOpcode(), this.currUser).toString();
                if(kdamCoursesNeeded == null)
                    return new Message(ACK,KDAMCHECK, "  ");;
                return new Message(ACK,KDAMCHECK, kdamCoursesNeeded);

            case COURSESTAT:
                String courseStats = database.courseStatus(msg.getSecondOpcode(), this.currUser);
                if(courseStats == null)
                    return new Message(ERR, COURSEREG);
                return new Message(ACK, COURSESTAT,courseStats);

            case STUDENTSTAT:
                String studentStatus = database.studentStatus(msg.getFirstString(), this.currUser);
                if(studentStatus == null)
                    return new Message(ERR, STUDENTSTAT);
                return new Message(ACK,STUDENTSTAT,studentStatus);

            case ISREGISTERED:
                String registrationCheck = database.checkRegistration(msg.getSecondOpcode(), this.currUser);
                if(registrationCheck == null)
                    return new Message(ERR, STUDENTSTAT);
                return new Message(MESSAGE,ISREGISTERED,registrationCheck);

            case UNREGISTER:
                if(database.courseUnregister(msg.getSecondOpcode(), this.currUser))
                    return new Message(MESSAGE,"Unregistered successfully");
                return new Message(ERR, UNREGISTER);

            case MYCOURSES:
                String studentCourses = database.getStudentCourses(this.currUser);
                if(studentCourses == null)
                    return new Message(ERR, MYCOURSES);
                return new Message(ACK,MYCOURSES,studentCourses);

        }

        return new Message(ERR, "Unable to define message");
    }


    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

}
