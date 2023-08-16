package br.cepel.questdb.data;

public class IndicatorData extends DefaultData<Double,Long> {
  public IndicatorData(Long codTendencia, Double valor, Long data) {
    super(data, valor, codTendencia);
  }

  public IndicatorData() {
    super(-1L, -1.0, -1L);
  }
}
