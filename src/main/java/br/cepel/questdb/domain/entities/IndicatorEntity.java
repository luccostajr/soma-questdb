package br.cepel.questdb.domain.entities;

import java.util.List;
import java.util.Random;

import br.cepel.questdb.data.IndicatorData;
import br.cepel.questdb.services.helper.IndicatorHelper;

public class IndicatorEntity {
  private final String DEFAULT_NAME = "IndicatorEntity";
  private static Random random = new Random();
  private static Long firstIndicatorId = null;

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
    
    Long newIndicatorId = indicatorId;
    if (newIndicatorId == null) newIndicatorId = IndicatorHelper.generateRandomIndicatorId();
    
    List<IndicatorData> data = entity.getData();
    System.out.println(data.size());

    for (IndicatorData indicatorData : data) {
      if (firstIndicatorId == null) firstIndicatorId = indicatorData.getDate();
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

  public Long getFirstIndicatorId() {
    return firstIndicatorId;
  }

  public Long getLastTimeStamp(int limit) {
    if (limit > getData().size()) limit = getData().size();
    
    Long indicatorId = null;
    int counter = 0;
    
    for (IndicatorData indicatorData : getData()) {
      if (counter == limit) {
        indicatorId = indicatorData.getIndicatorId();
        break;
      }
      counter++;
    }
    
    return indicatorId;
  }
}
