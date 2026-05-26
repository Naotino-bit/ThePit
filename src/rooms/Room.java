package rooms;

import characters.Character;

public abstract class Room {
    protected String roomName;
    protected boolean isCleared = false; // Diventa true quando puoi proseguire

    // Metodo chiamato appena il player entra nella stanza
    public abstract String enterRoom(Character player);

    // Metodo che smista i comandi del giocatore mentre è in questa stanza
    public abstract String processCommand(String command, Character player);

    // Getters
    public boolean isCleared() { return isCleared; }
    public String getRoomName() { return roomName; }

    // Ritorna gli oggetti droppati a terra (se ce ne sono)
    public java.util.ArrayList<items.Items> getPendingLoot() {
        return new java.util.ArrayList<>();
    }
}