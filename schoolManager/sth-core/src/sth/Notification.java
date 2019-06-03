package sth;

import java.io.Serializable;

class Notification implements Serializable{

    private static final long serialVersionUID = 201810051538L;

    private String _message;

    Notification(String message){
        _message = message;
    }

    public String show(){
        return _message;
    }
}
