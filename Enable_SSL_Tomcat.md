#wiki page for enabling ssl on tomcat on eclipse

# Introduction #

sup


# Details #

Add your content here.  Format your content with:
  * Find server.xml
  * create self signed certificate
  * add location of self signed certificate to a "connector" in server.xml
    * find the Java installation direction, go into the ./bin folder
    * ./keytool.exe -genkey - alias tomcat - keyalg RSA - storepass **changeit** -keystore **path** -keypass **changeit**
    * First and Last name: localhost
    * anything else can be random
  * add this line to the connector
```
    <Connector port="8443" protocol="HTTP/1.1" SSLEnabled="true"
               maxThreads="150" scheme="https" secure="true"
               keystoreFile="*key file path*"
               clientAuth="false" sslProtocol="TLS" />
```
Links
  * [full on tutorial](http://www.coderanch.com/t/87738/Tomcat/Self-signed-certificate-tomcat)
  * [stuff](http://www.cb1inc.com/2007/05/12/creating-self-signed-certs-on-apache-tomcat-5-5/)
  * [more stuff](http://www.liferay.com/web/guest/community/wiki/-/wiki/Main/How%20to%20configure%20https%20feature)
  * [and stuff](http://www.codemarvels.com/2010/11/enabling-ssl-on-tomcat-on-a-devl-machine-windows/)
  * [Where to find server.xml](http://stackoverflow.com/questions/2465041/run-jsp-in-eclipse-on-specific-port-and-ssl)
  * [How to create self signing certificate](http://www.akadia.com/services/ssh_test_certificate.html)