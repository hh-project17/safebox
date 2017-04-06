[![Build Status](https://travis-ci.org/hh-project17/safebox.svg?branch=master)](https://travis-ci.org/hh-project17/safebox)
[![codecov](https://codecov.io/gh/hh-project17/safebox/branch/master/graph/badge.svg)](https://codecov.io/gh/hh-project17/safebox)

# SafeBox
Веб-сервис для запуска кода (Java и Python) в песочнице

Для работы понадобится docker. Для его установки (и создания образа с нужными компиляторами/интерпритаторами) просто запустите _./setup_docker.sh_

Запустить сервис можно так:

Перейти в корень проекта и выполнить mvn spring-boot:run 

или собрать проект с помощью mvn clean install, а дальше java -jar target/safebox-1.0.jar

или через idea запустив метод main в классе Main

Если docker начал ругаться при запуске, то скорее всего нужно выполнить команду sudo chmod 777 /var/run/docker.sock или запустить веб-сервис с правами супер пользователя

Веб-сервис принимает Http POST запросы по урлу /compile с параметрами

* compilerType - int параметр:  java(0), python2(1), python3(2)
* code - String параметр: код программы
* userInput - String параметр (optional): пользовательский ввод
* ram - ограничение по памяти
* timeout - ограничение по времени

Можно конфигурировать некоторые параметры в файле application.properties

* server.port - по умолчанию стоит 9000
* imageName - имя образа, который загрузится в docker контейнер. По умолчанию, sandbox
* defaultTimeout - сколько времени будем ждать исполнения программы, по умолчанию 60_000ms
* defaultRam - ограничение по памяти, по умолчанию стоит 256 мб



Также для работы с докером можно использовать следующие команды (выручали):

\#delete all containers
docker rm $(docker ps -q -f status=exited)

\#stop all containers
docker stop $(docker ps -q -f status=running)

\#build image
docker build -t sandbox src/main/resources/docker

Для тестирования докера можно пользоваться curl -ом, например:

curl -X POST --form code="import java.util.Scanner; class sdf{public static void main(String[] args) {Scanner r = new Scanner(System.in);System.out.println(r.nextInt());}}" --form compilerType=0 --form userInput=10  localhost:9000/compile
