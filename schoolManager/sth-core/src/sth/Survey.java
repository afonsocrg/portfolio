package sth;

import java.util.Vector;
import java.io.Serializable;
import java.util.HashSet;

import sth.exceptions.AnsweredSurveyException;
import sth.exceptions.CreatedSurveyException;
import sth.exceptions.OpenSurveyException;
import sth.exceptions.ClosedSurveyException;
import sth.exceptions.FinishedSurveyException;
import sth.exceptions.InvalidCancelException;
import sth.exceptions.InvalidCloseException;
import sth.exceptions.InvalidFinishException;
import sth.exceptions.InvalidOpenException;
import sth.exceptions.InvalidSurveyAnswerSubmissionException;

class Survey implements Serializable{
    /* Survey State: State Pattern*/
    private abstract class SurveyState implements Serializable {
        
        private static final long serialVersionUID = 201810051538L;
        /** cancel survey */
        public abstract void cancel() throws InvalidCancelException;
        /** close survey */
        public abstract void close() throws InvalidCloseException;
        /** finish survey */
        public abstract void finish() throws InvalidFinishException;
        /** open survey */
        public abstract void open() throws InvalidOpenException;
        /** answer survey */
        public void answer(int id, SurveyAnswer answer) throws InvalidSurveyAnswerSubmissionException {
            throw new InvalidSurveyAnswerSubmissionException("O projeto não está aberto.");
        }
        /** get survey results */
        public abstract SurveyResults getResults() throws CreatedSurveyException, OpenSurveyException, ClosedSurveyException;
        public void projectClosed() {}
    }

    /** Created State */
    private class CreatedState extends SurveyState implements Serializable {

        private static final long serialVersionUID = 201810051538L;

        @Override
        public void cancel() {
            _project.deleteSurvey();
        }
        
        @Override
        public void open() throws InvalidOpenException {
            // the survey is automatically opened after the project is closed. If in this state,
            // the project is for sure, open
            throw new InvalidOpenException("O projeto associado ainda não foi fechado.");
        }
        
        @Override
        public void close() throws InvalidCloseException {
            throw new InvalidCloseException("O projeto associado ainda não foi fechado.");
        }
        
        @Override
        public void finish() throws InvalidFinishException {
            throw new InvalidFinishException("O projeto associado ainda não foi fechado.");
        }

        @Override
        public SurveyResults getResults() throws CreatedSurveyException {
            throw new CreatedSurveyException();
        }

        @Override
        public void projectClosed() { _state = new OpenState(); }
    }

    /** Open State */
    private class OpenState extends SurveyState {

        private static final long serialVersionUID = 201810051538L;

        @Override
        public void cancel() throws InvalidCancelException {
            if(_answers.size() > 0)
                throw new AnsweredSurveyException("O projeto já tem respostas.");
            _project.deleteSurvey();
        }
        
        @Override
        public void open() throws InvalidOpenException {
            throw new InvalidOpenException("O projeto já está aberto");
        }
        
        @Override
        public void close() { _state = new ClosedState(); }
        
        @Override
        public void finish() throws InvalidFinishException {
            throw new InvalidFinishException("O projeto ainda está aberto");
        }

        @Override
        public void answer(int id, SurveyAnswer answer) {
            if(!_answerIds.contains(id)) {
                int hours = answer.hours();
                // recalculate quick statistics
                if(_answers.size() == 0) {
                    _avg = _min = _max = hours;
                } else {
                    if(hours > _max) _max = hours;
                    if(hours < _min) _min = hours;
                    _avg = ((_avg * _answers.size()) + hours) / (_answers.size() + 1);
                }

                _answerIds.add(id);
                _answers.add(answer);
            }
        }

        @Override
        public SurveyResults getResults() throws OpenSurveyException {
            throw new OpenSurveyException();
        }
    }

    /** Closed State */
    private class ClosedState extends SurveyState {

        private static final long serialVersionUID = 201810051538L;

        @Override
        public void cancel() {
            _state = new OpenState();
        }
        
        @Override
        public void open() {
            _state = new OpenState();
        }
        
        @Override
        public void close() { /* Do nothing here */}
        
        @Override
        public void finish() { _state = new FinishedState(); }

        @Override
        public SurveyResults getResults() throws ClosedSurveyException {
            throw new ClosedSurveyException();
        }
    }

    /** Finished State */
    private class FinishedState extends SurveyState {

        private static final long serialVersionUID = 201810051538L;

        @Override
        public void cancel() throws InvalidCancelException {
            throw new FinishedSurveyException("O projeto está finalizado");
        }
        
        @Override
        public void open() throws InvalidOpenException {
            throw new InvalidOpenException("O projeto está finalizado");
        }
        
        @Override
        public void close() throws InvalidCloseException {
            throw new InvalidCloseException("O projeto está finalizado");
        }
        
        @Override
        public void finish() { /* Do nothing here */}

        @Override
        public SurveyResults getResults() {
            return new SurveyResults(_min, _avg, _max, _answers.size());
        }
    }

    /*Serial number for serealization */
    private static final long serialVersionUID = 201810051538L;

    /* Survey state: State pattern implementation */
    private SurveyState _state;
    
    /* Corresponding project */
    private Project _project;

    /* Save answers */
    private Vector<SurveyAnswer> _answers;

    /* Hash to quick searches. Set because no order is maintained */
    private HashSet<Integer> _answerIds;

    /* Survey statistics (for quick access) */
    private int _max = 0;
    private double _avg = 0;
    private int _min = 0;

    public Survey(Project p) {
        _state = new CreatedState();
        _answers = new Vector<SurveyAnswer>();
        _answerIds = new HashSet<Integer>();
        _project = p;
    }

    /** cancel survey */
    public void cancel() throws InvalidCancelException { _state.cancel(); }
    /** open survey */
    public void open() throws InvalidOpenException { _state.open(); }
    /** close survey */
    public void close() throws InvalidCloseException { _state.close(); }
    /** finish survey */
    public void finish() throws InvalidFinishException { _state.finish(); }
    /** answer survey */
    public void answer(int id, SurveyAnswer answer) throws InvalidSurveyAnswerSubmissionException {
         _state.answer(id, answer);
    }

    public int getAnswerNumber(){
        return _answers.size();
    }
    
    /** get survey results */
    public SurveyResults getResults() throws CreatedSurveyException, OpenSurveyException, ClosedSurveyException { return _state.getResults(); }

    // implicit state change
    /** change survey state to open */
    public void projectClosed() { _state.projectClosed(); }   
}
