package sth.app.main;

import java.io.FileNotFoundException;
import java.io.IOException;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.Input;
import pt.tecnico.po.ui.Menu;
import pt.tecnico.po.ui.DialogException;
import sth.SchoolManager;
import sth.exceptions.InvalidOperationException;
import sth.exceptions.NoSuchPersonIdException;
import sth.app.exceptions.NoSuchPersonException;
import sth.exceptions.InvalidOperationException;

/**
 * 4.1.1. Open existing document.
 */
public class DoOpen extends Command<SchoolManager>{

  /*input file*/
  Input<String> _inputFile;
  
  /**
   * @param receiver
   */
  public DoOpen(SchoolManager receiver) {
    super(Label.OPEN, receiver);
    _inputFile = _form.addStringInput(Message.openFile());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException{
    _form.parse();
    try {
      if(_inputFile.value() != null)
        _display.popup(_receiver.open(_inputFile.value()));
        
    } catch (FileNotFoundException fnfe) {
      _display.popup(Message.fileNotFound());
    } catch (ClassNotFoundException | IOException e) {
      e.printStackTrace();
    } catch (NoSuchPersonIdException e){
      throw new NoSuchPersonException(_receiver.getLoginId());
    } catch (InvalidOperationException e) {
      e.printStackTrace();
    }
  }

}
