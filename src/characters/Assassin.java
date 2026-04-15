package characters;

import items.Items;
import items.weapons.Bow;
import items.weapons.Claymore;
import items.weapons.Dagger;

public class Assassin extends Character{
    public Assassin() {
        name = "Assassino";
        hpMax = 100;
        hp = hpMax;
        strength = 50;
        agility = 81; //primaria
        intelligence = 62;
        precision = 62; //secondaria
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
    }
}
