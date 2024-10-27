# Expense Tracker Application
The Expense Tracker Application is a web-based application that allows users to manage\
their personal finances by tracking their expenses, categorizing them, and generating reports.
# Features
- Secure signup and login functionality using JWT-based authentication.
- User accounts with unique emails.
- Add and categorize expenses.
- Customizable time periods for filtering expenses and generating report.
# Technologies Used
- Java (Spring Boot)
- Spring Security for authentication
- JPA/Hibernate for ORM
- PostgreSQL for the database
- Springdoc OpenAPI for API documentation
# Prerequisites
- JDK 21 or higher
- PostgreSQL database
# How to run
- Clone the repository
- run docker-compose file to run postgres db
- open code in ide and run it
- we use spring profile for making different between dev env from prod env
- you can find the opendoc url from application.properties file
- first you must signup the account and then used the token you received in response for other request
- you also will receive the token when signin successfully
- by using that token you can use other apies too
- first add some expense 
- you can create result by calling api



