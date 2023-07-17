package org.example.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;
@Data
@Builder
@AllArgsConstructor
public class Human {
    private long id;
    private String name;
    private String lastname;
    private int age;
}
