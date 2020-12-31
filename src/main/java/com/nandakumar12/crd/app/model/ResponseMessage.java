package com.nandakumar12.crd.app.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * This is the an helper model which will used to construct some valid
 * response message, this class will be automatically serialized by jackson api
 *
 * @author  Nandakumar12
 */
@Getter
@Setter
@AllArgsConstructor
public class ResponseMessage {
  String message;
}
