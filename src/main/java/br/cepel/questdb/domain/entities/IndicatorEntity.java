package br.cepel.questdb.domain.entities;

import java.util.List;
import java.util.Random;

import br.cepel.questdb.data.IndicatorData;

public class IndicatorEntity {
  private final String DEFAULT_NAME = "IndicatorEntity";
  private static Random random = new Random();

  protected String name = null;
  protected List<IndicatorData> data = null;

  public IndicatorEntity (String name, List<IndicatorData> data) {
    if (data == null) throw new IllegalArgumentException("DefaultEntity::ctor: data list cannot be null");
    if (name == null) name = DEFAULT_NAME + ":" + Math.abs(random.nextInt());
    
    this.name = name;
    this.data = data;
  }

  public IndicatorEntity (IndicatorEntity entity) {
    if (entity == null) throw new IllegalArgumentException("DefaultEntity::ctor: entity cannot be null");

    String name = entity.getName();
    if (name == null) name = DEFAULT_NAME + ":" + Math.abs(random.nextInt());
    
    this.name = name;
    this.data = entity.getData();
  }

  public IndicatorEntity(IndicatorEntity entity, Long indicatorId) { 
    this(entity);
    
    this.indicatorId = getNewIndicatorId();
    
    Long newIndicatorId = indicatorId;
    if (newIndicatorId == null) newIndicatorId = getNewIndicatorId();
    
    List<IndicatorData> data = entity.getData();
    System.out.println(data.size());

    for (IndicatorData indicatorData : data) {
      if (firstTimeStamp == null) firstTimeStamp = indicatorData.getDate();
      indicatorData.setIndicatorId(newIndicatorId);
    }
  }

  public List<IndicatorData> getData () {
    return data;
  }

  public List<IndicatorData> getData (Integer start, Integer end) {
    if (start == null || start < 0) {
        start = 0;
    }
    
    if (end == null || end > data.size()) {
        end = data.size();
    }
    
    return data.subList(start, end);
  }

  public String getName() {
      return name;
  }
}
