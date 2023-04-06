package org.example.parts;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class MailMetaDataTest {

    String mailMetaData = "sender: testy@gmail.com\n" +
            "recipients: pesho@gmail.com, gosho@gmail.com\n"+
            "subject: Hello, MJT\n" +
            "received: 2022-12-08 14:14";

    String mailMetaDataEmpty =
            "";

    MailMetaData mailMetaDataClass = new MailMetaData(mailMetaData);
    MailMetaData mailMetaDataClassEmpty = new MailMetaData(mailMetaDataEmpty);
    @Test
    void getSenderTest() {
        String expected = "testy@gmail.com";
        assertEquals(expected, mailMetaDataClass.getSender());
    }

    @Test
    void getReceivedTest() {
        LocalDateTime expected = LocalDateTime.of(2022,12,8,14,14);
        assertEquals(expected, mailMetaDataClass.getReceived());
    }

    @Test
    void getSubjectTest() {
        String expected = "Hello, MJT";
        assertEquals(expected, mailMetaDataClass.getSubject());
    }

    @Test
    void getRecipientsTest() {
        List<String> expected = new ArrayList<>();
        expected.add("pesho@gmail.com");
        expected.add("gosho@gmail.com");
        assertEquals(expected, mailMetaDataClass.getRecipients());
    }

    @Test
    void getSenderTestNull() {
        assertEquals(null, mailMetaDataClassEmpty.getSender());
    }
    @Test
    void getReceivedTestNull() {
        assertEquals(null, mailMetaDataClassEmpty.getReceived());
    }

    @Test
    void getSubjectTestNull() {
        assertEquals(null, mailMetaDataClassEmpty.getSubject());
    }

    @Test
    void getRecipientsTestNull() {
        assertEquals(null, mailMetaDataClassEmpty.getRecipients());
    }

}

