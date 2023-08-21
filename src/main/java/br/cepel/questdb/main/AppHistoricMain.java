package br.cepel.questdb.main;

public class AppHistoricMain {
  public static void main(String[] args) {
    try {
      AppIndicator appHistoricFirst = new AppIndicator();
      appHistoricFirst.execute();
      appHistoricFirst = null;
      System.gc();
    } 
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
