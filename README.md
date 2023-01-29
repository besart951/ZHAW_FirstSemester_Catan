# team02-TheInformant-catan

## Introduction

This project is the third of the module "Software Project 1". This team studies computer science at the Zurich University of Applied Sciences. In this module we apply the acquired theoretical knowledge from the lectures. We learn the skills of team roles, organizing meetings and writing protocols. The project is a console-based version of the board game Catan which can be played with 2-4 players.

In the following section you can see what we have developed together.



## Team

* [Michel Fäh](https://github.zhaw.ch/faehmic2)
* [Besart Morina](https://github.zhaw.ch/morinbe3)
* [Louie Wolf](https://github.zhaw.ch/wolflou1)
* [Nico Zwahlen](https://github.zhaw.ch/zwahlni2)

## Manual

* [Spielanleitung](anleitung/catan_spielregel_DE.pdf)
* [Instructions](anleitung/catan_game_rules_EN.pdf)

The game does not have a standalone version and can be started by running the main method in the Java file: `src/ch/zhaw/catan/Program.java`

The game consists of different phases:

| Phase          | Description                                         |
|----------------| --------------------------------------------------- |
| Configuration   | Sets the number of players and the required win points.
| Initial | Every player is allowed to set two settlements as well as two roads in ascending and descending order.
| Roll dice | Resources are given to all players who have a settlement at the rolled resource field.
| Build and Trade | Gives the current player the option to build or trade.
| End Phase | Gives players the option to display their points or quit the game. If there is a winner, it will be displayed.


#### Configuration Phase
Players are called to specify the number of win points they need and the number of players. Then the board is set up and the initial phase starts.

#### Initial Phase
The players are allowed to place 2 times in ascending and descending order one settlement and one road each. In doing so, the locations must meet the criteria of the rules. 

#### Playing Phases
The game alternates between the Dice Roll Phase and the Build and Trade Phase until a player wins or the program ends. In the Dice Roll Phase, the player whose turn it is rolls the dice and resources are distributed to all players who have a settlement or city on the dice rolled. In the Build and Trade phase, the player whose turn it is can build structures if he has enough resources or trade a resource 1:4 with the bank. If a player has enough points to win or the players want to end the game then the game changes to the end phase.

#### End Phase
If a player has won, this is displayed. Players have the option to display their victory points and exit the program.


##### Symbols on field
| Symbol | Meaning                                            |
|--------| --------------------------------------------------- |
| GR     | GRAIN
| WL     | WOOL
| LU     | WOOD
| OR     | ORE
| BR     | BRICKS
| TH     | THIEF


##### Structuress
| Structure         | Cost                                                |
| ----------------- | --------------------------------------------------- |
| Settlement        | WOOD, BRICKS, WOOL, GRAIN
| Road              | WOOD, BRICKS
| City              | ORE (3), GRAIN (2)

##### Win Points
| Type                  | Points                                                |
| --------------------- | --------------------------------------------------- |
| Settlement            | 1
| City                  | 2

# Class Diagram

![Class Diagram](class_diagram/klassendiagramm.svg)
