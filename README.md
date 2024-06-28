# SPE summer resit project template

## Group Details

Group Name: Imperative  
Deployment lab machine: it075752.wks.bris.ac.uk  
Group Team Channel: [Imperative](https://teams.microsoft.com/l/channel/19%3A56e1f7d7cc57472ab35f61bedb1a0a8c%40thread.tacv2/Team%20Imperative?groupId=929cf74e-332b-4f32-9b05-35403b3bb092&tenantId=b2e47f30-cd7d-4a4e-a5da-b18cf1a4151b&ngc=true)

## Overview

This template is the skeleton project that acts as the starting point for the SPE summer resit assignment.  
**Note:** that on MS Windows, you might need to replace `./mvnw` with `.\mvnw` in the following commands.

## Building the project

You can compile all of the source code in the project (which is located in `src/main/java`) using the following command:
```
./mvnw clean compile
```

## Running test cases

Run all of the test scripts (which are located in `src/test/java`) using the following command:
```
./mvnw test
```

## Manually running the Server and Client

You can start the server using the following command:
```
./mvnw exec:java@server
```

In a separate terminal, run the client using the following command:
```
./mvnw exec:java@client -Dexec.args="username"
```
Where `username` is the the name of the player
