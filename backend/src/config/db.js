require('dotenv').config();
const { Pool } = require('pg');

const client = new Pool({   
    user: process.env.DB_USER,
    host: process.env.DB_HOST,
    database: process.env.DB_NAME,
    password: process.env.DB_PASSWORD,
    port: process.env.DB_PORT,
});

try {
    client.connect();
    console.log('Database Connected');
} catch (error) {
    console.log(error);
}
module.exports = client;