# Project "Demo"
[![Build Status](https://travis-ci.org/UNIZAR-30246-WebEngineering/UrlShortener2015.svg)](https://travis-ci.org/UNIZAR-30246-WebEngineering/UrlShortener2015)


This project is the template for the creation of projects. This project also will contain solutions for blocking issues.

The __Demo__ extends the __Common__ project. The code of the project __Demo__ is in the package `urlshortener2014.demo`. It defines the class `UrlShortenerControllerWithLogs` that extends the `UrlShortenerController` provided by the __Core__ with log support for debugging. 

`Application` and `config` classes configure the __Demo__ application to use `UrlShortenerControllerWithLogs` as controller instead of `UrlShortenerController`.

The application can be run as follows:

```
$ gradle run
```

Gradle will compile project __Common__ and then create a `jar` file of it. Next will compile project __Demo__ and run it. Now you have a shortener service running at port 8080 that logs in the console all the requests that you perform.
