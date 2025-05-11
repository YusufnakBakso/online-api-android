const express = require("express");
const router = express.Router();

const UserController = require("../controllers/usersController");
const AuthorController = require("../controllers/authorsController");
const CategoryController = require("../controllers/categoriesController");

const authMiddleware = require("../middleware/authMiddleware");

router.post("/login", UserController.loginUser);
router.post("/register", UserController.createUser);
router.post("/token-refresh", UserController.newAccesToken)

router.get("/users",authMiddleware, UserController.getAllUsers);
router.get("/users/:id", authMiddleware, UserController.getUserById);
router.put("/users/:id", authMiddleware, UserController.updateUser);
router.delete("/users/:id", authMiddleware , UserController.deleteUser);

router.get("/authors", authMiddleware, AuthorController.getAllAuthors);
router.get("/authors/:id", authMiddleware, AuthorController.getAuthorById);
router.post("/authors", authMiddleware, AuthorController.createAuthor);
router.put("/authors/:id", authMiddleware, AuthorController.updateAuthor);
router.delete("/authors/:id", authMiddleware, AuthorController.deleteAuthor);

router.get("/categories", authMiddleware, CategoryController.getAllCategories);
router.get("/categories/:id", authMiddleware, CategoryController.getCategoriesById);
router.post("/categories", authMiddleware, CategoryController.createCategories);
router.put("/categories/:id", authMiddleware, CategoryController.updateCategories);
router.delete("/categories/:id", authMiddleware, CategoryController.deleteCategories);

module.exports = router;