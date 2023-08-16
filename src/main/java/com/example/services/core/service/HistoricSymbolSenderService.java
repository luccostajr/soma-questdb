package com.example.services.core.service;

import com.example.domain.core.Entity;
import com.example.domain.entities.data.DoubleHistoricData;
import com.example.services.questdb.service.HistoricSymbolQuestDBService;
import com.example.services.questdb.service.QuestDBService;

public class HistoricSymbolSenderService<T> extends SenderService<DoubleHistoricData<T>> {
  public HistoricSymbolSenderService(Entity<DoubleHistoricData<T>> entity) { 
    super(entity);
  } 

  public void execute() {
    HistoricSymbolQuestDBService<T> questDBService = (HistoricSymbolQuestDBService<T>) getQuestDBService(); 

    Entity<DoubleHistoricData<T>> entity = getEntity();
    
    try {
      questDBService.processEntity(entity);
    } 
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    questDBService.flushAndClose();
  }

  @Override
  protected QuestDBService createQuestDBService() {
    return new HistoricSymbolQuestDBService<T>(); 
  }
}
