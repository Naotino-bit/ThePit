package client;

import com.badlogic.gdx.Game;
import screens.WelcomeScreen; // Assicurati di importare la nuova schermata!

// Sostituiamo ApplicationAdapter con Game
public class Main extends Game {

    @Override
    public void create() {
        // Appena il gioco parte, carichiamo la schermata di Benvenuto!
        // Passiamo 'this' (il Main stesso) alla schermata così potrà dirci di cambiare pagina dopo.
        this.setScreen(new WelcomeScreen(this));
    }

    // Non c'è bisogno di fare l'Override di render() o dispose() qui,
    // la classe Game li gestisce in automatico passando il comando alla schermata attiva!
}
