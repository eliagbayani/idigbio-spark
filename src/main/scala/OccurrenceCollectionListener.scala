import java.util
import java.util.concurrent.atomic.{AtomicBoolean, AtomicLong}

import org.apache.spark.executor.{OutputMetrics, TaskMetrics}
import org.apache.spark.scheduler._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import spray.json._


case class MonitorSelector(taxonSelector: String, wktString: String, traitSelector: String)

case class MonitorStatus(selector: OccurrenceSelector, status: String, percentComplete: Double, eta: Long)

object MonitorStatusJsonProtocol extends DefaultJsonProtocol {
  implicit val monitorSelectorFormat = jsonFormat4(OccurrenceSelector)
  implicit val monitorStatusFormat = jsonFormat4(MonitorStatus)
}

class OccurrenceCollectionListener(monitorSelector: OccurrenceSelector) extends SparkListener {
  val props = new util.HashMap[String, Object]()
  val topic = "effechecka-selector"
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
    "org.apache.kafka.common.serialization.StringSerializer")
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
    "org.apache.kafka.common.serialization.StringSerializer")

  lazy val producer = new KafkaProducer[String, String](props)

  var started: AtomicBoolean = new AtomicBoolean(false)
  var startTime: Long = 0L
  var totalSubmittedTasks: AtomicLong = new AtomicLong(0L)
  var totalCompletedTasks: AtomicLong = new AtomicLong(0L)


  def sendMsg(msg: String): Unit = {
    val message = new ProducerRecord[String, String](topic, null, msg)
    producer.send(message)
  }

  override def onStageSubmitted(stage: SparkListenerStageSubmitted): Unit = {
    totalSubmittedTasks.getAndAdd(stage.stageInfo.numTasks)
  }

  override def onTaskEnd(task: SparkListenerTaskEnd): Unit = {
    if (task.taskInfo.successful) {
      totalCompletedTasks.incrementAndGet()
      val finishTime: Long = task.taskInfo.finishTime

      if (totalSubmittedTasks.get() > 10 && task.taskInfo.index % 100 == 0) {
        reportProgress(finishTime)
      }
    }

  }

  def reportProgress(finishTime: Long, monitorStatus: String = "processing"): Unit = {
    def timeToString(remainingTimeApproxMin: Float): String = {
      "%.0f".format(remainingTimeApproxMin / (1000 * 60))
    }

    val totalCompletedSnapshot = totalCompletedTasks.get
    val totalSubmittedSnapshot = totalSubmittedTasks.get

    val percentComplete = totalCompletedSnapshot * 100 / totalSubmittedSnapshot

    val totalDuration = finishTime - startTime
    val avgDurationPerTask = totalDuration / totalCompletedSnapshot.toFloat
    val remainingTimeApproxMs = (totalSubmittedSnapshot - totalCompletedSnapshot) * avgDurationPerTask
    val remainingTimeApproxMin: Float = remainingTimeApproxMs / (1000 * 60)

    import MonitorStatusJsonProtocol._
    val status = MonitorStatus(selector = monitorSelector,
      status = monitorStatus, percentComplete = percentComplete, eta = remainingTimeApproxMs.toLong)

    sendMsg(status.toJson.toString)
  }

  override def onJobStart(jobStart: SparkListenerJobStart): Unit = {
    // onApplicationStart not sent to embedded listener, so using first job instead
    if (!started.getAndSet(true)) {
      startTime = jobStart.time
    }
  }

  override def onApplicationEnd(applicationEnd: SparkListenerApplicationEnd): Unit = {
    reportProgress(applicationEnd.time, "ready")
  }
}
