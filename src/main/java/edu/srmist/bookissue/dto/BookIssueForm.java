package edu.srmist.bookissue.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

public class BookIssueForm {

    @NotBlank(message = "studentName is required")
    private String studentName;

    @NotBlank(message = "regNo is required")
    private String regNo;

    @NotBlank(message = "bookTitle is required")
    private String bookTitle;

    @NotBlank(message = "issueDate is required")
    private String issueDate;

    @NotBlank(message = "reason is required")
    private String reason;

    private Boolean agreeTerms = true;

    public BookIssueForm() {
    }

    public BookIssueForm(String studentName, String regNo, String bookTitle, String issueDate, String reason, Boolean agreeTerms) {
        this.studentName = studentName;
        this.regNo = regNo;
        this.bookTitle = bookTitle;
        this.issueDate = issueDate;
        this.reason = reason;
        this.agreeTerms = agreeTerms != null ? agreeTerms : true;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("studentName", studentName);
        map.put("regNo", regNo);
        map.put("bookTitle", bookTitle);
        map.put("issueDate", issueDate);
        map.put("reason", reason);
        map.put("agreeTerms", agreeTerms);
        return map;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Boolean getAgreeTerms() {
        return agreeTerms;
    }

    public void setAgreeTerms(Boolean agreeTerms) {
        this.agreeTerms = agreeTerms;
    }
}
