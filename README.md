[![Build Status](https://travis-ci.org/hh-project17/safebox.svg?branch=master)](https://travis-ci.org/hh-project17/safebox)
[![codecov](https://codecov.io/gh/hh-project17/safebox/branch/master/graph/badge.svg)](https://codecov.io/gh/hh-project17/safebox)

# SafeBox
Веб-сервис для запуска кода (Java и Python) в песочнице

Для работы понадобится docker. Для его установки (и создания образа с нужными компиляторами/интерпритаторами) просто запустите _./setup_docker.sh_

Веб-сервис принимает Http POST запросы по урлу /compile с параметрами

* compilerType - int параметр:  java(0), python2(1), python3(2)
* code - String параметр: код программы
* userInput - String параметр (optional): пользовательский ввод

Можно конфигурировать некоторые параметры в файле application.properties

* server.port - по умолчанию стоит 9000
* imageName - имя образа, который загрузится в docker контейнер. По умолчанию, sandbox
* timeout - сколько времени будем ждать исполнения программы, по умолчанию 100_000ms
