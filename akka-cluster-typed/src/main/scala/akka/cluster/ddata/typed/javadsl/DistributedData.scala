/**
 * Copyright (C) 2017-2018 Lightbend Inc. <https://www.lightbend.com>
 */

package akka.cluster.ddata.typed.javadsl

import akka.actor.typed.ActorSystem
import akka.actor.typed.Extension
import akka.actor.typed.ExtensionId
import akka.actor.typed.ActorRef
import akka.actor.typed.ExtensionSetup

object DistributedData extends ExtensionId[DistributedData] {
  def get(system: ActorSystem[_]): DistributedData = apply(system)

  override def createExtension(system: ActorSystem[_]): DistributedData =
    new DistributedData(system)
}

/**
 * Akka extension for convenient configuration and use of the
 * [[Replicator]]. Configuration settings are defined in the
 * `akka.cluster.ddata` section, see `reference.conf`.
 *
 * This is using the same underlying `Replicator` instance as
 * [[akka.cluster.ddata.DistributedData]] and that means that typed
 * and untyped actors can share the same data.
 */
class DistributedData(system: ActorSystem[_]) extends Extension {

  /**
   * `ActorRef` of the [[Replicator]] .
   */
  val replicator: ActorRef[Replicator.Command] =
    akka.cluster.ddata.typed.scaladsl.DistributedData(system).replicator.narrow[Replicator.Command]

}

object DistributedDataSetup {
  def apply[T <: Extension](createExtension: ActorSystem[_] ⇒ DistributedData): DistributedDataSetup =
    new DistributedDataSetup(new java.util.function.Function[ActorSystem[_], DistributedData] {
      override def apply(sys: ActorSystem[_]): DistributedData = createExtension(sys)
    }) // TODO can be simplified when compiled only with Scala >= 2.12

}

/**
 * Can be used in [[akka.actor.setup.ActorSystemSetup]] when starting the [[ActorSystem]]
 * to replace the default implementation of the [[DistributedData]] extension. Intended
 * for tests that need to replace extension with stub/mock implementations.
 */
final class DistributedDataSetup(createExtension: java.util.function.Function[ActorSystem[_], DistributedData])
  extends ExtensionSetup[DistributedData](DistributedData, createExtension)
