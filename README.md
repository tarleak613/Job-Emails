# 📧 Access Email Job Alerts - Spring Boot Application

This is the **v2** of the Spring Boot application that connects to your Gmail inbox, reads emails, and filters **job/internship-related emails** using a combination of keyword-based and AI-based classification via Google Gemini API.

---

## 🚀 What's New in Version 2?

- Uses **Google Gemini API** to classify emails as job-related or not with AI-powered NLP  
- Better accuracy compared to keyword-only filtering  
- Handles email content preprocessing and JSON-based prompt generation for Gemini  
- Still supports Gmail IMAP connection for fetching emails  
- Improved JSON parsing of Gemini API responses  
- Maintains legacy keyword filtering as fallback (optional)  

## 🔧 Technologies Used

- Java 17  
- Spring Boot 3.5.0  
- Jakarta Mail (JavaMail)  
- Maven  
- Java HTTP Client  
- Jackson (JSON parsing)  
- Google Gemini API (for AI classification)

## 🔧 Dependencies Used

```xml
<!-- Jakarta Mail (Angus Mail - for Spring Boot 3+) -->
<dependency>
    <groupId>com.sun.mail</groupId>
    <artifactId>jakarta.mail</artifactId>
    <version>2.0.1</version>
</dependency>

<!-- Jakarta Activation (needed by Angus Mail) -->
<dependency>
    <groupId>com.sun.activation</groupId>
    <artifactId>jakarta.activation</artifactId>
    <version>2.0.1</version>
</dependency>

<!-- Twilio (for WhatsApp push notifications) -->
<dependency>
    <groupId>com.twilio.sdk</groupId>
    <artifactId>twilio</artifactId>
    <version>10.9.0</version>
</dependency>


## 📦 How to Run

### 1. Clone the repository
<pre> git clone https://github.com/YOUR_USERNAME/access-email.git  </pre>
<pre> cd access-email </pre>
<pre>git checkout v2</pre>


### 2. Configure Gmail Credentials
Add to `src/main/resources/application.properties`:
<pre> email.username=your_email@gmail.com </pre>
<pre> email.password=your_app_password </pre>
<pre>gemini.api.url=https://generativelanguage.googleapis.com/v1beta2/models/gemini-2.0-flash:generateMessage
gemini.api.key=YOUR_GEMINI_API_KEY</pre>
<pre>twilio.account_sid=your_sid
twilio.auth_token=your_aut_token
twilio.from_whatsapp=whatsapp:your_from_number
twilio.to_whatsapp=whatsapp:your_to_number</pre>

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

- [x] Integrate Gemini API for AI-based classification
- [x] Implement AI/NLP-based filtering
- [ ] Support HTML email rendering
- [ ] Add pagination for large inboxes
- [x] Implement API authentication
- [x] Add attachment handling

## 📄 License  
Personal/educational use only (Not licensed for production)

## ✍️ Author  
**Ayush Kumar**  
📧 [ayushbhagat1213@gmail.com](mailto:ayushbhagat1213@gmail.com)
🌐 [https://www.linkedin.com/in/bhagat-ayush/](https://www.linkedin.com/in/bhagat-ayush/)
