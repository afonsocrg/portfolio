package sth;

import java.io.Serializable;
import sth.exceptions.*;

import java.util.TreeMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

class Student extends Person implements Serializable {
	/** Serial number for serialization */
	private static final long serialVersionUID = 201810051538L;

	/** Maximum Disciplines that a Student can attend at a time */
	static final int MAXDISCIPLINES = 6;

	/** Course Student is studying at */
	private Course _registeredCourse;

	/** Attending Disciplines */
	private TreeMap<String, Discipline> _resgiteredDiciplines = new TreeMap<String, Discipline>();

	private LinkedList<Notification> _notifications = new LinkedList<Notification>();

	/**
	 * 
	 * @param name
	 * @param id
	 * @param phoneNumber
	 * @see sth.Person#Person(String, int, String)
	 */
	Student(String name, int id, String phoneNumber) { super(name, id, phoneNumber); }

	@Override
	public void update(Notification n) {
		_notifications.add(n);
	}

	@Override
	String showNotification() {
		String res = "";
		while(_notifications.size() > 0) {
			res += _notifications.remove().show() + "\n";
		}
		return res;
	}

	@Override 
	String showSurveyResults(String disciplineName, String projectName) throws InvalidDisciplineException, InvalidProjectException, InvalidSurveyException{
		String res = disciplineName + " - " + projectName;
		Project p = getDiscipline(disciplineName).getProject(projectName);
		
		if(!p.hasSubmited(getId())) throw new InvalidProjectException();
		
		SurveyResults sR;
		try{
			sR = p.getSurveyResults();			
		} catch (CreatedSurveyException e) {
			return res + " (por abrir)";
		} catch (OpenSurveyException e){
			return res + " (aberto)";
		} catch (ClosedSurveyException e){
			return res + " (fechado)";
		}

		res += "\n" + " * Número de Respostas: " + sR.getNumAnswers();
		res += "\n" + " * Tempo médio (horas): " + sR.getAvg();
		return res;
	}

	@Override
	String showDisciplineSurveys(String disciplineName) throws InvalidDisciplineException, InvalidOperationException {
		if(!isRepresentative()){
			throw new InvalidOperationException();
		}

		Discipline d = _registeredCourse.getDiscipline(disciplineName);
		if(d == null) throw new InvalidDisciplineException();
		String res = "";
		
		Iterator<Project> it = d.getProjects().iterator();
		SurveyResults sR;
		while(it.hasNext()){
			Project p = it.next();
			if(p.hasSurvey()){
				res += disciplineName + " - " + p.getName();
				try{
					sR = p.getSurveyResults();
					res += " - " + sR.getNumAnswers() + " respostas" + " - " + sR.getAvg() + " horas";
				} catch (CreatedSurveyException e) {	
					res += " (por abrir)";
				} catch (OpenSurveyException e){
					res += " (aberto)";
				} catch (ClosedSurveyException e){
					res += " (fechado)";
				}
				if(it.hasNext()) res += "\n"; // prepare next line
			}
		}
		return res;
	}

	/**
	 * @see sth.Person#isStudent()
	 */
	@Override
	boolean isStudent() { return true; }

	/**
	 * @see sth.Person#isRepresentative()
	 */
	@Override
	boolean isRepresentative() { return _registeredCourse.isRepresentative(getId()); }

	/**
	 * @see sth.Person#addNewCourse(sth.Course)
	 */
	@Override
	void registerCourse(Course newCourse) throws InvalidCourseException {
		if(_registeredCourse == null)
			_registeredCourse = newCourse;
		else if(!_registeredCourse.equals(newCourse)){
			throw new InvalidCourseException();
		}
	}

	/**
	 * @see sth.Person#addNewDiscipline(sth.Discipline)
	 */
	@Override
	void registerDiscipline(Discipline newDiscipline) throws InvalidDisciplineException {
		if(_resgiteredDiciplines.size() > MAXDISCIPLINES){
			throw new InvalidDisciplineException();
		}
		_resgiteredDiciplines.put(newDiscipline.getName(), newDiscipline);
		newDiscipline.registerStudent(this);
	}

	/**
	 * @see sth.Person#getDiscipline(String)
	 */
	@Override
	Discipline getDiscipline(String disciplineName) throws InvalidDisciplineException  {
		Discipline d = _resgiteredDiciplines.get(disciplineName);
		if(d == null)
			throw new InvalidDisciplineException();
		
		return d;
	}

	@Override
	Course getCourse(){
		return _registeredCourse;
	}

	public String accept(PersonPrinter p) { return p.printPerson(this); }

	String printDisciplines(){
		String res = "";
		
		// build sorted body
		ArrayList<Discipline> matching = new ArrayList<Discipline>();
		_resgiteredDiciplines.forEach((discName, disc) -> {
				matching.add(disc);
		});
		Collections.sort(matching);

		// add body
		Iterator<Discipline> it = matching.iterator();
		while(it.hasNext()){
			Discipline d = it.next();
			res += "\n* " + _registeredCourse.getName() + " - " + d.getName();
		}
		
		return res;
	}
}
