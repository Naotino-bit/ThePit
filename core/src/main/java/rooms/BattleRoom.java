package rooms;

import characters.Character;
import characters.enemies.Enemies;
import game.BattleManager;

import java.util.ArrayList;

public class BattleRoom extends Room {
    public BattleManager getBattleManager() { return battle; }

    private BattleManager battle;

    public BattleRoom(Character player, ArrayList<Enemies> enemies) {
        this.roomName = "Stanza dei Mostri";
        // Prepariamo la battaglia
        this.battle = new BattleManager(player, enemies);
    }

    public String enterRoom(Character player) {
        return "Sei entrato in una stanza. Ci sono dei nemici!";
    }

    @Override
    public String processCommand(String command, Character player) {
        // Giriamo il comando direttamente all'arbitro della battaglia
        String risultato = battle.manageRound(command);

        // Se l'arbitro dice che è finita, dichiariamo la stanza completata!
        if (battle.isBattleOver()) {
            this.isCleared = true;
        }

        return risultato;
    }

    @Override
    public ArrayList<items.Items> getPendingLoot() {
        if (battle != null) {
            return battle.getPendingLoot();
        }
        return new ArrayList<>();
    }
}