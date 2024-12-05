-- Insert user 1
INSERT INTO users (email, password, created_at, updated_at)
VALUES ('user1@example.com', '$2a$10$D0JrY2R1goL3hFcCv0zutuXburweA9DE9JtFM17CqZyVxchS.m24m', NOW(), null); --password123

-- Insert user 2
INSERT INTO users (email, password, created_at, updated_at)
VALUES ('user2@example.com', '$2a$10$.DaJFpdjoI6GyOrLcM8BDuLC2eJEfHBQGs13i/5r33kYVrKZIwOqW', NOW(), null); --password456

-- Insert user 3
INSERT INTO users (email, password, created_at, updated_at)
VALUES ('user3@example.com', '$2a$10$kvW.ec1o53rpJTOCqjNrC.8hC.I4Xm5cbtibX6Edel.0gnduSG/oK', NOW(), null); --password789