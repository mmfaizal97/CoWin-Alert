package com.cowin.centers;

import com.cowin.vaccinefees.VaccineFees;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Center {

    private Integer center_id;
    private String name;
    private String address;
    private String state_name;
    private String district_name;
    private String block_name;
    private Integer pincode;
    private Double lat;
    @SerializedName("long")
    private Double long1;
    private String from;
    private String to;
    private String fee_type;
    private List<VaccineFees> vaccine_fees;
    private List<Session> sessions;

}
