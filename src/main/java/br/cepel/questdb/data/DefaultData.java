package br.cepel.questdb.data;

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
}
