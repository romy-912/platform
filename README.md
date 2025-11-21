# PLATFORM 백엔드 프로젝트

## 📋 프로젝트 개요
- **기술스택**: Spring Boot 3.4.8 + Java 21 + MyBatis + MS SQL Server
- **서버포트**: 9999 (기본 포트) 
  - 필요시 변경 `-Dserver.port=8080`
- **Context path**: `/api`

## 🏗️ 아키텍처 구조

### 기술스택
- **프레임워크**: Spring Boot 3.4.8, Spring Security, Spring WebFlux
- **ORM**: MyBatis 3.0.4
- **데이터베이스**: MS SQL Server
- **캐시**: Redis
- **인증**: JWT (jjwt 0.12.6)
- **저장소**: Azure Blob Storage, Azure File Storage
- **문서화**: SpringDoc OpenAPI (Swagger)
- **빌드**: Gradle + Jib (컨테이너화)

### 도메인 모듈 구조
```
main/
├── auth/          # 인증/권한 관리
├── common/        # 공통 기능 (파일, 코드, 폴더 등)
├── community/     # 커뮤니티
└── department/    # 부서 관리
```

각 도메인은 표준 레이어드 아키텍처:
- `controller/` - REST API 엔드포인트
- `service/` - 비즈니스 로직
- `mapper/` - MyBatis 매퍼 인터페이스
- `dto/` - 데이터 전송 객체
- `dvo/` - 도메인 값 객체
- `converter/` - 객체 변환 (MapStruct)

## 🔧 개발 환경
- **Java**: 21 (Eclipse Temurin)
- **컨테이너**: Docker (Jib 플러그인)
- **프로파일**: local, stage, prod 환경 분리
- **모니터링**: Spring Actuator (health 엔드포인트)
  - health - 애플리케이션 헬스 체크 엔드포인트
  - 접근 경로: /api/actuator/health
  - 상세 정보 표시: never (상세 정보 숨김)


## 🚀 실행 방법

### 로컬 개발 환경
```bash
# 프로젝트 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun

# 또는 JAR 파일 실행
java -jar build/libs/platform-0.0.1-SNAPSHOT.jar
```

### Docker 컨테이너
```bash
# Docker 이미지 빌드
./gradlew jib

# 컨테이너 실행 (환경에 따라 프로파일 설정)
docker run -e "SPRING_PROFILES_ACTIVE=stage" [이미지명]
```

## 📚 API 문서
- **Swagger UI**: http://localhost:9999/api/swagger-ui.html
- **기본 URL**: http://localhost:9999/api

## 🗂️ 주요 디렉토리 구조
```
src/
├── main/
│   ├── java/com/romy/platform/
│   │   ├── config/           # 설정 클래스
│   │   ├── common/           # 공통 유틸리티
│   │   └── main/            # 비즈니스 도메인
│   └── resources/
│       ├── mappers/          # MyBatis XML 매퍼
│       ├── application.yml   # 애플리케이션 설정
│       └── messages_*.properties # 국제화 메시지
└── test/                     # 테스트 코드
```

---

- **작성자**: Claude (Anthropic AI Assistant)
- **작성일**: 2025-10-01