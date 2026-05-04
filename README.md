![alt text] (https://github.com/Naotino-bit/ThePit/blob/master/ThePit.png)
<!-- [![Ask DeepWiki](https://devin.ai/assets/askdeepwiki.png)](https://deepwiki.com/Naotino-bit/ThePit) -->



ThePit is a text-based, turn-based RPG developed in Java. It utilizes a client-server architecture where the server manages game logic, and players connect via a client to interact with the world. The game features a classic RPG structure with character classes, item management, and a strategic combat system.

## Features

*   **Class-Based Characters**: Choose from five distinct classes: Warrior, Tank, Archer, Mage, and Assassin, each with unique base statistics for strength, agility, intelligence, and precision.
*   **Turn-Based Combat**: Engage in strategic battles against enemies like Zombies. The `BattleManager` handles the flow of combat, processing player and enemy turns.
*   **Extensive Item System**: Equip your character with a wide variety of items to boost your stats. The system includes:
    *   **Weapons**: Swords, Bows, Staffs, Daggers, Claymores, and Shields.
    *   **Armor**: Helmets, Chestplates, Leggings, and Boots.
    *   **Artifacts**: Cloaks, Earrings, Necklaces, and Rings.
*   **Inventory and Equipment**: Collect items in your inventory and equip them to their designated slots (e.g., "Primaria", "Torso", "Anello") to gain their benefits. Stats are dynamically updated based on equipped gear.
*   **Client-Server Architecture**: The game runs on a simple Java socket-based server (`GameServer`) that handles game state and logic. Players connect and send commands through a corresponding `GameClient`.

## Project Structure

The project is organized into several key packages within the `src` directory:

*   `characters`: Contains the abstract `Character` class and sub-packages for player classes (`player`) and enemy types (`enemies`).
*   `items`: Defines the base `Items` class and specific categories for `weapons`, `armors`, and `artefacts`.
*   `game`: Includes the core game logic, with `Game.java` acting as the main controller and `BattleManager.java` handling combat encounters.
*   `client`: Contains the `GameClient.java` class, which connects to the server and allows the user to send commands.
*   `server`: Contains the `GameServer.java` class, which listens for client connections and processes game commands.

## How to Run

To run the game, you need a Java Development Kit (JDK) installed.

### 1. Start the Server

First, compile and run the `GameServer`. It will start listening for connections on port `8080`.

```bash
# Navigate to the src directory
cd src

# Compile the server and game files
javac server/GameServer.java game/*.java characters/**/*.java items/**/*.java

# Run the server
java server.GameServer
```
You will see the message: `Server in ascolto sulla porta 8080...`

### 2. Start the Client

In a **separate terminal window**, compile and run the `GameClient`.

```bash
# Navigate to the src directory
cd src

# Compile the client file
javac client/GameClient.java

# Run the client
java client.GameClient
```
The client will connect to the server, and you can start playing by entering commands in the client's terminal.

## Gameplay Commands

The game is controlled via text commands entered into the client.

*   `spawn zombie`: Initiates a battle with a Zombie.
*   `ATTACCA [target_index]`: Attacks an enemy during combat. For now, use `ATTACCA 0`.
*   `INVENTARIO`: Displays the items currently in your inventory.
*   `ESCI`: Disconnects from the server and closes the client.
