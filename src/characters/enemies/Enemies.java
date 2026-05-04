package characters.enemies;

import characters.Character;
import game.XmlHandler;
import items.Items;
//TODO ANCHE I MOSTRI SCALANO IN BASE AL LIVELLO DEL PLAYER
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Enemies extends Character {
    protected ArrayList<Items> possibleDrops;
    public int expReward;
    protected int minMoneyDrop;
    protected int maxMoneyDrop;

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

    @Override
    protected void applyLevelUpStats() { return; }


    //TODO IMPLEMENTARE getExpReward e generateMoneyDrop
    
    public int getExpReward(){ return expReward; }

    public int generateMoneyDrop(){
        java.util.Random rand = new java.util.Random();

        return rand.nextInt((maxMoneyDrop - minMoneyDrop)+1)+ minMoneyDrop;
    }
}
