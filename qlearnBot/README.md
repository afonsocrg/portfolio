# Q-Learning Agent (December 2019)
In this project, we were challenged to program a generic Q-Learning Agent. It would explore unknown environments and receive rewards based on its actions. These rewards would be considered in future decisions, allowing the agent to learn.

### Finding the best parameters
After having a working learning agent, we needed to find the optimal parameters to it.

After some research and reading some papers, we found that the most used parameters were pretty different from the ones that were giving us the best results. This happened because in the testing environment (provided by the teaching staff), the agent had few opportunities to learn, so we needed it to learn quickly (leading us to give an extremely high (0.995) learning rate, compared to the ones mostly used).

### Genetic Algorithms??
We still weren't convinced. We wanted to find the optimal parameters for this particular case. However, we didn't want to brute-force them and we also wanted a new challenge...

During the course, we talked briefly about genetic algorithms and their usage, but we didn't explore the algorithm itself... It caught our attention and we started our research again to find out how they work. (Actually, since I find this field of Computer Science exciting, I've been researching about it since I entered my Computer Science degree, in 2017).

Before we implemented the algorithm we would need to make the agent more versatile, by accepting its parameters in the constructor function. After that, we were able to create several agents with custom parameters, and soon we finished implemented the genetic algorithm.

Unfortunately, the algorithm wouldn't converge to any specific value, yielding random values every time we ran it...

### Dynamic progress bars???
We quickly found out that the genetic algorithm was slow, and if we tried logging information, it would be a mess of lines... Since we had no other projects to work with (ha, I wish... :'( ), we dove into it.

After learning about ANSI escape codes, we managed to make a neat screen that displayed progress bars and useful information. This last addition made me think that it would be fun to make (someday) a progress bar (maybe screen displaying) library in python.

### Conclusion
These extras made this project extremely challenging and fun to develop, making it one of my favourites up to this moment.
