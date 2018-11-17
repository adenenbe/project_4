
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Filename: CourseSchedulerUtil.java Project: p4 Authors: Debra Deppeler
 * 
 * Use this class for implementing Course Planner
 * 
 * @param <T> represents type
 */

public class CourseSchedulerUtil<T> {

	// can add private but not public members

	/**
	 * Graph object
	 */
	private GraphImpl<T> graphImpl;
	
	/**
	 * Privatate embedded class visitor. This class allows the creation of objects that can be marked as visited.
	 * This is useful as it will allow CourseSchedulerUtil to visit each course and also keep track of each course
	 * that has been visited. 
	 * 
	 * @author adenenbe
	 *
	 * @param <T> Generic T parameter to store the entity that is actually being visited
	 */
	private class Visitor<T> {
		
		// field variables
		T value; // value being stored in this object
		boolean visited; // indicator as to whether the object has been visited
		
		/**
		 * Constructor for the class. Stores the value based on the input and marks the object as not visited since
		 * when it is first created it cannot yet be visited
		 * @param value is the information that the object is storing
		 */
		Visitor (T value){
			this.value = value;
			visited = false;
		}
		
		/**
		 * Indicates whether the object has been visited
		 * 
		 * @return true if the object has been visited, false if the object has not been visited
		 */
		boolean isVisited(){
			return visited;
		}
		
		/**
		 * Marks the object as visited
		 */
		void setVisited() {
			visited = true;
		}
		
		/**
		 * Gets the value stored in the object
		 * 
		 * @return the value stored in the object
		 */
		T getValue(){
			return value;
		}
	}

	/**
	 * constructor to initialize a graph object
	 */
	public CourseSchedulerUtil() {
		this.graphImpl = new GraphImpl<T>();
	}

	/**
	 * createEntity method is for parsing the input json file
	 * 
	 * @return array of Entity object which stores information about a single course
	 *         including its name and its prerequisites
	 * @throws Exception like FileNotFound, JsonParseException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Entity[] createEntity(String fileName) throws Exception {
		// read in input files
		Object obj = new JSONParser().parse(new FileReader(fileName));

		// create JSONObject for all the info in the JSON file
		JSONObject jo = (JSONObject) obj;

		// create an array of courses from the "courses" key of the JSONObject
		JSONArray courses = (JSONArray) jo.get("courses");

		ArrayList<String> allCourses = new ArrayList<String>(); // track all courses added to entity list
		// either as a course entry or a prerequisite

		// initialize array of entities
		Entity[] courseInformation = new Entity[courses.size()]; // create an oversized array in the case where we will
																	// need
		// to add courses that aren't listed in the names field but listed in the
		// prerequisites field

		// loop over array of courses to pull course name and prerequisites while will
		// be used to populate the entities which will
		// be put in the entity array
		for (int i = 0; i < courses.size(); i++) {
			JSONObject coursesInfo = (JSONObject) courses.get(i);

			// declare course name as string
			String course = (String) coursesInfo.get("name");

			if (!allCourses.contains(course)) {
				allCourses.add(course); // add to grand course catalog if not already present
			}

			// create JSONArray for prerequisites
			JSONArray prereqs = (JSONArray) ((JSONObject) coursesInfo).get("prerequisites");
			String[] prereqList = new String[prereqs.size()];
			for (int j = 0; j < prereqs.size(); j++) {
				prereqList[j] = (String) (prereqs.get(j));

				if (!allCourses.contains(prereqList[j])) {
					allCourses.add(prereqList[j]); // add to grand course catalog if not already present
				}
			}

			// store information from JSONObject into Entity array
			courseInformation[i] = new Entity<String>();
			courseInformation[i].setName(course);
			courseInformation[i].setPrerequisites(prereqList);
		}
		
		// some courses may have been listed as prerequisites to other courses in the JSON file but were
		// not actually listed as a course in the JSON file. These are valid courses so they must be added
		// to the list of entities. Utilizes the grand course catalog allCourses as the source of all possibly
		// available courses
		for (String checkCourse : allCourses) {
			for (Entity currentCourse : courseInformation) {
				if (((String) currentCourse.getName()).equals(checkCourse)) {
					break; // don't do anything if the course already exists int the entity list
				}
				if (currentCourse != courseInformation[courseInformation.length - 1]) {
					continue; // if we have not found a matching course in the entity listbut have not yet
					// checked all entities, continue looping over entities
				}
				
				// otherwise, course is not present in the entity list and we must add it
				Entity[] copy = courseInformation; 
				courseInformation = new Entity[copy.length + 1]; // need to create a new entity array with one extra piece
				
				// add all entities back into the new entity array
				for (int i = 0; i < copy.length; i++) {
					courseInformation[i] = copy[i]; 
				}
				
				// add missing entity to the new entity array
				int index = courseInformation.length - 1;
				courseInformation[index] = new Entity();
				courseInformation[index].setName(checkCourse);
				courseInformation[index].setPrerequisites(new String[0]);
			}
		}

		return courseInformation;

	}

	/**
	 * Construct a directed graph from the created entity object
	 * 
	 * @param entities which has information about a single course including its
	 *                 name and its prerequisites
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void constructGraph(Entity[] entities) {
		// first pass over entities adds the each course to the graph as a vertex
		for (int i = 0; i < entities.length; i++) {
			graphImpl.addVertex((T) (entities[i].getName()));
		}
		
		// second pass adds all links between courses and their prerequisites. We must add links during a separate pass from adding
		// vertices because adding links to the graph require that all vertices be present in the graph
		// so we must do this step
		for (int i = 0; i < entities.length; i++) {
			T[] prereqs = (T[]) entities[i].getPrerequisites();
			T vertex1 = (T) entities[i].getName();
			for (T vertex2 : prereqs) {
				graphImpl.addEdge(vertex1, vertex2);
			}
		}

	}

	/**
	 * Returns all the unique available courses
	 * 
	 * @return the sorted list of all available courses
	 */
	public Set<T> getAllCourses() {
		Set<T> sortedCourses = new TreeSet<T>(); //set to store sorted order of classes. Uses TreeSet data structure to accomplish sorting
		Set<T> unsortedCourses = graphImpl.getAllVertices(); // unsorted set of courses obtained using built in member function of GraphImpl. this
		// allows us to access all courses in the graph to add to our TreeSet structure without having to directly manipulate the graph
		
		// add all the items from the unsorted set to the sorted set
		for (T course : unsortedCourses) {
			sortedCourses.add(course);
		}
		return sortedCourses;
	}

	/**
	 * To check whether all given courses can be completed or not
	 * 
	 * @return boolean true if all given courses can be completed, otherwise false
	 * @throws Exception
	 */
	public boolean canCoursesBeCompleted() throws Exception {
		try {
			getSubjectOrder(); // call function which gets a list of a possible order that all courses can
			//be taken. If a list is returned all courses can be taken, if an exception is thrown, they can't
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	/**
	 * The order of courses in which the courses has to be taken
	 * 
	 * @return the list of courses in the order it has to be taken
	 * @throws Exception when courses can't be completed in any order
	 */
	public List<T> getSubjectOrder() throws Exception {
		Integer trackingSize = graphImpl.size(); // keeps track of the amount of courses left unvisited
		List <T> orderedList = new ArrayList<T>(); // Stores list of courses in potential order of completion ending with courseName
		Stack<Visitor<T>> inProgress = new Stack<Visitor<T>>(); // keeps track of current courses on stack of courses being processed.
		// used to identify loops
		
		// quit if no courses are in the graph
		if (trackingSize == 0) {
			return orderedList;
		}
		
		// store all courses in a wrapper class that allows them to be marked as visited
		Map<T, Visitor<T>> visitorMap = new HashMap<T, Visitor<T>>();
		for (T course : getAllCourses()) {
			visitorMap.put(course, new Visitor<T> (course));
		}
		
		// Loop over all visitors to check for the shortest to construct the shortest path following the graphs linkages
		// Looping over all visitors will ensure that all courses are included in the list but we likely won't actually loop
		// through every single one
		for (Visitor<T> currentVisitor : visitorMap.values()) {
			
			// check if visitor has already been visited before constructing it's path. Don't need to reconstruct a 
			// path that has already been constructed
			if(!currentVisitor.isVisited()) {
			visitNodes(currentVisitor, visitorMap, inProgress, orderedList, trackingSize);
			}
			
			// quit out once all visitors are included in the path
			if(trackingSize == 0) {
				break;
			}
		}
		
		return orderedList;

	}

	/**
	 * The minimum course required to be taken for a given course
	 * 
	 * @param courseName
	 * @return the number of minimum courses needed for a given course
	 */
	public int getMinimalCourseCompletion(T courseName) throws Exception {
		Integer trackingSize = graphImpl.size(); // keeps track of the amount of courses left unvisited
		List <T> orderedList = new ArrayList<T>(); // Stores list of courses in potential order of completion ending with courseName
		Visitor<T> currentVisitor; // stores the current course to find a path from 
		Stack<Visitor<T>> inProgress = new Stack<Visitor<T>>(); // keeps track of current courses on stack of courses being processed.
		// used to identify loops
		
		// quit if no courses are in the graph
		if (trackingSize == 0) {
			return 0;
		}
		
		// store all courses in a wrapper class that allows them to be marked as visited
		Map<T, Visitor<T>> visitorMap = new HashMap<T, Visitor<T>>();
		for (T course : getAllCourses()) {
			visitorMap.put(course, new Visitor<T> (course));
		}
		
		currentVisitor = visitorMap.get(courseName);
		
		// visit all courses from currentVisitor. Grab size of the returned list. If exception is thrown, courses can't be completed, return -1
		try {
		visitNodes(currentVisitor, visitorMap, inProgress, orderedList, trackingSize);
		return orderedList.size() - 1;
		} catch (Exception e) {
				return -1;
			}

	}
	
	/**
	 * Helper function possible course ordering methods. This method actually performs the walking of the graph.
	 * This function uses the topological method of depth first search to recursively find all of a given nodes
	 * linkages in the graph and identify cycles if any are present
	 * 
	 * @param currentVisitor is the visitor node that we will try to find a path from
	 * @param visitorMap is a map structure that links the courses names to their corresponding visitor object
	 * @param inProgress is a stack of all nodes that are currently being evaluated. Since this function uses recursion
	 * multiple nodes could be processed at once so we use a stack to track them all
	 * @param orderedList is the list that will store the ordered sequence of nodes based off of their linkages in the graph
	 * @param size the current size of the graph, gets decremented as each node gets added to orderedList
	 * @throws Exception
	 */
	private void visitNodes(Visitor<T> currentVisitor, Map<T, Visitor<T>> visitorMap, 
		Stack<Visitor<T>> inProgress, List<T> orderedList, Integer size) throws Exception {
		
		// throw an exception if the course we are trying to find a path from doesn't exist
		if (!visitorMap.containsValue(currentVisitor)) {
			throw new Exception("Course doesn't exist!");
		}
		
		currentVisitor.setVisited(); // set current visitor node as visited
		inProgress.push(currentVisitor); // add it to the stack as it's currently being processed
		
		// loop over all adjacent nodes in the stack to the current visitor. If they are not yet visited make a recursive
		// call to them. If they are already visited and are currently being processed (already live at a lower stack level)
		// then throw an exception as we have identified a cycle.
		for (T prereq : graphImpl.getAdjacentVerticesOf(currentVisitor.getValue())){
			Visitor <T> prereqVisitor = visitorMap.get(prereq);
			if (!prereqVisitor.isVisited()) {
				visitNodes(visitorMap.get(prereq), visitorMap, inProgress, orderedList, size);
			} else if (inProgress.contains(prereqVisitor)) {
				throw new Exception("Can't complete all courses!");
			}
		}
		orderedList.add(currentVisitor.getValue()); // add current visitor node to the ordered list
		size--; // decrement size
		inProgress.pop(); // pop current node off the stack as it's no longer being processed		
	}
	


	public static void main(String[] args) {
		try {
			Entity[] entity = createEntity("valid.json");
			CourseSchedulerUtil<String> courses = new CourseSchedulerUtil<String>();
			courses.constructGraph(entity);
			List<String> sortedCourses = courses.getSubjectOrder();
			System.out.println(courses.canCoursesBeCompleted());
			for (String course : sortedCourses) {
				System.out.println(course + ": " + courses.getMinimalCourseCompletion(course));
			}
			
		} catch (Exception e) {
			System.out.print(e);
		}

	}

}

