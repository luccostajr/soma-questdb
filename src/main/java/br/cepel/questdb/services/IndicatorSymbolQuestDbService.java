package br.cepel.questdb.services;

import java.util.List;

import br.cepel.questdb.data.IndicatorData;
import br.cepel.questdb.domain.entities.IndicatorEntity;
import io.questdb.client.Sender;

public class IndicatorSymbolQuestDbService extends QuestDBService {
  public IndicatorSymbolQuestDbService() {
    super();
  }

  public void processEntity(IndicatorEntity entity) throws Exception {
    if (!acquire()) return;
    if (entity == null) {
      throw new RuntimeException("HistoricQuestDBService::process(Entity):unexpected error: entity is null.");
    }
    
    Sender sender = getSender();
    List<IndicatorData> values = entity.getData(); 
    values.forEach(indicatorData -> {
      Long date = indicatorData.getDate();
      Double value = indicatorData.getValue();
      String indicatorId = indicatorData.getIndicatorId().toString();

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
