package ru.otus.highload.homework.fourth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserSearchDto(@JsonProperty("first_name") String firstName,
                            @JsonProperty("last_name") String lastName) {
}
