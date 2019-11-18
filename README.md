# BoardGameTimer

1. Database:
- create database bgt;
- create user 'admin'@'localhost' identified by 'Admin123#';
- grant all privileges on bgt.* to 'admin'@'localhost';

- select user,host from mysql.user;
- show grants for 'admin'@'localhost';

2. Tasks:
a) BGT-000
	- project base
b) BGT-001
	- .gitignore
c) BND-000
	- backend base
d) BND-001
	- backend logic
e) BND-002
	- db connection
f) BND-003
	- backend api
