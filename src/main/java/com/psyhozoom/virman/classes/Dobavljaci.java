package com.psyhozoom.virman.classes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONObject;

public class Dobavljaci {

  private Database db;

  private boolean error = false;
  private String errorMSG = "";


  public Dobavljaci(Database db) {
    this.db = db;
  }

  public void saveNewDobavljac(JSONObject readObj) {
    PreparedStatement ps;
    String query =
        "INSERT INTO Dobavljaci (naziv, imeVlasnika, mesto, tel1, tel2, komentar, clientID)"
            + " VALUES "
            + "(?,?,?,?,?,?,?)";
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
      setError(true);
      setErrorMSG(e.getMessage());
      e.printStackTrace();
    }
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

  public JSONObject getDobavljacData(int dobavljacID) {
    JSONObject dobavljac = new JSONObject();
    PreparedStatement ps;
    ResultSet rs;
    String query = "SELECT  * FROM Dobavljaci WHERE id=?";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, dobavljacID);
      rs = ps.executeQuery();
      if (rs.isBeforeFirst()) {
        while (rs.next()) {
          dobavljac.put("id", rs.getInt("id"));
          dobavljac.put("naziv", rs.getString("naziv"));
          dobavljac.put("imeVlasnika", rs.getString("imeVlasnika"));
          dobavljac.put("mesto", rs.getString("mesto"));
          dobavljac.put("tel1", rs.getString("tel1"));
          dobavljac.put("tel2", rs.getString("tel2"));
          dobavljac.put("komentar", rs.getString("komentar"));
          dobavljac.put("clientID", rs.getInt("clientID"));
        }
      }
      ps.close();
      rs.close();
    } catch (SQLException e) {
      setErrorMSG(e.getMessage());
      setError(true);
      e.printStackTrace();
    }
    return dobavljac;
  }

  public void addNewRacunToDobavljac(String racun, int dobavljacID) {
    PreparedStatement ps;
    String query = "INSERT INTO DObavljacRacuni (dobavljacID, racun) VALUES (?,?)";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, dobavljacID);
      ps.setString(2, racun);
      ps.executeUpdate();
      ps.close();
    } catch (SQLException e) {
      setError(true);
      setErrorMSG(e.getMessage());
      e.printStackTrace();
    }
  }

  public void editDobavljac(JSONObject readObj) {
    PreparedStatement ps;
    String query = "UPDATE Dobavljaci SET naziv=?, imeVlasnika=?, mesto=?, tel1=?, tel2=?,  komentar=? WHERE id=?";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setString(1, readObj.getString("naziv"));
      ps.setString(2, readObj.getString("imeVlasnika"));
      ps.setString(3, readObj.getString("mesto"));
      ps.setString(4, readObj.getString("tel1"));
      ps.setString(5, readObj.getString("tel2"));
      ps.setString(6, readObj.getString("komentar"));
      ps.setInt(7, readObj.getInt("dobavljacID"));
      ps.executeUpdate();
      ps.close();
    } catch (SQLException e) {
      setErrorMSG(e.getMessage());
      setError(true);
      e.printStackTrace();
    }

  }

  public void deleteDobavljac(int dobavljacID) {
    deleteRacunDobavljaca(dobavljacID);
    if (isError()) {
      return;
    }

    PreparedStatement ps;
    String query = "DELETE FROM Dobavljaci WHERE id=?";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, dobavljacID);
      ps.executeUpdate();
      ps.close();
    } catch (SQLException e) {
      setErrorMSG(e.getMessage());
      setError(true);
      e.printStackTrace();
    }

  }

  private void deleteRacunDobavljaca(int dobavljacID) {
    PreparedStatement ps;
    String query = "DELETE FROM DobavljacRacun WHERE dobavljacID=?";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, dobavljacID);
      ps.executeUpdate();
    } catch (SQLException e) {
      setError(true);
      setErrorMSG(e.getMessage());
      e.printStackTrace();
    }
  }
}
