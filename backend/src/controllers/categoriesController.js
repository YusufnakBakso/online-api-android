const CategoriesModel = require("../models/categoriesModel");

const getAllCategories = async (req, res) => {
    try {
        const result = await CategoriesModel.getAllCategories();
        if (result.length === 0) {
            return res.status(404).json({
                "status": 404,
                "message": "Data Kategori Tidak Ditemukan"
            });
        }
        res.status(200).json({
            "status": 200,
            "message": "Berhasil Menampilkan Semua Data Kategori",
            "data": result
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

const getCategoriesById = async (req, res) => {
    try {
        const { id } = req.params;
        const result = await CategoriesModel.getCategoriesById(id);
        if (!result) {
            return res.status(404).json({
                "status": 404,
                "message": "Data Kategori Tidak Ditemukan"
            });
        }
        res.status(200).json({
            "status": 200,
            "message": "Berhasil Menampilkan Data Kategori",
            "data": result
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

const createCategories = async (req, res) => {
    try {
        const { name, description } = req.body;
        const result = await CategoriesModel.createCategories({ name, description });
        res.status(201).json({
            "status": 201,
            "message": "Berhasil Menambahkan Data Kategori",
            "data": result
        });
    } catch (error) {
        if (error.code === "23505") {
            return res.status(409).json({
                "status": 409,
                "message": "Kategori Sudah Terdaftar"
            });
        }
        res.status(500).json({ error: error.message });
    }
};

const updateCategories = async (req, res) => {
    try {
        const { id } = req.params;
        const { name, description } = req.body;
        const result = await CategoriesModel.updateCategories(id, { name, description });
        if (!result) {
            return res.status(404).json({
                "status": 404,
                "message": "Data Kategori Tidak Ditemukan"
            });
        }
        res.status(200).json({
            "status": 200,
            "message": "Berhasil Mengubah Data Kategori",
            "data": result
        });
    } catch (error) {
        if (error.code === "23505") {
            return res.status(409).json({
                "status": 409,
                "message": "Kategori Sudah Terdaftar"
            });
        }
        res.status(500).json({ error: error.message });
    }
};

const deleteCategories = async (req, res) => {
    try {
        const { id } = req.params;
        const result = await CategoriesModel.getCategoriesById(id);
        if (!result) {
            return res.status(404).json({
                "status": 404,
                "message": "Data Kategori Tidak Ditemukan"
            });
        }

        await CategoriesModel.deleteCategories(id);
        res.status(200).json({
            "status": 200,
            "message": "Berhasil Menghapus Data Kategori"
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

module.exports = { getAllCategories, getCategoriesById, createCategories, updateCategories, deleteCategories };
