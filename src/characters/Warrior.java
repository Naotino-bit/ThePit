package characters;

import items.Items;

public class Warrior extends Character{
    public Warrior(){
        name = "Guerriero";
        hpMax = 120;
        hp = hpMax;
        strength = 90; //primaria
        agility = 85; //secondaria
        intelligence = 30;
        precision = 50;
    }

    @Override
    public void equip(Items item) {
        String classe = item.getClass().toString();
        if(classe.contains("Sword")){
            equippedItems.replace("Primaria", item);
        } else if (classe.contains("Shield")) {
            equippedItems.replace("Secondaria", item);
        } else {
            System.out.println("Non puoi equipaggiare " + item.getName());
        }
    }
}
