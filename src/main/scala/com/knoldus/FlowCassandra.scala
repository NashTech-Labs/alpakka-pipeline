package com.knoldus

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.alpakka.cassandra.scaladsl.{CassandraFlow, CassandraSession, CassandraSessionRegistry}
import akka.stream.alpakka.cassandra.{CassandraSessionSettings, CassandraWriteSettings}
import akka.stream.scaladsl.Flow
import com.datastax.oss.driver.api.core.cql.{BoundStatement, PreparedStatement}

object FlowCassandra {
  val system: ActorSystem = ActorSystem.create()
  val sessionSettings: CassandraSessionSettings = CassandraSessionSettings()
  implicit val cassandraSession: CassandraSession =
    CassandraSessionRegistry.get(system).sessionFor(sessionSettings)

  val statementBinder: (Person, PreparedStatement) => BoundStatement =
    (person, preparedStatement) => preparedStatement.bind(person.id, person.name, person.city)

  val written: Flow[Person, Person, NotUsed] = CassandraFlow.create(CassandraWriteSettings.defaults,
    s"INSERT INTO demo.emp(id, name, city) VALUES (?, ?, ?)",
    statementBinder)
}
