package characters.enemies;

import characters.Character;
import game.XmlHandler;
import items.Items;
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


    
    public int getExpReward(){ return expReward; }

    public int generateMoneyDrop(){
        java.util.Random rand = new java.util.Random();

        return rand.nextInt((maxMoneyDrop - minMoneyDrop)+1)+ minMoneyDrop;
    }

    // Metodo per far scalare il nemico in base al livello del player
    public void setLevelAndScale(int playerLevel) {
        java.util.Random rand = new java.util.Random();

        int varianza = rand.nextInt(4) - 1; //i mostri ora hanno un livello semicasuale che varia attorno al livello del player

        //prendiamo effettivamente il livello del nostro mostro
        this.level = playerLevel + varianza;
        if (this.level < 1) this.level = 1; // Niente mostri di livello 0!

        //gem ha sostituito di scrivere i nomi così e onestamente l'ha gasata
        this.name = this.name + " (Lv." + this.level + ")";

        int levelDiff = this.level - 1;

        if (levelDiff > 0) {
            this.baseHpMax += (int) (this.baseHpMax * 0.20 * levelDiff);     // +20% HP per livello
            this.baseStrength += (int) (this.baseStrength * 0.15 * levelDiff); // +15% Forza
            this.baseAgility += (int) (this.baseAgility * 0.05 * levelDiff);   // +5% Agilità
            this.baseIntelligence += (int) (this.baseIntelligence * 0.15 * levelDiff); // +15% Intelligenza
            this.basePrecision += (int) (this.basePrecision * 0.05 * levelDiff); // +5% Precisione

            this.expReward += (int) (this.expReward * 0.25 * levelDiff);       // +25% EXP in più
            this.minMoneyDrop += (int) (this.minMoneyDrop * 0.15 * levelDiff); // +15% Soldi
            this.maxMoneyDrop += (int) (this.maxMoneyDrop * 0.15 * levelDiff);
        }

        this.updateStats();
        this.totalHp = this.totalHpMax;
        this.currentMana = this.manaMax;
    }
    public void applyBossBuff() {
        this.name = "BOSS " + this.name;
        this.baseHpMax *= 3;
        this.totalHpMax *= 3;
        this.totalHp = this.totalHpMax;
        
        this.baseStrength = (int)(this.baseStrength * 1.5);
        this.baseAgility = (int)(this.baseAgility * 1.5);
        this.baseIntelligence = (int)(this.baseIntelligence * 1.5);
        this.basePrecision = (int)(this.basePrecision * 1.5);
        
        this.expReward *= 3;
        this.minMoneyDrop *= 3;
        this.maxMoneyDrop *= 3;
        
        // Loot assicurato (generiamo 3 drop invece di 1 se possibile)
        generateLoot(3);
        
        this.updateStats();
    }
}
