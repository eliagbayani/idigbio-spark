#!/bin/bash
OUTPUT_DIR=$1
CASSANDRA_HOME=/home/int/apache-cassandra-2.1.13/

function backup {
	filename=$OUTPUT_DIR$1-`date -u +"%Y%m%d"`.csv
	command="COPY $1 TO STDOUT;"
	echo $command
	$CASSANDRA_HOME/bin/cqlsh -e "$command" > $filename
	cp $filename $OUTPUT_DIR$1-current.csv
	cp $filename $OUTPUT_DIR$1-`date -u +"%Hh"`.csv
}

echo "backup starting..."
backup effechecka.monitors
backup effechecka.selector
backup effechecka.checklist_registry
backup effechecka.occurrence_collection_registry
backup effechecka.subscriptions
echo "backup complete."
