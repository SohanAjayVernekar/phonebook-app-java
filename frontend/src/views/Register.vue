<template>
  <div class="register-page">

    <div class="register-card">

      <!-- Header -->
      <div class="register-header">

        <div class="register-icon">
          👤
        </div>

        <div class="register-brand">
          CONNECT
        </div>

        <h1 class="register-heading">
          Create Account
        </h1>

        <p>
          Create your account to manage your contacts
        </p>

      </div>


      <!-- Error -->
      <div
        v-if="authStore.error"
        class="register-error"
      >
        {{ authStore.error }}
      </div>


      <!-- Register Form -->
      <form
        class="register-form"
        @submit.prevent="handleRegister"
      >

        <!-- Name -->
        <div class="register-field">

          <label for="name">
            Full Name
          </label>

          <input
            id="name"
            v-model="name"
            type="text"
            placeholder="Enter your full name"
            autocomplete="name"
            required
          />

        </div>


        <!-- Email -->
        <div class="register-field">

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
        <div class="register-field">

          <label
            for="password"
            class="register-password-label"
          >
            <span>Password</span>

            <button
              type="button"
              class="register-show-password"
              @click="showPassword = !showPassword"
            >
              {{ showPassword ? "Hide" : "Show" }}
            </button>

          </label>

          <input
            id="password"
            v-model="password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="Create a password"
            autocomplete="new-password"
            minlength="8"
            required
          />

          <small>
            Password must contain at least 8 characters.
          </small>

        </div>


        <!-- Confirm Password -->
        <div class="register-field">

          <label
            for="confirmPassword"
            class="register-password-label"
          >
            <span>Confirm Password</span>

            <button
              type="button"
              class="register-show-password"
              @click="
                showConfirmPassword = !showConfirmPassword
              "
            >
              {{ showConfirmPassword ? "Hide" : "Show" }}
            </button>

          </label>

          <input
            id="confirmPassword"
            v-model="confirmPassword"
            :type="
              showConfirmPassword
                ? 'text'
                : 'password'
            "
            placeholder="Confirm your password"
            autocomplete="new-password"
            minlength="8"
            required
          />

        </div>


        <!-- Submit -->
        <button
          type="submit"
          class="register-submit"
          :disabled="authStore.loading"
        >

          <span
            v-if="authStore.loading"
            class="loading-spinner"
          ></span>

          <span>
            {{
              authStore.loading
                ? "Creating Account..."
                : "Create Account"
            }}
          </span>

        </button>

      </form>


      <!-- Login -->
      <div class="register-login">

        <span>
          Already have an account?
        </span>

        <button
          type="button"
          @click="goToLogin"
        >
          Sign In
        </button>

      </div>

    </div>

  </div>
</template>


<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../stores/authStore";

const router = useRouter();
const authStore = useAuthStore();


/* =========================================================
   FORM DATA
========================================================= */

const name = ref("");
const email = ref("");
const password = ref("");
const confirmPassword = ref("");

const showPassword = ref(false);
const showConfirmPassword = ref(false);


/* =========================================================
   REGISTER
========================================================= */

const handleRegister = async () => {

  authStore.clearError();


  if (password.value !== confirmPassword.value) {
    authStore.error = "Passwords do not match.";
    return;
  }


  if (password.value.length < 8) {
    authStore.error =
      "Password must contain at least 8 characters.";
    return;
  }


  try {

    await authStore.register(
      name.value,
      email.value,
      password.value
    );

    router.push("/contacts");

  } catch (error) {

    console.error(
      "Registration failed:",
      error
    );

  }
};


/* =========================================================
   LOGIN
========================================================= */

const goToLogin = () => {
  router.push("/login");
};
</script>