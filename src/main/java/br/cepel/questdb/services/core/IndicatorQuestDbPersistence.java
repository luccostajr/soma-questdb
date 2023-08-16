package br.cepel.questdb.services.core;

import java.util.List;
import java.util.concurrent.RecursiveAction;

import br.cepel.questdb.data.IndicatorData;
import br.cepel.questdb.domain.entities.IndicatorEntity;
import br.cepel.questdb.services.IndicatorSymbolQuestDbService;
import br.cepel.questdb.services.QuestDBService;
import br.cepel.questdb.services.config.ServicesConfig;

public class IndicatorQuestDbPersistence extends RecursiveAction {
  protected QuestDBService questDBService = null; 
  private IndicatorEntity entity = null;

  protected QuestDBService createQuestDBService(ServicesConfig servicesConfig) {
    return new IndicatorSymbolQuestDbService(servicesConfig); 
  }

  public IndicatorQuestDbPersistence(IndicatorEntity entity, ServicesConfig servicesConfig) {
    if (entity == null) throw new IllegalArgumentException("IndicatorSenderService(ctor)::entity is null.");
    this.entity = entity;
    this.questDBService = createQuestDBService(servicesConfig);
  }

  public String getName() {
    return entity.getName();
  }

  public List<IndicatorData> getData() {
    return entity.getData();
  }

  public List<IndicatorData> getData(Integer start, Integer end) {
    return entity.getData(start, end);
  }

  public Integer getDataSize() {
    return entity.getData().size();
  }

  public QuestDBService getQuestDBService() {
    return questDBService;
  }

  public IndicatorEntity getEntity() {
    return entity;
  }

  @Override
  protected void compute() {
    throw new UnsupportedOperationException("Unimplemented method 'SenderService::compute'");
  }

  public void execute() {
    IndicatorSymbolQuestDbService questDBService = (IndicatorSymbolQuestDbService) getQuestDBService(); 

    IndicatorEntity entity = getEntity();
    
    try {
      questDBService.processEntity(entity);
    } 
    catch (Exception e) {
      e.printStackTrace();
    }

    questDBService.flushAndClose();
  }
}
