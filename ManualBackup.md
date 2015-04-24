# Introduction #

Add your content here.

# Steps for Backup #

Steps
  * backup MySQL DB
  * Headings, paragraphs, and lists
  * Automatic links to other wiki pages

Backup MySQL DB
```
.\mysqldump.exe -u *username* -h *hostIP* --database *dbname* -p > *filename.sql*
```
Notes:
  * ucceeded when using mysqldump.exe from MySQL Server 5.1, the one packaged with MySQL workbench did not work
  * Convert the .sql file to UTF8 if you are on windows. use notepad

# Installation #

  * Install Java
  * Install Apache Tomcat
  * Install MySQL
  * Setup DB
  * Setup FileSystem
  * Setup Tomcat

## Install Java ##

Installing Java is dependent on the Operating System, simply follow instructions from http://www.java.com/en/download/index.jsp

Install JRE or JDK
  * If installed JRE, make sure
    * JRE\_HOME - environmental variable points to the folder JRE is installed
  * If installed JDK, make sure
    * JAVA\_HOME - environmental variable points to the folder JDK is installed

# Steps for Restoration #

## Install MySQL ##
- Different instruction on windows and linux
- linux - http://www.cyberciti.biz/tips/how-do-i-enable-remote-access-to-mysql-database-server.html


Run MySQL
```
on windows run 
mysqld --lower_case_table_names=2

on linux run 
/etc/init.d/mysql start --lower_case_table_names=0
```

Allow Remote connection on MySQL
```
GRANT ALL ON DBNAME.* TO USERNAME@'IP' IDENTIFIED BY 'PASSWORD'
```
DBNAME is the db you grant access to
  * can set to **?
USERNAME is the user name, usually the highest user is 'root'
IP is the IP of the incoming connector, could be set to '%' for all IP
PASSWORD is the password for USERNAME@IP**


Add MySQL Server on startup
  * in Windows, add mySQL as a startup service.  You can add arguments, or edit the config file to modify startup option
  * in linux, add MySQL to the startup script...

## Install Tomcat ##
  * http://www.mkyong.com/tomcat/how-to-install-tomcat-in-ubuntu/
  * on linux startup run /.../apache-tomcat-6.0.16/bin$ sh startup.sh

Restoring MySQL DB
  * use mySQL console and type in
```
source *filename.sql*;
```
  * or run mysql.exe by this...
```
if on windows
./mysql.exe -u *username* -p < *filename.sql
```

NOTE: Make sure, if you're on windows, convert .sql file to UTF8 format. You can use notepad to save it as UTF8

Add your content here.  Format your content with:
  * Text in **bold** or _italic_
  * Headings, paragraphs, and lists
  * Automatic links to other wiki pages