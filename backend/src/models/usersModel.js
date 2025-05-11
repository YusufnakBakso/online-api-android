const client = require("../config/db");

const UserModel = {
    loginUser: async (user) => {
        const result = await client.query("SELECT * FROM users WHERE email = $1", [user.email]);
        return result.rows[0];
    },
    getAllUsers: async () => {
        const result = await client.query("SELECT * FROM users ORDER BY id");
        return result.rows;
    }, 
    getUserById: async (id) => {
        const result = await client.query("SELECT * FROM users WHERE id = $1", [id]);
        return result.rows[0];
    },
    createUser: async (user) => {
        try {
            await client.query("BEGIN");
    
            const idResult = await client.query("SELECT nextval('users_id_seq')");
            const nextId = idResult.rows[0].nextval;
    
            const result = await client.query(
                "INSERT INTO users (id, full_name, email, password) VALUES ($1, $2, $3, $4) RETURNING *",
                [nextId, user.full_name, user.email, user.hashedPassword]
            );
    
            await client.query("COMMIT");
            return result.rows[0];

        } catch (error) {
            await client.query("ROLLBACK");
            await client.query("SELECT setval('users_id_seq', (SELECT COALESCE(MAX(id), 1) FROM users))");
            throw error;
        }
    },
    updateUser: async (id, user) => {
        const result = await client.query("UPDATE users SET full_name = $1, email = $2, password = $3 WHERE id = $4 RETURNING *", [user.full_name, user.email, user.password, id]);
        return result.rows[0];
    },
    deleteUser: async (id) => {
        await client.query("DELETE FROM users WHERE id = $1", [id]);
        return { message: "User deleted successfully" };
    }
}

module.exports = UserModel