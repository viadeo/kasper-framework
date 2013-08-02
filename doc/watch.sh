#!/bin/sh

# CONFIGURATION
if [ -z "$1" ]
  then
	DIR="source"
  else
	DIR=$1
fi

EVENTS="modify,move,create,delete,delete_self"
FIFO="/tmp/inotify2.fifo"

# FUNCTIONS
on_exit() {
    kill $INOTIFY_PID
    rm $FIFO
    exit
}

on_event() {
    local date=$1
    local time=$2
    local file=$3

    sleep 5

    echo "$date $time Fichier modifier: $file"
}

# MAIN
if [ ! -e "$FIFO" ]
then
    mkfifo "$FIFO"
fi

inotifywait --exclude "$DIR/_build/|.*/\..*" -me "$EVENTS" --timefmt '%Y-%m-%d %H:%M:%S' --format '%T %f' "$DIR" > "$FIFO" &
INOTIFY_PID=$!

trap "on_exit" 2 3 15

while read date time file
do
  (cd $DIR && make html) > /dev/null # let errors being displayed, can be usefu, 2>&1
done < "$FIFO"

on_exit



