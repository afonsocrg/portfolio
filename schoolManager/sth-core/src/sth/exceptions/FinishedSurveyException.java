package sth.exceptions;

public class FinishedSurveyException extends InvalidCancelException{
    private static final long serialVersionUID = 201409301048L;

    public FinishedSurveyException(String origin){
        super(origin);
    }
}