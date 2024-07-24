#!/bin/bash

# Define variables
PROJECT_BASE="${HOME}/Imperative"
SERVER_START_CMD="./mvnw exec:java@server" # Command to start your server
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
pushd "$PROJECT_BASE" || exit

# Kill the currently running server
kill_server

# Pull the latest code from the master branch
git fetch
git pull

# Build the project using Maven
./mvnw clean compile

#Start the updated server and log output to server.log
$SERVER_START_CMD > "$SERVER_LOG" 2>&1 &

# Return to the previous directory
popd || exit

#CRONTAB INSTRUCTIONS
# on the lab machine, in a terminal use crontab -e
# this will open in vim where you need to enter this line
# 0 * * * * /bin/sh ${HOME}/Imperative/src/cron_script.sh
# this will update every hour calling the cron script above