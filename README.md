# User Management Service 

This service provides functionality for creating, updating and retrieving system users and their details. 
When creating a new user, the user's credentials are generated using Keycloak (Open Source Identity and Access Management) 
by calling a seperate keycloak-manager service. The user is also created in the access-control-manager where the user can be 
assigned roles. 

A user can have a number of user detail records but only one will be active at any time. 
This structure supports the requirements to remove someone's access to the system because they no longer need access, 
but to allow them to rejoin at a later date without having to issue a new username.

## Prerequisites 

* Java 17 
* Maven


