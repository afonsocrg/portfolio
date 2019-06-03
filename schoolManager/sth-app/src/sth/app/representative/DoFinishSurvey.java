package sth.app.representative;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;
import sth.SchoolManager;

import sth.exceptions.InvalidDisciplineException;
import sth.exceptions.InvalidOperationException;
import sth.app.exceptions.NoSuchProjectException;
import sth.exceptions.InvalidProjectException;
import sth.exceptions.InvalidSurveyException;
import sth.exceptions.InvalidFinishException;
import sth.app.exceptions.NoSurveyException;
import sth.app.exceptions.FinishingSurveyException;
import sth.app.exceptions.NoSuchDisciplineException;

/**
 * 4.5.5. Finish survey.
 */
public class DoFinishSurvey extends Command<SchoolManager> {

  Input<String> _disciplineName;
  Input<String> _projectName;

  /**
   * @param receiver
   */
  public DoFinishSurvey(SchoolManager receiver) {
    super(Label.FINISH_SURVEY, receiver);
    _disciplineName = _form.addStringInput(Message.requestDisciplineName());
    _projectName = _form.addStringInput(Message.requestProjectName());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    _form.parse();

    try{
      _receiver.finishSurvey(_disciplineName.value(), _projectName.value());
    } catch (InvalidDisciplineException e){
      throw new NoSuchDisciplineException(_disciplineName.value());
    } catch (InvalidProjectException e){
      throw new NoSuchProjectException(_disciplineName.value(), _projectName.value());
    } catch (InvalidSurveyException e){
      throw new NoSurveyException(_disciplineName.value(), _projectName.value());
    } catch (InvalidFinishException e){
      throw new FinishingSurveyException(_disciplineName.value(), _projectName.value());
    } catch (InvalidOperationException e){
      e.printStackTrace();
    }
  }

}
