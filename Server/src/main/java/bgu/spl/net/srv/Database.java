package bgu.spl.net.srv;


import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive object representing the Database where all courses and users are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add private fields and methods to this class as you see fit.
 */
public class Database {
	private ConcurrentHashMap<Integer, Course> courseInfo;
	private ConcurrentHashMap<String, Student> studentInfo;
	private ConcurrentHashMap<String, Object> loggedIn;
	private ConcurrentHashMap<String, Admin> adminInfo;
	private ArrayList<Integer> listOfCourses;
	private Object lock;

	private static class DB_Holder {
		private static Database DB_instance = new Database();
	}

	//to prevent user from creating new Database
	private Database() {
		courseInfo = new ConcurrentHashMap<>();
		studentInfo = new ConcurrentHashMap<>();
		adminInfo = new ConcurrentHashMap<>();
		loggedIn = new ConcurrentHashMap<>();
		listOfCourses = new ArrayList<>();
		initialize("Courses.txt");
		lock = new Object();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Database getInstance() {
		return DB_Holder.DB_instance;
	}

	/**
	 * loades the courses from the file path specified
	 * into the Database, returns true if successful.
	 */
	boolean initialize(String coursesFilePath) {
		BufferedReader inFile = null;
		try {
			FileReader fr = new FileReader(coursesFilePath);
			inFile = new BufferedReader(fr);
			String line = inFile.readLine();
			while (line != null) {
				String[] StringArr = line.split("\\|");
				ArrayList<Integer> kdamList = new ArrayList<>();
				if (StringArr[2].length() > 2) {
					String[] kdamList2 = StringArr[2].substring(1, StringArr[2].length() - 1).split(",");
					for (String s : kdamList2)
						kdamList.add(Integer.parseInt(s));
				}
				Course toAdd = new Course(Integer.parseInt(StringArr[0]), StringArr[1], kdamList, Integer.parseInt(StringArr[3]));
				courseInfo.putIfAbsent(Integer.parseInt(StringArr[0]), toAdd);
				listOfCourses.add(Integer.parseInt(StringArr[0]));
				line = inFile.readLine();
			}
			return true;
		} catch (FileNotFoundException exception) {
			System.out.println("The file was not found");
		} catch (IOException exception) {
			exception.printStackTrace();
		} finally {
			try {
				assert inFile != null;
				inFile.close();
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
		return false;
	}

	public ConcurrentHashMap<String, Admin> getAdminInfo() {
		return adminInfo;
	}

	public ArrayList<Integer> getListOfCourses() {
		return listOfCourses;
	}

	public ConcurrentHashMap<Integer, Course> getCourseInfo() {
		return courseInfo;
	}

	public ConcurrentHashMap<String, Student> getStudentInfo() {
		return studentInfo;
	}

	public ConcurrentHashMap<String, Object> getLoggedIn() {
		return loggedIn;
	}

	public boolean kdamCourseCheck(ArrayList<Integer> reqCheck, ArrayList<Integer> myKdam) {
		return myKdam.containsAll(reqCheck);
	}

	public Message courseReg(String connections, int courseNum) {
		if ((connections == null) ||
				getAdminInfo().containsKey(connections) ||
				!getLoggedIn().containsKey(connections) ||
				!getCourseInfo().containsKey(courseNum) ||
				getCourseInfo().get(courseNum).hasStudent(connections) ||
				getCourseInfo().get(courseNum).isFull() ||
				!kdamCourseCheck(getCourseInfo().get(courseNum).getKdamCoursesList(), getStudentInfo().get(connections).getMyCourses())) {
			return new Error((short) 5);
		} else {
			synchronized (getCourseInfo().get(courseNum)) {
				if(getCourseInfo().get(courseNum).addStudent(connections)) {
					getStudentInfo().get(connections).addCourse(courseNum);
					return new Acknowledgement((short) 5);
				}
			}
			return new Error((short) 5);
		}
	}

	public Message adminReg(String username, String password) {
		synchronized (lock) {
			if (!getAdminInfo().containsKey(username) && !getStudentInfo().containsKey(username)) {
				getAdminInfo().put(username, new Admin(username, password));
				return new Acknowledgement((short) 1);
			} else
				return new Error((short) 1);
		}
	}
	public Message studentReg(String username, String password) {
		synchronized (lock) {
			if (!getStudentInfo().containsKey(username) && !getAdminInfo().containsKey(username)) {
				getStudentInfo().put(username, new Student(username, password));
				return new Acknowledgement((short) 2);
			} else
				return new Error((short) 2);
		}
	}
}
