# User-Microservice

Before starting this service, please enable the Eureka service and the Gateway service.

In addition, the Common Library will need to be installed to the local Maven repository before compiling this service.

`mvn spring-boot:run` will compile and run the service.

Configuration may be necessary. View `application.properties` and `application-dev.properties` for information on which configurations are required.

All properties beginning with `cloud.aws.pinpoint` are only necessary for an AWS-connected service. When not using the messaging service, you may switch out the MessagingService implementation with MockMessagingService.

JWT tokens are generated with PEM-encoded PKCS#8 formatted RSA keys. The private (`app.key`) and public (`app.pub`) key files are expected to be in the resources folder of the classpath.

All properties beginning with `megabytes.frontend` are necessary for generating correct frontend-facing links for the user. This will not need to be reconfigured in most cases outside of deployment.

# API Documentation

To access through the Gateway, prefix all URLS by `/user-service`.

Get Login Info
--------------

`GET /login/u/{username}`

**Path Variables:**

`username` - The username of the user whose login info will be retrieved.

**Response:**

```java
{
  username: String,
  password: String,
  enabled: boolean
}
```

Should only be called internally by the authentication microservice. Pulls sensitive login information from the database for authentication purposes.

Get All Users
-------------

`GET /user/all`

**Response:**

```java
[
  {
    id: Integer,
    userName: String
  },
  ...
]
```

Should only be called by an admin.

Get All Users + Information
---------------------------

`GET /user/allUserInformation`

**Response:**

```java
[
  {
    users_Id: Integer,
    userName: String,
    enabled: boolean,
    first_name: String,
    last_name: String,
    email: String,
    phone_number: String,
    birthdate: Date,
    veteran_status: boolean,
    email_confirmed: boolean,
    account_active: boolean
  },
  ...
]
```

Should only be called by an admin.

Get User By Username
--------------------

`GET /user/username/{username}`

**Path Variables:**

`username` - The username of the user whose login info will be retrieved.

**Response:**

```java
{
  id: Integer,
  userName: String,
  password: String,
  enabled: boolean
}
```

Should only be called by an admin.

Get User Information By User Id
-------------------------------

`GET /user/userInformation/id/{userId}`

**Path Variables:**

`userId` - The numeric id of the user whose information will be retrieved.

**Response:**

```java
{
  users_Id: Integer,
  userName: String,
  enabled: boolean,
  first_name: String,
  last_name: String,
  email: String,
  phone_number: String,
  birthdate: Date,
  veteran_status: boolean,
  email_confirmed: boolean,
  account_active: boolean
}
```

Should only be retrieved with proper authorization.

Get User By User Id \[NOT IMPLEMENTED\]
---------------------------------------

`GET /user/id/{userId}`

**Path Variables:**

`userId` - The numeric id of the user whose login info will be retrieved.

Response:

```java
{
  id: Integer,
  userName: String,
  password: String,
  enabled: boolean
}
```

Should only be called by an admin.

Create User
-----------

`POST /user/create-user`

**Request:**

```java
{
  userName: String,
  password: String
}
```

**Response:**

`User created with id: {id}`

Should only be called by an admin. `/create-user-information` should be generally preferred over this call as it contains necessary communication info for password resets.

Create User + Information
-------------------------

`POST /user/create-user-information`

**Request:**

```java
{
  userName: String
  password: String,
  first_name: String,
  last_name: String,
  email: String,
  phone_number: String,
  birthdate: Date,
  veteran_status: Boolean,
  email_confirmed: Boolean,
  communication_type_id: Integer,
  account_active: Boolean
}
```

**Response:**

`User created with: {id}`

Creates a user along with the necessary communication info. A reset password message is sent to the communication method specified in the request.

Update User
-----------

`POST /user/update-user`

**Request:**

```java
{
  id: Integer,
  userName: String,
  password: String,
  enabled: Boolean
}
```

**Response:**

`Updated user`

Updates a user based on the ID specified in the request body.

Update User Information
-----------------------

`POST /user/update-user-information`

**Request:**

```java
{
  users_Id: Integer,
  first_name: String,
  last_name: String,
  email: String,
  phone_number: String,
  birthdate: Date,
  veteran_status: Boolean,
  email_confirmed: Boolean,
  account_active: Boolean
}
```

**Response:**

`Updated userInformation`

Updates a user based on the ID specified in the request body.

Delete User \[NOT IMPLEMENTED\]
-------------------------------

`POST /user/delete-user`

**Request:**

```java
{
  id: Integer
}
```

**Response:**

`null`

Deletes a user based on the ID specified in the request body.

Set User Active Information
---------------------------

`POST /user/set-user-active-information`

**Request:**

```java
{
  users_Id: Integer,
  enabled: Boolean,
  account_active: Boolean
}
```

**Response:**

`User account status updated.`

Sets a user (based on the ID specified in the request body) active or inactive.

Send Confirmation Message
-------------------------

`POST /user/confirmationMessage`

**Request:**

```java
{
  userId: Integer
}
```

**Response:**

`Sent successfully`

Sends a confirmation message to the preferred communication address of the user ID specified in the request body.

Send Reset Password Message
---------------------------

`POST /user/resetPasswordMessage`

**Request:**

```java
{
  email: String
}
```

**Response:**

`Sent successfully`

Sends a reset password message to the preferred communication address of the user email specified in the request body.

Apply Confirmation
------------------

`PUT /user/confirmation`

**Query Parameters:**

`token` - The confirmation token sent in the confirmation message. Should already be embedded in the link.

**Response:**

`Email confirmed`

Confirms the communication method of the user embedded in the token data.

Apply Reset Password
--------------------

`POST /user/resetPassword`

**Query Parameters:**

`token` - The reset password token sent in the reset password message. Should already be embedded in the link.

**Request:**

```java
{
  password: String
}
```

**Response:**

`Password set successfully`

Sets a new password for the user embedded in the token data.