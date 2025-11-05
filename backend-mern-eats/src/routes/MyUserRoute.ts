import express from "express";
import MyUserController from "../controllers/MyUserController";
import verifyToken from "../middleware/auth";
import { validateMyUserRequest } from "../middleware/validation";

const router = express.Router();

router.get("/", verifyToken, MyUserController.getCurrentUser);
router.post("/", verifyToken, MyUserController.createCurrentUser);
router.put(
  "/",
  verifyToken,
  validateMyUserRequest,
  validateMyUserRequest,
  MyUserController.updateCurrentUser
);

export default router;
