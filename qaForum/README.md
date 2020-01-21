# Q&A Forum (September - October 2019)

The goal of this project (developed alongside [Daniel](https://github.com/Beu-Wolf) and [Marcelo](https://github.com/tosmarcel)) was to build a simple online forum (both the client and the server side) to share questions and answers. Users can suggest topics, post questions composed of text (and possibly also an image to illustrate the question), and post answers.

## Functionality
A user can perform the following operations:
 * Register as a new user;
 * Request the list of topics available in the Forum Server;
 * Request, for a given topic, the list of available question titles and the indication if they have been replied;
 * Retrieve the text and images of a previous question and the corresponding replies available;
 * Submit an answer to an existing question;
 * Propose a new topic;
 * Submit a new question for an existing topic;

## Usage
Compile the project with `make`;
Run the Server program with `./FS [-p FSport]`
 \- `FSport` is the port where the server accepts TCP and UDP requests (`58036` default)

Run the client with `./user [-n FSIP] [-p FSport]`
 \- `FSIP` is the IP where the server is running (`localhost` default).
 \- `FSport` is the port where the server is accepting TCP and UDP requests (`58036` default)
 
 #### Client commands
  * `register <userID>`: Registers the current session. The result of the server validation is displayed to the user.
  * `topic_list`: Displays all the topics in the server.
  * `topic_select topic`: Selects a topic (locally) to work with in the subsequent commands. This command does not communicate with the server.
  * `topic_propose topic`: `topic` is suggested to be a new topic in the server. It also becomes the selected topic. The server response is displayed to the user. `tp topic` is also accepted;
  * `question_list`: Displays all the questions (and the number of replies for each one) related to the selected topic. `ql` is also accepted;
  * `question_get question`: Selects the given question and asks the server for the files (question and answer text and images) associated with the respective question. These files are stored in the local client machine, in a directory named after the selected topic;
  * `question_submit question text_file [image_file.ext]`: Submits a new question related to the selected topic, where `question` is the question title, `text_file` is the body and the (optional) `image_file.ext` is an illustrative image;
  * `answer_submit text_file [image_file.ext]`: Sends `text_file` and `image_file.ext` (if exists) to the server as an answer to the selected question;
  * `exit`: Exits the client application.
 
##### Aliases
 * `reg` is equivalent to `register`;
 * `tl` is equivalent to `topic_list`;
 * `ts topic_number` is equivalent to `topic_select topic`, where `topic_number` corresponds to the given topic number;
 * `tp` is equivalent to `topic_propose`;
 * `ql` is equivalent to `question_list`;
 * `qg question_number` is equivalent to `question_get question`, where `question_number` corresponds to the given question number;
 * `qs` is equivalent to `question_submit`;
 * `as` is equivalent to `answer_submit`;