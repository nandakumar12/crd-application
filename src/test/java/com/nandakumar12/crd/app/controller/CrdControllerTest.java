package com.nandakumar12.crd.app.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nandakumar12.crd.app.exception.DataNotFoundException;
import com.nandakumar12.crd.app.model.Data;
import com.nandakumar12.crd.app.model.ResponseMessage;
import com.nandakumar12.crd.app.repository.DataRepository;
import com.nandakumar12.crd.app.service.DataParser;
import com.nandakumar12.crd.app.service.DataService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import java.util.concurrent.CompletableFuture;

@WebMvcTest(controllers = CrdController.class)
class CrdControllerTest {

    @MockBean
    private DataService dataService;

    @MockBean
    private DataParser dataParser;

    @MockBean
    private DataRepository dataRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    CustomExceptionHandler customExceptionHandler;

    @Captor
    ArgumentCaptor<Data> dataArgumentCaptor;

    Gson gson = new Gson();

    @Test
    @DisplayName("Should retrieve the data with given key when making GET request to - /api/crd/data")
    void getData() throws Exception {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("key1","val1");
        jsonObject.addProperty("key2","val2");
        Data expectedData = new Data(gson.toJson(jsonObject),"50");
        Mockito.when(dataService.readData("15")).thenReturn(CompletableFuture.completedFuture(expectedData));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/crd/data?key=15"))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.value").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.timeToLive").doesNotExist());
    }


    @Test
    @DisplayName("This method will try to post data when making POST request to - /api/crd/data")
    void postValidData() throws Exception {

        Gson gson = new Gson();
        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("key1","Mock value 1");
        jsonData.addProperty("key2","Mock value 2");
        Data expectedData = new Data(gson.toJson(jsonData),"0");
        ArgumentMatcher<Data> dataArgumentMatcher = data -> data instanceof Data;
        Mockito.when(dataService.addData(Mockito.eq("99"), Mockito.argThat(dataArgumentMatcher))).thenAnswer(i -> CompletableFuture.completedFuture(i.getArguments()[1]));
        Mockito.when(dataParser.parse("99",gson.toJson(jsonData))).thenReturn(expectedData);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/crd/data?key=99")
                .content(gson.toJson(jsonData))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.value",Matchers.is(gson.toJson(jsonData))));

    }

    @Test
    @DisplayName("This method will try to delete a missing data when make DELETE - /api/crd/data")
    void deleteData() throws Exception {


        Mockito.when(dataService.deleteData("99")).thenThrow(DataNotFoundException.class);
        Mockito.when(customExceptionHandler.handleException(Mockito.anyObject())).thenReturn(ResponseEntity.badRequest().body(new ResponseMessage("data not found")) );
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/crd/data?key=99")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());

    }




}

