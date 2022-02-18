#!/bin/bash


USERNAME=mbanik

# can add any number of 120 machines to scale your solution
NODE_HOSTS="montpelier.cs.colostate.edu trenton.cs.colostate.edu saint-paul.cs.colostate.edu boston.cs.colostate.edu jackson.cs.colostate.edu helena.cs.colostate.edu"
# the port and ip of the registry
# Note: this assumes that the registry is already running before the script is run. 
REGISTRY_HOST="phoenix.cs.colostate.edu"
PORT=9000

NUM_MSGS=10000
JAR_PATH="~/cs455/demo/build/libs/demo-final.jar"

# the following command runs the nodes that connect to the registry and send messages
# java -cp ${JAR_PATH} cs455.overlay.Main node ${HOSTNAME} ${REGISTRY_HOST} ${PORT} ${NUM_MSGS} &

for HOSTNAME in ${NODE_HOSTS} ; do
    ssh -l ${USERNAME} ${HOSTNAME} java -cp ${JAR_PATH} cs455.overlay.Main node ${HOSTNAME} ${REGISTRY_HOST} ${PORT} ${NUM_MSGS} &
done


