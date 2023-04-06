package org.example.mail;

import org.example.exceptions.AccountAlreadyExistsException;
import org.example.exceptions.AccountNotFoundException;
import org.example.exceptions.FolderNotFoundException;
import org.example.helpers.Directory;
import org.example.parts.Rule;
import org.example.parts.Account;
import org.example.parts.Mail;
import org.example.parts.MailMetaData;

import java.util.*;



public class Outlook implements MailClient{
    private final HashMap<String, Account> accounts;
    private final HashMap<String, Directory> accountInbox;
    private final HashMap<String, Directory> accountSent;
    private final HashMap<String, Set<Rule>> accountRules;

    public Outlook() {
        this.accounts = new HashMap<>();
        this.accountInbox = new HashMap<>();
        this.accountSent = new HashMap<>();
        this.accountRules = new HashMap<>();
    }

    @Override
    public Account addNewAccount(String accountName, String email) {
        if(accountName == null || accountName.isEmpty() || accountName.isBlank()) {
            throw new IllegalArgumentException("Account name can't be null!");
        }

        if(email == null || email.isEmpty() || email.isBlank()) {
            throw new IllegalArgumentException("Email can't be null!");
        }

        if(accounts.containsKey(accountName)) {
            throw new AccountAlreadyExistsException("Account with this name already exists");
        }

        if(checkForExistingEmail(email) != null) {
            throw new AccountAlreadyExistsException("Account with this email already exists");
        }
        Account account = new Account(email, accountName);
        accounts.put(accountName, account);
        //Set up inbox directory for the account
        accountInbox.put(accountName, new Directory("inbox"));
        //Set up sent(emails) directory for the account
        accountSent.put(accountName, new Directory("sent"));
        return account;
    }

    public Account checkForExistingEmail(String email) {
        for (Map.Entry<String, Account> mapElement : this.accounts.entrySet()) {
            Account account = mapElement.getValue();
            if(email.equals(account.emailAddress())) {
                return account;
            }
        }

        return null;

    }

    @Override
    public void createFolder(String accountName, String path) {
        if(accountName == null || accountName.isEmpty() || accountName.isBlank()) {
            throw new IllegalArgumentException("Account name can't be null!");
        }

        if(path == null || path.isEmpty() || path.isBlank()) {
            throw new IllegalArgumentException("Path can't be null!");
        }

        if(!accounts.containsKey(accountName)) {
            throw new AccountNotFoundException("Account with such a name does not exist!");
        }

        List<String> pathList = new ArrayList<>(Arrays.asList(path.split("/")));
        //Get the name of the folder to be created
        String newFolder = pathList.get(pathList.size()-1);
        //Remove that folder from the path list
        pathList.remove(pathList.size()-1);
        //Get the inbox directory of the account
        Directory directoryFolder = accountInbox.get(accountName);
        //Add the new folder to a specific path starting with inbox
        directoryFolder.addChildrenFolderToDirectory(newFolder, pathList);
        //System.out.println(directoryFolder);
        //Save the updated set to the hashmap
        this.accountInbox.put(accountName, directoryFolder);
    }

    @Override
    public void addRule(String accountName, String folderPath, String ruleDefinition, int priority) {
        if(accountName == null || accountName.isEmpty() || accountName.isBlank()) {
            throw new IllegalArgumentException("Account name can't be null!");
        }

        if(folderPath == null || folderPath.isEmpty() || folderPath.isBlank()) {
            throw new IllegalArgumentException("Folder path can't be null!");
        }

        if(ruleDefinition == null || ruleDefinition.isEmpty() || ruleDefinition.isBlank()) {
            throw new IllegalArgumentException("Rule definition can't be null!");
        }

        if(priority < 1 || priority > 10) {
            throw new IllegalArgumentException("Priority must be a number between 1 and 10!");
        }

        if(!accounts.containsKey(accountName)) {
            throw new AccountNotFoundException("Account with such a name does not exist!");
        }

        List<String> paths = new ArrayList<>(Arrays.asList(folderPath.split("/")));
        accountInbox.get(accountName).checkIfPathIsValid(paths);

        Rule rule = new Rule(folderPath, ruleDefinition, priority);
        this.accountRules.putIfAbsent(accountName, new HashSet<>());

        Set<Rule> rules = this.accountRules.get(accountName);
        if(rules.contains(rule)) {
            throw new IllegalArgumentException("Such a rule already exists or is in conflict with another one!");
        }
        rules.add(rule);
        this.accountRules.put(accountName, rules);
    }

    public void removeRule(String accountName, String folderPath, String ruleDefinition, int priority) {
        Rule rule = new Rule(folderPath, ruleDefinition, priority);
        Set<Rule> rules = this.accountRules.get(accountName);
        if(!rules.remove(rule)) {
            throw new IllegalArgumentException("Such a rule does not exist!");
        }
        this.accountRules.put(accountName, rules);
    }

    @Override
    public void receiveMail(String accountName, String mailMetadata, String mailContent) {
        System.out.println("Enter Recieve Mail!");
        Set<Rule> rules = this.accountRules.get(accountName);
        MailMetaData mailMetaDataClass = new MailMetaData(mailMetadata);
        String sender = mailMetaDataClass.getSender();
        System.out.println("Sender: " + sender);
        String subject = mailMetaDataClass.getSubject();
        List<String> recipientsList = mailMetaDataClass.getRecipients();

        List<Rule> applyRules = new ArrayList<>();
        for(Rule rule : rules) {
            Set<String> ruleFrom = rule.getRuleConditionsFrom();
            boolean containsFrom = checkIfCollectionContainsString(ruleFrom, sender);

            Set<String> ruleSubject = rule.getRuleConditionsSubject();
            boolean containsSubject = checkSubjectRule(ruleSubject, subject);

            Set<String> ruleSubjectOrBody = rule.getRuleConditionsSubjectOrBody();
            boolean containsSubjectOrBody = checkSubjectOrBodyRule(ruleSubjectOrBody, mailContent, subject);

            Set<String> ruleRecipients = rule.getRuleConditionsRecipients();
            boolean containsRecipients = checkRecipientsRule(ruleRecipients, recipientsList);

            boolean applyRule = (containsFrom && containsSubject && containsSubjectOrBody && containsRecipients);
            if(applyRule) {
                applyRules.add(rule);
            }
        }
        Set<String> recipieintsSet = new HashSet<>(recipientsList);
        Account account = checkForExistingEmail(sender);
        Mail mail = new Mail(account, recipieintsSet, subject,mailContent, mailMetaDataClass.getReceived());
        String folder = "inbox";

        if(applyRules.size() > 0) {
            Collections.sort(applyRules);
            Rule rule = applyRules.get(0);
            String path = rule.getFolderPath();
            List<String> pathList = new ArrayList<>(Arrays.asList(path.split("/")));
            folder = pathList.get(pathList.size() - 1);
        }
        System.out.println(folder);

        accountInbox.get(accountName).addEmail(folder, mail);

    }

    public boolean checkIfCollectionContainsString(Collection<String> collection, String element) {
        return collection.contains(element);
    }

    public boolean checkSubjectRule(Set<String> ruleSubject, String subject) {
        for(String rule:ruleSubject) {
            if(!subject.contains(rule)) {
                return false;
            }
        }

        return true;
    }
    public boolean checkSubjectOrBodyRule(Set<String> ruleSubjectOrBody, String mailContent, String subject) {
        for(String rule:ruleSubjectOrBody) {
            if(!mailContent.contains(rule) && !subject.contains(rule)) {
                return false;
            }
        }

        return true;
    }

    public boolean checkRecipientsRule(Set<String> ruleRecipients, List<String> recipientsList) {
        for(String rule : ruleRecipients) {
            if (recipientsList.contains(rule)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public Collection<Mail> getMailsFromFolder(String account, String folderPath) {
        if(account == null || account.isEmpty() || account.isBlank()) {
            throw new IllegalArgumentException("Account name can't be null!");
        }

        if(folderPath == null || folderPath.isEmpty() || folderPath.isBlank()) {
            throw new IllegalArgumentException("Folder path can't be null!");
        }

        if(!this.accounts.containsKey(account)) {
            throw new AccountNotFoundException("Account with such a name does not exist!");
        }

        List<String> folderPathList = Arrays.asList(folderPath.split("/"));
        String rootFolder = folderPathList.get(0);

        HashMap<String, Set<Mail>> allEmails = accountInbox.get(account).getEmails();
        String folder = folderPathList.get(folderPathList.size() - 1);

        if(!this.accountInbox.get(account).getFolders().containsKey(folder)) {
            throw new FolderNotFoundException("Such a folder does not exist");
        }

        if(rootFolder.equals("inbox")) {
            this.accountInbox.get(account).checkIfPathIsValid(folderPathList);
        }

        return allEmails.get(folder);

    }

    @Override
    public void sendMail(String accountName, String mailMetadata, String mailContent) {
        if(accountName == null || accountName.isEmpty() || accountName.isBlank()) {
            throw new IllegalArgumentException("Account name can't be null!");
        }

        if(mailMetadata == null || mailMetadata.isEmpty() || mailMetadata.isBlank()) {
            throw new IllegalArgumentException("Meta data can't be null!");
        }

        if(mailContent == null || mailContent.isEmpty() || mailContent.isBlank()) {
            throw new IllegalArgumentException("Mail content can't be null!");
        }

        MailMetaData mailMetaDataClass = new MailMetaData(mailMetadata);
        Account senderAccount = this.accounts.get(accountName);
        Set<String> recipientsSet = new HashSet<>(mailMetaDataClass.getRecipients());
        Mail mail = new Mail(senderAccount, recipientsSet, mailMetaDataClass.getSubject(), mailContent, mailMetaDataClass.getReceived());

        //Add email to the sent folder
        this.accountSent.get(accountName).addEmail("sent", mail);

        //automatically add sender if such does not exist in the metadata
        if( mailMetaDataClass.getSender() == null || mailMetaDataClass.getSender().isEmpty() || mailMetaDataClass.getSender().isBlank()) {
            String accountEmail = senderAccount.emailAddress();
            mailMetadata += (System.lineSeparator() + "sender: " + accountEmail + System.lineSeparator());
            System.out.println(mailMetadata);
        }

        callReceiveMailForExistingUsers(recipientsSet, mailMetadata, mailContent);
    }

    public void callReceiveMailForExistingUsers(Set<String> recipientsSet, String mailMetadata, String mailContent) {
        for(String recipient: recipientsSet) {
            for (Map.Entry<String, Account> mapElement : this.accounts.entrySet()) {
                Account account = mapElement.getValue();
                if(recipient.equals(account.emailAddress())) {
                    receiveMail(account.name(), mailMetadata, mailContent);
                }
            }
        }
    }

    public HashMap<String, Directory> getAccountInbox() {
        return accountInbox;
    }

    public HashMap<String, Account> getAccounts() {
        return accounts;
    }

    public HashMap<String, Directory> getAccountSent() {
        return accountSent;
    }

    public HashMap<String, Set<Rule>> getAccountRules() {
        return accountRules;
    }
}
