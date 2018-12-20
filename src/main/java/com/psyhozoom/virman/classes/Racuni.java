package com.psyhozoom.virman.classes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONObject;

public class Racuni {

  private Database db;
  private boolean error;
  private String errorMSG;

  public Racuni(Database db) {
    this.db = db;
  }


  public boolean isError() {
    return error;
  }

  public void setError(boolean error) {
    this.error = error;
  }

  public String getErrorMSG() {
    return errorMSG;
  }

  public void setErrorMSG(String errorMSG) {
    this.errorMSG = errorMSG;
  }

  public void addRacunToClient(int clientID, String racun) {
    PreparedStatement ps;
    String query = "INSERT INTO ClientRacuni (clientID, racun) VALUES (?,?)";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, clientID);
      ps.setString(2, racun);
      ps.executeUpdate();
      ps.close();
    } catch (SQLException e) {
      setError(true);
      setErrorMSG(e.getMessage());
      e.printStackTrace();
    }
  }

  public JSONObject getRacuniOfClient(int clientID) {
    JSONObject object = new JSONObject();
    PreparedStatement ps;
    ResultSet rs;
    String query = "SELECT * FROM ClientRacuni WHERE clientID=? order by mainRacun DESC";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, clientID);
      rs = ps.executeQuery();
      if (rs.isBeforeFirst()) {
        int i = 0;
        while (rs.next()) {
          JSONObject racun = new JSONObject();
          racun.put("id", rs.getInt("id"));
          racun.put("clientID", rs.getInt("clientID"));
          racun.put("racun", rs.getString("racun"));
          racun.put("mainRacun", rs.getBoolean("mainRacun"));
          object.put(String.valueOf(i), racun);
          i++;
        }
      }
      ps.close();
      rs.close();
    } catch (SQLException e) {
      setErrorMSG(e.getMessage());
      setError(true);
      e.printStackTrace();
    }
    return object;
  }

  public void deleteClientRacun(int racunID) {
    PreparedStatement ps;
    String query = "DELETE FROM ClientRacuni WHERE id=?";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, racunID);
      ps.executeUpdate();
      ps.close();
    } catch (SQLException e) {
      setError(true);
      setErrorMSG(e.getMessage());
      e.printStackTrace();
    }
  }

  public JSONObject getRacuniOfDobavljac(int dobavljacID) {
    JSONObject object = new JSONObject();
    PreparedStatement ps;
    ResultSet rs;
    String query = "SELECT * FROM DobavljacRacuni WHERE dobavljacID=? order by mainRacun DESC";

    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, dobavljacID);
      rs = ps.executeQuery();
      if (rs.isBeforeFirst()) {
        int i = 0;
        while (rs.next()) {
          JSONObject racun = new JSONObject();
          racun.put("id", rs.getInt("id"));
          racun.put("dobavljacID", rs.getInt("dobavljacID"));
          racun.put("racun", rs.getString("racun"));
          racun.put("mainRacun", rs.getBoolean("mainRacun"));
          object.put(String.valueOf(i), racun);
          i++;
        }
      }
      ps.close();
      rs.close();
    } catch (SQLException e) {
      setErrorMSG(e.getMessage());
      setError(true);
      e.printStackTrace();
    }

    return object;
  }

  public void setDefaultRacunDobavljac(int dobavljac, int racunID) {
    PreparedStatement ps;
    String query = "UPDATE DobavljacRacuni set mainRacun=false where dobavljacID=?";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, dobavljac);
      ps.executeUpdate();
      ps.close();
    } catch (SQLException e) {
      setError(true);
      setErrorMSG(e.getMessage());
      e.printStackTrace();
    }

    query = "UPDATE DobavljacRacuni set mainRacun=true WHERE id=?";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, racunID);
      ps.executeUpdate();
      ps.close();
    } catch (SQLException e) {
      setErrorMSG(e.getMessage());
      setError(true);
      e.printStackTrace();
    }
  }

  public void setDefaultRacunClient(int clientID, int clientRacun) {
    PreparedStatement ps;
    String query = "UPDATE ClientRacuni SET mainRacun=false WHERE clientID=?";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, clientID);
      ps.executeUpdate();
      ps.close();
    } catch (SQLException e) {
      setError(true);
      setErrorMSG(e.getMessage());
      e.printStackTrace();
    }

    query = "UPDATE ClientRacuni SET mainRacun=true  WHERE id=?";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, clientRacun);
      ps.executeUpdate();
      ps.close();
    } catch (SQLException e) {
      setErrorMSG(e.getMessage());
      setError(true);
      e.printStackTrace();
    }
  }

  public void deleteDobavljacRacun(int racunID) {
    PreparedStatement ps;
    String query = "DELETE from DobavljacRacuni WHERE id=?";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, racunID);
      ps.executeUpdate();
      ps.close();
    } catch (SQLException e) {
      setError(true);
      setErrorMSG(e.getMessage());
      e.printStackTrace();
    }
  }
}
