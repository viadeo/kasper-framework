
Automated HTTP exposition
====================

During the first iteration on implementing kasper queries, we had to implement by hand each resource exposing a query.

Those implementations had following disadvantages: 

 * varying from team to team, thus being harder to use from a consumer perspective
 * a great portion of code was just boilerplate
 * decreased productivity as all the communication/exchange format had to be reimplemented for each new resource
 * no error handling

Platform teams were spending precious time on doing all that, so to improve productivity and make everyones life easier we implemented all
that exposition layer in kasper framework.

Goals
-----

 * Do all the work of exposing queries & commands by requiring 0 line of code from platform teams
 * Be easy to use on both platform and consumer side
 * Handle errors
 * Uniformize the communication
 * Be extensible in order to allow customization and extension/addition of new features.

How it works
------------
Kasper framework provides an exposition component allowing to automatically expose commands and queries.
Actually it is an HTTP exposition exchanging JSON messages + standard HTTP Headers and implemented via Java Servlets and a mini library
doing the databinding between query strings and query POJOS and vice versa. 

Queries & DTOs
--------------
A query is submitted using à GET request, the parameters will be in the query string not in the body. This was the prefered way because we want to keep queries as simple as possible and we also think that using GET is handy with tools such as curl.


In case of an error a standard HTTP error code will be set with the 

   {
     "code": 404,
     "reason": "Some error message."
   }
::

Commands
--------
