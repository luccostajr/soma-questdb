package br.cepel.questdb.services.helper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicesHelper {
  private static final String CSV_HEADER = "FASE";
  private static Map<String,List<Integer>> timeResults = null;

  public static FileWriter createFile(String fileName) throws IOException {
    FileWriter writer = new FileWriter(fileName);
    return writer;
  }

  public static void dumpMessage(FileWriter writer, String message) throws IOException {
    if (message!= null) {
      System.out.println(message);

      if (writer != null) {
        writer.append(message + "\n");
        writer.flush();
      }
    }
  }

  public static void dumpTimeResultMessage(FileWriter writer, String message, long start) throws IOException {
    if (writer != null && start > 0) {
      Long end = System.currentTimeMillis();
      Long delta = (end - start);
      dumpMessage(writer, message + ": " + delta + " ms");
      addTimeResult(message, delta.intValue()); 
    }
  }

  private static void addTimeResult(String key, Integer value) throws IOException {
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

  public static void storeTimeResults(String fileName) throws IOException {
    if (timeResults != null && timeResults.size() > 0) {
      System.out.println("Creating result file: " + fileName);
      FileWriter resultWriter = ServicesHelper.createFile(fileName);
      
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

  public static void dumpDeltaMessage(FileWriter writer, String timeResultTag, Long delta) throws IOException {
    dumpMessage(writer, timeResultTag + ": " + delta + " ms");
    addTimeResult(timeResultTag, delta.intValue()); 
  }
}
