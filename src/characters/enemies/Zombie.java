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
        this.baseHpMax = 35;
        this.baseAgility = 5;
        this.baseStrength = 15;
        this.baseIntelligence = 0;
        this.basePrecision = 5;
        


        //OGNI TIPO DI DROP
        this.expReward = 50; //exp droppata
        this.minMoneyDrop = 15;
        this.maxMoneyDrop = 30;


        this.generateLoot(2);



    }

}
