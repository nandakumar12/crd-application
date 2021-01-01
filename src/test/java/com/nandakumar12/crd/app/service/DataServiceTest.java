package com.nandakumar12.crd.app.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nandakumar12.crd.app.exception.DataNotFoundException;
import com.nandakumar12.crd.app.exception.DuplicateKeyException;
import com.nandakumar12.crd.app.exception.InvalidDataException;
import com.nandakumar12.crd.app.exception.ValueExpiredException;
import com.nandakumar12.crd.app.model.Data;
import com.nandakumar12.crd.app.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.assertj.core.api.Assertions;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DataServiceTest {

    @Mock
    private DataRepository dataRepository;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    DataService dataService;

    JsonObject jsonObject;

    Gson gson = new Gson();

    @BeforeEach
    public void setup(){
         dataService = new DataService(dataRepository);
         jsonObject = new JsonObject();
         jsonObject.addProperty("key1","Sample val1");
         jsonObject.addProperty("key2","Sample val2");
    }

    @Test
    @DisplayName("This method will read unexpired data from repository")
    void readValidDataFromRepository() throws ValueExpiredException, DataNotFoundException, ExecutionException, InterruptedException {
        Data expectedData = new Data(gson.toJson(jsonObject),Long.toString(System.currentTimeMillis()+500000L));
        Mockito.when(dataRepository.findByKey("1")).thenReturn(java.util.Optional.of(expectedData));
        CompletableFuture<Data> actualData = dataService.readData("1");
        Assertions.assertThat(actualData).isCompleted();
        Assertions.assertThat(actualData.get().getValue()).isEqualTo(expectedData.getValue());
    }

    @Test
    @DisplayName("This method will try to read an expired data from repository")
    void readExpiredDataFromRepository() {
        Data expectedData = new Data(gson.toJson(jsonObject),Long.toString(System.currentTimeMillis()-100L));
        Mockito.when(dataRepository.findByKey(Mockito.anyString())).thenReturn(java.util.Optional.of(expectedData));
        ValueExpiredException exception = assertThrows(ValueExpiredException.class,()->dataService.readData("1"));
        Assertions.assertThat(exception).hasMessageContaining("expired");

    }

    @Test
    @DisplayName("This method will try to delete an data which is present in the repository")
    void deleteValidDataFromRepo() throws ValueExpiredException, DataNotFoundException {
        Data expectedData = new Data(gson.toJson(jsonObject),Long.toString(System.currentTimeMillis()+50000L));
        Mockito.when(dataRepository.findByKey("1")).thenReturn(java.util.Optional.of(expectedData));
        Mockito.when(dataRepository.deleteByKey("1")).thenReturn(expectedData);
        CompletableFuture<Data> actualData = dataService.deleteData("1");
        actualData.thenAccept(data -> Assertions.assertThat(data.getValue()).isEqualTo(expectedData.getValue()));
    }

    @Test
    @DisplayName("This method will try to delete an data which is not present")
    void deleteDataNotPresentInRepository() throws DataNotFoundException {
        Data expectedData = new Data(gson.toJson(jsonObject),Long.toString(System.currentTimeMillis()));
        Mockito.when(dataRepository.findByKey("2")).thenReturn(java.util.Optional.empty());
        DataNotFoundException exception = assertThrows(DataNotFoundException.class,()->dataService.deleteData("2"));
        Assertions.assertThat(exception).hasMessageContaining("not found");

    }

    @Test
    @DisplayName("This method will check the validity of an proper data")
    void checkValidityOfData() throws ValueExpiredException, DataNotFoundException {
        Data expectedData = new Data(gson.toJson(jsonObject),Long.toString(System.currentTimeMillis()-100L));
        assertThrows(ValueExpiredException.class,()->dataService.isValid(expectedData, "1"));
        Mockito.verify(dataRepository,Mockito.times(1)).deleteByKey(stringArgumentCaptor.capture());
        Assertions.assertThat(stringArgumentCaptor.getValue()).isEqualTo("1");

    }

    @Test
    @DisplayName("This method will try to add an data whose key is null")
    void addInvalidDataToRepository(){
        Data expectedData = new Data(gson.toJson(jsonObject),Long.toString(System.currentTimeMillis()-100L));
        assertThrows(InvalidDataException.class,()->dataService.addData(null, expectedData));

    }

    @Test
    @DisplayName("This method will try to add an data whose key is already been used")
    void addDuplicateDataToRepository() throws ValueExpiredException, DataNotFoundException {
        Data expectedData = new Data(gson.toJson(jsonObject),Long.toString(System.currentTimeMillis()+5000L));
        Mockito.when(dataRepository.exits("1")).thenReturn(true);
        Mockito.when(dataRepository.findByKey("1")).thenReturn(java.util.Optional.of(expectedData));
        assertThrows(DuplicateKeyException.class,()->dataService.addData("1", expectedData));
    }


}