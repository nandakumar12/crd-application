package com.nandakumar12.crd.app.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This is the model of data that is to be persisted in the data store
 * {@link Data#value} will hold the json value
 * {@link Data#timeToLive} will hold the expiry timestamp of the data
 *
 * @author  Nandakumar12
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@lombok.Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Data {
  String value;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  String timeToLive;
}
