import express from "express";
import multer from "multer";
import MyRestaurantController from "../controllers/MyRestaurantController";
import verifyToken from "../middleware/auth";
import { validateMyRestaurantRequest } from "../middleware/validation";

const router = express.Router();

const storage = multer.memoryStorage();
const upload = multer({
  storage: storage,
  limits: {
    fields: 50,
    fileSize: 5 * 1024 * 1024, //5mb
    files: 1,
    parts: 50,
  },
});

router.get("/", verifyToken, MyRestaurantController.getMyRestaurant);

// api/my/restaurant
router.post(
  "/",
  upload.single("imageFile"), //middleware
  validateMyRestaurantRequest,
  verifyToken,
  MyRestaurantController.createMyRestaurant
);

router.put(
  "/",
  upload.single("imageFile"), //middleware
  validateMyRestaurantRequest,
  verifyToken,
  MyRestaurantController.updateMyRestaurant
);

export default router;
