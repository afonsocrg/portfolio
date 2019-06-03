package sth;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.FileNotFoundException;
import java.io.NotSerializableException;

import sth.exceptions.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;


/**
 * The façade class
 */
public class SchoolManager {

	/**
	 * Managed school
	 */
	private School _school = new School();

	/**
	 * Current user
	 */
	private Person _user;

	/**
	 * Current school saving file
	 */
	private String _savingFile;

	/**
	 * If school has changed since last time saved
	 */
	private boolean _changed = true;

	/**
	 * @return 
	 */
	public boolean hasSavingFile(){
		return _savingFile != null;
	}

	/**
	 * @return id of current logged in person
	 */
	public int getLoginId(){
		return _user.getId();
	}

	private void changed() { _changed = true; }

	/**
	 * @param datafile to import from
	 * @throws ImportFileException
	 * @throws InvalidCourseSelectionException
	 */
	public void importFile(String datafile) throws ImportFileException {
		try {
			_school.importFile(datafile);
		} catch (IOException | BadEntryException e) {
			throw new ImportFileException(e);
		}
	}

	/**
	 * Validates user and resets it
	 * @param id
	 * @throws NoSuchPersonIdException
	 */
	public void login(int id) throws NoSuchPersonIdException {
		if(_user != null && id != _user.getId()) {
			// should never happen
		} else {
			_user = _school.getPerson(id);
		}
	}

	/**
	 * @return true when the currently logged in person is an administrative
	 */
	public boolean hasAdministrative() {
		return _user.isAdministrative();
	}

	/**
	 * @return true when the currently logged in person is a professor
	 */
	public boolean hasProfessor() {
		return _user.isProfessor();
	}

	/**
	 * @return true when the currently logged in person is a student
	 */
	public boolean hasStudent() {
		return _user.isStudent();
	}

	/**
	 * @return true when the currently logged in person is a representative
	 */
	public boolean hasRepresentative() {
		return _user.isRepresentative();
	}

	/**
	 *
	 * @param newPhoneNumber to assign to user
	 * @return updated user stringified
	 */
	public String changePhoneNumber(String newPhoneNumber) {
		changed();
		_user.setPhoneNumber(newPhoneNumber);
		return _user.accept(new DefaultPrinter());
	}

	/**
	 *
	 * @return sringified current user
	 * @throws NoSuchPersonIdException
	 */
	public String showPerson() throws NoSuchPersonIdException {
		return _school.printPerson(_user.getId());
	}

	/**
	 *
	 * @return all people from school stringified
	 */
	public String showAllPersons() {
		return _school.printAllPeople();
	}

	/**
	 *
	 * @param searchQuery
	 * @return set of stringified people that match
	 *  the search
	 */
	public String searchPerson(String searchQuery) {
		return _school.printMatchingPeople(searchQuery);
	}

	/**
	 *
	 * @param disciplineName
	 * @return stringified students that attend given Discipline
	 * @throws InvalidDisciplineException
	 */
	public String showDisciplineStudents(String disciplineName) throws InvalidDisciplineException {
		if (hasProfessor()) //check if current user can execute this command
			return _user.getDiscipline(disciplineName).showStudents();

		return ""; //Invalid Access
	}

	/**
	 *
	 * @param disciplineName
	 * @param projectName
	 * @throws InvalidDisciplineException
	 * @throws InvalidProjectException
	 */
	public void createProject(String disciplineName, String projectName) throws InvalidDisciplineException, InvalidProjectException{
		if(hasProfessor()){
			changed();
			_user.getDiscipline(disciplineName).createProject(new Project(projectName, "Projeto: " + projectName));
		}	//check if current user can execute this command
			       
	}

	/**
	 *
	 * @param disciplineName
	 * @param projectName
	 * @throws InvalidDisciplineException
	 * @throws InvalidProjectException
	 */
	public void closeProject(String disciplineName, String projectName) throws InvalidDisciplineException, InvalidProjectException {
		if(hasProfessor()){ //check if current user can execute this command
			changed();
			_user.getDiscipline(disciplineName).closeProject(projectName);
		}
	}

	/**
	 * 
	 * @param disciplineName
	 * @param projectName
	 * @return
	 * 	Stringified project submissions
	 * @throws InvalidDisciplineException
	 * @throws InvalidProjectException
	 */
	public String showProjectSubmissions(String disciplineName, String projectName) throws InvalidOperationException, InvalidDisciplineException, InvalidProjectException {
		if(!hasProfessor()) //check if current user can execute this command
			throw new InvalidOperationException();	
		
		return _user.getDiscipline(disciplineName).showProjectSubmissions(projectName);
	}


	/**
	 * 
	 * @param disciplineName
	 * @param projectName
	 * @param deliveryMessage
	 * @throws InvalidDisciplineException
	 * @throws InvalidProjectException
	 */
	public void deliverProject(String disciplineName, String projectName, String deliveryMessage) throws InvalidOperationException, InvalidDisciplineException, InvalidProjectException{
		if(!hasStudent())
			throw new InvalidOperationException();
		
		changed();
		_user.getDiscipline(disciplineName).deliverProject(projectName, new Submission(_user.getId(), deliveryMessage)); 
	}

	/**
	 * 
	 * @param disciplineName
	 * @param projectName
	 * @throws InvalidOperationException
	 * @throws InvalidDisciplineException
	 * @throws InvalidProjectException
	 * @throws InvalidSurveyException
	 */
	public void createSurvey(String disciplineName, String projectName) throws InvalidOperationException, InvalidDisciplineException, InvalidProjectException, InvalidSurveyException{
		if(!hasRepresentative())
			throw new InvalidOperationException();
		
		changed();
		Discipline d = _user.getCourse().getDiscipline(disciplineName);
		d.createSurvey(projectName);
		
	}

	/**
	 * 
	 * @param disciplineName
	 * @param projectName
	 * @param hoursSpent
	 * @param comment
	 * @throws InvalidOperationException
	 * @throws InvalidDisciplineException
	 * @throws InvalidProjectException
	 * @throws InvalidSurveyException
	 * @throws InvalidSurveyAnswerSubmissionException
	 */
	public void answerSurvey(String disciplineName, String projectName, int hoursSpent, String comment) throws InvalidOperationException, InvalidDisciplineException, InvalidProjectException, InvalidSurveyException, InvalidSurveyAnswerSubmissionException {
		if(!hasStudent())
			throw new InvalidOperationException();

		changed();
		_user.getDiscipline(disciplineName).answerSurvey(projectName, _user.getId(), new SurveyAnswer(hoursSpent, comment));
	}

	/**
	 * 
	 * @param disciplineName
	 * @param projectName
	 * @throws InvalidOperationException
	 * @throws InvalidDisciplineException
	 * @throws InvalidProjectException
	 * @throws InvalidSurveyException
	 * @throws InvalidCancelException
	 */
	public void cancelSurvey(String disciplineName, String projectName) throws InvalidOperationException, InvalidDisciplineException, InvalidProjectException, InvalidSurveyException, InvalidCancelException{
		if(!hasRepresentative())
			throw new InvalidOperationException();
		
		changed();
	
		Discipline d = _user.getCourse().getDiscipline(disciplineName);
		d.cancelSurvey(projectName);
		
		
	}
	/**
	 * 
	 * @param disciplineName
	 * @param projectName
	 * @throws InvalidOperationException
	 * @throws InvalidDisciplineException
	 * @throws InvalidProjectException
	 * @throws InvalidSurveyException
	 * @throws InvalidOpenException
	 */
	public void openSurvey(String disciplineName, String projectName) throws InvalidOperationException, InvalidDisciplineException, InvalidProjectException, InvalidSurveyException, InvalidOpenException{
		if(!hasRepresentative())
			throw new InvalidOperationException();
		
		
		Discipline d = _user.getCourse().getDiscipline(disciplineName);
		d.openSurvey(projectName);
		
	}

	/**
	 * 
	 * @param disciplineName
	 * @param projectName
	 * @throws InvalidOperationException
	 * @throws InvalidDisciplineException
	 * @throws InvalidProjectException
	 * @throws InvalidSurveyException
	 * @throws InvalidCloseException
	 */
	public void closeSurvey(String disciplineName, String projectName) throws InvalidOperationException, InvalidDisciplineException, InvalidProjectException, InvalidSurveyException, InvalidCloseException{
		if(!hasRepresentative())
			throw new InvalidOperationException();

		
		Discipline d = _user.getCourse().getDiscipline(disciplineName);
		d.closeSurvey(projectName);
		
	}

	public void finishSurvey(String disciplineName, String projectName) throws InvalidOperationException, InvalidDisciplineException, InvalidProjectException,InvalidSurveyException, InvalidFinishException{
		if(!hasRepresentative())
			throw new InvalidOperationException();

		
		Discipline d = _user.getCourse().getDiscipline(disciplineName);
		d.finishSurvey(projectName);
		
	}

	public String showSurveyResults(String disciplineName, String projectName) throws InvalidOperationException, InvalidDisciplineException, InvalidProjectException, InvalidSurveyException{
		return _user.showSurveyResults(disciplineName, projectName);
	}

	public String showDisciplineSurveys(String disciplineName) throws InvalidOperationException, InvalidDisciplineException{
		return _user.showDisciplineSurveys(disciplineName);
	}
	
	/**
	 * Open previously saved school from inputFile
	 * @param inputFile
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws NoSuchPersonIdException
	 */
	public String open(String inputFile) throws InvalidOperationException, ClassNotFoundException, IOException, FileNotFoundException, NoSuchPersonIdException {
		if(inputFile == null)
			throw new FileNotFoundException();
		
		changed();
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(inputFile));
		_savingFile = inputFile;
		School newSchool = (School) in.readObject();
		in.close();
		School old = _school;
		try {
			_school = newSchool;
			login(_user.getId());
			return _user.showNotification();
		} catch(NoSuchPersonIdException e) {
			_school = old;
			throw e;
		}
	}

	/**
	 * Save current school in file
	 * @param outputFile
	 * @throws IOException
	 * @throws InvalidClassException
	 * @throws NotSerializableException
	 */
	public void save(String outputFile) throws IOException, InvalidClassException, NotSerializableException {
		ObjectOutputStream out;
		if(!_changed) return;

		_changed = false;
		if(outputFile != null) {
			_savingFile = outputFile;
		}

		out = new ObjectOutputStream(new FileOutputStream(_savingFile));
		out.writeObject(_school);
		out.close();
		
	}

	public void save() throws IOException, InvalidClassException, NotSerializableException { 
		save(_savingFile); 
	}
}
