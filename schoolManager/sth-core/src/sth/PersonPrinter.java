package sth;

abstract class PersonPrinter {
    abstract String printPerson(Professor p);
    abstract String printPerson(Administrative a);
    abstract String printPerson(Student s);
}
