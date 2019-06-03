package sth;

class SurveyResults {
    private int _max;
    private int _min;
    private double _avg;
    private int _numAnswers;

    SurveyResults(int min, double avg, int max, int numAnswers) {
        _min = min;
        _avg = avg;
        _max = max;
        _numAnswers = numAnswers;
    }

    int getMin() { return _min; }
    int getMax() { return _max; }
    double getAvg() { return _avg; }
    int getNumAnswers() { return _numAnswers; }
}
