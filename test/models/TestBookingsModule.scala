package models

import play.api.Configuration

class TestBookingsModule extends BookingsModule {

  override def configure {
    bind[Configuration].toProvider[FakeConfigurationProvider]
  }
}
