package sth;

import java.io.Serializable;
import java.util.TreeMap;
import java.util.Set;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import sth.exceptions.*;

class Professor extends Person implements Serializable{

  	/** Serial number for serialization. */
  	private static final long serialVersionUID = 201810051538L;

	/** Teaching courses */
	private TreeMap<String, Course> _lecturedCourses = new TreeMap<String, Course>();

	/** Teaching disciplines */
	private TreeMap<String, Discipline> _lecturedDisciplines = new TreeMap<String, Discipline>();

	private LinkedList<Notification> _notifications = new LinkedList<Notification>();

	/**
	 * 
	 * @param name
	 * @param id
	 * @param phoneNumber
	 * @see sth.Person#Person(String, int, String)
	 */
	Professor(String name, int id, String phoneNumber){
		super(name, id, phoneNumber);
	}

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

	/**
	 * @see sth.Person#isProfessor()
	 */
	@Override
	boolean isProfessor() { return true; }

	/**
	 * Add new teaching Course
	 * @see sth.Person#registerCourse(sth.Course)
	 * @param newCourse
	 */
	@Override
	void registerCourse(Course newCourse) {
		if(!_lecturedCourses.containsValue(newCourse))
			_lecturedCourses.put(newCourse.getName(), newCourse);
	}

	/**
	 * @see sth.Person#registerDiscipline(sth.Discipline)
	 */
	@Override
	void registerDiscipline(Discipline newDiscipline){
		newDiscipline.registerProfessor(this);
		_lecturedDisciplines.put(newDiscipline.getName(), newDiscipline);
	}

	@Override 
	String showSurveyResults(String disciplineName, String projectName) throws InvalidDisciplineException, InvalidProjectException, InvalidSurveyException {
		String res = disciplineName + " - " + projectName;
		Project p = getDiscipline(disciplineName).getProject(projectName);
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

		res += "\n" + " * Número de Submissões: " + p.getSubmissionNumber();
		res += "\n" + " * Número de Respostas: " + sR.getNumAnswers();
		res += "\n" + " * Tempos de resolução (horas) (mínimo, médio, máximo): " + sR.getMin() + ", " + sR.getAvg() + ", " + sR.getMax();
		return res;
	}

	/**
	 * @see sth.Person#getDiscipline(String)
	 */
	@Override
	Discipline getDiscipline(String disciplineName) throws InvalidDisciplineException{
		Discipline d = _lecturedDisciplines.get(disciplineName);
		if(d == null) {
			throw new InvalidDisciplineException();
		} else {
			return _lecturedDisciplines.get(disciplineName);
		}
	}

	public String accept(PersonPrinter p) { return p.printPerson(this); }

	String printCourses(){
		String res = "";
		Iterator<Course> it = _lecturedCourses.values().iterator();
		while(it.hasNext()) { // iterate over courses
			Course c = it.next();
			res += c.showTeachingDisciplines(getId());
		}
		return res;
	}
}
