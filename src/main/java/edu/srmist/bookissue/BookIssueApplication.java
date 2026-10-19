package edu.srmist.bookissue;

import io.camunda.client.annotation.Deployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main Spring Boot Application for SRMIST Workflow Automation Lab (EX-4).
 * Demonstrates User Task Form integration with Camunda Tasklist and Operate.
 */
@SpringBootApplication
@Deployment(resources = {
        "classpath:bpmn/book-issue.bpmn",
        "classpath:bpmn/book-issue.form",
        "classpath:bpmn/event-registration.bpmn",
        "classpath:bpmn/event-registration.form"
})
public class BookIssueApplication {

    private static final Logger log = LoggerFactory.getLogger(BookIssueApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BookIssueApplication.class, args);
    }

    @Bean
    public CommandLineRunner startupBanner() {
        return args -> {
            log.info("==========================================================================");
            log.info(" SRMIST Workflow Automation Lab (EX-4): Library Book Issue Process       ");
            log.info(" Deployed Models:                                                        ");
            log.info("   1. Library Book Issue Workflow (book-issue.bpmn + book-issue.form)     ");
            log.info("   2. Campus Event Registration   (event-registration.bpmn + form)       ");
            log.info(" Form Fields: studentName, regNo, bookTitle, issueDate, reason, agreeTerms");
            log.info(" REST Endpoints:                                                         ");
            log.info("   - POST http://localhost:8080/api/book-issue/start                     ");
            log.info("   - POST http://localhost:8080/api/book-issue/instances/{key}/complete-form");
            log.info("   - GET  http://localhost:8080/api/book-issue/instances                ");
            log.info("==========================================================================");
        };
    }
}
