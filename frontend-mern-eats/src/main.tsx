import React from "react";
import ReactDOM from "react-dom/client";
import "./global.css";
import { BrowserRouter as Router } from "react-router-dom";
import AppRoutes from "./AppRoutes";
import { QueryClient, QueryClientProvider } from "react-query";
import { Toaster } from "sonner";
import { AuthProvider } from "./auth/useAuth";
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
    },
  },
});

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <Router>
      <AuthProvider>
        <QueryClientProvider client={queryClient}>
          <AppRoutes />
          <Toaster visibleToasts={1} position="top-right" richColors />
        </QueryClientProvider>
      </AuthProvider>
    </Router>
  </React.StrictMode>
);
