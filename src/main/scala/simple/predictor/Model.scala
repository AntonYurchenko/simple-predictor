package simple.predictor

import java.awt.image.BufferedImage
import org.apache.mxnet._
import org.apache.mxnet.infer.ObjectDetector
import simple.predictor.Model.Prediction

class Model(prefix: String, epoch: Int, imageEdge: Int, threshold: Float, context: Context) {

  val initShape = Shape(1, 3, imageEdge, imageEdge)
  val initData = DataDesc(name = "data", initShape, DType.Float32, Layout.NCHW)
  val model = new ObjectDetector(prefix, IndexedSeq(initData), context, Option(epoch))

  private def toPrediction(originWidth: Int, originHeight: Int)(predict: (String, Array[Float])): Prediction = {
    val (objectClass, Array(probability, kx, ky, kw, kh)) = predict
    val x = (originWidth * kx).toInt
    val y = (originHeight * ky).toInt
    val w = (originWidth * kw).toInt
    val h = (originHeight * kh).toInt
    val width = if ((x + w) < originWidth) w else originWidth - x
    val height = if (y + h < originHeight) h else originHeight - y
    Prediction(objectClass, probability, x, y, width, height)

  }

  def predict(image: BufferedImage): Seq[Prediction] =
    model.imageObjectDetect(image).head map toPrediction(image.getWidth, image.getHeight) filter (_.probability > threshold)

}

object Model {

  case class Prediction(objectClass: String, probability: Float, x: Int, y: Int, width: Int, height: Int)

}
