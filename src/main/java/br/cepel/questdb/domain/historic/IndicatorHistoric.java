package br.cepel.questdb.domain.historic;

import java.util.List;
import java.util.Random;

import br.cepel.questdb.data.IndicatorData;
import br.cepel.questdb.domain.entities.IndicatorEntity;

public class IndicatorHistoric extends IndicatorEntity {
  private Long indicatorId = null;
  private static Long firstTimeStamp = null;
  private static Random random = new Random();

  public IndicatorHistoric(IndicatorEntity entity, Long indicatorId) { 
    super(entity);
    
    this.indicatorId = getNewIndicatorId();
    
    Long newIndicatorId = indicatorId;
    if (newIndicatorId == null) newIndicatorId = getNewIndicatorId();
    
    List<IndicatorData> data = entity.getData();
    System.out.println(data.size());

    for (IndicatorData indicatorData : data) {
      if (firstTimeStamp == null) firstTimeStamp = indicatorData.getDate();
      indicatorData.setIndicatorId(newIndicatorId);
    }
  }

  public IndicatorHistoric(IndicatorEntity entity) {
    this(entity, null);
  }

  public Long getIndicatorId() {
    return indicatorId;
  }

  public Long getFirstTimeStamp() {
    return firstTimeStamp;
  }

  public Long getLastTimeStamp(int limit) {
    if (limit > getData().size()) limit = getData().size();
    
    Long lastTimeStamp = null;
    int counter = 0;
    
    for (IndicatorData indicatorData : getData()) {
      if (counter == limit) {
        lastTimeStamp = indicatorData.getDate();
        break;
      }
      counter++;
    }
    
    return lastTimeStamp;
  }

  protected Long getNewIndicatorId() {
    random.setSeed(System.currentTimeMillis());
    return Math.abs(random.nextLong());
  }
}
