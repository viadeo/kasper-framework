
Using security
========================

You can create a Kasper platform with a specific security configuration. There is two strategies. One for Public resources
calls identified by the @XKasperPublic annotation, and one for all calls. This security configuration contains different
callbacks interfaces allowing you to check some needs.

Those needs are :

* SecurityTokenValidator to check the security token (not called for Public strategy)
* IdentityContextProvider to set the user information in the context
* ApplicationIdValidator to check the applicationId
* IpAddressValidator to check the ip address

These callbacks are called before query and command requests.
