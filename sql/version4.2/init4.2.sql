USE matchon;
INSERT INTO sports_type (sports_type_name)
VALUES
    ('SOCCER'),
    ('FUTSAL');

INSERT INTO attachment (board_type, board_number, file_order, original_name, saved_name, save_path,
                        thumbnail_path, created_date, created_person, modified_date, modified_person, is_deleted)
VALUES
    ('MATCHUP_BOARD', 1, 1, 'file1.png', 'file1_saved.png', '/uploads/2025/05/', '/uploads/2025/05/thumb/file1_saved.png', '2025-05-17 20:52:28', 'System', '2025-05-17 20:52:28', 'System', FALSE),
    ('MATCHUP_BOARD', 2, 1, 'file2.png', 'file2_saved.png', '/uploads/2025/05/', '/uploads/2025/05/thumb/file2_saved.png', '2025-05-17 20:52:28', 'System', '2025-05-17 20:52:28', 'System', FALSE),
    ('MATCHUP_BOARD', 3, 1, 'file3.pdf', 'file3_saved.pdf', '/uploads/2025/05/', NULL, '2025-05-17 20:52:28', 'System', '2025-05-17 20:52:28', 'System', FALSE),
    ('CHAT_MESSAGE', 4, 1, 'file4.png', 'file4_saved.png', '/uploads/2025/05/', '/uploads/2025/05/thumb/file4_saved.png', '2025-05-17 20:52:28', 'System', '2025-05-17 20:52:28', 'System', FALSE),
    ('MEMBER', 5, 1, 'file5.png', 'file5_saved.png', '/uploads/2025/05/', '/uploads/2025/05/thumb/file5_saved.png', '2025-05-17 20:52:28', 'System', '2025-05-17 20:52:28', 'System', FALSE),
    ('HOST_PROFILE', 6, 1, 'file6.png', 'file6_saved.png', '/uploads/2025/05/', '/uploads/2025/05/thumb/file6_saved.png', '2025-05-17 20:52:28', 'System', '2025-05-17 20:52:28', 'System', FALSE),
    ('TEAM', 7, 1, 'file7.png', 'file7_saved.png', '/uploads/2025/05/', '/uploads/2025/05/thumb/file7_saved.png', '2025-05-17 20:52:28', 'System', '2025-05-17 20:52:28', 'System', FALSE),
    ('STADIUM', 8, 1, 'file8.png', 'file8_saved.png', '/uploads/2025/05/', '/uploads/2025/05/thumb/file8_saved.png', '2025-05-17 20:52:28', 'System', '2025-05-17 20:52:28', 'System', FALSE),
    ('BOARD', 9, 1, 'file9.png', 'file9_saved.png', '/uploads/2025/05/', '/uploads/2025/05/thumb/file9_saved.png', '2025-05-17 20:52:28', 'System', '2025-05-17 20:52:28', 'System', FALSE),
    ('BOARD', 10, 1, 'file10.png', 'file10_saved.png', '/uploads/2025/05/', '/uploads/2025/05/thumb/file10_saved.png', '2025-05-17 20:52:28', 'System', '2025-05-17 20:52:28', 'System', FALSE);

INSERT INTO chat_room (is_group_chat, chat_room_name, created_date, created_person, modified_date,
                       modified_person, is_deleted)
VALUES
    (TRUE, 'Chat Room 1', '2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', 'Member1', FALSE),
    (FALSE, 'Chat Room 2', '2025-05-17 20:52:28', 'Member2', '2025-05-17 20:52:28', 'Member2', FALSE),
    (TRUE, 'Chat Room 3', '2025-05-17 20:52:28', 'Member3', '2025-05-17 20:52:28', 'Member3', FALSE),
    (FALSE, 'Chat Room 4', '2025-05-17 20:52:28', 'Member4', '2025-05-17 20:52:28', 'Member4', FALSE),
    (TRUE, 'Chat Room 5', '2025-05-17 20:52:28', 'Member5', '2025-05-17 20:52:28', 'Member5', FALSE),
    (FALSE, 'Chat Room 6', '2025-05-17 20:52:28', 'Member6', '2025-05-17 20:52:28', 'Member6', FALSE),
    (TRUE, 'Chat Room 7', '2025-05-17 20:52:28', 'Member7', '2025-05-17 20:52:28', 'Member7', FALSE),
    (FALSE, 'Chat Room 8', '2025-05-17 20:52:28', 'Member8', '2025-05-17 20:52:28', 'Member8', FALSE),
    (TRUE, 'Chat Room 9', '2025-05-17 20:52:28', 'Member9', '2025-05-17 20:52:28', 'Member9', FALSE),
    (FALSE, 'Chat Room 10', '2025-05-17 20:52:28', 'Member10', '2025-05-17 20:52:28', 'Member10', FALSE);

INSERT INTO stadium (stadium_region, stadium_name, stadium_address, stadium_image_attchment_enabled,
                     create_date, create_person, modified_date, modified_person, is_deleted)
VALUES
    ('Seoul', 'Seoul Stadium', 'Seoul Address', FALSE, '2025-05-17 20:52:28', 'Admin1', '2025-05-17 20:52:28',
     'Admin1', FALSE),
    ('Busan', 'Busan Stadium', 'Busan Address', FALSE, '2025-05-17 20:52:28', 'Admin2', '2025-05-17 20:52:28',
     'Admin2', FALSE),
    ('Incheon', 'Incheon Stadium', 'Incheon Address', FALSE, '2025-05-17 20:52:28', 'Admin3',
     '2025-05-17 20:52:28', 'Admin3', FALSE),
    ('Daegu', 'Daegu Stadium', 'Daegu Address', FALSE, '2025-05-17 20:52:28', 'Admin4', '2025-05-17 20:52:28',
     'Admin4', FALSE),
    ('Daejeon', 'Daejeon Stadium', 'Daejeon Address', FALSE, '2025-05-17 20:52:28', 'Admin5',
     '2025-05-17 20:52:28', 'Admin5', FALSE),
    ('Gwangju', 'Gwangju Stadium', 'Gwangju Address', FALSE, '2025-05-17 20:52:28', 'Admin6',
     '2025-05-17 20:52:28', 'Admin6', FALSE),
    ('Suwon', 'Suwon Stadium', 'Suwon Address', FALSE, '2025-05-17 20:52:28', 'Admin7', '2025-05-17 20:52:28',
     'Admin7', FALSE),
    ('Ulsan', 'Ulsan Stadium', 'Ulsan Address', FALSE, '2025-05-17 20:52:28', 'Admin8', '2025-05-17 20:52:28',
     'Admin8', FALSE),
    ('Jeju', 'Jeju Stadium', 'Jeju Address', FALSE, '2025-05-17 20:52:28', 'Admin9', '2025-05-17 20:52:28',
     'Admin9', FALSE),
    ('Sejong', 'Sejong Stadium', 'Sejong Address', FALSE, '2025-05-17 20:52:28', 'Admin10',
     '2025-05-17 20:52:28', 'Admin10', FALSE);

INSERT INTO positions (position_name)
VALUES
    ('GOALKEEPER'),
    ('CENTER_BACK'),
    ('LEFT_RIGHT_BACK'),
    ('LEFT_RIGHT_WING_BACK'),
    ('CENTRAL_DEFENSIVE_MIDFIELDER'),
    ('CENTRAL_MIDFIELDER'),
    ('CENTRAL_ATTACKING_MIDFIELDER'),
    ('LEFT_RIGHT_WING'),
    ('STRIKER_CENTER_FORWARD'),
    ('SECOND_STRIKER');


INSERT INTO team (team_name, team_region, team_rating_average, recruitment_status, created_person,
                  created_date, modified_person, modified_date, team_introduction, team_attachment_enabled, is_deleted)
VALUES
    ('Team1', 'CAPITAL_REGION', 1.76, TRUE, 'User1', '2025-05-17 20:52:28', 'User1', '2025-05-17 20:52:28',
     'This is team 1''s introduction.', TRUE, FALSE),
    ('Team2', 'YEONGNAM_REGION', 1.52, FALSE, 'User2', '2025-05-17 20:52:28', 'User2', '2025-05-17 20:52:28',
     'This is team 2''s introduction.', TRUE, FALSE),
    ('Team3', 'HONAM_REGION', 3.09, TRUE, 'User3', '2025-05-17 20:52:28', 'User3', '2025-05-17 20:52:28',
     'This is team 3''s introduction.', TRUE, FALSE),
    ('Team4', 'CHUNGCHEONG_REGION', 2.67, FALSE, 'User4', '2025-05-17 20:52:28', 'User4', '2025-05-17 20:52:28',
     'This is team 4''s introduction.', TRUE, FALSE),
    ('Team5', 'GANGWON_REGION', 2.08, TRUE, 'User5', '2025-05-17 20:52:28', 'User5', '2025-05-17 20:52:28',
     'This is team 5''s introduction.', TRUE, FALSE),
    ('Team6', 'JEJU', 2.76, FALSE, 'User6', '2025-05-17 20:52:28', 'User6', '2025-05-17 20:52:28',
     'This is team 6''s introduction.', TRUE, FALSE),
    ('Team7', 'CAPITAL_REGION', 2.20, TRUE, 'User7', '2025-05-17 20:52:28', 'User7', '2025-05-17 20:52:28',
     'This is team 7''s introduction.', TRUE, FALSE),
    ('Team8', 'YEONGNAM_REGION', 1.48, FALSE, 'User8', '2025-05-17 20:52:28', 'User8', '2025-05-17 20:52:28',
     'This is team 8''s introduction.', TRUE, FALSE),
    ('Team9', 'HONAM_REGION', 4.86, TRUE, 'User9', '2025-05-17 20:52:28', 'User9', '2025-05-17 20:52:28',
     'This is team 9''s introduction.', TRUE, FALSE),
    ('Team10', 'CHUNGCHEONG_REGION', 4.11, FALSE, 'User10', '2025-05-17 20:52:28', 'User10',
     '2025-05-17 20:52:28', 'This is team 10''s introduction.', TRUE, FALSE);

INSERT INTO member (member_email, member_password, member_name, member_role, position_id, preferred_time,
                    team_id, my_temperature, picture_attachment_enabled, created_date, modified_date, is_deleted)
VALUES
    ('user1@example.com', 'password1', 'Member1', 'USER', 1, 'WEEKEND_MORNING', 1, 35.8, TRUE,
     '2025-05-17 20:52:28', '2025-05-17 20:52:28', FALSE),
    ('user2@example.com', 'password2', 'Member2', 'USER', 2, 'WEEKEND_AFTERNOON', 2, 36.9, FALSE,
     '2025-05-17 20:52:28', '2025-05-17 20:52:28', FALSE),
    ('user3@example.com', 'password3', 'Member3', 'USER', 3, 'WEEKEND_EVENING', 3, 39.9, TRUE,
     '2025-05-17 20:52:28', '2025-05-17 20:52:28', FALSE),
    ('user4@example.com', 'password4', 'Member4', 'USER', 4, 'WEEKDAY_MORNING', 4, 39.0, FALSE,
     '2025-05-17 20:52:28', '2025-05-17 20:52:28', FALSE),
    ('user5@example.com', 'password5', 'Member5', 'USER', 5, 'WEEKDAY_AFTERNOON', 5, 39.1, TRUE,
     '2025-05-17 20:52:28', '2025-05-17 20:52:28', FALSE),
    ('user6@example.com', 'password6', 'Member6', 'USER', 6, 'WEEKDAY_EVENING', 6, 36.5, FALSE,
     '2025-05-17 20:52:28', '2025-05-17 20:52:28', FALSE),
    ('user7@example.com', 'password7', 'Member7', 'USER', 7, 'WEEKEND_MORNING', 7, 38.8, TRUE,
     '2025-05-17 20:52:28', '2025-05-17 20:52:28', FALSE),
    ('user8@example.com', 'password8', 'Member8', 'USER', 8, 'WEEKEND_AFTERNOON', 8, 36.1, FALSE,
     '2025-05-17 20:52:28', '2025-05-17 20:52:28', FALSE),
    ('user9@example.com', 'password9', 'Member9', 'USER', 9, 'WEEKEND_EVENING', 9, 35.9, TRUE,
     '2025-05-17 20:52:28', '2025-05-17 20:52:28', FALSE),
    ('user10@example.com', 'password10', 'Member10', 'USER', 10, 'WEEKDAY_MORNING', 10, 36.9, FALSE,
     '2025-05-17 20:52:28', '2025-05-17 20:52:28', FALSE);

INSERT INTO refresh_token (member_id, refresh_token_data, refresh_token_expired_date, created_date)
VALUES
    (1, 'dummy_token_1', '2025-05-18 20:52:28', '2025-05-17 20:52:28'),
    (2, 'dummy_token_2', '2025-05-19 20:52:28', '2025-05-17 20:52:28'),
    (3, 'dummy_token_3', '2025-05-20 20:52:28', '2025-05-17 20:52:28'),
    (4, 'dummy_token_4', '2025-05-21 20:52:28', '2025-05-17 20:52:28'),
    (5, 'dummy_token_5', '2025-05-22 20:52:28', '2025-05-17 20:52:28'),
    (6, 'dummy_token_6', '2025-05-23 20:52:28', '2025-05-17 20:52:28'),
    (7, 'dummy_token_7', '2025-05-24 20:52:28', '2025-05-17 20:52:28'),
    (8, 'dummy_token_8', '2025-05-25 20:52:28', '2025-05-17 20:52:28'),
    (9, 'dummy_token_9', '2025-05-26 20:52:28', '2025-05-17 20:52:28'),
    (10, 'dummy_token_10', '2025-05-27 20:52:28', '2025-05-17 20:52:28');

INSERT INTO inquiry (writer_id, inquiry_category, inquiry_title, inquiry_content, inquiry_status,
                     created_date, created_person, modified_date, modified_person, is_deleted)
VALUES
    (1, 'TEAM_GUEST', 'Inquiry 1 Title', 'Content of inquiry 1', 'PENDING', '2025-05-17 20:52:28', 'Member1',
     '2025-05-17 20:52:28', 'Member1', FALSE),
    (2, 'EVENT', 'Inquiry 2 Title', 'Content of inquiry 2', 'COMPLETED', '2025-05-17 20:52:28', 'Member2',
     '2025-05-17 20:52:28', 'Member2', FALSE),
    (3, 'TUTORIAL', 'Inquiry 3 Title', 'Content of inquiry 3', 'PENDING', '2025-05-17 20:52:28', 'Member3',
     '2025-05-17 20:52:28', 'Member3', FALSE),
    (4, 'MANNER_TEMPERATURE', 'Inquiry 4 Title', 'Content of inquiry 4', 'COMPLETED', '2025-05-17 20:52:28',
     'Member4', '2025-05-17 20:52:28', 'Member4', FALSE),
    (5, 'COMMUNITY', 'Inquiry 5 Title', 'Content of inquiry 5', 'PENDING', '2025-05-17 20:52:28', 'Member5',
     '2025-05-17 20:52:28', 'Member5', FALSE),
    (6, 'ACCOUNT', 'Inquiry 6 Title', 'Content of inquiry 6', 'COMPLETED', '2025-05-17 20:52:28', 'Member6',
     '2025-05-17 20:52:28', 'Member6', FALSE),
    (7, 'REPORT', 'Inquiry 7 Title', 'Content of inquiry 7', 'PENDING', '2025-05-17 20:52:28', 'Member7',
     '2025-05-17 20:52:28', 'Member7', FALSE),
    (8, 'TEAM_GUEST', 'Inquiry 8 Title', 'Content of inquiry 8', 'COMPLETED', '2025-05-17 20:52:28', 'Member8',
     '2025-05-17 20:52:28', 'Member8', FALSE),
    (9, 'EVENT', 'Inquiry 9 Title', 'Content of inquiry 9', 'PENDING', '2025-05-17 20:52:28', 'Member9',
     '2025-05-17 20:52:28', 'Member9', FALSE),
    (10, 'TUTORIAL', 'Inquiry 10 Title', 'Content of inquiry 10', 'COMPLETED', '2025-05-17 20:52:28', 'Member10',
     '2025-05-17 20:52:28', 'Member10', FALSE);

INSERT INTO faq (faq_id, created_date, created_person, modified_date, modified_person,
                 faq_category, faq_content, faq_title, is_deleted, admin_id)
VALUES (null, now(), 'admin', now(), 'admin', 'TEAM_GUEST', '팀 채팅 안내입니다.', '팀 채팅 안내', 0, 1),
       (null, now(), 'admin', now(), 'admin', 'ACCOUNT', '개인정보는 이렇게 수정하시면 됩니다.', '개인정보 수정하는 법', 0, 1),
       (null, now(), 'admin', now(), 'admin', 'REPORT', '신고방법 이렇게 이용하시면 됩니다.', '신고 방법', 0, 1),
       (null, now(), 'admin', now(), 'admin', 'TEAM_GUEST', 'Guest는 이렇게 이용하시면 됩니다.', 'Guest 이용 방법', 0, 1),
       (null, now(), 'admin', now(), 'admin', 'TUTORIAL', 'MatchON은 이렇게 이용하시면 됩니다.', 'MatchON 이용 방법', 0, 1),
       (null, now(), 'admin', now(), 'admin', 'MANNER_TEMPERATURE', '매너온도의 계산 기준은 이렇습니다.', '매너온도 계산 기준 안내', 0, 1),
       (null, now(), 'admin', now(), 'admin', 'TUTORIAL', '챗봇은 이렇게 이용하시면 됩니다.', '챗봇 이용 방법', 0, 1),
       (null, now(), 'admin', now(), 'admin', 'REPORT', '멤버 신고는 이렇게 하시면 됩니다.', '멤버 신고 안내', 0, 1);



INSERT INTO review (team_review_id, review_writer, team_id, review_rating, content, created_person, created_date, modified_person, modified_date, is_deleted)
VALUES (1, 1, 1, 2, 'Review content by Member1', 'Member1', '2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', FALSE),
       (2, 2, 2, 3, 'Review content by Member1', 'Member1', '2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', FALSE),
       (3, 3, 3, 5, 'Review content by Member1', 'Member1', '2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', FALSE),
       (4, 4, 4, 1, 'Review content by Member1', 'Member1', '2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', FALSE),
       (5, 5, 5, 3, 'Review content by Member1', 'Member1', '2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', FALSE),
       (6, 6, 6, 4, 'Review content by Member1', 'Member1', '2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', FALSE),
       (7, 7, 7, 2, 'Review content by Member1', 'Member1', '2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', FALSE),
       (8, 8, 8, 1, 'Review content by Member1', 'Member1', '2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', FALSE),
       (9, 9, 9, 4, 'Review content by Member1', 'Member1', '2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', FALSE),
       (10, 10, 10, 3, 'Review content by Member1', 'Member1', '2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', FALSE);

INSERT INTO response (review_id, review_response, created_person, created_date, modified_person,
                      modified_date, is_deleted)
VALUES (1, 'Response to review 1', 'Member2', '2025-05-17 20:52:28', 'Member2', '2025-05-17 20:52:28', FALSE),
       (2, 'Response to review 2', 'Member3', '2025-05-17 20:52:28', 'Member3', '2025-05-17 20:52:28', FALSE),
       (3, 'Response to review 3', 'Member4', '2025-05-17 20:52:28', 'Member4', '2025-05-17 20:52:28', FALSE),
       (4, 'Response to review 4', 'Member5', '2025-05-17 20:52:28', 'Member5', '2025-05-17 20:52:28', FALSE),
       (5, 'Response to review 5', 'Member6', '2025-05-17 20:52:28', 'Member6', '2025-05-17 20:52:28', FALSE),
       (6, 'Response to review 6', 'Member7', '2025-05-17 20:52:28', 'Member7', '2025-05-17 20:52:28', FALSE),
       (7, 'Response to review 7', 'Member8', '2025-05-17 20:52:28', 'Member8', '2025-05-17 20:52:28', FALSE),
       (8, 'Response to review 8', 'Member9', '2025-05-17 20:52:28', 'Member9', '2025-05-17 20:52:28', FALSE),
       (9, 'Response to review 9', 'Member10', '2025-05-17 20:52:28', 'Member10', '2025-05-17 20:52:28', FALSE),
       (10, 'Response to review 10', 'Member1', '2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', FALSE);

INSERT INTO team_join_request (applicant_id, join_request_status, created_person, created_date,
                               modified_person, modified_date, is_deleted)
VALUES (1, 'PENDING', 'Member1', '2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', FALSE),
       (2, 'DENIED', 'Member2', '2025-05-17 20:52:28', 'Member2', '2025-05-17 20:52:28', FALSE),
       (3, 'PENDING', 'Member3', '2025-05-17 20:52:28', 'Member3', '2025-05-17 20:52:28', FALSE),
       (4, 'APPROVED', 'Member4', '2025-05-17 20:52:28', 'Member4', '2025-05-17 20:52:28', FALSE),
       (5, 'PENDING', 'Member5', '2025-05-17 20:52:28', 'Member5', '2025-05-17 20:52:28', FALSE),
       (6, 'DENIED', 'Member6', '2025-05-17 20:52:28', 'Member6', '2025-05-17 20:52:28', FALSE),
       (7, 'PENDING', 'Member7', '2025-05-17 20:52:28', 'Member7', '2025-05-17 20:52:28', FALSE),
       (8, 'APPROVED', 'Member8', '2025-05-17 20:52:28', 'Member8', '2025-05-17 20:52:28', FALSE),
       (9, 'PENDING', 'Member9', '2025-05-17 20:52:28', 'Member9', '2025-05-17 20:52:28', FALSE),
       (10, 'DENIED', 'Member10', '2025-05-17 20:52:28', 'Member10', '2025-05-17 20:52:28', FALSE);

INSERT INTO board (title, content, board_attachment_enabled, created_person, created_date, modified_person,
                   modified_date, category, is_deleted, writer)
VALUES ('Board Post 1', 'Content of board post 1', FALSE, 'Member1', '2025-05-17 20:52:28', 'Member1',
        '2025-05-17 20:52:28', 'ANNOUNCEMENT', FALSE, 1),
       ('Board Post 2', 'Content of board post 2', FALSE, 'Member2', '2025-05-17 20:52:28', 'Member2',
        '2025-05-17 20:52:28', 'FREEBOARD', FALSE, 2),
       ('Board Post 3', 'Content of board post 3', FALSE, 'Member3', '2025-05-17 20:52:28', 'Member3',
        '2025-05-17 20:52:28', 'INFORMATION', FALSE, 3),
       ('Board Post 4', 'Content of board post 4', FALSE, 'Member4', '2025-05-17 20:52:28', 'Member4',
        '2025-05-17 20:52:28', 'ANNOUNCEMENT', FALSE, 4),
       ('Board Post 5', 'Content of board post 5', FALSE, 'Member5', '2025-05-17 20:52:28', 'Member5',
        '2025-05-17 20:52:28', 'FREEBOARD', FALSE, 5),
       ('Board Post 6', 'Content of board post 6', FALSE, 'Member6', '2025-05-17 20:52:28', 'Member6',
        '2025-05-17 20:52:28', 'INFORMATION', FALSE, 6),
       ('Board Post 7', 'Content of board post 7', FALSE, 'Member7', '2025-05-17 20:52:28', 'Member7',
        '2025-05-17 20:52:28', 'ANNOUNCEMENT', FALSE, 7),
       ('Board Post 8', 'Content of board post 8', FALSE, 'Member8', '2025-05-17 20:52:28', 'Member8',
        '2025-05-17 20:52:28', 'FREEBOARD', FALSE, 8),
       ('Board Post 9', 'Content of board post 9', TRUE, 'Member9', '2025-05-17 20:52:28', 'Member9',
        '2025-05-17 20:52:28', 'INFORMATION', FALSE, 9),
       ('Board Post 10', 'Content of board post 10', TRUE, 'Member10', '2025-05-17 20:52:28', 'Member10',
        '2025-05-17 20:52:28', 'ANNOUNCEMENT', FALSE, 10);

INSERT INTO comment (board_id, member_id, content, created_person, created_date, modified_person, modified_date,
                     is_deleted)
VALUES (1, 1, 'Comment 1 content', 'Member2', '2025-05-17 20:52:28', 'Member2', '2025-05-17 20:52:28', FALSE),
       (2, 2, 'Comment 2 content', 'Member3', '2025-05-17 20:52:28', 'Member3', '2025-05-17 20:52:28', FALSE),
       (3, 3, 'Comment 3 content', 'Member4', '2025-05-17 20:52:28', 'Member4', '2025-05-17 20:52:28', FALSE),
       (4, 4, 'Comment 4 content', 'Member5', '2025-05-17 20:52:28', 'Member5', '2025-05-17 20:52:28', FALSE),
       (5, 5, 'Comment 5 content', 'Member6', '2025-05-17 20:52:28', 'Member6', '2025-05-17 20:52:28', FALSE),
       (6, 6, 'Comment 6 content', 'Member7', '2025-05-17 20:52:28', 'Member7', '2025-05-17 20:52:28', FALSE),
       (7, 7, 'Comment 7 content', 'Member8', '2025-05-17 20:52:28', 'Member8', '2025-05-17 20:52:28', FALSE),
       (8, 8, 'Comment 8 content', 'Member9', '2025-05-17 20:52:28', 'Member9', '2025-05-17 20:52:28', FALSE),
       (9, 9, 'Comment 9 content', 'Member10', '2025-05-17 20:52:28', 'Member10', '2025-05-17 20:52:28', FALSE),
       (10, 10, 'Comment 10 content', 'Member1', '2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', FALSE);

INSERT INTO host_profile (host_id, host_name, created_date, modified_date, picture_attachment_enabled)
VALUES	(1, 'Host1', '2025-05-17 20:52:28', '2025-05-17 20:52:28', TRUE),
	    (2, 'Host2', '2025-05-17 20:52:28', '2025-05-17 20:52:28', TRUE),
	    (3, 'Host3', '2025-05-17 20:52:28', '2025-05-17 20:52:28', TRUE),
	    (4, 'Host4', '2025-05-17 20:52:28', '2025-05-17 20:52:28', TRUE),
	    (5, 'Host5', '2025-05-17 20:52:28', '2025-05-17 20:52:28', TRUE),
	    (6, 'Host6', '2025-05-17 20:52:28', '2025-05-17 20:52:28', TRUE),
	    (7, 'Host7', '2025-05-17 20:52:28', '2025-05-17 20:52:28', TRUE),
	    (8, 'Host8', '2025-05-17 20:52:28', '2025-05-17 20:52:28', TRUE),
	    (9, 'Host9', '2025-05-17 20:52:28', '2025-05-17 20:52:28', TRUE),
	    (10, 'Host10', '2025-05-17 20:52:28', '2025-05-17 20:52:28', TRUE);

INSERT INTO inquiry_answer (inquiry_id, admin_id, answer_content, created_date, created_person,
                            modified_date, modified_person, is_deleted)
VALUES
    (2, 1, 'Answer content for inquiry 2', '2025-05-17 20:52:28', 'Member1',
     '2025-05-17 20:52:28', 'Member1', FALSE),
    (4, 1, 'Answer content for inquiry 4', '2025-05-17 20:52:28', 'Member1',
     '2025-05-17 20:52:28', 'Member1', FALSE),
    (6, 1, 'Answer content for inquiry 6', '2025-05-17 20:52:28', 'Member1',
     '2025-05-17 20:52:28', 'Member1', FALSE),
    (8, 2, 'Answer content for inquiry 8', '2025-05-17 20:52:28', 'Member2',
     '2025-05-17 20:52:28', 'Member2', FALSE),
    (10, 2, 'Answer content for inquiry 10', '2025-05-17 20:52:28', 'Member2',
     '2025-05-17 20:52:28', 'Member2', FALSE);



INSERT INTO matchup_board (writer_id, sports_type_id, reservation_attachment_enabled, team_intro,
                           sports_facility_name, sports_facility_address, match_datetime, match_duration,
                           current_participant_count, max_participants, min_manner_temperature, match_description,
                           created_date, created_person, modified_date, modified_person, is_deleted)
VALUES (1, 1, TRUE, 'Team introduction by Member1''s team.', 'Sports Center 1', 'Address 1', '2025-05-18 20:52:28',
        '01:00:00', 3, 10, 35.9, 'Description for matchup board 1', '2025-05-17 20:52:28', 'Member1',
        '2025-05-17 20:52:28', 'Member1', FALSE),
       (2, 1, TRUE, 'Team introduction by Member2''s team.', 'Sports Center 2', 'Address 2', '2025-05-19 20:52:28',
        '02:00:00', 8, 10, 37.7, 'Description for matchup board 2', '2025-05-17 20:52:28', 'Member2',
        '2025-05-17 20:52:28', 'Member2', FALSE),
       (3, 2, TRUE, 'Team introduction by Member3''s team.', 'Sports Center 3', 'Address 3', '2025-05-20 20:52:28',
        '01:00:00', 2, 10, 35.3, 'Description for matchup board 3', '2025-05-17 20:52:28', 'Member3',
        '2025-05-17 20:52:28', 'Member3', FALSE),
       (4, 2, TRUE, 'Team introduction by Member4''s team.', 'Sports Center 4', 'Address 4', '2025-05-21 20:52:28',
        '02:00:00', 0, 10, 35.6, 'Description for matchup board 4', '2025-05-17 20:52:28', 'Member4',
        '2025-05-17 20:52:28', 'Member4', FALSE),
       (5, 1, TRUE, 'Team introduction by Member5''s team.', 'Sports Center 5', 'Address 5', '2025-05-22 20:52:28',
        '01:00:00', 5, 10, 35.9, 'Description for matchup board 5', '2025-05-17 20:52:28', 'Member5',
        '2025-05-17 20:52:28', 'Member5', FALSE),
       (6, 1, TRUE, 'Team introduction by Member6''s team.', 'Sports Center 6', 'Address 6', '2025-05-23 20:52:28',
        '02:00:00', 4, 10, 37.9, 'Description for matchup board 6', '2025-05-17 20:52:28', 'Member6',
        '2025-05-17 20:52:28', 'Member6', FALSE),
       (7, 2, TRUE, 'Team introduction by Member7''s team.', 'Sports Center 7', 'Address 7', '2025-05-24 20:52:28',
        '01:00:00', 1, 10, 37.9, 'Description for matchup board 7', '2025-05-17 20:52:28', 'Member7',
        '2025-05-17 20:52:28', 'Member7', FALSE),
       (8, 2, TRUE, 'Team introduction by Member8''s team.', 'Sports Center 8', 'Address 8', '2025-05-25 20:52:28',
        '02:00:00', 5, 10, 37.2, 'Description for matchup board 8', '2025-05-17 20:52:28', 'Member8',
        '2025-05-17 20:52:28', 'Member8', FALSE),
       (9, 1, TRUE, 'Team introduction by Member9''s team.', 'Sports Center 9', 'Address 9', '2025-05-26 20:52:28',
        '01:00:00', 6, 10, 37.7, 'Description for matchup board 9', '2025-05-17 20:52:28', 'Member9',
        '2025-05-17 20:52:28', 'Member9', FALSE),
       (10, 1, TRUE, 'Team introduction by Member10''s team.', 'Sports Center 10', 'Address 10',
        '2025-05-27 20:52:28', '02:00:00', 1, 10, 37.8, 'Description for matchup board 10', '2025-05-17 20:52:28',
        'Member10', '2025-05-17 20:52:28', 'Member10', FALSE);

INSERT INTO matchup_request (matchup_board_id, applicant_id, self_intro, participant_count, status,
                             request_submitted_count, cancel_submitted_count, created_date, created_person, modified_date, modified_person, is_deleted)
VALUES (1, 2, 'Self-introduction of Member2 for matchup 1', 3, 'PENDING', 1, 0, '2025-05-17 20:52:28', 'Member2',
        '2025-05-17 20:52:28', 'Member2', FALSE),
       (2, 3, 'Self-introduction of Member3 for matchup 2', 2, 'APPROVED', 1, 0,  '2025-05-17 20:52:28', 'Member3',
        '2025-05-17 20:52:28', 'Member3', FALSE),
       (3, 4, 'Self-introduction of Member4 for matchup 3', 1, 'PENDING', 1, 0,  '2025-05-17 20:52:28', 'Member4',
        '2025-05-17 20:52:28', 'Member4', FALSE),
       (4, 5, 'Self-introduction of Member5 for matchup 4', 3, 'APPROVED', 1, 0,  '2025-05-17 20:52:28', 'Member5',
        '2025-05-17 20:52:28', 'Member5', FALSE),
       (5, 6, 'Self-introduction of Member6 for matchup 5', 3, 'DENIED', 1, 0,  '2025-05-17 20:52:28', 'Member6',
        '2025-05-17 20:52:28', 'Member6', FALSE),
       (6, 7, 'Self-introduction of Member7 for matchup 6', 3, 'APPROVED', 1, 0,  '2025-05-17 20:52:28', 'Member7',
        '2025-05-17 20:52:28', 'Member7', FALSE),
       (7, 8, 'Self-introduction of Member8 for matchup 7', 1, 'PENDING', 1, 0,  '2025-05-17 20:52:28', 'Member8',
        '2025-05-17 20:52:28', 'Member8', FALSE),
       (8, 9, 'Self-introduction of Member9 for matchup 8', 2, 'APPROVED', 1, 0, '2025-05-17 20:52:28', 'Member9',
        '2025-05-17 20:52:28', 'Member9', FALSE),
       (9, 10, 'Self-introduction of Member10 for matchup 9', 3, 'PENDING', 1, 0,  '2025-05-17 20:52:28', 'Member10',
        '2025-05-17 20:52:28', 'Member10', FALSE),
       (10, 1, 'Self-introduction of Member1 for matchup 10', 2, 'DENIED', 1, 0, '2025-05-17 20:52:28', 'Member1',
        '2025-05-17 20:52:28', 'Member1', FALSE);

INSERT INTO chat_message (chat_room_id, sender_id, all_attachment_enabled, content, created_date,
                          created_person, modified_date, modified_person, is_deleted)
VALUES (1, 1, FALSE, 'Message 1 from Member1', '2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', 'Member1',
        FALSE),
       (2, 3, FALSE, 'Message 2 from Member3', '2025-05-17 20:52:28', 'Member3', '2025-05-17 20:52:28', 'Member3',
        FALSE),
       (3, 4, FALSE, 'Message 3 from Member4', '2025-05-17 20:52:28', 'Member4', '2025-05-17 20:52:28', 'Member4',
        FALSE),
       (4, 5, FALSE, 'Message 4 from Member5', '2025-05-17 20:52:28', 'Member5', '2025-05-17 20:52:28', 'Member5',
        FALSE),
       (5, 6, FALSE, 'Message 5 from Member6', '2025-05-17 20:52:28', 'Member6', '2025-05-17 20:52:28', 'Member6',
        FALSE),
       (6, 7, FALSE, 'Message 6 from Member7', '2025-05-17 20:52:28', 'Member7', '2025-05-17 20:52:28', 'Member7',
        FALSE),
       (7, 8, FALSE, 'Message 7 from Member8', '2025-05-17 20:52:28', 'Member8', '2025-05-17 20:52:28', 'Member8',
        FALSE),
       (8, 9, FALSE, 'Message 8 from Member9', '2025-05-17 20:52:28', 'Member9', '2025-05-17 20:52:28', 'Member9',
        FALSE),
       (9, 10, FALSE, 'Message 9 from Member10', '2025-05-17 20:52:28', 'Member10', '2025-05-17 20:52:28',
        'Member10', FALSE),
       (10, 1, FALSE, 'Message 10 from Member1', '2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', 'Member1',
        FALSE);

INSERT INTO chat_participant (chat_room_id, member_id, chat_role, created_date, created_person, modified_date,
                              modified_person, is_deleted)
VALUES (1, 1, 'MEMBER','2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', 'Member1', FALSE),
       (2, 3, 'OWNER','2025-05-17 20:52:28', 'Member3', '2025-05-17 20:52:28', 'Member3', FALSE),
       (3, 4, 'ADMIN','2025-05-17 20:52:28', 'Member4', '2025-05-17 20:52:28', 'Member4', FALSE),
       (4, 5, 'MEMBER','2025-05-17 20:52:28', 'Member5', '2025-05-17 20:52:28', 'Member5', FALSE),
       (5, 6, 'OWNER','2025-05-17 20:52:28', 'Member6', '2025-05-17 20:52:28', 'Member6', FALSE),
       (6, 7, 'ADMIN','2025-05-17 20:52:28', 'Member7', '2025-05-17 20:52:28', 'Member7', FALSE),
       (7, 8, 'MEMBER','2025-05-17 20:52:28', 'Member8', '2025-05-17 20:52:28', 'Member8', FALSE),
       (8, 9, 'OWNER','2025-05-17 20:52:28', 'Member9', '2025-05-17 20:52:28', 'Member9', FALSE),
       (9, 10, 'ADMIN','2025-05-17 20:52:28', 'Member10', '2025-05-17 20:52:28', 'Member10', FALSE),
       (10, 1, 'MEMBER','2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', 'Member1', FALSE);

INSERT INTO team_member (member_id, team_id, introduction, team_leader_status)
VALUES (1, 1, 'Hello from Member1', TRUE),
       (2, 2, 'Hello from Member2', TRUE),
       (3, 3, 'Hello from Member3', FALSE),
       (4, 4, 'Hello from Member4', FALSE),
       (5, 5, 'Hello from Member5', FALSE),
       (6, 6, 'Hello from Member6', FALSE),
       (7, 7, 'Hello from Member7', FALSE),
       (8, 8, 'Hello from Member8', FALSE),
       (9, 9, 'Hello from Member9', FALSE),
       (10, 10, 'Hello from Member10', FALSE);

INSERT INTO recruiting_position (team_id, position_id)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 4),
       (5, 5),
       (6, 6),
       (7, 7),
       (8, 8),
       (9, 9),
       (10, 10);

INSERT INTO event_request (host_id, event_date, event_region, event_title, host_profile_id, event_address, event_method,
                           event_contact, event_status, created_date, created_person, modified_date, modified_person,
                           is_deleted)
VALUES (1, '2025-05-17', 'CAPITAL_REGION', 'Event 1 Title', 1, 'Address 1', 'Online', '010-0000-0001', 'APPROVED',
        '2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', 'Member1', FALSE),
       (2, '2025-05-17', 'YEONGNAM_REGION', 'Event 2 Title', 1, 'Address 2', 'Offline', '010-0000-0002', 'DENIED',
        '2025-05-17 20:52:28', 'Member2', '2025-05-17 20:52:28', 'Member2', FALSE),
       (3, '2025-05-17', 'HONAM_REGION', 'Event 3 Title', 2, 'Address 3', 'Online', '010-0000-0003', 'PENDING',
        '2025-05-17 20:52:28', 'Member3', '2025-05-17 20:52:28', 'Member3', FALSE),
       (4, '2025-05-17', 'CHUNGCHEONG_REGION', 'Event 4 Title', 2, 'Address 4', 'Offline', '010-0000-0004', 'APPROVED',
        '2025-05-17 20:52:28', 'Member4', '2025-05-17 20:52:28', 'Member4', FALSE),
       (5, '2025-05-17', 'GANGWON_REGION', 'Event 5 Title', 3, 'Address 5', 'Online', '010-0000-0005', 'DENIED',
        '2025-05-17 20:52:28', 'Member5', '2025-05-17 20:52:28', 'Member5', FALSE),
       (6, '2025-05-17', 'JEJU', 'Event 6 Title', 3, 'Address 6', 'Offline', '010-0000-0006', 'PENDING', '2025-05-17 20:52:28',
        'Member6', '2025-05-17 20:52:28', 'Member6', FALSE),
       (7, '2025-05-17', 'CAPITAL_REGION', 'Event 7 Title', 3, 'Address 7', 'Online', '010-0000-0007', 'APPROVED',
        '2025-05-17 20:52:28', 'Member7', '2025-05-17 20:52:28', 'Member7', FALSE),
       (8, '2025-05-17', 'YEONGNAM_REGION', 'Event 8 Title', 4, 'Address 8', 'Offline', '010-0000-0008', 'DENIED',
        '2025-05-17 20:52:28', 'Member8', '2025-05-17 20:52:28', 'Member8', FALSE),
       (9, '2025-05-17', 'HONAM_REGION', 'Event 9 Title', 4, 'Address 9', 'Online', '010-0000-0009', 'PENDING',
        '2025-05-17 20:52:28', 'Member9', '2025-05-17 20:52:28', 'Member9', FALSE),
       (10, '2025-05-17', 'CHUNGCHEONG_REGION', 'Event 10 Title', 4, 'Address 10', 'Offline', '010-0000-0010', 'APPROVED',
        '2025-05-17 20:52:28', 'Member10', '2025-05-17 20:52:28', 'Member10', FALSE);

INSERT INTO chat_user_block(blocker_id, blocked_id, created_person, modified_person)
VALUES 
(1, 2, 'Member1', 'Member3'),
(2, 3, 'Member3', 'Member3'),
(3, 4, 'Member3', 'Member4'),
(4, 5, 'Member4', 'Member5'),
(5, 6, 'Member5', 'Member6'),
(6, 7, 'Member6', 'Member7'),
(7, 8, 'Member7', 'Member8'),
(8, 9, 'Member8', 'Member9'),
(9, 10, 'Member9', 'Member10'),
(10, 1, 'Member10', 'Member1');


INSERT INTO matchup_rating (request_id, evaluator_id, target_member_id, manner_score, skill_score,
                            review, created_date, created_person, modified_date, modified_person)
VALUES (1, 1, 2, 5, 4, 'Review for matchup request 1', '2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28',
        'Member1'),
       (2, 2, 3, 1, 3, 'Review for matchup request 2', '2025-05-17 20:52:28', 'Member2', '2025-05-17 20:52:28',
        'Member2'),
       (3, 3, 4, 3, 2, 'Review for matchup request 3', '2025-05-17 20:52:28', 'Member3', '2025-05-17 20:52:28',
        'Member3'),
       (4, 4, 5, 3, 2, 'Review for matchup request 4', '2025-05-17 20:52:28', 'Member4', '2025-05-17 20:52:28',
        'Member4'),
       (5, 5, 6, 2, 1, 'Review for matchup request 5', '2025-05-17 20:52:28', 'Member5', '2025-05-17 20:52:28',
        'Member5'),
       (6, 6, 7, 1, 2, 'Review for matchup request 6', '2025-05-17 20:52:28', 'Member6', '2025-05-17 20:52:28',
        'Member6'),
       (7, 7, 8, 1, 3, 'Review for matchup request 7', '2025-05-17 20:52:28', 'Member7', '2025-05-17 20:52:28',
        'Member7'),
       (8, 8, 9, 3, 3, 'Review for matchup request 8', '2025-05-17 20:52:28', 'Member8', '2025-05-17 20:52:28',
        'Member8'),
       (9, 9, 10, 4, 4, 'Review for matchup request 9', '2025-05-17 20:52:28', 'Member9', '2025-05-17 20:52:28',
        'Member9'),
       (10, 10, 1, 1, 5, 'Review for matchup request 10', '2025-05-17 20:52:28', 'Member10', '2025-05-17 20:52:28',
        'Member10');


INSERT INTO message_read_log (chat_room_id, receiver_id, chat_message_id, is_read, created_date,
                              created_person, modified_date, modified_person)
VALUES (1, 1, 1, FALSE, '2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', 'Member1'),
       (2, 2, 2, FALSE, '2025-05-17 20:52:28', 'Member2', '2025-05-17 20:52:28', 'Member2'),
       (3, 3, 3, FALSE, '2025-05-17 20:52:28', 'Member3', '2025-05-17 20:52:28', 'Member3'),
       (4, 4, 4, FALSE, '2025-05-17 20:52:28', 'Member4', '2025-05-17 20:52:28', 'Member4'),
       (5, 5, 5, FALSE, '2025-05-17 20:52:28', 'Member5', '2025-05-17 20:52:28', 'Member5'),
       (6, 6, 6, TRUE, '2025-05-17 20:52:28', 'Member6', '2025-05-17 20:52:28', 'Member6'),
       (7, 7, 7, TRUE, '2025-05-17 20:52:28', 'Member7', '2025-05-17 20:52:28', 'Member7'),
       (8, 8, 8, TRUE, '2025-05-17 20:52:28', 'Member8', '2025-05-17 20:52:28', 'Member8'),
       (9, 9, 9, TRUE, '2025-05-17 20:52:28', 'Member9', '2025-05-17 20:52:28', 'Member9'),
       (10, 10, 10, TRUE, '2025-05-17 20:52:28', 'Member10', '2025-05-17 20:52:28', 'Member10');


