package sth;

public class DefaultPrinter extends PersonPrinter {
    String printPerson(Professor p) {
        return "DOCENTE|" + personInfo(p) + p.printCourses();
    }

    String printPerson(Administrative a) {
        return "FUNCIONÁRIO|" + personInfo(a);
    }
    
    String printPerson(Student s) {
        return (s.isRepresentative() ? "DELEGADO|" : "ALUNO|") + personInfo(s) + s.printDisciplines();
    }

    String personInfo(Person p) {
        return "" + p.getId() + "|" + p.getPhoneNumber() + "|" + p.getName();
    }
}
