package com.example.domain.entities.data;

import java.util.UUID;

public class UuidHistoricData extends DoubleHistoricData<UUID> {
  public UuidHistoricData(long date, Double value, UUID indicatorId) {
    super(date, value, indicatorId);
  }

  public UuidHistoricData(long date, Double value) {
    super(date, value, null);
  }
}
