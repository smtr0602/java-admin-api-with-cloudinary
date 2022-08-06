# java-kutaka-admin-api

## About the App

This is an API for [kutaka app](https://github.com/smtr0602/kutaka-app) admin users.

## Why was this created

- I needed an API for kutaka admin users.
- I was planning on using Node.js to develop the API (and I still am in the future), but I had the chance to learn Spring Boot,
  so I thought I would try and see how solid of an API I could create **in two weeks** with Sprint Boot.

### Tech Stack

- Java Spring Boot
- MongoDB
- Cloudinary (cloud storage)
- Heroku

## Features

### POST: `/auth/login`

Login as admin user

#### Request body:

```
{
  username: string,
  password: string
}
```

#### Response body:

```
{
  // JWT
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
}
```

### POST: `/novels`

Post new novel entry

#### Request body:

```
nameEn: string,
nameJa: string,
isHidden: boolean,
categories(optional): array,
tags(optional): array
cover: image file
pages: image files
```

### GET: `/novels(?optional_query)`

Get list of novels

- Optional query params
  - `nameEn`(string) - English title keyword
  - `nameJa`(string) - Japanese title keyword
  - `categories`(array) - categories
  - `tags`(array) - tags
  - `isHidden`(boolean) - visibility to public

### GET: `/novels/{novelId}`

Get single novel

### PUT: `/novels/{novelId}`

Update existing novel entry

#### Request body:

```
nameEn: string,
nameJa: string,
isHidden: boolean,
categories(optional): array,
tags(optional): array
cover: image file
pages: image files
```

### DELETE: `/novels/{novelId}`

Delete existing novel entry

## Difficulties

- Manipulating and combining multiple lists in desired ways was challenging.
  - Ex) Response from MongoDB + Cloudinary
- Implementation of JWT was more complicated than the classic stateful way using session & cookie.

## Issues & Improvements (as of Aug 5, 2022)

- I named each folder name in cloud storage after `nameEn` instead of `id` thinking it would be visually easy to see on the dashboard,
  which can cause issues when user updates `nameEn`.
- JWT gets stored in memory and included in Authorization header, but it is better to be saved in cookies.
  - This way the user will not need to login each time pages gets reloaded.
- Response headers could be improved in terms of cache settings, security, etc.
- Signup feature has not been added
  - This was omitted as the users will be only two or three people.
