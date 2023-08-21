package br.cepel.questdb.main;

import br.cepel.questdb.domain.entities.IndicatorEntity;
import br.cepel.questdb.domain.historic.IndicatorHistoric;
import br.cepel.questdb.services.config.ServicesConfig;

public class AppIndicator extends AbstractApp {
  private static final String TABLE_NAME = "TENDENCIA_MEDIDA";
  private static final String FILE_NAME_STRING = "QUERY_LONG_SYMBOL";
  private static final String RESULT_FILE_NAME = "TEMPOS_LONG_SYMBOL";

  @Override
  protected ServicesConfig getServicesConfig() {
    ServicesConfig applicationConfig = new ServicesConfig();
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
  protected IndicatorHistoric createHistoric(IndicatorEntity entity) {
    return new IndicatorHistoric(entity);
  }
}
