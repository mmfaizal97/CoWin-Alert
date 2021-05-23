package com.cowin.service;

import com.cowin.districts.District;
import com.cowin.states.State;

import java.util.List;

public interface CowinService {

    List<State> listStatesToChooseFrom();

    List<District> listDistrictsToChooseFrom(Integer stateChoice);

    void startSlotSearchingService(Integer districtChoice, String filterChoice, String dosageChoice, Integer waitTime);

    void createSoundAlert();

}
