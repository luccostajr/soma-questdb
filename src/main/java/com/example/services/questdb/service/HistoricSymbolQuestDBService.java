package com.example.services.questdb.service;

import java.util.List;

import com.example.domain.core.Entity;
import com.example.domain.core.SingleValue;
import com.example.domain.entities.data.DoubleHistoricData;

import io.questdb.client.Sender;

public class HistoricSymbolQuestDBService<T> extends QuestDBService {
  public HistoricSymbolQuestDBService() {
    super();
  }

  public void processEntity(Entity<DoubleHistoricData<T>> entity) throws Exception {
    if (!acquire()) return;
    if (entity == null) {
      throw new RuntimeException("HistoricQuestDBService::process(Entity):unexpected error: entity is null.");
    }
    
    Sender sender = getSender();
    List<SingleValue<DoubleHistoricData<T>>> values = entity.getData(); 
    values.forEach(singleValue -> {
      DoubleHistoricData<T> historicData = singleValue.getValue();
      Long date = historicData.getDate();
      Double value = historicData.getValue();
      String indicatorId = historicData.getIndicatorId().toString();

      sender
        // .table(historic.getName())
        .table(entity.getName())
        .symbol("indicatorId", indicatorId)
        .doubleColumn("value", value)
        .at(date);

      //sender.flush();
    });

    flush();
    release();
  }
}
