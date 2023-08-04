#!/usr/bin/env python

# run the experiments for all relevant values of k and one specific choice of algorithm

from __future__ import print_function
from subprocess import call

import sys
import random
import os
import argparse
import glob
import subprocess as sp
import multiprocessing as mp

parser = argparse.ArgumentParser(
    description='A script for running our experiments on the real-world and random graphs.')

#graph_repo_path = "/export/storage/data"
graph_repo_path = ""
resultFilesPath = ""


def work(in_file):
    
    """Defines the work unit on an input file"""
    # each line in the file contains the following arguments seperated by ;
    # output_file, graph_file, stop_after_timeout, algo_type, time_limit, problem_id usw
    split_line = in_file.split(";")
    # path to the output file relative to resulFilePath
    output_file = resultFilesPath + split_line[0]
    # path to the graph file relative to where the graph repository is located
    graph_file = graph_repo_path + split_line[1]
    # the type of the algorithm that should be used
    algo_type = split_line[2]
    # the problem_id for these instances
    problem_id = int(split_line[3])
    # the time limit for the execution in ms
    time_limit = split_line[4]
    # max steps only relevant for reduction 
    max_steps = split_line[5]
    # degCaps only relevant for reduction
    degCapIntersect = split_line[6]
    degCapSubsets = split_line[7]
    # relaxParam only relevant for reduction
    relaxParam = split_line[8]
    # addProb only relevant for callbacks
    addProb = split_line[9]
    # relationConv only relevant for callbacks
    relationConv = split_line[10]
    

    #run experiments
    sp.call(
        ["java", "-jar", "TerrainGuarding.jar", output_file, graph_file,
        algo_type, str(problem_id), str(time_limit), max_steps, degCapIntersect, degCapSubsets, relaxParam, addProb, relationConv]
    )

    #run experiments
    #sp.call(
    #    ["java", "-Djava.library.path=/opt/gurobi1000/linux64/lib/", "-jar", "Code.jar", output_file, graph_file,
    #    algo_type, str(problem_id), str(time_limit)]
    #)
        
 


if __name__ == '__main__':
    files = []
    # experiments for real-world instances
    for line in open(sys.argv[1]):
        if not line.startswith("#"):
            files += [line.strip()]

    # Set up the parallel task pool to use all available processors
    count = 12

    # Set up the parallel task pool to use all available processors
    #count = 12

    # shuffle such that load on each processor is more evenly distributed
    random.shuffle(files)
    pool = mp.Pool(processes=count)

    # Run the jobs

    pool.map(work, files)