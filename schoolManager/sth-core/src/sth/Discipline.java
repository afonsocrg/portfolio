package sth;

import java.io.Serializable;
import java.text.Collator;
import java.util.TreeMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import sth.exceptions.*;

class Discipline implements Serializable, Comparable<Discipline>, Notificator{

	/** Serial number for serialization. */
	private static final long serialVersionUID = 201810051538L;


	/** Discipline capacity */
	private int _capacity = 100;

	/** Name of Discipline */
	private final String _name;

	/** Students registered in Discipline */
	private TreeMap<Integer, Student> _students;

	/** Teachers of Discipline */
	private TreeMap<Integer, Professor> _professors;

	/** Projects of Discipline */
	private TreeMap<String, Project> _projects;

	/** People to be notified  */
	private HashSet<Person> _messagedPeople;


	/**
	 * 
	 * @param name of Discipline
	 */
	Discipline(String name){
		_name = name;
		_students = new TreeMap<Integer, Student>();
		_professors = new TreeMap<Integer, Professor>();
		_projects = new TreeMap<String, Project>();
		_messagedPeople = new HashSet<Person>();
	}

	/**
	 * 
	 * @param name of discipline
	 * @param capacity of discipline
	 */
	Discipline(String name, int capacity){
		this(name);
		_capacity = capacity;
	}

	public void addListener(Person p){
		_messagedPeople.add(p);
	}

	public boolean containsListener(Person p){
		return _messagedPeople.contains(p);
	}

	public void removeListener(Person p){
		_messagedPeople.remove(p);
	}

	public void notifyListeners(String message){
		Notification n = new Notification(message);
		_messagedPeople.forEach(p -> p.update(n));
	}

	/**
	 * Register Discipline teacher
	 * @param p
	 */
	void registerProfessor(Professor p) { 
		_professors.put(p.getId(), p);
		addListener(p);
	}

	/**
	 * Register Discipline Student
	 * @param s
	 */
	void registerStudent(Student s) {
		if(_students.size() < _capacity){
			_students.put(s.getId(), s);
			addListener(s);
		}
	}
	

	/**
	 * @see sth.Student#toString()
	 * @return all students stringified
	 */
	String showStudents(){
		String res = "";
		DefaultPrinter dp = new DefaultPrinter();
		Iterator<Student> it = _students.values().iterator();
		while(it.hasNext()){
			Student s = it.next();
			res += s.accept(dp) + "\n";
		}
		return res;
	}

	/**
	 * 
	 * @return Discipline name
	 */
	String getName(){
		return _name;
	}

	/**
	 * Return if discipline is lectured by the person with given id
	 * @param id of school person
	 * @return whether that person is a Discipline Teacher
	 */
	boolean hasProfessor(int id){
		return _professors.containsKey(id);
	}


	/**
	 *
	 * @param newProject
	 */
	void createProject(Project newProject) throws InvalidProjectException {
		if(_projects.containsKey(newProject.getName()))
			throw new InvalidProjectException();
	
		_projects.put(newProject.getName(), newProject);
	}

	/**
	 * Closes project
	 * @param projectName
	 * 	of project to close
	 * @throws InvalidProjectException
	 * 	if no project found
	 */
	void closeProject(String projectName) throws InvalidProjectException{
		Project p = _projects.get(projectName);
		if(p == null)
			throw new InvalidProjectException();
		
		if(!p.isOpen())
			return;
		
		p.close();
		if(p.hasSurvey()){
			String message = "Pode preencher inquérito do projecto " + projectName + " da disciplina " + _name;
			notifyListeners(message);
		}

	}

	void deliverProject(String projectName, Submission newSub) throws InvalidProjectException{
		Project p = _projects.get(projectName);
		if(p == null)
			throw new InvalidProjectException();
		
		p.submit(newSub);
	}

	/**
	 * Show all project submissions
	 * @param projectName
	 * @return
	 * 	all submissions stringified
	 * @throws InvalidProjectException
	 */
	String showProjectSubmissions(String projectName) throws InvalidProjectException {
		String res = _name + " - ";
		Project p = _projects.get(projectName);
		if(p == null)
			throw new InvalidProjectException();
		
		res += p.showSubmissions();

		return res;
	}

	/**
	 * 
	 * @param id of school person
	 * @return whether that project exists in this Discipline
	 */

	boolean hasProject(String projectName){
		return _projects.containsKey(projectName); 
	}

	/**
	 *
	 * @param projectName
	 * @return project associated with that name
	 */
	Project getProject(String projectName) throws InvalidProjectException {
		if(!hasProject(projectName)) throw new InvalidProjectException();
		return _projects.get(projectName);
	}

	void createSurvey(String projectName) throws InvalidProjectException, InvalidSurveyException{
		Project p = _projects.get(projectName);
		if(!hasProject(projectName))
			throw new InvalidProjectException();
		
		p.createSurvey();

	}

	void answerSurvey(String projectName, int id, SurveyAnswer answer) throws InvalidProjectException, InvalidSurveyException, InvalidSurveyAnswerSubmissionException {
		Project p = _projects.get(projectName);
		if(!hasProject(projectName))
			throw new InvalidProjectException();
		
		p.answerSurvey(id, answer);
	}

	void cancelSurvey(String projectName) throws InvalidProjectException, InvalidSurveyException, InvalidCancelException{
		Project p = _projects.get(projectName);
		if(!hasProject(projectName))
			throw new InvalidProjectException();
		
		p.cancelSurvey();
	}

	void openSurvey(String projectName) throws InvalidProjectException, InvalidSurveyException, InvalidOpenException{
		Project p = _projects.get(projectName);
		if(!hasProject(projectName))
			throw new InvalidProjectException();

		p.openSurvey();
	}

	void closeSurvey(String projectName) throws InvalidProjectException, InvalidSurveyException, InvalidCloseException{
		Project p = _projects.get(projectName);
		if(!hasProject(projectName))
			throw new InvalidProjectException();

		p.closeSurvey();
	}

	void finishSurvey(String projectName) throws InvalidProjectException, InvalidSurveyException, InvalidFinishException{
		Project p = _projects.get(projectName);
		if(!hasProject(projectName))
			throw new InvalidProjectException();

		p.finishSurvey();
		String message = "Resultados do inquérito do projecto " + projectName + " da disciplina " + _name;
		notifyListeners(message);
	}


	SurveyResults getSurveyResults(String projectName) throws InvalidSurveyException, CreatedSurveyException, OpenSurveyException, ClosedSurveyException, InvalidProjectException {
		Project p = _projects.get(projectName);
		if(!hasProject(projectName))
			throw new InvalidProjectException();
		return p.getSurveyResults();
	}

	Collection<Project> getProjects() { return _projects.values(); }


	/**
	 * Sets new Discipline capacity
	 * @param newCap to discipline
	 */
	void setCapacity(int newCap) { _capacity = newCap; }

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() { return _name; }

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o){
		if (o instanceof Discipline){
			Discipline d = (Discipline) o;
			return Collator.getInstance(Locale.getDefault()).equals(_name, d.getName());
		}
		return false;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Discipline d){
		return Collator.getInstance(Locale.getDefault()).compare(_name, d.getName()); 
	}
}