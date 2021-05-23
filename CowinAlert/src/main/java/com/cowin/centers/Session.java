package com.cowin.centers;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Session {

    private String session_id;
    private String date;
    private Integer available_capacity;
    private Integer available_capacity_dose1;
    private Integer available_capacity_dose2;
    private Integer min_age_limit;
    private String vaccine;
    private List<String> slots;

}
