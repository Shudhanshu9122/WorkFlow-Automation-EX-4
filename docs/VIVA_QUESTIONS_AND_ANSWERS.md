# 🎓 Camunda 8 Lab EX-4: Viva Voce Questions & Answers
**Course:** Workflow Automation (SRMIST)  
**Lab Assignment:** Exercise 4 (EX-4) — User Task Form Modeling, Linking & Variables  
**Author:** Sudhanshu Kumar  

---

## Question 1: How is a Camunda Form linked to a User Task in BPMN 2.0?

### Answer:
A Camunda Form is linked to a User Task using its unique **Form ID** via the Zeebe extension element `<zeebe:formDefinition>` inside the task definition XML:

```xml
<bpmn:userTask id="Task_FillBookIssueForm" name="Issue Book Form">
  <bpmn:extensionElements>
    <zeebe:formDefinition formId="book-issue-form" />
  </bpmn:extensionElements>
</bpmn:userTask>
```

* In Camunda Web Modeler, this is configured in the **Properties Panel** under **Form** &rarr; Type: **Camunda Form** &rarr; Link to form: `book-issue-form`.
* When both the BPMN diagram and the `.form` schema file are deployed to the cluster, Tasklist automatically resolves the form definition by its `formId` and renders the dynamic UI when the task is opened.

---

## Question 2: What happens to form fields when a task is completed in Tasklist?

### Answer:
When a user clicks **Complete Task** in Tasklist:
1. **Payload Extraction:** Tasklist packages all field values into a JSON object using each component's assigned **Key** (e.g. `studentName: "Sudhanshu Kumar"`, `days: 3`, `reason: "Coursework"`).
2. **Zeebe CompleteJobCommand:** Tasklist issues an internal `CompleteJobCommand` to the Zeebe engine containing this JSON dictionary as variables.
3. **Variable Scope Assignment:** Zeebe commits these key-value pairs into the process instance's variable scope.
4. **Operate Persistence:** The variables are immediately visible under the **Variables** panel in **Camunda Operate** and are accessible to all subsequent downstream gateway conditions, service workers, or notification tasks.

---

## Question 3: What is the difference between "Claiming" (Assigning) and "Completing" a task in Tasklist?

### Answer:

| Action | API / Engine State | Effect in Tasklist |
| :--- | :--- | :--- |
| **Claim / Assign to me** | Sets the `assignee` variable / metadata on the user task to the logged-in user. | Prevents other operators/students from concurrently modifying or completing the task. The task moves to the user's "Assigned to me" inbox. |
| **Complete Task** | Submits form variables to Zeebe and advances the process token past the User Task. | The task disappears from the active Tasklist queue, and execution proceeds to the next BPMN node. |

---

## Question 4: How can a downstream Service Task access variables entered in the User Task form?

### Answer:
Because form fields become standard Zeebe process variables upon task completion, any downstream Spring Boot Job Worker can inject them directly using the `@Variable` annotation:

```java
@JobWorker(type = "confirm-book-issue")
public Map<String, Object> confirmBookIssue(
        @Variable String studentName,
        @Variable String regNo,
        @Variable String bookTitle,
        @Variable String issueDate,
        @Variable String reason) {
    
    // Perform automated processing, validation, or database updates
    String dueDate = LocalDate.parse(issueDate).plusDays(14).toString();
    return Map.of("dueDate", dueDate, "issueStatus", "ISSUED");
}
```

The values returned by the worker are also committed back to Operate as additional process variables.

---

## Question 5: How are validation rules (e.g., Mandatory fields) enforced in Camunda Forms?

### Answer:
Within the form JSON schema, each component has a `validate` object:
```json
{
  "label": "Student Name",
  "type": "textfield",
  "key": "studentName",
  "validate": {
    "required": true,
    "minLength": 3
  }
}
```
* **Client-side enforcement:** Tasklist prevents the user from clicking the **Complete Task** button until all required fields satisfy their validation rules, displaying inline visual warnings.
