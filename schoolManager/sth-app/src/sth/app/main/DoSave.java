package sth.app.main;

import java.io.IOException;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.Input;
import pt.tecnico.po.ui.DialogException;
import sth.SchoolManager;


/**
 * 4.1.1. Save to file under current name (if unnamed, query for name).
 */
public class DoSave extends Command<SchoolManager> {

  Input<String> _outputFile;

  /**
   * @param receiver
   */
  public DoSave(SchoolManager receiver) {
    super(Label.SAVE, receiver);
}

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    try{
      if (!_receiver.hasSavingFile()){
        _outputFile = _form.addStringInput(Message.newSaveAs());
        _form.parse();
        _receiver.save(_outputFile.value());
      }else {
        _receiver.save();
      }
    } catch (IOException e){
      // catches also NotSerializableException InvalidClassException
      e.printStackTrace();
    }
  }
}
