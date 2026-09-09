package de.timkodiert.mokka.importer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CsvEncoding {

    US_ASCII(StandardCharsets.US_ASCII),
    ISO_8859_1(StandardCharsets.ISO_8859_1),
    UTF_8(StandardCharsets.UTF_8),
    UTF_16(StandardCharsets.UTF_16),
    UTF_16BE(StandardCharsets.UTF_16BE),
    UTF_16LE(StandardCharsets.UTF_16LE);

    private final Charset charset;
}
