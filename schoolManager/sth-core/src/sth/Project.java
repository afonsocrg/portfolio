package sth;

import java.io.Serializable;
import java.util.TreeMap;

import sth.exceptions.InvalidCancelException;
import sth.exceptions.InvalidCloseException;
import sth.exceptions.InvalidOpenException;
import sth.exceptions.InvalidFinishException;
import sth.exceptions.InvalidProjectException;
import sth.exceptions.InvalidSurveyAnswerSubmissionException;
import sth.exceptions.InvalidSurveyException;
import sth.exceptions.CreatedSurveyException;
import sth.exceptions.ClosedSurveyException;
import sth.exceptions.OpenSurveyException;

import java.util.Iterator;

/**Project implementation. */

class Project implements Serializable {

	/** Serial number for serialization. */
    private static final long serialVersionUID = 201810051538L;    

    /** Project name */
    private String _name;

    /** Project description */
    private String _description;

    /** current Project state -> open or closed */
    private boolean _isOpen;

    /** Collection of Project Submissions */
    private TreeMap<Integer, Submission> _submissions;

    private Survey _survey;

    /**
     * 
     * @param name of Project
     * @param description of Project
     */
    Project(String name, String description) {
        _name = name;
        _description = description;
        _isOpen = true;
        _submissions = new TreeMap<Integer, Submission>();
    }

    /**
     * change project current state to closed
     *
     */
    void close(){ 
        if(!_isOpen) return;
        
        _isOpen = false;
        if(hasSurvey())
            _survey.projectClosed();
    }

    boolean hasSurvey(){
        return _survey != null;
    }

    Survey getSurvey() throws InvalidSurveyException{
        if(!hasSurvey())
            throw new InvalidSurveyException();
        
        return _survey;
    }


    void deleteSurvey() { _survey = null; }

    /**
     * Save a submission in submissions
     * @param id of person who's submitting
     * @param submission
     */
    void submit(Submission newSub) throws InvalidProjectException {
        if(!_isOpen)
            throw new InvalidProjectException();
        
        _submissions.put(newSub.getStudent(), newSub);
    }

    int getSubmissionNumber(){
        return _submissions.size();
    }

    boolean hasSubmited(int id){
        return _submissions.containsKey(id);
    }

    /**
     * @return all submissions of this project ordered by submission time
     */
    String showSubmissions() {
        String res = _name;
        Iterator<Submission> it = _submissions.values().iterator();
        while(it.hasNext()) {
            res += "\n* " + it.next();
        }
        return res;
    }

    void createSurvey() throws InvalidProjectException, InvalidSurveyException{
        if (!_isOpen)
            throw new InvalidProjectException();
        if(hasSurvey()){
            throw new InvalidSurveyException();
        }

        _survey = new Survey(this);
    }

    void answerSurvey(int id, SurveyAnswer answer) throws InvalidProjectException, InvalidSurveyException, InvalidSurveyAnswerSubmissionException {
        if(!hasSubmited(id))
            throw new InvalidProjectException();
        
        if(!hasSurvey())
            throw new InvalidSurveyException();
        
        
        _survey.answer(id, answer);
    }

    void cancelSurvey() throws InvalidSurveyException, InvalidCancelException{
        if (!hasSurvey())
            throw new InvalidSurveyException();
        
        _survey.cancel();
    }

    void openSurvey() throws InvalidSurveyException, InvalidOpenException{
        if(!hasSurvey())
            throw new InvalidSurveyException();

        _survey.open();
    }

    void closeSurvey() throws InvalidSurveyException, InvalidCloseException{
        if(!hasSurvey())
            throw new InvalidSurveyException();

        _survey.close();
    }

    void finishSurvey() throws InvalidSurveyException, InvalidFinishException{
        if(!hasSurvey())
            throw new InvalidSurveyException();

        _survey.finish();
    }

    boolean isOpen() { return _isOpen; }


    SurveyResults getSurveyResults() throws InvalidSurveyException, CreatedSurveyException, OpenSurveyException, ClosedSurveyException {
        if(_survey == null) throw new InvalidSurveyException();
        return _survey.getResults();
    }

    /**
     * 
     * @return project name
     */
    String getName() { return _name; }
}
