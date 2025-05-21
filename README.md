# Ekşi Sözlük Klonu Mikroservis Mimarisi Dönüşüm Planı

Bu belge, Ekşi Sözlük klonunun mevcut paylaşımlı modüller yapısından, izole edilmiş mikroservis mimarisine dönüştürülmesi için adım adım planı içermektedir.

## 1. Mevcut Mimari Sorunları

- Entity ve repository modülleri tüm servisler tarafından paylaşılıyor
- Servisler arasında yüksek bağımlılık (tight coupling) var
- Her servis başlatıldığında tüm entity'ler taranıyor ve tablolar oluşturuluyor
- Veri izolasyonu yok, bir servis diğer servisin veritabanına doğrudan erişebiliyor

## 2. Hedef Mimari Prensipleri

### Domain-Driven Design (DDD) Yaklaşımı
- Her mikroservis kendi bounded context'ine sahip
- Her servis kendi veri modelini (entity) yönetiyor
- Servisler arası gevşek bağlantı (loose coupling)

### Veri İzolasyonu (Database per Service)
- Her servis kendi veritabanını kullanır
- Servisler birbirlerinin veritabanlarına doğrudan erişemez

### Servisler Arası İletişim
- Senkron iletişim: REST API (OpenFeign)
- Asenkron iletişim: Message Queue (RabbitMQ)

## 3. Hedef Proje Yapısı

```
eksiclone/
│
├── common/                              # Ortak bileşenler
│   ├── common-dto/                      # Servisler arası DTO'lar
│   └── common-util/                     # Ortak utility sınıfları
│
├── infrastructure/                      # Altyapı servisleri  
│   ├── api-gateway/                     # API Gateway
│   ├── discovery-service/               # Eureka Service
│   └── config-server/                   # Config Service
│
├── domain-services/                     # Domain servisleri  
│   ├── user-service/                    # Kullanıcı yönetimi
│   │   ├── src/main/java/
│   │   │   └── in.batur.eksiclone.user/
│   │   │       ├── api/                 # Controller'lar
│   │   │       ├── config/              # Konfigürasyonlar
│   │   │       ├── domain/              # Entity sınıfları
│   │   │       ├── dto/                 # DTO sınıfları
│   │   │       ├── exception/           # Exception'lar
│   │   │       ├── mapper/              # Dönüşümler
│   │   │       ├── repository/          # Repository'ler
│   │   │       └── service/             # Servis katmanı
│   │   └── pom.xml
│   │
│   ├── entry-service/                   # Entry yönetimi
│   ├── topic-service/                   # Topic yönetimi
│   ├── favorite-service/                # Favoriler
│   ├── notification-service/            # Bildirimler
│   ├── message-service/                 # Mesajlaşma
│   ├── moderation-service/              # Moderasyon
│   ├── file-service/                    # Dosyalar
│   ├── auth-service/                    # Kimlik doğrulama
│   ├── search-service/                  # Arama
│   └── statistics-service/              # İstatistikler
│
└── pom.xml                              # Ana pom.xml
```

## 4. Dönüşüm Aşamaları

### Aşama 1: Proje Yapısını Değiştirme
- [x] Yeni proje yapısını oluştur
- [ ] Mevcut modülleri yeni yapıya uygun şekilde taşı
- [ ] Ortak utility sınıfları oluştur

### Aşama 2: Domain Modellerini Ayrıştırma
- [ ] Her servis için domain entity'lerini ayrı paketlere taşı
- [ ] Entity'lerdeki dış referansları (diğer domain'lere ait) ID tabanlı yapıya dönüştür
- [ ] Servislere özgü repository'leri oluştur

### Aşama 3: Veritabanı İzolasyonu
- [ ] Her servis için ayrı veritabanı oluştur
- [ ] Veritabanı bağlantı ayarlarını güncelle
- [ ] @EntityScan ve @EnableJpaRepositories anotasyonlarını her servise özgü olarak düzenle

### Aşama 4: Servisler Arası İletişim
- [ ] Ortak DTO'ları tanımla
- [ ] OpenFeign ile REST istemcileri oluştur
- [ ] RabbitMQ entegrasyonu ve event yapısını tanımla

### Aşama 5: API Gateway ve Service Discovery
- [ ] API Gateway rotalarını düzenle
- [ ] Service discovery entegrasyonunu test et
- [ ] Güvenlik ve kimlik doğrulama ayarlarını yap

### Aşama 6: Test ve Deployment
- [ ] Her servisi ayrı ayrı test et
- [ ] Entegrasyon testlerini çalıştır
- [ ] Docker ve Docker Compose yapılandırmalarını güncelle

## 5. Servis İletişim Şablonu Örnekleri

### Örnek 1: Entry Oluşturma İşlemi
1. Client, `entry-service`'e POST isteği yapar
2. `entry-service` yeni entry'yi kaydeder
3. `entry-service` bir "EntryCreatedEvent" yayınlar
4. `notification-service` ve `statistics-service` bu olayı dinler ve gerekli işlemleri yapar

### Örnek 2: Kullanıcı Bilgilerini Getirme
1. `entry-service`, entryler için yazar bilgisine ihtiyaç duyar
2. OpenFeign ile `user-service`'e HTTP isteği yapar
3. `user-service` minimal kullanıcı bilgilerini (id, username, displayName) döndürür
4. `entry-service` bu bilgileri DTO'suna ekler

## 6. Entity Dönüşüm Örnekleri

### Mevcut Entity İlişkisi
```java
// Entry.java - Mevcut
@Entity
public class Entry extends BaseEntity {
    @ManyToOne
    private User author;
    // ...
}
```

### Hedef Entity İlişkisi
```java
// Entry.java - Hedef
@Entity
public class Entry extends BaseEntity {
    @Column(name = "author_id")
    private Long authorId;
    
    @Column(name = "author_username")
    private String authorUsername;
    // ...
}
```

## 7. Mikroservis Best Practices

1. **Single Responsibility**: Her servis sadece bir iş yapar ve o konuda uzmanlaşır
2. **Database per Service**: Her servis kendi veritabanını yönetir
3. **Private Tables**: Hiçbir servis başka bir servisin tablolarına erişemez
4. **API Composition**: Veri birleştirme işlemi API Gateway veya istemci tarafında yapılır
5. **Event-Driven Communication**: Servisler arasında asenkron iletişim tercih edilir
6. **Resilience**: Hata durumlarına karşı dayanıklılık (Circuit Breaker, Retry, Fallback)
7. **Monitoring & Tracing**: Merkezi izleme ve dağıtık izleme sistemi

## 8. Veri Tutarlılığı Stratejisi

Mikroservis mimarisinde ACID yerine BASE (Basically Available, Soft state, Eventually consistent) yaklaşımı benimsenir:

1. **Saga Pattern**: Dağıtık işlemler için telafi edici işlemler (compensating transactions)
2. **Event Sourcing**: Durum değişikliklerini event olarak kaydetme
3. **CQRS**: Command Query Responsibility Segregation

## 9. İleriki Adımlar

- [ ] CI/CD pipeline yapılandırması
- [ ] Merkezi loglama sistemi
- [ ] Performans izleme araçları
- [ ] Otomatik ölçeklendirme yapılandırması
- [ ] Servis mesh mimarisi (Istio)

Bu dönüşüm planı takip edilerek, ekşi sözlük klonu uygulaması modern mikroservis mimarisine uygun hale getirilecektir. Her aşama tamamlandıkça, sistem daha izole, ölçeklenebilir ve yönetilebilir olacaktır.