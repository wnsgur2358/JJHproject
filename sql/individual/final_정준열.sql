CREATE TABLE team(
team_id BIGINT AUTO_INCREMENT PRIMARY KEY,
team_name VARCHAR(200) NOT NULL UNIQUE,
team_region ENUM('capital region (seoul·gyeong-gi·incheon)', 'yeongnam region(busan·daegu·ulsan·gyeongnam·gyeongbuk)', 'Honam region (gwangju·jeonnam·jeonbuk)', 'chungcheong region (daejeon·sejong·chungnam·chungbuk)', 'gangwon region', 'jeju') NOT NULL,
sports_type VARCHAR(50) NOT NULL,
team_rating_average DOUBLE,
recruitment_status BOOLEAN DEFAULT FALSE,
team_member_id BIGINT,
created_person VARCHAR(50),
created_date DATETIME DEFAULT NOW(),
modified_person VARCHAR(50),
modified_date DATETIME,
team_introduction TEXT NOT NULL
team_attachment_enabled BOOLEAN DEFAULT TRUE CHECK(picture_attachment_enabled=true),
is_deleted BOOLEAN DEFAULT FALSE,
FOREIGN KEY (sports_type) REFERENCES sports_type(sports_type_id),
FOREIGN KEY (team_member_id) REFERENCES team(member_id)
);

CREATE TABLE review(
team_review_id BIGINT AUTO_INCREMENT PRIMARY KEY,
review_writer BIGINT,
review_rating INT NOT NULL,
content TEXT NOT NULL,
created_person VARCHAR(50),
created_date DATETIME DEFAULT NOW(),
modified_person VARCHAR(50),
modified_date DATETIME,
is_deleted BOOLEAN DEFAULT FALSE
FOREIGN KEY (review_writer) REFERENCES member(member_id)
);

CREATE TABLE response(
resonse_id BIGINT AUTO_INCREMENT PRIMARY KEY,
review_id BIGINT,
review_response TEXT,
created_person VARCHAR(50),
created_date DATETIME DEFAULT NOW(),
modified_person VARCHAR(50),
modified_date DATETIME,
is_deleted BOOLEAN DEFAULT FALSE,
FOREIGN KEY (review_id) REFERENCES review(team_review_id)
);

CREATE TABLE team_member(
team_member_id BIGINT AUTO_INCREMENT PRIMARY KEY,
member_id BIGINT NOT NULL,
team_id BIGINT NOT NULL,
introduction TEXT,
team_leader_status BOOLEAN DEFAULT FALSE
FOREIGN KEY (member_id) REFERENCES member(member_id),
FOREIGN KEY (team_id) REFERENCES team(team_id)
);

CREATE TABLE recruiting_position(
recruiting_postion_id BIGINT AUTO_INCREMENT PRIMARY KEY,
team_id BIGINT
position_id BIGINT
UNIQUE(team_id, position_id),
FOREIGN KEY (team_id) REFERENCES team(team_id),
FOREIGN KEY (position_id) REFERENCES positions(position_id)
);

CREATE TABLE team_join_request(
join_request_id BIGINT AUTO_INCREMENT PRIMARY KEY,
applicant_id BIGINT,
join_request_status ENUM('pending', 'approved', 'denied'),
created_person VARCHAR(50),
created_date DATETIME DEFAULT NOW(),
modified_person VARCHAR(50),
modified_date DATETIME,
is_deleted BOOLEAN DEFAULT FALSE,
FOREIGN KEY (applicant_id) REFERENCES member(member_id)
)