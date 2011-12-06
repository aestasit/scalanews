# --- !Ups

insert into profile (id, username, password, email) values (1001, 'luciano', 'ecb4599131b1a179017931d0f58d43a05b02d570', 'aaa@hotmail.com');
insert into profile (id, username, password, email) values (1002, 'aestas', 'ecb4599131b1a179017931d0f58d43a05b02d570', 'aestasit@hotmail.com');

insert into story (id, title, story, profileId, created, votes, rank) values (2001, 'The Scala Programming language', 'http://www.scala-lang.org', 1001,'2011-12-02 14:55:21', 1, 0);
insert into story (id, title, story, profileId, created, votes, rank) values (2002, 'Twitter Scala School', 'http://twitter.github.com/scala_school/', 1002,'2011-12-02 15:55:21', 1, 0);

insert into `comments`(id,comment,profileId,storyId,parentComment) values(3001,'Nice!',1002,2001,NULL);
insert into `comments`(id,comment,profileId,storyId,parentComment) values(3002,'Thanks!',1001,2001,NULL);

# --- !Downs
delete from story;
delete from profile;


