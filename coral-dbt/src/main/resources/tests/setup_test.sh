#!/bin/bash

# Check that the correct number of arguments were provided
if [ $# -ne 2 ]; then
    echo "Usage: $0 source_file destination_file"
    exit 1
fi

# Copy the macro for isolated invocation as a Jinja function
cp $1 $2

# Remove copyright and comments (will otherwise be printed) and return call
sed -i -e '/\/\*/,/\*\//d ; /^--/d ; s/return(\(.*\))/\1/' $2

# Add call to macro
echo -e "\n{{ $(perl -ne 'if (/{% *macro +([[:alnum:]_]+)\(([^)]*)\)/) {print "$1($2)\n";}' $2) }}" >> $2
