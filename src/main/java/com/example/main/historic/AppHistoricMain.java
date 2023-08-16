package com.example.main.historic;

public class AppHistoricMain {
  public static void main(String[] args) {
    try {
      AppUuidHistoricSymbol appHistoricFirst = new AppUuidHistoricSymbol();
      appHistoricFirst.execute();
      appHistoricFirst = null;
      System.gc();

      AppLongHistoricSymbol appLongHistoricFirst = new AppLongHistoricSymbol();
      appLongHistoricFirst.execute();
      appLongHistoricFirst = null;
      System.gc();
    } 
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
