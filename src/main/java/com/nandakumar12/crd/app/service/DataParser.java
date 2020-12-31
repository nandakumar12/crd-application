package com.nandakumar12.crd.app.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nandakumar12.crd.app.exception.DuplicateKeyException;
import com.nandakumar12.crd.app.exception.InvalidDataException;
import com.nandakumar12.crd.app.model.Data;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

/**
 * This is an helper class which contains few methods which will
 * be used to parse and validate the data before storing it into the
 * datastore
 *
 * @author  Nandakumar12
 */
@Service
@Slf4j
@AllArgsConstructor
public class DataParser {
  private final DataService dataService;
  static final String TIME_TO_LIVE = "timeToLive";

  /**
   * This method will parse the json data provided to extract the timeToLive property and
   * validate it
   *
   * @param key this is the key of the respective value to be stored
   * @param bodyData this is the value which is to be stored
   *
   * @return Data This is the data that will be stored once it passes all validity checks
   * @throws DuplicateKeyException if the key is already taken
   * @throws ExecutionException if the future completed with some exception
   * @throws InterruptedException if the thread that is waiting or sleeping is interrupted
   * @throws InvalidDataException if the provided data fails any validity check
   *
   */
  public Data parse(String key, String bodyData)
          throws DuplicateKeyException, InterruptedException, InvalidDataException, ExecutionException {
    if (bodyData.getBytes(StandardCharsets.UTF_8).length / 1024 > 16) {
      throw new InvalidDataException("Data size exceeded 16Kb");
    }
    Gson gson = new Gson();
    JsonObject jsonObject = JsonParser.parseString(bodyData).getAsJsonObject();
    Data data = null;
    if (jsonObject.get(TIME_TO_LIVE) == null) {
      data = new Data(gson.toJson(jsonObject), "0");
    } else if (checkTTLValidity(jsonObject.get(TIME_TO_LIVE).getAsString())) {
      String ttl = jsonObject.get(TIME_TO_LIVE).getAsString();
      jsonObject.remove(TIME_TO_LIVE);
      data = new Data(gson.toJson(jsonObject), ttl);
    }
    Data newData = dataService.addData(key, data).get();
    log.info("The new data is " + newData);
    return newData;
  }

  boolean checkTTLValidity(String ttl) throws InvalidDataException {
    try {
      Long.parseLong(ttl);
    } catch (NumberFormatException e) {
      throw new InvalidDataException("The ttl is not an valid number");
    }
    return true;
  }
}
