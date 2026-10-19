#!/usr/bin/env bash
# Shell test runner for Camunda 8 Lab EX-4

BASE_URL="http://localhost:8080/api/book-issue"

echo "=== [1] Start process instance ==="
START_RESP=$(curl -s -X POST "${BASE_URL}/start" -H "Content-Type: application/json" -d '{}')
echo "${START_RESP}" | jq . || echo "${START_RESP}"
echo -e "\n"

INSTANCE_KEY=$(echo "${START_RESP}" | grep -o '"processInstanceKey":[0-9]*' | cut -d: -f2)

if [ -z "$INSTANCE_KEY" ]; then
  INSTANCE_KEY="2251799813685301"
fi

echo "=== [2] Complete User Task Form for instance ${INSTANCE_KEY} ==="
COMPLETE_RESP=$(curl -s -X POST "${BASE_URL}/instances/${INSTANCE_KEY}/complete-form" \
  -H "Content-Type: application/json" \
  -d '{
    "studentName": "Sudhanshu Kumar",
    "regNo": "RA2311003010001",
    "bookTitle": "Clean Code",
    "issueDate": "2026-10-25",
    "reason": "Exam preparation",
    "agreeTerms": true
  }')
echo "${COMPLETE_RESP}" | jq . || echo "${COMPLETE_RESP}"
echo -e "\n"

echo "=== [3] Query instances and verify variables in Operate ==="
curl -s -X GET "${BASE_URL}/instances" | jq . || curl -s -X GET "${BASE_URL}/instances"
echo -e "\n"
