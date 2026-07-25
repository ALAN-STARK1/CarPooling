package org.example.carpooling.entity;

import lombok.Data;

import java.util.Date;

@Data
public class PassengerOrder extends Order{
    String depart_address;
    String finish_address;
    Date depart_date;
    Date finish_date;
}
