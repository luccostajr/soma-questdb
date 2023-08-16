package com.example.domain.core;

import java.util.List;
import java.util.Random;

public class Entity<T> {
  private final String DEFAULT_NAME = "default";
  private static Random random = new Random();

  protected String name = null;
  protected List<SingleValue<T>> data = null;

  public Entity (String name, List<SingleValue<T>> data) {
    if (data == null) throw new IllegalArgumentException("DefaultEntity::ctor: data list cannot be null");
    if (name == null) name = DEFAULT_NAME + ":" + Math.abs(random.nextInt());
    
    this.name = name;
    this.data = data;
  }

  public Entity (Entity<T> entity) {
    if (entity == null) throw new IllegalArgumentException("DefaultEntity::ctor: entity cannot be null");

    String name = entity.getName();
    if (name == null) name = DEFAULT_NAME + ":" + Math.abs(random.nextInt());
    
    this.name = name;
    this.data = entity.getData();
  }

  public List<SingleValue<T>> getData () {
    return data;
  }

  public List<SingleValue<T>> getData (Integer start, Integer end) {
    if (start == null || start < 0) {
        start = 0;
    }
    
    if (end == null || end > data.size()) {
        end = data.size();
    }
    
    return data.subList(start, end);
  }

  public String getName() {
      return name;
  }
}
