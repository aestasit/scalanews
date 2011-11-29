# --- First database schema

# --- !Ups

create table profile (
  id                    bigint not null,
  username              varchar(25) not null,
  password              varchar(18) not null,
  created               timestamp,
  email                 varchar(50),
  constraint pk_profile primary key (username)
);

create table story (
  id                    bigint not null,
  title                 varchar(25) not null,
  story                 clob,
  profileId             bigint not null,
  created               timestamp,
  votes                 int default 0,
  rank                  double,
  constraint pk_story primary key (id));

create table comments (
  id                    bigint not null,
  commentclob           clob,
  profileId             bigint not null,
  storyId               bigint not null,
  parentComment         bigint,
  created               timestamp,
  votes                 int default 0,
  constraint pk_comments primary key(id));

create table votes (
  id                    bigint not null,
  storyId               bigint not null,
  commentId             bigint,
  created               timestamp,
  profileId             bigint not null,
  constraint pk_votes primary key(id)
);
  
alter table story add constraint fk_story_profile_1 foreign key (profileId) references profile (id) on delete restrict on update restrict;
alter table comments add constraint fk_comments_profile_1 foreign key (profileId) references profile (id) on delete restrict on update restrict;
alter table comments add constraint fk_comments_parent_2 foreign key (parentComment) references comments (id) on delete restrict on update restrict;
alter table comments add constraint fk_comments_story_3 foreign key (storyId) references story (id) on delete restrict on update restrict;
alter table votes add constraint fk_votes_profile_1 foreign key (profileId) references profile (id) on delete restrict on update restrict;
alter table votes add constraint fk_votes_comment_2 foreign key (commentId) references comments (id) on delete restrict on update restrict;
alter table votes add constraint fk_votes_story_3 foreign key (storyId) references story (id) on delete restrict on update restrict;
 
create sequence news_seq start with 1000; 
create sequence user_seq start with 1000;

# --- !Downs
drop table if exists profile;
drop table if exists story;
drop table if exists comments;
drop table if exists votes;
