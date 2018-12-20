package com.psyhozoom.virman.classes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONObject;

public class Clients {

  private Database db;
  private boolean error = false;
  private String errorMSG = "";


  public Clients(Database db) {
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

  public JSONObject getAllClients() {
    JSONObject object = new JSONObject();
    PreparedStatement ps;
    ResultSet rs;
    String query = "SELECT * FROM Clients";
    try {
      ps = db.conn.prepareStatement(query);
      rs = ps.executeQuery();
      if (rs.isBeforeFirst()) {
        int i = 0;
        while (rs.next()) {
          JSONObject client = new JSONObject();
          client.put("id", rs.getInt("id"));
          client.put("naziv", rs.getString("naziv"));
          client.put("imeVlasnika", rs.getString("imeVlasnika"));
          client.put("mesto", rs.getString("mesto"));
          client.put("tel1", rs.getString("tel1"));
          client.put("tel2", rs.getString("tel2"));
          object.put(String.valueOf(i), client);
          i++;
        }
      }
      ps.close();
      rs.close();
    } catch (SQLException e) {
      setError(true);
      setErrorMSG(e.getMessage());
      e.printStackTrace();
    }
    return object;
  }

  public void saveNewClient(JSONObject readObj) {
    JSONObject object = new JSONObject();
    String query = "INSERT INTO Clients (naziv, imeVlasnika, mesto, tel1, tel2, komentar) "
        + "VALUES "
        + "(?,?,?,?,?,?)";
    PreparedStatement ps;
    try {
      ps = db.conn.prepareStatement(query);
      ps.setString(1, readObj.getString("naziv"));
      ps.setString(2, readObj.getString("imeVlasnika"));
      ps.setString(3, readObj.getString("mesto"));
      ps.setString(4, readObj.getString("tel1"));
      ps.setString(5, readObj.getString("tel2"));
      ps.setString(6, readObj.getString("komentar"));
      ps.executeUpdate();
      ps.close();
    } catch (SQLException e) {
      setErrorMSG(e.getMessage());
      setError(true);
      e.printStackTrace();
    }
  }

  public JSONObject getClientData(int id) {
    JSONObject client = new JSONObject();
    PreparedStatement ps;
    ResultSet rs;
    String query = "SELECT * FROM Clients WHERE id=?";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, id);
      rs = ps.executeQuery();
      if (rs.isBeforeFirst()) {
        while (rs.next()) {
          client.put("id", rs.getInt("id"));
          client.put("naziv", rs.getString("naziv"));
          client.put("imeVlasnika", rs.getString("imeVlasnika"));
          client.put("mesto", rs.getString("mesto"));
          client.put("tel1", rs.getString("tel1"));
          client.put("tel2", rs.getString("tel2"));
          client.put("komentar", rs.getString("komentar"));
        }
      }
      ps.close();
      rs.close();
    } catch (SQLException e) {
      setError(true);
      setErrorMSG(e.getMessage());
      e.printStackTrace();
    }
    return client;
  }

  public JSONObject getDobavljaciOfClient(int id) {
    JSONObject object = new JSONObject();
    PreparedStatement ps;
    ResultSet rs;
    String query = "SELECT * FROM Dobavljaci WHERE clientID=?";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, id);
      rs = ps.executeQuery();
      if (rs.isBeforeFirst()) {
        int i = 0;
        while (rs.next()) {
          JSONObject dobavljac = new JSONObject();
          dobavljac.put("id", rs.getInt("id"));
          dobavljac.put("naziv", rs.getString("naziv"));
          dobavljac.put("imeVlasnika", rs.getString("imeVlasnika"));
          dobavljac.put("mesto", rs.getString("mesto"));
          dobavljac.put("tel1", rs.getString("tel1"));
          dobavljac.put("tel2", rs.getString("tel2"));
          dobavljac.put("komentar", rs.getString("komentar"));
          dobavljac.put("clientID", rs.getInt("clientID"));
          object.put(String.valueOf(i), dobavljac);
          i++;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return object;
  }

  public void saveClientData(JSONObject readObj) {
    PreparedStatement ps;
    String query = "UPDATE Clients SET naziv=?, imeVlasnika=?, mesto=?, tel1=?, tel2=?, komentar=? WHERE id=?";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setString(1, readObj.getString("naziv"));
      ps.setString(2, readObj.getString("imeVlasnika"));
      ps.setString(3, readObj.getString("mesto"));
      ps.setString(4, readObj.getString("tel1"));
      ps.setString(5, readObj.getString("tel2"));
      ps.setString(6, readObj.getString("komentar"));
      ps.setInt(7, readObj.getInt("clientID"));
      ps.executeUpdate();

      ps.close();
    } catch (SQLException e) {
      setErrorMSG(e.getMessage());
      setError(true);
      e.printStackTrace();
    }
  }

  public void deleteClient(int clientID) {
    PreparedStatement ps;
    String query = "DELETE FROM Clients WHERE id=?";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, clientID);
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    query = "DELETE FROM ClientRacuni WHERE clientID =?";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, clientID);
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    query = "SELECT id FROM Dobavljaci WHERE clientID=?";
    ResultSet rs;
    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, clientID);
      rs = ps.executeQuery();
      if (rs.isBeforeFirst()) {
        while (rs.next()) {
          deleteDobavljacRacun(rs.getInt("id"));
          deleteDobavljac(rs.getInt("id"));
        }
      }
      ps.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void deleteDobavljac(int id) {
    PreparedStatement ps;
    String query = "DELETE FROM Dobavljaci WHERE id=?";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, id);
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void deleteDobavljacRacun(int id) {
    PreparedStatement ps;
    String query = "DELETE FROM DobavljacRacuni WHERE dobavljacID=?";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, id);
      ps.executeUpdate();
      ps.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
