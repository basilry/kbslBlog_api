# 🚀 basilry.kim | API 서버

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java" alt="Java 17" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat-square&logo=spring" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/JPA-Hibernate-brown?style=flat-square&logo=hibernate" alt="JPA" />
  <img src="https://img.shields.io/badge/QueryDSL-5.0.0-blue?style=flat-square" alt="QueryDSL" />
  <img src="https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql" alt="MySQL" />
  <img src="https://img.shields.io/badge/Gradle-7.x-06A0CE?style=flat-square&logo=gradle" alt="Gradle" />
  <img src="https://img.shields.io/badge/Google%20Drive-API-4285F4?style=flat-square&logo=google-drive" alt="Google Drive API" />
</p>

## 📝 프로젝트 소개

basilry.kim API 서버는 블로그 서비스를 위한 강력하고 확장 가능한 백엔드 시스템입니다. RESTful API를 통해 게시글, 댓글, 사용자 관리 등 다양한 기능을 제공합니다.

### 주요 특징
- ✅ 사용자 인증 및 권한 관리
- ✅ 게시글 CRUD 및 검색 기능
- ✅ 좋아요, 댓글 시스템
- ✅ 이미지 업로드 및 최적화
- ✅ 안정적인 시간대 처리 (Asia/Seoul)

## 🔧 기술 스택

### 백엔드
- **언어**: Java 17
- **프레임워크**: Spring Boot 3.x
- **데이터베이스 접근**: Spring Data JPA, QueryDSL
- **데이터베이스**: MySQL 8.0
- **빌드 도구**: Gradle 7.x

### 인프라
- **서버**: AWS EC2
- **CI/CD**: GitHub Actions
- **이미지 저장소**: Google Drive API

## 📚 API 문서

### 사용자 관리
| 기능 | 엔드포인트 | 메서드 |
|------|------------|--------|
| 회원가입 | `/users/register` | POST |
| 로그인 | `/authenticate` | POST |
| 토큰 갱신 | `/refresh` | POST |
| 내 정보 조회 | `/users/me` | GET |
| 내 정보 수정 | `/users/me` | PUT |

### 게시글
| 기능 | 엔드포인트 | 메서드 |
|------|------------|--------|
| 게시글 목록 | `/posts?page={page}` | GET |
| 게시글 상세 | `/posts/{id}` | GET |
| 게시글 작성 | `/posts` | POST |
| 게시글 수정 | `/posts/{id}` | PUT |
| 게시글 삭제 | `/posts/{id}` | DELETE |
| 게시글 좋아요 | `/posts/{id}/like` | POST |

### 파일 업로드
| 기능 | 엔드포인트 | 메서드 |
|------|------------|--------|
| 단일 파일 업로드 | `/file/single` | POST |

### 기타 API
| 기능 | 엔드포인트 | 메서드 |
|------|------------|--------|
| 이미지 프록시 | `/image-proxy` | GET |
| 건강 체크 | `/health` | GET |
| 홈 | `/` | GET |
| 경력 관련 | `/careers` | GET |
| 자격증 관련 | `/certifications` | GET |
| 연구 관련 | `/researches` | GET |

## 🏃‍♂️ 시작하기

### 필수 조건
- JDK 17 이상
- MySQL 8.0 이상
- Gradle 7.x

### 설치 방법
```bash
# 저장소 클론
git clone https://github.com/username/kbslBlog_api.git

# 디렉토리 이동
cd kbslBlog_api

# 의존성 설치
./gradlew build

# 서버 실행
./gradlew bootRun
```

## 📅 개발 로드맵

### 완료된 작업
- ✅ 프로젝트 기본 구조 설정
- ✅ 사용자 인증 시스템 구축
- ✅ 게시글 CRUD 기능 구현
- ✅ 파일 업로드 기능 구현
- ✅ 이미지 프록시 기능 구현
- ✅ 시간대 처리 최적화 (Asia/Seoul)

### 진행 중인 작업
- 🔄 API 문서화 (Swagger)
- 🔄 테스트 코드 작성
- 🔄 성능 최적화

### 예정된 작업
- ⏳ 댓글 시스템 구현
- ⏳ 사용자 프로필 관리 개선
- ⏳ 태그 및 카테고리 시스템

## 📝 개발 기록

### 2025년
- **3월 20일**
  - 시간대 설정 최적화 (Asia/Seoul)
  - Java 버전 21 -> 17 버전 변경

- **3월 18일**
  - 이미지 프록시 처리 기능 구현

- **3월 14일 ~ 3월 13일**
  - 파일 업로드 최적화 처리
  - 멀티 파일 업로드 부분 용량체크 구현
  - 외부 URL 이미지 다운로드 후 Google Drive 저장 처리
  - 썸네일 처리 구현

- **3월 12일 ~ 3월 11일**
  - Google Drive 파일 업로드 기능 구현
  - 포스팅 수정 기능 추가
  - 포스팅 개별 조회 로직 개선 및 삭제 기능 구현

- **3월 8일 ~ 3월 3일**
  - 포스팅 작성 기능 구현
  - ApiResult 관련 리턴타입 수정 및 에러코드 추가
  - 포스팅 목록 썸네일 조회 및 좋아요 기능 구현
  - 페이지네이션 구현
  - 포스트/좋아요 엔티티 구성

- **2월 27일 ~ 2월 26일**
  - 유저 프로필 수정 및 불러오기 기능 구현
  - 경력, 수료/자격, 연구/학습 조회 및 추가 기능 구현

- **2월 25일 ~ 2월 20일**
  - JWT 클레임 조정
  - 토큰 저장 확인 및 클레임 부분 조정
  - 회원가입 및 로그인 기능 구현
  - JWT 셋팅 및 유저 로그인 기능 구현

- **2월 14일 ~ 2월 13일**
  - JWT 셋팅 진행
  - 의존성 모듈 조정 및 보안 기반 셋팅
  - 프로젝트 생성 및 초기 구조 설정

## 📜 라이센스

이 프로젝트는 MIT 라이센스를 따릅니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

## 👨‍💻 기여자

- **basilry** - *초기 작업* - [GitHub](https://github.com/basilry)

---

<p align="center">© 2025 kbslBlog. All rights reserved.</p>