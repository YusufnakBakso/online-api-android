const client = require("../config/db");

const CategoriesModel = {
    getAllCategories: async () => {
        const result = await client.query("SELECT * FROM categories ORDER BY id");
        return result.rows;
    },

    getCategoriesById: async (id) => {
        const result = await client.query("SELECT * FROM categories WHERE id = $1", [id]);
        return result.rows[0];
    },

    createCategories: async (categories) => {
        try {
            await client.query("BEGIN");

            const idResult = await client.query("SELECT nextval('categories_id_seq')");
            const nextId = idResult.rows[0].nextval;

            const result = await client.query(
                "INSERT INTO categories (id, name, description) VALUES ($1, $2, $3) RETURNING *",
                [nextId, categories.name, categories.description]
            );

            await client.query("COMMIT");
            return result.rows[0];

        } catch (error) {
            await client.query("ROLLBACK");
            await client.query("SELECT setval('categories_id_seq', (SELECT COALESCE(MAX(id), 1) FROM categories))");
            throw error;
        }
    },

    updateCategories: async (id, categories) => {
        const result = await client.query("UPDATE categories SET name = $1, description = $2 WHERE id = $3 RETURNING *", [categories.name, categories.description, id]);
        return result.rows[0];
    },

    deleteCategories: async (id) => {
        const result = await client.query("DELETE FROM categories WHERE id = $1", [id]);
        return result.rows[0];
    }
}
module.exports = CategoriesModel