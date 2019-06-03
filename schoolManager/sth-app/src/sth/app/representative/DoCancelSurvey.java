package sth.app.representative;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;
import sth.SchoolManager;

import sth.app.exceptions.NoSuchDisciplineException;
import sth.exceptions.AnsweredSurveyException;
import sth.exceptions.FinishedSurveyException;
import sth.exceptions.InvalidCancelException;
import sth.exceptions.InvalidDisciplineException;
import sth.exceptions.InvalidOperationException;
import sth.app.exceptions.NoSuchProjectException;
import sth.exceptions.InvalidProjectException;
import sth.exceptions.InvalidSurveyException;
import sth.app.exceptions.NonEmptySurveyException;
import sth.app.exceptions.NoSurveyException;
import sth.app.exceptions.SurveyFinishedException;


/**
 * 4.5.2. Cancel survey.
 */
public class DoCancelSurvey extends Command<SchoolManager> {

  Input<String> _disciplineName;
  Input<String> _projectName;

  /**
   * @param receiver
   */
  public DoCancelSurvey(SchoolManager receiver) {
    super(Label.CANCEL_SURVEY, receiver);
    _disciplineName = _form.addStringInput(Message.requestDisciplineName());
    _projectName = _form.addStringInput(Message.requestProjectName());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    _form.parse();
    try{
      _receiver.cancelSurvey(_disciplineName.value(), _projectName.value());
    } catch (InvalidDisciplineException e ){
      throw new NoSuchDisciplineException(_disciplineName.value());
    } catch (InvalidProjectException e){
      throw new NoSuchProjectException(_disciplineName.value(), _projectName.value());
    } catch (AnsweredSurveyException e) {
      throw new NonEmptySurveyException(_disciplineName.value(), _projectName.value());
    } catch (FinishedSurveyException e){
      throw new SurveyFinishedException(_disciplineName.value(), _projectName.value());
    } catch (InvalidCancelException e){
      e.printStackTrace();
    } catch (InvalidSurveyException e){
      throw new NoSurveyException(_disciplineName.value(), _projectName.value());
    } catch (InvalidOperationException e){
      e.printStackTrace();
    }
  }

}
