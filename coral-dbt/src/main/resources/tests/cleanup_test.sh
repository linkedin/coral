#!/bin/bash

# Check that the correct number of arguments were provided
if [ $# -ne 1 ]; then
    echo "Usage: $0 target_file"
    exit 1
fi

# Remove given file
rm -f $1
