import { useMutation } from "react-query";
import { toast } from "sonner";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL as string;

type LoginRequest = {
  email: string;
  password: string;
};

type RegisterRequest = {
  email: string;
  password: string;
  name: string;
};

type AuthResponse = {
  userId: string;
  email: string;
  name: string;
};

export const useLogin = () => {
  const loginRequest = async (
    credentials: LoginRequest
  ): Promise<AuthResponse> => {
    const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(credentials),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || "Failed to login");
    }

    return response.json();
  };

  const {
    mutateAsync: login,
    isLoading,
    isError,
    isSuccess,
    error,
  } = useMutation(loginRequest);

  if (isSuccess) {
    toast.success("Login successful");
  }

  if (error) {
    toast.error(error instanceof Error ? error.message : "Login failed");
  }

  return { login, isLoading, isError, isSuccess };
};

export const useRegister = () => {
  const registerRequest = async (
    userData: RegisterRequest
  ): Promise<AuthResponse> => {
    const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(userData),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || "Failed to register");
    }

    return response.json();
  };

  const {
    mutateAsync: register,
    isLoading,
    isError,
    isSuccess,
    error,
  } = useMutation(registerRequest);

  if (isSuccess) {
    toast.success("Account created successfully");
  }

  if (error) {
    toast.error(error instanceof Error ? error.message : "Registration failed");
  }

  return { register, isLoading, isError, isSuccess };
};

export const useLogout = () => {
  const logoutRequest = async (): Promise<void> => {
    const response = await fetch(`${API_BASE_URL}/api/auth/logout`, {
      method: "POST",
      credentials: "include",
    });

    if (!response.ok) {
      throw new Error("Failed to logout");
    }
  };

  const {
    mutateAsync: logout,
    isLoading,
    isError,
    isSuccess,
    error,
  } = useMutation(logoutRequest);

  if (isSuccess) {
    toast.success("Logged out successfully");
  }

  if (error) {
    toast.error("Failed to logout");
  }

  return { logout, isLoading, isError, isSuccess };
};

export const useValidateToken = () => {
  const validateTokenRequest = async (): Promise<AuthResponse> => {
    const response = await fetch(`${API_BASE_URL}/api/auth/validate-token`, {
      credentials: "include",
    });

    if (!response.ok) {
      throw new Error("Invalid token");
    }

    return response.json();
  };

  const {
    mutateAsync: validateToken,
    isLoading,
    isError,
    data,
  } = useMutation(validateTokenRequest);

  return { validateToken, isLoading, isError, userData: data };
};
