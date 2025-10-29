DROP DATABASE IF EXISTS matchon;

CREATE DATABASE IF NOT EXISTS matchon;
USE matchon;


DROP TABLE IF EXISTS matchup_board;
CREATE TABLE matchup_board(
	matchup_board_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
	writer_id BIGINT NOT NULL,  -- FK 설정 해야됨
	sports_type_id BIGINT NOT NULL, -- FK 설정 해야됨
	reservation_attachment_enabled BOOLEAN NOT NULL DEFAULT TRUE CHECK (reservation_attachment_enabled=true),
	team_intro TEXT NOT NULL,
	sports_facility_name VARCHAR(100) NOT NULL,
	sports_facility_address VARCHAR(100) NOT NULL,
	match_datetime DATETIME NOT NULL,
	match_duration DATETIME NOT NULL,
	current_participant_count INT NOT NULL,
	max_participants INT NOT NULL,
	min_manner_temperature DOUBLE NOT NULL, 
	match_description TEXT NOT NULL,
	created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
	created_person VARCHAR(50) NOT NULL,
	modified_date DATETIME DEFAULT current_timestamp on update current_timestamp,
	modified_person VARCHAR(50) NOT NULL,
	is_deleted BOOLEAN NOT NULL DEFAULT FALSE
	
);

DROP TABLE IF EXISTS matchup_request;
CREATE TABLE matchup_request(
	matchup_request_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
	matchup_board_id BIGINT NOT NULL,
	applicant_id BIGINT NOT NULL, -- FK 설정 해야됨,
	self_intro TEXT NOT NULL,	
	participant_count INT NOT NULL,	
	status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
	created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
	created_person VARCHAR(50) NOT NULL,
	modified_date DATETIME DEFAULT current_timestamp on update current_timestamp,
	modified_person VARCHAR(50) NOT NULL,
	is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
	CONSTRAINT FK_matchup_board_id FOREIGN KEY (matchup_board_id) REFERENCES matchup_board(matchup_board_id)
);


DROP TABLE IF EXISTS matchup_rating;
CREATE TABLE matchup_rating(
	matchup_rating_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,	
	request_id BIGINT NOT NULL,
	evaluator_id BIGINT NOT NULL, -- FK 설정 해야됨
	target_member_id BIGINT NOT NULL, -- FK 설정 해야됨
	manner_score INT NOT NULL CHECK (1<=manner_score AND manner_score<=5),
	skill_score INT NOT NULL CHECK (1<=skill_score AND skill_score<=5),
	review TEXT NOT NULL,	
	created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
	created_person VARCHAR(50) NOT NULL,
	modified_date DATETIME DEFAULT current_timestamp on update current_timestamp,
	modified_person VARCHAR(50) NOT NULL,
	CONSTRAINT FK_matchup_request_id FOREIGN KEY (request_id) REFERENCES matchup_request(matchup_request_id)
);

DROP TABLE IF EXISTS guest_board;
CREATE TABLE guest_board(
	guest_board_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
	writer_id BIGINT NOT NULL, -- FK 설정 해야됨
	sports_type_id BIGINT NOT NULL, -- FK 설정 해야됨
	preferred_position_id BIGINT NOT NULL, -- FK 설정 해야됨
	picture_attachment_enabled BOOLEAN NOT NULL DEFAULT TRUE CHECK (picture_attachment_enabled=true),
	self_intro TEXT NOT NULL,
	preferred_region1 VARCHAR(50) NOT NULL,
	preferred_region2 VARCHAR(50) NULL,
	preferred_region3 VARCHAR(50) NULL,
	preferred_time1 ENUM('weekday_morning', 'weekday_afternoon', 'weekday_evening', 'weekend_morning', 'weekend_afternoon', 'weekend_evening') NOT NULL DEFAULT 'weekend_morning',
	preferred_time2 ENUM('weekday_morning', 'weekday_afternoon', 'weekday_evening', 'weekend_morning', 'weekend_afternoon', 'weekend_evening') NULL,
	preferred_time3 ENUM('weekday_morning', 'weekday_afternoon', 'weekday_evening', 'weekend_morning', 'weekend_afternoon', 'weekend_evening') NULL,
	preferred_time4 ENUM('weekday_morning', 'weekday_afternoon', 'weekday_evening', 'weekend_morning', 'weekend_afternoon', 'weekend_evening') NULL,
	preferred_time5 ENUM('weekday_morning', 'weekday_afternoon', 'weekday_evening', 'weekend_morning', 'weekend_afternoon', 'weekend_evening') NULL,
	preferred_time6 ENUM('weekday_morning', 'weekday_afternoon', 'weekday_evening', 'weekend_morning', 'weekend_afternoon', 'weekend_evening') NULL,
	created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
	created_person VARCHAR(50) NOT NULL,
	modified_date DATETIME DEFAULT current_timestamp on update current_timestamp,
	modified_person VARCHAR(50) NOT NULL,
	is_deleted BOOLEAN NOT NULL DEFAULT FALSE

);

DROP TABLE IF EXISTS guest_request;
CREATE TABLE guest_request(
	guest_request_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
	guest_board_id BIGINT NOT NULL, -- FK 설정 해야됨
	applicant_id BIGINT NOT NULL, -- FK 설정 해야됨
	reservation_attachment_enabled BOOLEAN NOT NULL DEFAULT TRUE CHECK (reservation_attachment_enabled=true),
	self_intro TEXT NOT NULL,
	sports_facility_name VARCHAR(100) NOT NULL,
	sports_facility_address VARCHAR(100) NOT NULL, 
	match_date DATETIME NOT NULL,
	match_duration TIME NOT NULL,
	match_description TEXT NOT NULL, 
	status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
	created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
	created_person VARCHAR(50) NOT NULL,
	modified_date DATETIME DEFAULT current_timestamp on update current_timestamp,
	modified_person VARCHAR(50) NOT NULL,
	is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
	CONSTRAINT FK_guest_board_id FOREIGN KEY (guest_board_id) REFERENCES guest_board(guest_board_id)
);

DROP TABLE IF EXISTS guest_rating;
CREATE TABLE guest_rating(
	guest_rating_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
	request_id BIGINT NOT NULL, 
	evaluator_id BIGINT NOT NULL, -- FK 설정 해야됨
	target_member_id BIGINT NOT NULL, -- FK 설정 해야됨
	manner_score INT NOT NULL CHECK (1<=manner_score AND manner_score<=5),
	skill_score INT NOT NULL CHECK (1<=skill_score AND skill_score<=5),
	review TEXT NOT NULL,
	created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
	created_person VARCHAR(50) NOT NULL,
	modified_date DATETIME DEFAULT current_timestamp on update current_timestamp,
	modified_person VARCHAR(50) NOT NULL,
	CONSTRAINT FK_request_id FOREIGN KEY (request_id) REFERENCES guest_request(guest_request_id)

);

DROP TABLE IF EXISTS attachment;
CREATE TABLE attachment(
	attachment_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
	board_type ENUM('matchup_board', 'guest_board', 'guest_request', 'chat_message') NOT NULL, 
	board_number BIGINT NOT NULL, 
	file_order INT NOT NULL,
	original_name VARCHAR(255) NOT NULL,
	saved_name VARCHAR(255) NOT NULL,
	save_path VARCHAR(255) NOT NULL,
	thumbnail_path VARCHAR(255) NULL,
	created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
	created_person VARCHAR(50) NOT NULL,
	modified_date DATETIME DEFAULT current_timestamp on update current_timestamp,
	modified_person VARCHAR(50) NOT NULL,
	is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

DROP TABLE IF EXISTS chat_room;
CREATE TABLE chat_room(
	chat_room_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
	is_group_chat BOOLEAN DEFAULT TRUE,
	chat_room_name VARCHAR(255) NOT NULL,
	created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
	created_person VARCHAR(50) NOT NULL,
	modified_date DATETIME DEFAULT current_timestamp on update current_timestamp,
	modified_person VARCHAR(50) NOT NULL,
	is_deleted BOOLEAN NOT NULL DEFAULT FALSE
	
);

DROP TABLE IF EXISTS chat_message;
CREATE TABLE chat_message(
	chat_message_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
	chat_room_id BIGINT NOT NULL, 
	sender_id BIGINT NOT NULL, -- FK 설정 해야됨
	all_attachment_enabled BOOLEAN NOT NULL DEFAULT TRUE CHECK (all_attachment_enabled=true),
	content TEXT NOT NULL, 
	created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
	created_person VARCHAR(50) NOT NULL,
	modified_date DATETIME DEFAULT current_timestamp on update current_timestamp,
	modified_person VARCHAR(50) NOT NULL,
	is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
	CONSTRAINT FK_chat_message_tbl_chat_room_id_ FOREIGN KEY (chat_room_id) REFERENCES chat_room(chat_room_id)

);

DROP TABLE IF EXISTS chat_participant;
CREATE TABLE chat_participant(
	chat_participant_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
	chat_room_id BIGINT NOT NULL,
	member_id BIGINT NOT NULL, -- FK 설정 해야됨
	created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
	created_person VARCHAR(50) NOT NULL,
	modified_date DATETIME DEFAULT current_timestamp on update current_timestamp,
	modified_person VARCHAR(50) NOT NULL,
	is_deleted BOOLEAN NOT NULL DEFAULT FALSE,	
	CONSTRAINT FK_chat_participant_tbl_chat_room_id FOREIGN KEY (chat_room_id) REFERENCES chat_room(chat_room_id)
);

DROP TABLE IF EXISTS message_read_log;
CREATE TABLE message_read_log(
	message_read_log_id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
	chat_room_id BIGINT NOT NULL, 
	receiver_id BIGINT NOT NULL, -- FK 설정 해야됨
	chat_message_id BIGINT NOT NULL, 
	is_read BOOLEAN DEFAULT FALSE,
	created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
	created_person VARCHAR(50) NOT NULL,
	modified_date DATETIME DEFAULT current_timestamp on update current_timestamp,
	modified_person VARCHAR(50) NOT NULL,
	CONSTRAINT FK_message_read_log_tbl_chat_room_id FOREIGN KEY (chat_room_id) REFERENCES chat_room(chat_room_id),
	CONSTRAINT FK_chat_message_id FOREIGN KEY (chat_message_id) REFERENCES chat_message(chat_message_id)

);





















