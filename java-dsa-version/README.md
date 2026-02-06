# Java + DSA Library Management System (Semester 3)

This version of the Library Management System is developed as a **Semester 3 academic project**.
It is an enhancement of the Semester 2 Java Swing + DBMS project, with a focus on
**Data Structures, logic optimization, and better code organization**.

## Key Improvements Over Semester 2
- Introduction of Data Structures for internal data handling
- Separation of responsibilities into multiple classes
- Improved issue/return logic
- Better maintainability and readability
- Database-backed persistence with structured access

## Technologies Used
- Java
- Java Swing
- MySQL
- JDBC
- Data Structures (Lists, Maps, Undo Actions)

## Project Structure
java-dsa-version/
├── database/
│ └── library_system_sem3.sql
├── src/
│ ├── Action.java
│ ├── Book.java
│ ├── DatabaseManager.java
│ ├── LibraryData.java
│ ├── LibraryUI.java

## Database Setup (Retained from Previous Version)

This version continues to use MySQL for persistent data storage, carried forward
from the Semester 2 implementation.

The primary focus of this semester is on applying Data Structures and improving
internal logic, while maintaining database connectivity for realism and continuity.

### To run the project:
- Install MySQL
- Create the database using the provided SQL file in the `database` folder
- Add MySQL Connector/J to the project classpath

MySQL Connector/J can be downloaded from:
https://dev.mysql.com/downloads/connector/j/


## Academic Note
This project is developed for learning purposes as part of my Software Engineering degree.
More advanced architectural improvements will be implemented in future semesters.

**Semester:** 3  
**Program:** BS Software Engineering  
