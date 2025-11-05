import { Restaurant } from "@/types";
import { useMutation, useQuery } from "react-query";
import { toast } from "sonner";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL as string;

export const useGetMyRestaurant = () => {
  const getMyRestaurantRequest = async (): Promise<Restaurant> => {
    const response = await fetch(`${API_BASE_URL}/api/my/restaurant`, {
      credentials: "include", // Sends httpOnly cookie
    });

    if (!response.ok) {
      throw new Error("Failed to fetch restaurant");
    }

    return response.json();
  };

  const { data: restaurant, isLoading } = useQuery(
    "fetchMyRestaurant",
    getMyRestaurantRequest
  );

  return { restaurant, isLoading };
};

export const useCreateMyRestaurant = () => {
  const createMyRestaurantRequest = async (
    restaurantData: FormData
  ): Promise<Restaurant> => {
    const response = await fetch(`${API_BASE_URL}/api/my/restaurant`, {
      method: "POST",
      credentials: "include", // Sends httpOnly cookie
      // Don't set Content-Type for FormData - browser sets it automatically with boundary
      body: restaurantData,
    });

    if (!response.ok) {
      throw new Error("Failed to create restaurant");
    }

    return response.json();
  };

  const {
    mutate: createRestaurant,
    isLoading,
    isSuccess,
    error,
  } = useMutation(createMyRestaurantRequest);

  if (isSuccess) {
    toast.success("Restaurant created successfully");
  }

  if (error) {
    toast.error("Failed to create restaurant");
  }

  return { createRestaurant, isLoading };
};

export const useUpdateMyRestaurant = () => {
  const updateMyRestaurantRequest = async (
    restaurantData: FormData
  ): Promise<Restaurant> => {
    const response = await fetch(`${API_BASE_URL}/api/my/restaurant`, {
      method: "PUT",
      credentials: "include", // Sends httpOnly cookie
      // Don't set Content-Type for FormData - browser sets it automatically with boundary
      body: restaurantData,
    });

    if (!response.ok) {
      throw new Error("Failed to update restaurant");
    }

    return response.json();
  };

  const {
    mutate: updateRestaurant,
    isLoading,
    isSuccess,
    error,
  } = useMutation(updateMyRestaurantRequest);

  if (isSuccess) {
    toast.success("Restaurant updated successfully");
  }

  if (error) {
    toast.error("Failed to update restaurant");
  }

  return { updateRestaurant, isLoading };
};
