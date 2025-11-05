import { Response, Request } from "express";
import User from "../models/user";

const getCurrentUser = async (req: Request, res: Response) => {
  try {
    const currentUser = await User.findOne({ _id: req.userId });
    if (!currentUser) {
      return res.status(404).json({ message: "User not found" });
    }
    return res.json(currentUser.toObject());
  } catch (error) {
    console.log(error);
    return res.status(500).json({ message: "Error getting user" });
  }
};

const createCurrentUser = async (req: Request, res: Response) => {
  try {
    const { email } = req.body;
    const existingUser = await User.findOne({ email });
    if (existingUser) {
      console.log("User already exists");
      return res
        .status(400)
        .json({ message: "User already exists with this email" });
    }
    const newUser = new User(req.body);
    await newUser.save();
    console.log("User created");

    return res.status(201).json(newUser.toObject());
  } catch (error) {
    console.log("ERROR CREATING NEW USER", error);
    return res.status(500).json({ message: "Error creating user" });
  }
};

const updateCurrentUser = async (req: Request, res: Response) => {
  try {
    const { name, addressLine1, country, city } = req.body;
    const user = await User.findById(req.userId);

    if (!user) {
      return res.status(404).json({ message: "User not found" });
    }
    user.name = name;
    user.addressLine1 = addressLine1;
    user.country = country;
    user.city = city;

    await user.save();
    res.send(user.toObject());
  } catch (error) {
    console.log(error);
    res.status(500).json({ message: "Error updating user" });
  }
};

export default {
  getCurrentUser,
  createCurrentUser,
  updateCurrentUser,
};
