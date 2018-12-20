package com.psyhozoom.virman.classes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

  String url = "jdbc:sqlite:database.db";
  Connection conn = null;
  Statement statement = null;

  public void connect() {
    try {
      conn = DriverManager.getConnection(url);
      statement = conn.createStatement();

    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

  public Connection getConn() {
    return conn;
  }

  public void close() {
    try {
      conn.close();
      statement.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public Statement getStatement() {
    return this.statement;
  }

}
