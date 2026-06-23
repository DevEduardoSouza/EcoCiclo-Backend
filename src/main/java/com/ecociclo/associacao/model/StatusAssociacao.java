package com.ecociclo.associacao.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusAssociacao {
    OK,
    NEGADO,
    PENDENTE;

    @JsonCreator
    public static StatusAssociacao fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return StatusAssociacao.valueOf(value.trim().toUpperCase());
    }

    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}
