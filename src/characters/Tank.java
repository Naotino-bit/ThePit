package characters;

import items.Items;

public class Tank extends Character{
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
        String classe = item.getClass().toString();
        if(classe.contains("Claymore")){
            equippedItems.replace("Primaria", item);
            equippedItems.replace("Secondaria", item);
        } else {
            System.out.println("Non puoi equipaggiare " + item.getName());
        }
    }
}
