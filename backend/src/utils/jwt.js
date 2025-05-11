const jwt = require("jsonwebtoken");
require("dotenv").config();

const accessToken = (data) => {
    return jwt.sign(
        { id: data.id, name: data.full_name, email: data.email, role: data.role },
        process.env.SECRET_KEY_ACCESS_TOKEN,
        { expiresIn: '2m' }
    );
};

const refreshToken = (data) => {
    return jwt.sign(
        { id: data.id, name: data.full_name, email: data.email, role: data.role },
        process.env.SECRET_KEY_REFRESH_TOKEN,
        { expiresIn: '3m' }
    );
};

module.exports = { accessToken, refreshToken };

