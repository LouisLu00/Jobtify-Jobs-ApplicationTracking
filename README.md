
# Jobtify - Application Tracking

This API provides endpoints for managing job applications, including creation, retrieval, updating, and deletion. Each endpoint ensures proper validation of users and jobs.

## Endpoints Documentation

### 1. Get All Applications by User ID
**URL:** `/api/application/user/{userId}/applications?status={status}`  
**Method:** `GET`

**Description:**  
Retrieve all applications for a specific user. <u>Optionally, filter applications by their status.</u>

**Response Codes:**
- `200 OK` - Successfully retrieved applications.
- `404 Not Found` - User not found.

---

### 2. Get All Applications by Job ID
**URL:** `/api/application/job/{jobId}/applications?status={status}`  
**Method:** `GET`

**Description:**  
Retrieve all applications for a specific job. <u>Optionally, filter applications by their status.</u>

**Response Codes:**
- `200 OK` - Successfully retrieved applications.
- `404 Not Found` - Job not found.

---

### 3. Get an Application by Application ID
**URL:** `/api/application/applications/{applicationId}`  
**Method:** `GET`

**Description:**  
Retrieve a specific application using its unique application ID.

**Response Codes:**
- `200 OK` - Successfully retrieved the application.
- `404 Not Found` - Application not found.

---

### 4. Create a New Application
**URL:** `/api/application/{userId}/{jobId}/applications`  
**Method:** `POST`

**Request Body:**
```json
{
  "timeOfApplication": "2024-10-28T10:30:00",
  "applicationStatus": "applied",
  "notes": "This is a new application"
}
```

**Description:**  
Create a new application for a user and job. User and job must exist. The application status **must be valid**.

**Response Codes:**
- `201 Created` - Application created successfully.
- `400 Bad Request` - Invalid application status.
- `404 Not Found` - User or job not found.
- `408 Request Timeout` - Request to user or job service timed out.
- `503 Service Unavailable` - User or job server unavailable.

---

### 5. Update an Existing Application
**URL:** `/api/application/applications/{applicationId}`  
**Method:** `PUT`

**Request Parameters (Optional):**
- `status` (string): New application status.
- `notes` (string): Updated notes for the application.
- `timeOfApplication` (datetime): Updated time of application.

**Description:**  
Update the status, notes, or time of an existing application.

**Response Codes:**
- `200 OK` - Application updated successfully.
- `400 Bad Request` - Invalid application status.
- `404 Not Found` - Application not found.

---

### 6. Delete an Application by ID
**URL:** `/api/application/applications/{applicationId}`  
**Method:** `DELETE`

**Description:**  
Delete a specific application using its unique application ID.

**Response Codes:**
- `204 No Content` - Application deleted successfully.
- `404 Not Found` - Application not found.

---

## Notes
1. **Validation:**
  - The `ValidationService` ensures that the user and job IDs exist before processing requests.
  - If the user or job service is unavailable or times out, appropriate HTTP error codes (`503`, `408`) are returned.

2. **Application Statuses:**  
   Valid statuses are:
  - `saved`
  - `applied`
  - `withdraw`
  - `offered`
  - `rejected`
  - `interviewing`
  - `archived`
  - `screening`

3. **Error Codes and Handling:**
  - Common error codes like `400`, `404`, `408`, and `503` are used to provide detailed feedback on failures.
  - Ensure the request payload and parameters adhere to the documented schema to avoid errors.

---
