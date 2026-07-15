# Развертывание платежной системы на сервере

Инструкция рассчитана на схему, где каждый микросервис лежит в отдельном GitHub-репозитории, а на сервере все контейнеры подключаются к общей Docker-сети `ps-observability-net`.

## 1. Что должно быть на сервере

Рекомендуемый минимальный набор:

```bash
sudo apt update
sudo apt install -y git ca-certificates curl
```

Установите Docker Engine и Compose plugin по официальной инструкции Docker для вашей ОС. После установки проверьте:

```bash
docker --version
docker compose version
```

Для production лучше использовать домен и HTTPS через reverse proxy. На первом этапе можно запускать по IP и прямым портам.

## 2. Подготовка директорий и репозиториев

Создайте рабочую директорию:

```bash
sudo mkdir -p /opt/ps
sudo chown -R "$USER":"$USER" /opt/ps
cd /opt/ps
```

Склонируйте репозитории:

```bash
git clone <github-url>/ps_core.git
git clone <github-url>/ps_transfer.git
git clone <github-url>/ps_cashback.git
git clone <github-url>/notification-service.git
git clone <github-url>/bff.git
git clone <github-url>/ps_front.git
```

Если frontend деплоится отдельно, `ps_front` можно не класть на этот сервер.

## 3. Общие переменные

В каждом репозитории, где есть `.env.example`, создайте `.env`:

```bash
cp .env.example .env
```

Минимально везде должны совпадать:

```env
PUBLIC_SCHEME=http
PUBLIC_HOST=your-server-ip-or-domain
```

Для HTTPS через reverse proxy:

```env
PUBLIC_SCHEME=https
PUBLIC_HOST=your-domain.example
```

Важно: `TRANSFER_INTERNAL_TOKEN` в `ps_core/.env` должен совпадать с `CORE_INTERNAL_TOKEN` в `ps_transfer/.env`. `CASHBACK_INTERNAL_TOKEN` в `ps_core/.env` должен совпадать с `CASHBACK_INTERNAL_TOKEN` в `ps_cashback/.env`.

Для BFF cookies:

```env
# прямой HTTP-доступ
SESSION_COOKIE_SECURE=false
SESSION_COOKIE_SAME_SITE=Lax

# HTTPS через reverse proxy
SESSION_COOKIE_SECURE=true
SESSION_COOKIE_SAME_SITE=None
```

## 4. Порядок запуска

Сначала поднимается core, потому что его compose создает общую сеть и инфраструктуру: PostgreSQL, Kafka, Keycloak, Redis, Grafana, Prometheus, Loki, Tempo и Alloy.

```bash
cd /opt/ps/ps_core
docker compose up -d --build
```

Дождитесь, что Keycloak и core стартовали:

```bash
docker compose ps
docker compose logs -f keycloak keycloak-config ps_core
```

Затем поднимайте остальные сервисы:

```bash
cd /opt/ps/ps_transfer
docker compose up -d --build

cd /opt/ps/notification-service
docker compose up -d --build

cd /opt/ps/ps_cashback
docker compose up -d --build

cd /opt/ps/bff
docker compose up -d --build
```

Frontend:

```bash
cd /opt/ps/ps_front
docker compose up -d --build
```

Если у frontend используется переменная `VITE_BFF_BASE_URL`, она попадает в bundle на этапе сборки. При смене адреса сервера frontend нужно пересобрать.

## 5. Что должно быть доступно снаружи

При запуске без reverse proxy:

```text
Frontend:   http://your-server-ip-or-domain:5173
BFF:        http://your-server-ip-or-domain:9091
Keycloak:   http://your-server-ip-or-domain:8081
Grafana:    http://your-server-ip-or-domain:3000
Prometheus: http://your-server-ip-or-domain:9090
Kafka UI:   http://your-server-ip-or-domain:8070
```

DB, Kafka, Redis, Loki, Tempo, Alloy и внутренние микросервисы лучше не открывать в интернет. Если порты опубликованы compose-файлами, ограничьте доступ firewall-ом.

## 6. Проверка после запуска

Core:

```bash
cd /opt/ps/ps_core
docker compose ps
docker compose logs --tail=100 ps_core
```

Transfer, cashback, notification-service и BFF:

```bash
cd /opt/ps/ps_transfer && docker compose ps
cd /opt/ps/ps_cashback && docker compose ps
cd /opt/ps/notification-service && docker compose ps
cd /opt/ps/bff && docker compose ps
```

Проверьте в браузере:

```text
Keycloak: http://your-server-ip-or-domain:8081
Grafana:  http://your-server-ip-or-domain:3000
BFF:      http://your-server-ip-or-domain:9091/actuator/health
```

В Grafana datasource и dashboard provisioning подтягиваются из `monitoring/grafana/provisioning`.

## 7. Обновление версии

Перед обновлением посмотрите текущие контейнеры:

```bash
docker compose ps
```

Обновляйте сервисы по одному:

```bash
cd /opt/ps/ps_cashback
git pull
docker compose up -d --build
docker compose logs -f ps-cashback
```

Для остальных репозиториев схема такая же. Если менялись общие контракты, сначала обновляйте инфраструктурно-зависимые сервисы: `ps_core`, затем потребителей/интеграции, затем `bff`, затем frontend.

## 8. Бэкапы перед релизом

Перед миграциями БД делайте дамп нужной базы:

```bash
cd /opt/ps/ps_core
docker compose exec -T postgres pg_dump -U postgres payments_system > payments_system_backup.sql

cd /opt/ps/ps_transfer
docker compose exec -T transfer-postgres pg_dump -U postgres ps_transfer > ps_transfer_backup.sql

cd /opt/ps/ps_cashback
docker compose exec -T cashback-db pg_dump -U cashback ps_cashback > ps_cashback_backup.sql

cd /opt/ps/notification-service
docker compose exec -T notification-db pg_dump -U notifications notifications > notifications_backup.sql
```

Команды дампа можно адаптировать под реальные имена сервисов, баз и пользователей из `.env`.

## 9. Типовые проблемы

Если после логина `ERR_NAME_NOT_RESOLVED` или redirect ведет на неверный адрес, проверьте `PUBLIC_HOST`, `PUBLIC_SCHEME` и контейнер `keycloak-config`.

Если сервис не видит другой сервис, проверьте, что оба контейнера подключены к сети `ps-observability-net`:

```bash
docker network inspect ps-observability-net
```

Если JWT не проходит валидацию, внешний `issuer-uri` должен совпадать с тем, что Keycloak кладет в токен, а `jwk-set-uri` внутри контейнеров должен указывать на `http://keycloak:8080/...`.

Если в логах OpenTelemetry появляется `sending queue is full`, временно уменьшите `TRACING_SAMPLING_PROBABILITY` или увеличьте ресурсы/лимиты Alloy и Tempo.
