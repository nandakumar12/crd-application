package com.nandakumar12.crd.app.controller;

import com.nandakumar12.crd.app.exception.*;
import com.nandakumar12.crd.app.model.Data;
import com.nandakumar12.crd.app.model.ResponseMessage;
import com.nandakumar12.crd.app.repository.DataRepository;
import com.nandakumar12.crd.app.service.DataParser;
import com.nandakumar12.crd.app.service.DataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping(value = "/api/crd")
public class CrdController {

  @Autowired DataService dataService;

  @Autowired
  DataParser dataParser;

  @GetMapping(value = "/data")
  public ResponseEntity<Object> getData(@RequestParam("key") String key)
      throws ValueExpiredException, DataNotFoundException, ExecutionException, InterruptedException{
    Data data = dataService.readData(key).get();
    return ResponseEntity.ok().body(data);
  }

  @PostMapping(value = "/data")
  public ResponseEntity<Object> createData(
      @RequestParam("key") String key, @RequestBody(required = false) String bodyData)
          throws DuplicateKeyException, ExecutionException, InterruptedException, InvalidDataException, InvalidKeyException {
    if (key.length() > 32) {
      throw new InvalidKeyException("key size exceeds 32 chars");
    }
    log.info("The body data is "+bodyData);
    if (bodyData == null) {
      return ResponseEntity.badRequest()
          .body(new ResponseMessage("Required Data in body is missing"));
    } else {
      return ResponseEntity.status(201).body(dataParser.parse(key, bodyData));
    }
  }

  @DeleteMapping(value = "/data")
  public ResponseEntity<Object> deleteData(@RequestParam("key") String key)
          throws DataNotFoundException, ValueExpiredException, ExecutionException, InterruptedException {
    log.info("Exec controller");
    return ResponseEntity.ok().body(dataService.deleteData(key).get());
  }

}
