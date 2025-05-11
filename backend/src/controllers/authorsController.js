const AuthorModel = require("../models/authorsModel");

const getAllAuthors = async (req, res) => {
    try {
        const data = await AuthorModel.getAllAuthors();
        if (data.length === 0) {
            return res.status(404).json({
                "status": 404,
                "message": "Data Penulis Tidak Ditemukan"
            });
        }
        res.status(200).json({
            "status": 200,
            "message": "Berhasil Menampilkan Semua Data Penulis",
            "data": data
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
}
const getAuthorById = async (req, res) => {
    try {
        const { id } = req.params;
        const data = await AuthorModel.getAuthorById(id);
        if (!data) {
            return res.status(404).json({
                "status": 404,
                "message": "Data Penulis Tidak Ditemukan"
            });
        }
        res.status(200).json({
            "status": 200,
            "message": "Berhasil Menampilkan Data Penulis",
            "data": data
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
}
const createAuthor = async (req, res) => {
    try {
        const { name, bio } = req.body;
        const data = await AuthorModel.createAuthor({ name,bio });
        res.status(201).json({
            "status": 201,
            "message": "Berhasil Menambahkan Data Penulis",
            "data": data
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
}
const updateAuthor = async (req, res) => {
    try {
        const { id } = req.params;
        const { name, bio } = req.body;
        const data = await AuthorModel.updateAuthor(id, { name, bio });
        if (!data) {
            return res.status(404).json({
                "status": 404,
                "message": "Data Penulis Tidak Ditemukan"
            });
        }
        res.status(200).json({
            "status": 200,
            "message": "Berhasil Mengubah Data Penulis",
            "data": data
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
}
const deleteAuthor = async (req, res) => {
    try {
        const { id } = req.params;
        const checAuthor = await AuthorModel.getAuthorById(id);
        if (!checAuthor) {
            return res.status(404).json({
                "status": 404,
                "message": "Data Penulis Tidak Ditemukan"
            });
        }

        await AuthorModel.deleteAuthor(id);
        res.status(200).json({
            "status": 200,
            "message": "Berhasil Menghapus Data Penulis"
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
}

module.exports = { getAllAuthors, getAuthorById, createAuthor, updateAuthor, deleteAuthor };