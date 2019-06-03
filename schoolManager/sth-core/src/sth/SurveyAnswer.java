package sth;

import java.io.Serializable;

class SurveyAnswer implements Serializable{
    private static final long serialVersionUID = 201810051538L;
    private String _comment;
    private int _hoursRequired;

    SurveyAnswer(int hoursRequired, String comment) {
        _comment = comment;
        _hoursRequired = hoursRequired;
    }

    public int hours() { return _hoursRequired; }
}