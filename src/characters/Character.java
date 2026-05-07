//TODO implementare Leveling (TEORICAMENTE FINITA LA LOGICA MA BISOGNA IMPLENTARE NEL GAMEPLAY)
// gestire tutti gli effetti secondari delle armi, armature ecc



package characters;

import items.Items;
import items.armors.Armors;
import items.artefacts.Artefacts;
import items.usables.Usables;
import items.weapons.*;

import java.util.*;

public abstract class Character {
    protected String name;  //classe

    //statistiche BASE
    protected int baseHpMax;
    protected int baseStrength; //Danno personaggio
    protected int baseAgility; //Precedenza turno, probabilità schivata
    protected int baseIntelligence; //Quantità, velocità recupero mana
    protected int basePrecision; //CRIT RATE / DMG Multiplier

    //statistiche + stat oggetti
    protected int totalHpMax;
    protected int totalHp;
    protected int totalStrength; //Danno personaggio
    protected int totalAgility; //Precedenza turno, probabilità schivata
    protected int totalIntelligence; //Quantità, velocità recupero mana
    protected int totalPrecision; //CRIT RATE / DMG Multiplier
    protected int weaponDamage; //Danno aggiunto dall'arma
    protected int armorDefence; //Difesa aumantata dallo scudo

    protected int manaMax;
    protected int currentMana;
    protected int manaRegen;
    protected int hpRegen;

    protected int critRate;
    protected double critMultiplier = 1.5;

    protected int dodgeChance;    // Probabilità di schivare un colpo

    //Sistema per livellare
    protected int level = 1;
        protected int exp = 0;
    protected int expToNextLevel = 100;

    //SOLDI
    protected int money = 0;

    protected ArrayList<Items> inventory = new ArrayList<Items>();
    protected HashMap<String, Items> equippedItems = new LinkedHashMap<String, Items>();
    protected HashMap<String, Integer> activeEffects = new HashMap<>();

    protected Character lastTarget = null;
    protected int predatorStacks = 0;

    public Character(){
        equippedItems.put("Testa", null);
        equippedItems.put("Torso", null);
        equippedItems.put("Gambe", null);
        equippedItems.put("Piedi", null);
        equippedItems.put("Primaria", null);
        equippedItems.put("Secondaria", null);
        equippedItems.put("Mantello", null);
        equippedItems.put("Orecchini", null);
        equippedItems.put("Collana", null);
        equippedItems.put("Anello", null);
    }

    public abstract boolean canEquipWeapon(Items item);

    protected void updateStats(){
        //resetto il personaggio alle stats base
        this.totalHpMax = this.baseHpMax;
        this.totalStrength = this.baseStrength;
        this.totalAgility = this.baseAgility;
        this.totalIntelligence = this.baseIntelligence;
        this.totalPrecision = this.basePrecision;

        this.weaponDamage = 0;
        this.armorDefence = 0;
        this.hpRegen = 0;

        //serve a non calcolare due volte le armi a due mani
        HashSet<Items> uniqueEquipped = new HashSet<>(equippedItems.values());
        for(Items item : uniqueEquipped){
            if(item != null){

                if(item instanceof Shield || item instanceof Armors) this.armorDefence += item.getBaseStat();
                else if(item instanceof Weapons) this.weaponDamage += item.getBaseStat();
                switch(item.getBoostedStat()){
                    case "Forza":
                        this.totalStrength += item.getBoostedStatVal();
                        break;
                    case "Intelligenza":
                        this.totalIntelligence += item.getBoostedStatVal();
                        break;
                    case "Agilità":
                        this.totalAgility += item.getBoostedStatVal();
                        break;
                    case "Precisione":
                        this.totalPrecision += item.getBoostedStatVal();
                        break;
                    case "Vitalità":
                        this.totalHpMax += item.getBoostedStatVal();
                        break;
                }

                if(item instanceof Artefacts){
                    Artefacts artefact = (Artefacts) item;
                    for (Map.Entry<String, Integer> subStat : artefact.getSubStats().entrySet()){
                        String nameSubStat = subStat.getKey();
                        int valSubStat = subStat.getValue();

                        switch(nameSubStat) { //gasa lo switch scritto così
                            case "Forza":
                                this.totalStrength += valSubStat;
                                break;
                            case "Intelligenza":
                                this.totalIntelligence += valSubStat;
                                break;
                            case "Agilità":
                                this.totalAgility += valSubStat;
                                break;
                            case "Precisione":
                                this.totalPrecision += valSubStat;
                                break;
                            case "Vitalità":
                                this.totalHpMax += valSubStat;
                                break;
                        }
                    }
                }
            }
        }

        HashMap<String, Integer> setCounter = new HashMap<>();

        for(Items item : uniqueEquipped){
            if (item instanceof Artefacts) {
                Artefacts artefacts = (Artefacts) item;

                String nomeSet = artefacts.getNameOfSet();
                setCounter.put(nomeSet, setCounter.getOrDefault(nomeSet, 0) + 1);
            }
        }

        //
        for (Map.Entry<String, Integer> set : setCounter.entrySet()) {
            String nameOfSet = set.getKey();
            int equippedPieces = set.getValue();

            if (nameOfSet.equals("Crimson Seed")) {
                if (equippedPieces >= 2) this.hpRegen += 10;
                if (equippedPieces >= 4) this.hpRegen += 20;
            }
            else if (nameOfSet.equals("Crimson Amber")) {
                if (equippedPieces >= 2) this.totalHpMax += 25;
                if (equippedPieces >= 4) this.totalHpMax += 60;
            }
            else if (nameOfSet.equals("Cerulean Seed")) {
                if (equippedPieces >= 2) this.manaRegen += 10;
                if (equippedPieces >= 4) this.manaRegen += 20;
            }
            else if (nameOfSet.equals("Cerulean Amber")) {
                if (equippedPieces >= 2) this.manaMax += 25;
                if (equippedPieces >= 4) this.manaMax += 60;
            }
            else if (nameOfSet.equals("Turquoise Turtle")) {
                if (equippedPieces >= 2) this.totalAgility += 25;
                if (equippedPieces >= 4) this.totalAgility += 60;
            }
            else if (nameOfSet.equals("Emerald Amber")) {
                if (equippedPieces >= 2) this.totalAgility += (this.baseAgility * 20) / 100; //il 20% in più
                if (equippedPieces >= 4) this.totalAgility += (this.baseAgility * 45) / 100; //il 45% in più
            }
            else if (nameOfSet.equals("Sharpshot")) {
                if (equippedPieces >= 2) this.totalPrecision += 25;
                if (equippedPieces >= 4) this.totalPrecision += 60;
            }
            else if (nameOfSet.equals("Arrow Sting")) {
                if (equippedPieces >= 2) this.totalPrecision += (this.basePrecision * 20) / 100;
                if (equippedPieces >= 4) this.totalPrecision += (this.basePrecision * 45) / 100;
            }
            else if (nameOfSet.equals("Barbarian")) {
                if (equippedPieces >= 2) this.totalStrength += 25;
                if (equippedPieces >= 4) this.totalStrength += 60;
            }
            else if (nameOfSet.equals("Zoro")) {
                if (equippedPieces >= 2) this.totalStrength += (this.baseStrength * 20) / 100;
                if (equippedPieces >= 4) this.totalStrength += (this.baseStrength * 45) / 100;
            }
            else if (nameOfSet.equals("Stargazer")) {
                if (equippedPieces >= 2) this.totalIntelligence += 25;
                if (equippedPieces >= 4) this.totalIntelligence += 60;
            }
            else if (nameOfSet.equals("High Priest")) {
                if (equippedPieces >= 2) this.totalIntelligence += (this.baseIntelligence * 20) / 100;
                if (equippedPieces >= 4) this.totalIntelligence += (this.baseIntelligence * 45) / 100;
            }
            else if (nameOfSet.equals("Turtle Shell")) {
                if (equippedPieces >= 2) this.armorDefence += 25;
                if (equippedPieces >= 4) this.armorDefence += 60;
            }
            else if (nameOfSet.equals("Dragon Scale")) { //COSI E' OP VA RIVISTA QUESTA IN PARTICOLARE
                if (equippedPieces >= 2) this.armorDefence += (this.armorDefence * 20) / 100;
                if (equippedPieces >= 4) this.armorDefence += (this.armorDefence * 45) / 100;
            }
            // quelli singoli NON vanno messi ovviamente
        }

        if(this.totalHp > this.totalHpMax) {this.totalHp = this.totalHpMax;}

        updateDerivedStats();
    }

    public void updateDerivedStats(){

        if (activeEffects.containsKey("Ghiaccio")) {
            this.totalAgility /= 2;
        }

        Items mainWeapon = this.equippedItems.get("Primaria");
        if(mainWeapon != null && mainWeapon instanceof Weapons){
            String passive = ((Weapons) mainWeapon).getEffect();

            if(passive.equals("RecuperoVita")){
                this.hpRegen += 10;
            }
            else if(passive.equals("Balestra")){
                this.totalAgility -= 15;
                if (this.totalAgility < 1) this.totalAgility = 1;
            }
        }

        this.manaMax = this.totalIntelligence * 2;
        this.manaRegen = (this.totalIntelligence / 10) +1;

        if(this.currentMana > this.manaMax){
            this.currentMana = this.manaMax;
        }

        this.critRate = this.totalPrecision / 2;
        if (this.critRate > 100) this.critRate = 100;

        this.dodgeChance = this.totalAgility / 4;
        if (this.dodgeChance > 50) this.dodgeChance = 50;
    }


    public void takeDamage(int receivedDamage) {
        this.takeDamage(receivedDamage, false);
    }

    public void takeDamage(int receivedDamage, boolean ignoreArmor) {


        Items scudo = this.equippedItems.get("Secondaria");
        String shieldEffect = "Nessuno";

        if (scudo != null && scudo instanceof Weapons) {
            shieldEffect = ((Weapons) scudo).getEffect();
        }

        if (shieldEffect.equals("Immortale")) {
            Random rand = new Random();
            if (rand.nextInt(100) < 20) {
                System.out.println("Il " + scudo.getName() + " di " + this.getName() + " assorbe l'impatto! 0 Danni!");
                return;
            }
        }

        if (ignoreArmor) {
            this.totalHp -= receivedDamage;
            System.out.println("L'attacco ignora le difese! Subiti " + receivedDamage + " Danni Puri!");
        } else {
            int  actualDamage= Math.max(0, receivedDamage - this.armorDefence);
            this.totalHp -= actualDamage;
        }
    }
    protected int getBaseDamage(){
        return this.weaponDamage + this.totalStrength;
    }

    public void attack(Character target){
        this.attack(target, null);
    }
    //DEVI PASSARE ENEMY LIST SE NON LO PASSI LO CONTA NULL
    public void attack(Character target, ArrayList<? extends Character> enemyList) {
        int finalDamage = this.getBaseDamage();
        boolean isPureDamage = false;

        Items mainWeapon = this.equippedItems.get("Primaria");
        String weaponEffect = "Nessuno";


        if (mainWeapon != null && mainWeapon instanceof Weapons) {
            Weapons weapon = (Weapons) mainWeapon;
           weaponEffect = weapon.getEffect();
        }

        if(weaponEffect.equalsIgnoreCase("Puro")) {
            isPureDamage = true;
        }

        if(target.rollDodge()){
            System.out.println(target.getName() + " ha schivato l'attacco!");
            return;
        }

        int finalCritRate = this.critRate;

        if(weaponEffect.equals("Ripetizione")){
            if(this.lastTarget == target){
                this.predatorStacks++;
            } else{
                this.lastTarget = target;
                this.predatorStacks = 1;
            }

            finalCritRate += (this.predatorStacks * 25);
        }

        boolean isCrit = this.rollCrit(finalCritRate);
        if(isCrit){
            System.out.println(this.getName() + " ha fatto un colpo critico!");
            finalDamage = (int)(finalDamage * this.critMultiplier);
        }

        target.takeDamage(finalDamage, isPureDamage);

        if (weaponEffect.equals("Veleno")) {
            target.applyEffect("Veleno", 3);
        }
        else if (weaponEffect.equals("Fuoco")) {
            target.applyEffect("Fuoco", 2);
        }
        else if (weaponEffect.equals("Ghiaccio")) {
            target.applyEffect("Ghiaccio", 2);
        }
        else if (weaponEffect.equals("DannoArea") && enemyList != null){
            int splashDamage = Math.max(1, (int) (finalDamage * 0.25));
            System.out.println("Attacco ad area!");
            for (Character enemy : enemyList) {
                if(enemy != target && !enemy.isDead()){
                    enemy.takeDamage(splashDamage, false);
                }
            }
        }

        if(isCrit){
            Items collana = this.equippedItems.get("Collana");
            if(collana != null && collana.getName().equals("Assassin's Crimson Dagger")){
                int lifesteal = (int)(finalDamage * 0.30);
                this.setHp(this.totalHp + lifesteal);
                System.out.println(this.getName() + " ha recuperato " + lifesteal + "HP!");
            }
        }


    }

    public void applyEffect(String effectName, int turns){
        int currentTurns = activeEffects.getOrDefault(effectName, 0);
        activeEffects.put(effectName, Math.max(currentTurns, turns));
        System.out.println(this.getName() + " subisce l'effetto: [" + effectName.toUpperCase() +  "] per +" + turns + " turni!" );
        updateStats();
    }

    public boolean isDead() {
        return this.totalHp <= 0;
    }

    //SETTERS
    public void setHp(int hp) {
        this.totalHp = hp;
        if (this.totalHp > this.totalHpMax) this.totalHp = this.totalHpMax;
    }
    public void setCurrentMana(int amount){
        this.currentMana = amount;
        if(this.currentMana > this.manaMax) this.currentMana = this.manaMax;
        if(this.currentMana < 0) this.currentMana = 0;
    }

    //GETTERS
    public String getName() {return name;}
    public int getHpMax(){return totalHpMax;}
    public int getHp(){return  totalHp;}
    public String getEquippedItems() {
        String temp ="\n----- Equipaggiamento -----\n";
        for( String i : equippedItems.keySet()) {
            try {
                temp += i + ": " + equippedItems.get(i).getName() + "\n";
            } catch (NullPointerException e) {
                temp += i + ": " + "Non equipaggiato\n";
            }
            if(i.equals("Piedi") || i.equals("Secondaria")) {
                temp += "/\n";
            }
        }
        return temp;
    }

    public HashMap<String, Items> getEquippedItemsRaw() {
        return equippedItems;
    }

    public ArrayList<Items> getInventory(){
        return inventory;
    }
    public ArrayList<Usables> getInventoryUsables(){
        ArrayList<Usables> inventoryUsable = new ArrayList<>();
        for(Items item: inventory) {
            if(item instanceof Usables) {
                inventoryUsable.add((Usables) item);
            }
        }
        return inventoryUsable;
    }
    public int getLevel() {return level;}
    public int getMoney(){ return money;}
    public int getCurrentMana(){ return this.currentMana; }
    public int getManaMax(){ return this.manaMax; }
    public int getManaRegen(){ return this.manaRegen; }
    public double getCritMultiplier(){ return this.critMultiplier; }
    public HashMap<String, Integer> getStats() {
        HashMap<String, Integer> stats = new HashMap<>();
        stats.put("HpMax", totalHpMax);
        stats.put("Strength" , totalStrength);
        stats.put("Agility", totalAgility);
        stats.put("Intelligence", totalIntelligence);
        stats.put("Precision", totalPrecision);
        return stats;
    }
    public HashMap<String, Integer> getBaseStats() {
        HashMap<String, Integer> stats = new HashMap<>();
        stats.put("HpMax", baseHpMax);
        stats.put("Strength" , baseStrength);
        stats.put("Agility", baseAgility);
        stats.put("Intelligence", baseIntelligence);
        stats.put("Precision", basePrecision);
        return stats;
    }
/*
    //STAMPA DEL PERSONAGGIO
    public void presentation(){
        System.out.println("\n-----STATS-----\n" +
                "Classe: " + name + "" +
                "\nPunti vita: " + totalHp + "/" + totalHpMax +
                "\nForza: " + totalStrength +
                "\nAgilità: " + totalAgility +
                "\nIntelligenza: " + totalIntelligence +
                "\nPrecisione: " + totalPrecision);
    }
*/

    public boolean addToInventory (Items item) {
        if(inventoryFull()) {
            return false;
        }
        inventory.add(item);
        return true;
    }

    public void removeFromInventory (Items item){
        inventory.remove(item);
    }

    public boolean inventoryFull() {return inventory.size() >= 20;}

    public boolean inInventory(Items item){return inventory.contains(item);}

    public void equip(Items item){
        if(!inInventory(item)){
            System.out.println("Non hai questo oggetto nell'inventario");
            return;
        }

        if(item instanceof Weapons){
            if(!canEquipWeapon(item)){
                System.out.println("Non puoi usare: "+ item.getName());
                return;
            }

            inventory.remove(item);
            equipWeaponLogic(item);
        }

        else if (item instanceof Armors || item instanceof Artefacts) {
            inventory.remove(item);

            if(equippedItems.get(item.getEquippedSlot().getFirst()) != null){
                inventory.add(equippedItems.get(item.getEquippedSlot().getFirst()));
            }

            equippedItems.replace(item.getEquippedSlot().getFirst(), item);
            System.out.println("Hai equipaggiato: " + item.getName());
        }

        updateStats();
    }

    private void equipWeaponLogic(Items item){
        Items primaria = equippedItems.get("Primaria");
        Items secondaria = equippedItems.get("Secondaria");

        boolean isTwoHanded = (item instanceof Bow || item instanceof Staff || item instanceof Claymore);
        if(isTwoHanded){
            if(primaria != null){ inventory.add(primaria); }
            if(secondaria != null && secondaria != primaria){ inventory.add(secondaria);}

            equippedItems.replace("Primaria", item);
            equippedItems.replace("Secondaria", item);

            System.out.println("Hai equipaggiato " + item.getName() + " a due mani");
        }

        else if(item instanceof Dagger){
            if(primaria == null){
                equippedItems.replace("Primaria", item);
            }
            else if (secondaria == null) {
                if(primaria == secondaria){
                    inventory.add(primaria);
                    equippedItems.replace("Primaria", item);
                    equippedItems.replace("Secondaria", null);
                }
                else{
                    equippedItems.replace("Secondaria", item);
                }
            }
            else{
                inventory.add(primaria);
                equippedItems.replace("Primaria", item);
            }
            System.out.println("Hai equipaggiato " + item.getName());
        }
        else if(item instanceof Shield) {
            if(secondaria != null && primaria != secondaria) inventory.add(secondaria);

            if(primaria != null && primaria == secondaria){
                inventory.add(primaria);
                equippedItems.replace("Primaria", null);
            }
            equippedItems.replace("Secondaria", item);
            System.out.println("Hai equipaggiato " + item.getName());
        }
        else if (item instanceof Sword) {
            if(primaria != null && primaria != secondaria) inventory.add(primaria);
            if(primaria != null && primaria == secondaria){
                inventory.add(primaria);
                equippedItems.replace("Secondaria", null);
            }
            equippedItems.replace("Primaria", item);
            System.out.println("Hai equipaggiato " + item.getName());
        }
    }

    public void deEquip(String target){
        Items itemToDeEquip = equippedItems.get(target);
        if(itemToDeEquip != null){
            inventory.add(itemToDeEquip);

            if(target.equals("Primaria") && equippedItems.get("Secondaria") == itemToDeEquip){
                equippedItems.replace("Secondaria", null);
            }
            else if (target.equals("Secondaria") && equippedItems.get("Primaria") == itemToDeEquip ){
                equippedItems.replace("Primaria", null);
            }

            equippedItems.replace(target, null);
            System.out.println("Hai rimosso: " + itemToDeEquip.getName());

            updateStats();
        }
        else{
            System.out.println("Lo slot " + target + " è già vuoto");
        }
    }

    //TODO IMPLEMENTA GAIN EXP
    public String gainExp(int amount){
        this.exp += amount;
        String log = "Hai ottenuto " + amount + " EXP.\n";
        while(this.exp >= this.expToNextLevel){
            log += levelUp();
        }
        return log;
    }
    protected abstract void applyLevelUpStats();
    public String levelUp(){
        this.exp -= this.expToNextLevel;
        this.level++;
        this.expToNextLevel = (int) (this.expToNextLevel * 1.5);

        int tempHpMax = this.totalHpMax;

        //AUMENTI ALLE STATS BASE
        applyLevelUpStats();
        updateStats();

        int hpDifference = this.totalHpMax - tempHpMax;
        this.totalHp += hpDifference;
        return "Sei salito al livello " + this.level + "!\n";
     }

     //TODO IMPLEMENTA GAIN MONEY
    public void gainMoney(int amount){
        this.money += amount;
    }

    //Da implementare in un futuro NEGOZIO
    public boolean spendMoney(int amount) {
        if (this.money >= amount) {
            this.money -= amount;
            return true;
        }
        System.out.println("Non hai abbastanza soldi! Te ne servono " + amount + " ma ne hai solo " + this.money);
        return false;
    }

    //TODO IMPLEMENTA END TURN
    public void endTurn(){
        this.setCurrentMana(this.currentMana + this.manaRegen);

        if(this.hpRegen > 0){
            this.setHp(this.totalHp + this.hpRegen);
        }

        if(!activeEffects.isEmpty()){
            ArrayList<String> toRemove = new ArrayList<>();

            for(Map.Entry<String, Integer> entry : activeEffects.entrySet()){
                String effect = entry.getKey();
                int turnsLeft = entry.getValue();

                if(effect.equals("Veleno")){
                    int poisonDamage = Math.max(1, (int) (this.totalHpMax * 0.05));
                    this.totalHp -= poisonDamage;
                    System.out.println(this.getName() + " subisce " + poisonDamage + " danno da veleno");
                }
                else if(effect.equals("Fuoco")){
                    this.totalHp -= 15;
                    System.out.println(this.getName() + " subisce  15 danno da scottatura");
                }

                turnsLeft--;
                if (turnsLeft <= 0) toRemove.add(effect);
                else activeEffects.put(effect, turnsLeft);
            }
            for (String effect : toRemove) {
                activeEffects.remove(effect);
                System.out.println("L'effetto [" + effect.toUpperCase() + "] su " + this.getName() + " è svanito.");
            }
            if (!toRemove.isEmpty()) updateStats();

        }

    }

    public boolean rollCrit(int rate){
        Random rand = new Random();
        return (rand.nextInt(100) + 1) <= rate;
    }
    public boolean rollCrit(){
        return this.rollCrit(this.critRate);
    }
    public boolean rollDodge(){
        Random rand = new Random();
        return (rand.nextInt(100)+1) <= this.dodgeChance;
    }

    public void handleDeath() {
        //gestiamo il respawn, per ora ritorna a maxhp e perde parte dell'inventario
        setHp(totalHpMax);
        Random rand = new Random();

        ArrayList<Items> totalPlayerItem = new ArrayList<>();
        totalPlayerItem.addAll(inventory);
        totalPlayerItem.addAll(getEquippedItemsRaw().values());
        totalPlayerItem.removeAll(Collections.singleton(null));//togliamo tutti i null
        System.out.println(totalPlayerItem);
        int roll = rand.nextInt(totalPlayerItem.size());
        if(roll>3) roll = 3;//massimo 3 item da far perdere //TODO scaling in base a difficolta
        System.out.println("ITEMS DA DROPPARE :" + roll);

        try {
            for(int i = 0; i<roll; i++){
                roll = rand.nextInt(totalPlayerItem.size()); //un item a caso da levare
                Items itemToDrop = totalPlayerItem.get(roll);
                System.out.println("DOVREBBE DROPPARE: " + itemToDrop);
                if (inventory.contains(itemToDrop)){
                    removeFromInventory(itemToDrop);
                    System.out.println("BUTTATO CAUSA MORTE: " + itemToDrop);
                } else {
                    ArrayList<String> itemsEquippedKey = new ArrayList<>(getEquippedItemsRaw().keySet());
                    ArrayList<Items> itemsEquippedValue = new ArrayList<>(getEquippedItemsRaw().values());
                    for(int j = 0; j<itemsEquippedKey.size(); j++){
                        if(itemsEquippedValue.get(j) != null) {
                            if(itemsEquippedValue.get(j).equals(itemToDrop)){
                                deEquip(itemsEquippedKey.get(j));
                                removeFromInventory(itemToDrop);
                                System.out.println("BUTTATO CAUSA MORTE (DALL'EQUIPAGGIAMENTO): " + itemToDrop);
                            }
                        }
                    }
                }

            }
        } catch (Exception e) {
            System.out.println("Errore durante la perdità oggetti causa morte");
        }
    }
}

