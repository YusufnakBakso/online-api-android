const express = require("express");
const cors = require("cors");
const app = express();

app.use(cors());

require("dotenv").config();
const port = process.env.PORT;
const routes = require("./routes/router");

app.use(express.json());

app.use("/api", routes);

app.listen(port, () => {
  console.log(`Server berjalan di http://0.0.0.0:${port}`);
});
