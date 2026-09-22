import axios from "axios";


const api = axios.create({
  /*
   * Nginx proxies /api/* to FastAPI.
   *
   * Using a relative URL means:
   * - localhost works
   * - ngrok works
   * - no hard-coded backend URL
   */
  baseURL: "/api",

  headers: {
    "Content-Type": "application/json",
  },

  timeout: 10000,
});


// =========================================================
// Add JWT token to every API request
// =========================================================

api.interceptors.request.use(
  (config) => {

    const token =
      localStorage.getItem(
        "access_token"
      );


    if (token) {

      config.headers.Authorization =
        `Bearer ${token}`;

    }


    return config;

  },

  (error) => {

    return Promise.reject(
      error
    );

  }
);


export default api;