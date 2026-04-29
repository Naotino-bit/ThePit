package characters.enemies;

import characters.Character;
import game.XmlHandler;
import items.Items;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Enemies extends Character {
    protected ArrayList<Items> possibleDrops;

    public Enemies() {

        possibleDrops = new ArrayList<Items>();
    }

    public void generateLoot(int attempts){
        for (int i = 0; i < attempts; i++){
            Items item = XmlHandler.rollRandomItem();
            if (item != null) this.possibleDrops.add(item);
        }
    }

    public ArrayList<Items> getDrops(){
        if(this.possibleDrops.isEmpty()) return new ArrayList<>();

        ArrayList<Items> lootToDrop = new ArrayList<>(this.possibleDrops);
        this.possibleDrops.clear(); //qua pulisco l'array per il prossimo mob ucciso

        return lootToDrop;
    }

    @Override
    public boolean canEquipWeapon(Items item) {return false;}
}
