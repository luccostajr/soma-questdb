package com.example.domain.entities.data;

public class DefaultHistoricData<V,T> {
  protected long date;
  protected V value;
  protected T indicatorId = null;

  public DefaultHistoricData(long date, V value, T indicatorId) {
    this.date = date;
    this.value = value;
    this.indicatorId = indicatorId;
  }

  public long getDate() {
    return date;
  }

  public V getValue() {
    return value;
  }
  
  public T getIndicatorId() {
    return indicatorId;
  }

  public void setIndicatorId(T indicatorId) {
    this.indicatorId = indicatorId;
  }
}
