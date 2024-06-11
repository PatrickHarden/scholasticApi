package com.scholastic.litplatformbaapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class IdAndNameApiModel {
    private int id;
    private String name;

    private JsonNode identifiers;

    public IdAndNameApiModel(int id, String name, JsonNode identifiers) {
        this.id = id;
        this.name = name;
        this.identifiers = identifiers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JsonNode getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(JsonNode identifiers) {
        this.identifiers = identifiers;
    }

}
