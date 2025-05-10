# Eksiclone Microservices Application

## Proje Özeti
Eksiclone, Java ve Spring Boot kullanılarak geliştirilmiş mikroservis mimarisine sahip bir forum uygulamasıdır. Kullanıcıların kayıt olup giriş yapabileceği, rollerle yetkilendirileceği ve içerik paylaşabileceği bir platform sunar.

## Teknolojiler
- Java 21
- Spring Boot 3.4.5
- Spring Cloud 2024.0.1
- Spring Security
- JWT (JSON Web Token)
- MariaDB
- RabbitMQ
- Flyway Migration
- Lombok
- MapStruct
- Springdoc OpenAPI

## Proje Mimarisi

### Modüller
- **entity**: Veritabanı entity sınıfları
- **repository**: JPA repository sınıfları
- **security**: JWT güvenlik yapılandırması ve kontrolleri
- **user-service**: Kullanıcı yönetimi servisi
- **role-service**: Rol yönetimi servisi
- **gateway**: API Gateway
- **eureka-server**: Service discovery server
- **config-server**: Merkezi yapılandırma sunucusu

## Başlatma Sırası
1. **eureka-server**: Service discovery için
2. **config-server**: Merkezi yapılandırma servisi için
3. **user-service**, **role-service**: Temel servisler
4. **gateway**: API Gateway

## Proje Yapılandırması

### Çevresel Değişkenler
Proje aşağıdaki çevresel değişkenlere ihtiyaç duyar:

```properties
# Config Server
CONFIG_SERVER_URL=http://localhost:8888

# Eureka Server
EUREKA_SERVER_URL=http://localhost:8761/eureka/

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=eksiclone
RABBITMQ_PASSWORD=eksiclone_password

# Veritabanı (service'lerde)
SPRING_DATASOURCE_URL=jdbc:mariadb://localhost:3306/eksidb
SPRING_DATASOURCE_USERNAME=eksiuser
SPRING_DATASOURCE_PASSWORD=eksipassword

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# Varsayılan Şifreler (Sadece ilk kurulumda kullanılır)
DEFAULT_ADMIN_PASSWORD=admin123
DEFAULT_MODERATOR_PASSWORD=moderator123
DEFAULT_USER_PASSWORD=user123
```

## Güvenlik İyileştirmeleri

1. **JWT Anahtar Yönetimi**: Güvenli bir şekilde RSA anahtar çiftleriyle imzalama
2. **Ortam Değişkenleri**: Hassas bilgiler ortam değişkenleri ile sağlanıyor
3. **Şifre Güvenliği**: Şifreler BCrypt ile hashlenip saklanıyor
4. **CORS/CSRF Yapılandırması**: Güvenli CORS ve CSRF yapılandırması
5. **RabbitMQ İçin Mesaj Güvenliği**: Mesaj içerikleri şifreleniyor

## API Dokümantasyonu
Swagger UI ile erişilebilir:
- Gateway: `http://localhost:8080/swagger-ui.html`
- User Service: `http://localhost:8081/swagger-ui.html`
- Role Service: `http://localhost:8082/swagger-ui.html`

## Geliştirme Ortamı Kurulumu

### Ön Koşullar
- Java 21
- Maven
- Docker (RabbitMQ ve MariaDB için)
- Git

### RabbitMQ Kurulumu
```bash
docker run -d --name eksiclone-rabbitmq -p 5672:5672 -p 15672:15672 -e RABBITMQ_DEFAULT_USER=eksiclone -e RABBITMQ_DEFAULT_PASS=eksiclone_password rabbitmq:management
```

### MariaDB Kurulumu
```bash
docker run -d --name eksiclone-mariadb -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=eksidb -e MYSQL_USER=eksiuser -e MYSQL_PASSWORD=eksipassword mariadb:latest
```

### JWT Anahtarlarını Oluşturma
```bash
mkdir -p ./keys
openssl genrsa -out ./keys/private.key 2048
openssl rsa -in ./keys/private.key -pubout -out ./keys/public.key
```

### Projeyi Derleme ve Çalıştırma
```bash
mvn clean install
cd eureka-server && mvn spring-boot:run
cd config-server && mvn spring-boot:run
cd user-service && mvn spring-boot:run
cd role-service && mvn spring-boot:run
cd gateway && mvn spring-boot:run
```
