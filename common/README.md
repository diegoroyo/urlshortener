# Project "Common"

The __Common__ is a [Spring Boot](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/) application that offers a minimum set of functionalities shared by all subprojects.

* __Short URL creation service__:  `POST /` creates a shortened URL from a URL in the request parameter `url`.
* __Redirection service__: `GET /{id}` redirects the request to a URL associated with the parameter `id`.
* __Database service__: Persistence and retrieval of `ShortURL` and `Click` objects.

