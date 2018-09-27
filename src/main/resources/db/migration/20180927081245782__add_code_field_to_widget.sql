-- add a new column
ALTER TABLE widget
ADD COLUMN code text;

-- make sure that the value in this column will be unique
CREATE UNIQUE INDEX unq_widget_code
ON widget (LOWER(code))
WHERE code IS NOT NULL;
