const express = require("express");
const cors = require("cors");
const dotenv = require("dotenv");
const mongoose = require("mongoose");

dotenv.config();

const app = express();

app.use(cors());
app.use(express.json());

mongoose
  .connect(process.env.MONGODB_URI)
  .then(() => console.log("MongoDB Atlas connected"))
  .catch((error) => console.error("MongoDB connection failed:", error.message));

const UserSchema = new mongoose.Schema({
  username: String,
  email: String,
  password: String,
  interests: [String],
  planName: {
    type: String,
    default: "Free"
  },
  createdAt: {
    type: Date,
    default: Date.now
  }
});

const ResultSchema = new mongoose.Schema({
  username: String,
  topic: String,
  prompt: String,
  response: String,
  createdAt: {
    type: Date,
    default: Date.now
  }
});

const PurchaseSchema = new mongoose.Schema({
  username: String,
  planName: String,
  amount: Number,
  status: String,
  createdAt: {
    type: Date,
    default: Date.now
  }
});

const User = mongoose.model("User", UserSchema);
const Result = mongoose.model("Result", ResultSchema);
const Purchase = mongoose.model("Purchase", PurchaseSchema);

app.post("/signup", async (req, res) => {
  try {
    const { username, email, password, interests } = req.body;

    const user = await User.create({
      username,
      email,
      password,
      interests: interests || []
    });

    res.json({
      message: "User created",
      user
    });
  } catch (error) {
    res.status(500).json({
      message: "Signup failed",
      error: error.message
    });
  }
});

app.post("/login", async (req, res) => {
  try {
    const { username, password } = req.body;

    const user = await User.findOne({ username, password });

    if (!user) {
      return res.status(401).json({
        message: "Invalid login"
      });
    }

    res.json({
      message: "Login successful",
      user
    });
  } catch (error) {
    res.status(500).json({
      message: "Login failed",
      error: error.message
    });
  }
});

app.post("/update-interests", async (req, res) => {
  try {
    const { username, interests } = req.body;

    const user = await User.findOneAndUpdate(
      { username },
      { interests },
      { new: true }
    );

    if (!user) {
      return res.status(404).json({ message: "User not found" });
    }

    res.json({
      message: "Interests updated",
      user
    });
  } catch (error) {
    res.status(500).json({
      message: "Update failed",
      error: error.message
    });
  }
});

app.post("/llm", async (req, res) => {
  try {
    const { prompt, username, topic } = req.body;

    const ollamaResponse = await fetch(process.env.OLLAMA_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        model: process.env.OLLAMA_MODEL,
        prompt,
        stream: false
      })
    });

    const data = await ollamaResponse.json();

    const responseText = data.response || "No response generated.";

    if (username) {
      await Result.create({
        username,
        topic,
        prompt,
        response: responseText
      });
    }

    res.json({
      response: responseText
    });
  } catch (error) {
    res.status(500).json({
      message: "LLM request failed",
      error: error.message
    });
  }
});

app.get("/history/:username", async (req, res) => {
  try {
    const history = await Result.find({
      username: req.params.username
    }).sort({ createdAt: -1 });

    res.json(history);
  } catch (error) {
    res.status(500).json({
      message: "History fetch failed",
      error: error.message
    });
  }
});

app.post("/purchase", async (req, res) => {
  try {
    const { username, planName, amount } = req.body;

    const purchase = await Purchase.create({
      username,
      planName,
      amount,
      status: "SUCCESS"
    });

    await User.findOneAndUpdate(
      { username },
      { planName },
      { new: true }
    );

    res.json({
      message: "Purchase successful",
      purchase
    });
  } catch (error) {
    res.status(500).json({
      message: "Purchase failed",
      error: error.message
    });
  }
});

app.get("/profile/:username", async (req, res) => {
  try {
    const user = await User.findOne({
      username: req.params.username
    });

    if (!user) {
      return res.status(404).json({
        message: "User not found"
      });
    }

    res.json(user);
  } catch (error) {
    res.status(500).json({
      message: "Profile fetch failed",
      error: error.message
    });
  }
});

app.listen(process.env.PORT, () => {
  console.log(`Backend running on port ${process.env.PORT}`);
});