ALTER TABLE "Category" ADD COLUMN "color" VARCHAR(255) NULL;

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

CREATE TABLE Category_tmp
(
    "id"           integer       NOT NULL,
    "name"         VARCHAR2(255) NOT NULL,
    "description"  VARCHAR2(255),
    "group_id"     INTEGER REFERENCES CategoryGroup ("id"),
    "budgetValue"  INTEGER       NULL,
    "budgetActive" BOOLEAN       NOT NULL DEFAULT false,
    "budgetType"   VARCHAR2(255) NULL,
    "color"        VARCHAR2(255) NOT NULL,
    PRIMARY KEY ("id")
);

INSERT INTO Category_tmp (id, name, description, group_id, budgetValue, budgetActive, budgetType, color)
SELECT id,
       name,
       description,
       group_id,
       budgetValue,
       budgetActive,
       budgetType,
       color
FROM Category;

DROP TABLE Category;
ALTER TABLE Category_tmp
    RENAME TO Category;
