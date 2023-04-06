package org.example.parts;

import java.time.LocalDateTime;
import java.util.*;



/*
 The class takes milMetadata argument in the following format:
 The class does not make any validations as it is required, it assumes the format is correct!
 The extract the values from the string and returns them separately as class members
 *sender: testy@gmail.com
 * subject: Hello, MJT!
 * recipients: pesho@gmail.com, gosho@gmail.com,
 * received: 2022-12-08 14:14
 */

public class MailMetaData {

    private final String mailMetadata;
    private String sender;
    private LocalDateTime received;
    private String subject;
    private List<String> recipients;
    public MailMetaData(String mailMetadata) {
        this.mailMetadata = mailMetadata;
        this.extractMetaData();
    }
    //Must be in format yyyy-MM-dd HH:mm
    public LocalDateTime stringToDateTime(String stringDateTime) {
        int year = Integer.parseInt(stringDateTime.substring(0,4));
        int month = Integer.parseInt(stringDateTime.substring(5,7));
        int day = Integer.parseInt(stringDateTime.substring(8,10));

        int hours = Integer.parseInt(stringDateTime.substring(11,13));
        int minutes = Integer.parseInt(stringDateTime.substring(14,16));

        return LocalDateTime.of(year, month, day, hours, minutes);
    }

    public void extractMetaData() {
        String[] mailMetaDataArray = this.mailMetadata.split("\\R");
        for(String mailMetaDataString:mailMetaDataArray) {
            int doubleDotsIndex = mailMetaDataString.indexOf(":");
            String metaDatResults = mailMetaDataString.substring(doubleDotsIndex+1).strip();
            String metaDataNames = mailMetaDataString.substring(0, doubleDotsIndex+1);
            switch (metaDataNames) {
                case "sender:" -> this.sender = metaDatResults;
                case "subject:" -> this.subject = metaDatResults;
                case "recipients:" ->
                        this.recipients = new ArrayList<>(Arrays.asList(metaDatResults.replaceAll("\\s", "").split(",")));
                case "received:" -> this.received = stringToDateTime(metaDatResults);
            }
        }

    }

    public String getSender() {
        return sender;
    }

    public LocalDateTime getReceived() {
        return received;
    }

    public String getSubject() {
        return subject;
    }

    public List<String> getRecipients() {
        return recipients;
    }
}
