package com.andersenlab.etalon.infoservice.util;

import com.andersenlab.etalon.infoservice.util.enums.FileExtension;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FileUtils {

  public static String fixSpaces(String name) {
    return name.replace(" .", ".").replace(" ", "_");
  }

  public static String getFilename(String filename, FileExtension extension) {
    return filename.concat(extension.getExtension());
  }
}
