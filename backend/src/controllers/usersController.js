const UserModel = require("../models/usersModel");
const bycrpt = require("bcrypt");
const jwt = require("jsonwebtoken");
const {accessToken, refreshToken} = require("../utils/jwt");

require('dotenv').config();

const loginUser = async (req, res) => {
    try {
        const { email, password } = req.body;
        const data = await UserModel.loginUser({email});
        if (!data) {
            return res.status(404).json({
                "status": 404,
                "message": "Data User Tidak Ditemukan"
            });
        }
        const isMatch = await bycrpt.compare(password, data.password);
        if (!isMatch) {
            return res.status(401).json({
                "status": 401,
                "message": "Password Salah"
            });
        }
        const token = accessToken(data);
        const refresh = refreshToken(data);
        res.status(200).json({
            "status": 200,
            "message": "Berhasil Menampilkan Data User",
            "data": data,
            "accessToken": token,
            "refreshToken": refresh
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

const createUser = async (req, res) => {
    try {
        const { full_name, email, password } = req.body;
        const hashedPassword = await bycrpt.hash(password, 10);
        const data = await UserModel.createUser({ full_name, email, hashedPassword });

        res.status(201).json({
            "status": 201,
            "message": "Berhasil Menambahkan Data User",
            "data": data,

        });
    } catch (error) {
        if (error.code === "23505") {
            return res.status(409).json({
                "status": 409,
                "message": "Email Sudah Terdaftar"
            });
        }
        res.status(500).json({ error: error.message });
    }
}

const getAllUsers = async (req, res) => {
    try {
        const data = await UserModel.getAllUsers();
        if (data.length === 0) {
            return res.status(404).json({
                "status": 404,
                "message": "Data User Tidak Ditemukan"
            });
        }
        res.status(200).json({
            "status": 200,
            "message": "Berhasil Menampilkan Semua Data Users",
            "data": data
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

const getUserById = async (req, res) => {
    try {
        const { id } = req.params;
        const data = await UserModel.getUserById(id);
        if (!data) {
            return res.status(404).json({
                "status": 404,
                "message": "Data User Tidak Ditemukan"
            });
        }
        res.status(200).json({
            "status": 200,
            "message": "Berhasil Menampilkan Data User",
            "data": data
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

const updateUser = async (req, res) => {
    try {
        const { id } = req.params;
        const { full_name, email, password } = req.body;
        const data = await UserModel.updateUser(id, { full_name, email, password });
        if (!data) {
            return res.status(404).json({
                "status": 404,
                "message": "Data User Tidak Ditemukan"
            });
        }
        res.status(200).json({
            "status": 200,
            "message": "Berhasil Mengubah Data User",
            "data": data
        });
    } catch (error) {
        if (error.code === "23505") {
            return res.status(409).json({
                "status": 409,
                "message": "Email Sudah Terdaftar"
            });
        }
        res.status(500).json({ error: error.message });
    }
};

const deleteUser = async (req, res) => {
    try {
        const { id } = req.params;
        const checkUser = await UserModel.getUserById(id);
        if (!checkUser) {
            return res.status(404).json({
                "status": 404,
                "message": "Data User Tidak Ditemukan"
            });
        }

        await UserModel.deleteUser(id);
        res.status(200).json({
            "status": 200,
            "message": "Berhasil Menghapus Data User"
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

const newAccesToken = (req, res) => {
    const { refreshToken } = req.body;

    // Perbaikan: cek jika refreshToken TIDAK ADA
    if (!refreshToken) {
        return res.status(401).json({ message: "Token tidak ditemukan" });
    }

    jwt.verify(refreshToken, process.env.SECRET_KEY_REFRESH_TOKEN, (err, user) => {
        if (err) {
            return res.status(403).json({ message: "Refresh token tidak valid." });
        }

        // Pastikan data 'user' di-refresh token cukup untuk generate access token baru
        const newAccessToken = accessToken(user);

        return res.status(200).json({
            message: "Access token berhasil dibuat.",
            accessToken: newAccessToken
        });
    });
};

module.exports = {
    loginUser,
    getAllUsers,
    getUserById,
    createUser,
    updateUser,
    deleteUser,
    newAccesToken,
};