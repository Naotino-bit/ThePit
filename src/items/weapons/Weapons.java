package items.weapons;

import items.Items;

public class Weapons extends Items {


    public void getInfo() {
        System.out.println("----- Info " + this.name + " -----\nRarità: " + this.rarity + "\nDanno: " + this.damage);
    }
}
