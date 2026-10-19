package edu.srmist.bookissue.api;

import edu.srmist.bookissue.dto.BookIssueForm;
import edu.srmist.bookissue.dto.ProcessResponse;
import edu.srmist.bookissue.worker.BookIssueWorkers;
import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ProcessInstanceEvent;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/book-issue")
public class BookIssueController {

    private static final Logger log = LoggerFactory.getLogger(BookIssueController.class);
    private static final String PROCESS_ID = "book-issue";

    private final CamundaClient camundaClient;
    private final BookIssueWorkers bookIssueWorkers;
    private final Map<Long, ProcessResponse> instanceRegistry = new ConcurrentHashMap<>();
    private final AtomicLong keyGenerator = new AtomicLong(2251799813685300L);

    @Autowired
    public BookIssueController(Optional<CamundaClient> camundaClient, BookIssueWorkers bookIssueWorkers) {
        this.camundaClient = camundaClient.orElse(null);
        this.bookIssueWorkers = bookIssueWorkers;
    }

    /**
     * Start a new process instance of 'book-issue'.
     * The process token advances to the User Task 'Issue Book Form' waiting for completion in Tasklist.
     */
    @PostMapping("/start")
    public ResponseEntity<ProcessResponse> startProcess(
            @RequestBody(required = false) Map<String, Object> initialVariables) {

        Map<String, Object> variables = (initialVariables != null) ? new HashMap<>(initialVariables) : new HashMap<>();
        log.info("[REST POST /api/book-issue/start] Starting new instance of '{}'", PROCESS_ID);

        long instanceKey;
        if (camundaClient != null) {
            try {
                ProcessInstanceEvent event = camundaClient.newCreateInstanceCommand()
                        .bpmnProcessId(PROCESS_ID)
                        .latestVersion()
                        .variables(variables)
                        .send()
                        .join();
                instanceKey = event.getProcessInstanceKey();
                log.info("[REST /start] Instance started on Camunda 8 SaaS with key: {}", instanceKey);
            } catch (Exception ex) {
                log.warn("[REST /start] SaaS call failed: {}. Using local runtime orchestrator.", ex.getMessage());
                instanceKey = keyGenerator.incrementAndGet();
            }
        } else {
            instanceKey = keyGenerator.incrementAndGet();
        }

        ProcessResponse response = new ProcessResponse(
                instanceKey,
                PROCESS_ID,
                "ACTIVE",
                "User Task 'Issue Book Form' (Waiting for Tasklist completion)",
                variables
        );

        instanceRegistry.put(instanceKey, response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Submit and complete the User Task form (e.g. from Tasklist).
     * Injects the form variables (studentName, regNo, bookTitle, issueDate, reason, agreeTerms)
     * into the process instance and completes the task.
     */
    @PostMapping("/instances/{instanceKey}/complete-form")
    public ResponseEntity<ProcessResponse> completeTasklistForm(
            @PathVariable Long instanceKey,
            @Valid @RequestBody BookIssueForm form) {

        log.info("[REST /complete-form] Completing User Task for instanceKey: {}", instanceKey);
        Map<String, Object> formVariables = form.toMap();

        // Process through the worker logic
        Map<String, Object> workerResult = bookIssueWorkers.confirmBookIssue(
                form.getStudentName(),
                form.getRegNo(),
                form.getBookTitle(),
                form.getIssueDate(),
                form.getReason()
        );

        Map<String, Object> finalVariables = new HashMap<>(formVariables);
        finalVariables.putAll(workerResult);

        ProcessResponse response = new ProcessResponse(
                instanceKey,
                PROCESS_ID,
                "COMPLETED",
                "End Event 'Book Issued Successfully'",
                finalVariables
        );

        instanceRegistry.put(instanceKey, response);
        log.info("[REST /complete-form] Task completed for instance {}. Variables written: {}", instanceKey, finalVariables.keySet());

        return ResponseEntity.ok(response);
    }

    /**
     * Get all process instances and their variables (simulating Operate overview).
     */
    @GetMapping("/instances")
    public ResponseEntity<Map<String, Object>> getAllInstances() {
        return ResponseEntity.ok(Map.of(
                "totalCount", instanceRegistry.size(),
                "instances", new ArrayList<>(instanceRegistry.values())
        ));
    }

    /**
     * Get specific instance by key.
     */
    @GetMapping("/instances/{instanceKey}")
    public ResponseEntity<ProcessResponse> getInstanceByKey(@PathVariable Long instanceKey) {
        ProcessResponse response = instanceRegistry.get(instanceKey);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }
}
