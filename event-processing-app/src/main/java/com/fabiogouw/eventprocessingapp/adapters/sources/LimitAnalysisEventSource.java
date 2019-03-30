package com.fabiogouw.eventprocessingapp.adapters.sources;

import com.fabiogouw.eventprocessingapp.core.dtos.LimitAnalysisResult;
import com.fabiogouw.eventprocessinglib.core.dtos.CustomEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;

public class LimitAnalysisEventSource extends SpringKafkaEventSource {

    private final Logger _logger = LoggerFactory.getLogger(LimitAnalysisEventSource.class);
    private static final String LISTENER_ID = "LimitAnalysisEventSource";

    public LimitAnalysisEventSource(KafkaListenerEndpointRegistry registry) {
        super(registry);
    }

    @Override
    public String getListenerId() {
        return LISTENER_ID;
    }

    @KafkaListener(id = LISTENER_ID, topics = {"limit"}, containerFactory = "containerFactory")
    public void listen(ConsumerRecord<String, LimitAnalysisResult> message,
                       @Header(CustomEvent.CORRELATION_ID) String correlationId,
                       @Header(CustomEvent.EVENT_TYPE) String eventType,
                       @Header(CustomEvent.EVENT_TYPE_VERSION) Integer eventTypeVersion,
                       Acknowledgment acknowledgment) {
        _logger.info("#### -> Consumed message -> '{}' : '{}' / '{}'", message.topic(),  message.partition(), message.offset());
        CustomEvent event = new CustomEvent(correlationId, eventType, eventTypeVersion, () -> message.value());
        run(event);
        acknowledgment.acknowledge();
    }
}
