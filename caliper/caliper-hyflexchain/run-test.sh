#! /bin/bash

id=$1
results_folder=$2

mkdir -p $results_folder

# This is the file I whould change benchmarks configs to test locally
# CALIPER_BENCHCONFIG=config/benchmark_private.yaml # to activate on private transactions

docker run --rm -d --network host \
    -v "$(pwd)/config:/hyperledger/caliper/workspace/config" \
    -v "$(pwd)/crypto:/hyperledger/caliper/workspace/crypto" \
    -v "$(pwd)/$results_folder:/hyperledger/caliper/workspace/results" \
    -v /var/run/docker.sock:/var/run/docker.sock \
    -e CALIPER_BENCHCONFIG=config/benchmark_private.yaml \
    --name caliper henriquej0904/hyflexchain:caliper /bin/sh -c "./launch-master.sh && cp report.html results/report-$id.html"

docker wait caliper
