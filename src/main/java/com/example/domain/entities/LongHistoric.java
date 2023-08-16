package com.example.domain.entities;

import java.util.Random;

import com.example.domain.core.Entity;
import com.example.domain.entities.data.DoubleHistoricData;

public class LongHistoric extends AbstractHistoric<Long> {
  private static Random random = new Random();

  public LongHistoric(Entity<DoubleHistoricData<Long>> entity) {
    super(entity);
  }

  @Override
  protected Long getNewIndicatorId() {
    random.setSeed(System.currentTimeMillis());
    return Math.abs(random.nextLong());
  }
}
