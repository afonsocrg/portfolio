package sth.app.student;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;
import sth.SchoolManager;
import sth.app.exceptions.NoSuchDisciplineException;
import sth.exceptions.InvalidDisciplineException;
import sth.exceptions.InvalidOperationException;
import sth.app.exceptions.NoSuchProjectException;
import sth.exceptions.InvalidProjectException;

/**
 * 4.4.1. Deliver project.
 */
public class DoDeliverProject extends Command<SchoolManager> {

  Input<String> _disciplineName;
  Input<String> _projectName;
  Input<String> _deliveryMessage;

  /**
   * @param receiver
   */
  public DoDeliverProject(SchoolManager receiver) {
    super(Label.DELIVER_PROJECT, receiver);
    _disciplineName = _form.addStringInput(Message.requestDisciplineName());
    _projectName = _form.addStringInput(Message.requestProjectName());
    _deliveryMessage = _form.addStringInput(Message.requestDeliveryMessage());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    _form.parse();
    try {
      _receiver.deliverProject(_disciplineName.value(), _projectName.value(), _deliveryMessage.value()); 
    } catch (InvalidDisciplineException e) {
      throw new NoSuchDisciplineException(_disciplineName.value());
    } catch (InvalidProjectException e){
      throw new NoSuchProjectException(_disciplineName.value(), _projectName.value());
    } catch(InvalidOperationException e) {
      e.printStackTrace();
    }
  }

}
