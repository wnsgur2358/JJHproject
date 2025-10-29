USE matchon;

-- sports_type
CREATE TABLE sports_type (
    sports_type_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sports_type_name ENUM ('soccer', 'futsal', 'baseball', 'basketball') NOT NULL
);

-- positions
CREATE TABLE positions (
    position_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sports_type_id BIGINT,
    position_name VARCHAR(50) NOT NULL,
    FOREIGN KEY (sports_type_id) REFERENCES sports_type(sports_type_id)
);

-- member
CREATE TABLE member (
    member_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_email VARCHAR(100) NOT NULL UNIQUE,
    member_password VARCHAR(100) NOT NULL,
    member_name VARCHAR(50),
    member_role ENUM('user', 'admin', 'host') NOT NULL,
    sports_type_id BIGINT,
    position_id BIGINT,
    perferred_time ENUM('weekday_morning', 'weekday_afternoon', 'weekday_evening', 'weekend_morning', 'weekend_afternoon', 'weekend_evening'),
    team_id BIGINT,
    my_temperture DOUBLE DEFAULT 36.5,
    picture_attachment_enabled BOOLEAN DEFAULT TRUE CHECK (picture_attachment_enabled = TRUE),
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (position_id) REFERENCES positions(position_id),
    FOREIGN KEY (sports_type_id) REFERENCES sports_type(sports_type_id),
    FOREIGN KEY (team) REFERENCES team(team_id)
);

-- host_profile
CREATE TABLE host_profile (
    host_profile_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    host_id BIGINT NOT NULL,
    host_name VARCHAR(100) NOT NULL UNIQUE,
    sports_type_id BIGINT,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    picture_attachment_enabled BOOLEAN DEFAULT TRUE CHECK (picture_attachment_enabled = TRUE),
    FOREIGN KEY (host_id) REFERENCES member(member_id),
    FOREIGN KEY (sports_type_id) REFERENCES sports_type(sports_type_id)
);

-- refresh_token
CREATE TABLE refresh_token (
    refresh_token_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    refresh_token_data VARCHAR(512),
    refresh_token_expired_date DATETIME NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(member_id)
);

-- inquiry
CREATE TABLE inquiry (
    inquiry_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    writer_id BIGINT NOT NULL,
    inquiry_category ENUM('tg(team, guest)', 'event', 'tutorial', 'manner_temperture', 'community', 'account', 'report') NOT NULL,
    inquiry_title VARCHAR(100) NOT NULL,
    inquiry_content TEXT NOT NULL,
    inquiry_status ENUM('waiting', 'complete') DEFAULT 'waiting',
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_person VARCHAR(100),
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modified_person VARCHAR(100),
    is_deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (writer_id) REFERENCES member(member_id)
);

-- inquiry_answer
CREATE TABLE inquiry_answer (
    answer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inquiry_id BIGINT NOT NULL,
    admin_id BIGINT NOT NULL,
    answer_content TEXT NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_person VARCHAR(100),
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modified_person VARCHAR(100),
    is_deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (inquiry_id) REFERENCES inquiry(inquiry_id),
    FOREIGN KEY (admin_id) REFERENCES member(member_id)
);

-- faq
CREATE TABLE faq (
    faq_id BIGINT AUTO_INCREMENT PRIMARY KEY,
   	admin_id BIGINT NOT NULL,
   	faq_category ENUM('tg(team, guest)', 'event', 'tutorial', 'manner_temperture', 'community', 'account', 'report') NOT NULL,
   	faq_title VARCHAR(100) NOT NULL,
   	faq_content TEXT NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_person VARCHAR(100),
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modified_person VARCHAR(100),
    is_deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (admin_id) REFERENCES member(member_id)
);

-- event_request
CREATE TABLE event_request (
    event_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    host_id BIGINT NOT NULL,
    event_date DATE NOT NULL,
    event_region ENUM('capital region (seoul·gyeong-gi·incheon)', 'yeongnam region(busan·daegu·ulsan·gyeongnam·gyeongbuk)', 'Honam region (gwangju·jeonnam·jeonbuk)', 'chungcheong region (daejeon·sejong·chungnam·chungbuk)', 'gangwon region', 'jeju') NOT NULL,
    event_title VARCHAR(100) NOT NULL,
    host_profile_id BIGINT NOT NULL,
    sports_type_id BIGINT NOT NULL,
    event_method VARCHAR(100) NOT NULL,
    event_contact VARCHAR(50) NOT NULL,
    event_status ENUM('waiting', 'approved', 'denied') DEFAULT 'waiting',
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_person VARCHAR(100),
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modified_person VARCHAR(100),
    is_deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (host_id) REFERENCES member(member_id),
    FOREIGN KEY (host_profile_id) REFERENCES host_peofile(host_peofile_id),
    FOREIGN KEY (sports_type_id) REFERENCES sports_type(sports_type_id)
);



