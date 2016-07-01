drop table ATGTR_CAT_SHOW;
drop table ATGTR_FELINE_CAT;
drop table ATGTR_FELINE;
drop table ATGTR_SHOW;




create table ATGTR_FELINE(
		FELINE_ID VARCHAR2(40) NOT NULL,
		FELINE_NAME VARCHAR2(254),
		constraint ATGTR_FELINE_PK primary key(FELINE_ID)
		);


create table ATGTR_FELINE_CAT(
		FELINE_ID VARCHAR2(40) NOT NULL,
		CAT_ID VARCHAR2(40) NOT NULL,
		constraint ATGTR_FELINE_CAT_PK primary key(FELINE_ID, CAT_ID)
	);


create table ATGTR_SHOW(
		SHOW_ID VARCHAR2(40) NOT NULL,
		SHOW_NAME VARCHAR2(254),
		SHOW_DATE DATE,
		constraint ATGTR_SHOW_PK primary key(SHOW_ID)
	);


create table ATGTR_CAT_SHOW(
		SHOW_ID VARCHAR2(40) NOT NULL,
		CAT_ID VARCHAR2(40) NOT NULL,
		SEQ_NUM NUMBER NOT NULL,
		constraint ATGTR_CAT_SHOW_PK primary key(CAT_ID, SHOW_ID, SEQ_NUM)
);