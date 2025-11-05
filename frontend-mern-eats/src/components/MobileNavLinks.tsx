import { Link } from "react-router-dom";
import { Button } from "./ui/button";
import { useAuth } from "@/auth/useAuth";

export default function MobileNavLinks() {
  const { logout } = useAuth();
  return (
    <>
      <Link
        to="/user-profile"
        className="flex bg-white items-center font-bold hover:text-orange-500"
      >
        User Profile
      </Link>
      <Button
        className="flex items-center px-3 font-bold hover:bg-gray-500"
        onClick={() => logout()}
      >
        Log Out
      </Button>
    </>
  );
}
