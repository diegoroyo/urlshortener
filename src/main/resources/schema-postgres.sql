-- Clean database

DROP TABLE IF EXISTS CLICK;
DROP TABLE IF EXISTS SHORTURL;

-- ShortURL

CREATE TABLE SHORTURL
(
    ID      VARCHAR(256) CONSTRAINT shorturl_pk PRIMARY KEY, -- Key
    TARGET  VARCHAR(1024),           -- Original URL
    CREATED TIMESTAMP,               -- Creation date
    MODE    INTEGER,                 -- Redirect mode
    ACTIVE  BOOLEAN,                 -- Active URL
    SAFE    BOOLEAN,                 -- Safe target
    IP      VARCHAR(20),             -- IP
    COUNTRY VARCHAR(50)              -- Country
);

-- Click

CREATE TABLE CLICK
(
    CLICKID BIGINT CONSTRAINT click_pk PRIMARY KEY,                       -- KEY
    SHORTID  VARCHAR(256) REFERENCES SHORTURL(ID), -- Foreign key
    CREATED  TIMESTAMP,                                                   -- Creation date
    REFERRER VARCHAR(1024),                                               -- Traffic origin
    BROWSER  VARCHAR(50),                                                 -- Browser
    PLATFORM VARCHAR(50),                                                 -- Platform
    IP       VARCHAR(20),                                                 -- IP
    COUNTRY  VARCHAR(50)                                                  -- Country
);