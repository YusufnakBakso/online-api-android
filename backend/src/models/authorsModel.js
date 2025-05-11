const client = require("../config/db");

const AuthorModel = {
    getAllAuthors: async () => {
        const result = await client.query("SELECT * FROM authors ORDER BY id");        
        return result.rows;
    },
    getAuthorById: async (id) => {
        const result = await client.query("SELECT * FROM authors WHERE id = $1", [id]);        
        return result.rows[0];
    },
    createAuthor: async (author) => {
        const result = await client.query("INSERT INTO authors (name, bio) VALUES ($1, $2) RETURNING *", [author.name, author.bio]);        
        return result.rows[0];
    },
    updateAuthor: async (id, author) => {
        const result = await client.query("UPDATE authors SET name = $1, bio = $2 WHERE id = $3 RETURNING *", [author.name, author.bio, id]);        
        return result.rows[0];
    },
    deleteAuthor: async (id) => {
        const result = await client.query("DELETE FROM authors WHERE id = $1", [id]);        
        return result.rows[0];
    }
}

module.exports = AuthorModel;