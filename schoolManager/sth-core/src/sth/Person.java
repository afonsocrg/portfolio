package sth;

import java.awt.dnd.InvalidDnDOperationException;
import java.io.Serializable;
import java.text.Collator;
import java.util.Locale;
import java.text.RuleBasedCollator;
import java.text.ParseException;
import sth.exceptions.*;

abstract class Person implements Serializable, Comparable<Person>, Observer, Printable{

	Locale locale = new Locale("pt");
	
	/** Serial number for serialization. */
  	private static final long serialVersionUID = 201810051538L;

	/** Person name */
	private String _name;

	/** Person id */
	private final int _id;

	/** Person phone number */
	private String _phoneNumber;

	/**
	 * 
	 * @param name
	 * @param id
	 * @param phoneNumber
	 */
	Person(String name, int id, String phoneNumber){
		_name = name;
		_id = id;
		_phoneNumber = phoneNumber;
	}

	public void update(Notification n) { /*leave empty*/ }

	/**
	 * 
	 * @return whether person is an Administrative
	 */
	boolean isAdministrative() { return false; }
	
	/**
	 * 
	 * @return whether person is a Professor
	 */	
	boolean isProfessor() { return false; }

	/**
	 * 
	 * @return whether person is a Student
	 */	
	boolean isStudent() { return false; }

	/**
	 * 
	 * @return whether person is a Representative
	 */
	boolean isRepresentative() { return false; }


	/**
	 * Register in Course
	 * @param newCourse
	 * @throws InvalidCourseSelectionException
	 */
	void registerCourse(Course newCourse) throws InvalidOperationException {
		throw new InvalidOperationException();
	}

	/**
	 * Register in Discipline
	 * @param newDiscipline to register
	 * @throws InvalidDisciplineException
	 */
	void registerDiscipline(Discipline newDiscipline) throws InvalidDisciplineException {
		throw new InvalidDisciplineException();
	}

	/**
	 * 
	 * @param disciplineName
	 * @return registered Discipline, null if Discipline was not found
	 */
	Discipline getDiscipline(String disciplineName) throws InvalidDisciplineException {
		throw new InvalidDisciplineException();
	}

	Course getCourse() throws InvalidOperationException{
		throw new InvalidOperationException();
	}

	/**
	 * 
	 * @return Person's name
	 */
	String getName() { return _name; }

	/**
	 * 
	 * @return Person's id
	 */
	int getId() { return _id; }


	String getPhoneNumber() { return _phoneNumber; }

	/**
	 * Changes Person phone number
	 * @param newPhoneNumber to assign
	 */
	void setPhoneNumber(String newPhoneNumber) { _phoneNumber = newPhoneNumber; }

	String showNotification(){
		return ""; // by default people don't have notifications
	}

	String showSurveyResults(String disciplineName, String projectName) throws InvalidOperationException{
		throw new InvalidOperationException();
	}

	String showDisciplineSurveys(String disciplineName) throws InvalidOperationException{
		throw new InvalidOperationException();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if(o instanceof Person) {
			Person p = (Person) o;
			// even twins have different ids
			return p.getId() == _id;
		}
		return false;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Person p) {
		final Collator c = Collator.getInstance(locale);
		c.setStrength(Collator.NO_DECOMPOSITION); // ignore accent
		return c.compare(_name, p.getName());
	}
}