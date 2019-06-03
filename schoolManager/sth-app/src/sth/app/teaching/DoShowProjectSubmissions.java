package sth.app.teaching;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import sth.app.exceptions.NoSuchDisciplineException;
import sth.app.exceptions.NoSuchProjectException;

import sth.SchoolManager;
import sth.exceptions.InvalidDisciplineException;
import sth.exceptions.InvalidProjectException;
import sth.exceptions.InvalidOperationException;

/**
 * 4.3.3. Show project submissions.
 */
public class DoShowProjectSubmissions extends Command<SchoolManager> {

  Input<String> _disciplineName;
  Input<String> _projectName;

  /**
   * @param receiver
   */
  public DoShowProjectSubmissions(SchoolManager receiver) {
    super(Label.SHOW_PROJECT_SUBMISSIONS, receiver);
    _disciplineName = _form.addStringInput(Message.requestDisciplineName());
    _projectName = _form.addStringInput(Message.requestProjectName());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    _form.parse();
    try{
      _display.popup(_receiver.showProjectSubmissions(_disciplineName.value(), _projectName.value()));
    } catch(InvalidDisciplineException e){
      throw new NoSuchDisciplineException(_disciplineName.value());
    } catch(InvalidProjectException e){
      throw new NoSuchProjectException(_disciplineName.value(), _projectName.value());
    } catch(InvalidOperationException e) {
      e.printStackTrace();
    } 
  }
}
