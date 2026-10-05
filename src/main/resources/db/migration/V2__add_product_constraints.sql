ALTER TABLE public.products
    ALTER COLUMN category_id SET NOT NULL;

ALTER TABLE public.products
    ADD CONSTRAINT products_price_check
        CHECK (price >= 0.01);

ALTER TABLE public.products
    ADD CONSTRAINT products_stock_check
        CHECK (stock >= 0);

CREATE INDEX idx_products_category_id
    ON public.products(category_id);