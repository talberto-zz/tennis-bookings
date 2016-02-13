package models

import com.google.inject.Provider

import play.api.Configuration
import play.api.Play._

class ConfigurationProvider extends Provider[Configuration] {

  def get: Configuration = {
    application.configuration
  }
}
