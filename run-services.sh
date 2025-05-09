#!/bin/bash

# Proje kök dizini
BASE_DIR=$(pwd)

# PID dosyalarının saklanacağı dizin
PID_DIR="$BASE_DIR/pids"
mkdir -p "$PID_DIR"

# Log dosyalarının saklanacağı dizin
LOG_DIR="$BASE_DIR/logs"
mkdir -p "$LOG_DIR"

# Servis JAR dosyalarının yolları
EUREKA_JAR="$BASE_DIR/eureka-server/target/eureka-server-0.0.1-SNAPSHOT.jar"
GATEWAY_JAR="$BASE_DIR/gateway/target/gateway-0.0.1-SNAPSHOT.jar"
USER_SERVICE_JAR="$BASE_DIR/user-service/target/user-service-0.0.1-SNAPSHOT.jar"
ROLE_SERVICE_JAR="$BASE_DIR/role-service/target/role-service-0.0.1-SNAPSHOT.jar"

# Servis portları
EUREKA_PORT=8761
GATEWAY_PORT=8080
USER_SERVICE_PORT=8082
ROLE_SERVICE_PORT=8083

# MySQL bağlantı bilgileri
MYSQL_USER="root"
MYSQL_PASSWORD="k67k94e9" # Burayı kendi MySQL şifrenizle değiştirin

# Renkli çıktı için
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Hata mesajı yazdırma
error() {
    echo -e "${RED}Hata: $1${NC}"
    exit 1
}

# Bilgi mesajı yazdırma
info() {
    echo -e "${GREEN}$1${NC}"
}

# Uyarı mesajı yazdırma
warn() {
    echo -e "${YELLOW}$1${NC}"
}

# JAR dosyasının varlığını kontrol et
check_jar() {
    local jar=$1
    if [ ! -f "$jar" ]; then
        error "$jar dosyası bulunamadı. Lütfen 'mvn clean install' komutunu çalıştırın."
    fi
}

# Portun serbest olduğunu kontrol et
check_port() {
    local port=$1
    local name=$2
    if netstat -tuln | grep -q ":$port "; then
        error "$name için $port portu zaten kullanılıyor. Lütfen portu serbest bırakın."
    fi
}

# MySQL bağlantısını kontrol et
check_mysql() {
    info "MySQL bağlantısı kontrol ediliyor..."
    if ! mysql -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "SELECT 1;" > /dev/null 2>&1; then
        error "MySQL bağlantısı başarısız. Kullanıcı adı ve şifreyi kontrol edin veya MySQL'ü başlatın: sudo systemctl start mysql"
    fi
    # Veritabanlarının varlığını kontrol et
    mysql -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "CREATE DATABASE IF NOT EXISTS userdb;" || error "userdb oluşturulamadı"
    mysql -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "CREATE DATABASE IF NOT EXISTS roledb;" || error "roledb oluşturulamadı"
    info "MySQL bağlantısı ve veritabanları hazır."
}

# Servis başlatma fonksiyonu
start_service() {
    local name=$1
    local jar=$2
    local port=$3
    local pid_file="$PID_DIR/$name.pid"
    local log_file="$LOG_DIR/$name.log"

    # JAR dosyasını kontrol et
    check_jar "$jar"

    # Portu kontrol et
    check_port "$port" "$name"

    if [ -f "$pid_file" ]; then
        pid=$(cat "$pid_file")
        if ps -p "$pid" > /dev/null; then
            warn "$name zaten çalışıyor (PID: $pid)"
            return
        fi
    fi

    info "Starting $name on port $port..."
    nohup java -jar "$jar" > "$log_file" 2>&1 &
    pid=$!
    echo "$pid" > "$pid_file"
    sleep 10  # Servisin başlaması için bekle
    if ps -p "$pid" > /dev/null; then
        info "$name başlatıldı (PID: $pid)"
        # İlk birkaç satır logu göster
        info "Log dosyası ($log_file) ilk 5 satır:"
        head -n 5 "$log_file"
    else
        error "$name başlatılamadı. Logları kontrol et: $log_file\nSon 10 satır:\n$(tail -n 10 "$log_file")"
    fi
}

# Servis durdurma fonksiyonu
stop_service() {
    local name=$1
    local pid_file="$PID_DIR/$name.pid"

    if [ -f "$pid_file" ]; then
        pid=$(cat "$pid_file")
        if ps -p "$pid" > /dev/null; then
            info "Stopping $name (PID: $pid)..."
            kill "$pid"
            sleep 2
            if ps -p "$pid" > /dev/null; then
                warn "Zorla durduruluyor..."
                kill -9 "$pid"
            fi
            rm -f "$pid_file"
            info "$name durduruldu"
        else
            warn "$name zaten çalışmıyor"
            rm -f "$pid_file"
        fi
    else
        warn "$name için PID dosyası bulunamadı"
    fi
}

# Tüm servisleri başlat
start_all() {
    info "Tüm servisler başlatılıyor..."
    check_mysql
    start_service "eureka-server" "$EUREKA_JAR" "$EUREKA_PORT"
    sleep 15  # Eureka'nın tamamen başlamasını bekle
    start_service "gateway" "$GATEWAY_JAR" "$GATEWAY_PORT"
    start_service "user-service" "$USER_SERVICE_JAR" "$USER_SERVICE_PORT"
    start_service "role-service" "$ROLE_SERVICE_JAR" "$ROLE_SERVICE_PORT"
    info "Tüm servisler başlatıldı. Loglar: $LOG_DIR"
    info "Eureka Dashboard: http://localhost:8761"
    info "Swagger UI (User Service): http://localhost:8082/swagger-ui.html"
    info "Swagger UI (Role Service): http://localhost:8083/swagger-ui.html"
}

# Tüm servisleri durdur
stop_all() {
    info "Tüm servisler durduruluyor..."
    stop_service "role-service"
    stop_service "user-service"
    stop_service "gateway"
    stop_service "eureka-server"
    info "Tüm servisler durduruldu"
}

# Maven ile projeyi derle
build_project() {
    info "Projeyi derliyorum (mvn clean install)..."
    mvn clean install || error "Proje derlenemedi. Lütfen Maven hatalarını kontrol edin."
    info "Proje başarıyla derlendi."
}

# Komut satırı argümanlarına göre işlem yap
case "$1" in
    start)
        build_project
        start_all
        ;;
    stop)
        stop_all
        ;;
    restart)
        stop_all
        sleep 2
        build_project
        start_all
        ;;
    *)
        echo "Kullanım: $0 {start|stop|restart}"
        exit 1
        ;;
esac
