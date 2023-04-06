package org.example.parts;

import org.example.exceptions.RuleAlreadyDefinedException;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RuleTest {


    String ruleDefinitionTest = "subject-includes: mjt, izpit, 2022\n" +
            "subject-or-body-includes: izpit\n" +
            "recipients-includes: pesho@gmail.com, gosho@gmail.com\n"+
            "from: stoyo@fmi.bg";

    
    Rule rule = new Rule("inbox/important", ruleDefinitionTest, 5);
    @Test
    void getRuleConditionsSubjectTest() {
        Set<String> rulesTest = new HashSet<>();
        rulesTest.add("mjt");
        rulesTest.add("izpit");
        rulesTest.add("2022");
        assertTrue(rulesTest.containsAll(rule.getRuleConditionsSubject()) && rule.getRuleConditionsSubject().containsAll(rulesTest));
    }

    @Test
    void getRuleConditionsSubjectOrBodyTest() {
        Set<String> rulesTest = new HashSet<>();
        rulesTest.add("izpit");
        assertTrue(rulesTest.containsAll(rule.getRuleConditionsSubjectOrBody()) && rule.getRuleConditionsSubjectOrBody().containsAll(rulesTest));
    }

    @Test
    void getRuleConditionsRecipientsTest() {
        Set<String> rulesTest = new HashSet<>();
        rulesTest.add("pesho@gmail.com");
        rulesTest.add("gosho@gmail.com");
        assertTrue(rulesTest.containsAll(rule.getRuleConditionsRecipients()) && rule.getRuleConditionsRecipients().containsAll(rulesTest));
    }

    @Test
    void getRuleConditionsFromTest() {
        Set<String> rulesTest = new HashSet<>();
        rulesTest.add("stoyo@fmi.bg");
        assertTrue(rulesTest.containsAll(rule.getRuleConditionsFrom()) && rule.getRuleConditionsFrom().containsAll(rulesTest));
    }


    @Test
    void testDuplicationConditions() {
        String ruleDefinitionTestDuplicates = "subject-includes: mjt, izpit, 2022\n" +
                "subject-includes: izpit\n" +
                "recipients-includes: pesho@gmail.com, gosho@gmail.com\n"+
                "from: stoyo@fmi.bg";
        assertThrows(RuleAlreadyDefinedException.class, ()-> new Rule("inbox/important", ruleDefinitionTestDuplicates, 5),
                "Expected RuleAlreadyDefinedException when there duplicate rules!");
    }


    @Test
    void testInvalidConditions() {
        String ruleDefinitionTestInvalidNames = "da se popalni";
        assertThrows(IllegalArgumentException.class, ()-> new Rule("inbox/important", ruleDefinitionTestInvalidNames, 5),
                "Expected Illegal argument exception!");

    }



}