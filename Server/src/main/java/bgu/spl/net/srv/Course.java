package bgu.spl.net.srv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class Course {
    private int courseNum;
    private String courseName;
    private ArrayList<Integer> KdamCoursesList;
    private int numOfMaxStudents;
    private AtomicInteger numOfCurrStudents;
    private ArrayList<String> listOfStudents;

    public Course(int courseNum, String courseName, ArrayList<Integer> kdamCoursesList, int numOfMaxStudents){
        this.courseNum = courseNum;
        this.courseName = courseName;
        this.KdamCoursesList = kdamCoursesList;
        this.numOfMaxStudents = numOfMaxStudents;
        numOfCurrStudents = new AtomicInteger(0);
        this.listOfStudents = new ArrayList<>();
    }
    public synchronized boolean addStudent(String userName){
        if(!this.isFull()) {
            listOfStudents.add(userName);
            numOfCurrStudents.getAndIncrement();
            Collections.sort(listOfStudents);
            return true;
        }
        else{
            return false;
        }
    }
    public boolean isFull(){
        return numOfCurrStudents.get() == numOfMaxStudents;
    }
    public void removeStudent(String userName){
        listOfStudents.remove(userName);
        numOfCurrStudents.getAndDecrement();
    }
    public ArrayList<Integer> getKdamCoursesList(){
        return this.KdamCoursesList;
    }
    public boolean hasStudent(String username){
        return this.listOfStudents.contains(username);
    }
    public String getCourseName(){
        return this.courseName;
    }

    public int getNumOfCurrStudents() {
        return numOfCurrStudents.get();
    }

    public int getNumOfMaxStudents() {
        return numOfMaxStudents;
    }

    public ArrayList<String> getListOfStudents() {
        return listOfStudents;
    }

}
