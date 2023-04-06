package org.example.parts;

import org.example.parts.Account;

import java.time.LocalDateTime;
import java.util.Set;

public record Mail(Account sender, Set<String> recipients, String subject, String body, LocalDateTime received) {
        }