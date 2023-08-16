package com.example.services.questdb.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import io.questdb.client.Sender;

/*
soma.jdbc.user={db_user}
soma.jdbc.pass={db_pass}
soma.jdbc.driver=org.postgresql.Driver
soma.jdbc.url=jdbc:postgresql://postgres/postgres
*/

public class QuestDBHelper {
  private static final String DEFAULT_SSLMODE = "disable";
  private static final String DEFAULT_HOST = "localhost";
  private static final String DEFAULT_INFLUX_PORT = "9009";
  private static final String DEFAULT_POSTGRES_PORT = "8812";
  private static final String DEFAULT_POSTGRES_USER = "admin";
  private static final String DEFAULT_POSTGRES_PASS = "quest";
  
  private static final void setPostgresProperties() {
    System.setProperty("soma.datalake.driver", "org.postgresql.Driver");

    if (System.getProperty("soma.datalake.pgurl") == null) {
      if (System.getProperty("soma.datalake.host") == null) System.setProperty("soma.datalake.host", DEFAULT_HOST);
      if (System.getProperty("soma.datalake.pgport") == null) System.setProperty("soma.datalake.pgport", DEFAULT_POSTGRES_PORT);
      
      System.setProperty("soma.datalake.pgurl", 
        "jdbc:postgresql://"+
        System.getProperty("soma.datalake.host")+":"+
        System.getProperty("soma.datalake.pgport")+"/qdb");
    }

    if (System.getProperty("soma.datalake.pguser") == null) System.setProperty("soma.datalake.pguser", DEFAULT_POSTGRES_USER);
    if (System.getProperty("soma.datalake.pgpass") == null) System.setProperty("soma.datalake.pgpass", DEFAULT_POSTGRES_PASS);
  }

  private static final void setInfluxProperties() {
    if (System.getProperty("soma.datalake.infxurl") == null) {
      if (System.getProperty("soma.datalake.host") == null) System.setProperty("soma.datalake.host", DEFAULT_HOST);
      if (System.getProperty("soma.datalake.infxport") == null) System.setProperty("soma.datalake.infxport", DEFAULT_INFLUX_PORT);
      
      System.setProperty("soma.datalake.infxurl", 
        System.getProperty("soma.datalake.host")+":"+
        System.getProperty("soma.datalake.infxport"));
    }

    // set sslmode value to 'require' if connecting to a QuestDB Cloud instance
    if (System.getProperty("soma.datalake.sslmode") == null) System.setProperty("soma.datalake.sslmode", DEFAULT_SSLMODE);
  }

  public static Connection getPostgresConnection() throws SQLException {
    setPostgresProperties();

    System.out.println("pgUser  = " + System.getProperty("soma.datalake.pguser"));
    System.out.println("pgPass  = " + System.getProperty("soma.datalake.pgpass"));
    System.out.println("pgUrl   = " + System.getProperty("soma.datalake.pgurl"));
    System.out.println("sslmode = " + System.getProperty("soma.datalake.sslmode"));

    Properties properties = new Properties();
    properties.setProperty("user", System.getProperty("soma.datalake.pguser"));
    properties.setProperty("password", System.getProperty("soma.datalake.pgpass"));

    properties.setProperty("sslmode", System.getProperty("soma.datalake.sslmode"));

    final Connection connection = DriverManager.getConnection(
        System.getProperty("soma.datalake.pgurl"), properties);

    return connection;
  }

  public static Sender getInfluxSender() {
    setInfluxProperties();

    Sender sender = Sender.builder()
      .address(System.getProperty("soma.datalake.infxurl"))
      .build();

    return sender;
  }
}
