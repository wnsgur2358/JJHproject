DROP DATABASE IF EXISTS matchon;
CREATE DATABASE IF NOT EXISTS matchon;
USE matchon;



-- FK 없는 것
-- ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
CREATE TABLE sports_type
(
    sports_type_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    sports_type_name ENUM ('SOCCER', 'FUTSAL') NOT NULL
);


CREATE TABLE attachment
(
    attachment_id   BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    board_type      ENUM('MATCHUP_BOARD', 'GUEST_BOARD', 'GUEST_REQUEST', 'CHAT_MESSAGE', 'MEMBER', 'HOST_PROFILE', 'TEAM', 'STADIUM','BOARD') NOT NULL,
    board_number    BIGINT             NOT NULL,
    file_order      INT                NOT NULL,
    original_name   VARCHAR(255)       NOT NULL,
    saved_name      VARCHAR(255)       NOT NULL,
    save_path       VARCHAR(255)       NOT NULL,
    thumbnail_path  VARCHAR(255) NULL,
    created_date    DATETIME                    DEFAULT CURRENT_TIMESTAMP,
    created_person  VARCHAR(100)        NOT NULL,
    modified_date   DATETIME                    DEFAULT current_timestamp on update current_timestamp,
    modified_person VARCHAR(100)        NOT NULL,
    is_deleted      BOOLEAN            NOT NULL DEFAULT FALSE
);


CREATE TABLE chat_room
(
    chat_room_id    BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    is_group_chat   BOOLEAN                     DEFAULT TRUE NULL,
    chat_room_name  VARCHAR(255)       NOT NULL,
    created_date    DATETIME                    DEFAULT CURRENT_TIMESTAMP,
    created_person  VARCHAR(100)        NOT NULL,
    modified_date   DATETIME                    DEFAULT current_timestamp on update current_timestamp,
    modified_person VARCHAR(100)        NOT NULL,
    is_deleted      BOOLEAN            NOT NULL DEFAULT FALSE

);

create table stadium
(
    stadium_id                      bigint primary KEY AUTO_INCREMENT,
    stadium_region                  varchar(100),
    stadium_name                    varchar(100),
    stadium_address                 varchar(255),
    stadium_image_attchment_enabled boolean  default false,
    create_date                     DATETIME DEFAULT current_timestamp,
    create_person                   varchar(100),
    modified_date                   DATETIME DEFAULT current_timestamp on update current_timestamp,
    modified_person                 varchar(100),
    is_deleted                      boolean  default false
);

-- positions
CREATE TABLE positions
(
    position_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    position_name ENUM('GOALKEEPER', 'CENTER_BACK', 'LEFT_RIGHT_BACK', 'LEFT_RIGHT_WING_BACK', 'CENTRAL_DEFENSIVE_MIDFIELDER', 'CENTRAL_MIDFIELDER', 'CENTRAL_ATTACKING_MIDFIELDER', 'LEFT_RIGHT_WING', 'STRIKER_CENTER_FORWARD', 'SECOND_STRIKER', 'LEFT_RIGHT_WINGER') NOT NULL
);

CREATE TABLE team
(
    team_id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_name               VARCHAR(200) NOT NULL UNIQUE,
    team_region             ENUM('CAPITAL_REGION', 'YEONGNAM_REGION', 'HONAM_REGION', 'CHUNGCHEONG_REGION', 'GANGWON_REGION', 'JEJU') NOT NULL,
    team_rating_average DOUBLE,
    recruitment_status      BOOLEAN  DEFAULT FALSE,
    created_person          VARCHAR(100),
    created_date            DATETIME DEFAULT current_timestamp,
    modified_person         VARCHAR(100),
    modified_date           DATETIME DEFAULT current_timestamp on update current_timestamp,
    team_introduction       TEXT         NOT NULL,
    team_attachment_enabled BOOLEAN  DEFAULT TRUE CHECK (team_attachment_enabled = true),
    is_deleted              BOOLEAN  DEFAULT FALSE,
    CONSTRAINT UK_team_name UNIQUE KEY (team_name)
);


-- ↑FK 없는 것
-- ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑


-- member, 우선 순위: member
CREATE TABLE member
(
    member_id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_email               VARCHAR(100) NOT NULL UNIQUE,
    member_password            VARCHAR(100) NOT NULL,
    member_name                VARCHAR(50) NOT NULL,
    member_role                ENUM('USER', 'ADMIN', 'HOST') NOT NULL,
    position_id                BIGINT,
    preferred_time             ENUM('WEEKDAY_MORNING', 'WEEKDAY_AFTERNOON', 'WEEKDAY_EVENING', 'WEEKEND_MORNING', 'WEEKEND_AFTERNOON', 'WEEKEND_EVENING'),
    team_id                    BIGINT,
    my_temperature DOUBLE,
    picture_attachment_enabled BOOLEAN,
    created_date               DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_date              DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted                 BOOLEAN  DEFAULT FALSE,
    CONSTRAINT FK_member_2_positions FOREIGN KEY (position_id) REFERENCES positions (position_id), -- MEMBER:positions = N:1
    CONSTRAINT FK_member_2_team FOREIGN KEY (team_id) REFERENCES team (team_id)                    -- MEMBER:team = N:1
);


-- FK 1개
-- ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

-- refresh_token
CREATE TABLE refresh_token
(
    refresh_token_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id                  BIGINT   NOT NULL,
    refresh_token_data         VARCHAR(512),
    refresh_token_expired_date DATETIME NOT NULL,
    created_date               DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_refresh_token_2_member FOREIGN KEY (member_id) REFERENCES member (member_id),
    CONSTRAINT UK_refresh_token_2_member UNIQUE (member_id)
);

-- inquiry
CREATE TABLE inquiry
(
    inquiry_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    writer_id        BIGINT       NOT NULL,
    inquiry_category ENUM('TEAM_GUEST', 'EVENT', 'TUTORIAL', 'MANNER_TEMPERATURE', 'COMMUNITY', 'ACCOUNT', 'REPORT') NOT NULL,
    inquiry_title    VARCHAR(100) NOT NULL,
    inquiry_content  TEXT         NOT NULL,
    inquiry_status   ENUM('PENDING', 'COMPLETED') DEFAULT 'PENDING',
    created_date     DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_person   VARCHAR(100),
    modified_date    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modified_person  VARCHAR(100),
    is_deleted       BOOLEAN  DEFAULT FALSE,
    CONSTRAINT FK_inquiry_2_member FOREIGN KEY (writer_id) REFERENCES member (member_id)
);

-- faq
CREATE TABLE faq
(
    faq_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    admin_id        BIGINT       NOT NULL,
    faq_category    ENUM('TEAM_GUEST', 'EVENT', 'TUTORIAL', 'MANNER_TEMPERATURE', 'COMMUNITY', 'ACCOUNT', 'REPORT') NOT NULL,
    faq_title       VARCHAR(100) NOT NULL,
    faq_content     TEXT         NOT NULL,
    created_date    DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_person  VARCHAR(100),
    modified_date   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modified_person VARCHAR(100),
    is_deleted      BOOLEAN  DEFAULT FALSE,
    CONSTRAINT FK_faq_2_member FOREIGN KEY (admin_id) REFERENCES member (member_id)
);

CREATE TABLE review (
                        team_review_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        review_writer BIGINT NOT NULL,
                        team_id BIGINT NOT NULL,
                        review_rating INT NOT NULL,
                        content TEXT NOT NULL,
                        created_person VARCHAR(100),
                        created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                        modified_person VARCHAR(100),
                        modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        is_deleted BOOLEAN DEFAULT FALSE,
                        CONSTRAINT FK_review_writer FOREIGN KEY (review_writer) REFERENCES member(member_id),
                        CONSTRAINT FK_review_team FOREIGN KEY (team_id) REFERENCES team(team_id)

);

CREATE TABLE response
(
    resonse_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    review_id       BIGINT,
    review_response TEXT,
    created_person  VARCHAR(100),
    created_date    DATETIME DEFAULT current_timestamp,
    modified_person VARCHAR(100),
    modified_date   DATETIME DEFAULT current_timestamp on update current_timestamp,
    is_deleted      BOOLEAN  DEFAULT FALSE,
    CONSTRAINT FK_response_2_review FOREIGN KEY (review_id) REFERENCES review (team_review_id)
);

CREATE TABLE team_join_request
(
    join_request_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    applicant_id        BIGINT,
    join_request_status ENUM('PENDING', 'APPROVED', 'DENIED'),
    created_person      VARCHAR(100),
    created_date        DATETIME DEFAULT current_timestamp,
    modified_person     VARCHAR(100),
    modified_date       DATETIME DEFAULT current_timestamp on update current_timestamp,
    is_deleted          BOOLEAN  DEFAULT FALSE,
    CONSTRAINT FK_team_join_request_2_member FOREIGN KEY (applicant_id) REFERENCES member (member_id)
);

CREATE TABLE board
(
    board_id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    title                    VARCHAR(100) NOT NULL,
    content                  TEXT         NOT NULL,
    board_attachment_enabled BOOLEAN  DEFAULT FALSE,
    created_person           VARCHAR(100),
    created_date             DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_person          VARCHAR(100),
    modified_date            DATETIME DEFAULT current_timestamp on update current_timestamp,
    category                 ENUM('ANNOUNCEMENT', 'FREEBOARD', 'INFORMATION') DEFAULT 'FREEBOARD',
    is_deleted               BOOLEAN  DEFAULT FALSE,
    writer                   BIGINT       NOT NULL,
    CONSTRAINT FK_board_2_member FOREIGN KEY (writer) REFERENCES member (member_id)
);


CREATE TABLE comment
(
    comment_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    board_id        BIGINT NOT NULL,
    member_id		BIGINT NOT NULL,
    content         TEXT   NOT NULL,
    created_person  VARCHAR(100),
    created_date    DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_person VARCHAR(100),
    modified_date   DATETIME DEFAULT current_timestamp on update current_timestamp,
    is_deleted      BOOLEAN  DEFAULT FALSE,
    CONSTRAINT FK_comment_2_board FOREIGN KEY (board_id) REFERENCES board (board_id),
    CONSTRAINT FK_comment_2_member FOREIGN KEY (member_id) REFERENCES member (member_id)
);

-- host_profile
CREATE TABLE host_profile (
    host_profile_id 					BIGINT AUTO_INCREMENT PRIMARY KEY,
    host_id 							BIGINT NOT NULL,
    host_name 							VARCHAR(100) UNIQUE DEFAULT NULL,
    created_date DATETIME 				DEFAULT CURRENT_TIMESTAMP,
    modified_date DATETIME 				DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    picture_attachment_enabled 			BOOLEAN DEFAULT TRUE CHECK (picture_attachment_enabled = TRUE),
    CONSTRAINT FK_host_profile_2_member FOREIGN KEY (host_id) REFERENCES member (member_id)
);


-- ↑FK 1개인 것
-- ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑


-- FK 2개
-- ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

-- inquiry_answer
CREATE TABLE inquiry_answer 
(
    answer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inquiry_id BIGINT UNIQUE NOT NULL, -- UNIQUE 추가
    admin_id BIGINT NOT NULL,
    answer_content TEXT NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_person VARCHAR(100),
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modified_person VARCHAR(100),
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT FK_inquiry_answer_2_inquiry FOREIGN KEY (inquiry_id) REFERENCES inquiry(inquiry_id),
    CONSTRAINT FK_inquiry_answer_2_member FOREIGN KEY (admin_id) REFERENCES member(member_id)
);


CREATE TABLE matchup_board
(
    matchup_board_id               BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    writer_id                      BIGINT             NOT NULL,
    sports_type_id                 BIGINT             NOT NULL,
    reservation_attachment_enabled BOOLEAN            NOT NULL DEFAULT TRUE CHECK (reservation_attachment_enabled = true),
    team_intro                     TEXT               NOT NULL,
    sports_facility_name           VARCHAR(100)       NOT NULL,
    sports_facility_address        VARCHAR(100)       NOT NULL,
    match_datetime                 DATETIME           NOT NULL,
    match_duration                 TIME               NOT NULL,
    current_participant_count      INT                NOT NULL,
    max_participants               INT                NOT NULL,
    min_manner_temperature DOUBLE NOT NULL,
    match_description              TEXT               NOT NULL,
    created_date                   DATETIME                    DEFAULT CURRENT_TIMESTAMP,
    created_person                 VARCHAR(100)        NOT NULL,
    modified_date                  DATETIME                    DEFAULT current_timestamp on update current_timestamp,
    modified_person                VARCHAR(100)        NOT NULL,
    is_deleted                     BOOLEAN            NOT NULL DEFAULT FALSE,
    CONSTRAINT FK_matchup_board_2_member FOREIGN KEY (writer_id) REFERENCES member (member_id),
    CONSTRAINT FK_matchup_board_2_sports_type FOREIGN KEY (sports_type_id) REFERENCES sports_type (sports_type_id)

);


CREATE TABLE matchup_request
(
    matchup_request_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    matchup_board_id   BIGINT             NOT NULL,
    applicant_id       BIGINT             NOT NULL,
    self_intro         TEXT               NOT NULL,
    participant_count  INT                NOT NULL,
    status             ENUM('PENDING', 'APPROVED', 'DENIED', 'CANCELREQUESTED') NOT NULL DEFAULT 'PENDING',
    request_submitted_count 		INT DEFAULT 1 NULL,
    cancel_submitted_count INT DEFAULT 0 NULL,
    created_date       DATETIME                    DEFAULT CURRENT_TIMESTAMP,
    created_person     VARCHAR(100)        NOT NULL,
    modified_date      DATETIME                    DEFAULT current_timestamp on update current_timestamp,
    modified_person    VARCHAR(100)        NOT NULL,
    is_deleted         BOOLEAN            NOT NULL DEFAULT FALSE,
    CONSTRAINT FK_matchup_request_2_matchup_board FOREIGN KEY (matchup_board_id) REFERENCES matchup_board (matchup_board_id),
    CONSTRAINT FK_matchup_request_2_member FOREIGN KEY (applicant_id) REFERENCES member (member_id)
);


CREATE TABLE chat_message
(
    chat_message_id        BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    chat_room_id           BIGINT             NOT NULL,
    sender_id              BIGINT             NOT NULL,
    all_attachment_enabled BOOLEAN NULL DEFAULT FALSE,
    content                TEXT NULL,
    created_date           DATETIME                    DEFAULT CURRENT_TIMESTAMP,
    created_person         VARCHAR(100)        NOT NULL,
    modified_date          DATETIME                    DEFAULT current_timestamp on update current_timestamp,
    modified_person        VARCHAR(100)        NOT NULL,
    is_deleted             BOOLEAN            NOT NULL DEFAULT FALSE,
    CONSTRAINT FK_chat_message_2_chat_room FOREIGN KEY (chat_room_id) REFERENCES chat_room (chat_room_id),
    CONSTRAINT FK_chat_message_2_member FOREIGN KEY (sender_id) REFERENCES member (member_id)

);

CREATE TABLE chat_participant
(
    chat_participant_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    chat_room_id        BIGINT             NOT NULL,
    member_id           BIGINT             NOT NULL,
    created_date        DATETIME                    DEFAULT CURRENT_TIMESTAMP,
    created_person      VARCHAR(100)        NOT NULL,
    modified_date       DATETIME                    DEFAULT current_timestamp on update current_timestamp,
    modified_person     VARCHAR(100)        NOT NULL,
    is_deleted          BOOLEAN            NOT NULL DEFAULT FALSE,
    CONSTRAINT FK_chat_participant_2_chat_room FOREIGN KEY (chat_room_id) REFERENCES chat_room (chat_room_id),
    CONSTRAINT FK_chat_participant_2_member FOREIGN KEY (member_id) REFERENCES member (member_id)
);



CREATE TABLE team_member
(
    team_member_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id          BIGINT NOT NULL,
    team_id            BIGINT NOT NULL,
    introduction       TEXT,
    team_leader_status BOOLEAN DEFAULT FALSE,
    CONSTRAINT FK_team_member_2_member FOREIGN KEY (member_id) REFERENCES member (member_id),
    CONSTRAINT FK_team_member_2_team FOREIGN KEY (team_id) REFERENCES team (team_id)
);

CREATE TABLE recruiting_position
(
    recruiting_postion_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_id               BIGINT,
    position_id           BIGINT,
    UNIQUE (team_id, position_id),
    CONSTRAINT FK_recruiting_position_2_team FOREIGN KEY (team_id) REFERENCES team (team_id),
    CONSTRAINT FK_recruiting_position_2_positions FOREIGN KEY (position_id) REFERENCES positions (position_id)
);

-- event_request
CREATE TABLE event_request
(
    event_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    host_id         BIGINT       NOT NULL,
    event_date      DATE         NOT NULL,
    event_region    VARCHAR(30)  NOT NULL,
    event_title     VARCHAR(100) NOT NULL,
    host_profile_id BIGINT       NOT NULL,
    event_address   VARCHAR(100) NOT NULL,
    event_method    VARCHAR(100) NOT NULL,
    event_contact   VARCHAR(50)  NOT NULL,
    event_status    ENUM('PENDING', 'APPROVED', 'DENIED') DEFAULT 'PENDING',
    created_date    DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_person  VARCHAR(100),
    modified_date   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modified_person VARCHAR(100),
    is_deleted      BOOLEAN  DEFAULT FALSE,
    CONSTRAINT FK_event_request_2_member FOREIGN KEY (host_id) REFERENCES member (member_id),
    CONSTRAINT FK_event_request_2_host_profile FOREIGN KEY (host_profile_id) REFERENCES host_profile (host_profile_id)
);


-- ↑FK 2개인 것
-- ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑


-- FK 3개
-- ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓


CREATE TABLE matchup_rating
(
    matchup_rating_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    request_id        BIGINT             NOT NULL,
    evaluator_id      BIGINT             NOT NULL,
    target_member_id  BIGINT             NOT NULL,
    manner_score      INT                NOT NULL CHECK (1 <= manner_score AND manner_score <= 5),
    skill_score       INT                NOT NULL CHECK (1 <= skill_score AND skill_score <= 5),
    review            TEXT               NOT NULL,
    created_date      DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_person    VARCHAR(100)        NOT NULL,
    modified_date     DATETIME DEFAULT current_timestamp on update current_timestamp,
    modified_person   VARCHAR(100)        NOT NULL,
    CONSTRAINT FK_matchup_rating_2_matchup_request FOREIGN KEY (request_id) REFERENCES matchup_request (matchup_request_id),
    CONSTRAINT FK_matchup_rating_2_member_eval FOREIGN KEY (evaluator_id) REFERENCES member (member_id),
    CONSTRAINT FK_matchup_rating_2_member_target FOREIGN KEY (target_member_id) REFERENCES member (member_id)

);

CREATE TABLE guest_board
(
    guest_board_id             BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    writer_id                  BIGINT             NOT NULL, -- FK 설정 해야됨
    sports_type_id             BIGINT             NOT NULL, -- FK 설정 해야됨
    preferred_position_id      BIGINT             NOT NULL, -- FK 설정 해야됨
    picture_attachment_enabled BOOLEAN            NOT NULL DEFAULT TRUE CHECK (picture_attachment_enabled = true),
    self_intro                 TEXT               NOT NULL,
    preferred_region1          VARCHAR(50)        NOT NULL,
    preferred_region2          VARCHAR(50) NULL,
    preferred_region3          VARCHAR(50) NULL,
    preferred_time1            ENUM('WEEKDAY_MORNING', 'WEEKDAY_AFTERNOON', 'WEEKDAY_EVENING', 'WEEKEND_MORNING', 'WEEKEND_AFTERNOON', 'WEEKEND_EVENING') NOT NULL DEFAULT 'WEEKEND_MORNING',
    preferred_time2            ENUM('WEEKDAY_MORNING', 'WEEKDAY_AFTERNOON', 'WEEKDAY_EVENING', 'WEEKEND_MORNING', 'WEEKEND_AFTERNOON', 'WEEKEND_EVENING') NULL,
    preferred_time3            ENUM('WEEKDAY_MORNING', 'WEEKDAY_AFTERNOON', 'WEEKDAY_EVENING', 'WEEKEND_MORNING', 'WEEKEND_AFTERNOON', 'WEEKEND_EVENING') NULL,
    preferred_time4            ENUM('WEEKDAY_MORNING', 'WEEKDAY_AFTERNOON', 'WEEKDAY_EVENING', 'WEEKEND_MORNING', 'WEEKEND_AFTERNOON', 'WEEKEND_EVENING') NULL,
    preferred_time5            ENUM('WEEKDAY_MORNING', 'WEEKDAY_AFTERNOON', 'WEEKDAY_EVENING', 'WEEKEND_MORNING', 'WEEKEND_AFTERNOON', 'WEEKEND_EVENING') NULL,
    preferred_time6            ENUM('WEEKDAY_MORNING', 'WEEKDAY_AFTERNOON', 'WEEKDAY_EVENING', 'WEEKEND_MORNING', 'WEEKEND_AFTERNOON', 'WEEKEND_EVENING') NULL,
    created_date               DATETIME                    DEFAULT CURRENT_TIMESTAMP,
    created_person             VARCHAR(100)        NOT NULL,
    modified_date              DATETIME                    DEFAULT current_timestamp on update current_timestamp,
    modified_person            VARCHAR(100)        NOT NULL,
    is_deleted                 BOOLEAN            NOT NULL DEFAULT FALSE,
    CONSTRAINT FK_guest_board_2_member FOREIGN KEY (writer_id) REFERENCES member (member_id),
    CONSTRAINT FK_guest_board_2_sports_type FOREIGN KEY (sports_type_id) REFERENCES sports_type (sports_type_id),
    CONSTRAINT FK_guest_board_2_positions FOREIGN KEY (preferred_position_id) REFERENCES positions (position_id)


);

CREATE TABLE guest_request
(
    guest_request_id               BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    guest_board_id                 BIGINT             NOT NULL,
    applicant_id                   BIGINT             NOT NULL,
    reservation_attachment_enabled BOOLEAN            NOT NULL DEFAULT TRUE CHECK (reservation_attachment_enabled = true),
    self_intro                     TEXT               NOT NULL,
    sports_facility_name           VARCHAR(100)       NOT NULL,
    sports_facility_address        VARCHAR(100)       NOT NULL,
    match_date                     DATETIME           NOT NULL,
    match_duration                 TIME               NOT NULL,
    match_description              TEXT               NOT NULL,
    status                         ENUM('PENDING', 'APPROVED', 'DENIED', 'CANCELREQUESTED') NOT NULL DEFAULT 'PENDING',
    request_submitted_count 		INT DEFAULT 1 NULL,
    cancel_submitted_count INT DEFAULT 0 NULL,
    created_date                   DATETIME                    DEFAULT CURRENT_TIMESTAMP,
    created_person                 VARCHAR(100)        NOT NULL,
    modified_date                  DATETIME                    DEFAULT current_timestamp on update current_timestamp,
    modified_person                VARCHAR(100)        NOT NULL,
    is_deleted                     BOOLEAN            NOT NULL DEFAULT FALSE,
    CONSTRAINT FK_guest_request_2_guest_board FOREIGN KEY (guest_board_id) REFERENCES guest_board (guest_board_id),
    CONSTRAINT FK_guest_request_2_member FOREIGN KEY (applicant_id) REFERENCES member (member_id)
);



CREATE TABLE guest_rating
(
    guest_rating_id  BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    request_id       BIGINT             NOT NULL,
    evaluator_id     BIGINT             NOT NULL,
    target_member_id BIGINT             NOT NULL,
    manner_score     INT                NOT NULL CHECK (1 <= manner_score AND manner_score <= 5),
    skill_score      INT                NOT NULL CHECK (1 <= skill_score AND skill_score <= 5),
    review           TEXT               NOT NULL,
    created_date     DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_person   VARCHAR(100)        NOT NULL,
    modified_date    DATETIME DEFAULT current_timestamp on update current_timestamp,
    modified_person  VARCHAR(100)        NOT NULL,
    CONSTRAINT FK_guest_rating_2_guest_request FOREIGN KEY (request_id) REFERENCES guest_request (guest_request_id),
    CONSTRAINT FK_guest_rating_2_member_eval FOREIGN KEY (evaluator_id) REFERENCES member (member_id),
    CONSTRAINT FK_guest_rating_2_member_target FOREIGN KEY (target_member_id) REFERENCES member (member_id)

);

CREATE TABLE message_read_log
(
    message_read_log_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    chat_room_id        BIGINT             NOT NULL,
    receiver_id         BIGINT             NOT NULL,
    chat_message_id     BIGINT             NOT NULL,
    is_read             BOOLEAN  DEFAULT FALSE,
    created_date        DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_person      VARCHAR(100)        NOT NULL,
    modified_date       DATETIME DEFAULT current_timestamp on update current_timestamp,
    modified_person     VARCHAR(100)        NOT NULL,
    CONSTRAINT FK_message_read_log_2_chat_room FOREIGN KEY (chat_room_id) REFERENCES chat_room (chat_room_id),
    CONSTRAINT FK_message_read_log_2_member FOREIGN KEY (receiver_id) REFERENCES member (member_id),
    CONSTRAINT FK_message_read_log_2_chat_message FOREIGN KEY (chat_message_id) REFERENCES chat_message (chat_message_id)

);


-- ↑FK 3개인 것
-- ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
