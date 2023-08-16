package com.example.services.core.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.domain.core.SingleValue;

public class Helper {
  private static final long DEFAULT_QUANTITY = 5000; 
  private static Random random = null;

  public static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static List<SingleValue<Double>> generateData(Long quantity, Double stableValue) { 
    List<SingleValue<Double>> data = new ArrayList<SingleValue<Double>>(); 
    for(long i = 0; i < quantity; i++) { 
        Double value = stableValue; 
        SingleValue<Double> singleValue = new SingleValue<>(value); 
        data.add(singleValue); 
    } 
    return data;
  }

  public static List<SingleValue<Double>> generateData(Long quantity) { 
    return generateData(quantity, 10.0);
  }

  public static Double generateRandomValue() { 
    if (random == null) { 
      random = new Random();
      random.setSeed(System.currentTimeMillis());
    }

    return Math.floor(random.nextDouble()*100);
  }

  public static List<SingleValue<Double>> generateRandomData(Long quantity) { 
    if (random == null) { 
      random = new Random();
      random.setSeed(System.currentTimeMillis());
    }

    List<SingleValue<Double>> data = new ArrayList<SingleValue<Double>>(); 
    for(long i = 0; i < quantity; i++) { 
        Double value = generateRandomValue(); 
        SingleValue<Double> singleValue = new SingleValue<>(value); 
        data.add(singleValue); 
    } 
    return data;
  }

  public static List<SingleValue<Double>> generateData() { 
    return generateData(DEFAULT_QUANTITY);
  }
}
