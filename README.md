# bumbleB
bumbleB is the code name for vRealize Business Management Servicification initiative
The major goal is to servicify the existing monolithic application to smaller micro services

#vrbc
This repository holds all micro-services that are been developed. 
However the intention to re-use itfm-cloud repository for final artifact delivery to ESO team & Client 


# Intended project structure

Each functional component will have its own directory under which two projects will be created
1. contracts
2. services

# Contracts
Contracts holds all the model and service contract (Interface) using JaxRs Annotations. 
This will be used to render swagger UI.
This project should not depend on any other services project

# services
Services holds all the micro-services and it depends on the corresponding contracts project.
It can also depend on other contracts project but not on any other services project.

# Code Style
We will use google code style formatting to begin with and we will do necessary modifications if needed.
Config directory holds the code style formatting for both intellij and eclipse