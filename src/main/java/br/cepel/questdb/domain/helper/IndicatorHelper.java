package br.cepel.questdb.domain.helper;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.cepel.questdb.data.IndicatorData;

public class IndicatorHelper {
  private static final long DEFAULT_QUANTITY = 5000; 
  private static Random random = new Random(System.currentTimeMillis());
  private static Long counterDate = 0L;

  public static Long getNow() {
    ZonedDateTime now = ZonedDateTime.now();
    long date = now.toInstant().toEpochMilli() + counterDate++;
    return date * 1000000;
  }

  public static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } 
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static List<IndicatorData> generateData(Long quantity, Double stableValue) { 
    List<IndicatorData> dataList = new ArrayList<IndicatorData>(); 
    
    for (long i = 0; i < quantity; i++) { 
      Long date = getNow(); 
      IndicatorData indicatorData = new IndicatorData();

      indicatorData.setValue(stableValue);
      indicatorData.setIndicatorId(random.nextLong(100000));
      indicatorData.setDate(date);

      dataList.add(indicatorData); 
    } 

    return dataList;
  }

  public static List<IndicatorData> generateData(Long quantity) { 
    return generateData(quantity, 10.0);
  }

  public static List<IndicatorData> generateRandomData(Long quantity) { 
    List<IndicatorData> dataList = new ArrayList<IndicatorData>(); 

    for (long i = 0; i < quantity; i++) { 
      Long date = getNow(); 
      IndicatorData indicatorData = new IndicatorData();

      indicatorData.setValue(generateRandomValue());
      indicatorData.setIndicatorId(random.nextLong(100000));
      indicatorData.setDate(date);

      dataList.add(indicatorData); 
    } 

    return dataList;
  }

  public static List<IndicatorData> generateData() { 
    return generateData(DEFAULT_QUANTITY);
  }

  public static List<IndicatorData> generateRandomData() { 
    return generateRandomData(DEFAULT_QUANTITY);
  }

  public static Double generateRandomValue() { 
    return Math.floor(random.nextDouble()*100);
  }

  public static Long generateRandomIndicatorId() {
    return Math.abs(random.nextLong());
  }
}
