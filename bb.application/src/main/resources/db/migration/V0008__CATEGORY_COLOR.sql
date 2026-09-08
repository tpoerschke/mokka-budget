ALTER TABLE "Category" ADD COLUMN "color" VARCHAR(255) NULL;

-- Es wird darauf verzichtet, die Tabelle neu aufzubauen, damit die neue Spalte
-- nicht nullable ist. Grund: Der Aufwand mit den beiden Fremdschlüsseln in
-- FixedTurnover und UniqueTurnoverInformation ist zu groß.

UPDATE "Category"
SET "color" = CASE ("id" % 10)
                  WHEN 0 THEN '#E53935'
                  WHEN 1 THEN '#D81B60'
                  WHEN 2 THEN '#8E24AA'
                  WHEN 3 THEN '#3949AB'
                  WHEN 4 THEN '#1E88E5'
                  WHEN 5 THEN '#00897B'
                  WHEN 6 THEN '#43A047'
                  WHEN 7 THEN '#FDD835'
                  WHEN 8 THEN '#FB8C00'
                  ELSE '#6D4C41'
    END
WHERE "color" IS NULL;

