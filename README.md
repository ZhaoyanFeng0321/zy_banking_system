# **ZyBank - A Modern Banking Application**  
This is a secure and feature-rich online banking system. This project implements core banking functionalities such as account management, transactions, and authentication.

## **Features**  
✅ User Registration & Authentication  
✅ Account Creation & Management  
✅ Deposit & Withdrawal Transactions  
✅ Fund Transfers Between Accounts  
✅ Transaction History  
✅ RESTful API with Spring Boot  
✅ Database Integration with PostgreSQL  

## **Tech Stack**  
- **Backend:** Java, Spring Boot, Spring Security, JPA (Hibernate)  
- **Database:** PostgreSQL  
- **API Testing:** Postman  
- **Version Control:** Git & GitHub  

## **Installation & Setup**  
### **1️⃣ Clone the Repository**  
```sh
git clone https://github.com/yourusername/zybank.git
cd zybank
```

### **2️⃣ Configure Database**  
Update `application.properties` (or `application.yml`) with your PostgreSQL credentials:  
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/yourdb
spring.datasource.username=your_username
spring.datasource.password=your_password

```
Alternative: Connect to AWS RDS
```properties
psql -h yourdb.abcdefg12345.us-east-1.rds.amazonaws.com -U yourusername -d yourdb -p 5432
```
Connect to Redis
```properties
brew services start redis
redis-cli
```

### **3️⃣ Build & Run the Application**  
```sh
./mvnw spring-boot:run
```

### **4️⃣ API Endpoints**  
- **Register User:** `POST /api/v1/user/register`  
- **Login:** `POST /api/v1/auth/login`  
- **Create Account:** `POST /api/v1/accounts`  
- **Check Balance:** `GET /api/v1/accounts/{accountId}/balance`  
- **Transfer Funds:** `POST /api/v1/accounts/transfer`  
- **View Transactions:** `GET /api/v1/accounts/{accountId}/transactions`  

## **Improvements & Extension Ideas**  
✅ Adding Redis for JWT token refresh and expiration \
✅ Sets access token & refresh token in an HttpOnly, Secure cookie\
✅ centralized error handling\
🔲 Redis Rate Limiting Mechanism\
🔲 Distributed Caching Implementation\
🔲 Retry Mechanism Implementation\
🔲 Load Balancer Configuration:\
🔲 Speed up by:
1. short-live access token (no blacklist check)
2. Refresh token revocation with Redis (only checked on refresh) 
3. Keep JWT payloads small and verify signature in each service.

 🔲 Implement RLS for PostgreSql/SpringBoot Filter\
 🔲 Adding security to sensitive information(card#, cvv, pin...)\
...
