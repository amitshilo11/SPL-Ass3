package bgu.spl.net.impl.BGRSServer;

import java.util.ArrayList;
import java.util.List;

public class CourseObj {
    private int courseNum;
    private String courseName;
    private int numOfMaxStudents;
    private int numOfRegisteredStudents;
    private List<Integer> kdamCoursesList;
    private List<UserObj> registeredStudents;


    CourseObj(int courseNum, String courseName, List<Integer> kdamList, int numOfMaxStudents){
        this.registeredStudents = new ArrayList<>();
        this.courseNum = courseNum;
        this.courseName = courseName;
        this.numOfMaxStudents = numOfMaxStudents;
        this.numOfRegisteredStudents = 0;
        this.kdamCoursesList = kdamList;
        this.registeredStudents = new ArrayList<>();
    }

    public int getCourseNum() {
        return courseNum;
    }

    public String getCourseName() {
        return courseName;
    }

    public List<Integer> getKdamCoursesList() {
        return kdamCoursesList;
    }

    public int getNumOfMaxStudents() {
        return numOfMaxStudents;
    }

    public int getNumOfRegisteredStudents() {
        return numOfRegisteredStudents;
    }

    public void addStudent(UserObj student){
        registeredStudents.add(student);
        numOfRegisteredStudents++;
    }

    public int getNumOfSeatsAvailable(){
        return numOfMaxStudents - numOfRegisteredStudents;
    }

    public boolean unregisterStudent(UserObj student){
        if(!(registeredStudents.contains(student)))
            return false;
        registeredStudents.remove(student);
        return true;

    }

    public List<UserObj> getRegisteredStudents(){
        return registeredStudents;
    }
}
