create table stadium (
	stadium_id bigint primary key,
	stadium_region varchar(100),
	stadium_nmae varchar(100),
	stadium_address varchar(255),
	stadium_image_attchment_enabled boolean default false,
	create_date datetime,
	create_person varchar(50),
	modified_date datetime,
	modified_person varchar(50),
	is_deleted boolean default false 
);

select * from stadium;