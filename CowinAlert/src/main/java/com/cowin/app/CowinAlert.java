package com.cowin.app;

import com.cowin.districts.District;
import com.cowin.service.CowinService;
import com.cowin.service.CowinServiceImpl;
import com.cowin.states.State;
import com.cowin.ui.CowinSlotFinder;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CowinAlert {

    public static void main(String[] args) {
        CowinService cowinService = new CowinServiceImpl();
        List<State> stateList = cowinService.listStatesToChooseFrom();
        List<District> districtList = new ArrayList<>();

        if (stateList == null || stateList.isEmpty()) {
            System.out.println("Error Connecting to Server. Please Try Again Later.");
            return;
        }

        JComboBox<String> stateComboBox = new JComboBox<>();
        JComboBox<String> districtComboBox = new JComboBox<>();
        JComboBox<String> ageFilter = new JComboBox<>(new String[]{"18+", "45+"});
        JComboBox<String> dosage = new JComboBox<>(new String[]{"Dose 1", "Dose 2"});
        JTextField repeatFrequency = new JTextField("5");
        Font bigFont = repeatFrequency.getFont().deriveFont(Font.BOLD, 13f);
        repeatFrequency.setFont(bigFont);
        JLabel blankLabel = new JLabel(" ");

        stateList.forEach(state -> stateComboBox.addItem(state.getState_name()));
        stateComboBox.addActionListener(e -> {
            districtComboBox.removeAllItems();
            int stateChoice = stateList.stream().filter(state -> state.getState_name().equals(stateComboBox.getSelectedItem()))
                    .collect(Collectors.toList()).get(0).getState_id();
            List<District> districtListTemp = cowinService.listDistrictsToChooseFrom(stateChoice);
            if (districtListTemp == null || districtListTemp.isEmpty()) {
                System.out.println("Error Connecting to Server. Please Try Again Later.");
                return;
            }
            districtListTemp.forEach(district -> districtComboBox.addItem(district.getDistrict_name()));
            districtList.clear();
            districtList.addAll(districtListTemp);
        });

        final JComponent[] inputs = new JComponent[]{new JLabel("State: "), stateComboBox, blankLabel,
                new JLabel("District: "), districtComboBox, blankLabel,
                new JLabel("Age Filter: "), ageFilter, blankLabel,
                new JLabel("Dosage: "), dosage, blankLabel,
                new JLabel("Repeat Search Frequency (in seconds): "), repeatFrequency, blankLabel};
        int result = JOptionPane.showConfirmDialog(null, inputs, "Cowin Vaccination Slot Notifier", JOptionPane.DEFAULT_OPTION);

        if (result != JOptionPane.OK_OPTION) {
            System.out.println("Closing App..");
            return;
        }

        Integer districtChoice = districtList.stream().filter(district -> district.getDistrict_name().equals(districtComboBox.getSelectedItem()))
                .collect(Collectors.toList()).get(0).getDistrict_id();
        String ageFilterChoice = (String) ageFilter.getSelectedItem();
        String dosageChoice = (String) dosage.getSelectedItem();
        int waitTime;
        try {
            waitTime = Integer.parseInt(repeatFrequency.getText());
        } catch (Exception exception) {
            waitTime = 5;
        }

        CowinSlotFinder.createAndShowGui(cowinService, districtChoice, ageFilterChoice, dosageChoice, waitTime);

        System.out.println("\n\nAbove Centers Found... Terminating....");
    }

}
