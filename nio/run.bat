@echo off
echo compiling "server\Test.java"
javac "server\Test.java"
echo compiling "client\Test.java"
javac "client\Test.java"
echo compiling "user\Test.java"
javac "user\Test.java"

start java "server.Test"
start java "client.Test"
start java "user.Test"