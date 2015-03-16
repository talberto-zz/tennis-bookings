package models

import com.google.inject.AbstractModule

import play.api.Configuration

import net.codingwell.scalaguice.ScalaModule

class BookingsModule extends AbstractModule with ScalaModule {

  def configure {
    bind[Configuration].toProvider[ConfigurationProvider]
  }
}