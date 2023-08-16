package com.example.main.historic;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.domain.core.Entity;
import com.example.domain.core.SingleValue;
import com.example.domain.entities.AbstractHistoric;
import com.example.domain.entities.data.DoubleHistoricData;
import com.example.services.core.helper.Helper;
import com.example.services.core.service.SenderService;
import com.example.services.questdb.helper.QuestDBHelper;

public abstract class AbstractAppHistoric<T> {
  private static final String DEFAULT_PATH = ""; //"questdb-demo/storytelling/";
  private static final String DEFAULT_TABLE_NAME = "HistoricData";
  private static final String DEFAULT_FILE_NAME = "QUERY_HISTORIC";
  private static final String DEFAULT_RESULT_FILE_NAME = "RESULTS_HISTORIC";
  private static final String CSV_HEADER = "FASE";

  private static final int DEFAULT_QUANTITY_PER_HISTORIC = 1000000;
  private static final int DEFAULT_DISTINCT_HISTORIC = 1000;
  private static final int DEFAULT_NUMBER_OF_RESULTS = 100000;
  private static final int DEFAULT_TIMEOUT_RETRIES = 50;
  private static final int DEFAULT_NUMBER_OF_EXECUTIONS = 10;
  private static final String DEFAULT_SQL = "SELECT * FROM " + DEFAULT_TABLE_NAME;

  private FileWriter writer = null;
  private FileWriter resultWriter = null;
  private Map<String,List<Integer>> timeResults = null;

  private AppHistoricConfig applicationConfig = null;
  private SenderService<?> senderService = null;

  /*
   * INJECTION OF THE SPECIFIC SENDER SERVICE
   */
  protected abstract SenderService<?> createSenderService(AbstractHistoric<T> historic);

  /*
   * INJECTION OF THE CUSTOM CONFIGURATION PARAMETERS
   */
  protected abstract AppHistoricConfig getApplicationConfig();

  /*
   * INJECTION OF THE HISTORIC CLASS
   */
  protected abstract AbstractHistoric<T> 
    createHistoric(Entity<DoubleHistoricData<T>> entity);

  /*
   * CONFIGURATION PARAMETERS CLASS
   */
  protected class AppHistoricConfig {
    public int numberOfExecutions;
    public String path = "";
    public String fileName = "";
    public String resultFileName = "";
    public String tableName = "";
    public long quantityPerHistoric = 0;
    public long distinctHistorics = 0;
    public int numberOfQueryResults = 0;
    public boolean toExecuteQuery = false;
    public T indicatorId = null;
    public String sql = "";
    public int timeoutRetries = 0;
    public boolean waitForever = false;
    public Long startTimestamp = null;
    public Long endTimestamp = null;

    public AppHistoricConfig() {
      this.numberOfExecutions = DEFAULT_NUMBER_OF_EXECUTIONS;
      this.path = DEFAULT_PATH;
      this.fileName = DEFAULT_PATH + DEFAULT_FILE_NAME;
      this.resultFileName = DEFAULT_PATH + DEFAULT_RESULT_FILE_NAME;
      this.tableName = DEFAULT_TABLE_NAME;
      this.quantityPerHistoric = DEFAULT_QUANTITY_PER_HISTORIC;
      this.distinctHistorics = DEFAULT_DISTINCT_HISTORIC;
      this.numberOfQueryResults = DEFAULT_NUMBER_OF_RESULTS;
      this.toExecuteQuery = false;
      this.indicatorId = null;
      this.sql = DEFAULT_SQL;
      this.timeoutRetries = DEFAULT_TIMEOUT_RETRIES;
      this.waitForever = false;
      this.startTimestamp = null;
      this.endTimestamp = null;
    }

    @Override
    public String toString() {
      return "AppHistoricConfig: {" + "\n" +
        "\tnumberOfExecutions=" + numberOfExecutions + "\n" +
        "\tpath='" + path + "'" + "\n" +
        "\tfileName='" + fileName + "'" + "\n" +
        "\tresultFileName='" + resultFileName + "'" + "\n" +
        "\ttableName='" + tableName + "'" + "\n" +
        "\tquantityPerHistoric=" + quantityPerHistoric + "\n" +
        "\thistoric_groups_qty=" + distinctHistorics + "\n" +
        "\tnumberOfQueryResults=" + numberOfQueryResults + "\n" +
        "\ttoExecuteQuery=" + toExecuteQuery + "\n" +
        "\tsql='" + sql + "'" + "\n" +
        "\ttimeoutRetries=" + timeoutRetries + "\n" +
        "\twaitForever=" + waitForever + "\n" +
        "\tindicatorId=" + indicatorId + "\n" +
        "\tstartTimestamp=" + startTimestamp + "\n" +
        "\tendTimestamp=" + endTimestamp + "\n" +
        '}';
    }
  }

  /*
   * RESULTS PERSISTENCE METHODS
   */

  private void createFile(int idx) throws IOException {
    String fileName = applicationConfig.fileName + "_" + idx + ".txt";

    System.out.println("Creating file: " + fileName);
    writer = new FileWriter(fileName);
    writer.write("Statistics:\n");
    writer.flush();
  }

  private void dumpMessage(String message) throws IOException {
    System.out.println(message);
    writer.append(message + "\n");
    writer.flush();
  }

  private void dumpTimeResultMessage(String timeResultTag, long start) throws IOException {
    if (start > 0) {
      Long end = System.currentTimeMillis();
      Long delta = (end - start);
      dumpMessage(timeResultTag + ": " + delta + " ms");
      addTimeResult(timeResultTag, delta.intValue()); 
    }
    else {
      dumpMessage(timeResultTag + ": ERRO. SEM CONTABILIZAÇÃO DE TEMPO!");
      addTimeResult(timeResultTag, -1); 
    }
  }

  private void dumpDeltaMessage(String timeResultTag, Long delta) throws IOException {
    dumpMessage(timeResultTag + ": " + delta + " ms");
    addTimeResult(timeResultTag, delta.intValue()); 
  }

  private void closeFile() throws IOException {
    writer.close();
  }

  private void createResultFile() throws IOException {
    if (resultWriter == null) {
      String fileName = applicationConfig.resultFileName + ".csv";

      System.out.println("Creating result file: " + fileName);
      resultWriter = new FileWriter(fileName);
    }
  }

  private void storeTimeResults() throws IOException {
    if (timeResults != null && timeResults.size() > 0) {
      createResultFile();
      
      // mount the header
      resultWriter.write(CSV_HEADER);

      // get the first element in the map
      List<Integer> firstResult = timeResults.entrySet().iterator().next().getValue();
      for (int idx = 0; idx < firstResult.size(); idx++) {
        resultWriter.write(";Execução " + (idx+1));
      }
      resultWriter.write("\n");
      resultWriter.flush();

      for (Map.Entry<String,List<Integer>> entry : timeResults.entrySet()) {
        String key = entry.getKey();
        resultWriter.write(key);
        List<Integer> values = entry.getValue();
        for (Integer value : values) {
          resultWriter.write(";" + value);
        }
        resultWriter.write("\n");
        resultWriter.flush();
      }
      resultWriter.close();
    }
  }

  private void addTimeResult(String key, Integer value) throws IOException {
    if (timeResults == null) {
      timeResults = new HashMap<>();
    }

    List<Integer> values = timeResults.get(key);
    if (values == null) {
      values = new ArrayList<>();
    }

    values.add(value);
    timeResults.put(key, values);
  }

  private FileWriter createCSVFile(String fileName) throws IOException {
    System.out.println("Creating CSV file: " + fileName + ".csv");
    FileWriter csvWriter = new FileWriter(fileName+".csv");
    return csvWriter;
  }

  /*
   * GET FIRST INDICATOR ID (RANDOM) - TO FILTER QUERIES RESULTS
   */
  private T getFirstIndicatorId(AbstractHistoric<T> historic) {
    List<SingleValue<DoubleHistoricData<T>>> data = historic.getData();
    SingleValue<DoubleHistoricData<T>> historicValue = data.get(0);
    DoubleHistoricData<T> historicData = historicValue.getValue();
    return historicData.getIndicatorId();
  }

  /*
   * EXECUTE QUERY
   */
  private Connection prepareDB() throws SQLException, IOException {
    long start = System.currentTimeMillis();
    
    Connection connection = QuestDBHelper.getPostgresConnection();

    dumpTimeResultMessage("Preparando o ambiente QuestDB",start);

    return connection;
  }

  private String getLimitClause () {
    String startDatetime = "";
    String endDatetime = "";

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
    startDatetime = dateFormat.format(applicationConfig.startTimestamp / 1000000);
    endDatetime = dateFormat.format(applicationConfig.endTimestamp / 1000000);

    return 
      " and timestamp between " + 
      "'" + startDatetime + "' and '" + endDatetime + "'";
  }

  private String getWhereClause () {
    return " where indicatorId = '" + applicationConfig.indicatorId + "'";
  }

  private long prepareSQLClause () throws IOException {
    long effectiveNumberOfQueryResults = applicationConfig.distinctHistorics * applicationConfig.quantityPerHistoric;
    applicationConfig.sql = "SELECT * FROM " + applicationConfig.tableName + getWhereClause ();

    if (applicationConfig.numberOfQueryResults > 0 && applicationConfig.numberOfQueryResults < effectiveNumberOfQueryResults) {
      applicationConfig.sql = applicationConfig.sql + getLimitClause();
      effectiveNumberOfQueryResults = applicationConfig.numberOfQueryResults;
    }

    dumpMessage("Application Config used to query data:\n" + applicationConfig);

    return effectiveNumberOfQueryResults;
  }

  private void executeQuery() throws IOException, SQLException {
    long effectiveNumberOfQueryResults = prepareSQLClause();

    Boolean hasAbnormalTermination = false;
    do {
      Connection connection = prepareDB();
      try (PreparedStatement preparedStatement = 
              connection.prepareStatement(
                applicationConfig.sql,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
      )) {
        int retriesCount = 0;
        int recordsReaded = 0;
        
        System.out.print("Waiting: ");
        ResultSet rs = null;
        
        Boolean timeoutTermination = false;
        long startPrepare = -1;
        long startWait = -1;
        do {
          startPrepare = System.currentTimeMillis();
          try {
            rs = preparedStatement.executeQuery();
          } 
          catch (Exception e) {
            hasAbnormalTermination = true;
            dumpMessage("Erro na consulta: preparedStatement.executeQuery::" + e.getMessage());
          }

          if (!hasAbnormalTermination) {
            dumpTimeResultMessage("Aguardando preparedStatement.executeQuery()",startPrepare);

            if (startWait == -1) startWait = System.currentTimeMillis();
            if (rs != null) {
              rs.last();
              recordsReaded = rs.getRow();
            }

            if (rs == null || recordsReaded < effectiveNumberOfQueryResults) {
              Helper.sleep(1000);
              System.out.print(".");
              retriesCount++;
            }

            if (!applicationConfig.waitForever && applicationConfig.timeoutRetries > 0) {
              timeoutTermination = (retriesCount >= applicationConfig.timeoutRetries);
            }
          }
        } while (!hasAbnormalTermination && !timeoutTermination && recordsReaded < effectiveNumberOfQueryResults);

        if (!hasAbnormalTermination && !timeoutTermination) {
          System.out.println("x");

          dumpTimeResultMessage("Aguardando o fim das inserções no QuestDB",startWait);
          dumpMessage("Fetching: " + effectiveNumberOfQueryResults + " records.");
        
          long startQuery = System.currentTimeMillis();

          recordsReaded = 0;
          rs.beforeFirst();

          // int count = 0;
          while (rs.next()) {
            String indicatorId = rs.getString("indicatorId");
            T var = null;
            if (var instanceof UUID) {
              UUID uuid = UUID.fromString(indicatorId);
            }
            else if (var instanceof Long) {
              Long longId = Long.parseLong(indicatorId);
            }

            Double value = rs.getDouble("value");
            long timestamp = rs.getTimestamp("timestamp").getTime();

            // dumpMessage("("+(count++)+") "+
            //   "indicatorId="+indicatorId+", value="+value+", timestamp="+timestamp);
            // count++;
            recordsReaded++;
            System.out.print(".");
          }
          
          dumpTimeResultMessage("\nConsultando todos os registros do critério",startQuery);
          
          System.out.println("x");
          dumpMessage("Fetched " + recordsReaded + " records.");
          applicationConfig.waitForever = false;
        } 
        else {
          if (hasAbnormalTermination) dumpMessage("Abnormal termination: WaitForever = " + applicationConfig.waitForever);
          if (timeoutTermination) dumpMessage("Timeout termination: WaitForever = " + applicationConfig.waitForever);
          if (!applicationConfig.waitForever) {
            dumpTimeResultMessage("Aguardando preparedStatement.executeQuery()",startPrepare);
            dumpTimeResultMessage("Aguardando o fim das inserções no QuestDB",-1);
            dumpTimeResultMessage("Consultando todos os registros do critério",-1);
          }
        }

        connection.close();
      }
    } while (applicationConfig.waitForever);
  }

  /*
   * CREATE HISTORIC SENDER SERVICE
   */
  private void sendHistoric(AbstractHistoric<T> historic) throws IOException {
    senderService = createSenderService(historic);
    senderService.execute();
  }

  private void getFirstIndicators(AbstractHistoric<T> historic) {
    if (applicationConfig.indicatorId == null) 
      applicationConfig.indicatorId = getFirstIndicatorId(historic);
    
    if (applicationConfig.startTimestamp == null) 
      applicationConfig.startTimestamp = historic.getFirstTimeStamp();
    
    if (applicationConfig.endTimestamp == null) 
      applicationConfig.endTimestamp = historic.getLastTimeStamp(applicationConfig.numberOfQueryResults);
  }
  
  /*
   * CREATE HISTORIC
   */
  private AbstractHistoric<T> createDistinctHistoric() throws IOException {
    List<SingleValue<DoubleHistoricData<T>>> historicArray = 
      new ArrayList<>((int) applicationConfig.quantityPerHistoric);
      
    for (int i = 0; i < applicationConfig.quantityPerHistoric; i++) {
      Long timestamp = SingleValue.getNow();
      Double value = Helper.generateRandomValue();

      DoubleHistoricData<T> historicData = new DoubleHistoricData<T>(timestamp, value);
      SingleValue<DoubleHistoricData<T>> historicValue = new SingleValue<>(historicData);
      historicArray.add(historicValue);
    }

    Entity<DoubleHistoricData<T>> entity = new Entity<>(applicationConfig.tableName, historicArray);
    AbstractHistoric<T> historic = createHistoric(entity);

    // get first uuid to use as indicatorId in the query
    getFirstIndicators(historic);

    return historic;
  }

  /*
   * PROCESS
   */
  private void processHistoric(int idx) throws IOException, SQLException {
    long totalTimeToCreate = 0L;
    long totalTimeToSend = 0L;

    FileWriter processHistoricFile = createCSVFile(applicationConfig.resultFileName + "_DETAILS_" + idx);

    String header = "Execução #" + idx;
    String timeCreate = "CREATE";
    String timeSend = "SEND";

    for (int i = 0; i < applicationConfig.distinctHistorics; i++) {
      System.out.println(
        applicationConfig.resultFileName + 
        "::Historic proccess cycle #" + (i+1) + " of " + 
        applicationConfig.distinctHistorics);

      header += ";" + (i+1);

      long startCreate = System.currentTimeMillis();
      AbstractHistoric<T> historic = createDistinctHistoric();
      long durationCreate = System.currentTimeMillis() - startCreate;
      totalTimeToCreate += durationCreate;

      timeCreate += ";" + durationCreate;

      long startSend = System.currentTimeMillis();
      sendHistoric(historic);
      long durationSend = System.currentTimeMillis() - startSend;
      totalTimeToSend += durationSend;

      timeSend += ";" + durationSend;
    }

    header += "\n";
    timeCreate += "\n";
    timeSend += "\n";

    processHistoricFile.write(header);
    processHistoricFile.write(timeCreate);
    processHistoricFile.write(timeSend);

    processHistoricFile.flush();
    processHistoricFile.close();

    dumpDeltaMessage(
      "Criando todos os registros de histórico",totalTimeToCreate);
    dumpDeltaMessage(
      "Enviando todos os registros de histórico",totalTimeToSend);

    if (applicationConfig.toExecuteQuery) {
      executeQuery();
    }
  }

  public void execute() throws IOException, SQLException {
    applicationConfig = getApplicationConfig();
    for (int idx = 0; idx < applicationConfig.numberOfExecutions; idx++) {
      createFile(idx+1);
      processHistoric(idx+1);
      closeFile();
    }

    System.out.println("Process finished. Storing time results...");
    storeTimeResults();
    System.out.println("Time results successfully stored.");
  }
}
