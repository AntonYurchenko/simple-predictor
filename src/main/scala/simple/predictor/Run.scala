package simple.predictor

import java.net.InetSocketAddress

import simple.predictor.Config._

object Run extends App {

  // https://mxnet.incubator.apache.org/versions/master/tutorials/java/ssd_inference.html
  val model = new Model(modelPrefix, modemEpoch, modemEdge, threshold, context)
  val server = new Server(new InetSocketAddress(host, port), entryPoint, model)

  Runtime.getRuntime.addShutdownHook(new Thread(() => server.stop()))

  try server.start() catch {
    case ex: Exception => ex.printStackTrace()
  }

}
