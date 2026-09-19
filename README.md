# Canteen Online Ordering System

Java 17 + Spring Boot + Thymeleaf + H2 দিয়ে তৈরি beginner-friendly MVP।

## চালানোর নিয়ম

1. JDK 17 বা তার পরের ভার্সন ইনস্টল করুন।
2. PowerShell খুলে project folder-এ যান:

   ```powershell
   cd C:\Users\ASUS\Documents\canteen-ordering
   ```

3. অ্যাপ চালান:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

4. ব্রাউজারে খুলুন:
   - Customer: http://localhost:8080
   - Admin: http://localhost:8080/admin
   - H2 database console: http://localhost:8080/h2-console

## Admin login

Admin page এখন login-protected। ডিফল্ট development credentials:

- Username: `admin`
- Password: `change-me-now`

Production বা public deployment-এর আগে PowerShell-এ নিজের credentials দিন:

```powershell
$env:CANTEEN_ADMIN_USERNAME="your-admin-name"
$env:CANTEEN_ADMIN_PASSWORD="a-long-random-password"
.\mvnw.cmd spring-boot:run
```

H2 console-এ JDBC URL হবে `jdbc:h2:file:./data/canteen`। bKash/Nagad-এর merchant credentials ছাড়া আসল gateway transaction চালু করা নিরাপদ নয়; সেগুলো environment variable হিসেবে `application.properties`-এ প্রস্তুত রাখা হয়েছে। Gateway provider-এর official API credentials পাওয়ার পর server-side payment service যোগ করতে হবে—কখনোই secret frontend বা Git-এ রাখবেন না।

## বর্তমান ফিচার

- ছবি, দাম, stock এবং availability সহ menu
- খাবার নির্বাচন ও pickup time
- Cash on pickup, bKash, Nagad method নির্বাচন
- Order status: Pending → Preparing → Ready → Completed
- Admin order management
- Menu item যোগ ও availability toggle
- আজকের sales total
