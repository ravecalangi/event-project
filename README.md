# School Management System

## Description
A Java-based School Management System with role-based dashboards for Admin, Faculty, and Students. The system includes enrollment management, attendance tracking, subject management, login authentication, and database connectivity using Java OOP principles.

## Features
- Admin Dashboard for managing system operations
- Student Dashboard interface
- Faculty Dashboard interface
- Login and authentication system
- Enrollment form management
- Attendance tracking system
- Subject management form
- Database connection integration
- Sidebar navigation UI
- Custom UI themes and styling
- Input validation utilities
- Reusable UI components

## Technologies Used
- Java
- Java Swing (GUI)
- JDBC (Database Connectivity)
- OOP (Object-Oriented Programming)

## How to Run
1. Download or clone the project.
2. Open the project in NetBeans or any Java IDE.
3. Configure your database connection inside `DBConnection.java`.
4. Compile the project:
```bash
javac *.java
```
5. Run the application:
```bash
java LoginForm
```

## Usage
1. Launch the application.
2. Login using your credentials.
3. Access dashboards based on user roles:
   - Admin
   - Faculty
   - Student
4. Manage attendance, enrollment, and subjects using the available forms.

### Example

**Input:**
```text
Username: admin
Password: admin123
```

**Output:**
```text
Login Successful
Opening Admin Dashboard...
```

## Project Structure
```text
AdminDashboard.java
Attendance.java
AttendanceIcon.java
DBConnection.java
EnrollmentForm.java
FacultyDashboard.java
LoginForm.java
RoundBorder.java
Sidebar.java
StudentDashboard.java
SubjectForm.java
Theme.java
UI.java
Validator.java
```

## License
MIT License
