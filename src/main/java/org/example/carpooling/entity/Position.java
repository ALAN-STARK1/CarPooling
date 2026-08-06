package org.example.carpooling.entity;

import lombok.Data;

@Data
public class Position {
    private Long id;
    private String name;
    private String city;
    private double lng;
    private double lat;
}
