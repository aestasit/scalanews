# --- !Ups

insert into profile (id, username, password, email) values (1001, 'luciano', 'ecb4599131b1a179017931d0f58d43a05b02d570', 'aaa@hotmail.com');

# --- !Downs
delete from profile;
