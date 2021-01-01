package com.nandakumar12.crd.app.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nandakumar12.crd.app.exception.DataStoreSizeExceeded;
import com.nandakumar12.crd.app.exception.DataNotFoundException;
import com.nandakumar12.crd.app.model.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is the utility class which we will use to interact with our datastore (currently file)
 *
 * @author  Nandakumar12
 */
@Repository
@Slf4j
public class DataRepository {

  @Autowired ApplicationContext applicationContext;

  @Value("${application.file-path:./}")
  String filePath;

  Map<String, Data> data = null;

  /**
   * This method will be automatically invoked by spring after the bean creation
   * this will read the data from datastore and initialize into in-memory variable {@link DataRepository#data}
   * for faster access
   *
   *
   * @return nothing
   * @throws IOException if some error occurs during reading the file
   * @throws DataStoreSizeExceeded if the size of the data is greater than 1GB
   */
  @PostConstruct
  void initializeData() throws IOException, DataStoreSizeExceeded {
    File file = new File(filePath + "\\data.json");
    if (file.createNewFile()) {
      log.info("File is created!");
    } else {
      if ((double) file.length() / (1024 * 1024) > 1024) {
        throw new DataStoreSizeExceeded("The data store size is greater than 1Gb");
      }
      Gson gson = new Gson();
      log.info("File already exists.");
      Type type = new TypeToken<ConcurrentHashMap<String, Data>>() {}.getType();
      data = gson.fromJson(new FileReader(file), type);
    }
    if(data==null){
      data = new ConcurrentHashMap<>();
    }
  }

  /**
   * This method will be automatically invoked during the destruction of bean spring will
   * automatically inject an shutdown hook to jvm to invoke this method this will serialize the data
   * present in {@link DataRepository#data} and store it in the data store
   *
   * @return nothing
   * @throws IOException if some error occurs during writing the file
   * @throws DataStoreSizeExceeded if the size of the data is to be written exceeds 1GB
   */
  @PreDestroy
  void writeDataToFile() throws IOException, DataStoreSizeExceeded {
    Gson gson = new Gson();
    log.info("writing data to data store..");
    String json = gson.toJson(data);
    if (json.getBytes(StandardCharsets.UTF_8).length / (1024 * 1024) > 1024) {
      throw new DataStoreSizeExceeded("The data store size is greater than 1Gb");
    }
    if(data!=null){
      try (FileWriter fw = new FileWriter(filePath + "\\data.json")) {
        fw.write(json);
      }
    }

  }

  /**
   * This method will read and return the respective value for the provided key
   *
   * @param key this is the key of the respective value to be retrieved
   *
   * @return Optional<Data> this the respective data for the given key that is enclosed in a optional
   *
   */
  public Optional<Data> findByKey(String key) {
    return Optional.ofNullable(data.get(key));
  }


  /**
   * This method will delete and return the respective value for the provided key
   * from the datastore
   *
   * @param key this is the key of the respective value to be deleted
   *
   * @return Data this the respective data for the given key which is being deleted
   * @throws DataNotFoundException when no data is found for the given key
   *
   */
  public Data deleteByKey(String key) throws DataNotFoundException {
    Data deletedData = null;
    if (!data.containsKey(key)) {
      throw new DataNotFoundException("The given key is not present");
    } else {
      deletedData = data.remove(key);
    }
    return deletedData;
  }

  public boolean exits(String key) {
    log.info("the data is "+data);
    return data.containsKey(key);
  }

  /**
   * This method will store that data with the provided key into the datastore
   *
   * @param key this is the key of the respective value to be stored
   * @param value this is the value to be stored
   *
   * @return Data this the respective data for the given key which is being stored
   *
   */
  public Data createData(String key, Data value) {
    if (!value.getTimeToLive().equals("0")) {
      value.setTimeToLive(
          Long.toString(
              System.currentTimeMillis() + (Long.parseLong(value.getTimeToLive())) * 1000));
    }
    log.info("The data is " + value);
    data.put(key, value);
    return value;
  }
}
