package characters.enemies;

import characters.Character;
import items.Items;
import items.weapons.Shield;
import items.weapons.Sword;
import java.util.Random;
import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;

public class Zombie extends Enemies {
    public Zombie() {
        this.name = "Zombie";
        this.hpMax = 10; //*livello dello zombie
        this.hp = hpMax;

        possibleDrops.add(new Sword("Spada di legno", "Comune", 10, ""));
        possibleDrops.add(new Shield("Scudo di legno", "Comune", 10, ""));
    }

    public Items getDrop () {
        Random rand = new Random();
        int randomInt = rand.nextInt(2); // Numero intero da 0 a 99 [6, 15]
        return possibleDrops.get(randomInt);
    }

    //metodo per morte del npc
}
