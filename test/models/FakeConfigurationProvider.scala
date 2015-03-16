package models

import com.google.inject.Provider

import play.api.Configuration
import play.api.test.FakeApplication

class FakeConfigurationProvider extends Provider[Configuration] {

  val application = new FakeApplication()
  
  def get: Configuration = {
    application.configuration
  }
}
