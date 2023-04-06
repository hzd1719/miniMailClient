package org.example;

import org.example.helpers.Directory;
import org.example.parts.Account;
import org.example.parts.Mail;
import org.example.parts.Rule;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
       /* Outlook outlook = new Outlook();


        outlook.createFolder(account2.name(), "inbox/naked");
        outlook.createFolder(account2.name(), "inbox/fake");
        outlook.createFolder(account2.name(), "inbox/fake/scam"); */
       /* String str = """
                subject-includes: <list-of-keywords>
                subject-or-body-includes: <list-of-keywords>
                recipients-includes: <list-of-recipient-emails>
                from: <sender-email>""";
        String str2 = "subject-includes: mjt, izpit, 2022\n" +
                "subject-or-body-includes: izpit\n" +
                "recipients: pesho@gmail.com, gosho@gmail.com\n"+
                "from: stoyo@fmi.bg";
        System.out.println(str2);
        System.out.println(str2.charAt(34));
        List<String> strings = Arrays.asList(str2.split("\\R"));
        System.out.println(strings.get(1));



        String newline = System.getProperty("line.separator");
        String mystr = """
               Tovae moqt string
               Dali ima new separator
               Se chudq az
               """;

        List<String> nystrings = Arrays.asList(mystr.split("\\R"));
        System.out.println(nystrings.get(1));
        //System.out.println(str.charAt(36));
        System.out.println("New start:");
        Rule rule = new Rule("inbox", str2, 4);
        Rule rule2 = new Rule("inboxsdadsa", str2, 4);
        Set<Rule> ruleSet = new HashSet<>();
        ruleSet.add(rule);
        System.out.println("Contains: ");
        System.out.println(ruleSet.contains(rule2));
        /*System.out.println(rule.equals(rule2));
        System.out.println(rule.getRuleConditionsFrom());
        System.out.println(rule2.getRuleConditionsFrom()); */
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
        System.out.println(fullPath);
        System.out.println(directory);
        directory.removeFolderAndFolderChilds("important", fullPath);
        System.out.println(directory);
        System.out.println("After");
    }
}
