#!/bin/bash

PROJECT_DIR="/home/ubuntu/kbslBlog_api"
REPO_URL="https://github.com/basilry/kbslBlog_api.git"

# 1. 프로젝트 클론 (이미 클론되어 있다면 이 부분은 주석 처리)
if [ ! -d "$PROJECT_DIR" ]; then
  echo "프로젝트 디렉토리가 없으므로 클론합니다..."
  cd /home/ubuntu || exit 1
  git clone "$REPO_URL" kbslBlog_api
else
  echo "프로젝트 디렉토리가 존재하므로 git pull 진행 중..."
  cd "$PROJECT_DIR" || exit 1
  git pull origin master
fi

# 2. Gradle 빌드
echo "프로젝트 디렉토리로 이동합니다: $PROJECT_DIR"
cd "$PROJECT_DIR" || exit 1

echo "Gradle 빌드를 시작합니다..."
# Gradle Wrapper가 있는 경우:
chmod +x ./gradlew
./gradlew clean build

# 빌드 후, 생성된 JAR 파일 경로 찾기 (build/libs 폴더 내의 첫번째 .jar 파일)
JAR_FILE=$(find "$PROJECT_DIR/build/libs" -name "*.jar" | head -n 1)
if [ -z "$JAR_FILE" ]; then
  echo "빌드 후 JAR 파일을 찾을 수 없습니다. 빌드 에러를 확인하세요."
  exit 1
fi
echo "빌드 완료: $JAR_FILE"

# 3. systemd 서비스 파일 생성
SERVICE_NAME="kbslBlog_api"
SERVICE_FILE="/etc/systemd/system/${SERVICE_NAME}.service"
echo "서비스 파일을 생성합니다: $SERVICE_FILE"

sudo tee "$SERVICE_FILE" > /dev/null <<EOF
[Unit]
Description=${SERVICE_NAME} Spring Boot Application
After=network.target

[Service]
User=ubuntu
WorkingDirectory=${PROJECT_DIR}
ExecStart=/usr/bin/java -jar ${JAR_FILE}
SuccessExitStatus=143
TimeoutStopSec=10
Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target
EOF

echo "서비스 파일 생성 완료."

# 4. systemd 데몬 재로드 및 서비스 활성화
echo "systemd 데몬을 재로드합니다..."
sudo systemctl daemon-reload

echo "서비스를 부팅 시 자동 시작하도록 활성화합니다..."
sudo systemctl enable "${SERVICE_NAME}.service"

echo "서비스를 시작합니다..."
sudo systemctl start "${SERVICE_NAME}.service"

echo "서비스 상태:"
sudo systemctl status "${SERVICE_NAME}.service" --no-pager

