CREATE TABLE IF NOT EXISTS prices (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    brand_id    BIGINT       NOT NULL,
    start_date  TIMESTAMP    NOT NULL,
    end_date    TIMESTAMP    NOT NULL,
    price_list  BIGINT       NOT NULL,
    product_id  BIGINT       NOT NULL,
    priority    INTEGER      NOT NULL,
    price       DECIMAL(10,2) NOT NULL,
    currency    VARCHAR(3)   NOT NULL
);

-- Covers the lookup query: WHERE brand_id=? AND product_id=? AND start_date<=? AND end_date>=?
-- Including priority allows the DB to satisfy ORDER BY priority DESC without a separate sort step
CREATE INDEX IF NOT EXISTS idx_prices_lookup
    ON prices (brand_id, product_id, start_date, end_date, priority);
