# Deadwood Game - README

## Overview
Deadwood is a text-based game where players move between rooms, take on acting roles, rehearse, act, and upgrade their rank. The game follows the official Deadwood board game mechanics and is implemented in Java.

## How to Compile and Run

### 1 Compile the Code
Open a terminal or command prompt, navigate to the directory containing the Deadwood files, and run:
```sh
javac *.java
```
This compiles all Java files in the directory.

### 2 Run the Game
Once compiled, start the game by running:
```sh
java Deadwood <numPlayers>
```
Replace `<numPlayers>` with a number between **2 and 8** (e.g., `java Deadwood 4`).

## How to Play
Once the game starts, you can type commands to interact with it.

### **Basic Commands**
| **Command**   |                        **Description**                                          |
|-------------- |---------------------------------------------------------------------------------|
| `who`         | Shows the current active playe's details (money, credits, rank, role).          |
| `where`       | Displays the current player's location.                                         |
| `move <room>` | Moves the player to an adjacent room (e.g., `move Casting Office`).             |
| `work <role>` | Assigns the player to a role in the room (if available and rank is sufficient). |
| `rehearse`    | Adds a rehearsal bonus to increase acting success rate.                         |
| `act`         | Attempts to act; success is based on a dice roll + rehearsal bonus.             |
| `upgrade <rank> <payment>` | Upgrades rank using `cash` or `credits` (e.g., `upgrade 3 cash`).  |
| `end`         | Ends the current player's turn and moves to the next player.                    |
| `quit`        | Ends the game immediately.                                                      |

## Example Game Flow
```
> who
Player 1 ($0, 0cr) Rank: 1

> where
Trailer

> move Casting Office
Moved to Casting Office.

> upgrade 3 cash
Successfully upgraded to rank 3.

> end
Now it's Player 2's turn.

> quit
Game ended.
```

## Troubleshooting
### **Common Issues and Fixes**
**1. Game doesn't start (`Error: Could not find or load main class Deadwood`)**
- Ensure you **compiled** the code with `javac *.java` before running `java Deadwood <numPlayers>`.
- Ensure you are in the correct directory where the Java files are stored.

**2. Invalid Move (`Invalid move. You can only move to adjacent rooms.`)**
- Ensure that you are only moving to **adjacent rooms** as per the game rules.
- Use `where` to check your location before moving.

**3. Acting Failed**
- If acting fails, rehearse first (`rehearse`) to increase the chance of success.

**4. Unable to Upgrade**
- Ensure you are in the **Casting Office**.
- Check if you have enough `cash` or `credits`.
- Use `upgrade <rank> <cash/credits>` correctly


