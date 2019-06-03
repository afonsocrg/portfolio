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
import sth.exceptions.InvalidOpenException;
import sth.app.exceptions.NoSurveyException;
import sth.app.exceptions.OpeningSurveyException;
import sth.app.exceptions.NoSuchDisciplineException;

/**
 * 4.5.3. Open survey.
 */
public class DoOpenSurvey extends Command<SchoolManager> {

  Input<String> _disciplineName;
  Input<String> _projectName;

  /**
   * @param receiver
   */
  public DoOpenSurvey(SchoolManager receiver) {
    super(Label.OPEN_SURVEY, receiver);
    _disciplineName = _form.addStringInput(Message.requestDisciplineName());
    _projectName = _form.addStringInput(Message.requestProjectName());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    _form.parse();
    try{
      _receiver.openSurvey(_disciplineName.value(), _projectName.value());
    } catch (InvalidDisciplineException e ){
      throw new NoSuchDisciplineException(_disciplineName.value());
    } catch (InvalidProjectException e){
      throw new NoSuchProjectException(_disciplineName.value(), _projectName.value());
    } catch (InvalidSurveyException e){
      throw new NoSurveyException(_disciplineName.value(), _projectName.value());
    } catch (InvalidOpenException e){
      throw new OpeningSurveyException(_disciplineName.value(), _projectName.value());
    } catch (InvalidOperationException e){
      e.printStackTrace();
    }
  }

}
