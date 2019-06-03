package sth.app.student;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;
import sth.SchoolManager;

import sth.app.exceptions.NoSuchDisciplineException;
import sth.exceptions.InvalidDisciplineException;
import sth.exceptions.InvalidOperationException;
import sth.app.exceptions.NoSuchProjectException;
import sth.app.exceptions.NoSurveyException;
import sth.exceptions.InvalidProjectException;
import sth.exceptions.InvalidSurveyException;


/**
 * 4.4.3. Show survey results.
 */
public class DoShowSurveyResults extends Command<SchoolManager> {

  Input<String> _disciplineName;
  Input<String> _projectName;

  /**
   * @param receiver
   */
  public DoShowSurveyResults(SchoolManager receiver) {
    super(Label.SHOW_SURVEY_RESULTS, receiver);
    _disciplineName = _form.addStringInput(Message.requestDisciplineName());
    _projectName = _form.addStringInput(Message.requestProjectName());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    _form.parse();
    try{
      _display.popup(_receiver.showSurveyResults(_disciplineName.value(), _projectName.value()));
    } catch (InvalidDisciplineException e){
      throw new NoSuchDisciplineException(_disciplineName.value());
    } catch (InvalidProjectException e){
      throw new NoSuchProjectException(_disciplineName.value(), _projectName.value());
    } catch (InvalidSurveyException e){
      throw new NoSurveyException(_disciplineName.value(), _projectName.value());
    } catch (InvalidOperationException e){
      e.printStackTrace();
    }
  }

}
