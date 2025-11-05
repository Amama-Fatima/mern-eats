import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import DetailsSection from "./DetailsSection";
import { Separator } from "@/components/ui/separator";
import CuisinesSection from "./CuisinesSection";
import MenuSection from "./MenuSection";
import ImageSection from "./ImageSection";
import LoadingButton from "@/components/LoadingButton";
import { Button } from "@/components/ui/button";
import { Form } from "@/components/ui/form";
import { Restaurant } from "@/types";
import { useEffect } from "react";

const formSchema = z
  .object({
    restaurantName: z.string({ required_error: "Name is required" }),
    city: z.string({ required_error: "City is required" }),
    country: z.string({ required_error: "Country is required" }),
    deliveryPrice: z.coerce.number({
      required_error: "Delivery price is required",
      invalid_type_error: "Delivery price must be a number",
    }),
    estimatedDeliveryTime: z.coerce.number({
      required_error: "Estimated delivery time is required",
      invalid_type_error: "Estimated delivery time must be a number",
    }),
    cuisines: z.array(
      z.string().nonempty({ message: "Please select at least one cuisine" })
    ),
    menuItems: z.array(
      z.object({
        name: z.string().min(1, { message: "Name is required" }),
        price: z.coerce.number().min(1, { message: "Price is req" }),
      })
    ),
    imageUrl: z.string().optional(),
    imageFile: z.instanceof(File, { message: "image is required" }).optional(),
  })
  .refine((data) => data.imageUrl || data.imageFile, {
    message: "Either image URL or image File must be provided",
    path: ["imageFile"],
  });
type RestaurantFormData = z.infer<typeof formSchema>;

type Props = {
  restaurant?: Restaurant;
  onSave: (restaurantData: FormData) => void;
  isLoading: boolean;
};

export default function ManageRestaurantForm({
  restaurant,
  onSave,
  isLoading,
}: Props) {
  const form = useForm<RestaurantFormData>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      restaurantName: "",
      city: "",
      country: "",
      deliveryPrice: 0,
      estimatedDeliveryTime: 0,
      cuisines: [],
      menuItems: [{ name: "", price: 0 }],
      imageUrl: "",
    },
  });

  useEffect(() => {
    if (!restaurant) return;
    const deliveryPriceFormatted = parseInt(
      (restaurant.deliveryPrice / 100).toFixed(2)
    );
    const menuItemsFormatted = restaurant.menuItems.map((menuItem) => {
      const priceFormatted = parseInt((menuItem.price / 100).toFixed(2));
      return { ...menuItem, price: priceFormatted };
    });
    const updateRestaurant = {
      ...restaurant,
      deliveryPrice: deliveryPriceFormatted,
      menuItems: menuItemsFormatted,
    };
    form.reset(updateRestaurant);
  }, [form, restaurant]);
  const onSubmit = (data: RestaurantFormData) => {
    const formData = new FormData();
    formData.append("restaurantName", data.restaurantName);
    formData.append("city", data.city);
    formData.append("country", data.country);
    formData.append("deliveryPrice", (data.deliveryPrice * 100).toString());
    formData.append(
      "estimatedDeliveryTime",
      data.estimatedDeliveryTime.toString()
    );
    data.cuisines.forEach((cuisine, index) => {
      formData.append(`cuisines[${index}]`, cuisine);
    });
    data.menuItems.forEach((menuItem, index) => {
      formData.append(`menuItems[${index}][name]`, menuItem.name);
      formData.append(
        `menuItems[${index}][price]`,
        (menuItem.price * 100).toString()
      );
    });
    if (data.imageFile) {
      formData.append("imageFile", data.imageFile);
    }
    onSave(formData);
  };
  return (
    <Form {...form}>
      <form
        className="space-y-8 bg-gray-50 p-10 rounded-lg"
        onSubmit={form.handleSubmit(onSubmit)}
      >
        <DetailsSection />
        <Separator />
        <CuisinesSection />
        <Separator />
        <MenuSection />
        <Separator />
        <ImageSection />
        {isLoading ? <LoadingButton /> : <Button type="submit">Save</Button>}
      </form>
    </Form>
  );
}
