package bgu.spl.net.srv;

import bgu.spl.net.api.MessagingProtocol;

public class MessagingProtocolImpl implements MessagingProtocol<Message> {
    private Database dB_instance = Database.getInstance();
    private String connections = "";
    boolean _shouldTerminate = false;
    private int state = 0; //0 - not logged in yet, 1 - i am a student, 2 - i am an admin.

    public Message process(Message msg) {
        Message m = null;
        short operation = msg.getOp();
        switch (operation) {
            case 1:
                m = adminReg(msg.getUserName(), msg.getPassWord());
                break;
            case 2:
                m = studentReg(msg.getUserName(), msg.getPassWord());
                break;
            case 3:
                m = login(msg.getUserName(), msg.getPassWord());
                break;
            case 4:
                m = logout();
                break;
            case 5:
                m = courseReg(msg.getCourseNum());
                break;
            case 6:
                m = kdamCheck(msg.getCourseNum());
                break;
            case 7:
                m = courseStat(msg.getCourseNum());
                break;
            case 8:
                m = studentStat(msg.getUserName());
                break;
            case 9:
                m = isRegistered(msg.getCourseNum());
                break;
            case 10:
                m = unRegister(msg.getCourseNum());
                break;
            case 11:
                m = myCourses();
                break;
        }
        return m;
    }

    public boolean shouldTerminate() {
        return _shouldTerminate;
    }

    public Message adminReg(String username, String password) {
        if(connections.equals(""))
            return dB_instance.adminReg(username, password);
        return new Error((short) 1);
    }
    public Message studentReg(String username, String password) {
        if(connections.equals(""))
            return dB_instance.studentReg(username, password);
        return new Error((short) 2);
    }

    public Message login(String username, String password) {
        if(!(connections.equals("")))
            return new Error((short) 3);
        if (dB_instance.getLoggedIn().containsKey(username)) {
            return new Error((short) 3);
        }
        if (dB_instance.getStudentInfo().containsKey(username)) { //I'm a student
            if (dB_instance.getStudentInfo().get(username).getPassWord().equals(password)) {
                dB_instance.getLoggedIn().put(username, dB_instance.getStudentInfo().get(username));
                connections = username;
                state = 1;
                return new Acknowledgement((short) 3);
            } else
                return new Error((short) 3);
        } else if (dB_instance.getAdminInfo().containsKey(username)) { //I'm an admin
            if (dB_instance.getAdminInfo().get(username).getPassword().equals(password)) {
                dB_instance.getLoggedIn().put(username, dB_instance.getAdminInfo().get(username));
                connections = username;
                state = 2;
                return new Acknowledgement((short) 3);
            } else
                return new Error((short) 3);
        }
        return new Error((short) 3);
    }

    public Message logout() {
        if (!(connections.equals(""))) {
            if (!dB_instance.getLoggedIn().containsKey(connections)) {
                return new Error((short) 4);
            } else {
                dB_instance.getLoggedIn().remove(connections);
                _shouldTerminate = true;
                connections = "";
                state = 0;
                return new Acknowledgement((short) 4);
            }
        }
        return new Error((short) 4);
    }

    private Message courseReg(Integer courseNum) {
        if(!connections.equals(""))
            return dB_instance.courseReg(connections, courseNum);
        return new Error((short) 5);
    }

    private Message kdamCheck(Integer numCourse) {
        Acknowledgement send = new Acknowledgement((short) 6);
        String s = "";
        for (Integer i: dB_instance.getListOfCourses()) {
            for (Integer j: dB_instance.getCourseInfo().get(numCourse).getKdamCoursesList()) {
                if(j.equals(i))
                    s = s + i.toString() + ",";
            }
        }
        if(!s.equals(""))
            send.getList().add("[" + s.substring(0,s.length()-1) + "]");
        else{
            send.getList().add("[]");
        }
        return send;
    }

    public Message courseStat(Integer courseNum) {
        if (!(connections.equals(""))) {
            if (state==2) {
                Acknowledgement send = new Acknowledgement((short) 7);
                int max = dB_instance.getCourseInfo().get(courseNum).getNumOfMaxStudents();
                int curr = dB_instance.getCourseInfo().get(courseNum).getNumOfCurrStudents();
                send.getList().add("Course: (" + (courseNum.toString()) + ") " + dB_instance.getCourseInfo().get(courseNum).getCourseName());
                send.getList().add("Seats Available: " + (max-curr) + "/" + max);
                String s = "";
                for (String name: dB_instance.getCourseInfo().get(courseNum).getListOfStudents()) {
                    s = s + name + ",";
                }
                if(!s.equals(""))
                    send.getList().add("Students Registered: " + "[" + s.substring(0,s.length()-1) + "]");
                else
                    send.getList().add("Students Registered: " + "[]");
                return send;
            }
        }
        return new Error((short) 7);
    }

    public Message studentStat(String username) {
        if (!(connections.equals(""))) {
            if ((state==2) && dB_instance.getStudentInfo().containsKey(username)) {
                Acknowledgement send = new Acknowledgement((short) 8);
                send.getList().add("Student: " + username);
                String s = "";
                for (Integer i : dB_instance.getListOfCourses()) {
                    for (Integer j : dB_instance.getStudentInfo().get(username).getMyCourses()) {
                        if (i.equals(j))
                            s = s + i.toString() + ",";
                    }
                }
                if(!s.equals(""))
                    send.getList().add("Courses: " + "[" + s.substring(0,s.length()-1) + "]");
                else{
                    send.getList().add("Courses: " + "[]");
                }
                return send;
            }
        }
        return new Error((short) 8);
    }
    public Message isRegistered(Integer courseNum){
        if(!(connections.equals(""))){
            if(state==1){
                Acknowledgement send = new Acknowledgement((short) 9);
                if(dB_instance.getCourseInfo().get(courseNum).hasStudent(connections)){
                    send.getList().add("REGISTERED");
                }
                else{
                    send.getList().add("NOT REGISTERED");
                }
                return send;
            }
            return new Error((short) 9);
        }
        return new Error((short) 9);
    }

    public Message unRegister(Integer courseNum){
        if(!(connections.equals(""))) {
            if (state==1) {
                if(dB_instance.getCourseInfo().get(courseNum).hasStudent(connections)) {
                    synchronized (dB_instance.getCourseInfo().get(courseNum)) {
                        dB_instance.getCourseInfo().get(courseNum).removeStudent(connections);
                        dB_instance.getStudentInfo().get(connections).removeCourse(courseNum);
                    }
                    return new Acknowledgement((short) 10);
                }
                return new Error((short) 10);
            }
        }
        return new Error((short) 10);
    }

    public Message myCourses(){
        if(!(connections.equals(""))) {
            if (state==1){
                Acknowledgement send = new Acknowledgement((short) 11);
                String s = "";
                for (Integer i : dB_instance.getListOfCourses()) {
                    for (Integer j : dB_instance.getStudentInfo().get(connections).getMyCourses()) {
                        if (i.equals(j))
                            s = s + i.toString() + ",";
                    }
                }
                if(!s.equals(""))
                    send.getList().add("[" + s.substring(0,s.length()-1) + "]");
                else{
                    send.getList().add("[]");
                }
                return send;
            }
            return new Error((short) 11);
        }
        return new Error((short) 11);
    }
}