#!/bin/bash

# Define variables
PROJECT_BASE="${HOME}/Imperative"
PROJECT_DIR="${PROJECT_BASE}/src"
SERVER_LOG="${PROJECT_BASE}/server.log"


# Function to kill the running server
kill_server() {
  # Find the process ID of the server
  SERVER_PID=$(pgrep -f "exec:java@server")
  if [ -n "$SERVER_PID" ]; then
    echo "Killing server with PID: $SERVER_PID"
    kill -9 "$SERVER_PID"
  else
    echo "No running server found"
  fi
}

# Change to the project directory
pushd "$PROJECT_DIR" || exit

# Kill the currently running server
kill_server

# Pull the latest code from the master branch
git fetch
git pull origin master

# Build the project using Maven
./mvnw clean compile

# Start the updated server
./mvnw exec:java@server

# Return to the previous directory
popd || exit

#crontab is 0 * * * * /bin/sh ${HOME}/Imperative/src/cron_script.sh for hourly updates