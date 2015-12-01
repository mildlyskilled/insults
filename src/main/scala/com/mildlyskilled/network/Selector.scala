package com.mildlyskilled.network

import com.typesafe.config.ConfigFactory
import java.net.NetworkInterface
import scala.collection.JavaConverters._
import scala.collection.convert.Wrappers.JEnumerationWrapper
import scala.tools.jline.console.ConsoleReader


trait Selector {

  def configKey = "engine"

  def distillIpAddresses(interface: NetworkInterface): String = {
    val interfaceArray = interface.getInterfaceAddresses
    val i = for (interface <- interfaceArray.asScala.filter(_.getBroadcast != null)) yield interface.getAddress.getHostAddress
    i.mkString
  }

  val interfaces = new JEnumerationWrapper(NetworkInterface.getNetworkInterfaces).toList.filter(!_.isLoopback).filter(_.isUp)
  /** Ideally this should give a list of ip addresses and then we choose the one we want
    * but alas I am lazy so just pop the first ip address that works and use it instead
    * I use getBroadcast here as a subtle way of filtering out IPV6 addresses they have
    * a null value
    */
  val ipAddresses = for (i <- interfaces) yield distillIpAddresses(i)

  /**
    * First make sure the ip address in the configuration file is not
    * in the actual IP address list found on this machine
    */
  val config = ConfigFactory.load.getConfig(configKey)
  val ipAddressInConfig = config.getString("akka.remote.netty.tcp.hostname")

  val ipAddress = {

    if (ipAddresses.size > 1) {
      ipAddresses foreach println
      /**
        * Make sure the IP address typed in here is valid otherwise this prompt
        * will be displayed FOREVER (unitl a keyboard interrupt of course)
        */
      new ConsoleReader().readLine("Which IP Address shall we bind to?  ")
    } else {
      ipAddresses.head
    }
  }


  val clientConfig = ConfigFactory.parseString( s"""akka.remote.netty.tcp.hostname="$ipAddress" """)
  val defaultConfig = ConfigFactory.load.getConfig(configKey)

  def completeConfig = clientConfig.withFallback(defaultConfig)
}
