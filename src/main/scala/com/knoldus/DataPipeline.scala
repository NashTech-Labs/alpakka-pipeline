package com.knoldus

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.alpakka.elasticsearch.WriteMessage
import akka.stream.alpakka.elasticsearch.scaladsl.ElasticsearchSink
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.{Done, NotUsed}
import org.apache.http.HttpHost
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}
import org.elasticsearch.client.RestClient
import play.api.libs.json.Json
import spray.json.{JsObject, JsString, JsonWriter}

import scala.concurrent.Future

object DataPipeline extends App {
  implicit val system: ActorSystem = ActorSystem.create()
  val client: RestClient = RestClient.builder(new HttpHost("localhost", 9200, "http")).build()

  val jsonWriter: JsonWriter[Person] = (person: Person) => {
    JsObject(
      "id" -> JsString(person.id),
      "name" -> JsString(person.name),
      "city" -> JsString(person.city))
  }

  val esSink2: Sink[WriteMessage[Person, NotUsed], Future[Done]] = ElasticsearchSink
    .create[Person]("person-index", "person")(client, jsonWriter)

  val consumerSettings: ConsumerSettings[Array[Byte], String] =
    ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
      .withBootstrapServers("localhost:9092")
      .withGroupId("alpakka-demo")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val kafkaSource: Source[ConsumerRecord[Array[Byte], String], Consumer.Control] =
    Consumer.plainSource(consumerSettings, Subscriptions.topics("per"))

  val flow: Flow[Person, WriteMessage[Person, NotUsed], NotUsed] = Flow[Person].map(person => WriteMessage.createIndexMessage(person.id, person))
  val convert: Flow[ConsumerRecord[Array[Byte], String], Person, NotUsed] = Flow[ConsumerRecord[Array[Byte], String]].map { message =>

    // Parsing the record as Person Object
    Json.parse(message.value()).as[Person]
  }
  kafkaSource
    .via(convert)
    .via(FlowCassandra.written)
    .via(flow)
    .runWith(esSink2)
}
