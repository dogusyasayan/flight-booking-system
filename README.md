# ✈ Flight Booking Management System

Bu proje, bir **Uçuş Rezervasyon ve Yönetim Sistemi** olarak geliştirildi. Sistem iki farklı dashboard'tan oluşmaktadır:
- **Admin Paneli**: Yöneticiler uçuşları oluşturabilir, güncelleyebilir, silebilir ve uçuşlara koltuk ekleyebilir.
- **Passenger Paneli**: Kullanıcılar kalkış zamanı geçmemiş uçuşları listeleyebilir ve koltuk satın alabilir.

Proje, **Spring Boot** altyapısında geliştirildi ve **Spring Security** kullanılarak yetkilendirme mekanizması sağlandı. Token bazlı kimlik doğrulama ile **Admin** ve **Passenger** rolleri ayrıldı.

## ⚡ Teknolojiler

Projede aşağıdaki teknolojiler kullanılmıştır:

- **Java 11**  
- **Spring Boot 3**  
- **Spring Security** *(Role-based Authentication)*  
- **Spring Data JPA**  
- **H2 Database** *(Test ve Gerçek ortamda kullanılmaktadır.)*  
- **JUnit 5 & Mockito** *(Unit ve Integration Testler için)*  
- **Swagger UI** *(API dokümantasyonu için)*  
- **HTML, CSS, JavaScript** *(Frontend yapısı için)*  

## 💡 Sistem Mimarisi

Proje, **Spring Boot MVC** yapısına uygun olarak geliştirildi. Ana bileşenler:

- **AuthService**: Kullanıcı kaydı, kimlik doğrulama ve JWT token yönetimi.
- **FlightService**: Uçuşları listeleme, ekleme, güncelleme ve silme işlemleri.
- **SeatService**: Koltuk işlemleri ve uçuşlara koltuk ekleme.
- **PaymentService**: Ödeme hesaplama ve mock ödeme yönetimi.
- **Exception Handling**: Global hata yönetimi.
- **Lock Mechanism**: Aynı anda çift talebi önlemek için kullanıldı.

## 🛠 Veritabanı Yapısı

Projede **H2 Database** kullanılmaktadır.

![Database Schema](https://github.com/user-attachments/assets/ba69d0b0-bcda-403a-ab90-aa63fa43868d)

Tablolar:
- **Users** (id, username, password, role)
- **Flights** (id, flight_code, departure, arrival, description)
- **Seats** (id, flight_id, seat_number, price, status)
- **Payments** (id, user_id, flight_id, amount, payment_date)

## 📝 API Endpointleri

### 🔐 Kimlik Doğrulama
- **POST /auth/sign-up** - Yeni kullanıcı kaydı
- **POST /auth/sign-in** - Kullanıcı girişi

### ✈ Uçuş Yönetimi
- **GET /flight/all** - Tüm uçuşları listele
- **POST /flight** - Yeni uçuş ekle (*Admin*)
- **PUT /flight/{flightCode}** - Uçuş bilgilerini güncelle (*Admin*)
- **DELETE /flight/{flightCode}** - Uçuşu sil (*Admin*)

### 🪑 Koltuk Yönetimi
- **GET /seat/{flightCode}** - Belirtilen uçuşun koltuklarını getir
- **POST /seat/{flightCode}** - Uçuşa koltuk ekle (*Admin*)
- **PUT /seat** - Koltuk bilgisini güncelle (*Admin*)

### 💳 Ödeme Yönetimi
- **POST /payment/calculate/{flightCode}** - Ödeme tutarını hesapla
- **POST /payment/{flightCode}/** - Ödeme yap (*Passenger*)

## 💡 Unit ve Integration Testler

- **Testler JUnit 5 ve Mockito** kullanılarak yazıldı.
- **%100 Unit Test Coverage** sağlamak için her servis için testler yazıldı.
- **H2 Database** kullanılarak test ortamı oluşturuldu.

## 🔒 Güvenlik ve Yetkilendirme
- **Spring Security & JWT** kullanıldı.
- **Admin ve Passenger rolleri** ayrıldı.
- **Token bazlı authentication** sağlandı.

## 🌋 Ödeme ve Lock Mekanizması
- **Mock Payment System** gerçek bir ödeme işlemine gerek duymadan sistemin çalışmasını sağlar.
- **Lock Mechanism** aynı anda gelen çoklu ödeme taleplerinin önüne geçmek için kullanıldı.

## 🎨 Kullanıcı Arayüzü (UI Screenshots)

Aşağıda **Flight Booking Management System** projesine ait **Admin Paneli** ve **Passenger Paneli** ekran görüntülerini bulabilirsiniz.

### 🔐 **Kimlik Doğrulama & Giriş Ekranı**
- Kullanıcılar sisteme **Passenger** veya **Admin** olarak kayıt olabilir.
- **Spring Security** kullanılarak JWT tabanlı kimlik doğrulama sağlanır.
- Kullanıcı giriş yaptıktan sonra rolüne göre yönlendirilir.

📌 **Auth Dashboard:**
> *![image](https://github.com/user-attachments/assets/06df3edf-3416-479c-a89d-410c857b5e04)*

---

### ✈ **Admin Panel**
- **Uçuşları yönetebilir** (oluşturma, güncelleme, silme).
- **Farklı fiyatlandırmalarla koltuk ekleyebilir** (Business, Economy gibi sınıflar).
- **Uçuş ve koltuk bilgilerini düzenleyebilir**.

📌 **Admin Dashboard:**
> *![image](https://github.com/user-attachments/assets/fd1ed361-89fd-4130-8620-abf23891ffe0)*
---

### 🏷 **Passenger Paneli**
- Kullanıcılar kalkış zamanı geçmemiş uçuşları listeleyebilir.
- Uçuş detaylarını inceleyerek **koltuk satın alabilir**.
- Ödeme işlemi **Mock Payment System** ile gerçekleştirilir.

📌 **Passenger Dashboard:**
> *![image](https://github.com/user-attachments/assets/9a126a9e-081b-4346-bf86-0947b46717f1)*

## ⚡ Kurulum

1. **Projeyi Klonlayın:**
```sh
git clone https://github.com/username/flight-booking-system.git
cd flight-booking-system
```
2. **Bağımlılıkları Yükleyin:**
```sh
mvn clean install
```
3. **Uygulamayı Başlatın:**
```sh
mvn spring-boot:run
```

## 📑 Swagger UI Kullanımı

![Swagger UI](https://github.com/user-attachments/assets/3981237b-f5e3-4a7a-80df-c499a6ffeda1)

API endpointlerini test etmek ve dökümantasyona erişmek için **Swagger UI** kullanabilirsiniz:
```sh
http://localhost:8080/swagger-ui/index.html
```

