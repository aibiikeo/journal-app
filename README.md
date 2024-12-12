# Journal App API

Welcome to the **Journal App API**! This API allows you to manage journal entries, user authentication, and image uploads related to journal entries. The API provides functionalities to create, read, update, and delete journal entries, reset passwords, and more.

---

## API Documentation

The API follows the **OpenAPI Specification (OAS) 3.0.1**, and the documentation is available in **Swagger** format for easy exploration of endpoints. Below is a summary of the available functionality and how you can explore the full API.

### Base URL

```
http://64.23.185.31:8080
```

### Authentication

All requests that modify data (POST, PUT, DELETE) require an `Authorization` header with a valid token.

---

## Explore and Test the API with Swagger UI

You can explore and test the full range of available API endpoints using **Swagger UI**. Simply visit the following URL to view the Swagger documentation:

```
http://64.23.185.31:8080/swagger-ui.html
```

In Swagger, you'll find all the available endpoints, request parameters, request bodies, and response schemas. The interactive interface allows you to execute requests directly from within Swagger to test the API. It's a comprehensive tool for exploring the API and understanding how each endpoint works.

---

## Getting Started with the API

To get started, first sign up by sending a `POST` request to `/trusted/auth/signup` with your email and password. Upon successful sign-up, you'll receive your **User ID**. Next, log in by sending a `POST` request to `/trusted/auth/login` with your credentials, and you will receive a **Bearer token**.

With your **User ID** and **Bearer token**, you can now start creating and managing your journal entries. Use a `POST` request to `/journal-entries/{userId}` (replace `{userId}` with your actual User ID) to create a journal entry. In the request body, include the title, content, entry date, and any images you want to attach. After creating an entry, you can easily update it by using a `PUT` request to `/journal-entries/{userId}/{id}`, or delete it with a `DELETE` request to `/journal-entries/{userId}/{id}`. To view your journal entries, use a `GET` request to `/journal-entries/{userId}` for all entries, or `/journal-entries/{userId}/{id}` to view a specific entry.

Be sure to include the **Bearer token** in the `Authorization` header for all requests that modify or view data.

---

## **API Endpoints for Postman**

You can also test the API directly in Postman by setting up the requests.

1. **Sign Up**  
   `POST http://64.23.185.31:8080/trusted/auth/signup`
    - Create a new user account.

2. **Login**  
   `POST http://64.23.185.31:8080/trusted/auth/login`
    - Log in to receive a **Bearer token**.

3. **Create Journal Entry**  
   `POST http://64.23.185.31:8080/journal-entries/{userId}`
    - Replace `{userId}` with your **User ID** to create a new journal entry.

4. **Update Journal Entry**  
   `PUT http://64.23.185.31:8080/journal-entries/{userId}/{id}`
    - Replace `{userId}` and `{id}` to update an existing journal entry.

5. **Delete Journal Entry**  
   `DELETE http://64.23.185.31:8080/journal-entries/{userId}/{id}`
    - Replace `{userId}` and `{id}` to delete a specific journal entry.

6. **View All Journal Entries**  
   `GET http://64.23.185.31:8080/journal-entries/{userId}`
    - Replace `{userId}` to view all journal entries for a user.

7. **View Single Journal Entry**  
   `GET http://64.23.185.31:8080/journal-entries/{userId}/{id}`
    - Replace `{userId}` and `{id}` to view a specific journal entry.

8. **View an Image**  
`GET http://64.23.185.31:8080/journal-entries/images/{fileName}`
    - Replace `{fileName}` to download a specific image linked to a journal entry.

9. **Delete Image from Journal Entry**  
    `DELETE http://64.23.185.31:8080/journal-entries/{userId}/{id}/{imageId}`
    - Replace `{userId}`, `{id}`, and `{imageId}` to delete a specific image from a journal entry.

10. **Reset Password**  
    `POST http://64.23.185.31:8080/password-reset/reset`
    - Reset the password using a token.

11. **Request Password Reset**  
    `POST http://64.23.185.31:8080/password-reset/request/{userId}`
    - Replace `{userId}` to request a password reset for a specific user.

12. **Base URL**  
    `GET http://64.23.185.31:8080/`

Make sure to replace the placeholders (`{userId}`, `{id}`, `{fileName}`, `{imageId}`) with actual values when using the API.