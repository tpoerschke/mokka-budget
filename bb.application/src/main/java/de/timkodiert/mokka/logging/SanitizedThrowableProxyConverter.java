package de.timkodiert.mokka.logging;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.IThrowableProxy;

public class SanitizedThrowableProxyConverter extends ThrowableProxyConverter {

    @Override
    protected String throwableProxyToString(IThrowableProxy tp) {
        return DatabaseLogSanitizer.sanitize(super.throwableProxyToString(tp));
    }
}
