import { Request, Response } from "express";
import Restaurant from "../models/restaurant";
import cloudinary from "cloudinary";
import mongoose from "mongoose";

const getMyRestaurant = async (req: Request, res: Response) => {
  console.log("inside get rest. the req.userId is", req.userId);
  try {
    const restaurant = await Restaurant.findOne({ user: req.userId });
    console.log("the restaurant is", restaurant);
    if (!restaurant) {
      return res.status(404).json({ message: "Restaurant not found" });
    }
    return res.json(restaurant);
  } catch (error) {
    console.log(error);
    res.status(500).json({ message: "Server Error" });
  }
};

const createMyRestaurant = async (req: Request, res: Response) => {
  console.log("inside create rest. the req.userId is", req.userId);
  try {
    const existingRestaurant = await Restaurant.find({ user: req.userId });
    console.log("the existing restaurant is", existingRestaurant);
    if (existingRestaurant.length > 0) {
      return res.status(409).json({ message: "Restaurant already exists" });
    }
    // const image = req.file as Express.Multer.File;
    // const base64Image = Buffer.from(image.buffer).toString("base64");
    // const dataURI = `data:${image.mimetype};base64,${base64Image}`;
    // const uploadResponse = await cloudinary.v2.uploader.upload(dataURI);
    const imageUrl = await uploadImage(req.file as Express.Multer.File);
    const restaurant = new Restaurant(req.body);
    restaurant.imageUrl = imageUrl;
    restaurant.user = new mongoose.Types.ObjectId(req.userId);
    restaurant.lastUpdated = new Date();
    console.log("the restaurant to be created is", restaurant);
    await restaurant.save();
    res.status(201).send(restaurant);
  } catch (error) {
    console.log("error occured creating restaurant");
    console.log(error);
    res.status(500).json({ message: "Server Error" });
  }
};

const updateMyRestaurant = async (req: Request, res: Response) => {
  console.log("inside update rest. the req.userId is", req.userId);
  try {
    const restaurant = await Restaurant.findOne({ user: req.userId });
    console.log("the restaurant to update is", restaurant);
    if (!restaurant) {
      return res.status(404).json({ message: "Restaurant not found" });
    }

    restaurant.restaurantName = req.body.restaurantName;
    restaurant.city = req.body.city;
    restaurant.country = req.body.country;
    restaurant.deliveryPrice = req.body.deliveryPrice;
    restaurant.estimatedDeliveryTime = req.body.estimatedDeliveryTime;
    restaurant.cuisines = req.body.cuisines;
    restaurant.menuItems = req.body.menuItems;
    restaurant.lastUpdated = new Date();
    console.log("the updated restaurant is", restaurant);
    if (req.file) {
      const imageUrl = await uploadImage(req.file as Express.Multer.File);
      restaurant.imageUrl = imageUrl;
    }
    await restaurant.save();
    res.status(200).send(restaurant);
  } catch (error) {
    console.log("error occured updating restaurant");
    console.log(error);
    res.status(500).json({ message: "Server Error" });
  }
};

const uploadImage = async (file: Express.Multer.File) => {
  const image = file;
  const base64Image = Buffer.from(image.buffer).toString("base64");
  const dataURI = `data:${image.mimetype};base64,${base64Image}`;
  const uploadResponse = await cloudinary.v2.uploader.upload(dataURI);
  return uploadResponse.url;
};

export default {
  getMyRestaurant,
  createMyRestaurant,
  updateMyRestaurant,
};
