package com.cowin.districts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class District implements Comparable<District>{

    private Integer district_id;
    private String district_name;

    @Override
    public String toString() {
        return "District ID: " + district_id + "\tDistrict Name: " + district_name;
    }

    @Override
    public int compareTo(District district) {
        return getDistrict_name().compareTo(district.getDistrict_name());
    }

}
