package com.andersenlab.etalon.infoservice.util;

import java.util.ArrayList;
import java.util.List;

public class SplitListUtils {

  private SplitListUtils() {}

  public static <T> List<List<T>> splitList(List<T> list, int batchSize) {
    List<List<T>> batches = new ArrayList<>();
    for (int i = 0; i < list.size(); i += batchSize) {
      int end = Math.min(i + batchSize, list.size());
      batches.add(list.subList(i, end));
    }
    return batches;
  }
}
