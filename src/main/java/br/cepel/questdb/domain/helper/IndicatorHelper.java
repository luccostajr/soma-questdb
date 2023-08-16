package br.cepel.questdb.domain.helper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.cepel.questdb.data.IndicatorData;

public class IndicatorHelper {
  private static final long DEFAULT_QUANTITY = 5000; 
  private static Random random = null;
  private static Long counterDate = 0L;
  private static Long now = 0L;

  public static Long getNow() {
    if (now == null) {
      now = Instant.now().toEpochMilli();
    }
    return (now + counterDate++) * 1000000;
  }

  public static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static List<IndicatorData> generateData(Long quantity, Double stableValue) { 
    List<IndicatorData> dataList = new ArrayList<IndicatorData>(); 
    Long date = Instant.now().toEpochMilli() + counterDate++; 
    
    for(long i = 0; i < quantity; i++) { 
      IndicatorData indicatorData = new IndicatorData();
      indicatorData.setValue(stableValue);
      indicatorData.setIndicatorId(random.nextLong(100000));
      indicatorData.setDate(date * 1000000);

      dataList.add(indicatorData); 
    } 

    return dataList;
  }

  public static List<IndicatorData> generateData(Long quantity) { 
    return generateData(quantity, 10.0);
  }

  public static List<IndicatorData> generateRandomData(Long quantity) { 
    if (random == null) { 
      random = new Random();
      random.setSeed(System.currentTimeMillis());
    }

    List<IndicatorData> dataList = new ArrayList<IndicatorData>(); 
    Long date = Instant.now().toEpochMilli() + counterDate++; 

    for (long i = 0; i < quantity; i++) { 
      Double value = generateRandomValue(); 
      IndicatorData indicatorData = new IndicatorData();
      indicatorData.setValue(value);
      indicatorData.setIndicatorId(random.nextLong(100000));
      indicatorData.setDate(date * 1000000);

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
    random.setSeed(System.currentTimeMillis());
    return Math.floor(random.nextDouble()*100);
  }

  public static Long generateRandomIndicatorId() {
    random.setSeed(System.currentTimeMillis());
    return Math.abs(random.nextLong());
  }
}
