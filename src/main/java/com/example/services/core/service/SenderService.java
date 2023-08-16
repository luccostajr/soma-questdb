package com.example.services.core.service;

import java.util.List;
import java.util.concurrent.RecursiveAction;

import com.example.domain.core.Entity;
import com.example.domain.core.SingleValue;
import com.example.services.questdb.service.QuestDBService;

public abstract class SenderService<T> extends RecursiveAction {
  protected QuestDBService questDBService = null; 
  private Entity<T> entity = null;

  protected abstract QuestDBService createQuestDBService();
  public abstract void execute();

  public SenderService(Entity<T> entity) {
    if (entity == null) throw new IllegalArgumentException("SenderService(ctor)::entity is null.");
    this.entity = entity;
    this.questDBService = createQuestDBService();
  }

  public String getName() {
    return entity.getName();
  }

  public List<SingleValue<T>> getData() {
    return entity.getData();
  }

  public List<SingleValue<T>> getData(Integer start, Integer end) {
    return entity.getData(start, end);
  }

  public Integer getDataSize() {
    return entity.getData().size();
  }

  public QuestDBService getQuestDBService() {
    return questDBService;
  }

  public Entity<T> getEntity() {
    return entity;
  }

  @Override
  protected void compute() {
    throw new UnsupportedOperationException("Unimplemented method 'SenderService::compute'");
  }
}
