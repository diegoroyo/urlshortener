# Web Engineering 2019-2020 / URL Shortener

[![Build Status](https://travis-ci.org/UNIZAR-30246-WebEngineering/UrlShortener.svg?branch=master)](https://travis-ci.org/diegoroyo/urlShortener)
[![made-with-Kotlin](https://img.shields.io/badge/Made%20with-java-e01250.svg)](https://kotlinlang.org/)
[![built-with-gradle](https://img.shields.io/badge/Built%20with-gradle-1a3ef3.svg)](https://gradle.org/install/)

<p align="center">
    <img src="https://i.ibb.co/0QykvXt/217631.png" alt="Logo" width=200 height=200>
  </a>

  <h3 align="center">RioVolga</h3>

  <p align="center">
    <b>A customized url shortener</b> <br>
  </p>
</p>

# 1 - Description 

This repository is a gradle projet of a url shortener implemented in kotlin language. The url shortener has the following 
functionalities to the users:

* The application optionally allows to the users to generate a QR code that redirects to the desired original URI.

* The application allows the generation of shortened URIs customized by the users with a specified format.

* The application verifies that the shortened URI is not on a spam list, so that the service is more secure for users.

* The application has a protection mechanism against DoS attacks, which limits the number of requests.

* The application can display information about the status of the URIs stored in the system (QR, Safe Browsing, number of clicks
among others).

&nbsp;

# 2 - Compilation and execution

The application can be compiled as follows:

```
$ gradle build
```


The application can be run as follows:

```
$ gradle bootRun
```

After doing the last commands there will be a shortener service running at port 8080.

&nbsp;


# 3 - Tests and continuous integration

In order to test the behaviour of the url shortener, there are tests that verify the performance of the application. These
test are checked by Travis CI automatically and check the different functionalities of the application in all contexts and
possible situations (cases of error and correct responses).

&nbsp;

# 4 - Authors

The project has been programmed by the following developers:

* **diegoroyo** - [diegoroyo](https://github.com/diegoroyo)
* **Andrewkm210**  - [Andrewkm210](https://github.com/Andrewkm210)
* **ZgzInfinity** - [ZgzInfinity](https://github.com/ZgzInfinity)


