update T_CONFIG set CFG_VALUE_C = 'RAM' where CFG_ID_C = 'LUCENE_DIRECTORY_STORAGE';

CREATE TABLE registration_request (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    password TEXT NOT NULL,
    message TEXT,
    status TEXT NOT NULL DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);