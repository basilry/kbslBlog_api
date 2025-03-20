#!/bin/bash
# logs.sh - 백엔드 애플리케이션 로그 확인 스크립트

SERVICE_NAME="kbslBlog_api"
echo "서비스 ($SERVICE_NAME) 로그를 실시간으로 확인합니다..."
sudo journalctl -u "${SERVICE_NAME}.service" -f
