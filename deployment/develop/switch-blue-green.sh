#!/bin/bash

set -euo pipefail

# ========환경 설정========
ENV="dev"
BASE_DIR="$HOME/deployment/develop"
COMPOSE_DIR="$BASE_DIR/docker-compose"
NGINX_CONF_DIR="$BASE_DIR/nginx"
MAX_RETRIES=20
HEALTHCHECK_WAIT=3

# ========로그 출력 함수========
log() {
  local LEVEL=$1
  local STEP=$2
  local MESSAGE=$3
  echo -e "[$(date +"%T")] [$LEVEL] [$STEP] $MESSAGE"
}

# ========헬스체크 수행 함수========
health_check() {
  local NAME=$1
  local RETRIES=0

  log "INFO" "HealthCheck" "Waiting 10 seconds before checking $NAME..."
  sleep 10

  local COMPOSE_FILE="$COMPOSE_DIR/docker-compose.${NAME}.yml"
  local CONTAINER_ID
  CONTAINER_ID=$(docker compose -f "$COMPOSE_FILE" ps -q "$NAME")

  if [[ -z "$CONTAINER_ID" ]]; then
    log "ERROR" "HealthCheck" "No container found for service: $NAME"
    return 1
  fi

  while [[ $RETRIES -lt $MAX_RETRIES ]]; do
    log "INFO" "HealthCheck" "Checking $NAME (Attempt $((RETRIES + 1))/$MAX_RETRIES)..."

    local STATUS
    STATUS=$(docker exec "$CONTAINER_ID" curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:8080/api/health || true)

    if [[ "$STATUS" == "200" ]]; then
      log "INFO" "HealthCheck" "$NAME is healthy."
      return 0
    fi

    log "WARN" "HealthCheck" "Attempt $((RETRIES + 1)) failed (status: $STATUS)"
    ((RETRIES++))
    sleep "$HEALTHCHECK_WAIT"
  done

  log "ERROR" "HealthCheck" "$NAME failed health check after $MAX_RETRIES attempts."
  return 1
}

# ========컨테이너 시작 함수========
start_service() {
  local NAME=$1
  local COMPOSE_FILE="$COMPOSE_DIR/docker-compose.${NAME}.yml"
  log "INFO" "Service" "Starting $NAME container..."
  docker compose -f "$COMPOSE_FILE" up -d
}

# ========컨테이너 중지 함수========
stop_service() {
  local NAME=$1
  local COMPOSE_FILE="$COMPOSE_DIR/docker-compose.${NAME}.yml"
  log "INFO" "Service" "Stopping $NAME container..."
  docker compose -f "$COMPOSE_FILE" stop
}

# ========Nginx 설정 스위치 및 리로드========
reload_nginx() {
  local TARGET=$1
  local NGINX_FILE="$NGINX_CONF_DIR/nginx-${TARGET}.conf"
  log "INFO" "Nginx" "Switching to $TARGET configuration..."
  sudo cp "$NGINX_FILE" /etc/nginx/conf.d/nginx.conf
  sudo nginx -s reload
  log "INFO" "Nginx" "Reloaded nginx configuration."
}

# ========메인 로직: Blue ↔ Green 스위칭========
main() {
  local CURRENT="green"
  local NEXT="blue"

  if [[ -z $(docker ps --filter "name=green" --filter "status=running" -q) ]]; then
    CURRENT="blue"
    NEXT="green"
  fi

  log "INFO" "Deploy" "Switching from $CURRENT to $NEXT..."

  start_service "$NEXT"

  if ! health_check "$NEXT"; then
    log "ERROR" "Deploy" "$NEXT failed health check. Deployment cancelled."
    stop_service "$NEXT"
    exit 1
  fi

  reload_nginx "$NEXT"
  stop_service "$CURRENT"

  log "INFO" "Deploy" "$NEXT is now live."
}

# ========스크립트 실행 시작========
main "$@"
