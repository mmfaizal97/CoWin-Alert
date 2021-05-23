package com.cowin.ui;

import com.cowin.service.CowinService;

import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;

public class CowinSlotFinder extends JPanel {

    public CowinSlotFinder() {
        setLayout(new BorderLayout());
        JTextArea textArea = new JTextArea(50, 150);
        add(new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        TextAreaOutputStream taOutputStream = new TextAreaOutputStream(textArea);
        System.setOut(new PrintStream(taOutputStream));
    }

    public static void createAndShowGui(CowinService cowinService, Integer districtChoice, String ageFilterChoice, String dosageChoice, int waitTime) {
        JFrame frame = new JFrame("Cowin Slot Finder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new CowinSlotFinder());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        cowinService.startSlotSearchingService(districtChoice, ageFilterChoice, dosageChoice, waitTime);
        cowinService.createSoundAlert();
    }

}