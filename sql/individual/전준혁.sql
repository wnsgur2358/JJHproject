CREATE TABLE board (
    board_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    board_attachment_enabled BOOLEAN DEFAULT FALSE,
    created_person VARCHAR(50),
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_person VARCHAR(50),
    modified_date DATETIME,   
    category ENUM('announcement', 'freeboard', 'information') DEFAULT 'freeboard',
    is_deleted BOOLEAN DEFAULT FALSE,   
    writer BIGINT NOT NULL,
    FOREIGN KEY (writer) REFERENCES member(member_id)
);




CREATE TABLE comment (
    comment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    created_person BIGINT NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_person BIGINT,
    modified_date DATETIME,
    is_deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (created_person) REFERENCES member(member_id),
    FOREIGN KEY (modified_person) REFERENCES member(member_id)
);