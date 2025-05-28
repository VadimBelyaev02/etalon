package com.andersenlab.etalon.infoservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TechnicalException extends RuntimeException {

  public static final String ERROR_CREATING_PDF = "An error occurred while creating PDF file";
  public static final String ERROR_CREATING_EXCEL = "An error occurred while creating EXCEL file";
  public static final String ERROR_READING_WRITING_FILE =
      "An error occurred while reading or writing a PDF/EXCEL file";
  public static final String FAILED_DELETE_TEMPORARY_FILE = "Failed to delete temporary file: %s";
  public static final String ERROR_CREATING_IMAGE_FILE =
      "An error occurred while creating image file for pdf";
  public static final String ERROR_LOADING_IMAGE_FILE =
      "An error occurred while loading image file for excel";
  public static final String ERROR_USING_STATEMENT_TYPE =
      "An error occurred while using this statement type. This statement type %s is not supported";
  public static final String NO_CONFIRMATION_STRATEGY_OPERATION =
      "No confirmation strategy found for this operation: %s";
  public static final String CONFIRMATION_REQUEST_NOT_FOUND_BY_ID =
      "Confirmation request with id %s is not found";
  public static final String USER_ID_CAN_NOT_BE_BLANK = "User id can not be blank";
  public static final String ERROR_DURING_DB_SCHEMA_INIT =
      "Error during DB schema initialization: %s";
  public static final String ERROR_DURING_READING_DATA_FROM_URL =
      "An error occurred while reading data from URL %s";
  public static final String FILE_PARSING_ERROR = "Error parsing file into bytes";
  public static final String LOCALE_NOT_FOUND = "No translation found for locale: %s";
  private final HttpStatus httpStatus;

  public TechnicalException(final HttpStatus httpStatus, final String message) {
    super(message);
    this.httpStatus = httpStatus;
  }
}
