package simple.predictor

import java.net.InetSocketAddress

import com.sun.net.httpserver.{HttpExchange, HttpServer}
import javax.imageio.ImageIO
import org.json4s.DefaultFormats
import org.json4s.native.Serialization

class Server(address: InetSocketAddress, entryPoint: String, model: Model) {

  private val server = HttpServer.create(address, 0)

  server.createContext(entryPoint, (http: HttpExchange) => {

    val header = http.getRequestHeaders
    val (httpCode, json) = if (header.containsKey("Content-Type") && header.getFirst("Content-Type") == "image/jpeg") {
      val image = ImageIO.read(http.getRequestBody)
      val predictionSeq = model.predict(image)
      (200, Map("prediction" -> predictionSeq))
    } else (400, Map("error" -> "Invalid content"))

    val responseJson = Serialization.write(json)(DefaultFormats)
    val httpOs = http.getResponseBody
    http.getResponseHeaders.set("Content-Type", "application/json")
    http.sendResponseHeaders(httpCode, responseJson.length)
    httpOs.write(responseJson.getBytes)
    httpOs.close()

  })

  def start(): Unit = server.start()

  def stop(): Unit = server.stop(0)

}
