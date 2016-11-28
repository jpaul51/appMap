package com.example.iem.mapapp;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Jonas on 28/11/2016.
 */

public class JtsObjectMapper {

    public static ObjectMapper JtsObjectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JtsModule());

        return mapper;
    }


}
