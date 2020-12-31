package com.nandakumar12.crd.app.service;

import com.nandakumar12.crd.app.exception.DataNotFoundException;
import com.nandakumar12.crd.app.exception.DuplicateKeyException;
import com.nandakumar12.crd.app.exception.InvalidDataException;
import com.nandakumar12.crd.app.exception.ValueExpiredException;
import com.nandakumar12.crd.app.model.Data;
import com.nandakumar12.crd.app.repository.DataRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * This is an service class which will be used to access the data present in
 * the datastore, all the methods present in the class are asynchronous which will
 * use the multi-thread configuration provided in {@link com.nandakumar12.crd.app.config.AppConfig}
 *
 * @author  Nandakumar12
 */
@Service
@Slf4j
@AllArgsConstructor
public class DataService {
  private final DataRepository dataRepository;

  /**
   * This method has the logic for reading the data from data store
   *
   * @param key this is the key of the respective value which should be retrieved
   *
   * @return CompletableFuture<Data>  This the data paired with the given key
   *                                   which will be completed sometime in the future
   * @throws ValueExpiredException if the data we are trying to access is expired
   * @throws DataNotFoundException if no data is paired with the given key
   *
   */
  @Async
  public CompletableFuture<Data> readData(String key)
      throws ValueExpiredException, DataNotFoundException {
    Optional<Data> data = dataRepository.findByKey(key);
    Data foundData = null;
    if (data.isPresent() && isValid(data.get(), key)) {
      foundData = data.get();
    }
    if (foundData == null) {
      throw new DataNotFoundException("Data not found");
    }
    return CompletableFuture.completedFuture(foundData);
  }

  /**
   * This method will safely delete the data which is paired with the provided key
   *
   * @param key this is the key of the respective value which should be deleted
   *
   * @return CompletableFuture<Data>  This the deleted data which will be completed sometime in the future
   * @throws ValueExpiredException if the data we are trying to access is expired
   * @throws DataNotFoundException if no data is paired with the given key
   *
   */
  @Async
  public CompletableFuture<Data> deleteData(String key)
      throws DataNotFoundException, ValueExpiredException {
    Optional<Data> data = dataRepository.findByKey(key);
    log.info("Exec data service");
    Data foundData = null;
    if (data.isPresent() && isValid(data.get(), key)) {
      foundData = dataRepository.deleteByKey(key);
    }
    if (foundData == null) {
      throw new DataNotFoundException("data not found");
    }
    return CompletableFuture.completedFuture(foundData);
  }

  /**
   * This method will add the data in the datastore and will be paired with the given key
   *
   * @param key this is the key of the respective value which should be stored
   * @param value this is the body of the value which is to be stored
   *
   * @return CompletableFuture<Data>  This the stored data which will be completed sometime in the future
   * @throws DuplicateKeyException if the provided key is already taken
   * @throws InvalidDataException if the provided fails for any validity checks
   *
   */
  @Async
  public CompletableFuture<Data> addData(String key, Data value)
      throws DuplicateKeyException, InvalidDataException {
    if (key != null && value != null) {
      if (dataRepository.exits(key)) {
        throw new DuplicateKeyException("Key already exists !");
      }
      log.info("The ttl is " + value.getTimeToLive());
      return CompletableFuture.completedFuture(dataRepository.createData(key, value));
    } else {
      throw new InvalidDataException("Key or Data can't be null");
    }
  }

  public boolean isValid(Data data, String key)
      throws DataNotFoundException, ValueExpiredException {
    long currentTimeStamp = System.currentTimeMillis();
    long expireTime = Long.parseLong(data.getTimeToLive());
    if (expireTime != 0 && expireTime < currentTimeStamp) {
      dataRepository.deleteByKey(key);
      throw new ValueExpiredException("The value has expired");
    }
    return true;
  }
}
