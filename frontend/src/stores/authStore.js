import { defineStore } from "pinia";
import api from "../services/api";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: localStorage.getItem("access_token") || null,
    user: JSON.parse(localStorage.getItem("auth_user") || "null"),
    loading: false,
    error: null,
  }),

  getters: {
    isAuthenticated: (state) => Boolean(state.token),
  },

  actions: {

    /* =====================================================
       LOGIN
    ===================================================== */

    async login(email, password) {
      this.loading = true;
      this.error = null;

      try {
        const response = await api.post("/auth/login", {
          email,
          password,
        });

        const data = response.data;

        this.token = data.access_token;

        localStorage.setItem(
          "access_token",
          data.access_token
        );

        this.user = data.user;

        localStorage.setItem(
          "auth_user",
          JSON.stringify(data.user)
        );

        return data;

      } catch (error) {

        if (
          error.response &&
          error.response.data
        ) {
          this.error =
            error.response.data.detail ||
            "Login failed";
        } else {
          this.error =
            "Unable to connect to the server";
        }

        throw error;

      } finally {
        this.loading = false;
      }
    },


    /* =====================================================
       REGISTER
    ===================================================== */

    async register(name, email, password) {
      this.loading = true;
      this.error = null;

      try {
        const response = await api.post(
          "/auth/register",
          {
            name,
            email,
            password,
          }
        );

        const data = response.data;

        /*
         * The backend registration endpoint
         * returns a token and user.
         */

        this.token = data.access_token;

        localStorage.setItem(
          "access_token",
          data.access_token
        );

        this.user = data.user;

        localStorage.setItem(
          "auth_user",
          JSON.stringify(data.user)
        );

        return data;

      } catch (error) {

        if (
          error.response &&
          error.response.data
        ) {
          this.error =
            error.response.data.detail ||
            "Registration failed";
        } else {
          this.error =
            "Unable to connect to the server";
        }

        throw error;

      } finally {
        this.loading = false;
      }
    },


    /* =====================================================
       LOGOUT
    ===================================================== */

    logout() {
      this.token = null;
      this.user = null;
      this.error = null;

      localStorage.removeItem(
        "access_token"
      );

      localStorage.removeItem(
        "auth_user"
      );
    },


    /* =====================================================
       CLEAR ERROR
    ===================================================== */

    clearError() {
      this.error = null;
    },

  },
});