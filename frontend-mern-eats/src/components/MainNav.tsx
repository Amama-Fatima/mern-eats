import { Link } from "react-router-dom";
import { Button } from "./ui/button";
import UsernameMenu from "./UsernameMenu";
import { useAuth } from "@/auth/useAuth";

export default function MainNav() {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <span className="flex space-x-2 items-center">
        <div className="h-10 w-20 bg-gray-200 animate-pulse rounded" />
      </span>
    );
  }

  return (
    <span className="flex space-x-2 items-center">
      {isAuthenticated ? (
        <UsernameMenu />
      ) : (
        <Link to="/login">
          <Button
            variant="ghost"
            className="font-bold text-lg hover:text-orange-500 hover:bg-white"
          >
            Log In
          </Button>
        </Link>
      )}
    </span>
  );
}
