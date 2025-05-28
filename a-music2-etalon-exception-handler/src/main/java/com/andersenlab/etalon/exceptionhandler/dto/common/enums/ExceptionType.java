package com.andersenlab.etalon.exceptionhandler.dto.common.enums;

import com.andersenlab.etalon.exceptionhandler.exception.BusinessException;
import com.andersenlab.etalon.exceptionhandler.exception.EnumConversionException;
import com.andersenlab.etalon.exceptionhandler.exception.GenericException;
import com.andersenlab.etalon.exceptionhandler.exception.NotFoundException;
import com.andersenlab.etalon.exceptionhandler.exception.TechnicalException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionType {
  BUSINESS(HttpStatus.BAD_REQUEST) {
    @Override
    public GenericException toException(String msg, HttpStatus status) {
      return new BusinessException(msg);
    }
  },
  ENUM_CONVERSION(HttpStatus.BAD_REQUEST) {
    @Override
    public GenericException toException(String msg, HttpStatus status) {
      return new EnumConversionException(msg);
    }
  },
  TECHNICAL(HttpStatus.BAD_REQUEST) {
    @Override
    public GenericException toException(String msg, HttpStatus status) {
      return new TechnicalException(msg);
    }
  },
  NOT_FOUND(HttpStatus.NOT_FOUND) {
    @Override
    public GenericException toException(String msg, HttpStatus status) {
      return new NotFoundException(msg);
    }
  },
  ;

  private final HttpStatus status;

  public abstract GenericException toException(String message, HttpStatus status);
}
