create table test_user(user_id varchar2(42) primary key, user_name varchar2(254));
create table test_user_list_urls(
		user_id VARCHAR2(40) NOT NULL,
		url_value VARCHAR2(254) NOT NULL,
		SEQ_NUM NUMBER NOT NULL,
		constraint test_user_list_urls_pk primary key(user_ID, SEQ_NUM)
);
create table test_user_map_urls(
		user_ID VARCHAR2(40) NOT NULL,
		url_key VARCHAR2(254) NOT NULL,
		url_VALUE VARCHAR2(254),
		constraint test_user_map_urls_PK primary key (user_ID,url_KEY)
	);
  
  
  commit