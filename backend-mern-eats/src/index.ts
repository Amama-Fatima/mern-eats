import express, { Request, Response } from "express";
import cors from "cors";
import "dotenv/config";
import mongoose from "mongoose";
import myUserRoute from "./routes/MyUserRoute";
import { v2 as cloudinary } from "cloudinary";
import cookieParser from "cookie-parser";
import myRestaurantRoute from "./routes/MyRestaurantRoutes";
import restaurantRoute from "./routes/RestaurantRoutes";
import authRoutes from "./routes/AuthRoutes";

const PORT = process.env.PORT || 7000;

mongoose
  .connect(process.env.MONGODB_CONNECTION_STRING as string)
  .then(() => console.log("connected to db"));

cloudinary.config({
  cloud_name: process.env.CLOUDINARY_CLOUD_NAME,
  api_key: process.env.CLOUDINARY_API_KEY,
  api_secret: process.env.CLOUDINARY_API_SECRET,
});

// Create express server
const app = express();
//adding middleware to automatically convert req of out api to json
app.use(express.json());
app.use(cookieParser());
//adding middleware to allow cross origin requests
app.use(
  cors({
    origin: [
      "https://frontend-mern-eats-app.onrender.com",
      "http://localhost:5173",
      "http://13.127.143.28",
      "http://13.204.193.139",
      "http://3.108.236.217",
    ],
    credentials: true,
  })
);

app.get("/", async (req: Request, res: Response) => {
  res.send({ message: "hello world!" });
});
app.get("/health", async (req: Request, res: Response) => {
  res.send({ message: "health ok!" });
});

app.use("/api/my/user", myUserRoute);
app.use("/api/auth", authRoutes);
app.use("/api/my/restaurant", myRestaurantRoute);
app.use("/api/restaurant", restaurantRoute);

app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
