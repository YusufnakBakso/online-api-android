# Library Management System

A modern library management application built with Next.js, Tailwind CSS, and Express.js .

## Project Overview

This application provides a complete solution for library management, including:
- Book catalog management 
- User membership system
- Borrowing and returning books
- Search and filter functionality
- Admin dashboard

## Tech Stack

### Frontend
- Next.js
- Tailwind CSS
- React Query
- Axios

### Backend
- Express.js
- PostgreSQL
- JWT Authentication

## Getting Started

### Prerequisites
- Node.js (v16+)
- npm or yarn
- PostgreSQL

## Frontend Setup

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
# or
yarn install
```

3. Create a `.env.local` file in the frontend root with the following variables:
```
NEXT_PUBLIC_API_URL=http://localhost:5000/api
```

4. Start the development server:
```bash
npm run dev
# or
yarn dev
```

5. Build for production:
```bash
npm run build
# or
yarn build
```

6. Start production server:
```bash
npm run start
# or
yarn start
```

The frontend will be available at `http://localhost:3000`

## Backend Setup

1. Navigate to the backend directory:
```bash
cd backend
```

2. Install dependencies:
```bash
npm install
# or
yarn install
```

3. Create a `.env` file in the backend root by copying the template:
```bash
cp env.copy .env
```

4. Update the `.env` file with your specific configuration:
```
# Server Configuration
PORT=5000
NODE_ENV=development

# Database Configuration
DB_HOST=your_db_host
DB_PORT=your_db_port
DB_USER=your_db_user
DB_PASSWORD=your_db_password
DB_NAME=your_db_name

# JWT secret (jika pakai autentikasi)
SECRET_KEY_ACCESS_TOKEN=your_access_token_secret
SECRET_KEY_REFRESH_TOKEN=your_refresh_token_secret

# API keys (jika perlu)
API_KEY=your_api_key
```

5. Start the development server:
```bash
npm run start
# or
yarn start
```

The backend API will be available at `http://localhost:5000/api`

## API Documentation

### Authentication Endpoints
- `POST /api/login` - Login user
- `POST /api/register` - Register a new user

### Users Endpoints
- `GET /api/users` - Get all users (Admin only)
- `GET /api/users/:id` - Get user by ID
- `PUT /api/users/:id` - Update user information
- `DELETE /api/users/:id` - Delete user (Admin only)

### Authors Endpoints
- `GET /api/authors` - Get all authors
- `GET /api/authors/:id` - Get author by ID
- `POST /api/authors` - Add a new author (Admin only)
- `PUT /api/authors/:id` - Update an author (Admin only)
- `DELETE /api/authors/:id` - Delete an author (Admin only)

### Categories Endpoints
- `GET /api/categories` - Get all categories
- `GET /api/categories/:id` - Get category by ID
- `POST /api/categories` - Add a new category (Admin only)
- `PUT /api/categories/:id` - Update a category (Admin only)
- `DELETE /api/categories/:id` - Delete a category (Admin only)


## Directory Structure

```
.
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── styles/
│   │   ├── hooks/
│   │   ├── contexts/
│   │   └── types/
│   ├── package.json
│   └── tsconfig.json
├── backend/
│   ├── src/
│   │   ├── config/
│   │   ├── controllers/
│   │   ├── middleware/
│   │   ├── models/
│   │   ├── routes/
│   │   ├── utils/
│   │   └── server.js
│   ├── env.copy
│   ├── package.json
│   └── gitignore
└── README.md
```

## Contributing

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin feature/my-new-feature`
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
