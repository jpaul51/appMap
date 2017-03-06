package com.example.iem.mapapp;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.joda.deser.DateTimeDeserializer;

import org.joda.time.DateTime;

/**
 * Created by Jonas on 28/11/2016.
 */

public class JtsObjectMapper extends SimpleModule {




    public static ObjectMapper JtsObjectMapper(){
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModules(new JodaModule(),new JtsModule());

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }


}
