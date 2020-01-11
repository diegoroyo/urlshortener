/*
 *******************************************
 *** Urlshortener - Web Engineering ********
 *** Authors: Name  ************************
 *** Andrew Mackay - 737069 ****************
 *** Ruben Rodríguez Esteban - 737215 ******
 *** Diego Royo Meneses - 740388 ***********
 *** Course: 2019 - 2020 *******************
 *******************************************
 */ 


/**
 * Handler which is used to throw the different exceptions
 */

package urlshortener.exception

import org.springframework.http.HttpStatus
import java.lang.RuntimeException
import org.springframework.web.bind.annotation.ResponseStatus


/**
 * Throws bad request error 
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
class BadRequestError(message: String) : RuntimeException(message)


/**
 * Throws not found error
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundError(message: String) : RuntimeException(message)


/**
 * Throws conflict error
 */
@ResponseStatus(HttpStatus.CONFLICT)
class ConflictError(message: String) : RuntimeException(message)


/**
 * Throws internal server error
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
class InternalServerError(message: String) : RuntimeException(message)


/**
 * Throws service unavailable error
 */
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
class ServiceUnavailableError(message: String) : RuntimeException(message)
