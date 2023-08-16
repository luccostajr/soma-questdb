package com.example.domain.entities.data;

public class DoubleHistoricData<T> extends DefaultHistoricData<Double,T> {
  public DoubleHistoricData(long date, Double value, T indicatorId) {
    super(date, value, indicatorId);
  }

  public DoubleHistoricData(long date, Double value) {
    super(date, value, null);
  }
}
