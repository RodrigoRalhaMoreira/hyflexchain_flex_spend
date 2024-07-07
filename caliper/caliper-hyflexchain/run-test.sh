#! /bin/bash

id=$1
results_folder=$2

mkdir -p $results_folder

# This is the file I whould change benchmarks configs to test locally
# CALIPER_BENCHCONFIG=config/benchmark_private.yaml # to activate on private transactions

docker run --rm --network host \
    -v "$(pwd)/config:/hyperledger/caliper/workspace/config" \
    -v "$(pwd)/crypto:/hyperledger/caliper/workspace/crypto" \
    -v "$(pwd)/$results_folder:/hyperledger/caliper/workspace/results" \
    -v "$(pwd)/lib/benchmarks:/hyperledger/caliper/workspace/lib/benchmarks" \
    -v "$(pwd)/lib/connector:/hyperledger/caliper/workspace/lib/connector" \
    -v "$(pwd)/lib/util:/hyperledger/caliper/workspace/lib/util" \
    -v /var/run/docker.sock:/var/run/docker.sock \
    -e CALIPER_BENCHCONFIG=config/cluster/benchmark_install_sc.yaml \
    --name caliper henriquej0904/hyflexchain:caliper /bin/sh -c "./launch-master.sh && cp report.html results/report-$id.html"

docker wait caliper
