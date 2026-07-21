package org.example.carpooling.Entity;

import lombok.Data;

import java.util.Date;

@Data
public abstract class Order {
    int id;
    String state;

    Date creationDate;
    Date endDate;
}
