package characters.player;

import characters.Character;
import items.Items;
import items.weapons.Dagger;

public class Assassin extends Character {
    public Assassin() {
        name = "Assassino";
        baseHpMax = 100;
        baseStrength = 50;
        baseAgility = 81; //primaria
        baseIntelligence = 62;
        basePrecision = 62; //secondaria

        updateStats();
        this.totalHp = this.totalHpMax;
    }

    @Override
    public void equip(Items item) {
        if(!inInventory(item)) {
            System.out.println("Non hai questo oggetto nell'inventario");
            return;
        }
        inventory.remove(item);
        if(item instanceof Dagger){
            equippedItems.replace("Primaria", item);
            equippedItems.replace("Secondaria", item);
        } else {
            super.equip(item);
        }
        updateStats();
    }
}
