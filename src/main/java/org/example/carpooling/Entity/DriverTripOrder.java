package org.example.carpooling.Entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class DriverTripOrder extends Order{
    String depart_address;
    String finish_address;
    Date depart_date;
    Date finish_date;
    List<Integer> passenger_ids;
}
