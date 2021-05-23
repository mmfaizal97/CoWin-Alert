package com.cowin.service;

import com.cowin.centers.Center;
import com.cowin.centers.Centers;
import com.cowin.districts.District;
import com.cowin.districts.Districts;
import com.cowin.states.State;
import com.cowin.states.States;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CowinServiceImpl implements CowinService {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String HTTP_REQUEST_TYPE = "GET";
    private static final String URL_SEPARATOR = "/";
    private static final String AMPERSAND = "&";
    private static final String HTTP_GET_INPUT_SEPARATOR = "?";
    private static final String EQUALS = "=";
    private static final Integer MAXIMUM_ACCEPTED_RESPONSE = 299;

    private static final String STATES_URL = "https://cdn-api.co-vin.in/api/v2/admin/location/states";
    private static final String DISTRICTS_URL = "https://cdn-api.co-vin.in/api/v2/admin/location/districts";
    private static final String SLOTS_WEEKLY_URL = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByDistrict";

    private static final String DATE_PARAM = "date";
    private static final String DISTRICT_ID_PARAM = "district_id";

    @Override
    public List<State> listStatesToChooseFrom() {
        StringBuilder content = null;
        try {
            URL url = new URL(STATES_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(HTTP_REQUEST_TYPE);
            con.setRequestProperty("User-Agent", USER_AGENT);
            int status = con.getResponseCode();
            Reader streamReader;
            if (status > MAXIMUM_ACCEPTED_RESPONSE) {
                System.out.println("Unable To Connect. Response: " + con.getResponseCode() + ".");
                System.out.println("Error Stream: \n");
                System.out.println(IOUtils.toString(con.getErrorStream(), StandardCharsets.UTF_8));
                return new ArrayList<>();
            } else {
                streamReader = new InputStreamReader(con.getInputStream());
            }
            BufferedReader in = new BufferedReader(streamReader);
            String inputLine;
            content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
        } catch (Exception e) {
            System.out.println("Something Went Wrong. Please Try Again...");
        }
        List<State> stateList = new ArrayList<>();
        if (content != null) {
            States states = new Gson().fromJson(String.valueOf(content), States.class);
            stateList = states.getStates();
            Collections.sort(stateList);
        }
        return stateList;
    }

    public void createSoundAlert() {
        byte[] buf = new byte[2];
        int frequency = 44100; //44100 sample points per 1 second
        AudioFormat af = new AudioFormat((float) frequency, 16, 1, true, false);
        try {
            SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
            sdl.open();
            sdl.start();
            int durationMs = 5000;
            int numberOfTimesFullSinFuncPerSec = 441; //number of times in 1sec sin function repeats
            for (int i = 0; i < durationMs * (float) 44100 / 1000; i++) { //1000 ms in 1 second
                float numberOfSamplesToRepresentFullSin = (float) frequency / numberOfTimesFullSinFuncPerSec;
                double angle = i / (numberOfSamplesToRepresentFullSin / 2.0) * Math.PI;  // /divide with 2 since sin goes 0PI to 2PI
                short a = (short) (Math.sin(angle) * 32767);  //32767 - max value for sample to take (-32767 to 32767)
                buf[0] = (byte) (a & 0xFF); //write 8bits ________WWWWWWWW out of 16
                buf[1] = (byte) (a >> 8); //write 8bits WWWWWWWW________ out of 16
                sdl.write(buf, 0, 2);
            }
            sdl.drain();
            sdl.stop();
        } catch (Exception e) {
            System.out.println("Something Went Wrong. Please Try Again...");
        }
    }

    @Override
    public List<District> listDistrictsToChooseFrom(Integer stateChoice) {
        String districtsURL = DISTRICTS_URL + URL_SEPARATOR + stateChoice;
        StringBuilder content = null;
        try {
            URL url = new URL(districtsURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(HTTP_REQUEST_TYPE);
            con.setRequestProperty("User-Agent", USER_AGENT);
            int status = con.getResponseCode();
            Reader streamReader;
            if (status > MAXIMUM_ACCEPTED_RESPONSE) {
                System.out.println("Unable To Connect. Response: " + con.getResponseCode() + ".");
                System.out.println("Error Stream: \n");
                System.out.println(IOUtils.toString(con.getErrorStream(), StandardCharsets.UTF_8));
                return new ArrayList<>();
            } else {
                streamReader = new InputStreamReader(con.getInputStream());
            }
            BufferedReader in = new BufferedReader(streamReader);
            String inputLine;
            content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
        } catch (Exception e) {
            System.out.println("Something Went Wrong. Please Try Again...");
        }

        List<District> districtList = new ArrayList<>();
        if (content != null) {
            Districts districts = new Gson().fromJson(String.valueOf(content), Districts.class);
            districtList = districts.getDistricts();
            Collections.sort(districtList);
        }
        return districtList;
    }

    @Override
    public void startSlotSearchingService(Integer districtChoice, String ageFilterChoice, String dosageChoice, Integer waitTime) {
        StringBuilder content;
        boolean firstIteration = true;

        String slotsUrl = SLOTS_WEEKLY_URL + HTTP_GET_INPUT_SEPARATOR + DISTRICT_ID_PARAM + EQUALS + districtChoice +
                AMPERSAND + DATE_PARAM + EQUALS + new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        while (true) {
            content = null;
            try {
                URL url = new URL(slotsUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod(HTTP_REQUEST_TYPE);
                con.setRequestProperty("User-Agent", USER_AGENT);
                int status = con.getResponseCode();
                Reader streamReader;
                if (status > MAXIMUM_ACCEPTED_RESPONSE) {
                    System.out.println("Unable To Connect. Response: " + con.getResponseCode() + ". Trying again in " + waitTime + " seconds.....");
                    Thread.sleep(waitTime * 1000L);
                    continue;
                } else {
                    streamReader = new InputStreamReader(con.getInputStream());
                }
                BufferedReader in = new BufferedReader(streamReader);
                String inputLine;
                content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();
            } catch (Exception e) {
                System.out.println("Something Went Wrong. Please Try Again...");
            }

            List<Center> centerList = new ArrayList<>();
            if (content != null) {
                Centers centers = new Gson().fromJson(String.valueOf(content), Centers.class);
                centerList = centers.getCenters();
                if (firstIteration) {
                    System.out.println();
                    centerList.forEach(System.out::println);
                    System.out.println();
                    firstIteration = false;
                }
            }

            List<Center> filteredCenters = centerList.stream().filter(center -> center.getSessions().stream().anyMatch(session ->
                    ((session.getAvailable_capacity() != 0 && ((dosageChoice.equals("Dose 1") && session.getAvailable_capacity_dose1() != 0)
                            || (dosageChoice.equals("Dose 2") && session.getAvailable_capacity_dose2() != 0)))
                            && (!ageFilterChoice.equalsIgnoreCase("18+") || session.getMin_age_limit() == 18))))
                    .collect(Collectors.toList());

            if (!filteredCenters.isEmpty()) {
                System.out.println("\n\nFound Centers With Slots Available In Next 7 Days...\n");
                filteredCenters.forEach(System.out::println);
                break;
            }

            System.out.println("No centers Available... Searching again in " + waitTime + " seconds.....");
            try {
                Thread.sleep(waitTime * 1000L);
            } catch (Exception e) {
                System.out.println("Something Went Wrong. Please Try Again...");
            }
        }
    }

}
