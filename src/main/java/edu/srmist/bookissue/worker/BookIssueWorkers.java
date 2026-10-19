package edu.srmist.bookissue.worker;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Camunda 8 Job Worker for Library Book Issue Process.
 * Matches service task definition type: 'confirm-book-issue'.
 */
@Component
public class BookIssueWorkers {

    private static final Logger log = LoggerFactory.getLogger(BookIssueWorkers.class);

    @JobWorker(type = "confirm-book-issue")
    public Map<String, Object> confirmBookIssue(
            @Variable(name = "studentName") String studentName,
            @Variable(name = "regNo") String regNo,
            @Variable(name = "bookTitle") String bookTitle,
            @Variable(name = "issueDate") String issueDate,
            @Variable(name = "reason") String reason) {

        log.info("[Worker: confirm-book-issue] Processing book issue confirmation for student: {} ({})",
                studentName, regNo);
        log.info("[Worker: confirm-book-issue] Book requested: '{}', Issue Date: '{}', Reason: '{}'",
                bookTitle, issueDate, reason);

        String dueDate;
        try {
            LocalDate parsedDate = LocalDate.parse(issueDate);
            dueDate = parsedDate.plusDays(14).toString();
        } catch (DateTimeParseException | NullPointerException ex) {
            dueDate = LocalDate.now().plusDays(14).toString();
        }

        String accessionNumber = "LIB-" + regNo + "-" + (issueDate != null ? issueDate : LocalDate.now().toString());
        String issueStatus = "ISSUED";

        log.info("[Worker: confirm-book-issue] Book '{}' successfully ISSUED to {} (Reg: {}). Return Due Date: {}. Accession No: {}",
                bookTitle, studentName, regNo, dueDate, accessionNumber);

        Map<String, Object> result = new HashMap<>();
        result.put("dueDate", dueDate);
        result.put("accessionNumber", accessionNumber);
        result.put("issueStatus", issueStatus);
        return result;
    }
}
