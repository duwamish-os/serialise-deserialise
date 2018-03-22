
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "type")
@JsonSubTypes(Array(
  new Type(value = classOf[ProductDetailsSimple], name = "simple"),
  new Type(value = classOf[ProductDetailsComplex], name = "complex")))
trait Product

case class ProductDetailsSimple(productId: String, description: String) extends Product

case class ProductDetailsComplex(productId: String, description: Map[String, String]) extends Product

case class PolymorphicInventory(products: List[Product])

object Serialisers {

  val jsonDS: ObjectMapper = (new ObjectMapper() with ScalaObjectMapper).registerModule(DefaultScalaModule)

  def main(args: Array[String]): Unit = {

    val inv = PolymorphicInventory(
      List(
        ProductDetailsSimple(productId = "Some_id", description = "some description"),
        ProductDetailsComplex(productId = "Some_id", description = Map("someKey" -> "somevalue"))))

    val s = jsonDS.writerWithDefaultPrettyPrinter().writeValueAsString(inv)
    println("Polymorphic Inventory as JSON: " + s)

    val json =
      """
        |{
        |  "products" : [ {
        |    "type" : "simple",
        |    "productId" : "Some_id",
        |    "description" : "some description"
        |  }, {
        |    "type" : "complex",
        |    "productId" : "Some_id",
        |    "description" : {
        |      "someKey" : "somevalue"
        |    }
        |  } ]
        |}
      """.stripMargin

    val jsonObj = jsonDS.readValue(json, classOf[PolymorphicInventory])
    println(jsonObj)
  }
}
