#!/bin/bash

# shellcheck disable=SC2164
pushd "${HOME}"/Imperative/src
#touch "testing$(date +%H%M%S)"
popd

#crontab is 0 * * * * /bin/sh ${HOME}/Imperative/src/cron_script.sh for hourly updates