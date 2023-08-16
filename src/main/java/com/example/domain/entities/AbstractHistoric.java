package com.example.domain.entities;

import java.util.List;

import com.example.domain.core.Entity;
import com.example.domain.core.SingleValue;
import com.example.domain.entities.data.DoubleHistoricData;

public abstract class AbstractHistoric<T> extends Entity<DoubleHistoricData<T>> {
  private T indicatorId = null;
  private static Long firstTimeStamp = null;

  /*
   * GET A NEW INDICATOR ID
   */
  protected abstract T getNewIndicatorId();

  public AbstractHistoric(Entity<DoubleHistoricData<T>> entity, T indicatorId) { 
    super(entity);
    
    this.indicatorId = getNewIndicatorId();
    
    T newIndicatorId = indicatorId;
    if (newIndicatorId == null) newIndicatorId = getNewIndicatorId();
    
    List<SingleValue<DoubleHistoricData<T>>> data = entity.getData();
    System.out.println(data.size());

    for (SingleValue<DoubleHistoricData<T>> singleValue : data) {
      if (firstTimeStamp == null) firstTimeStamp = singleValue.getTimestamp();

      DoubleHistoricData<T> historicData = singleValue.getValue();
      historicData.setIndicatorId(newIndicatorId);
    }
  }

  public AbstractHistoric(Entity<DoubleHistoricData<T>> entity) {
    this(entity, null);
  }

  public T getIndicatorId() {
    return indicatorId;
  }

  public Long getFirstTimeStamp() {
    return firstTimeStamp;
  }

  public Long getLastTimeStamp(int limit) {
    if (limit > getData().size()) limit = getData().size();
    
    Long lastTimeStamp = null;
    int counter = 0;
    
    for (SingleValue<DoubleHistoricData<T>> singleValue : getData()) {
      if (counter == limit) {
        lastTimeStamp = singleValue.getTimestamp();
        break;
      }
      counter++;
    }
    
    return lastTimeStamp;
  }
}
