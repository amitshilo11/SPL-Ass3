package bgu.spl.net.impl.BGRSServer;
import bgu.spl.net.api.DefinitionsAndVariablesSet;

import java.util.List;
import java.util.concurrent.*;
import java.util.*;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

/**
 * Passive object representing the Database where all courses and users are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add private fields and methods to this class as you see fit.
 */
public class Database implements DefinitionsAndVariablesSet {

	private ConcurrentHashMap<String, UserObj> users;
	private ConcurrentHashMap<Integer, CourseObj> courses;
	private List<UserObj> loginNow;


	//to prevent user from creating new Database
	private Database() {
		users = new ConcurrentHashMap<>();
		courses = new ConcurrentHashMap<>();
		loginNow = new ArrayList<>();
		//initialize("C:\\Users\\alssa\\OneDrive\\Desktop\\SPL\\Ass3\\Server\\spl-net\\src\\main\\java\\bgu\\spl\\net\\impl\\courseSystem\\Courses.txt");
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	private static Database MBI_instance = null;
	public static Database getInstance() {
		if (MBI_instance == null)
			MBI_instance = new Database();
		return MBI_instance;
	}

	public static short bytesToShort(byte[] byteArr)
	{
		short result = (short)((byteArr[0] & 0xff) << 8);
		result += (short)(byteArr[1] & 0xff);
		return result;
	}

	public  byte[] shortToBytes(short num)
	{
		byte[] bytesArr = new byte[2];
		bytesArr[0] = (byte)((num >> 8) & 0xFF);
		bytesArr[1] = (byte)(num & 0xFF);
		return bytesArr;
	}

	
	/**
	 * loads the courses from the file path specified
	 * into the Database, returns true if successful.
	 */
	boolean initialize(String coursesFilePath) {
		try {
			File coursesFile = new File(coursesFilePath);
			Scanner myReader = new Scanner(coursesFile);

			while (myReader.hasNextLine()) {
				String courseLine = myReader.nextLine();
				String[] splitLine = courseLine.split("\\|");

				int num = Integer.parseInt(splitLine[0]);
				String courseName = splitLine[1];

				String[] kdamCouses = splitLine[2].replaceAll("\\[", "").replaceAll("\\]", "").split(",");	//clean from '[' and ']' and a array with etch course num
				List<Integer> kdamList = new ArrayList<>();
				if (kdamCouses.length == 1)
					kdamList = null;
				else {
					for (String kdam : kdamCouses)
						kdamList.add(Integer.parseInt(kdam));
				}

				int maxStudents = Integer.parseInt(splitLine[3]);

				CourseObj newCourse = new CourseObj(num, courseName, kdamList, maxStudents);
				courses.putIfAbsent(newCourse.getCourseNum(), newCourse);
			}
			myReader.close();
			return true;

		} catch (FileNotFoundException e) {
			System.out.println("ERROR: cannot load courses file");
			e.printStackTrace();
			return false;
		}
	}

	// TODO fix already logged in
	public boolean setNewAdminUser(String username, String pass){
		if(users.containsKey(username))
			return false;

		UserObj currUser = new UserObj(username, pass, ADMIN);
		users.put(username, currUser);
		return true;
	}

	// TODO fix already logged in
	public boolean setNewStudentUser(String username, String pass){
		if(users.containsKey(username))
			return false;

		UserObj currUser = new UserObj(username, pass, STUDENT);
		users.put(username, currUser);
		return true;
	}


	// TODO fix already logged in
	public UserObj login(String username, String password){
		if(!(users.containsKey(username)))
			return null;

		UserObj currUser = users.get(username);
		if (currUser.getPassword().equals(password)) {
			loginNow.add(currUser);
			return currUser;
		}
		return null; //password is incorrect
	}

	public boolean logout(UserObj user){
		if(!(loginNow.contains(user)))
			return false; //user isnt logged in

		loginNow.remove(user);
		return true;
	}

	public boolean courseReg(int courseNum, UserObj student){
		if (student.getPermissions() != STUDENT)
			return false;	// do not have permission
		if (!(courses.containsKey(courseNum)))
			return false;	//course doesnt exist

		CourseObj currCourse = courses.get(courseNum);
		List kdamList = currCourse.getKdamCoursesList();
		if(!(kdamList.containsAll(student.getKdamCourses())))
			return false;	//user do not have all KdamCourses

		//TODO sincorsion
		if (currCourse.getNumOfRegisteredStudents() < currCourse.getNumOfMaxStudents()) {
			currCourse.addStudent(student);
			student.addKdamCourse(currCourse.getCourseNum());
			return true;
		}
		return false;	//no place left
	}

	//TODO return list of the KDAM courses, in the SAME ORDER as in the courses file
	public List<Integer> kdamCheck(int courseNum, UserObj student){
		if(!(courses.containsKey(courseNum)))
			return null;	//course doesnt exist

		List<Integer> courseKdamList = courses.get(courseNum).getKdamCoursesList();
		List<Integer> kdamCoursesNeeded = new ArrayList<>();
		for (Integer i : courseKdamList) {
			if(!(student.getKdamCourses().contains(i)))
				kdamCoursesNeeded.add(i);
		}

		return kdamCoursesNeeded;
	}


	public String courseStatus(int courseNum, UserObj admin){
		if(admin.getPermissions() != ADMIN)
			return null;	// do not have permission
		if(!(courses.containsKey(courseNum)))
			return null;	//course doesnt exist

		CourseObj currCourse = courses.get(courseNum);
		String temp = "Course: (" + currCourse.getCourseNum() + ") " + currCourse.getCourseName() + "\n"
				+ "Seats Available: " + currCourse.getNumOfSeatsAvailable() + "/" + currCourse.getNumOfMaxStudents() + "\n"
				+ "Students Registered: " + currCourse.getRegisteredStudents().toString(); //TODO ordered alphabetically
		return temp;
	}

	public String studentStatus(String studentName, UserObj admin){
		if(admin.getPermissions() != ADMIN)
			return null;	// do not have permission
		if(!(users.containsKey(studentName)))
			return null;	//student doesnt exist

		UserObj currStudent = users.get(studentName);
		if (currStudent.getPermissions() != STUDENT)
			return null; // user to chack is not a student
		String temp = "Student: " + currStudent.getUsername() + "\n"
				+"Courses: " + currStudent.getRegisteredCourses().toString(); //TODO return numbers instead names
		return temp;
	}

	public String checkRegistration(int courseNum, UserObj student){
		if (student.getPermissions() != STUDENT)
			return null; 	// user is not a student
		if(!(courses.containsKey(courseNum)))
			return null;	//course doesnt exist

		if(courses.get(courseNum).getRegisteredStudents().contains(student))
			return "REGISTERED";
		else
			return "NOT REGISTERED";
	}

	public boolean courseUnregister(int courseNum, UserObj student){
		if (student.getPermissions() != STUDENT)
			return false; 	// user is not a student
		if(!(courses.containsKey(courseNum)))
			return false;	//course doesnt exist

		if(courses.get(courseNum).unregisterStudent(student))
			return true;
		else
			return false;	//student dont registered to this course
	}

	public String getStudentCourses(UserObj student){
		if (student.getPermissions() != STUDENT)
			return null; 	// user is not a student
		if(student.getRegisteredCourses() == null)
			return "[]";
		return student.getRegisteredCourses().toString();
	}

}
