# snAIke
An artificial intelligence programming challenge based on the game of snake (with a few added twists)



## 1	Getting Started

These instructions will get you up and running in developing a snake agent and testing it.



### 1.1	Prerequisites

The only file that's needed prior to developing your own snake agent is GameEngine.jar and since it's a jar file it should run on most operating systems. Java 8 will need to be installed in order to develop a snake agent (this is done using Java or Python). You'll need the JDK is you're using Java but only the JRE if you're using Python.



### 1.2	Running

You can run the game from the command prompt or terminal by executing the following command (where the normal keys refer to the arrow keys):

```
java -jar GameEngine.jar [-j path/to/java.jar] [-p path/to/python.py] [-h <keys>|normal]
```

So let's say we want to run a game, providing one python file, one human player who will use the arrow keys and one human player who will use the use the WASD keys. We would accomplish this with the following:

```
java -jar GameEngine.jar -p agent.py -h normal -h wasd
```

If you created a Java agent, then export your project to an executable jar file.



## 2	How it Works

The objective of the game is to write a program that can play this multiplayer version of snake that can eat enough apples to become the longest snake on the board whilst avoiding the zombie snakes. Don't forget about the other players though - you'll want to stop them doing the same thing by any means necessary.



### 2.1	Game Mechanics

Here's how it all works. At any point in time, there are two apples on the board, eating any of which causes your snake to grow by a certain length. There are also three zombies present who will attempt to attack you. There are three main actors in the game: snakes, apples, and zombies. Let's look at the mechanics of each in turn.



#### 2.1.1	Snakes

Snakes move in one of the four cardinal directions at a speed of one square per timestep, with all moves executed simultaneously. A snake dies when it collides with any of the grid's sides, or moves into a non-empty and non-apple square. If a snake collides with the body of another snake, the latter is credited with a kill. In the event of a head-on collision between two snakes, both snakes are killed, but neither is credited with a kill. Additionally, a snake can be killed if a zombie collides with its head. When a snake dies, it will miss the next timestep, and be respawned on the following step at a random location on the board. Note that if your program has crashed, your snake will be removed from the board for the duration of the round.



#### 2.1.2	Apples

Both apples appear in random locations at the beginning of the game. Every time an apple is eaten, it is respawned at a random location. Additionally, apples will be respawned if they haven't been eaten within a certain number of moves. Eating an apple causes a snake to grow for the next few rounds by having its tail remain in its current position. If multiple snakes consume the same apple at the same time, both snakes are killed and the apple is respawned in a new location.



#### 2.1.3	Zombies

Zombies move around the board in pursuit of the snakes. They execute their moves after all the agents have made theirs, but they do so sequentially (as opposed to the snakes, who move simultaneously). Each zombie will take it in turn to move in the direction of the snake whose head is closest to it, provided that the square is empty or is a snake's head. If a zombie has two or more equally good actions available, it will first pick LEFT, then RIGHT, then UP, and finally DOWN. Note that zombies cannot be killed, so if one has no available move, it will simply remain in place for its turn.



### 2.2	Interacting with the Game

Now that we've explained how the game works, let's look at how your agent will actually play the game. Your agent interacts with the game through its standard IO mechanisms. This means that at every step, your agent will read in the current state of the game, and make its move by printing to standard output.



#### 2.2.1	Initialisation

When the game first starts, each agent is sent an initialisation string which specifies the number of snakes in the game, the width and height of the board, and the type of game being played (mode). For these purposes, you can assume that the number of snakes is always 4, the width and height 50, and the mode 1. The initial input thus looks like this:

> 4 50 50 1



#### 2.2.2	Game-State Updates

At each step in the game, a game-state string is generated and sent to all agents, which can then be read in via standard input. Coordinates are such that (0,0) represents the top left square of the board. Each state takes the following form:

> x- and y-coordinates of the first apple
> x- and y-coordinates of the second apple
> description of zombie 0
> description of zombie 1
> description of zombie 2
> your snake number (an integer from 0 to 3)
> description of snake 0
> description of snake 1
> description of snake 2
> description of snake 3

Each snake is described in the following format (note that zombies are similarly represented, but only their bodies are given):

> alive/dead length kills headX,headY bodyX,bodyY bodyX,bodyY ... tailX,tailY

To better describe what's going on here, let's look at a concerete example. Imagine that we receive the following game-state:

> 7 12
> 8 16
> 40,40 43,40 43,39
> 37,30 33,30
> 0,0 4,0
> 0
> alive 26 2 10,12 15,12 15,7 5,7 5,2
> dead 6 6 14,13 19,13
> alive 2 1 12,13 12,14
> alive 10 8 10,2 15,2 15,6 16,6

In this state, the first apple is located at position (7, 12), while the second is at (8,16). The next three lines represent the locations of the zombies. The first has its head at (40, 40) and occupies all squares from (40, 40) to (43, 40), as well as all squares from (43, 40) to (43, 39), while the second has its head at (37, 30) and occupies squares between (37, 30) and (33, 30). The final zombie has its head at (0, 0) and occupies squares between (0, 0) and (4, 0).

The next line gives the index of our snake. In this case, we're snake 0, so we're the first one in the next four lines. If we were the last snake, we'd get an index of 3. The next four lines describe each snake in the game. The first word of each line is either "alive" or "dead". Dead snakes are not displayed on the game board, and so they should be ignored. Next comes the snake's current length, followed by the number of other snakes it has killed.

Lastly the snake's coordinate chain is given. The coordinate chain is made up of (x,y) coordinates representing points of interest in the snake's body. The first coordinate is the snake's head, the next represents the first kink in the snake. There can be any number of kinks in the snake, all of which are all listed in order. Finally, the last coordinate represents the tail of the snake. As an example, the 3rd snake has the following description:

> alive 2 1 12,13 12,14

This snake is alive, has length 2, and 1 kill. Its head is at position (12, 13) and its tail is at (12, 14). From this we can deduce that the snake is traveling upwards, since the y-coordinate of its head is less than its tail's.



#### 2.2.3	Making a Move

Once the game-state has been read in, your agent should use that information to decide on its next move. A move is simply made by printing out an integer in the range 0-6. The available moves are as follows:

> 0	Up (relative to the play area - north)
> 1	Down (relative to the play area - south)
> 2	Left (relative to the play area - west)
> 3	Right (relative to the play area - east)
> 4	Left (relative to the head of your snake)
> 5	Straight (relative to the head of your snake)
> 6	Right (relative to the head of your snake)

Note that if you output a move that is opposite to the direction you're currently headed, you will simply continue straight.



#### 2.2.4	Logging

In order to enable some form of logging, the game creates two files per agent, located in the same directory as the Java or Python program. This is especially useful for Python agents, as they have no other method of debugging. The first file is an error file which logs all runtime errors triggered by the code, while the second is a log file which allows your program to save output. To write to the log file, simply prepend the word "log" and a space to your print statements. For example, if you output the string "log message", "message" will be appended to the end of the log file. Anything beginning with "log " will not be treated as a game move.



#### 2.2.5	Game Over

When the game has been concluded, instead of a normal game-state, a single line containing the words "Game Over" will be sent to each agent. This gives you the opportunity to do some last minute cleanup, saving data to files, etc. before you are shut down. If you do not exit after 500 milliseconds, you will be forcibly shut down.



### 2.3	Scoring

Throughout each game, the longest length achieved by each snake is recorded. Snakes are ranked based on their longest length, with ties broken by kill count. If the number of kills is also equal, then the snake with the higher index takes the win.



### 2.4	Creating a Java Agent

If you're writing your agent in Java, you're in luck. We've made it extremely easy to integrate your agent into the game engine from your IDE. This gives you the ability to debug your code, and allows you to quickly iterate on designs.

First download the library from our downloads page. Then create a new Java project in your IDE of choice. Under the project's properties, find a setting for libraries, and add the JAR you just downloaded as a compile-time library. Still in the properties, find the option that specifies the arguments that are passed to the program's main method. Add the following entry to the arguments:  `-develop`. **Note that this**
**must always be the first entry in the arguments**. You can also add arguments as described in section 1.2 earlier. Now you're ready to create your agent. Simply have your class inherit from DevelopmentAgent. Your class must override the `run()` method, which is where you'll put your agent logic. To illustrate, here's a sample agent that makes random moves:

```java
import java.io.BufferedReader; 
import java.io.IOException;
import java.io.InputStreamReader; 
import java.util.Random;

public class MyAgent extends DevelopmentAgent {

	public static void main(String args[]) throw s IOException { 
     	MyAgent agent = new MyAgent(); 
      	MyAgent.start(agent, args);
	}
  
	@Override 
  	public void run() {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) { 				String initString = br.readLine();
			String[] temp = initString.split(" ");
			int nSnakes = Integer.parseInt(temp[0]);
             while (true) {
				String line = br.readLine();
				if (line.contains("Game Over")) { 
                      break;
				}
                  String apple1 = line;
                  String apple2 = br.readLine(); 
               	  //do stuff with apples
               	  for (int i = 0; i < 3; i++) {
               	      String zombie = br.readLine();
               	  }
                  int mySnakeNum = Integer.parseInt(br.readLine());
                  for (int i = 0; i < nSnakes; i++) { 
                      String snakeLine = br.readLine(); 
                      if (i == mySnakeNum) {
						//hey! That's me :)
					}
					//do stuff with snakes
				 }
				 //finished reading, calculate move: 
               	  System.out.println("log calculating..."); 
                  int move = new Random().nextInt(4); 
                  System.out.println(move);
			}
		} catch (IOException e) {
          	e.printStackTrace();
		}
	}
}
```



### 2.5	Creating a Python Agent

Below is a sample agent written in Python to get you started. It reads in the input, and outputs a random move.

```python
import random

line = raw _input()
split = line.split(" ")
numSnakes = int(split[0])

while (True): #forever
    line = raw _input()
	if "Game Over" in line:
        break
	apple1 = line
	apple2 = raw _input()
    for i in range(3):
		zombie = raw _input()
    mySnakeNum = int(raw _input())
    for i in range(numSnakes):
		line = raw _input()
		if (i == mySnakeNum): 
            #hey! That's me :)
		#do stuff w ith snake    
	#finished reading, calculate move:
    print "log calculating..."
    move = random.randint(0,4)
    print move
```



### 2.6	The Config File

**Unless you feel very strongly about it, feel free to skip this section.**
The various parameters of the game can be set through a configuration file. If you want to use non-default parameters, create a text file with a name containing the phrase "snake_config" in the directory (or subdirectory) from which the game is run. Note that if multiple configuration files are found, they will all be applied, but we make no guarantee of the order in which this will occur. An example of a configuration file, as well as all available parameters and their default values, are given below.

```
# snake_config.txt
# Comments are allowed in the file, as are blank lines

# Add double the number of snakes to the board
num_snakes	8

# Make the bord bigger
game_width	75
game_height	75
```

| Key         | Default Value | Description                              |
| ----------- | ------------- | ---------------------------------------- |
| game_width  | 50            | The width of the board                   |
| game_height | 50            | The height of the board                  |
| duration    | 300           | The length of a single round (in seconds) |
| speed       | 50            | The amount of time each agent is given to calculate a move (in milliseconds) |
| num_snakes  | 4             | The number of snakes that contest a single round. The game supports any number of snakes greater than 1 |
| num_zombies | 3             | The number of zombies on the board       |
| random_seed | null          | This value sets the random number generator's seed, which allows for repeatable games. Any string value (including the string "null") can be used to set this configuration |



## FAQ##

- Why does my snake never appear on the board?

  Your program has most likely crashed before it was able to make a single move. Check your error logs to determing the reason for its abrupt exit.


- Why does my snake disappear from the board?

  Your program was most likely functioning correctly for a period of time, but received some input that caused it to exit abruptly. Check your error logs.


- Why does my snake sometimes/always go straight?

  If your agent unintentionally continues straight for an extended period of time, it signifies that the game engine is not receiving a move from your program within the required timeframe. Check that you are not doing too much computation between the time that you receive the state and the time that you output a move. Also check that when you print out a move, you also output a newline character (both CR+LF and LF are permissible).


- I'd like to submit a really awesome agent, but I'm not sure where to start. Any hints?

  The game is designed to require real-time decision making. You'll therefore want to look at lightweight, speedy techniques, which rules out a lot of the more advanced AI approaches. Solutions to the shortest path problem are where you'll want to start looking, and maybe look at some cool heuristics. The rest is up to you. Good luck, and happy coding.



## Acknowledgements

The game engine and much of the documentation (contents of this file) was created by Steve James of the University of the Witwatersrand
