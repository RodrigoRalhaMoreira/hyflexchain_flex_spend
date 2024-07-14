#! /bin/bash

# docker build -t rrmoreira57943/flexspend:caliper .

docker run --rm -it --network host \
    -v "$(pwd)/config:/hyperledger/caliper/workspace/config" \
    -v "$(pwd)/crypto:/hyperledger/caliper/workspace/crypto" \
    -v /var/run/docker.sock:/var/run/docker.sock \
    --name caliper-$1 rrmoreira57943/flexspend:caliper  # caliper launch manager --caliper-bind-file sut.yaml
