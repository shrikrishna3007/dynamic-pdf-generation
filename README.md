Project - Dynamic PDF Generation.
-----------------------------------------------


---------------------------------------------------
This Spring Boot application provides two REST APIs for dynamic PDF handling. The Generate API is a POST endpoint that accepts invoice details as JSON input, processes the data, and generates a PDF file. After successful creation, the service returns an HTTP 200 response along with a confirmation message. The Download API is a GET endpoint where the client passes the PDF file identifier (hash code / file name), and the service returns the corresponding PDF as a downloadable file in the response. The project demonstrates a clean backend design focused on file generation, retrieval, and API-driven document handling.

Technologies used: 
-  Java 17
-  Spring Boot 3
-  Junit
-  Mockito
-  iText
------------------------------------------------
Other related dependencies:
- Spring Web
- Lombok
- itextpdf
- jackson-databind
- Starter-test
---------------------------------------------------
API Design:

1. http://localhost:8080/pdf/generate
2. http://localhost:8080/pdf/download

Generate API: 
HTTP Method: Post method
Data: Invoice details -JSON data.
Response: HTTP status code 200 with String response.

Download API:
HTTP Method: Get method
Data: PDF name - Hash code
Response - PDF File data.
