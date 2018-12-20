package com.psyhozoom.virman.classes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONObject;

public class Sifre {

  private Database db;
  private boolean error = false;
  private String errorMSG = "";

  public Sifre(Database db) {
    this.db = db;
  }

  public JSONObject getAllSifre() {
    JSONObject object = new JSONObject();
    PreparedStatement ps;
    ResultSet rs;
    String query = "SELECT * FROM SifraPlacanja  ORDER BY broj ASC";
    try {
      ps = db.conn.prepareStatement(query);
      rs = ps.executeQuery();
      if (rs.isBeforeFirst()) {
        int i = 0;
        while (rs.next()) {
          JSONObject sifra = new JSONObject();
          sifra.put("id", rs.getInt("id"));
          sifra.put("broj", rs.getString("broj"));
          sifra.put("opis", rs.getString("opis"));
          sifra.put("duziOpis", rs.getString("duziOpis"));
          object.put(String.valueOf(i), sifra);
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

  public void deleteSifra(int idSfre) {
    PreparedStatement ps;
    String query = "DELETE FROM SifraPlacanja WHERE id=?";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, idSfre);
      ps.executeUpdate();
      ps.close();
    } catch (SQLException e) {
      setErrorMSG(e.getMessage());
      setError(true);
      e.printStackTrace();
    }
  }

  public void addSifra(JSONObject readObj) {
    PreparedStatement ps;
    String query = "INSERT INTO SifraPlacanja (broj, opis, duziOpis) VALUES (?,?,?)";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setString(1, readObj.getString("broj"));
      ps.setString(2, readObj.getString("opis"));
      ps.setString(3, readObj.getString("duziOpis"));
      ps.executeUpdate();
      ps.close();
    } catch (SQLException e) {
      setError(true);
      setErrorMSG(e.getMessage());
      e.printStackTrace();
    }
  }
}
