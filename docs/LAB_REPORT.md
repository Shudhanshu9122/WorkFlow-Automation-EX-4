# 📄 SRMIST Lab Report: Exercise 4 (EX-4)
## Subject: Workflow Automation Lab
## Topic: User Task Form Modeling, Linking, and Variable Verification in Camunda 8

* **Student Name:** Sudhanshu Kumar
* **Registration Number:** RA2411026010959
* **Process ID:** `book-issue`
* **Form ID:** `book-issue-form`
* **Cluster:** Camunda 8 SaaS / Local Gateway
* **GitHub Repository:** [https://github.com/Shudhanshu9122/WorkFlow-Automation-EX-4](https://github.com/Shudhanshu9122/WorkFlow-Automation-EX-4)

---

## 1. Objective
To design and model a business process containing a **User Task** in Camunda Modeler, build a custom **Form** with at least three fields (Name, Date, Reason), link the form to the User Task via its **Form ID**, deploy both to Camunda 8, execute an instance, claim and complete the task in **Camunda Tasklist**, and verify in **Camunda Operate** that all entered form inputs persist as process variables.

---

## 2. Process Design & BPMN Specification

### Chosen Business Process: Library Book Issue Workflow (`book-issue`)
1. **Start Event:** *Book Issue Requested*
2. **User Task:** *Issue Book Form* (Linked to form ID: `book-issue-form`)
3. **Service Task:** *Confirm Book Issue* (Worker type: `confirm-book-issue` computes due date and accession number)
4. **End Event:** *Book Issued Successfully*

```
[Start Event] ──> [User Task: Issue Book Form] ──> [Service Task: Confirm Issue] ──> [End Event]
                          │
                   (book-issue-form)
```

---

## 3. Form Definition & Variable Keys

The form `book-issue-form` contains 6 distinct fields with explicit variable keys:

| Field Label | Component Type | Variable Key | Sample Value | Mandatory |
| :--- | :--- | :--- | :--- | :---: |
| **Student Name** | Text Field | `studentName` | `"Sudhanshu Kumar"` | Yes |
| **Registration Number** | Text Field | `regNo` | `"RA2311003010001"` | Yes |
| **Book Title / ISBN** | Text Field | `bookTitle` | `"Clean Code"` | Yes |
| **Issue Date** | Text Field | `issueDate` | `"2026-10-25"` | Yes |
| **Reason / Academic Purpose** | Text Area | `reason` | `"Reference for Cloud Architecture course"` | Yes |
| **Agree to Terms** | Checkbox | `agreeTerms` | `true` | No |

---

## 4. Step-by-Step Lab Execution & Screenshot Guide

### Step 1 & 2: BPMN Process Modeling & Form Creation
1. Open **Camunda Web Modeler** at `console.camunda.io`.
2. Create diagram `book-issue.bpmn` with Process ID = `book-issue`.
3. Create form `book-issue.form` with Form ID = `book-issue-form`.
4. Select the **User Task**, open the Properties Panel &rarr; **Form** &rarr; select **Camunda Form** &rarr; Link to `book-issue-form`.

> **Screenshot 1 Required:** The BPMN process diagram canvas in Modeler showing the User Task linked to the form.
> **Screenshot 2 Required:** The Form Builder showing the fields (`studentName`, `regNo`, `bookTitle`, `issueDate`, `reason`).

---

### Step 3: Deployment to Camunda 8 Cluster
Click **Deploy** in Modeler (or launch the Spring Boot application which auto-deploys via `@Deployment`).

---

### Step 4: Start Instance & Claim in Tasklist
1. Start an instance from Modeler or send `POST http://localhost:8080/api/book-issue/start`.
2. Open **Camunda Tasklist** (`https://<region>.tasklist.camunda.io/<cluster-id>`).
3. Find the pending task **"Issue Book Form"**.
4. Click **Assign to me** (Claim task).
5. Fill in the form fields:
   * **Student Name:** `Sudhanshu Kumar`
   * **Registration Number:** `RA2311003010001`
   * **Book Title:** `Clean Code`
   * **Issue Date:** `2026-10-25`
   * **Reason:** `Reference for Cloud Architecture course`
6. Click **Complete Task**.

> **Screenshot 3 Required:** Tasklist showing the claimed form filled with the above student and book information before completion.

---

### Step 5: Verification in Camunda Operate
1. Open **Camunda Operate** (`https://<region>.operate.camunda.io/<cluster-id>`).
2. Navigate to **Processes** &rarr; `Library Book Issue Workflow`.
3. Click on the completed/running instance key.
4. Expand the **Variables** panel at the bottom.

> **Screenshot 4 Required:** The Operate instance view showing that all form values (`studentName`, `regNo`, `bookTitle`, `issueDate`, `reason`, `dueDate`, `accessionNumber`) are recorded as process variables.

---

## 5. Conclusion
The exercise successfully demonstrates human-in-the-loop task orchestration using Camunda 8 Forms. Data captured through the Tasklist UI seamlessly propagates into the Zeebe execution context as strongly-typed process variables accessible by downstream automated service workers.
