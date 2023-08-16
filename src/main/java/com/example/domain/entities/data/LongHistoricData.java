package com.example.domain.entities.data;

public class LongHistoricData extends DoubleHistoricData<Long>{
  public LongHistoricData(long date, Double value, Long indicatorId) {
    super(date, value, indicatorId);
  }

  public LongHistoricData(long date, Double value) {
    super(date, value, null);
  }
}
