package sth;

import java.io.Serializable;

/** Project submission implementation */
class Submission implements Serializable{

	/** Serial number for serialization. */
    private static final long serialVersionUID = 201810051538L;    
    
    /** commit message */
    private String _message;

    /** commiter id */
    private int _id;

    /**
     * 
     * @param id of commiter
     * @param m message to dissplay
     */
    Submission(int id, String m) { _message = m; _id = id; }


    int getStudent(){
        return _id;
    }
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() { return _id + " - " + _message; }
}
