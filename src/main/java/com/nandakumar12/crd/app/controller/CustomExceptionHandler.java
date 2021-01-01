package com.nandakumar12.crd.app.controller;

import com.nandakumar12.crd.app.exception.CrdException;
import com.nandakumar12.crd.app.exception.InvalidDataException;
import com.nandakumar12.crd.app.model.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


/**
 * This class contains the logic for handling exceptions that arises
 * in the presentation layer (controller) {@link com.nandakumar12.crd.app.controller.CrdController}
 * the methods present here will handle appropriate Exception thrown from controller
 * and creates ResponseEntity with a valid message
 *
 * @author  Nandakumar12
 */
@ControllerAdvice(assignableTypes = CrdController.class)
@Slf4j
public class CustomExceptionHandler {

  /**
   * This method will take care of {@link com.nandakumar12.crd.app.exception.InvalidDataException}
   * that is thrown from the controller and acknowledges the user as an
   * bad request
   *
   * @param exception This is instance of the InvalidDataException which will be automatically injected by spring
   *
   * @return ResponseEntity<Object> This is the responseEntity with an status code of 400 and appropriate error message
   *
   */
  @ExceptionHandler(InvalidDataException.class)
  public ResponseEntity<Object> handleInvalidDataException(InvalidDataException exception) {
    log.error(exception.getMessage());
    return ResponseEntity.badRequest().body(new ResponseMessage(getCause(exception).getMessage()));
  }


  /**
   * This method will take care of {@link com.nandakumar12.crd.app.exception.CrdException} and all its child exceptions
   * that is thrown from the controller and acknowledges the user with useful message info
   *
   * @param exception This is instance of the CrdException which will be automatically injected by spring
   *
   * @return ResponseEntity<Object> This is the responseEntity with an status code of 200 and appropriate error message
   *
   */
  @ExceptionHandler(CrdException.class)
  public ResponseEntity<Object> handleException(Exception exception) {
    log.error(exception.getMessage());
    return ResponseEntity.ok().body(new ResponseMessage(getCause(exception).getMessage()));
  }

  Throwable getCause(Throwable e){
    Throwable cause = null;
    Throwable res = e;
    while ((cause=res.getCause())!=null && res!=cause){
      res=cause;
    }
    return res;
  }
}
