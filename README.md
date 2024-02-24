### The OAuth 2.0 Authorization Framework
`Single sign-on (SSO)`: The idea of SSO is to allow users to connect to many, different services by proving that they own the account of a single service.     

Today, two protocols are the main competitors when it comes to setting up SSO:     
- `Security Assertion Markup Language 2.0 (SAML)`(legacy protocol)—A protocol using the Extensible Markup Language (XML) encoding.     
- `OpenID Connect (OIDC)`—An extension to the OAuth 2.0 (RFC 6749) authorization protocol using the JavaScript Object Notation (JSON) encoding.    

The OAuth 2.0 enables a third-party application to obtain limited access to an HTTP service(**OAuth 2.0 is designed for use with HTTP**, and heavily uses HTTP redirects),     
either on behalf of a resource owner,     
or by allowing the third-party application to obtain access on its own behalf.

OAuth Roles:
- resource owner
    - An entity capable of granting access to a protected resource.
       When the resource owner is a person, it is referred to as an `end-user`.    
    - For example: any person with Google account.
- client
    - An application that requests access to resources controlled by the resource owner.    
    - For example: any app with Google sign-in.    
- authorization server
    - The server issuing access tokens to the client after successfully authenticating the resource owner and obtaining authorization.    
    - For example: Google oauth service
- resource server
    - The server hosting the protected resources, capable of accepting and responding to protected resource requests using access tokens.    
    - For example: Google Drive service

### References
- [OAuth 2.0 RFC](https://datatracker.ietf.org/doc/html/rfc6749#section-4.1)