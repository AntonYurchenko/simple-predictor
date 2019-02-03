package simple.predictor

import java.awt.{BasicStroke, Color, Font}
import java.awt.image.BufferedImage
import java.io.{ByteArrayOutputStream, File}
import java.net.InetSocketAddress

import javax.imageio.ImageIO
import org.scalatest.{FlatSpec, Matchers}
import scalaj.http.Http
import org.json4s.{DefaultFormats, Formats}
import org.json4s.native.JsonMethods.parse
import simple.predictor.Config._
import simple.predictor.Model.Prediction

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ServerTest extends FlatSpec with Matchers {

  implicit val formats: Formats = DefaultFormats

  "Service" should "find a cat and a dog on photo" in {
    val model = new Model(modelPrefix, modemEpoch, modemEdge, threshold, context)
    val server = new Server(new InetSocketAddress(host, port), entryPoint, model)

    Future(server.start())
    Thread.sleep(5000)

    val image = ImageIO.read(getClass.getResourceAsStream("/cat_and_dog.jpg"))
    val byteOS = new ByteArrayOutputStream()
    ImageIO.write(image, "jpg", byteOS)
    val data = byteOS.toByteArray

    val response = Http(s"http://$host:$port$entryPoint").header("Content-Type", "image/jpeg").postData(data).asString
    response.code shouldEqual 200

    val prediction = parse(response.body) \\ "prediction"
    prediction.children.size shouldEqual 2

    val objectClassList = (prediction \\ "objectClass").children map (_.extract[String])
    objectClassList.head shouldEqual "cat"
    objectClassList.tail.head shouldEqual "dog"

    val bBoxCoordinates = prediction.children
      .map(_.extract[Prediction])

    val imageWithBoundaryBoxes = new BufferedImage(image.getWidth, image.getHeight, image.getType)
    val graph = imageWithBoundaryBoxes.createGraphics()
    graph.drawImage(image, 0, 0, null)
    graph.setColor(Color.RED)
    graph.setStroke(new BasicStroke(5))
    graph.setFont(new Font(Font.SANS_SERIF, Font.TRUETYPE_FONT, 30))
    bBoxCoordinates foreach { case Prediction(obj, prob, x, y, width, height) =>
      graph.drawRect(x, y, width, height)
      graph.drawString(s"$obj, prob: $prob", x + 15, y + 30)
    }
    graph.dispose()
    ImageIO.write(imageWithBoundaryBoxes, "jpg", new File("./test.jpg"))

  }

}
