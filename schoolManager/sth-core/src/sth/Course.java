package sth;

import java.io.Serializable;
import java.util.TreeMap;
//import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import sth.exceptions.InvalidDisciplineException;
import sth.exceptions.RepresentativeLimitExceededException;

import java.text.Collator;
import java.util.Iterator;

class Course implements Serializable, Comparable<Course>{
	
	/** Serial number for serialization. */
	private static final long serialVersionUID = 201810051538L;

	/** Maximum number of representatives in every course */
	static final int MAXREPRESENTATIVES = 7;

	/** Course representatives */
	private ArrayList<Integer> _representatives;
	
	/** course name */
	private final String _name;

	/** Disciplines associated to this course */
	private TreeMap<String, Discipline> _courseDisciplines;


	
	/**
	 * 
	 * @param name
	 * 		Course name
	 */
	Course(String name){
		_name = name;
		_courseDisciplines = new TreeMap<String, Discipline>();
		_representatives = new ArrayList<Integer>();
	}

	/**
	 * Associates Discipline to course
	 * @param discipline to add to list
	 */
	Discipline addNewDiscipline(String disciplineName){
		if(_courseDisciplines.containsKey(disciplineName))
			return _courseDisciplines.get(disciplineName);
		
		Discipline d = new Discipline(disciplineName);
		_courseDisciplines.put(disciplineName, d);
		return d;			

	}

	/**
	 * Get associated Discipline
	 * @param disciplineName
	 * @return
	 */
	Discipline getDiscipline(String disciplineName) throws InvalidDisciplineException {
		if(_courseDisciplines.containsKey(disciplineName))
			return _courseDisciplines.get(disciplineName);
		
		throw new InvalidDisciplineException();
	}

	/**
	 * Add new course representative
	 * @param id of new representative
	 */
	void addRepresentative(Person p) throws RepresentativeLimitExceededException {
		if(_representatives.size() >= MAXREPRESENTATIVES)
			throw new RepresentativeLimitExceededException();

		_representatives.add(p.getId());
		Iterator<Discipline> it = _courseDisciplines.values().iterator();
		while(it.hasNext()){
			Discipline d = it.next();
			d.addListener(p);
		}
	}

	/**
	 * 
	 * @param id
	 * @return whether the student associated with the given
	 * 		id is a representative or not
	 */
	boolean isRepresentative(int id) {
		return _representatives.contains(id);
	}

	/**
	 * @param id
	 * @return all the disciplines the Teacher with passed id currently lectures
	 */
	String showTeachingDisciplines(int id) {
		String res = "";
		ArrayList<Discipline> matching = new ArrayList<Discipline>();

		_courseDisciplines.forEach((discName, disc) -> {
			if (disc.hasProfessor(id))
				matching.add(disc);
		});
		Collections.sort(matching);

		Iterator<Discipline> it = matching.iterator();
		while(it.hasNext()){
			Discipline d = it.next();
			res += "\n* " + _name + " - " + d.getName();
		}
		return res;
	}

	/**
	 * 
	 * @return course name
	 */
	String getName(){ return _name; }

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() { return _name; }

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o){
		if (o instanceof Course){
			Course c = (Course) o;
			return (_name.equals(c.getName()));
		}
		return false;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Course c){
		final Collator nameCollator = Collator.getInstance();
		nameCollator.setStrength(Collator.NO_DECOMPOSITION);
		return nameCollator.compare(_name, c.getName());
	}
}