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
    ('SECOND_STRIKER'),
    ('LEFT_RIGHT_WINGER');

INSERT INTO chat_room (is_group_chat, chat_room_name, created_date, created_person,
                       modified_date, modified_person, is_deleted)
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
    (FALSE, 'Chat Room 10', '2025-05-17 20:52:28', 'Member10', '2025-05-17 20:52:28', 'Member10', FALSE),
    (TRUE, 'Team 1 Chat', '2025-06-05 10:00:00', 'System', '2025-06-05 10:00:00', 'System', FALSE),
    (TRUE, 'Team 2 Chat', '2025-06-05 10:00:00', 'System', '2025-06-05 10:00:00', 'System', FALSE),
    (TRUE, 'Team 3 Chat', '2025-06-05 10:00:00', 'System', '2025-06-05 10:00:00', 'System', FALSE),
    (TRUE, 'Team 4 Chat', '2025-06-05 10:00:00', 'System', '2025-06-05 10:00:00', 'System', FALSE),
    (TRUE, 'Team 5 Chat', '2025-06-05 10:00:00', 'System', '2025-06-05 10:00:00', 'System', FALSE),
    (TRUE, 'Team 6 Chat', '2025-06-05 10:00:00', 'System', '2025-06-05 10:00:00', 'System', FALSE),
    (TRUE, 'Team 7 Chat', '2025-06-05 10:00:00', 'System', '2025-06-05 10:00:00', 'System', FALSE),
    (TRUE, 'Team 8 Chat', '2025-06-05 10:00:00', 'System', '2025-06-05 10:00:00', 'System', FALSE),
    (TRUE, 'Team 9 Chat', '2025-06-05 10:00:00', 'System', '2025-06-05 10:00:00', 'System', FALSE),
    (TRUE, 'Team 10 Chat', '2025-06-05 10:00:00', 'System', '2025-06-05 10:00:00', 'System', FALSE);


INSERT INTO team (team_name, team_region, team_rating_average, recruitment_status, chat_room_id, created_person,
                  created_date, modified_person, modified_date, team_introduction, team_attachment_enabled, is_deleted)
VALUES
    ('Team1', 'CAPITAL_REGION', 2.00, TRUE, 1, 'user1@example.com', '2025-05-17 20:52:28', 'user1@example.com', '2025-05-17 20:52:28',
     'This is team 1''s introduction.', TRUE, FALSE),
    ('Team2', 'YEONGNAM_REGION', 3.00, FALSE, 2, 'user2@example.com', '2025-05-17 20:52:28', 'user2@example.com', '2025-05-17 20:52:28',
     'This is team 2''s introduction.', TRUE, FALSE),
    ('Team3', 'HONAM_REGION', 5.00, TRUE, 3, 'user3@example.com', '2025-05-17 20:52:28', 'user3@example.com', '2025-05-17 20:52:28',
     'This is team 3''s introduction.', TRUE, FALSE),
    ('Team4', 'CHUNGCHEONG_REGION', 1.00, FALSE, 4, 'user4@example.com', '2025-05-17 20:52:28', 'user4@example.com', '2025-05-17 20:52:28',
     'This is team 4''s introduction.', TRUE, FALSE),
    ('Team5', 'GANGWON_REGION', 3.00, TRUE, 5, 'user5@example.com', '2025-05-17 20:52:28', 'user5@example.com', '2025-05-17 20:52:28',
     'This is team 5''s introduction.', TRUE, FALSE),
    ('Team6', 'JEJU', 4.00, FALSE, 6, 'user6@example.com', '2025-05-17 20:52:28', 'user6@example.com', '2025-05-17 20:52:28',
     'This is team 6''s introduction.', TRUE, FALSE),
    ('Team7', 'CAPITAL_REGION', 2.00, TRUE, 7, 'user7@example.com', '2025-05-17 20:52:28', 'user7@example.com', '2025-05-17 20:52:28',
     'This is team 7''s introduction.', TRUE, FALSE),
    ('Team8', 'YEONGNAM_REGION', 1.00, FALSE, 8, 'user8@example.com', '2025-05-17 20:52:28', 'user8@example.com', '2025-05-17 20:52:28',
     'This is team 8''s introduction.', TRUE, FALSE),
    ('Team9', 'HONAM_REGION', 4.00, TRUE, 9, 'user9@example.com', '2025-05-17 20:52:28', 'user9@example.com', '2025-05-17 20:52:28',
     'This is team 9''s introduction.', TRUE, FALSE),
    ('Team10', 'CHUNGCHEONG_REGION', 3.00, FALSE, 10, 'user10@example.com', '2025-05-17 20:52:28', 'user10@example.com',
     '2025-05-17 20:52:28', 'This is team 10''s introduction.', TRUE, FALSE);




INSERT INTO member (member_email, member_password, previous_password, temporary_password, member_name, member_role, position_id, preferred_time,
                    team_id, my_temperature, picture_attachment_enabled, is_temporary_password, suspended_until, created_date, modified_date, is_deleted, email_agreement)
VALUES
    ('user1@example.com', '$2a$10$44Wyx23Yq2Ra1REDKpPsaesujiHxJFh..lnzrssN9tPG9jYmonEzS', NULL, NULL, 'Member1', 'USER', 1, 'WEEKEND_MORNING', 1, 35.8, TRUE, FALSE, NULL, '2025-05-17 20:52:28', '2025-05-17 20:52:28', FALSE, TRUE), -- Mathcon2025!!
    ('user2@example.com', '$2a$10$44Wyx23Yq2Ra1REDKpPsaesujiHxJFh..lnzrssN9tPG9jYmonEzS', NULL, NULL, 'Member2', 'USER', 2, 'WEEKEND_AFTERNOON', 2, 36.9, FALSE, FALSE, NULL, '2025-05-17 20:52:28', '2025-05-17 20:52:28', FALSE, TRUE), -- Mathcon2025!!
    ('user3@example.com', '$2a$10$44Wyx23Yq2Ra1REDKpPsaesujiHxJFh..lnzrssN9tPG9jYmonEzS', NULL, NULL, 'Member3', 'USER', 3, 'WEEKEND_EVENING', 3, 39.9, TRUE, FALSE, NULL, '2025-05-17 20:52:28', '2025-05-17 20:52:28', FALSE, FALSE), -- Mathcon2025!!
    ('user4@example.com', '$2a$10$44Wyx23Yq2Ra1REDKpPsaesujiHxJFh..lnzrssN9tPG9jYmonEzS', NULL, NULL, 'Member4', 'USER', 4, 'WEEKDAY_MORNING', 4, 39.0, FALSE, FALSE, NULL, '2025-05-17 20:52:28', '2025-05-17 20:52:28', FALSE, TRUE), -- Mathcon2025!!
    ('user5@example.com', '$2a$10$44Wyx23Yq2Ra1REDKpPsaesujiHxJFh..lnzrssN9tPG9jYmonEzS', NULL, NULL, 'Member5', 'USER', 5, 'WEEKDAY_AFTERNOON', 5, 39.1, TRUE, FALSE, NULL, '2025-05-17 20:52:28', '2025-05-17 20:52:28', FALSE, FALSE), -- Mathcon2025!!
    ('user6@example.com', '$2a$10$44Wyx23Yq2Ra1REDKpPsaesujiHxJFh..lnzrssN9tPG9jYmonEzS', NULL, NULL, 'Member6', 'USER', 6, 'WEEKDAY_EVENING', 6, 36.5, FALSE, FALSE, NULL, '2025-05-17 20:52:28', '2025-05-17 20:52:28', FALSE, TRUE), -- Mathcon2025!!
    ('user7@example.com', '$2a$10$44Wyx23Yq2Ra1REDKpPsaesujiHxJFh..lnzrssN9tPG9jYmonEzS', NULL, NULL, 'Member7', 'USER', 7, 'WEEKEND_MORNING', 7, 38.8, TRUE, FALSE, NULL, '2025-05-17 20:52:28', '2025-05-17 20:52:28', FALSE, TRUE), -- Mathcon2025!!
    ('user8@example.com', '$2a$10$44Wyx23Yq2Ra1REDKpPsaesujiHxJFh..lnzrssN9tPG9jYmonEzS', NULL, NULL, 'Member8', 'USER', 8, 'WEEKEND_AFTERNOON', 8, 36.1, FALSE, FALSE, NULL, '2025-05-17 20:52:28', '2025-05-17 20:52:28', FALSE, FALSE), -- Mathcon2025!!
    ('user9@example.com', '$2a$10$44Wyx23Yq2Ra1REDKpPsaesujiHxJFh..lnzrssN9tPG9jYmonEzS', NULL, NULL, 'Member9', 'USER', 9, 'WEEKEND_EVENING', 9, 35.9, TRUE, FALSE, NULL, '2025-05-17 20:52:28', '2025-05-17 20:52:28', FALSE, TRUE), -- Mathcon2025!!
    ('user10@example.com', '$2a$10$44Wyx23Yq2Ra1REDKpPsaesujiHxJFh..lnzrssN9tPG9jYmonEzS', NULL, NULL, 'Member10', 'USER', 10, 'WEEKDAY_MORNING', 10, 36.9, FALSE, FALSE, NULL, '2025-05-17 20:52:28', '2025-05-17 20:52:28', FALSE, TRUE), -- Mathcon2025!!
    ('matchon2025@gmail.com', '$2a$10$44Wyx23Yq2Ra1REDKpPsaesujiHxJFh..lnzrssN9tPG9jYmonEzS', NULL, NULL, 'admin', 'ADMIN', NULL, NULL, NULL, NULL, FALSE, FALSE, NULL, '2025-05-17 20:52:28', '2025-05-17 20:52:28', FALSE, TRUE); -- Mathcon2025!!

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
VALUES (null, now(), 'admin', now(), 'admin', 'TEAM_GUEST', '팀 채팅은 로그인 하신 후 팀 메뉴로 가셔서 팀을 생성하시거나 모집중인 팀에 가입하신 후 팀 채팅을 만드실 수 있습니다.
자세한 문의는 1:1문의를 이용해주세요', '팀 채팅 안내', 0, 1),
       (null, now(), 'admin', now(), 'admin', 'ACCOUNT', '로그인 하신 후 마이 들어가셔서 개인정보 수정하시면 됩니다.
자세한 문의는 1:1 문의를 이용해주세요', '개인정보 수정하는 법', 0, 1),
       (null, now(), 'admin', now(), 'admin', 'REPORT', '로그인 하신 후 cs>faq>1:1문의 들어가셔서 신고할 게시글을 작성해주세요.
해당 신고는 관리자 확인 후 조치되며, 관리자 판단하에따라 이루어집니다.
자세한 문의는 1:1문의를 이용해주세요', '신고 방법', 0, 1),
       (null, now(), 'admin', now(), 'admin', 'TEAM_GUEST', '로그인 하신 후 MATCHUP 들어가셔서 모집 중인 경기중에 참여하시고 싶은 경기에 참여 신청을 해주세요.
자세한 문의는 1:1문의를 이용해주세요', 'Guest 이용 방법', 0, 1),
       (null, now(), 'admin', now(), 'admin', 'TUTORIAL', 'INTRODUCTION 들어가셔서 글 확인해주세요.
자세한 문의는 1:1문의를 이용해주세요', 'MatchON 이용 방법', 0, 1),
       (null, now(), 'admin', now(), 'admin', 'MANNER_TEMPERATURE', '매너온도에서 매너 점수와 실력 점수의 비중은 7:3 입니다.
매너 점수와 실력 점수가 기본 점수보다 낮을 경우에는 매너 온도가 떨어집니다.
매너 점수와 실력 점수가 기본 점수와 같을 경우에는 매너 온도는 그대로 유지됩니다.
매너 점수와 실력 점수가 기본 점수보다 높을 경우에는 매너 온도가 상승합니다.
자세한 문의는 1:1문의를 이용해주세요', '매너온도 계산 기준 안내', 0, 1),
       (null, now(), 'admin', now(), 'admin', 'TUTORIAL', '시작 화면에서 매치봇(챗봇) 아이콘 누르시고 지정된 키워드로 이용해주세요.
자세한 문의는 1:1문의를 이용해주세요', '챗봇 이용 방법', 0, 1),
       (null, now(), 'admin', now(), 'admin', 'REPORT', '로그인 하신 후 피해를 끼치는 멤버를 1:1 문의에 신고해주세요.
해당 신고는 관리자 확인 후 조치되며, 관리자 판단하에따라 이루어집니다.
자세한 문의는 1:1문의를 이용해주세요', '멤버 신고 안내', 0, 1);




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

INSERT INTO response (review_id, review_response, responded_at, created_person, created_date, modified_person,
                      modified_date, is_deleted)
VALUES (1, 'Response to review 1', '2025-05-15 20:52:28', 'Member2', '2025-05-17 20:52:28', 'Member2', '2025-05-17 20:52:28', FALSE),
       (2, 'Response to review 2', '2025-05-15 20:52:28', 'Member3', '2025-05-17 20:52:28', 'Member3', '2025-05-17 20:52:28', FALSE),
       (3, 'Response to review 3', '2025-05-15 20:52:28', 'Member4', '2025-05-17 20:52:28', 'Member4', '2025-05-17 20:52:28', FALSE),
       (4, 'Response to review 4', '2025-05-15 20:52:28', 'Member5', '2025-05-17 20:52:28', 'Member5', '2025-05-17 20:52:28', FALSE),
       (5, 'Response to review 5', '2025-05-15 20:52:28', 'Member6', '2025-05-17 20:52:28', 'Member6', '2025-05-17 20:52:28', FALSE),
       (6, 'Response to review 6', '2025-05-15 20:52:28', 'Member7', '2025-05-17 20:52:28', 'Member7', '2025-05-17 20:52:28', FALSE),
       (7, 'Response to review 7', '2025-05-15 20:52:28', 'Member8', '2025-05-17 20:52:28', 'Member8', '2025-05-17 20:52:28', FALSE),
       (8, 'Response to review 8', '2025-05-15 20:52:28', 'Member9', '2025-05-17 20:52:28', 'Member9', '2025-05-17 20:52:28', FALSE),
       (9, 'Response to review 9', '2025-05-15 20:52:28', 'Member10', '2025-05-17 20:52:28', 'Member10', '2025-05-17 20:52:28', FALSE),
       (10, 'Response to review 10', '2025-05-15 20:52:28', 'Member1', '2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', FALSE);

INSERT INTO team_join_request (applicant_id, join_request_status, introduction, created_person, created_date,
                               modified_person, modified_date, is_deleted)
VALUES (1, 'PENDING', 'Member1', 'hihi','2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', FALSE),
       (2, 'DENIED', 'Member2', 'hihi','2025-05-17 20:52:28', 'Member2', '2025-05-17 20:52:28', FALSE),
       (3, 'PENDING', 'Member3', 'hihi','2025-05-17 20:52:28', 'Member3', '2025-05-17 20:52:28', FALSE),
       (4, 'APPROVED', 'Member4', 'hihi','2025-05-17 20:52:28', 'Member4', '2025-05-17 20:52:28', FALSE),
       (5, 'PENDING', 'Member5', 'hihi','2025-05-17 20:52:28', 'Member5', '2025-05-17 20:52:28', FALSE),
       (6, 'DENIED', 'Member6', 'hihi','2025-05-17 20:52:28', 'Member6', '2025-05-17 20:52:28', FALSE),
       (7, 'PENDING', 'Member7', 'hihi','2025-05-17 20:52:28', 'Member7', '2025-05-17 20:52:28', FALSE),
       (8, 'APPROVED', 'Member8', 'hihi','2025-05-17 20:52:28', 'Member8', '2025-05-17 20:52:28', FALSE),
       (9, 'PENDING', 'Member9', 'hihi','2025-05-17 20:52:28', 'Member9', '2025-05-17 20:52:28', FALSE),
       (10, 'DENIED', 'Member10', 'hihi','2025-05-17 20:52:28', 'Member10', '2025-05-17 20:52:28', FALSE);

INSERT INTO board (title, content, board_attachment_enabled, created_person, created_date,
                   modified_person, modified_date, category, is_deleted, pinned, writer)
VALUES
    ('Board Post 1', 'Content 1', TRUE, 'Member1', '2025-06-01 10:12:34', 'Member1', '2025-06-01 10:12:34', 'ANNOUNCEMENT', FALSE, FALSE, 1),
    ('Board Post 2', 'Content 2', FALSE, 'Member2', '2025-06-01 10:12:34', 'Member2', '2025-06-01 10:12:34', 'FREEBOARD', FALSE, FALSE, 2),
    ('Board Post 3', 'Content 3', TRUE, 'Member3', '2025-06-01 10:12:34', 'Member3', '2025-06-01 10:12:34', 'INFORMATION', FALSE, FALSE, 3),
    ('Board Post 4', 'Content 4', FALSE, 'Member4', '2025-06-01 10:12:34', 'Member4', '2025-06-01 10:12:34', 'FOOTBALL_TALK', FALSE, FALSE, 4),
    ('Board Post 5', 'Content 5', TRUE, 'Member5', '2025-06-01 10:12:34', 'Member5', '2025-06-01 10:12:34', 'ANNOUNCEMENT', FALSE, FALSE, 5),
    ('Board Post 6', 'Content 6', FALSE, 'Member6', '2025-06-01 10:12:34', 'Member6', '2025-06-01 10:12:34', 'FREEBOARD', FALSE, FALSE, 6),
    ('Board Post 7', 'Content 7', TRUE, 'Member7', '2025-06-01 10:12:34', 'Member7', '2025-06-01 10:12:34', 'INFORMATION', FALSE, FALSE, 7),
    ('Board Post 8', 'Content 8', FALSE, 'Member8', '2025-06-01 10:12:34', 'Member8', '2025-06-01 10:12:34', 'FOOTBALL_TALK', FALSE, FALSE, 8),
    ('Board Post 9', 'Content 9', TRUE, 'Member9', '2025-06-01 10:12:34', 'Member9', '2025-06-01 10:12:34', 'ANNOUNCEMENT', FALSE, FALSE, 9),
    ('Board Post 10', 'Content 10', FALSE, 'Member10', '2025-06-01 10:12:34', 'Member10', '2025-06-01 10:12:34', 'FREEBOARD', FALSE, FALSE, 10);

INSERT INTO report (report_type, target_id, reporter_id, reason, reason_type,
                    suspended, target_is_admin, target_member_id, target_writer_name,
                    created_date, created_person, modified_date, modified_person)
VALUES
    ('BOARD', 1, 2, '욕설이 포함되어 있습니다.', 'ABUSE', FALSE, FALSE, 1, 'Member1', '2025-06-01 10:12:34', 'Member2', '2025-06-01 10:12:34', 'Member2'),
    ('BOARD', 2, 3, '상업적 광고 링크가 포함됨.', 'ADVERTISEMENT', FALSE, FALSE, 2, 'Member2', '2025-06-01 10:12:34', 'Member3', '2025-06-01 10:12:34', 'Member3'),
    ('COMMENT', 3, 4, '같은 내용 반복 도배.', 'SPAM', FALSE, FALSE, 3, 'Member3', '2025-06-01 10:12:34', 'Member4', '2025-06-01 10:12:34', 'Member4'),
    ('BOARD', 4, 5, '게시판 성격에 맞지 않는 내용.', 'IRRELEVANT', FALSE, FALSE, 4, 'Member4', '2025-06-01 10:12:34', 'Member5', '2025-06-01 10:12:34', 'Member5'),
    ('COMMENT', 5, 6, '무의미한 글을 반복적으로 작성함.', 'SPAM', FALSE, FALSE, 5, 'Member5', '2025-06-01 10:12:34', 'Member6', '2025-06-01 10:12:34', 'Member6'),
    ('COMMENT', 6, 7, '지속적인 욕설과 비방 행위.', 'ABUSE', FALSE, FALSE, 6, 'Member6', '2025-06-01 10:12:34', 'Member7', '2025-06-01 10:12:34', 'Member7'),
    ('BOARD', 7, 8, '광고성 이벤트 참여 유도.', 'ADVERTISEMENT', FALSE, FALSE, 7, 'Member7', '2025-06-01 10:12:34', 'Member8', '2025-06-01 10:12:34', 'Member8'),
    ('COMMENT', 8, 9, '카테고리에 어울리지 않는 질문.', 'IRRELEVANT', FALSE, FALSE, 8, 'Member8', '2025-06-01 10:12:34', 'Member9', '2025-06-01 10:12:34', 'Member9'),
    ('BOARD', 9, 10, '기타 부적절한 사유로 신고합니다.', 'ETC', FALSE, FALSE, 9, 'Member9', '2025-06-01 10:12:34', 'Member10', '2025-06-01 10:12:34', 'Member10'),
    ('COMMENT', 10, 1, '관리자 권한 남용 의심', 'ETC', FALSE, TRUE, NULL, '관리자', '2025-06-01 10:12:34', 'Member1', '2025-06-01 10:12:34', 'Member1'),
    ('BOARD', 1, 4, '비방성 내용 포함.', 'ABUSE', FALSE, FALSE, 1, 'Member1', '2025-06-01 10:30:00', 'Member4', '2025-06-01 10:30:00', 'Member4'),
    ('COMMENT', 2, 5, '같은 문장을 반복해서 답변.', 'SPAM', FALSE, FALSE, 2, 'Member2', '2025-06-01 10:31:00', 'Member5', '2025-06-01 10:31:00', 'Member5'),
    ('BOARD', 3, 6, '광고성 내용 반복.', 'ADVERTISEMENT', FALSE, FALSE, 3, 'Member3', '2025-06-01 10:32:00', 'Member6', '2025-06-01 10:32:00', 'Member6'),
    ('COMMENT', 4, 7, '게시판 성격과 무관한 질문입니다.', 'IRRELEVANT', FALSE, FALSE, 4, 'Member4', '2025-06-01 10:33:00', 'Member7', '2025-06-01 10:33:00', 'Member7'),
    ('BOARD', 5, 8, '비속어 및 명예훼손성 발언.', 'ABUSE', FALSE, FALSE, 5, 'Member5', '2025-06-01 10:34:00', 'Member8', '2025-06-01 10:34:00', 'Member8'),
    ('BOARD', 6, 9, '상품 구매 유도 게시글입니다.', 'ADVERTISEMENT', FALSE, FALSE, 6, 'Member6', '2025-06-01 10:35:00', 'Member9', '2025-06-01 10:35:00', 'Member9'),
    ('COMMENT', 7, 10, '불필요한 댓글 반복.', 'SPAM', FALSE, FALSE, 7, 'Member7', '2025-06-01 10:36:00', 'Member10', '2025-06-01 10:36:00', 'Member10'),
    ('COMMENT', 8, 1, '지속적인 규칙 위반.', 'ETC', FALSE, FALSE, 8, 'Member8', '2025-06-01 10:37:00', 'Member1', '2025-06-01 10:37:00', 'Member1'),
    ('BOARD', 9, 2, '부적절한 이미지 포함.', 'IRRELEVANT', FALSE, FALSE, 9, 'Member9', '2025-06-01 10:38:00', 'Member2', '2025-06-01 10:38:00', 'Member2'),
    ('COMMENT', 10, 3, '운영자 권한을 남용하고 있음.', 'ETC', FALSE, TRUE, NULL, '관리자', '2025-06-01 10:39:00', 'Member3', '2025-06-01 10:39:00', 'Member3');

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

INSERT INTO notification (received_member_id, notification_message, target_url, is_read, created_person, modified_person)
VALUES
    (1, '[매치업] 6월 6일 경기 신청이 승인되었습니다.', '/matchup/board', FALSE, 'user1@example.com', 'user1@example.com'),
    (2, '[후기 요청] 6월 7일 경기 상대를 평가해주세요.', '/matchup/board', FALSE, 'user2@example.com', 'user2@example.com'),
    (3, '[알림] 새로운 채팅 메시지가 도착했습니다.', '/matchup/board', FALSE, 'user3@example.com', 'user3@example.com'),
    (4, '[공지] 6월 9일 시스템 점검 안내', '/matchup/board', FALSE, 'user4@example.com', 'user4@example.com'),
    (5, '[매치업] 새로운 경기 요청이 도착했습니다.', '/matchup/board', FALSE, 'user5@example.com', 'user5@example.com'),
    (6, '[팀 초대] MATCHON 팀에 초대되었습니다.', '/matchup/board', FALSE, 'user6@example.com', 'user6@example.com'),
    (7, '[경고] 비정상적인 접근이 감지되었습니다.', '/matchup/board', FALSE, 'user7@example.com', 'user7@example.com'),
    (8, '[알림] 일정이 곧 시작됩니다. 준비하세요!', '/matchup/board', FALSE, 'user8@example.com', 'user8@example.com'),
    (9, '[후기 수신] 상대방이 당신을 평가했습니다.', '/matchup/board', FALSE, 'user9@example.com', 'user9@example.com'),
    (10, '[채팅방] 새로운 참가자가 입장했습니다.', '/matchup/board', FALSE, 'user10@example.com', 'user10@example.com');



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
                           sports_facility_name, sports_facility_address, match_datetime, match_endtime,
                           current_participant_count, max_participants, min_manner_temperature, match_description,
                           chat_room_id, is_rating_initialized, is_notified,created_date, created_person, modified_date, modified_person, is_deleted)
VALUES (1, 1, TRUE, 'Team introduction by Member1''s team.', 'Sports Center 1', 'Address 1', '2025-05-18 20:52:28',
        '2025-05-18 22:52:28', 3, 10, 35.9, 'Description for matchup board 1', 1, FALSE, FALSE,'2025-05-17 20:52:28', 'Member1',
        '2025-05-17 20:52:28', 'Member1', FALSE),
       (2, 1, TRUE, 'Team introduction by Member2''s team.', 'Sports Center 2', 'Address 2', '2025-05-19 20:52:28',
        '2025-05-19 21:52:28', 8, 10, 37.7, 'Description for matchup board 2', 2, FALSE, FALSE,'2025-05-17 20:52:28', 'Member2',
        '2025-05-17 20:52:28', 'Member2', FALSE),
       (3, 2, TRUE, 'Team introduction by Member3''s team.', 'Sports Center 3', 'Address 3', '2025-05-20 20:52:28',
        '2025-05-20 22:52:28', 2, 10, 35.3, 'Description for matchup board 3', 3, FALSE, FALSE,'2025-05-17 20:52:28', 'Member3',
        '2025-05-17 20:52:28', 'Member3', FALSE),
       (4, 2, TRUE, 'Team introduction by Member4''s team.', 'Sports Center 4', 'Address 4', '2025-05-21 20:52:28',
        '2025-05-21 22:52:28', 0, 10, 35.6, 'Description for matchup board 4', 4, FALSE, FALSE,'2025-05-17 20:52:28', 'Member4',
        '2025-05-17 20:52:28', 'Member4', FALSE),
       (5, 1, TRUE, 'Team introduction by Member5''s team.', 'Sports Center 5', 'Address 5', '2025-05-22 20:52:28',
        '2025-05-22 22:52:28', 5, 10, 35.9, 'Description for matchup board 5', 5, FALSE, FALSE,'2025-05-17 20:52:28', 'Member5',
        '2025-05-17 20:52:28', 'Member5', FALSE),
       (6, 1, TRUE, 'Team introduction by Member6''s team.', 'Sports Center 6', 'Address 6', '2025-05-23 20:52:28',
        '2025-05-23 23:52:28', 4, 10, 37.9, 'Description for matchup board 6', 6, FALSE, FALSE,'2025-05-17 20:52:28', 'Member6',
        '2025-05-17 20:52:28', 'Member6', FALSE),
       (7, 2, TRUE, 'Team introduction by Member7''s team.', 'Sports Center 7', 'Address 7', '2025-05-24 20:52:28',
        '2025-05-24 23:52:28', 1, 10, 37.9, 'Description for matchup board 7', 7, FALSE, FALSE,'2025-05-17 20:52:28', 'Member7',
        '2025-05-17 20:52:28', 'Member7', FALSE),
       (8, 2, TRUE, 'Team introduction by Member8''s team.', 'Sports Center 8', 'Address 8', '2025-05-25 20:52:28',
        '2025-05-25 22:52:28', 5, 10, 37.2, 'Description for matchup board 8', 8, FALSE, FALSE,'2025-05-17 20:52:28', 'Member8',
        '2025-05-17 20:52:28', 'Member8', FALSE),
       (9, 1, TRUE, 'Team introduction by Member9''s team.', 'Sports Center 9', 'Address 9', '2025-05-26 20:52:28',
        '2025-05-26 22:52:28', 6, 10, 37.7, 'Description for matchup board 9', 9, FALSE, FALSE,'2025-05-17 20:52:28', 'Member9',
        '2025-05-17 20:52:28', 'Member9', FALSE),
       (10, 1, TRUE, 'Team introduction by Member10''s team.', 'Sports Center 10', 'Address 10', '2025-05-27 20:52:28', '2025-05-27 21:52:28', 1, 10, 37.8, 'Description for matchup board 10', 10, FALSE, FALSE,'2025-05-17 20:52:28',
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

INSERT INTO chat_message (chat_room_id, sender_id, content, created_date,
                          created_person, modified_date, modified_person, is_deleted)
VALUES (1, 1, 'Message 1 from Member1', '2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', 'Member1',
        FALSE),
       (2, 3, 'Message 2 from Member3', '2025-05-17 20:52:28', 'Member3', '2025-05-17 20:52:28', 'Member3',
        FALSE),
       (3, 4, 'Message 3 from Member4', '2025-05-17 20:52:28', 'Member4', '2025-05-17 20:52:28', 'Member4',
        FALSE),
       (4, 5, 'Message 4 from Member5', '2025-05-17 20:52:28', 'Member5', '2025-05-17 20:52:28', 'Member5',
        FALSE),
       (5, 6, 'Message 5 from Member6', '2025-05-17 20:52:28', 'Member6', '2025-05-17 20:52:28', 'Member6',
        FALSE),
       (6, 7, 'Message 6 from Member7', '2025-05-17 20:52:28', 'Member7', '2025-05-17 20:52:28', 'Member7',
        FALSE),
       (7, 8, 'Message 7 from Member8', '2025-05-17 20:52:28', 'Member8', '2025-05-17 20:52:28', 'Member8',
        FALSE),
       (8, 9, 'Message 8 from Member9', '2025-05-17 20:52:28', 'Member9', '2025-05-17 20:52:28', 'Member9',
        FALSE),
       (9, 10, 'Message 9 from Member10', '2025-05-17 20:52:28', 'Member10', '2025-05-17 20:52:28',
        'Member10', FALSE),
       (10, 1, 'Message 10 from Member1', '2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', 'Member1',
        FALSE);

INSERT INTO chat_participant (chat_room_id, member_id, created_date, created_person, modified_date,
                              modified_person, is_deleted)
VALUES (1, 1, '2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', 'Member1', FALSE),
       (2, 3, '2025-05-17 20:52:28', 'Member3', '2025-05-17 20:52:28', 'Member3', FALSE),
       (3, 4, '2025-05-17 20:52:28', 'Member4', '2025-05-17 20:52:28', 'Member4', FALSE),
       (4, 5, '2025-05-17 20:52:28', 'Member5', '2025-05-17 20:52:28', 'Member5', FALSE),
       (5, 6, '2025-05-17 20:52:28', 'Member6', '2025-05-17 20:52:28', 'Member6', FALSE),
       (6, 7, '2025-05-17 20:52:28', 'Member7', '2025-05-17 20:52:28', 'Member7', FALSE),
       (7, 8, '2025-05-17 20:52:28', 'Member8', '2025-05-17 20:52:28', 'Member8', FALSE),
       (8, 9, '2025-05-17 20:52:28', 'Member9', '2025-05-17 20:52:28', 'Member9', FALSE),
       (9, 10, '2025-05-17 20:52:28', 'Member10', '2025-05-17 20:52:28', 'Member10', FALSE),
       (10, 1, '2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', 'Member1', FALSE);

INSERT INTO team_member (member_id, team_id, introduction, team_leader_status)
VALUES (1, 1, 'Hello from Member1', TRUE),
       (2, 2, 'Hello from Member2', TRUE),
       (3, 3, 'Hello from Member3', TRUE),
       (4, 4, 'Hello from Member4', TRUE),
       (5, 5, 'Hello from Member5', TRUE),
       (6, 6, 'Hello from Member6', TRUE),
       (7, 7, 'Hello from Member7', TRUE),
       (8, 8, 'Hello from Member8', TRUE),
       (9, 9, 'Hello from Member9', TRUE),
       (10, 10, 'Hello from Member10', TRUE);

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

INSERT INTO event_request (host_id, event_date, event_region, event_title, event_description,host_profile_id, event_address, event_method,
                           event_contact, event_status, created_date, created_person, modified_date, modified_person,
                           is_deleted)
VALUES (1, '2025-05-17', 'CAPITAL_REGION', 'Event 1 Title', 'Event 1 Description', 1, 'Address 1', 'Online', '010-0000-0001', 'APPROVED',
        '2025-05-17 20:52:28', 'Member1', '2025-05-17 20:52:28', 'Member1', FALSE),
       (2, '2025-05-17', 'YEONGNAM_REGION', 'Event 2 Title', 'Event 2 Description', 1, 'Address 2', 'Offline', '010-0000-0002', 'DENIED',
        '2025-05-17 20:52:28', 'Member2', '2025-05-17 20:52:28', 'Member2', FALSE),
       (3, '2025-05-17', 'HONAM_REGION', 'Event 3 Title','Event 3 Description', 2, 'Address 3', 'Online', '010-0000-0003', 'PENDING',
        '2025-05-17 20:52:28', 'Member3', '2025-05-17 20:52:28', 'Member3', FALSE),
       (4, '2025-05-17', 'CHUNGCHEONG_REGION', 'Event 4 Title','Event 4 Description', 2, 'Address 4', 'Offline', '010-0000-0004', 'APPROVED',
        '2025-05-17 20:52:28', 'Member4', '2025-05-17 20:52:28', 'Member4', FALSE),
       (5, '2025-05-17', 'GANGWON_REGION', 'Event 5 Title', 'Event 5 Description', 3, 'Address 5', 'Online', '010-0000-0005', 'DENIED',
        '2025-05-17 20:52:28', 'Member5', '2025-05-17 20:52:28', 'Member5', FALSE),
       (6, '2025-05-17', 'JEJU', 'Event 6 Title','Event 6 Description', 3, 'Address 6', 'Offline', '010-0000-0006', 'PENDING', '2025-05-17 20:52:28',
        'Member6', '2025-05-17 20:52:28', 'Member6', FALSE),
       (7, '2025-05-17', 'CAPITAL_REGION', 'Event 7 Title', 'Event 7 Description', 3, 'Address 7', 'Online', '010-0000-0007', 'APPROVED',
        '2025-05-17 20:52:28', 'Member7', '2025-05-17 20:52:28', 'Member7', FALSE),
       (8, '2025-05-17', 'YEONGNAM_REGION', 'Event 8 Title','Event 8 Description',4, 'Address 8', 'Offline', '010-0000-0008', 'DENIED',
        '2025-05-17 20:52:28', 'Member8', '2025-05-17 20:52:28', 'Member8', FALSE),
       (9, '2025-05-17', 'HONAM_REGION', 'Event 9 Title','Event 9 Description', 4, 'Address 9', 'Online', '010-0000-0009', 'PENDING',
        '2025-05-17 20:52:28', 'Member9', '2025-05-17 20:52:28', 'Member9', FALSE),
       (10, '2025-05-17', 'CHUNGCHEONG_REGION', 'Event 10 Title','Event 10 Description', 4, 'Address 10', 'Offline', '010-0000-0010', 'APPROVED',
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


INSERT INTO matchup_rating (matchup_board_id, evaluator_id, target_member_id, manner_score, skill_score,
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

INSERT INTO introduction (title, content)
VALUES ('MATCHON 소개', 'MATCHON은 실시간 매칭 시스템을 기반으로 한 유저 친화적 스포츠 매칭 플랫폼입니다!'),
       ('MATCHUP', 'MATCHUP은 매치온의 핵심 기능으로, 사용자들은 자신의 취향에 따라 편하고 캐주얼한 경기부터 진지하고 프로페셔널한 경기까지 자유롭게 만들고 참여할 수 있습니다. 원하는 시간과 장소에 직접 경기를 개설하고, 함께할 참가자를 손쉽게 모집할 수 있으며, 그룹 채팅을 통해 참가자들과 실시간으로 소통할 수 있습니다. 또한, 1:1 문의 기능을 통해 개인적인 질문이나 요청도 빠르게 해결할 수 있고, STOMP 기반의 실시간 알림 시스템을 통해 참가 요청에 대한 승인 또는 반려 결과를 즉시 받아볼 수 있어 보다 신속하고 효율적인 매칭 경험을 제공합니다. 이 모든 과정을 통해 사용자들은 자신만의 특별한 매치온 경험을 쌓아갈 수 있습니다.'),
       ('STADIUM', '전국 각지의 구장을 지역별로 한눈에 확인하고, 검색 기능을 통해 간편하게 찾아보세요! 구장 상세 페이지에서는 주소, 전화번호 등 정보를 바로 확인할 수 있으며, 원하는 지역으로도 구장을 손쉽게 찾을 수 있습니다.'),
       ('COMMUNITY', '매치온 커뮤니티는 사용자 간, 사용자와 관리자 간의 원활한 소통을 위한 공간입니다. 자유롭게 정보를 공유하고 대화를 나눌 수 있는 게시판, 관리자는 공지사항을 통해 중요한 소식을 전달, 사용자들은 부적절한 게시글을 신고할 수 있으며, 관리자 확인 후 즉각 조치합니다.'),
       ('SCHEDULE',  '매치온만의 특별 기능! 이벤트 주최자는 관리자 승인 후 이벤트를 등록할 수 있으며, 해당 일정은 캘린더를 통해 사용자에게 공유됩니다. 지역별로 정리되어 있어 내 근처 이벤트도 쉽게 확인할 수 있습니다. 이번 주말엔 가까운 이벤트에 참여해보세요!'),
       ('CS', '매치온은 사용자 의견과 쾌적한 사용 경험을 소중히 생각합니다. 자주 묻는 질문은 FAQ에서 확인할 수 있으며, 궁금한 점이 해결되지 않는 경우 1:1 문의를 통해 언제든지 관리자와 직접 소통하실 수 있습니다.'),
       ('TEAM', '팀 기능을 통해 사용자들은 자신의 선호에 맞는 팀을 선택하거나 직접 팀을 만들어 팀장이 될 수 있습니다. 팀장은 필요 포지션, 활동 지역, 모집 여부 등을 설정하고 팀원 모집과 관리를 쉽게 할 수 있습니다.'),
       ('마이페이지', '마이페이지 기능을 통해 프로필 사진을 등록, 삭제할 수 있고 사용자가 소속된 팀을 조회할 수 있습니다.'),
       ('실시간 채팅&알림', '매치온은 STOMP 기반의 실시간 채팅과 알림 기능을 통해 참가 요청에 대한 승인 또는 반려 결과를 즉시 받아볼 수 있어 보다 효율적인 매칭과 빠르고 유기적인 커뮤니케이션 환경을 제공합니다.'),
       ('매치봇', '매치온의 AI 챗봇 매치봇은 여러분의 매칭 경험을 도와주는 든든한 동반자입니다. 자주 묻는 질문을 빠르게 해결, 해결이 어려운 질문은 1:1 문의로 안내, 간편하고 신속한 사용자 지원을 목표로 합니다!'),
       ('매너온도', '매치온의 핵심 기능인 매너온도는 유저가 받은 피드백을 바탕으로 실력과 스포츠맨십을 보여주는 지표입니다. 경기를 생성할 때 매너온도 기준을 설정하여 경기 분위기를 조성할 수 있으며, 다양한 실력대의 사용자들과 더욱 뜨겁고 열정적이고 즐거운 경기를 경험할 수 있습니다. 매너 온도 산출 방식: (새 매너 온도 = 기존 매너 온도 + (매너점수 X 0.14 + 실력점수 X 0.06 - 0.4) X 0.01)');