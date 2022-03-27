#!/usr/bin/bash

# prepare JARs

echo `git pull`

cd userprofile
echo "##### current working directory "`pwd`
echo "#### Running mvn build--"
echo `./mvnw clean install -DskipTests`
cp ./target/User-Profile-MS.jar /home/ec2-user/runtime/app1.jar

cd ..

cd authentication-service
echo "##### current working directory "`pwd`
echo "#### Running mvn build--"
echo `./mvnw clean install -DskipTests`
cp ./target/Auth-MS.jar /home/ec2-user/runtime/app2.jar

cd ..

cd tweet
echo "##### current working directory "`pwd`
echo "#### Running mvn build--"
echo `./mvnw clean install -DskipTests`
cp ./target/Tweet-MS.jar /home/ec2-user/runtime/app3.jar


# Return to HOME -- /home/ec2-user
cd

echo "##### current working directory "`pwd`

cd ./runtime


export BASE_LOG_DIRECTORY=/home/ec2-user/logs/
export DATABASE_URI=mongodb://documentDB:documentDB@docdb-2022-03-25-18-10-17.cluster-cqgt35jhle7c.us-east-2.docdb.amazonaws.com:27017/?ssl=true&ssl_ca_certs=rds-combined-ca-bundle.pem&replicaSet=rs0&readPreference=secondaryPreferred&retryWrites=false

# java -jar ./app1.jar &
# java -jar ./app2.jar &
# java -jar ./app3.jar &

java -jar ./app1.jar
