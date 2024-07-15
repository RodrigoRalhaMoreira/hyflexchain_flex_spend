#! /bin/bash

mvn clean compile assembly:single
docker build -f Dockerfile_tc -t rrmoreira57943/flexspend:hyflexchain-tc .
