# --- !Ups

insert into profile (id, username, password, email) values (1001, 'koevet', 'slimshady', 'aaa@hotmail.com');

# --- !Downs
delete from profile;
