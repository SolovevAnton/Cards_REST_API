/*DROP TABLE IF EXISTS cards;
DROP TABLE IF EXISTS categories;*/
DROP TABLE IF EXISTS users;

INSERT INTO users (login, name, password, registration_date)
VALUES
    ('1', 'first','1', NOW()),
    ('2', 'second','2', NOW()),
    ('3', 'third','3', NOW());

INSERT INTO categories (name, user_id)
VALUES
    ('firstCat', 1),
    ('secondCat', 1),
    ('thirdCat', 2);

INSERT INTO cards (question, answer, category_id, creation_date)
VALUES
    ('Q1', 'A1', 1, NOW()),    -- Assuming 'Q1' and 'A1' are linked to the category with ID 1
    ('Q2', 'A2', 1, NOW()),    -- Assuming 'Q2' and 'A2' are also linked to the category with ID 1
    ('Q3', 'A3', 2, NOW()),
    ('Q4', 'A4', 3, NOW());


