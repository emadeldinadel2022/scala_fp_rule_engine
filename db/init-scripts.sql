create extension if not exists hstore;
create schema if not exists destination;
create table if not exists destination."Order" ("order_id" BIGSERIAL NOT NULL PRIMARY KEY,
                        "transaction_timestamp" TIMESTAMP NOT NULL,"product_name" VARCHAR NOT NULL,
                        "expiry_date" DATE NOT NULL, "quantity" INT NOT NULL, "total_price" FLOAT NOT NULL,
                        "channel" VARCHAR NOT NULL, "payment_method" VARCHAR NOT NULL, "discount" NUMERIC NOT NULL,
                        "final_price" NUMERIC NOT NULL);