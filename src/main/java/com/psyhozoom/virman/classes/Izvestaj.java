package com.psyhozoom.virman.classes;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.json.JSONObject;

public class Izvestaj {

  private Database db;
  private boolean error = false;
  private String errorMSG = "";
  private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public Izvestaj(Database db) {
    this.db = db;
  }


  public JSONObject getIzvestaje(String date) {
    JSONObject object = new JSONObject();
    PreparedStatement ps;
    ResultSet rs;
    String query = "SELECT * FROM Izvestaji where Date=?";

    try {
      ps = db.conn.prepareStatement(query);
      ps.setString(1, date);
      rs = ps.executeQuery();
      if (rs.isBeforeFirst()) {
        int i = 0;
        while (rs.next()) {
          JSONObject izvestaj = new JSONObject();
          izvestaj.put("id", rs.getInt("id"));
          izvestaj.put("Platioc", rs.getString("Platioc"));
          izvestaj.put("Primaoc", rs.getString("Primaoc"));
          izvestaj.put("racunPlatioca", rs.getString("racunPlatioca"));
          izvestaj.put("racunPrimaoca", rs.getString("racunPrimaoca"));
          izvestaj.put("date", rs.getString("date"));
          izvestaj.put("iznos", rs.getDouble("iznos"));
          izvestaj.put("virman", rs.getString("virman"));
          izvestaj.put("sifraPlacanja", rs.getString("sifraPlacanja"));
          izvestaj.put("modelZaduzenje", rs.getString("modelZaduzenje"));
          izvestaj.put("modelOdobrenje", rs.getString("modelOdobrenje"));
          izvestaj.put("pozivNaBrojZaduzenje", rs.getString("pozivNaBrojZaduzenje"));
          izvestaj.put("pozivNaBrojOdobrenje", rs.getString("pozivNaBrojOdobrenje"));
          izvestaj.put("svrhaPlacanja", rs.getString("svrhaPlacanja"));
          object.put(String.valueOf(i), izvestaj);
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

  public void uplatiUplatu(JSONObject object) {
    PreparedStatement ps;
    String query = "INSERT INTO Izvestaji "
        + "(Platioc, mestoPlatioca, Primaoc,  mestoPrimaoca, racunPrimaoca,"
        + " svrhaPlacanja, sifraPlacanja, iznos, modelOdobrenje, pozivNaBrojOdobrenje, date, virman) "
        + "VALUES "
        + "(?,?,?,?,?,?,?,?,?,?,?,'NALOG ZA UPLATU')";

    try {
      ps = db.conn.prepareStatement(query);
      ps.setString(1, object.getString("Platioc"));
      ps.setString(2, object.getString("mesto"));
      ps.setString(3, object.getString("Platioc"));
      ps.setString(4, object.getString("mesto"));
      ps.setString(5, object.getString("racunPrimaoca"));
      ps.setString(6, object.getString("svrhaUplate"));
      ps.setInt(7, object.getInt("sifraPlacanja"));
      ps.setDouble(8, object.getDouble("iznos"));
      ps.setString(9, object.getString("modelOdobrenje"));
      ps.setString(10, object.getString("pozivNaBrojOdobrenje"));
      ps.setString(11, LocalDate.now().format(dtf));
      ps.executeUpdate();
      ps.close();
    } catch (SQLException e) {
      setError(true);
      setErrorMSG(e.getMessage());
      e.printStackTrace();
    }

  }

  public void uplatiIsplatu(JSONObject object) {
    PreparedStatement ps;
    String query = "INSERT INTO Izvestaji "
        + "(Platioc, mestoPlatioca, Primaoc, mestoPrimaoca, racunPlatioca, svrhaPlacanja, sifraPlacanja, "
        + "iznos, modelZaduzenje, pozivNaBrojZaduzenje, date, virman) "
        + "VALUES "
        + "(?,?,?,?,?,?,?,?,?,?,?, 'NALOG ZA ISPLATU')";

    try {
      ps = db.conn.prepareStatement(query);
      ps.setString(1, object.getString("Platioc"));
      ps.setString(2, object.getString("mesto"));
      ps.setString(3, object.getString("Platioc"));
      ps.setString(4, object.getString("mesto"));
      ps.setString(5, object.getString("racunPlatioca"));
      ps.setString(6, object.getString("svrhaUplate"));
      ps.setInt(7, object.getInt("sifraPlacanja"));
      ps.setDouble(8, object.getDouble("iznos"));
      ps.setString(9, object.getString("modelZaduzenje"));
      ps.setString(10, object.getString("pozivNaBrojZaduzenje"));
      ps.setString(11, LocalDate.now().format(dtf));
      ps.executeUpdate();
      ps.close();
    } catch (SQLException e) {
      setErrorMSG(e.getMessage());
      setError(true);
      e.printStackTrace();
    }

  }

  public void uplatiPrenos(JSONObject object) {
    PreparedStatement ps;
    String query = " INSERT INTO Izvestaji "
        + "(Platioc, mestoPlatioca, Primaoc, MestoPrimaoca, racunPlatioca, racunPrimaoca, svrhaUplate, sifrPlacanja, "
        + "iznos, modelZaduzenje, modelOdobrenje, pozivNaBrojZaduzenje, pozivNaBrojOdobrenje, date, virman) "
        + "VALUES "
        + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,'NALOG ZA PRENOS')";

    try {
      ps = db.conn.prepareStatement(query);
      ps.setString(1, object.getString("platioc"));
      ps.setString(2, object.getString("mestoPlatioca"));
      ps.setString(3, object.getString("primalac"));
      ps.setString(4, object.getString("mestoPrimaoca"));
      ps.setString(5, object.getString("racunPlatioca"));
      ps.setString(6, object.getString("racunPrimaoca"));
      ps.setInt(7, object.getInt("sifraPlacanja"));
      ps.setString(8, object.getString("svrhaPlacanja"));
      ps.setDouble(9, object.getDouble("iznos"));
      ps.setString(10, object.getString("modelZaduzenje"));
      ps.setString(11, object.getString("modelOdobrenje"));
      ps.setString(12, object.getString("pozivNaBrojZaduzenje"));
      ps.setString(13, object.getString("pozivNaBrojOdobrenje"));
      ps.setString(14, LocalDate.now().format(dtf));
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

  public void deleteIzvestaje(int id) {
    PreparedStatement ps;
    String query = "DELETE FROM Izvestaji WHERE id=?";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, id);
      ps.executeUpdate();
      ps.close();
    } catch (SQLException e) {
      setErrorMSG(e.getMessage());
      setError(true);
      e.printStackTrace();
    }
  }
}
