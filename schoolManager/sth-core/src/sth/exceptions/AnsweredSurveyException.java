package sth.exceptions;

public class AnsweredSurveyException extends InvalidCancelException{
    private static final long serialVersionUID = 201409301048L;

    public AnsweredSurveyException(String origin){
        super(origin);
    }
}