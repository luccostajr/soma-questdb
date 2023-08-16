package com.example.main.historic;

import java.util.UUID;

import com.example.domain.core.Entity;
import com.example.domain.entities.AbstractHistoric;
import com.example.domain.entities.UuidHistoric;
import com.example.domain.entities.data.DoubleHistoricData;
import com.example.services.core.service.HistoricSymbolSenderService;
import com.example.services.core.service.SenderService;

public class AppUuidHistoricSymbol extends AbstractAppHistoric<UUID> {
  private static final String TABLE_NAME = "SymbUuidHistoricData";
  private static final String FILE_NAME_STRING = "QUERY_UUID_SYMBOL";
  private static final String RESULT_FILE_NAME = "TEMPOS_UUID_SYMBOL";

  @Override
  protected SenderService<?> createSenderService(AbstractHistoric<UUID> historic) {
    return new HistoricSymbolSenderService<>(historic);
  }

  @Override
  protected AppHistoricConfig getApplicationConfig() {
    AppHistoricConfig applicationConfig = new AppHistoricConfig();
    applicationConfig.tableName = TABLE_NAME;
    applicationConfig.fileName = applicationConfig.path + FILE_NAME_STRING;
    applicationConfig.resultFileName = applicationConfig.path + RESULT_FILE_NAME;
    applicationConfig.numberOfExecutions = 1;
    applicationConfig.toExecuteQuery = true;
    applicationConfig.timeoutRetries = 100;
    applicationConfig.distinctHistorics = 5;

    return applicationConfig;
  }

  @Override
  protected AbstractHistoric<UUID> createHistoric(Entity<DoubleHistoricData<UUID>> entity) {
    return new UuidHistoric(entity);
  }
}
