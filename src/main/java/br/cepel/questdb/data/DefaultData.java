package br.cepel.questdb.data;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DefaultData<V,T> {
  protected Long date;
  protected V value;
  protected T indicatorId = null;

  public DefaultData(long date, V value, T indicatorId) {
    this.date = date;
    this.value = value;
    this.indicatorId = indicatorId;
  }

  public long getDate() {
    return date;
  }

  public void setDate(Long date) {
    this.date = date;
  }

  public V getValue() {
    return value;
  }

  public void setValue(V value) {
    this.value = value;
  }
  
  public T getIndicatorId() {
    return indicatorId;
  }

  public void setIndicatorId(T indicatorId) {
    this.indicatorId = indicatorId;
  }

  @Override
  public String toString() {
    Instant instant = Instant.ofEpochMilli(date);
    ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
    return "DefaultData::[date=" + zonedDateTime + ", value=" + value + ", indicatorId=" + indicatorId + "]";
  }
}
