package datasender.config

case class Config(
                   dataReaderConfig: DataReaderConfig,
                   kafkaProducerConfig: KafkaProducerConfig,
                   verbose: Boolean = true) {
  def isValid: Boolean = dataReaderConfig.isValid & kafkaProducerConfig.isValid
}




