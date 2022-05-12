package bgu.spl.net.impl.BGRSServer;

import java.util.ArrayList;
import java.util.List;

public class UserObj {
    private final String username;
    private final String password;
    private final int permissions;
    private List<Integer> kdamCourses;
    private List<CourseObj> registeredCourses;

    UserObj(String name, String pass, int perm){
        this.username = name;
        this.password = pass;
        this.permissions = perm;
        this.kdamCourses = new ArrayList<>();
        this.registeredCourses = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPermissions() {
        return permissions;
    }

    public void addKdamCourse(int courseNum) {
        this.kdamCourses.add(courseNum);
    }

    public List<Integer> getKdamCourses() {
        return kdamCourses;
    }

    public void addCourse(CourseObj course){
        registeredCourses.add(course);
    }

    public List<CourseObj> getRegisteredCourses() {
        return registeredCourses;
    }
}
