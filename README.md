[![Build Status](https://travis-ci.org/hh-project17/safebox.svg?branch=master)](https://travis-ci.org/hh-project17/safebox)
[![codecov](https://codecov.io/gh/hh-project17/safebox/branch/master/graph/badge.svg)](https://codecov.io/gh/hh-project17/safebox)

# SafeBox
Веб-сервис для запуска кода (Java и Python) в песочнице

Для работы понадобится установить docker.io (sudo apt-get install docker.io)

Запустить сервис можно так:

* Перейти в корень модуля app и выполнить mvn spring-boot:run 

* или собрать модуль app с помощью mvn clean install, а дальше java -jar target/safebox-1.0.jar

* или через idea запустив метод main в классе Main

* или собрав и установив deb-пакет (об этом ниже) 

Веб-сервис принимает Http POST запросы по урлу /compile с параметрами

* compilerType - int параметр:  java(0), python2(1), python3(2)
* code - String параметр: код программы
* userInput - String параметр (optional): пользовательский ввод
* ram - ограничение по памяти, mb
* timeout - ограничение по времени, ms

Можно конфигурировать некоторые параметры в файле application.properties

* server.port - по умолчанию стоит 9000
* imageName - имя образа, который загрузится в docker контейнер. По умолчанию, vorobey92/sandbox
* defaultTimeout - сколько времени будем ждать исполнения программы, по умолчанию 60_000ms
* defaultRam - ограничение по памяти, по умолчанию стоит 256 мб


#Сборка deb-пакета:
1. Переходим в корень проекта
2. mvn clean package

Для установки пакета необходимо выполнить sudo ./install.sh

После установки деб-пакета сервисом можно управлять следующим образом:
1. /etc/init.d/safebox start|stop|status|...
2. service safebox start|stop|status|...

Сам executable jar лежит в /var/safebox/

Логи пишутся в /var/log/safebox.log



#Полезности:

Если docker начал ругаться при запуске, то скорее всего нужно выполнить команду sudo chmod 777 /var/run/docker.sock или запустить веб-сервис с правами супер пользователя

Также для работы с докером можно использовать следующие команды (выручали):

* delete all containers

docker rm $(docker ps -q -f status=exited)

* stop all containers

docker stop $(docker ps -q -f status=running)

* build image

docker build -t sandbox src/main/resources/docker


Для тестирования сервиса можно пользоваться curl-ом, например:

curl -X POST --form code="import java.util.Scanner; class sdf{public static void main(String[] args) {Scanner r = new Scanner(System.in);System.out.println(r.nextInt());}}" --form compilerType=0 --form userInput=10  localhost:9000/compile
