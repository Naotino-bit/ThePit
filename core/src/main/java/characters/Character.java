
// gestire tutti gli effetti secondari delle armi, armature ecc

//TODO quando muori ritorni allo stato base. PERDI TUTTO, TUTTI I LIVELLI TUTTE LE STATS, TUTTO QUELLO CHE HAI.

package characters;

import items.Items;
import items.armors.Armors;
import items.artefacts.Artefacts;
import items.usables.Usables;
import items.weapons.*;

import java.util.*;

public abstract class Character {
    protected String name; // classe

    // statistiche BASE
    protected int baseHpMax;
    protected int baseStrength; // Danno personaggio
    protected int baseAgility; // Precedenza turno, probabilità schivata
    protected int baseIntelligence; // Quantità, velocità recupero mana
    protected int basePrecision; // CRIT RATE / DMG Multiplier

    // statistiche + stat oggetti
    protected int totalHpMax;
    protected int totalHp;
    protected int totalStrength; // Danno personaggio
    protected int totalAgility; // Precedenza turno, probabilità schivata
    protected int totalIntelligence; // Quantità, velocità recupero mana
    protected int totalPrecision; // CRIT RATE / DMG Multiplier
    protected int weaponDamage; // Danno aggiunto dall'arma
    protected int armorDefence; // Difesa aumantata dallo scudo

    protected int manaMax;
    protected int currentMana;
    protected int manaRegen;
    protected int hpRegen;

    protected int critRate;
    protected double critMultiplier = 1.5;

    protected int dodgeChance; // Probabilità di schivare un colpo

    // Sistema per livellare
    protected int level = 1;
    protected int exp = 0;
    protected int expToNextLevel = 100;

    // SOLDI
    protected int money = 0;

    protected ArrayList<Items> inventory = new ArrayList<Items>();
    protected HashMap<String, Items> equippedItems = new LinkedHashMap<String, Items>();
    protected HashMap<String, Integer> activeEffects = new HashMap<>();

    protected Character lastTarget = null;
    protected int predatorStacks = 0;

    public Character() {
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

    protected void updateStats() {
        // resetto il personaggio alle stats base
        this.totalHpMax = this.baseHpMax;
        this.totalStrength = this.baseStrength;
        this.totalAgility = this.baseAgility;
        this.totalIntelligence = this.baseIntelligence;
        this.totalPrecision = this.basePrecision;

        this.weaponDamage = 0;
        this.armorDefence = 0;
        this.hpRegen = 0;

        // serve a non calcolare due volte le armi a due mani
        HashSet<Items> uniqueEquipped = new HashSet<>(equippedItems.values());
        for (Items item : uniqueEquipped) {
            if (item != null) {

                if (item instanceof Shield || item instanceof Armors)
                    this.armorDefence += item.getBaseStat();
                else if (item instanceof Weapons)
                    this.weaponDamage += item.getBaseStat();
                switch (item.getBoostedStat()) {
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

                if (item instanceof Artefacts) {
                    Artefacts artefact = (Artefacts) item;
                    for (Map.Entry<String, Integer> subStat : artefact.getSubStats().entrySet()) {
                        String nameSubStat = subStat.getKey();
                        int valSubStat = subStat.getValue();

                        switch (nameSubStat) { // gasa lo switch scritto così
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

        for (Items item : uniqueEquipped) {
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
                if (equippedPieces >= 2)
                    this.hpRegen += 10;
                if (equippedPieces >= 4)
                    this.hpRegen += 20;
            } else if (nameOfSet.equals("Crimson Amber")) {
                if (equippedPieces >= 2)
                    this.totalHpMax += 5;
                if (equippedPieces >= 4)
                    this.totalHpMax += 15;
            } else if (nameOfSet.equals("Cerulean Seed")) {
                if (equippedPieces >= 2)
                    this.manaRegen += 2;
                if (equippedPieces >= 4)
                    this.manaRegen += 5;
            } else if (nameOfSet.equals("Cerulean Amber")) {
                if (equippedPieces >= 2)
                    this.manaMax += 5;
                if (equippedPieces >= 4)
                    this.manaMax += 15;
            } else if (nameOfSet.equals("Turquoise Turtle")) {
                if (equippedPieces >= 2)
                    this.totalAgility += 5;
                if (equippedPieces >= 4)
                    this.totalAgility += 15;
            } else if (nameOfSet.equals("Emerald Amber")) {
                if (equippedPieces >= 2)
                    this.totalAgility += (this.baseAgility * 20) / 100; // il 20% in più
                if (equippedPieces >= 4)
                    this.totalAgility += (this.baseAgility * 45) / 100; // il 45% in più
            } else if (nameOfSet.equals("Sharpshot")) {
                if (equippedPieces >= 2)
                    this.totalPrecision += 5;
                if (equippedPieces >= 4)
                    this.totalPrecision += 15;
            } else if (nameOfSet.equals("Arrow Sting")) {
                if (equippedPieces >= 2)
                    this.totalPrecision += (this.basePrecision * 20) / 100;
                if (equippedPieces >= 4)
                    this.totalPrecision += (this.basePrecision * 45) / 100;
            } else if (nameOfSet.equals("Barbarian")) {
                if (equippedPieces >= 2)
                    this.totalStrength += 5;
                if (equippedPieces >= 4)
                    this.totalStrength += 15;
            } else if (nameOfSet.equals("Zoro")) {
                if (equippedPieces >= 2)
                    this.totalStrength += (this.baseStrength * 20) / 100;
                if (equippedPieces >= 4)
                    this.totalStrength += (this.baseStrength * 45) / 100;
            } else if (nameOfSet.equals("Stargazer")) {
                if (equippedPieces >= 2)
                    this.totalIntelligence += 5;
                if (equippedPieces >= 4)
                    this.totalIntelligence += 15;
            } else if (nameOfSet.equals("High Priest")) {
                if (equippedPieces >= 2)
                    this.totalIntelligence += (this.baseIntelligence * 20) / 100;
                if (equippedPieces >= 4)
                    this.totalIntelligence += (this.baseIntelligence * 45) / 100;
            } else if (nameOfSet.equals("Turtle Shell")) {
                if (equippedPieces >= 2)
                    this.armorDefence += 5;
                if (equippedPieces >= 4)
                    this.armorDefence += 15;
            } else if (nameOfSet.equals("Dragon Scale")) { // COSI E' OP VA RIVISTA QUESTA IN PARTICOLARE
                if (equippedPieces >= 2)
                    this.armorDefence += (this.armorDefence * 20) / 100;
                if (equippedPieces >= 4)
                    this.armorDefence += (this.armorDefence * 45) / 100;
            }
            // quelli singoli NON vanno messi ovviamente
        }

        if (this.totalHp > this.totalHpMax) {
            this.totalHp = this.totalHpMax;
        }

        updateDerivedStats();
    }

    public void updateDerivedStats() {

        if (activeEffects.containsKey("Ghiaccio")) {
            this.totalAgility /= 2;
        }

        Items mainWeapon = this.equippedItems.get("Primaria");
        if (mainWeapon != null && mainWeapon instanceof Weapons) {
            String passive = ((Weapons) mainWeapon).getEffect();

            if (passive.equals("RecuperoVita")) {
                this.hpRegen += 10;
            } else if (passive.equals("Balestra")) {
                this.totalAgility -= 15;
                if (this.totalAgility < 1)
                    this.totalAgility = 1;
            }
        }

        this.manaMax = this.totalIntelligence * 5;
        this.manaRegen = (this.manaMax / 10) + 1;

        if (this.currentMana > this.manaMax) {
            this.currentMana = this.manaMax;
        }

        this.critRate = this.totalPrecision / 5;
        if (this.critRate > 100)
            this.critRate = 100;

        this.dodgeChance = this.totalAgility / 5;
        if (this.dodgeChance > 50)
            this.dodgeChance = 50;
    }

    public String takeDamage(int receivedDamage) {
        return this.takeDamage(receivedDamage, false);
    }

    public String takeDamage(int receivedDamage, boolean ignoreArmor) {
        return takeDamage(receivedDamage, ignoreArmor, false);
    }

    public String takeDamage(int receivedDamage, boolean ignoreArmor, boolean isMagic) {
        StringBuilder log = new StringBuilder();
        Items scudo = this.equippedItems.get("Secondaria");
        String shieldEffect = "Nessuno";

        if (scudo != null && scudo instanceof Weapons) {
            shieldEffect = ((Weapons) scudo).getEffect();
        }

        if (shieldEffect.equals("Immortale")) {
            Random rand = new Random();
            if (rand.nextInt(100) < 20) {
                log.append("Il ").append(scudo.getName()).append(" di ").append(this.getName())
                        .append(" assorbe l'impatto! 0 Danni!\n");
                return log.toString();
            }
        }

        if (ignoreArmor) {
            this.totalHp -= receivedDamage;
            log.append("L'attacco ignora le difese! Subiti ").append(receivedDamage).append(" Danni Puri!\n");
            log.append(this.getName()).append(" ha subito ").append(receivedDamage).append(" danni!\n");
        } else {
            int currentArmor = this.armorDefence;
            if (isMagic) {
                currentArmor = currentArmor / 2;
                log.append("L'attacco magico ignora parte dell'armatura!\n");
            }
            int actualDamage = Math.max(0, receivedDamage - currentArmor);
            this.totalHp -= actualDamage;
            log.append(this.getName()).append(" ha subito ").append(actualDamage).append(" danni!\n");
        }
        return log.toString();
    }

    protected int getBaseDamage() {
        return this.weaponDamage + this.totalStrength;
    }

    public String attack(Character target) {
        return this.physicalAttack(target, null, 1.0);
    }

    // DEVI PASSARE ENEMY LIST SE NON LO PASSI LO CONTA NULL
    public String attack(Character target, ArrayList<? extends Character> enemyList) {
        return this.physicalAttack(target, enemyList, 1.0);
    }

    public String physicalAttack(Character target, ArrayList<? extends Character> enemyList, double damageModifier) {
        StringBuilder log = new StringBuilder();
        int finalDamage = (int) (this.getBaseDamage() * damageModifier);
        boolean isPureDamage = false;

        Items mainWeapon = this.equippedItems.get("Primaria");
        String weaponEffect = "Nessuno";

        if (mainWeapon != null && mainWeapon instanceof Weapons) {
            Weapons weapon = (Weapons) mainWeapon;
            weaponEffect = weapon.getEffect();
        }

        if (weaponEffect.equalsIgnoreCase("Puro")) {
            isPureDamage = true;
        }

        if (target.rollDodge()) {
            log.append(target.getName()).append(" ha schivato l'attacco fisico!\n");
            return log.toString();
        }

        int finalCritRate = this.critRate;
        if (damageModifier >= 2.0)
            finalCritRate -= 10; // Penalità critico attacco pesante

        if (weaponEffect.equals("Ripetizione")) {
            if (this.lastTarget == target) {
                this.predatorStacks++;
            } else {
                this.lastTarget = target;
                this.predatorStacks = 1;
            }

            finalCritRate += (this.predatorStacks * 25);
        }

        boolean isCrit = this.rollCrit(finalCritRate);
        if (isCrit) {
            log.append(this.getName()).append(" ha fatto un colpo critico!\n");
            finalDamage = (int) (finalDamage * this.critMultiplier);
        }

        log.append(target.takeDamage(finalDamage, isPureDamage, false));

        if (weaponEffect.equals("Veleno")) {
            log.append(target.applyEffect("Veleno", 3));
        } else if (weaponEffect.equals("Fuoco")) {
            log.append(target.applyEffect("Fuoco", 2));
        } else if (weaponEffect.equals("Ghiaccio")) {
            log.append(target.applyEffect("Ghiaccio", 2));
        } else if (weaponEffect.equals("DannoArea") && enemyList != null) {
            int splashDamage = Math.max(1, (int) (finalDamage * 0.25));
            log.append("Attacco ad area!\n");
            for (Character enemy : enemyList) {
                if (enemy != target && !enemy.isDead()) {
                    log.append(enemy.takeDamage(splashDamage, false, false));
                }
            }
        }

        if (isCrit) {
            Items collana = this.equippedItems.get("Collana");
            if (collana != null && collana.getName().equals("Assassin's Crimson Dagger")) {
                int lifesteal = (int) (finalDamage * 0.30);
                this.setHp(this.totalHp + lifesteal);
                log.append(this.getName()).append(" ha recuperato ").append(lifesteal).append("HP!\n");
            }
        }

        return log.toString();
    }

    public String magicalAttack(Character target, ArrayList<? extends Character> enemyList, int magicType) {
        StringBuilder log = new StringBuilder();
        int manaCost = 0;
        if (magicType == 1)
            manaCost = 5;
        else if (magicType == 2)
            manaCost = 15;
        else if (magicType == 3)
            manaCost = 10;

        if (this.currentMana < manaCost) {
            log.append(this.getName()).append(" prova a lanciare una magia ma non ha abbastanza Mana!\n");
            return log.toString();
        }

        this.currentMana -= manaCost;

        if (magicType == 3) {
            int healAmount = this.totalIntelligence * 2;
            this.setHp(this.totalHp + healAmount);
            log.append(this.getName()).append(" si cura di ").append(healAmount).append(" HP usando la magia!\n");
            return log.toString();
        }

        if (target.rollDodge()) {
            log.append(target.getName()).append(" ha schivato la magia!\n");
            return log.toString();
        }

        int magicDamage = this.weaponDamage;
        if (magicType == 1) {
            magicDamage += (int) (this.totalIntelligence * 1.5);
            log.append(this.getName()).append(" lancia un Dardo Magico!\n");
        } else if (magicType == 2) {
            magicDamage += (int) (this.totalIntelligence * 3.0);
            log.append(this.getName()).append(" lancia un'Esplosione Arcana!\n");
        }

        boolean isCrit = this.rollCrit(this.critRate);
        if (isCrit) {
            log.append(this.getName()).append(" magia critica!\n");
            magicDamage = (int) (magicDamage * this.critMultiplier);
        }

        log.append(target.takeDamage(magicDamage, false, true));

        if (magicType == 2 && enemyList != null) {
            int splashDamage = Math.max(1, (int) (magicDamage * 0.25));
            log.append("Danno ad area dall'Esplosione!\n");
            for (Character enemy : enemyList) {
                if (enemy != target && !enemy.isDead()) {
                    log.append(enemy.takeDamage(splashDamage, false, true));
                }
            }
        }
        return log.toString();
    }

    public String applyEffect(String effectName, int turns) {
        int currentTurns = activeEffects.getOrDefault(effectName, 0);
        activeEffects.put(effectName, Math.max(currentTurns, turns));
        return this.getName() + " è ora affetto da " + effectName.toUpperCase() + "!\n";
    }

    public boolean isDead() {
        return this.totalHp <= 0;
    }

    // SETTERS
    public void setHp(int hp) {
        this.totalHp = hp;
        if (this.totalHp > this.totalHpMax)
            this.totalHp = this.totalHpMax;
    }

    public void setCurrentMana(int amount) {
        this.currentMana = amount;
        if (this.currentMana > this.manaMax)
            this.currentMana = this.manaMax;
        if (this.currentMana < 0)
            this.currentMana = 0;
    }

    // GETTERS
    public String getName() {
        return name;
    }

    public int getHpMax() {
        return totalHpMax;
    }

    public int getHp() {
        return totalHp;
    }

    public String getEquippedItems() {
        String temp = "\n----- Equipaggiamento -----\n";
        for (String i : equippedItems.keySet()) {
            try {
                temp += i + ": " + equippedItems.get(i).getName() + "\n";
            } catch (NullPointerException e) {
                temp += i + ": " + "Non equipaggiato\n";
            }
            if (i.equals("Piedi") || i.equals("Secondaria")) {
                temp += "/\n";
            }
        }
        return temp;
    }

    public HashMap<String, Items> getEquippedItemsRaw() {
        return equippedItems;
    }

    public ArrayList<Items> getInventory() {
        return inventory;
    }

    public ArrayList<Usables> getInventoryUsables() {
        ArrayList<Usables> inventoryUsable = new ArrayList<>();
        for (Items item : inventory) {
            if (item instanceof Usables) {
                inventoryUsable.add((Usables) item);
            }
        }
        return inventoryUsable;
    }

    public int getLevel() {
        return level;
    }

    public int getMoney() {
        return money;
    }

    public int getCurrentMana() {
        return this.currentMana;
    }

    public int getManaMax() {
        return this.manaMax;
    }

    public int getManaRegen() {
        return this.manaRegen;
    }

    public double getCritMultiplier() {
        return this.critMultiplier;
    }

    public int getCritRate() {
        return this.critRate;
    }

    public int getDodgeChance() {
        return this.dodgeChance;
    }

    public HashMap<String, Integer> getStats() {
        HashMap<String, Integer> stats = new HashMap<>();
        stats.put("HpMax", totalHpMax);
        stats.put("Strength", totalStrength);
        stats.put("Agility", totalAgility);
        stats.put("Intelligence", totalIntelligence);
        stats.put("Precision", totalPrecision);
        return stats;
    }

    public HashMap<String, Integer> getBaseStats() {
        HashMap<String, Integer> stats = new HashMap<>();
        stats.put("HpMax", baseHpMax);
        stats.put("Strength", baseStrength);
        stats.put("Agility", baseAgility);
        stats.put("Intelligence", baseIntelligence);
        stats.put("Precision", basePrecision);
        return stats;
    }
    /*
     * //STAMPA DEL PERSONAGGIO
     * public void presentation(){
     * System.out.println("\n-----STATS-----\n" +
     * "Classe: " + name + "" +
     * "\nPunti vita: " + totalHp + "/" + totalHpMax +
     * "\nForza: " + totalStrength +
     * "\nAgilità: " + totalAgility +
     * "\nIntelligenza: " + totalIntelligence +
     * "\nPrecisione: " + totalPrecision);
     * }
     */

    public boolean addToInventory(Items item) {
        if (inventoryFull()) {
            return false;
        }
        inventory.add(item);
        return true;
    }

    public void removeFromInventory(Items item) {
        inventory.remove(item);
    }

    public boolean inventoryFull() {
        return inventory.size() >= 20;
    }

    public boolean inInventory(Items item) {
        return inventory.contains(item);
    }

    public String equip(Items item) {
        if (!inInventory(item)) {
            return "Non hai questo oggetto nell'inventario";
        }

        if (item instanceof Weapons) {
            if (!canEquipWeapon(item)) {
                return "Non puoi usare: " + item.getName();
            }

            inventory.remove(item);
            String result = equipWeaponLogic(item);
            updateStats();
            return result;
        }

        else if (item instanceof Armors || item instanceof Artefacts) {
            inventory.remove(item);

            if (equippedItems.get(item.getEquippedSlot().getFirst()) != null) {
                inventory.add(equippedItems.get(item.getEquippedSlot().getFirst()));
            }

            equippedItems.replace(item.getEquippedSlot().getFirst(), item);
            updateStats();
            return "Hai equipaggiato: " + item.getName();
        }

        updateStats();
        return "Non puoi equipaggiare questo oggetto.";
    }

    private String equipWeaponLogic(Items item) {
        Items primaria = equippedItems.get("Primaria");
        Items secondaria = equippedItems.get("Secondaria");

        boolean isTwoHanded = (item instanceof Bow || item instanceof Staff || item instanceof Claymore);
        if (isTwoHanded) {
            if (primaria != null) {
                inventory.add(primaria);
            }
            if (secondaria != null && secondaria != primaria) {
                inventory.add(secondaria);
            }

            equippedItems.replace("Primaria", item);
            equippedItems.replace("Secondaria", item);

            return "Hai equipaggiato " + item.getName() + " a due mani";
        }

        else if (item instanceof Dagger) {
            if (primaria == null) {
                equippedItems.replace("Primaria", item);
            } else if (secondaria == null) {
                if (primaria == secondaria) {
                    inventory.add(primaria);
                    equippedItems.replace("Primaria", item);
                    equippedItems.replace("Secondaria", null);
                } else {
                    equippedItems.replace("Secondaria", item);
                }
            } else {
                inventory.add(primaria);
                equippedItems.replace("Primaria", item);
            }
            return "Hai equipaggiato " + item.getName();
        } else if (item instanceof Shield) {
            if (secondaria != null && primaria != secondaria)
                inventory.add(secondaria);

            if (primaria != null && primaria == secondaria) {
                inventory.add(primaria);
                equippedItems.replace("Primaria", null);
            }
            equippedItems.replace("Secondaria", item);
            return "Hai equipaggiato " + item.getName();
        } else if (item instanceof Sword) {
            if (primaria != null && primaria != secondaria)
                inventory.add(primaria);
            if (primaria != null && primaria == secondaria) {
                inventory.add(primaria);
                equippedItems.replace("Secondaria", null);
            }
            equippedItems.replace("Primaria", item);
            return "Hai equipaggiato " + item.getName();
        }
        return "Non equipaggiabile.";
    }

    public String deEquip(String target) {
        Items itemToDeEquip = equippedItems.get(target);
        if (itemToDeEquip != null) {
            inventory.add(itemToDeEquip);

            if (target.equals("Primaria") && equippedItems.get("Secondaria") == itemToDeEquip) {
                equippedItems.replace("Secondaria", null);
            } else if (target.equals("Secondaria") && equippedItems.get("Primaria") == itemToDeEquip) {
                equippedItems.replace("Primaria", null);
            }

            equippedItems.replace(target, null);
            updateStats();
            return "Hai rimosso: " + itemToDeEquip.getName();
        } else {
            return "Lo slot " + target + " è già vuoto";
        }
    }

    public String gainExp(int amount) {
        this.exp += amount;
        String log = "Hai ottenuto " + amount + " EXP.\n";
        while (this.exp >= this.expToNextLevel) {
            log += levelUp();
        }
        return log;
    }

    protected abstract void applyLevelUpStats();

    protected void resetBaseStats() {
    }

    public String levelUp() {
        this.exp -= this.expToNextLevel;
        this.level++;
        this.expToNextLevel = (int) (this.expToNextLevel * 1.5);

        int tempHpMax = this.totalHpMax;

        // AUMENTI ALLE STATS BASE
        applyLevelUpStats();
        updateStats();

        int hpDifference = this.totalHpMax - tempHpMax;
        this.totalHp += hpDifference;
        return "Sei salito al livello " + this.level + "!\n";
    }

    public void forceLevelUp() {
        this.level++;
        this.expToNextLevel = (int) (this.expToNextLevel * 1.5);
        applyLevelUpStats();
        updateStats();
    }

    public void forceHpAndMana(int newHp, int newMana) {
        this.totalHp = newHp;
        this.currentMana = newMana;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getExp() {
        return this.exp;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void gainMoney(int amount) {
        this.money += amount;
    }

    // Da implementare in un futuro NEGOZIO
    public boolean spendMoney(int amount) {
        if (this.money >= amount) {
            this.money -= amount;
            return true;
        }
        System.out.println("Non hai abbastanza soldi! Te ne servono " + amount + " ma ne hai solo " + this.money);
        return false;
    }

    public String endTurn() {
        StringBuilder log = new StringBuilder();
        this.setCurrentMana(this.currentMana + this.manaRegen);

        if (this.hpRegen > 0) {
            this.setHp(this.totalHp + this.hpRegen);
        }

        if (!activeEffects.isEmpty()) {
            ArrayList<String> toRemove = new ArrayList<>();

            for (Map.Entry<String, Integer> entry : activeEffects.entrySet()) {
                String effect = entry.getKey();
                int turnsLeft = entry.getValue();

                if (effect.equals("Veleno")) {
                    int poisonDamage = Math.max(1, (int) (this.totalHpMax * 0.05));
                    this.totalHp -= poisonDamage;
                    log.append(this.getName()).append(" subisce ").append(poisonDamage).append(" danno da veleno\n");
                } else if (effect.equals("Fuoco")) {
                    this.totalHp -= 15;
                    log.append(this.getName()).append(" subisce 15 danno da scottatura\n");
                }

                turnsLeft--;
                if (turnsLeft <= 0)
                    toRemove.add(effect);
                else
                    activeEffects.put(effect, turnsLeft);
            }
            for (String effect : toRemove) {
                activeEffects.remove(effect);
                log.append("L'effetto [").append(effect.toUpperCase()).append("] su ").append(this.getName())
                        .append(" è svanito.\n");
            }
            if (!toRemove.isEmpty())
                updateStats();
        }
        return log.toString();
    }

    public boolean rollCrit(int rate) {
        Random rand = new Random();
        return (rand.nextInt(100) + 1) <= rate;
    }

    public boolean rollCrit() {
        return this.rollCrit(this.critRate);
    }

    public boolean rollDodge() {
        Random rand = new Random();
        return (rand.nextInt(100) + 1) <= this.dodgeChance;
    }

    public void handleDeath() {
        /*
         * VECCHIO METODO (Tenuto per riferimento)
         * //gestiamo il respawn, per ora ritorna a maxhp e perde parte dell'inventario
         * setHp(totalHpMax);
         * Random rand = new Random();
         * 
         * ArrayList<Items> totalPlayerItem = new ArrayList<>();
         * totalPlayerItem.addAll(inventory);
         * totalPlayerItem.addAll(getEquippedItemsRaw().values());
         * totalPlayerItem.removeAll(Collections.singleton(null));//togliamo tutti i
         * null
         * System.out.println(totalPlayerItem);
         * int roll = rand.nextInt(totalPlayerItem.size());
         * if(roll>3) roll = 3;//massimo 3 item da far perdere //TODO scaling in base a
         * difficolta
         * System.out.println("ITEMS DA DROPPARE :" + roll);
         * 
         * try {
         * for(int i = 0; i<roll; i++){
         * roll = rand.nextInt(totalPlayerItem.size()); //un item a caso da levare
         * Items itemToDrop = totalPlayerItem.get(roll);
         * System.out.println("DOVREBBE DROPPARE: " + itemToDrop);
         * if (inventory.contains(itemToDrop)){
         * removeFromInventory(itemToDrop);
         * System.out.println("BUTTATO CAUSA MORTE: " + itemToDrop);
         * } else {
         * ArrayList<String> itemsEquippedKey = new
         * ArrayList<>(getEquippedItemsRaw().keySet());
         * ArrayList<Items> itemsEquippedValue = new
         * ArrayList<>(getEquippedItemsRaw().values());
         * for(int j = 0; j<itemsEquippedKey.size(); j++){
         * if(itemsEquippedValue.get(j) != null) {
         * if(itemsEquippedValue.get(j).equals(itemToDrop)){
         * deEquip(itemsEquippedKey.get(j));
         * removeFromInventory(itemToDrop);
         * System.out.println("BUTTATO CAUSA MORTE (DALL'EQUIPAGGIAMENTO): " +
         * itemToDrop);
         * }
         * }
         * }
         * }
         * 
         * }
         * } catch (Exception e) {
         * System.out.println("Errore durante la perdità oggetti causa morte");
         * }
         */

        // --- NUOVO METODO: Roguelike Infinite Mode Reset ---
        // Azzeriamo livello ed EXP
        this.level = 1;
        this.exp = 0;
        this.expToNextLevel = 100;

        // Azzeriamo le monete
        this.money = 0;

        // Azzeriamo l'inventario
        this.inventory.clear();

        // Rimuoviamo tutto l'equipaggiamento
        for (String key : this.equippedItems.keySet()) {
            this.equippedItems.put(key, null);
        }

        // Resettiamo le statistiche base
        resetBaseStats();

        // Ricalcoliamo le statistiche pulite (torna alle statistiche base della classe
        // al lv 1)
        updateStats();

        // Ripristiniamo vita e mana massimi
        this.setHp(this.totalHpMax);
        this.setCurrentMana(this.manaMax);
    }
}
