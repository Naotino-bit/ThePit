package rooms;

import characters.Character;

public class FountainRoom extends Room {
    
    public FountainRoom() {
        this.roomName = "Fontana della Vita";
    }

    @Override
    public String enterRoom(Character player) {
        return "💦 Sei entrato in una stanza tranquilla.\nAl centro c'è una " + roomName + " luminosa.\nPuoi gettare delle monete per recuperare salute (1 moneta = 2 HP).\n" +
               "I tuoi HP: " + player.getHp() + "/" + player.getHpMax() + " | Le tue monete: " + player.getMoney();
    }

    @Override
    public String processCommand(String command, Character player) {
        String[] parts = command.split(" ");
        String action = parts[0].toUpperCase();

        if (action.equals("ESCI")) {
            this.isCleared = true;
            return "Hai lasciato la Fontana della Vita.";
        }

        if (action.equals("BEVI")) {
            if (parts.length < 2) {
                return "";
            }
            try {
                int amountToSpend = Integer.parseInt(parts[1]);
                if (amountToSpend <= 0) {
                    return "Devi offrire almeno 1 moneta.";
                }

                if (player.spendMoney(amountToSpend)) {
                    int hpToRestore = amountToSpend * 2;
                    int oldHp = player.getHp();
                    player.setHp(player.getHp() + hpToRestore);
                    int restored = player.getHp() - oldHp;
                    return "Hai lanciato " + amountToSpend + " monete nella fontana.\nHai recuperato " + restored + " HP! (Ora sei a " + player.getHp() + "/" + player.getHpMax() + " HP)";
                } else {
                    return "Non hai abbastanza monete! Hai solo " + player.getMoney() + " monete.";
                }
            } catch (NumberFormatException e) {
                return "";
            }
        }

        return "";
    }
}
