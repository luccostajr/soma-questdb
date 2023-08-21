package br.cepel.questdb.main;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

import br.cepel.questdb.domain.entities.IndicatorEntity;
import br.cepel.questdb.domain.historic.IndicatorHistoric;
import br.cepel.questdb.services.QuestDbIndicatorService;
import br.cepel.questdb.services.config.ServicesConfig;
import br.cepel.questdb.services.core.IndicatorQuestDbPersistence;
import br.cepel.questdb.services.helper.ServicesHelper;

public abstract class AbstractApp {
  private ServicesConfig servicesConfig = null;

  /*
   * INJECTION OF THE CUSTOM CONFIGURATION PARAMETERS
   */
  protected abstract ServicesConfig getServicesConfig();

  /*
   * INJECTION OF THE HISTORIC CLASS
   */
  protected abstract IndicatorHistoric createHistoric(IndicatorEntity entity);

  /*
   * CREATE HISTORIC SENDER SERVICE
   */
  public void sendHistoric(IndicatorHistoric historic) throws IOException {
    IndicatorQuestDbPersistence senderService = new IndicatorQuestDbPersistence(historic, servicesConfig);
    if (senderService != null) senderService.execute();
  }

  /*
   * PROCESS
   */
  private void process(int idx, QuestDbIndicatorService service, FileWriter logWriter, FileWriter resultsWriter) throws IOException, SQLException {
    String header = "Execução #" + idx;
    String timeCreate = "CREATE";
    String timeSend = "SEND";

    for (int i = 0; i < servicesConfig.distinctHistorics; i++) {
      System.out.println(
        servicesConfig.resultFileName + 
        "::Historic proccess cycle #" + (i+1) + " of " + 
        servicesConfig.distinctHistorics);

      header += ";" + (i+1);

      long startCreate = System.currentTimeMillis();

      IndicatorHistoric historic = service.createDistinctHistoric();

      long durationCreate = System.currentTimeMillis() - startCreate;

      servicesConfig.totalTimeToCreate += durationCreate;
      timeCreate += ";" + durationCreate;

      long startSend = System.currentTimeMillis();
      
      sendHistoric(historic);
      
      long durationSend = System.currentTimeMillis() - startSend;
      servicesConfig.totalTimeToSend += durationSend;

      timeSend += ";" + durationSend;
    }

    header += "\n";
    timeCreate += "\n";
    timeSend += "\n";

    resultsWriter.write(header);
    resultsWriter.write(timeCreate);
    resultsWriter.write(timeSend);

    ServicesHelper.dumpDeltaMessage(logWriter,
      "Criando todos os registros de histórico", servicesConfig.totalTimeToCreate);
    ServicesHelper.dumpDeltaMessage(logWriter,
      "Enviando todos os registros de histórico", servicesConfig.totalTimeToSend);

    if (servicesConfig.toExecuteQuery) {
      service.executeQuery(logWriter, resultsWriter);
    }
  }

  public void execute() throws IOException, SQLException {
    servicesConfig = getServicesConfig();
    
    QuestDbIndicatorService service = new QuestDbIndicatorService(servicesConfig);

    for (int idx = 0; idx < servicesConfig.numberOfExecutions; idx++) {
      String fileName = servicesConfig.fileName + "_" + (idx+1) + ".txt";
      System.out.println("Creating Log file: " + fileName);
  
      FileWriter logWriter = ServicesHelper.createFile(fileName);
      logWriter.write("Statistics:\n");
      logWriter.flush();
  
      fileName = servicesConfig.resultFileName + "_DETAILS_" + idx + ".csv";
      System.out.println("Creating Metrics file: " + fileName);
      FileWriter resultsWriter = ServicesHelper.createFile(fileName);
  
      process(idx+1, service, logWriter, resultsWriter);

      resultsWriter.flush();
      resultsWriter.close();
  
      logWriter.flush();
      logWriter.close();
    }

    System.out.println("Process finished. Storing time results...");
    String fileName = servicesConfig.resultFileName + ".csv";
    ServicesHelper.storeTimeResults(fileName);
    System.out.println("Time results successfully stored.");
  }
}
