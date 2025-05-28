# 📧 Access Email Job Alerts - Spring Boot Application

This is a Spring Boot application that connects to your Gmail inbox, reads emails, and filters **job/internship-related emails** using keyword-based matching.

## 🚀 Features

- Connects securely to Gmail using IMAP
- Reads all emails in your inbox
- Filters out spam and non-relevant emails
- Returns emails containing keywords:  
  `job`, `jobs`, `intern`, `internship`, `career`, `hiring`, `placement`, `opportunity`
- REST API endpoint to fetch filtered emails

## 🔧 Technologies Used

- Java 17
- Spring Boot 3.5.0
- Jakarta Mail (JavaMail)
- Maven

## 📦 How to Run

### 1. Clone the repository
<pre> git clone https://github.com/YOUR_USERNAME/access-email.git  </pre>
<pre> cd access-email </pre>


### 2. Configure Gmail Credentials
Add to `src/main/resources/application.properties`:
<pre> email.username=your_email@gmail.com </pre>
<pre> email.password=your_app_password </pre>

> 💡 Use an [App Password](https://myaccount.google.com/apppasswords) if 2FA is enabled

### 3. Run the application
<pre> ./mvnw spring-boot:run </pre>

Or run `AccessEmailApplication` directly in your IDE (IntelliJ/VS Code).

## 🔍 API Endpoint

**GET** `/jobEmails`  
Fetches job/internship-related emails

**Sample Response:**
<pre>
[
{
"from": "careers@example.com",
"to": "your_email@gmail.com",
"subject": "Internship Opportunity at ABC Corp",
"body": "We are hiring interns for summer 2025..."
}
]
</pre>


## ✅ To Do

- [ ] Implement AI/NLP-based filtering
- [ ] Support HTML email rendering
- [ ] Add pagination for large inboxes
- [ ] Implement API authentication
- [ ] Add attachment handling

## 📄 License  
Personal/educational use only (Not licensed for production)

## ✍️ Author  
**Ayush Kumar**  
📧 [ayushbhagat1213@gmail.com](mailto:ayushbhagat1213@gmail.com)
🌐 [https://www.linkedin.com/in/bhagat-ayush/](https://www.linkedin.com/in/bhagat-ayush/)
