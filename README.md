<div align="center">

![ThePit]([https://via.placeholder.com/800x400?text=ThePit+-+16-bit+RPG](https://github.com/Naotino-bit/ThePit/blob/Added_GUI/assets/the_pit_text.png))

**Un videogioco RPG multiplayer in stile 16-bit con architettura Client-Server.**

![GitHub repo size](https://img.shields.io/github/repo-size/Naotino-bit/ThePit?style=for-the-badge&color=blue)
![GitHub last commit (branch)](https://img.shields.io/github/last-commit/Naotino-bit/ThePit/Added_GUI?style=for-the-badge&color=orange)
</div>

---

## 📖 Descrizione del Progetto

**ThePit** è un videogioco di ruolo (RPG) sviluppato per il corso di **Programmazione a oggetti** del Corso di Laurea in Informatica (L-31) presso l'Università degli Studi di Messina. 

Nato originariamente come gioco testuale su console, il progetto è stato recentemente potenziato con una vera e propria **Interfaccia Grafica (GUI)**. L'obiettivo dell'applicazione è fornire un'arena digitale in stile dungeon crawler, mettendo in pratica i concetti fondamentali della programmazione a oggetti (Ereditarietà, Polimorfismo, Incapsulamento) e la gestione di reti.

### ✨ Caratteristiche Principali
* **Architettura Client-Server:** La logica di gioco, le statistiche e i calcoli dei danni vengono elaborati da un server centrale in ascolto, mentre il client grafico si occupa unicamente dell'estetica e dell'input utente tramite Socket.
* **Grafica 16-bit:** Interfaccia utente interamente ridisegnata utilizzando la libreria **LibGDX**, con Viewport scalabili e finestre di dialogo.
* **Sistema di Classi:** Gestione polimorfica di personaggi multipli (Guerriero, Mago, Arciere, ecc.).
* **Data Driven:** Lettura e caricamento di oggetti, armi e armature in modo dinamico tramite file **XML**.

---

## 🛠️ Tecnologie Utilizzate

Il progetto sfrutta le seguenti tecnologie per garantire un'esperienza fluida e una solida struttura:

<div align="center">
  <img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/libGDX-%23E34F26.svg?style=for-the-badge&logo=java&logoColor=white" alt="LibGDX" />
  <img src="https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white" alt="Gradle" />
  <img src="https://img.shields.io/badge/mysql-%2300000F.svg?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL" />
  <img src="https://img.shields.io/badge/xml-%23000000.svg?style=for-the-badge&logo=xml&logoColor=white" alt="XML" />
  <img src="https://img.shields.io/badge/Sockets-Network-blue?style=for-the-badge" alt="Java Sockets" />
</div>

---

## 🚀 Guida all'Installazione

All'interno della repository vi è tutto il necessario per inizializzare il client grafico e il server di gioco.

### Prerequisiti
Prima di avviare l'applicazione, verifica di avere:
* **Java Development Kit (JDK 17 o superiore)** installato.
* Un IDE supportato (si consiglia **IntelliJ IDEA** per la massima compatibilità con Gradle).

### Avvio rapido

1. **Clona la repository** (assicurandoti di scaricare il branch corretto):
   ```bash
   git clone -b Added_GUI [https://github.com/Naotino-bit/ThePit.git](https://github.com/Naotino-bit/ThePit.git)
   cd ThePit
