CREATE TABLE IF NOT EXISTS "ImportConfiguration"
(
    "id"                 INTEGER      NOT NULL,
    "name"               VARCHAR(255) NOT NULL,
    "skipLines"          INTEGER      NOT NULL,
    "encoding"           VARCHAR(50)  NOT NULL,
    "columnDate"         INTEGER      NOT NULL,
    "columnReceiver"     INTEGER      NOT NULL,
    "columnPostingText"  INTEGER      NOT NULL,
    "columnReference"    INTEGER      NOT NULL,
    "columnAmount"       INTEGER      NOT NULL,
    PRIMARY KEY ("id")
);

INSERT INTO "ImportConfiguration"
(name, skipLines, encoding, columnDate, columnReceiver, columnPostingText, columnReference, columnAmount)
VALUES ('ING', 14, 'ISO_8859_1', 1, 2, 3, 4, 7);
