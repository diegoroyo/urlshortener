# Project "Demo"

This project is the template for the creation of projects. This project also will contain solutions for blocking issues.

The __Demo__ extends the __Common__ project. The code of the project __Demo__ is in the package `urlshortener2014.demo`. It defines the class `UrlShortenerControllerWithLogs` that extends the `UrlShortenerController` provided by the __Core__ with log support for debugging. 

`Application` and `config` classes configure the __Demo__ application to use `UrlShortenerControllerWithLogs` as controller instead of `UrlShortenerController`.

The application can be run as follows:

```
$ gradle bootrun
```

Gradle will compile project __Common__ and then create a `jar` file of it. Next will compile project __Demo__ and run it. Now you have a shortener service running at port 8080 that logs in the console all the requests that you perform.
Now you have a shortener service running at port 8080. You can test that it works as follows:

```
$ curl -v -d "url=http://www.unizar.es/" -X POST http://localhost:8080/link
> POST / HTTP/1.1
> User-Agent: curl/7.37.1
> Host: localhost:8080
> Accept: */*
> Content-Length: 25
> Content-Type: application/x-www-form-urlencoded
>
* upload completely sent off: 25 out of 25 bytes
< HTTP/1.1 201 Created
< Server: Apache-Coyote/1.1
< Location: http://localhost:8080/l6bb9db44
< Content-Type: application/json;charset=UTF-8
< Transfer-Encoding: chunked
< Date: Tue, 11 Nov 2014 17:00:29 GMT
<
* Connection #0 to host localhost left intact
{"hash":"6bb9db44","target":"http://www.unizar.es/","uri":"http://localhost:8080/l6bb9db44","created":"2014-11-11","owner":null,"mode":307}
```

And now, we can navigate to the shortened URL.

```
$ curl -v http://localhost:8080/l6bb9db44
> GET /6bb9db44 HTTP/1.1
> User-Agent: curl/7.37.1
> Host: localhost:8080
> Accept: */*
>
< HTTP/1.1 307 Temporary Redirect
< Server: Apache-Coyote/1.1
< Location: http://www.unizar.es/
< Content-Length: 0
< Date: Tue, 11 Nov 2014 17:02:26 GMT
<
```
