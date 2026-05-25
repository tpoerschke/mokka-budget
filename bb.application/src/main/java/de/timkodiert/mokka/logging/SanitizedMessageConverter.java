package de.timkodiert.mokka.logging;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class SanitizedMessageConverter extends MessageConverter {

    @Override
    public String convert(ILoggingEvent event) {
        return DatabaseLogSanitizer.sanitize(super.convert(event));
    }
}
