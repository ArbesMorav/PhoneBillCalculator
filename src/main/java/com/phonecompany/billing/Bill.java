package com.phonecompany.billing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Bill implements TelephoneBillCalculator {

    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH");
    Date startTime = null;
    Date endTime = null;
    Integer peakStartHour = 8;
    Integer peakEndHour = 16;
    BigDecimal peakHoursRate = BigDecimal.valueOf(1);
    BigDecimal offPeakHoursRate = BigDecimal.valueOf(0.5);
    BigDecimal overFiveMinutesRate = BigDecimal.valueOf(0.2);
    BigDecimal billedAmount = BigDecimal.valueOf(0);

    public String mostFrequent(String phoneLog) {

        // ArrayList of all phone number occurrences
        ArrayList<String> phoneNumbers = new ArrayList<>();
        Arrays.stream(phoneLog.split("\n")).map(s -> s.split(",")).forEach(entry -> phoneNumbers.add(entry[0]));

        // HashMap where we will assing number of occurrences to each unique phone number
        HashMap<String, Integer> frequencyMap = new HashMap<>();

        // If phone number is already in the HashMap, add one occurrence to it. If it is not, add the phone number
        for (String phone : phoneNumbers) {
            if (frequencyMap.containsKey(phone)) {
                frequencyMap.put(phone, frequencyMap.get(phone) + 1);
            } else {
                frequencyMap.put(phone, 1);
            }
        }

        ArrayList<String> listOfMostFrequent = new ArrayList<>();

        // Get the highest number of occurrences, use it to find all phone numbers with same amount
        int maxFrequency = Collections.max(frequencyMap.values());
        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() == maxFrequency) {
                listOfMostFrequent.add(entry.getKey());
            }
        }

        Collections.sort(listOfMostFrequent);
        // The last entry is the one with highest arithmetic value
        return listOfMostFrequent.get(listOfMostFrequent.size() - 1);
    }

    public BigDecimal calculate(String phoneLog) {

        String[] lines = phoneLog.split("\n");

        String mostFrequentNumber = mostFrequent(phoneLog);
        System.out.println("MOST FREQUENT (HIGHEST) NUMBER IS: " + mostFrequentNumber + " - CALLS TO THIS NUMBER WILL BE IGNORED");

        Arrays.stream(lines).map(s -> s.split(",")).forEach(line -> {
            String phone = line[0];
            try {
                startTime = dateFormat.parse(line[1]);
                endTime = dateFormat.parse(line[2]);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            // line of input skipped due to promotion
            if (phone.equals(mostFrequentNumber)) return;

            Instant instant = startTime.toInstant();
            ZonedDateTime time = instant.atZone(TimeZone.getDefault().toZoneId());

            long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(endTime.getTime() - startTime.getTime()) + 1;

            // We know how many minutes are going to be billed per line of input. Start with first 5 minutes,
            // checking each minute for peak time. After 5 minutes, use the appropriate pricing model
            for (int i = 0; i < diffMinutes; i++) {
                if (i < 5) {
                    if ((Integer.parseInt(hourFormatter.format(time.plusMinutes(i)))) >= peakStartHour
                            && (Integer.parseInt(hourFormatter.format(time.plusMinutes(i)))) <= peakEndHour) {
                        billedAmount = billedAmount.add(peakHoursRate);
                    } else {
                        billedAmount = billedAmount.add(offPeakHoursRate);
                    }
                } else {
                    billedAmount = billedAmount.add(overFiveMinutesRate);
                }
            }
        });

        // Using the setScale, the output will make more sense since it is representing financial value in CZK
        return billedAmount.setScale(2, RoundingMode.HALF_UP);
    }
}
