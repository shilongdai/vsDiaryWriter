#!/bin/bash
SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a syml$
  DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative syml$
done
DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"


java -jar -XstartOnFirstThread $DIR/full-journal-1.0.0.jar;

