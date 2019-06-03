package sth;

import java.util.HashSet;

interface Notificator{


    void addListener(Person p);
    void removeListener(Person p);

    void notifyListeners(String message);
}