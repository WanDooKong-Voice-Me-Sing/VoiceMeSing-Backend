# VoiceMeSing
### **사용자 목소리 기반 AI 커버곡 생성 플랫폼**

---

## 프로젝트 개요 (배경, 동기, 목적)
최근 유튜브에서 특정 인물의 목소리로 다양한 커버곡을 만들어 공유하는 콘텐츠가 인기를 끌고 있습니다. 그러나 대부분의 음성 변환 프로그램은 복잡하고 음질이 떨어져 일반 사용자들이 쉽게 접근하기 어렵습니다. 이에 우리는 간단한 절차로 고품질의 AI 음성 변환 기능을 제공하는 플랫폼을 개발하고, 사용자가 AI 지식 없이도 창작물을 만들고 공유할 수 있는 커뮤니티 기능을 추가하여, 누구나 쉽게 활용할 수 있는 환경을 조성하는 것을 목표로 이 프로젝트를 기획했습니다.

---

## 기술 스택
- **Backend**: Java, Spring Boot
- **Database**: MySQL
- **Authentication**: JWT, OAuth2.0 (카카오, 네이버)
- **Build Tool**: Gradle
- **Other**: Docker, AWS (EC2, RDS), Git, Github, Swagger

---

## 핵심 기능
### 📋 회원 관리
- **회원 가입**:
    - JWT 기반 일반 로그인과 카카오, 네이버 OAuth2.0 소셜 로그인 지원.
    - 구글 로그인도 지원 가능하지만 도메인 등록이 필요.
- **로그인**:
    - 이중 JWT(refresh, access) 발급.
    - Refresh 토큰은 서버 주도권을 유지하기 위해 데이터베이스에 저장.
    - Refresh 토큰 탈취를 방지하기 위해, 사용자가 마지막으로 발급받은 토큰만 검증.
    - Access 토큰 재발급 요청(`/reissue`) 시 최신 Refresh 토큰 검증 수행.
- **로그아웃**:
    - 데이터베이스에 저장된 사용자의 마지막 Refresh 토큰 제거.
    - 만료된 Refresh 토큰을 발급하여 사용자가 기존 토큰을 사용할 수 없도록 처리.

### 🔊 음성 모델
- **음성 모델 생성**:
    - 음성 모델의 이름과 음성 파일을 업로드하여 새로운 음성 모델 생성.
- **음성 모델 리스트 조회**:
    - 생성된 음성 모델 리스트 확인 가능.
- **음성 모델 삭제**:
    - 생성된 음성 모델 삭제.

### 🎤 커버곡
- **커버곡 생성**:
    - 생성할 커버곡의 이름, 사용할 음성 모델, 음원 파일을 업로드하여 커버곡 생성 요청.
- **커버곡 리스트 조회**:
    - 생성한 커버곡 리스트 확인 가능.
- **커버곡 공개 여부**:
    - 생성된 커버곡의 공개/비공개 설정 가능.
- **커버곡 삭제**:
    - 생성된 커버곡 삭제.

### 💬 게시판 기능
- 공개 설정된 커버곡 리스트를 모든 사용자에게 제공.
- 게시글 작성, 삭제 등 기본적인 게시판 기능 지원.

---

## 실행 방법

### 1. 프로젝트 클론
```bash
git clone https://github.com/WanDooKong-Voice-Me-Sing/VoiceMeSing-Backend.git
cd VoiceMeSing-Backend/voice-me-sing
```

### 2. 환경 변수 파일 생성
VoiceMeSing-Backend/voice-me-sin/src/main/resources/ 에 다음 환경 변수 파일 생성:

1. application.yaml
2. application-db.yaml
3. application-jwt.yaml
4. application-oauth2.yaml
5. application-path.yaml

#### application.yaml:
애플리케이션의 기본 설정 파일
```yaml
spring:
  application:
    name: voice-me-sing
  profiles:
    include:
      - db
      - jwt
      - oauth2
      - path
  servlet:
    multipart:
      enabled: true
      max-file-size: 15MB
      max-request-size: 15MB
```

#### application-db.yaml:
데이터베이스 관련 설정 파일
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        show-sql: true
        format_sql: true
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://<database-url>:3306/<database-name>?useSSL=false&useUnicode=true&serverTimezone=Asia/Seoul
    username: <username>
    password: <password>
```
#### application-jwt.yaml:
JWT 토큰 관련 설정 파일
```yaml
spring:
  jwt:
    secret: <secret-code>
```

#### application-oauth2.yaml:
OAuth2 소셜 로그인 설정 파일
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          naver:
            redirect-uri: <redirect-uri>
            authorization-grant-type: authorization_code
            client-id: <client-id>
            client-secret: <client-secret>
            scope: name,email
            client-name: naver
          kakao:
            redirect-uri: <redirect-uri>
            authorization-grant-type: authorization_code
            client-id: <client-id>
            client-secret: <client-secret>
            client-authentication-method: client_secret_post
            scope: profile_nickname,account_email
            client-name: kakao
      provider:
        naver:
          user-name-attribute: response
          authorization-uri: https://nid.naver.com/oauth2.0/authorize
          token-uri: https://nid.naver.com/oauth2.0/token
          user-info-uri: https://openapi.naver.com/v1/nid/me
        kakao:
          user-name-attribute: id
          authorization-uri: https://kauth.kakao.com/oauth/authorize
          token-uri: https://kauth.kakao.com/oauth/token
          user-info-uri: https://kapi.kakao.com/v2/user/me
```
#### application-path.yaml:
AI 서버와 프론트엔드 서버의 URL을 포함하는 설정 파일
```yaml
spring:
  pythonServerUrl: <pythonServerUrl>
  frontEndServerUrl: <frontEndServerUrl>
```
| 괄호 안의 값은 **실제 값**으로 채움

### 3. 의존성 설치 및 JAR 파일 생성
/VoiceMeSing-Backend/voice-me-sing/ 에서 다음을 실행:

```bash
./gradlew build
```

### 4. 애플리케이션 실행
#### 두 가지 방법 중 하나 실행:

임의의 디렉토리에서 다음을 실행:
```bash
java -jar <filePath/fileName>.jar
```
또는,

/VoiceMeSing-Backend/voice-me-sing/ 에서 다음을 실행:
```bash
./gradlew bootRun
```

### 서버 접속
```bash
http://localhost:<port>
```

### Swagger Docs
```bash
http://localhost:<port>/swagger-ui
```

---

## 문의
이메일: rkb109@g.hongik.ac.kr