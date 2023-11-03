INSERT INTO users (login, name, password, registration_date)
VALUES
    ('firstLog', 'firstPass', NOW()),
    ('secondLog', 'secondPass', NOW()),
    ('thirdLog', 'thirdPass', NOW());

INSERT INTO categories (id, name, user_id)
VALUES
    (1, 'firstCat', 1),
    (2, 'secondCat', 1),
    (3, 'thirdCat', 2);

INSERT INTO cards (id, question, answer, category_id, creation_date)
VALUES
    (1, 'Q1', 'A1', 1, NOW()),    -- Assuming 'Q1' and 'A1' are linked to the category with ID 1
    (2, 'Q2', 'A2', 1, NOW()),    -- Assuming 'Q2' and 'A2' are also linked to the category with ID 1
    (3, 'Q3', 'A3', 2, NOW());

