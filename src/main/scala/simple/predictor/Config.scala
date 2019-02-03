package simple.predictor

import org.apache.mxnet.Context

import scala.util.Try

object Config {

  val host: String = env("REST_HOST") getOrElse "0.0.0.0"
  val port: Int = env("REST_PORT") flatMap (p => Try(p.toInt).toOption) getOrElse 8080
  val entryPoint: String = env("REST_ENTRY_POINT") getOrElse "/predict"
  val threshold: Float = env("PROBABILITY_MORE") flatMap (p => Try(p.toFloat).toOption) getOrElse 0.5f
  val modelPrefix: String = env("MODEL_PREFIX") getOrElse "models/resnet50_ssd_model"
  val modemEpoch: Int = env("MODEL_EPOCH") flatMap (p => Try(p.toInt).toOption) getOrElse 0
  val modemEdge: Int = env("MODEL_EDGE") flatMap (p => Try(p.toInt).toOption) getOrElse 512
  val context: Context = env("MODEL_CONTEXT_GPU") flatMap {
    isGpu => Try(if (isGpu.toBoolean) Context.gpu() else Context.cpu()).toOption
  } getOrElse Context.cpu()

  private def env(name: String) = Option(System.getenv(name))

}
