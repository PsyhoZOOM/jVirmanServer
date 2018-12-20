package com.psyhozoom.virman.classes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONObject;

public class ServerWorker implements Runnable {

  public Socket socket;
  private InputStreamReader isr;
  private BufferedReader bfr;
  private OutputStreamWriter osw;
  private BufferedWriter bfw;
  private boolean loggedIn;
  private Database db;

  public ServerWorker(Socket socket, Database db) {
    this.socket = socket;
    this.db = db;

  }

  @Override
  public void run() {
    while (!socket.isClosed()) {
      if (isr == null) {
        System.out.println("socket oppened");
        try {
          isr = new InputStreamReader(socket.getInputStream());
          bfr = new BufferedReader(isr);

        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      System.out.println(socket.getPort());
      String a = null;
      try {
        a = bfr.readLine();

        if (a == null) {
          System.out.println(String.format("Klijent %s je diskonektovan.",
              socket.getRemoteSocketAddress().toString().replace("/", "")));
          return;
        }

        JSONObject object = new JSONObject(a);
        System.out.println(object.toString());

        if (!loggedIn) {
          goLogin(object);
          if (loggedIn) {
            continue;
          }
          object = new JSONObject();
          object.put("ERROR", "NOT_LOGGED");
          send(object);
          close();
          return;
        } else {
          goFork(object);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      System.out.println(a);
      if (a == null) {
        try {
          socket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    System.out.println("Client closed connection");
  }

  private void goLogin(JSONObject object) {
    String query = "SELECT * FROM users WHERE username=? AND pass =?";
    PreparedStatement ps;
    ResultSet rs;
    try {
      ps = db.conn.prepareStatement(query);
      ps.setString(1, object.getString("username"));
      ps.setString(2, object.getString("pass"));
      rs = ps.executeQuery();
      if (rs.isBeforeFirst()) {
        loggedIn = true;
        object = new JSONObject();
        object.put("LOGGED_IN", "true");
        send(object);
      } else {
        loggedIn = false;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }


  private void close() {
    try {
      bfr.close();
      isr.close();
      bfw.close();
      osw.close();
      System.out.println(String.format("KLIJENT DISKONEKTOVAN %s",
          socket.getRemoteSocketAddress().toString().replace("/", "")));
      socket.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void send(JSONObject object) {
    if (osw == null || bfw == null) {
      try {
        osw = new OutputStreamWriter(socket.getOutputStream());
        bfw = new BufferedWriter(osw);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    try {
      bfw.write(object.toString());
      bfw.newLine();
      bfw.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void goFork(JSONObject readObj) {
    System.out.println(readObj);

    if (readObj.getString("action").equals("getAllClients")) {
      JSONObject object = new JSONObject();
      Clients clients = new Clients(this.db);
      object = clients.getAllClients();
      if (clients.isError()) {
        object.put("ERROR", clients.getErrorMSG());
      }
      send(object);
      return;
    }

    if (readObj.getString("action").equals("saveNewClient")) {
      JSONObject object = new JSONObject();
      Clients clients = new Clients(this.db);
      clients.saveNewClient(readObj);
      if (clients.isError()) {
        object.put("ERROR", clients.getErrorMSG());
      }
      send(object);
      return;
    }
    if (readObj.getString("action").equals("getClientData")) {
      JSONObject object = new JSONObject();
      Clients clients = new Clients(this.db);
      object = clients.getClientData(readObj.getInt("id"));
      if (clients.isError()) {
        object.put("ERROR", clients.getErrorMSG());
      }
      send(object);
      return;
    }

    if (readObj.getString("action").equals("getDobavljaciOfClient")) {
      JSONObject object = new JSONObject();
      Clients clients = new Clients(this.db);
      object = clients.getDobavljaciOfClient(readObj.getInt("clientID"));
      if (clients.isError()) {
        object.put("ERROR", clients.getErrorMSG());
      }
      send(object);
      return;
    }

    if (readObj.getString("action").equals("saveClientData")) {
      JSONObject object = new JSONObject();
      Clients clients = new Clients(this.db);
      clients.saveClientData(readObj);
      if (clients.isError()) {
        object.put("ERROR", clients.getErrorMSG());
      }
      send(object);
      return;
    }

    if (readObj.getString("action").equals("addRacunToClient")) {
      JSONObject object = new JSONObject();
      Racuni racuni = new Racuni(this.db);
      racuni.addRacunToClient(readObj.getInt("clientID"), readObj.getString("racun"));
      if (racuni.isError()) {
        object.put("ERROR", racuni.getErrorMSG());
      }
      send(object);
      return;
    }

    if (readObj.getString("action").equals("deleteClient")) {
      JSONObject object = new JSONObject();
      Clients client = new Clients(this.db);
      client.deleteClient(readObj.getInt("clientID"));
      if (client.isError()) {
        object.put("ERROR", client.getErrorMSG());
      }
      send(object);
      return;
    }

    if (readObj.getString("action").equals("getRacuniClient")) {
      JSONObject object = new JSONObject();
      Racuni racuni = new Racuni(this.db);
      object = racuni.getRacuniOfClient(readObj.getInt("clientID"));
      if (racuni.isError()) {
        object.put("ERROR", racuni.getErrorMSG());
      }
      send(object);
      return;
    }

    if (readObj.getString("action").equals("deleteClientRacun")) {
      JSONObject object = new JSONObject();
      Racuni racuni = new Racuni(this.db);
      racuni.deleteClientRacun(readObj.getInt("racunID"));
      if (racuni.isError()) {
        object.put("ERROR", racuni.getErrorMSG());
      }
      send(object);

      return;
    }

    if (readObj.getString("action").equals("saveNewDobavljac")) {
      JSONObject object = new JSONObject();
      Dobavljaci dobavljac = new Dobavljaci(this.db);
      dobavljac.saveNewDobavljac(readObj);
      if (dobavljac.isError()) {
        object.put("ERROR", dobavljac.getErrorMSG());
      }
      send(object);
      return;
    }

    if (readObj.get("action").equals("editDobavljac")) {
      JSONObject object = new JSONObject();
      Dobavljaci dobavljac = new Dobavljaci(this.db);
      dobavljac.editDobavljac(readObj);
      if (dobavljac.isError()) {
        object.put("ERROR", dobavljac.getErrorMSG());
      }
      send(object);
      return;
    }

    if (readObj.get("action").equals("getDobavljacData")) {
      JSONObject object = new JSONObject();
      Dobavljaci dobavljaci = new Dobavljaci(this.db);
      object = dobavljaci.getDobavljacData(readObj.getInt("dobavljacID"));
      if (dobavljaci.isError()) {
        object.put("ERROR", dobavljaci.getErrorMSG());
      }
      send(object);
      return;
    }

    if (readObj.get("action").equals("getRacuniDobavljaca")) {
      JSONObject object = new JSONObject();
      Racuni racuni = new Racuni(this.db);
      object = racuni.getRacuniOfDobavljac(readObj.getInt("dobavljacID"));
      if (racuni.isError()) {
        object.put("ERROR", racuni.getErrorMSG());
      }
      send(object);
      return;
    }

    if (readObj.get("action").equals("addNewRacunToDobavljac")) {
      JSONObject object = new JSONObject();
      Dobavljaci dobavljac = new Dobavljaci(this.db);
      dobavljac.addNewRacunToDobavljac(readObj.getString("racun"), readObj.getInt("dobavljacID"));
      if (dobavljac.isError()) {
        object.put("ERROR", dobavljac.getErrorMSG());
      }
      send(object);
      return;
    }

    if (readObj.get("action").equals("getAllSifre")) {
      JSONObject object = new JSONObject();
      Sifre sifre = new Sifre(this.db);
      object = sifre.getAllSifre();
      if (sifre.isError()) {
        object.put("ERROR", sifre.getErrorMSG());
      }
      send(object);
      return;
    }

    if (readObj.getString("action").equals("deleteSifra")) {
      JSONObject object = new JSONObject();
      Sifre sifre = new Sifre(this.db);
      sifre.deleteSifra(readObj.getInt("idSifre"));
      if (sifre.isError()) {
        object.put("ERROR", sifre.getErrorMSG());
      }
      send(object);
      return;
    }

    if (readObj.getString("action").equals("addSifra")) {
      JSONObject object = new JSONObject();
      Sifre sifre = new Sifre(this.db);
      sifre.addSifra(readObj);
      if (sifre.isError()) {
        object.put("ERROR", sifre.getErrorMSG());
      }
      send(object);
      return;
    }

    if (readObj.getString("action").equals("updateClientMainRacun")) {
      JSONObject object = new JSONObject();
      Racuni racuni = new Racuni(this.db);
      racuni.setDefaultRacunClient(readObj.getInt("clientID"), readObj.getInt("racunID"));
      if (racuni.isError()) {
        object.put("ERROR", racuni.getErrorMSG());
      }
      send(object);
      return;
    }

    if (readObj.getString("action").equals("updateDobavljacMainRacun")) {
      JSONObject object = new JSONObject();
      Racuni racuni = new Racuni(this.db);
      racuni.setDefaultRacunDobavljac(readObj.getInt("dobavljacID"), readObj.getInt("racunID"));
      if (racuni.isError()) {
        object.put("ERROR", racuni.getErrorMSG());
      }
      send(object);
      return;
    }

    if (readObj.getString("action").equals("deleteDobavljacRacun")) {
      JSONObject object = new JSONObject();
      Racuni racuni = new Racuni(this.db);
      racuni.deleteDobavljacRacun(readObj.getInt("racunID"));
      if (racuni.isError()) {
        object.put("ERROR", racuni.getErrorMSG());
      }
      send(object);
      return;
    }

    if (readObj.getString("action").equals("deleteDobavljac")) {
      JSONObject object = new JSONObject();
      Dobavljaci dobavljaci = new Dobavljaci(this.db);
      dobavljaci.deleteDobavljac(readObj.getInt("dobavljacID"));
      if (dobavljaci.isError()) {
        object.put("ERROR", dobavljaci.getErrorMSG());
      }
      send(object);
      return;
    }

    if (readObj.getString("action").equals("getIzvestaje")) {
      JSONObject object = new JSONObject();
      Izvestaj izvestaj = new Izvestaj(this.db);
      object = izvestaj.getIzvestaje(readObj.getString("datum"));
      if ((izvestaj.isError())) {
        object.put("ERROR", izvestaj.getErrorMSG());
      }
      send(object);
      return;

    }

    if (readObj.getString("action").equals("uplatiUplatu")) {
      JSONObject object = new JSONObject();
      Izvestaj izvestaj = new Izvestaj(this.db);
      izvestaj.uplatiUplatu(readObj);
      if (izvestaj.isError()) {
        object.put("ERROR", izvestaj.getErrorMSG());
      }
      send(object);
      return;
    }

    if (readObj.getString("action").equals("uplatiIsplatu")) {
      JSONObject object = new JSONObject();
      Izvestaj izvestaj = new Izvestaj(this.db);
      izvestaj.uplatiIsplatu(readObj);
      if (izvestaj.isError()) {
        object.put("ERROR", izvestaj.getErrorMSG());
      }
      send(object);
      return;
    }

    if (readObj.getString("action").equals("uplatiPrenos")) {
      JSONObject object = new JSONObject();
      Izvestaj izvestaj = new Izvestaj(this.db);
      izvestaj.uplatiPrenos(readObj);
      if (izvestaj.isError()) {
        object.put("ERROR", izvestaj.getErrorMSG());
      }
      send(object);
      return;
    }

    if (readObj.getString("action").equals("deleteIzvestaj")) {
      JSONObject object = new JSONObject();
      Izvestaj izvestaj = new Izvestaj(this.db);
      izvestaj.deleteIzvestaje(readObj.getInt("id"));
      if (izvestaj.isError()) {
        object.put("ERROR", izvestaj.getErrorMSG());
      }
      send(object);
      return;
    }


  }
}
