package org.example.helpers;

import org.example.exceptions.FolderAlreadyExistsException;
import org.example.exceptions.FolderNotFoundException;
import org.example.exceptions.InvalidPathException;
import org.example.parts.Account;
import org.example.parts.Mail;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DirectoryTest {

    @Test
    void addChildrenFolderToDirectoryInvalidRootTest() {
        Directory directory = new Directory("inbox");
        List<String> fullPath = new ArrayList<>();
        fullPath.add("important");
        fullPath.add("inbox");
        assertThrows(InvalidPathException.class, ()-> directory.addChildrenFolderToDirectory("newfolder", fullPath),
                "Expected Invalid Path Exception because path does not start with inbox(root directory)");

    }

    @Test
    void addChildrenFolderToDirectoryInvalidPathTest() {
        Directory directory = new Directory("inbox");
        List<String> fullPath = new ArrayList<>();
        fullPath.add("inbox");
        directory.addChildrenFolderToDirectory("important", fullPath);
        fullPath.add("important");
        fullPath.add("nonexistentfolder");

        assertThrows(InvalidPathException.class, ()-> directory.addChildrenFolderToDirectory("newfolder", fullPath),
                "Expected Invalid Path Exception because the path is not valid!");

    }

    @Test
    void addChildrenFolderToDirectoryAlreadyExistingFolderTest() {
        Directory directory = new Directory("inbox");
        List<String> fullPath = new ArrayList<>();
        fullPath.add("inbox");
        directory.addChildrenFolderToDirectory("important", fullPath);

        assertThrows(FolderAlreadyExistsException.class, ()-> directory.addChildrenFolderToDirectory("important", fullPath),
                "Expected Invalid Path Exception because the path is not valid!");

    }

    @Test
    void addChildrenFolderToDirectoryTest() {
        Directory directory = new Directory("inbox");
        List<String> fullPath = new ArrayList<>();
        fullPath.add("inbox");
        directory.addChildrenFolderToDirectory("important", fullPath);

        HashMap<String, Set<String>> expectedFolders = new HashMap<>();
        Set<String> expectedFoldersSet = new HashSet<>();
        expectedFoldersSet.add("important");
        expectedFolders.put("inbox", expectedFoldersSet);
        expectedFolders.put("important", new HashSet<>());

        HashMap<String, Set<String>> actualFolders = directory.getFolders();

        assertTrue(expectedFolders.equals(actualFolders));

    }

    @Test
    void addChildrenFolderToDirectoryTestFalse() {
        Directory directory = new Directory("inbox");
        List<String> fullPath = new ArrayList<>();
        fullPath.add("inbox");
        directory.addChildrenFolderToDirectory("important", fullPath);

        HashMap<String, Set<String>> expectedFolders = new HashMap<>();
        Set<String> expectedFoldersSet = new HashSet<>();
        expectedFoldersSet.add("important");
        expectedFolders.put("inbox", expectedFoldersSet);
        expectedFolders.put("not-equal", new HashSet<>());

        HashMap<String, Set<String>> actualFolders = directory.getFolders();

        assertFalse(expectedFolders.equals(actualFolders));

    }



    @Test
    void addEmailToNonExistingFolderTest() {
        Directory directory = new Directory("inbox");
        HashSet<String> recipients = new HashSet<>();
        recipients.add("recievertest@abv.bg");
        Mail mail = new Mail(new Account("test@abv.bg","test"), recipients, "testing", "testingbody", LocalDateTime.now());
        assertThrows(FolderNotFoundException.class, ()->directory.addEmail("important", mail), "Expected Folder Not Found exception!");
    }

    @Test
    void addEmailTest() {
        Directory directory = new Directory("inbox");
        HashSet<String> recipients = new HashSet<>();
        recipients.add("recievertest@abv.bg");
        Mail mail = new Mail(new Account("test@abv.bg","test"), recipients, "testing", "testingbody", LocalDateTime.now());
        directory.addEmail("inbox", mail);

        HashMap<String, Set<Mail>> expectedEmails = new HashMap<>();
        Set<Mail> expectedMailsSet = new HashSet<>();
        expectedMailsSet.add(mail);
        expectedEmails.put("inbox", expectedMailsSet);

        HashMap<String, Set<Mail>> actualEmails = directory.getEmails();
        assertTrue(expectedEmails.equals(actualEmails));
    }


    @Test
    void removeNonExistingEmailTest() {
        Directory directory = new Directory("inbox");
        HashSet<String> recipients = new HashSet<>();
        recipients.add("recievertest@abv.bg");
        Mail mail = new Mail(new Account("test@abv.bg","test"), recipients, "testing", "testingbody", LocalDateTime.now());
        assertThrows(IllegalArgumentException.class, () ->directory.removeEmail("inbox", mail), "Expected IllegalArgumentException");
    }

    @Test
    void removeEmailTest() {
        Directory directory = new Directory("inbox");
        HashSet<String> recipients = new HashSet<>();
        recipients.add("recievertest@abv.bg");
        Mail mail = new Mail(new Account("test@abv.bg","test"), recipients, "testing", "testingbody", LocalDateTime.now());
        directory.addEmail("inbox", mail);

        HashMap<String, Set<Mail>> expectedEmails = new HashMap<>();
        expectedEmails.put("inbox", new HashSet<>());

        directory.removeEmail("inbox", mail);
        HashMap<String, Set<Mail>> actualEmails = directory.getEmails();
        assertTrue(expectedEmails.equals(actualEmails));

    }

    @Test
    void removeFolderAndFolderChilds() {
        Directory directory = new Directory("inbox");

        HashSet<String> recipients = new HashSet<>();
        recipients.add("test_recipients@gmail.com");
        recipients.add("pesho@gmail.com");
        recipients.add("test_receive@gmail.com");
        Mail mail = new Mail(new Account("test@abv.bg", "test"), recipients, "test-subject", "test-body",
                LocalDateTime.of(2022,12,8,14,14));

        List<String> fullPath = new ArrayList<>();
        fullPath.add("inbox");
        directory.addChildrenFolderToDirectory("important", fullPath);
        fullPath.add("important");
        directory.addChildrenFolderToDirectory("science",fullPath);
        directory.addChildrenFolderToDirectory("history", fullPath);
        directory.addChildrenFolderToDirectory("geography", fullPath);

        fullPath.remove("important");
        directory.addChildrenFolderToDirectory("funny", fullPath);
        directory.addChildrenFolderToDirectory("work", fullPath);
        directory.addChildrenFolderToDirectory("hobby", fullPath);
        fullPath.add("funny");
        directory.addChildrenFolderToDirectory("videos", fullPath);

        directory.addEmail("important", mail);
        directory.addEmail("videos", mail);
        directory.addEmail("inbox", mail);

        fullPath.remove("funny");

        directory.removeFolderAndFolderChilds("important", fullPath);

        HashMap<String, Set<Mail>> actualEmails = directory.getEmails();
        HashMap<String, Set<String>> actualFolders = directory.getFolders();

        HashMap<String, Set<Mail>> expectedEmails = new HashMap<>();
        HashMap<String, Set<String>> expectedFolders = new HashMap<>();

        Set<Mail> expectedMails = new HashSet<>();
        expectedMails.add(mail);
        expectedEmails.put("inbox", expectedMails);
        expectedEmails.put("videos", expectedMails);
        expectedEmails.put("work", new HashSet<>());
        expectedEmails.put("hobby", new HashSet<>());
        expectedEmails.put("funny", new HashSet<>());

        Set<String> expectedInboxChilds = new HashSet<>();
        expectedInboxChilds.add("funny");
        expectedInboxChilds.add("work");
        expectedInboxChilds.add("hobby");
        expectedFolders.put("inbox", expectedInboxChilds);

        expectedFolders.put("work", new HashSet<>());
        expectedFolders.put("hobby", new HashSet<>());

        Set<String> expectedFunnyChilds = new HashSet<>();
        expectedFunnyChilds.add("videos");
        expectedFolders.put("funny", expectedFunnyChilds);
        expectedFolders.put("videos", new HashSet<>());

        assertTrue(expectedEmails.equals(actualEmails) && expectedFolders.equals(actualFolders));
    }



}