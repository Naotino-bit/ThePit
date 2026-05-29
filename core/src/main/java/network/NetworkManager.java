package network;

import com.badlogic.gdx.Gdx;
import java.io.*;
import java.net.Socket;

public class NetworkManager {
    private String ip;
    private int port;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean connected = false;

    // Interfaccia per "ascoltare" i messaggi dal server nel resto del gioco
    public interface NetworkListener {
        void onMessageReceived(String message);
        void onConnectionError(String error);
    }

    private NetworkListener listener;

    public NetworkManager(String ip, int port, NetworkListener listener) {
        this.ip = ip;
        this.port = port;
        this.listener = listener;
    }

    public void setListener(NetworkListener listener) {
        this.listener = listener;
    }

    public void connect() {
        // Avviamo la connessione in un THREAD separato
        new Thread(() -> {
            try {
                socket = new Socket(ip, port);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                connected = true;

                String serverMessage;
                // Loop di ascolto infinito (finché il socket è aperto)
                while ((serverMessage = in.readLine()) != null) {
                    final String msg = serverMessage;

                    // IMPORTANTE: Poiché siamo in un thread separato,
                    // usiamo postRunnable per riportare il messaggio nel thread di LibGDX
                    Gdx.app.postRunnable(() -> {
                        listener.onMessageReceived(msg);
                    });
                }
                
                // Se usciamo dal loop, il server ha chiuso la connessione
                Gdx.app.postRunnable(() -> {
                    listener.onConnectionError("Connessione chiusa dal server.");
                });
            } catch (IOException e) {
                Gdx.app.postRunnable(() -> {
                    listener.onConnectionError("Errore di connessione: " + e.getMessage());
                });
            }
        }).start();
    }

    public void sendCommand(String command) {
        if (connected && out != null) {
            out.println(command);
        }
    }

    public void disconnect() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
