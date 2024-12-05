-- Insert user 1
INSERT INTO users (email, password, created_at, updated_at)
VALUES ('user1@example.com', '$2a$10$D0JrY2R1goL3hFcCv0zutuXburweA9DE9JtFM17CqZyVxchS.m24m', NOW(), null); --password123

-- Insert user 2
INSERT INTO users (email, password, created_at, updated_at)
VALUES ('user2@example.com', '$2a$10$.DaJFpdjoI6GyOrLcM8BDuLC2eJEfHBQGs13i/5r33kYVrKZIwOqW', NOW(), null); --password456

-- Insert user 3
INSERT INTO users (email, password, created_at, updated_at)
VALUES ('user3@example.com', '$2a$10$kvW.ec1o53rpJTOCqjNrC.8hC.I4Xm5cbtibX6Edel.0gnduSG/oK', NOW(), null); --password789

-- Insert first journal entry
INSERT INTO journal (title, content, entry_date, photo_url, user_id)
VALUES
    ('My First Journal', 'Today was a great day! I learned how to write SQL queries.', '2024-12-05', 'https://example.com/photo1.jpg', 1);

-- Insert second journal entry
INSERT INTO journal (title, content, entry_date, photo_url, user_id)
VALUES
    ('Learning Spring Boot', 'I started learning Spring Boot. It is really powerful and flexible!', '2024-12-06', 'https://example.com/photo2.jpg', 1);

-- Insert third journal entry
INSERT INTO journal (title, content, entry_date, photo_url, user_id)
VALUES
    ('A Day at the Park', 'Spent the day with friends at the park. We had a great picnic!', '2024-12-07', 'https://example.com/photo3.jpg', 2);

-- Insert fourth journal entry
INSERT INTO journal (title, content, entry_date, photo_url, user_id)
VALUES
    ('Spring Boot and Security', 'Learning how to integrate security into Spring Boot applications.', '2024-12-08', 'https://example.com/photo4.jpg', 3);
