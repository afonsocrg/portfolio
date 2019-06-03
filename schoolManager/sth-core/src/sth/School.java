package sth;

import java.io.Serializable;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

import java.util.TreeMap;
//import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import sth.exceptions.BadEntryException;
import sth.exceptions.NoSuchPersonIdException;
import sth.exceptions.RepresentativeLimitExceededException;
import sth.exceptions.InvalidCourseException;
import sth.exceptions.InvalidDisciplineException;
import sth.exceptions.InvalidOperationException;
import sth.exceptions.ImportFileException;
import sth.exceptions.NoSuchCourseException;


/**
 * School implementation.
 */
class School implements Serializable {
	
	/** Serial number for serialization. */
	private static final long serialVersionUID = 201810051538L;

	/** next Person id */
	private int nextID = 100000;

	/** School atendees */
	private TreeMap<Integer, Person> _people = new TreeMap<Integer, Person>();

	/** School Courses */
	private TreeMap<String, Course> _courses = new TreeMap<String, Course>();

	/**
	 * Reads People, Disciplines and Courses from import file and adds them to school
	 * @param filename to read from
	 * @throws BadEntryException
	 * @throws IOException
	 */
	void importFile(String filename) throws IOException, BadEntryException, ImportFileException{
		try{
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			String[] fields;
			
			Person currentPerson;
			int currentId;
			boolean currRepr;
			String courseName;
			String disciplineName;
			Course currentCourse;
			Discipline currentDiscipline;

			line = reader.readLine();
			while(line != null){
				currRepr = false;
				fields = line.split("\\|");
				if(!(validHead(fields))) throw new BadEntryException(fields[0]);

				currentId = Integer.parseInt(fields[1], 10);
				switch(fields[0]) {
					case "DOCENTE":
						currentPerson = new Professor(fields[3], currentId, fields[2]);
						break;

					case "FUNCIONÁRIO":
						currentPerson = new Administrative(fields[3], currentId, fields[2]);
						break;

					case "DELEGADO":
						currRepr = true;
					case "ALUNO":
						currentPerson = new Student(fields[3], currentId, fields[2]);
						break;
					default:
						throw new BadEntryException("Invalid header");
				}

				_people.put(currentId, currentPerson);
				// associate person with courses and disciplines
				while(((line = reader.readLine()) != null) && validBody(fields = line.split("\\|"))) {
					courseName = fields[0].substring(2);
					disciplineName = fields[1];

					currentCourse = addNewCourse(courseName);
					currentPerson.registerCourse(currentCourse);

					currentPerson.registerDiscipline(currentCourse.addNewDiscipline(disciplineName));
					if(currRepr) {
						currentCourse.addRepresentative(currentPerson);
						currRepr = false; //just needed once
					}
				}
			}
			reader.close();
		}
		catch(NumberFormatException e) {
			throw new BadEntryException("Invalid Number");
		} catch (InvalidCourseException e){
			throw new BadEntryException("Invalid Course");
		} catch (InvalidDisciplineException e) {
			throw new BadEntryException("Invalid Discipline");
		} catch (RepresentativeLimitExceededException e){
			throw new BadEntryException("Maximum representative exceeded");
		} catch (InvalidOperationException e) {
			throw new BadEntryException("Invalid Operation");
		}
		nextID = _people.lastKey() + 1;
	}

	/**
	 * Helper function
	 * Validates a Header (first input line that describes a person)
	 * @param fields of the splited input line
	 * @return
	 * 		whether line is a valid Header or not
	 */
	private boolean validHead(String[] fields){
		for(String s: fields)
			if(s == null || s.isEmpty())
				return false;
		return fields[0].equals("ALUNO") || fields[0].equals("FUNCIONÁRIO") || fields[0].equals("DELEGADO") || fields[0].equals("DOCENTE");
	}

	/**
	 * Helper function
	 * Validates a Body (input lines that follow the Header). Body information
	 * will be associated with the person described by header
	 * @param fields of the splited input line
	 * @return
	 * 		wheter line is a valid Body or not
	 */
	private boolean validBody(String[] fields){
		return fields[0].charAt(0) == '#';
	}

	/**
	 * Register course in school
	 * @param courseName to create / get
	 * @return
	 * 	new Course or respective Course if it already exists
	 */
	private Course addNewCourse(String courseName) {
		if(_courses.containsKey(courseName))
			return _courses.get(courseName);
		
    	Course c = new Course(courseName);
		_courses.put(courseName, c);
		return c;
	}

	/**
	 * Get person with given id
	 * @param id searching
	 * @return Person with the given id;
	 * @throws NoSuchPersonIdException if ID doesn't exist
	 */
	Person getPerson(int id) throws NoSuchPersonIdException {
		Person p = _people.get(id);
		if(p == null)
			throw new NoSuchPersonIdException(id);
		return p;
	}

	/**
	 * Present information about id specified person
	 * @param id of person to show
	 * @return person stringified
	 * @throws NoSuchPersonIdException
	 */
	String printPerson(int id) throws NoSuchPersonIdException {
		return getPerson(id).accept(new DefaultPrinter());
	}

	/**
	 * Present information about every known person
	 * @return
	 * 	Concatenartion of every stringified person
	 */
	String printAllPeople() {
		DefaultPrinter dp = new DefaultPrinter();
		String res = "";
		Iterator<Person> it = _people.values().iterator();

		while(it.hasNext())
			res += it.next().accept(dp) + "\n";

		return res;
	}

	/**
	 * Present information about people whose name matches query
	 * @param matching query
	 * @return every person whose name matches query
	 * 	(sorted alphabetically)
	 */
	String printMatchingPeople(String matching) {
		DefaultPrinter dp = new DefaultPrinter();
		String res = "";

		ArrayList<Person> matchingPeople = new ArrayList<Person>();

		_people.forEach((id, person) -> {
			if(person.getName().contains(matching))
				matchingPeople.add(person);
			
		});

		Collections.sort(matchingPeople);

		Iterator<Person> it = matchingPeople.iterator();
		
		while(it.hasNext()) {
			Person p = it.next();
			res += p.accept(dp) + "\n";
		}

		return res;
	}
}
