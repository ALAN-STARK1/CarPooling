package org.example.carpooling.entity;

import lombok.Data;

import java.util.Date;

@Data
public abstract class Order {
    Long id;
    String state;

    Date creationDate;
    Date endDate;
}
