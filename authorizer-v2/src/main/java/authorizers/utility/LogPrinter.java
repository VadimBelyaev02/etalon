package authorizers.utility;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LogPrinter {

  public void toPrint(String text, boolean isLogsEnabled) {
    if (isLogsEnabled) {
      System.out.println(text);
    }
  }
}
