package br.cepel.questdb.services.config;

/*
  * CONFIGURATION PARAMETERS CLASS
  */
public class ServicesConfig {
  private static final String DEFAULT_PATH = "./storytelling/";
  private static final String DEFAULT_TABLE_NAME = "HistoricData";
  private static final String DEFAULT_FILE_NAME = "QUERY_HISTORIC";
  private static final String DEFAULT_RESULT_FILE_NAME = "RESULTS_HISTORIC";

  private static final int DEFAULT_QUANTITY_PER_HISTORIC = 1000000;
  private static final int DEFAULT_DISTINCT_HISTORIC = 1000;
  private static final int DEFAULT_NUMBER_OF_RESULTS = 100000;
  private static final int DEFAULT_TIMEOUT_RETRIES = 50;
  private static final int DEFAULT_NUMBER_OF_EXECUTIONS = 10;
  private static final String DEFAULT_SQL = "SELECT * FROM " + DEFAULT_TABLE_NAME;

  public int numberOfExecutions;
  public String path = "";
  public String fileName = "";
  public String resultFileName = "";
  public String tableName = "";
  public long quantityPerHistoric = 0;
  public long distinctHistorics = 0;
  public int numberOfQueryResults = 0;
  public boolean toExecuteQuery = false;
  public Long indicatorId = null;
  public String sql = "";
  public int timeoutRetries = 0;
  public boolean waitForever = false;
  public Long startTimestamp = null;
  public Long endTimestamp = null;
  public long totalTimeToCreate = 0;
  public long totalTimeToSend = 0;

  public ServicesConfig() {
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
      "\ttotalTimeToCreate=" + totalTimeToCreate + "\n" +
      "\ttotalTimeToSend=" + totalTimeToSend + "\n" +
      '}';
  }
}
