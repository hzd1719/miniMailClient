package org.example.parts;

import org.example.exceptions.RuleAlreadyDefinedException;

import java.util.*;

public class Rule implements Comparable<Rule>{
    private String folderPath;
    private String ruleDefinition;
    private int priority;
    private final List<String> ruleConditionsNames = new ArrayList<>(4);
    private Set<String> ruleConditionsSubject;
    private Set<String> ruleConditionsSubjectOrBody;
    private Set<String> ruleConditionsRecipients;
    private Set<String> ruleConditionsFrom;

    public Rule(String folderPath, String ruleDefinition, int priority) {
        this.folderPath = folderPath;
        this.priority = priority;
        setRuleConditionsNames();
        setRuleDefinition(ruleDefinition);
        extractRuleConditions();
    }

    private void setRuleConditionsNames() {
        this.ruleConditionsNames.add("subject-includes:");
        this.ruleConditionsNames.add("subject-or-body-includes:");
        this.ruleConditionsNames.add("recipients-includes:");
        this.ruleConditionsNames.add("from:");
    }

    public List<String> getRuleConditionsNames() {
        return ruleConditionsNames;
    }

    public String getRuleDefinition() {
        return ruleDefinition;
    }

    public boolean isValidRuleDefinition(String ruleDefinition) {
        List<String> listOfConditions = Arrays.asList(ruleDefinition.split("\\R"));
        //System.out.println(listOfConditions.size());
        //System.out.println(listOfConditions);
        List<String> checkDuplicateNames = new ArrayList<>();
        /*if(listOfConditions.size() > 4 || listOfConditions.size() < 1 || (listOfConditions.size() == 1 && (listOfConditions.get(0).isBlank()))) {
            throw new IllegalArgumentException("Invalid format for rule definition, there must be between 1 and 4 rule conditions!");
        }*/

        for(String condition : listOfConditions) {
            int doubleDotsIndex = condition.indexOf(":");
            String nameOfRuleCondition = condition.substring(0, doubleDotsIndex+1);
            //System.out.println("Rule: " + nameOfRuleCondition);
            if(!this.ruleConditionsNames.contains(nameOfRuleCondition)) {
                throw new IllegalArgumentException("Invalid rule condition!");
            }

            else {
                if(checkDuplicateNames.contains(nameOfRuleCondition)) {
                    throw new RuleAlreadyDefinedException("Duplicate rule conditions!");
                }

                else {
                    checkDuplicateNames.add(nameOfRuleCondition);
                }
            }
        }
        return true;
    }

    public void setRuleDefinition(String ruleDefinition) {
        if(isValidRuleDefinition(ruleDefinition)) {
            this.ruleDefinition = ruleDefinition;
        }
    }

    public void extractRuleConditions() {
        String[] arrayOfConditions = ruleDefinition.split("\\R");
        for(String condition : arrayOfConditions) {
            int doubleDotsIndex = condition.indexOf(":");
            String rules = condition.substring(doubleDotsIndex+1);
            String rulesNames = condition.substring(0, doubleDotsIndex+1);
            switch (rulesNames) {
                case "subject-includes:" ->
                        this.ruleConditionsSubject = new HashSet<>(Arrays.asList(rules.replaceAll("\\s", "").split(",")));
                case "subject-or-body-includes:" ->
                        this.ruleConditionsSubjectOrBody = new HashSet<>(Arrays.asList(rules.replaceAll("\\s", "").split(",")));
                case "recipients-includes:" ->
                        this.ruleConditionsRecipients = new HashSet<>(Arrays.asList(rules.replaceAll("\\s", "").split(",")));
                case "from:" -> this.ruleConditionsFrom = new HashSet<>(Arrays.asList(rules.replaceAll("\\s", "").split(",")));
            }

        }
    }

    public Set<String> getRuleConditionsSubject() {
        return ruleConditionsSubject;
    }

    public Set<String> getRuleConditionsSubjectOrBody() {
        return ruleConditionsSubjectOrBody;
    }

    public Set<String> getRuleConditionsRecipients() {
        return ruleConditionsRecipients;
    }

    public Set<String> getRuleConditionsFrom() {
        return ruleConditionsFrom;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }


    public int getPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return priority == rule.priority && Objects.equals(ruleConditionsNames, rule.ruleConditionsNames) && Objects.equals(ruleConditionsSubject, rule.ruleConditionsSubject) && Objects.equals(ruleConditionsSubjectOrBody, rule.ruleConditionsSubjectOrBody) && Objects.equals(ruleConditionsRecipients, rule.ruleConditionsRecipients) && Objects.equals(ruleConditionsFrom, rule.ruleConditionsFrom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(priority, ruleConditionsNames, ruleConditionsSubject, ruleConditionsSubjectOrBody, ruleConditionsRecipients, ruleConditionsFrom);
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }


    @Override
    public int compareTo(Rule o) {
        return Integer.compare(this.priority, o.priority);
    }
}
