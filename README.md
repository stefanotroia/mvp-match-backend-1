## MVP MATCH #1 - Selling Machine

## Project Dependencies

- Micronaut 3.5.2 
- Micronaut Test / Junit 5
- Micronaut Security
- Jetbrains Exposed
- H2 Database (test only)

---
## OpenApi 3.0 Doc

At this [link](./openapi/mvp-match.yml) openapi documentation is located

---
## Authentication

To Implement Authentication and Authorization mechanism, Micronaut security has been used.
The Authentication is performed with a Bearer token, obtained calling  POST /mvp-match/login API.

The custom authentication provider AuthenticationProviderUserPassword, 
will look for an identity with matching username and password, in case of success a JWT token
will be issued with duration of 60 minutes. (jwt secret is located in [yml file](./src/main/resources/application.yml))

### Authentication Bonus
When an authentication is performed, the tuple (user_id, jit) will be saved in a database table,
describing the active session (a cache could be used to improve performances).
If another login for the same user is attempted, the error "session_conflict" will be thrown, since
only one session per user is allowed

If an user wants to log in, on a different device, will have to call logout API, which will
revoke all active tokens for the user.


## Selling machine

To implement a near life machine operations, the application will keep track of coins inserted in the vending machine.
The accepted coins are: [5,10,20,50,100], for each of them it will be saved a row in selling_machine table, keeping track 
of taken coins.
Every time a user performs a /deposit operation, both its deposit, and selling_machine rows will be updated.
Every time a user performs a /reset operation, both its deposit (to 0), and selling_machine rows will be updated.

A buy operation can be performed only if the selling machine has enough coins for the change, otherwise an error will be thrown.
Each time a user completes a purchase, its deposit will be set to 0, and he will receive the change.
