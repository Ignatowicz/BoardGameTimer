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
- BGT-002
	- project logic
- BGT-003
	- fix
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
- BND-005
	- fix
- FND-001
	- login
- FND-002
	- logic
- FND-003
	- fix

3. Notification id:
- 1 - invitation to the game
- 2 - accept the game
- 3 - reject the game
- 4 - start your turn
- 5 - other's player turn
- 6 - end the game

4. Heroku
- install heroku client: 
```
$ sudo snap install --classic heroku
```
- login to heroku
```
$ heroku login
```
- see actual logs
```
$ heroku logs -a secret-falls-72080
```
- add remote repository
```
$ git remote add heroku https://git.heroku.com/secret-falls-72080.git
```
- deploy changes:
```
$ git subtree push --prefix Backend/ heroku master
```
- deploy with force
```
$ git push heroku `git subtree split --prefix Backend/ master`:master -f
```

Documentation:
https://documenter.getpostman.com/view/9729849/SWE6Zczj?version=latest
