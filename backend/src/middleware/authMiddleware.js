const jwt = require("jsonwebtoken");

const authMiddleware = (req, res, next) => {
    try {
        const token = req.header("Authorization").replace("Bearer ", "");
        if (!token) {
            throw { error: "Token tidak ditemukan" };
        }
        const decoded = jwt.verify(token, process.env.SECRET_KEY_ACCESS_TOKEN);
        req.users = decoded
        next();
    } catch (error) {
        res.status(401).json({ 
            "status": 401,
            "message" : "Token tidak valid"
         });
    }
};

module.exports = authMiddleware;