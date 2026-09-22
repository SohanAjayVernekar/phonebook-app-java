<template>
  <div class="login-page">

    <div class="login-shell">

      <main class="login-card">

      <!-- Header -->
      <div class="login-header">

        <h1 class="login-heading">
          Welcome Back
        </h1>

        <p>
          Sign in to manage your contacts
        </p>

      </div>


      <!-- Error -->
      <div
        v-if="authStore.error"
        class="login-error"
      >
        {{ authStore.error }}
      </div>


      <!-- Login Form -->
      <form
        class="login-form"
        @submit.prevent="handleLogin"
      >

        <!-- Email -->
        <div class="login-field">

          <label for="email">
            Email Address
          </label>

          <input
            id="email"
            v-model="email"
            type="email"
            placeholder="Enter your email"
            autocomplete="email"
            required
          />

        </div>


        <!-- Password -->
        <div class="login-field">

          <label
            for="password"
            class="password-label"
          >
            <span>Password</span>

            <button
              type="button"
              class="login-show-password"
              :aria-label="showPassword ? 'Hide password' : 'Show password'"
              @click="showPassword = !showPassword"
            >
              {{ showPassword ? "Hide" : "Show" }}
            </button>

          </label>

          <div class="login-password-wrapper">

            <input
              id="password"
              v-model="password"
              :type="
                showPassword
                  ? 'text'
                  : 'password'
              "
              placeholder="Enter your password"
              autocomplete="current-password"
              required
            />

          </div>

        </div>


        <!-- Submit -->
        <button
          type="submit"
          class="login-submit"
          :disabled="authStore.loading"
        >

          <span
            v-if="authStore.loading"
            class="loading-spinner"
          ></span>

          <span>
            {{
              authStore.loading
                ? "Signing In..."
                : "Sign In"
            }}
          </span>

        </button>

      </form>

      <div class="login-register">
        <span>Don't have an account?</span>

        <button
          type="button"
          @click="goToRegister"
        >
          Create Account
        </button>
      </div>

      </main>

    </div>

  </div>
</template>


<script setup>
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "../stores/authStore";

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();


/* =========================================================
   FORM DATA
========================================================= */

const email = ref("");
const password = ref("");

const showPassword = ref(false);


/* =========================================================
   LOGIN
========================================================= */

const handleLogin = async () => {

  authStore.clearError();

  try {

    await authStore.login(
      email.value,
      password.value
    );

    const redirect =
      route.query.redirect || "/contacts";

    router.push(redirect);

  } catch (error) {

    console.error(
      "Login failed:",
      error
    );

  }
};


/* =========================================================
   REGISTER
========================================================= */

const goToRegister = () => {
  router.push("/register");
};


</script>