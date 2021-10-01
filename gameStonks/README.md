# GameStonks (March - May 2021)

This project was developed in the course of Autonomous Agents and Multiagent Systems. Inspired by the GameStop incident of early 2021, we tried to replicate that using multi-agent system that acted on a simulated stock market.
You can find more information about this project and the results in [our report](report.pdf).

---

## Running this project
### Before

Before running this project, make sure you have Python 3 installed, have the Numpy, Matplotlib and pickle packages installed.
Also, if you want to run this project with a Q-matrix that results from previous runs, make sure there is a `bestQ.pickle` file here. We provide one already.
Alternatively, if you don't want to use any previous Q-matrix, just delete the file, or move it to another directory

### Running

Running the project is really easy. Just navigate to this directory, that has the `main.py` file and run, in a terminal, `./main.py` or even `python3 main.py`.
You will see prints of the market matching buy and sell orders and showing the current value of the stock.

### After

After the run is done, the graphs will be stored in the `plots` directory, in a folder with the datetime of when the run ended, for example `20210528100554`. The Q-matrix of the best agent will rewrite the previous `bestQ.pickle`.

---

Afonso Gonçalves</br>
Daniel Seara    </br>
Mariana Oliveira 
