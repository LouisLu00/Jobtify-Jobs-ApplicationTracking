# Jobtify-ApplicationTracking

## Endpoints
***
### Underline means OPTIONAL
### Get all application by user_id
* URL: /api/application/user/{userId}/applications<u>?status={status}</u>
* Method: GET
* Response Codes:
    * 200 OK
    * 404 Not Found

### Get all applications by job_id
* URL: /api/application/job/{jobId}/applications<u>?status={status}</u>
* Method: GET
* Response Codes:
    * 200 OK
    * 404 Not Found

### Create a new application
* URL: /api/application/{userId}/{jobId}/applications
* Method: POST
* Request body:
  {
  "timeOfApplication": "2024-10-28T10:30:00",
  "applicationStatus": "applied",
  "notes": "This is a new application"
  }
* Response Codes:
    * 201 Application created successfully
    * 404 Invalid Input

### Update an existing application
* URL: /api/application/applications/{applicationId}
* Method: PUT
* Request Params: /api/application/applications/{applicationId}<u>?status={status}</u>&<u>notes={notes}</u>&<u>timeOfApplication={timeOfApplication}</u>
* Response Codes:
    * 200 Application updated successfully
    * 404 Application not found

### Delete an application by ID
* URL: /api/application/applications/{applicationId}
* Method: DELETE
* Response Codes:
    * 200 OK
    * 404 Not Found