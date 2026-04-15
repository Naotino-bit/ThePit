package characters;

import items.Items;
import items.weapons.Bow;
import items.weapons.Staff;

public class Mage extends Character{
    public Mage() {
        name = "Mago";
        hpMax = 75;
        hp = hpMax;
        strength = 30;
        agility = 30;
        intelligence = 115; //primaria
        precision = 80; //secondaria
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
    }
}
