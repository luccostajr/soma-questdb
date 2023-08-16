package br.cepel.questdb.services;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.cepel.questdb.data.IndicatorData;
import br.cepel.questdb.domain.entities.IndicatorEntity;
import br.cepel.questdb.domain.helper.IndicatorHelper;
import br.cepel.questdb.domain.historic.IndicatorHistoric;
import br.cepel.questdb.services.config.ServicesConfig;
import br.cepel.questdb.services.helper.ServicesHelper;
import br.cepel.questdb.services.properties.DbProperties;
import io.questdb.client.Sender;

public class IndicatorSymbolQuestDbService extends QuestDBService {
  private ServicesConfig servicesConfig = null;

  public IndicatorSymbolQuestDbService(ServicesConfig servicesConfig) {
    super();
    this.servicesConfig = servicesConfig;
  }

  public ServicesConfig getServicesConfig() {
    return servicesConfig;
  }

  /*
   * INSERT INTO QUESTDB USING INFLUX
   */
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
  
  /*
   * GET FIRST INDICATOR ID (RANDOM) - TO FILTER QUERIES RESULTS
   */
  protected Long getFirstIndicatorId(IndicatorHistoric historic) {
    List<IndicatorData> data = historic.getData();
    IndicatorData indicatorData = data.get(0);
    return indicatorData.getIndicatorId();
  }

  /*
   * EXECUTE QUERY
   */
  private Connection prepareDB() throws SQLException, IOException {
    //long start = System.currentTimeMillis();
    
    Connection connection = DbProperties.getPostgresConnection();

    //dumpTimeResultMessage("Preparando o ambiente QuestDB",start);

    return connection;
  }

  private String getLimitClause () {
    String startDatetime = "";
    String endDatetime = "";

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
    startDatetime = dateFormat.format(servicesConfig.startTimestamp / 1000000);
    endDatetime = dateFormat.format(servicesConfig.endTimestamp / 1000000);

    return 
      " and timestamp between " + 
      "'" + startDatetime + "' and '" + endDatetime + "'";
  }

  private String getWhereClause () {
    return " where indicatorId = '" + servicesConfig.indicatorId + "'";
  }

  private long prepareSQLClause (FileWriter logWriter) throws IOException {
    long effectiveNumberOfQueryResults = servicesConfig.distinctHistorics * servicesConfig.quantityPerHistoric;
    servicesConfig.sql = "SELECT * FROM " + servicesConfig.tableName + getWhereClause ();

    if (servicesConfig.numberOfQueryResults > 0 && servicesConfig.numberOfQueryResults < effectiveNumberOfQueryResults) {
      servicesConfig.sql = servicesConfig.sql + getLimitClause();
      effectiveNumberOfQueryResults = servicesConfig.numberOfQueryResults;
    }

    ServicesHelper.dumpMessage(logWriter, "Application Config used to query data:\n" + servicesConfig);

    return effectiveNumberOfQueryResults;
  }

  public void executeQuery(FileWriter logWriter, FileWriter resultsWriter) throws IOException, SQLException {
    long effectiveNumberOfQueryResults = prepareSQLClause(logWriter);

    Boolean hasAbnormalTermination = false;
    do {
      Connection connection = prepareDB();
      try (PreparedStatement preparedStatement = 
              connection.prepareStatement(
                servicesConfig.sql,
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
            ServicesHelper.dumpMessage(
              logWriter, "Erro na consulta: preparedStatement.executeQuery::" + e.getMessage());
          }

          if (!hasAbnormalTermination) {
            ServicesHelper.dumpTimeResultMessage(
              logWriter, "Aguardando preparedStatement.executeQuery()", 
              startPrepare);

            if (startWait == -1) startWait = System.currentTimeMillis();
            if (rs != null) {
              rs.last();
              recordsReaded = rs.getRow();
            }

            if (rs == null || recordsReaded < effectiveNumberOfQueryResults) {
              IndicatorHelper.sleep(1000);
              System.out.print(".");
              retriesCount++;
            }

            if (!servicesConfig.waitForever && servicesConfig.timeoutRetries > 0) {
              timeoutTermination = (retriesCount >= servicesConfig.timeoutRetries);
            }
          }
        } while (!hasAbnormalTermination && !timeoutTermination && recordsReaded < effectiveNumberOfQueryResults);

        if (!hasAbnormalTermination && !timeoutTermination) {
          System.out.println("x");

          ServicesHelper.dumpTimeResultMessage(
            logWriter, "Aguardando o fim das inserções no QuestDB", startWait);
          
          ServicesHelper.dumpMessage(
            logWriter, "Fetching: " + effectiveNumberOfQueryResults + " records.");
        
          long startQuery = System.currentTimeMillis();

          recordsReaded = 0;
          rs.beforeFirst();

          // int count = 0;
          while (rs.next()) {
            String indicatorId = rs.getString("indicatorId");
            Long longId = Long.parseLong(indicatorId);

            Double value = rs.getDouble("value");
            long timestamp = rs.getTimestamp("timestamp").getTime();

            // dumpMessage("("+(count++)+") "+
            //   "indicatorId="+indicatorId+", value="+value+", timestamp="+timestamp);
            // count++;
            recordsReaded++;
            System.out.print(".");
          }
          
          ServicesHelper.dumpTimeResultMessage(
            logWriter, "Consultando todos os registros do critério", startQuery);
          
          System.out.println("x");
          ServicesHelper.dumpMessage(
            logWriter, "Fetched " + recordsReaded + " records.");
          servicesConfig.waitForever = false;
        } 
        else {
          if (hasAbnormalTermination) ServicesHelper.dumpMessage(
              logWriter, "Abnormal termination: WaitForever = " + servicesConfig.waitForever);
          
          if (timeoutTermination) ServicesHelper.dumpMessage(
              logWriter, "Timeout termination: WaitForever = " + servicesConfig.waitForever);
          
          if (!servicesConfig.waitForever) {
            ServicesHelper.dumpTimeResultMessage(
              logWriter, "Aguardando preparedStatement.executeQuery()",startPrepare);
            ServicesHelper.dumpTimeResultMessage(
              logWriter, "Aguardando o fim das inserções no QuestDB",-1);
            ServicesHelper.dumpTimeResultMessage(
              logWriter, "Consultando todos os registros do critério",-1);
          }
        }

        connection.close();
      }
    } while (servicesConfig.waitForever);
  }

  private void getFirstIndicators(IndicatorHistoric historic) {
    if (servicesConfig.indicatorId == null) 
      servicesConfig.indicatorId = getFirstIndicatorId(historic);
    
    if (servicesConfig.startTimestamp == null) 
      servicesConfig.startTimestamp = historic.getFirstTimeStamp();
    
    if (servicesConfig.endTimestamp == null) 
      servicesConfig.endTimestamp = historic.getLastTimeStamp(servicesConfig.numberOfQueryResults);
  }
  
  /*
   * CREATE HISTORIC
   */
  public IndicatorHistoric createDistinctHistoric() throws IOException {
    List<IndicatorData> historicArray = 
      new ArrayList<>((int) servicesConfig.quantityPerHistoric);
      
    for (int i = 0; i < servicesConfig.quantityPerHistoric; i++) {
      Long timestamp = IndicatorHelper.getNow();
      Double value = IndicatorHelper.generateRandomValue();

      IndicatorData indicatorData = new IndicatorData ();
      indicatorData.setDate(timestamp);
      indicatorData.setValue(value);
      indicatorData.setIndicatorId(0L);

      historicArray.add(indicatorData);
    }

    IndicatorEntity entity = new IndicatorEntity(servicesConfig.tableName, historicArray);
    IndicatorHistoric historic = new IndicatorHistoric(entity);

    // get first uuid to use as indicatorId in the query
    getFirstIndicators(historic);

    return historic;
  }
}
