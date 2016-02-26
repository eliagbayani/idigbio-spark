# Submit a spark job against a Mesos-Spark configuration.
# see http://spark.apache.org/docs/latest/running-on-mesos.html
#
# SPARK_DATA: where datasets are 
# SPARK_JOBS: where spark job jars sit
# SPARK_HOME: where apache has been installed
#


$SPARK_HOME/bin/spark-submit \
--master mesos://apihack-c18.idigbio.org:7077 \
--deploy-mode cluster \
--executor-memory 40G \
--driver-memory 6G \
--total-executor-cores 10 \
--class DarwinCoreToParquet \
--conf spark.local.dir=/home/int/data/tmp \
file:///home/int/jobs/iDigBio-LD-assembly-1.3.3.jar \
file:///home/int/data/gbif/meta.xml