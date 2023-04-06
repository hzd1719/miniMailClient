package org.example.mail;

import org.example.exceptions.AccountNotFoundException;
import org.example.helpers.Directory;
import org.example.parts.Account;
import org.example.parts.Mail;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class OutlookTest {

    Outlook outlook = new Outlook();

    @Test
    void addNewAccountTestNullAccount() {
        assertThrows(IllegalArgumentException.class,()->outlook.addNewAccount(null, "notnull@gmail.com"),
                "IllegalArgumentException was expected!");
    }

    @Test
    void addNewAccountTestEmptyEmail() {
        assertThrows(IllegalArgumentException.class,()->outlook.addNewAccount("something@abv.bg", ""),
                "IllegalArgumentException was expected!");
    }

    @Test
    void addNewAccountTest() {
        Account expectedAccount = new Account("test@gmail.com", "test_account");
        assertEquals(expectedAccount, outlook.addNewAccount("test_account", "test@gmail.com"));
    }


    @Test
    void createFolderToNonExistingAccount() {
        assertThrows(AccountNotFoundException.class, ()-> outlook.createFolder("not_exist", "inbox/important"),
                "AccountNotFoundException was expected!");
    }

    @Test
    void createFolderTest() {
        outlook.addNewAccount("test_acc", "test@gmail.com");
        HashMap<String, Directory> expectedAccountInbox = new HashMap<>();
        Directory testDirectory = new Directory("inbox");
        List<String> path = new ArrayList<>();
        path.add("inbox");
        testDirectory.addChildrenFolderToDirectory("important", path);
        expectedAccountInbox.put("test_acc", testDirectory);

        outlook.createFolder("test_acc", "inbox/important");
        HashMap<String, Directory> actualAccountInbox = outlook.getAccountInbox();

        assertTrue(expectedAccountInbox.equals(actualAccountInbox));

    }

    @Test
    void getMailsFromFolderTest() {
        outlook.addNewAccount("test_acc", "test@gmail.com");
        HashMap<String, Directory> actualAccountInbox = outlook.getAccountInbox();
        Directory testDirectory = actualAccountInbox.get("test_acc");
        HashSet<String> recipients = new HashSet<>();
        recipients.add("recievertest@abv.bg");

        Mail testMail = new Mail(new Account("test@abv.bg","test"), recipients, "testing", "testingbody", LocalDateTime.now());
        testDirectory.addEmail("inbox", testMail);

        Collection<Mail> expected = new HashSet<>();
        expected.add(testMail);
        assertTrue(expected.equals(outlook.getMailsFromFolder("test_acc", "inbox")));
    }

    @Test
    void sendMailTest() {
        //TODO: dobavi i nqkvi pravila na akauntite da moje i tova da testvash(s tozi metod se testva
        //i recieve mail - taka testvam i drugite metodi
        outlook.addNewAccount("test_send", "test_send@gmail.com");

        outlook.addNewAccount("test_receive", "test_receive@gmail.com");
        outlook.createFolder("test_receive", "inbox/important");
        String ruleDefinition = "subject-includes: test_subject, test_subject2\n" +
                "subject-or-body-includes: test_body\n" +
                "recipients-includes: test_recipients@gmail.com, gosho@gmail.com\n"+
                "from: test_send@gmail.com";
        outlook.addRule("test_receive","inbox/important", ruleDefinition, 5);


        //expect the account that is sending the message test_send to have the sent mail in its sent directory
        //expect the account that is receiving the mail to have the message received mail in inbox/important directory

        String mailContent = "test_body";

        String mailMetaData = "sender: test_send@gmail.com\n" +
                "recipients: test_receive@gmail.com, pesho@gmail.com, test_recipients@gmail.com\n"+
                "subject: test_subject, test_subject2\n" +
                "received: 2022-12-08 14:14";

        outlook.sendMail("test_send", mailMetaData, mailContent);

        //Get all accounts and sent directory
        HashMap<String, Directory> actualAccountSent = outlook.getAccountSent();
        //Get the sent directory of test_send account
        Set<Mail> actualMailsInSentDirectory = actualAccountSent.get("test_send").getEmails().get("sent");

        //Get all acount and inbox directory
        HashMap<String, Directory> actualAccountReceived = outlook.getAccountInbox();
        //Get the inbox/important directory of test_receive account
        Set<Mail> actualMailsInImportantDirectory = actualAccountReceived.get("test_receive").getEmails().get("important");

        HashSet<String> recipients = new HashSet<>();
        recipients.add("test_recipients@gmail.com");
        recipients.add("pesho@gmail.com");
        recipients.add("test_receive@gmail.com");
        String subject = "test_subject, test_subject2";
        String body = "test_body";
        Mail mail = new Mail(outlook.getAccounts().get("test_send"), recipients, subject, body,
                LocalDateTime.of(2022,12,8,14,14));

        Set<Mail> expectedMailsInSentDirectory = new HashSet<>();
        expectedMailsInSentDirectory.add(mail);

        Set<Mail> expectedMailsInImportantDirectory = new HashSet<>();
        expectedMailsInImportantDirectory.add(mail);

        assertTrue(expectedMailsInSentDirectory.equals(actualMailsInSentDirectory)
                && expectedMailsInImportantDirectory.equals(actualMailsInImportantDirectory));

        //actualMailsInSentDirectory and actualMailsInImportantDirectory must equal expectedMailsInSentDirectory and expectedMailsInImportantDirectory

    }

    @Test
    void sendMailTNoSenderest() {
        //TODO: dobavi i nqkvi pravila na akauntite da moje i tova da testvash(s tozi metod se testva
        //i recieve mail - taka testvam i drugite metodi
        outlook.addNewAccount("test_send", "test_send@gmail.com");

        outlook.addNewAccount("test_receive", "test_receive@gmail.com");
        outlook.createFolder("test_receive", "inbox/important");
        String ruleDefinition = "subject-includes: test_subject, test_subject2\n" +
                "subject-or-body-includes: test_body\n" +
                "recipients-includes: test_recipients@gmail.com, gosho@gmail.com\n"+
                "from: test_send@gmail.com";
        outlook.addRule("test_receive","inbox/important", ruleDefinition, 5);


        //expect the account that is sending the message test_send to have the sent mail in its sent directory
        //expect the account that is receiving the mail to have the message received mail in inbox/important directory

        String mailContent = "test_body";

        String mailMetaData =
                "recipients: test_receive@gmail.com, pesho@gmail.com, test_recipients@gmail.com\n"+
                "subject: test_subject, test_subject2\n" +
                "received: 2022-12-08 14:14";

        outlook.sendMail("test_send", mailMetaData, mailContent);

        //Get all accounts and sent directory
        HashMap<String, Directory> actualAccountSent = outlook.getAccountSent();
        //Get the sent directory of test_send account
        Set<Mail> actualMailsInSentDirectory = actualAccountSent.get("test_send").getEmails().get("sent");

        //Get all acount and inbox directory
        HashMap<String, Directory> actualAccountReceived = outlook.getAccountInbox();
        //Get the inbox/important directory of test_receive account
        Set<Mail> actualMailsInImportantDirectory = actualAccountReceived.get("test_receive").getEmails().get("important");

        HashSet<String> recipients = new HashSet<>();
        recipients.add("test_recipients@gmail.com");
        recipients.add("pesho@gmail.com");
        recipients.add("test_receive@gmail.com");
        String subject = "test_subject, test_subject2";
        String body = "test_body";
        Mail mail = new Mail(outlook.getAccounts().get("test_send"), recipients, subject, body,
                LocalDateTime.of(2022,12,8,14,14));

        Set<Mail> expectedMailsInSentDirectory = new HashSet<>();
        expectedMailsInSentDirectory.add(mail);

        Set<Mail> expectedMailsInImportantDirectory = new HashSet<>();
        expectedMailsInImportantDirectory.add(mail);

        assertTrue(expectedMailsInSentDirectory.equals(actualMailsInSentDirectory)
                && expectedMailsInImportantDirectory.equals(actualMailsInImportantDirectory));

        //actualMailsInSentDirectory and actualMailsInImportantDirectory must equal expectedMailsInSentDirectory and expectedMailsInImportantDirectory

    }

}