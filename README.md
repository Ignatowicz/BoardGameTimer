# BoardGameTimer

1. Database:
```
$ sudo mysql --password

create database bgt;
create user 'admin'@'localhost' identified by 'Admin123#';
grant all privileges on bgt.* to 'admin'@'localhost';

select user,host from mysql.user;
show grants for 'admin'@'localhost';
```

2. Tasks:
- BGT-000
	- project base
- BGT-001
	- .gitignore
- BND-000
	- backend base
- BND-001
	- backend logic
- BND-002
	- db connection
- BND-003
	- backend api
- BND-004
	- backend notification

3. Notification id:
- 1 - start the game
- 2 - accept the game
- 3 - start the turn
- 4 - reject the game
- 5 - pause the game
- 6 - resume the game
- 7 - end the game

