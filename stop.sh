#!/bin/bash
# stop.sh - 백엔드 애플리케이션 중지 스크립트

SERVICE_NAME="kbslBlog_api"

echo "서비스 '${SERVICE_NAME}' 중지 중..."
sudo systemctl stop "${SERVICE_NAME}.service"

# 중지 후 상태 확인 (원하는 경우)
sudo systemctl status "${SERVICE_NAME}.service" --no-pager
echo "서비스 중지 완료."
