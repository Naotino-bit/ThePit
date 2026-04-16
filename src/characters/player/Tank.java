package characters.player;

import characters.Character;
import items.Items;
import items.weapons.Claymore;

public class Tank extends Character {
    public Tank() {
        name = "Tank";
        hpMax = 150;
        hp = hpMax;
        strength = 105; //primaria
        agility = 40;
        intelligence = 70; //secondaria
        precision = 40;
    }

    @Override
    public void equip(Items item) {
        if(!inInventory(item)) {
            System.out.println("Non hai questo oggetto nell'inventario");
            return;
        }
        inventory.remove(item);
        if(item instanceof Claymore){
            equippedItems.replace("Primaria", item);
            equippedItems.replace("Secondaria", item);
        } else {
            super.equip(item);
        }
    }
}
