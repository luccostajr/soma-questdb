package com.example.domain.entities;

import java.util.UUID;

import com.example.domain.core.Entity;
import com.example.domain.entities.data.DoubleHistoricData;

public class UuidHistoric extends AbstractHistoric<UUID> {
  public UuidHistoric(Entity<DoubleHistoricData<UUID>> entity) {
    super(entity);
  }

  @Override
  protected UUID getNewIndicatorId() {
    return UUID.randomUUID();
  }
}
