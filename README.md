# sample-jpa
Tomcat + Jersey + Hk2 + Hibernate + Jpa

##Create a war file

> mvn war:war

##Execute an Integration Tests with maven

> mvn clean verify

##Debug an Integration Tests with eclipse

> mvn -Dmaven.failsafe.debug="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000 -Xnoagent -Djava.compiler=NONE" verify

Attach to the port 8000.

##Execute project with Cargo

NOTE: cargo:run => org.codehaus.cargo:cargo-maven2-plugin:run

> mvn cargo:run

> mvn -Dcargo.start.jvmargs="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000 -Xnoagent -Djava.compiler=NONE" cargo:run

