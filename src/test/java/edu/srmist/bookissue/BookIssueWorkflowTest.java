package edu.srmist.bookissue;

import edu.srmist.bookissue.api.BookIssueController;
import edu.srmist.bookissue.dto.BookIssueForm;
import edu.srmist.bookissue.dto.ProcessResponse;
import edu.srmist.bookissue.worker.BookIssueWorkers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Book Issue Workflow & User Task Form Tests (Lab EX-4)")
class BookIssueWorkflowTest {

    private BookIssueWorkers workers;
    private BookIssueController controller;

    @BeforeEach
    void setUp() {
        workers = new BookIssueWorkers();
        controller = new BookIssueController(Optional.empty(), workers);
    }

    @Test
    @DisplayName("Worker test: confirmBookIssue calculates 14-day due date and accession number")
    void testConfirmBookIssueWorker() {
        String today = LocalDate.now().toString();
        String expectedDue = LocalDate.now().plusDays(14).toString();

        Map<String, Object> result = workers.confirmBookIssue(
                "Sudhanshu Kumar",
                "RA2311003010001",
                "Operating System Concepts",
                today,
                "Operating systems coursework"
        );

        assertEquals("ISSUED", result.get("issueStatus"));
        assertEquals(expectedDue, result.get("dueDate"));
        assertTrue(((String) result.get("accessionNumber")).startsWith("LIB-RA2311003010001-"));
    }

    @Test
    @DisplayName("Lifecycle test: Start instance -> Waits at User Task 'Issue Book Form'")
    void testStartProcessWaitsAtUserTask() {
        ResponseEntity<ProcessResponse> startResp = controller.startProcess(Map.of());
        assertEquals(201, startResp.getStatusCode().value());

        ProcessResponse response = startResp.getBody();
        assertNotNull(response);
        assertNotNull(response.getProcessInstanceKey());
        assertEquals("book-issue", response.getBpmnProcessId());
        assertEquals("ACTIVE", response.getStatus());
        assertTrue(response.getCurrentActivity().contains("User Task 'Issue Book Form'"));
    }

    @Test
    @DisplayName("Operate verification: Completing form populates all form fields as process variables")
    void testCompleteUserTaskFormVariablesAppearInInstance() {
        // Step 1: Start instance
        ResponseEntity<ProcessResponse> startResp = controller.startProcess(Map.of());
        Long key = startResp.getBody().getProcessInstanceKey();

        // Step 2: Fill form (Tasklist simulation)
        BookIssueForm form = new BookIssueForm(
                "Sudhanshu Kumar",
                "RA2311003010001",
                "Designing Data-Intensive Applications",
                "2026-10-25",
                "Big data distributed systems project",
                true
        );

        ResponseEntity<ProcessResponse> completeResp = controller.completeTasklistForm(key, form);
        assertEquals(200, completeResp.getStatusCode().value());

        ProcessResponse completedInstance = completeResp.getBody();
        assertNotNull(completedInstance);
        assertEquals("COMPLETED", completedInstance.getStatus());

        Map<String, Object> vars = completedInstance.getVariables();
        assertEquals("Sudhanshu Kumar", vars.get("studentName"));
        assertEquals("RA2311003010001", vars.get("regNo"));
        assertEquals("Designing Data-Intensive Applications", vars.get("bookTitle"));
        assertEquals("2026-10-25", vars.get("issueDate"));
        assertEquals("Big data distributed systems project", vars.get("reason"));
        assertEquals(true, vars.get("agreeTerms"));
        assertEquals("ISSUED", vars.get("issueStatus"));
        assertEquals("2026-11-08", vars.get("dueDate"));
    }
}
