package sth.app.person;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import sth.SchoolManager;
import sth.app.exceptions.NoSuchPersonException;
import sth.exceptions.NoSuchPersonIdException;

/**
 * 4.2.1. Show person.
 */
public class DoShowPerson extends Command<SchoolManager> {

  /**
   * @param receiver
   */
  public DoShowPerson(SchoolManager receiver) {
    super(Label.SHOW_PERSON, receiver);
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException{
    try {
      _display.popup(_receiver.showPerson());
    }
    catch(NoSuchPersonIdException e) {
      throw new NoSuchPersonException(_receiver.getLoginId());
    }
  }

}
