package sth.app.student;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;
import sth.SchoolManager;
import sth.app.exceptions.NoSuchDisciplineException;
import sth.app.exceptions.NoSuchProjectException;
import sth.app.exceptions.NoSurveyException;
import sth.exceptions.InvalidOperationException;
import sth.exceptions.InvalidProjectException;
import sth.exceptions.InvalidSurveyAnswerSubmissionException;
import sth.exceptions.InvalidDisciplineException;
import sth.exceptions.InvalidSurveyException;

/**
 * 4.4.2. Answer survey.
 */
public class DoAnswerSurvey extends Command<SchoolManager> {

  Input<String> _disciplineName;
  Input<String> _projectName;
  Input<String> _comment;
  Input<Integer> _hoursSpent;

  /**
   * @param receiver
   */
  public DoAnswerSurvey(SchoolManager receiver) {
    super(Label.ANSWER_SURVEY, receiver);
    _disciplineName = _form.addStringInput(Message.requestDisciplineName());
    _projectName = _form.addStringInput(Message.requestProjectName());
    _hoursSpent = _form.addIntegerInput(Message.requestProjectHours());
    _comment = _form.addStringInput(Message.requestComment());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    _form.parse();
    try {
      _receiver.answerSurvey(_disciplineName.value(), _projectName.value(), _hoursSpent.value(), _comment.value());
    } catch(InvalidDisciplineException e) {
      throw new NoSuchDisciplineException(_disciplineName.value());
    } catch(InvalidProjectException e) {
      throw new NoSuchProjectException(_disciplineName.value(), _projectName.value());
    } catch(InvalidSurveyAnswerSubmissionException | InvalidSurveyException e) {
      throw new NoSurveyException(_disciplineName.value(), _projectName.value());
    } catch(InvalidOperationException e) {
      e.printStackTrace();
    } 
    
  }

}
