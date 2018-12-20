package com.psyhozoom.virman;

import com.psyhozoom.virman.classes.Database;
import com.psyhozoom.virman.classes.ServerWorker;
import java.io.IOException;
import java.net.ServerSocket;

public class Server {

  public static void main(String[] args) {
    int port = 18567;
    ServerSocket serverSocket = null;

    try {
      serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      e.printStackTrace();
    }

    Database db = new Database();
    db.connect();

    while (true) {
      ServerWorker sw;
      try {
        sw = new ServerWorker(serverSocket.accept(), db);
        Thread th = new Thread(sw);
        th.start();
      } catch (IOException e) {
        e.printStackTrace();
      }

    }


  }

}
