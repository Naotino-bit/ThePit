package characters.player;

import characters.Character;
import items.Items;
import items.weapons.Staff;

public class Mage extends Character {
    public Mage() {
        name = "Mago";
        baseHpMax = 75;
        baseStrength = 30;
        baseAgility = 30;
        baseIntelligence = 115; //primaria
        basePrecision = 80; //secondaria

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
        if(item instanceof Staff){
            equippedItems.replace("Primaria", item);
            equippedItems.replace("Secondaria", item);
        } else {
            super.equip(item);
        }
        updateStats();
    }
}
