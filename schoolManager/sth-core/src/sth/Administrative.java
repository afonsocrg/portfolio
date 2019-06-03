package sth;

import java.io.Serializable;
import sth.exceptions.*;

class Administrative extends Person implements Serializable{
	
	/** Serial number for serialization. */
  	private static final long serialVersionUID = 201810051538L;

	/**
	 * 
	 * @param name
	 * @param id
	 * @param phoneNumber
	 * @see sth.Person#Person(String, int, String)
	 */
	Administrative(String name, int id, String phoneNumber){
		super(name, id, phoneNumber);
	}

	/**
	 * @see sth.Person#isAdministrative()
	 */
	@Override
	boolean isAdministrative() { return true; }

	public String accept(PersonPrinter p) { return p.printPerson(this); }
}