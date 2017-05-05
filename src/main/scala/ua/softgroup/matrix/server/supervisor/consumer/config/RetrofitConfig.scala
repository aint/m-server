package ua.softgroup.matrix.server.supervisor.consumer.config

import com.fasterxml.jackson.databind.{MapperFeature, ObjectMapper, SerializationFeature}
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.core.env.Environment
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import ua.softgroup.matrix.server.supervisor.consumer.endpoint.SupervisorEndpoint

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
@Configuration
class RetrofitConfig {

  private val SUPERVISOR_API = "supervisor.api"

  @Bean
  def supervisorEndpoint(environment: Environment, objectMapper: ObjectMapper): SupervisorEndpoint = {
    new Retrofit.Builder()
      .baseUrl(environment.getRequiredProperty(SUPERVISOR_API))
      .addConverterFactory(JacksonConverterFactory.create(objectMapper.findAndRegisterModules))
      .build.create(classOf[SupervisorEndpoint])
  }

  @Bean
  def objectMapper(builder: Jackson2ObjectMapperBuilder): ObjectMapper = {
    builder.createXmlMapper(false)
      .build[ObjectMapper]()
      .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
      .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true)
  }

}
