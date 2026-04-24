-- ============================================================
-- NoorSchool OCI ATP 배포용 DDL
-- 실행 순서: 이 파일 전체를 DBeaver에서 순서대로 실행
-- ============================================================


-- ============================================================
-- 1. 회원 테이블
-- ============================================================
CREATE TABLE TB_MEMBER (
    MEMBER_ID     NUMBER         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    GOOGLE_SUB    VARCHAR2(255)  NOT NULL UNIQUE,
    EMAIL         VARCHAR2(255)  NOT NULL UNIQUE,
    NAME          VARCHAR2(100)  NOT NULL,
    NICKNAME      VARCHAR2(100),
    PROFILE_IMAGE VARCHAR2(1000),
    ROLE          VARCHAR2(20)   DEFAULT 'USER'  NOT NULL,
    STATUS        VARCHAR2(20)   DEFAULT 'ACTIVE' NOT NULL,
    LAST_LOGIN_AT TIMESTAMP      DEFAULT SYSDATE NOT NULL,
    CREATED_AT    TIMESTAMP      DEFAULT SYSDATE NOT NULL,
    UPDATED_AT    TIMESTAMP      DEFAULT SYSDATE NOT NULL
);


-- ============================================================
-- 2. 리프레시 토큰 테이블
-- ============================================================
CREATE TABLE TB_REFRESH_TOKEN (
    TOKEN_ID      NUMBER         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    MEMBER_ID     NUMBER         NOT NULL,
    REFRESH_TOKEN VARCHAR2(1000) NOT NULL,
    EXPIRES_AT    TIMESTAMP      NOT NULL,
    IS_REVOKED    VARCHAR2(1)    DEFAULT 'N' NOT NULL,
    CREATED_AT    TIMESTAMP      DEFAULT SYSDATE NOT NULL,
    UPDATED_AT    TIMESTAMP      DEFAULT SYSDATE NOT NULL,
    CONSTRAINT FK_REFRESH_TOKEN_MEMBER FOREIGN KEY (MEMBER_ID) REFERENCES TB_MEMBER(MEMBER_ID)
);


-- ============================================================
-- 3. 단어 테이블
-- ============================================================
CREATE TABLE TB_WORD (
    WORD_ID    NUMBER        GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    WORD_KO    VARCHAR2(200) NOT NULL,
    WORD_AR    VARCHAR2(200) NOT NULL,
    POS        VARCHAR2(50),
    DIFFICULTY VARCHAR2(20)  DEFAULT 'EASY' NOT NULL,
    CATEGORY   VARCHAR2(100),
    FREQUENCY  NUMBER        DEFAULT 0,
    IS_PREMIUM VARCHAR2(1)   DEFAULT 'N' NOT NULL,
    IS_ACTIVE  VARCHAR2(1)   DEFAULT 'Y' NOT NULL,
    CREATED_AT TIMESTAMP     DEFAULT SYSDATE NOT NULL
);


-- ============================================================
-- 4. 약관 마스터 테이블
-- ============================================================
CREATE TABLE TB_TERMS (
    TERMS_ID    NUMBER        GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    TERMS_TYPE  VARCHAR2(50)  NOT NULL,       -- SERVICE | PRIVACY | MARKETING
    VERSION     VARCHAR2(20)  NOT NULL,
    TITLE_AR    VARCHAR2(500) NOT NULL,
    CONTENT_AR  CLOB          NOT NULL,
    IS_REQUIRED VARCHAR2(1)   DEFAULT 'Y' NOT NULL,   -- Y: 필수, N: 선택
    IS_ACTIVE   VARCHAR2(1)   DEFAULT 'Y' NOT NULL,   -- Y: 현행, N: 구버전
    CREATED_AT  TIMESTAMP     DEFAULT SYSDATE NOT NULL
);


-- ============================================================
-- 5. 사용자 약관 동의 내역 테이블
-- ============================================================
CREATE TABLE TB_TERMS_AGREEMENT (
    AGREEMENT_ID NUMBER      GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    MEMBER_ID    NUMBER       NOT NULL,
    TERMS_ID     NUMBER       NOT NULL,
    IS_AGREED    VARCHAR2(1)  NOT NULL,   -- Y: 동의, N: 거부(선택항목)
    AGREED_AT    TIMESTAMP    DEFAULT SYSDATE NOT NULL,
    CONSTRAINT FK_AGREEMENT_MEMBER FOREIGN KEY (MEMBER_ID) REFERENCES TB_MEMBER(MEMBER_ID),
    CONSTRAINT FK_AGREEMENT_TERMS  FOREIGN KEY (TERMS_ID)  REFERENCES TB_TERMS(TERMS_ID)
);
