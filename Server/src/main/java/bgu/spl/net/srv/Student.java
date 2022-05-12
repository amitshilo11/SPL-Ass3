package bgu.spl.net.srv;

import java.util.ArrayList;

public class Student {
    private String userName;
    private String passWord;
    private ArrayList<Integer> myCourses;

    public Student(String userName, String passWord){
        this.userName = userName;
        this.passWord = passWord;
        myCourses = new ArrayList<>();
    }
    public void addCourse(Integer numOfCourse){
        this.myCourses.add(numOfCourse);
    }
    public String getPassWord(){
        return this.passWord;
    }
    public ArrayList<Integer> getMyCourses(){
        return this.myCourses;
    }
    public void removeCourse(Integer courseNum){
        this.myCourses.remove(courseNum);
    }
}
