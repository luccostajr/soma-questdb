package com.example.domain.core;

import java.time.Instant;

public class SingleValue<T> {
  private T value;
  private long timestamp;
  private static Long now = null;
  private static Long counter = 0L;

  public static Long getNow() {
    if (now == null) {
      now = Instant.now().toEpochMilli();
    }
    return (now + counter++) * 1000000;
  }

  public SingleValue(T value) {
    this.value = value;
    this.timestamp = getNow();
  }

  public SingleValue(T value, long timestamp) {
    this.value = value;
    this.timestamp = timestamp;
  }

  public T getValue() {
    return value;
  }

  public long getTimestamp() {
    return timestamp;
  }
}
