<template>
  <section class="profile-page">

    <!-- =====================================================
         PAGE HEADING
    ====================================================== -->

    <div class="dashboard-heading">

      <div>
        <span class="section-label">
          ACCOUNT
        </span>

        <h1>
          Profile &amp; settings
        </h1>

        <p>
          Manage your personal details, preferences
          and account security.
        </p>
      </div>


      <div class="dashboard-date">
        <span>
          USER ID
        </span>

        <strong>
          {{ userIdLabel }}
        </strong>
      </div>

    </div>


    <!-- =====================================================
         PROFILE HERO
    ====================================================== -->

    <div class="profile-hero">

      <div class="profile-avatar-xl">
        {{ userInitials }}
      </div>

      <div class="profile-hero-info">
        <h2>
          {{ userName }}
        </h2>

        <span class="profile-hero-email">
          {{ userEmail }}
        </span>
      </div>


      <div class="profile-hero-chips">

        <span class="profile-chip chip-live">
          <i class="chip-dot"></i>
          Active session
        </span>

        <span
          v-if="memberSince !== '—'"
          class="profile-chip"
        >
          Member since {{ memberSince }}
        </span>

      </div>

    </div>


    <!-- =====================================================
         SETTINGS GRID
    ====================================================== -->

    <div class="settings-grid">


      <!-- ===================================================
           EDIT PROFILE
      ==================================================== -->

      <section class="settings-card">

        <div class="settings-card-title">
          <span class="settings-icon indigo">✎</span>

          <div>
            <h2>Edit profile</h2>
            <p>Update the name shown on your account.</p>
          </div>
        </div>


        <form
          class="profile-form"
          @submit.prevent="saveProfile"
        >

          <div
            v-if="profileSuccess"
            class="form-success"
            role="status"
          >
            {{ profileSuccess }}
          </div>

          <div
            v-if="profileError"
            class="form-error"
            role="alert"
          >
            {{ profileError }}
          </div>


          <!-- Full name -->
          <div class="form-group">
            <label for="profile-name">
              Full name
              <span>*</span>
            </label>

            <input
              id="profile-name"
              v-model.trim="profileForm.name"
              type="text"
              maxlength="255"
              autocomplete="name"
              placeholder="Enter your full name"
              required
              @input="profileTouched = true"
            />
          </div>


          <!-- Email (read-only) -->
          <div class="form-group">
            <label for="profile-email">
              Email address
            </label>

            <input
              id="profile-email"
              :value="userEmail"
              type="email"
              readonly
              disabled
            />

            <small class="field-hint">
              Your sign-in address — changing it
              isn&apos;t supported yet.
            </small>
          </div>


          <div class="form-actions">
            <button
              type="button"
              class="secondary-button"
              :disabled="savingProfile"
              @click="resetProfile"
            >
              Reset
            </button>

            <button
              type="submit"
              class="save-button"
              :disabled="savingProfile || !profileDirty"
            >
              {{
                savingProfile
                  ? "Saving…"
                  : "Save changes"
              }}
            </button>
          </div>

        </form>

      </section>


      <!-- ===================================================
           PREFERENCES
      ==================================================== -->

      <section class="settings-card">

        <div class="settings-card-title">
          <span class="settings-icon violet">⚙</span>

          <div>
            <h2>Preferences</h2>
            <p>How contacts are displayed — saved on this device.</p>
          </div>
        </div>


        <!-- Contact view -->
        <div class="setting-row">

          <div class="setting-text">
            <strong>Contact view</strong>
            <span>Grid cards or the data table.</span>
          </div>


          <div
            class="segmented"
            role="group"
            aria-label="Contact view"
          >
            <button
              type="button"
              :class="{ active: viewMode === 'grid' }"
              :aria-pressed="viewMode === 'grid'"
              @click="viewMode = 'grid'"
            >
              ▦ Grid
            </button>

            <button
              type="button"
              :class="{ active: viewMode === 'list' }"
              :aria-pressed="viewMode === 'list'"
              @click="viewMode = 'list'"
            >
              ☰ Table
            </button>
          </div>

        </div>


        <!-- Row density -->
        <div class="setting-row">

          <div class="setting-text">
            <strong>Row density</strong>
            <span>Spacing of table rows and contact cards.</span>
          </div>


          <div
            class="segmented"
            role="group"
            aria-label="Row density"
          >
            <button
              type="button"
              :class="{ active: density === 'comfortable' }"
              :aria-pressed="density === 'comfortable'"
              @click="density = 'comfortable'"
            >
              Comfortable
            </button>

            <button
              type="button"
              :class="{ active: density === 'compact' }"
              :aria-pressed="density === 'compact'"
              @click="density = 'compact'"
            >
              Compact
            </button>
          </div>

        </div>


        <p class="settings-note">
          Changes apply the next time you open
          the contacts list.
        </p>

      </section>


      <!-- ===================================================
           SECURITY
      ==================================================== -->

      <section class="settings-card">

        <div class="settings-card-title">
          <span class="settings-icon blue">🔒</span>

          <div>
            <h2>Change password</h2>
            <p>You&apos;ll stay signed in on this device.</p>
          </div>
        </div>


        <form
          class="password-form"
          @submit.prevent="changePassword"
        >

          <div
            v-if="passwordSuccess"
            class="form-success"
            role="status"
          >
            {{ passwordSuccess }}
          </div>

          <div
            v-if="passwordError"
            class="form-error"
            role="alert"
          >
            {{ passwordError }}
          </div>


          <!-- Current password -->
          <div class="form-group">
            <label for="password-current">
              Current password
              <span>*</span>
            </label>

            <input
              id="password-current"
              v-model="passwordForm.current"
              type="password"
              autocomplete="current-password"
              placeholder="Enter your current password"
              required
            />
          </div>


          <!-- New password -->
          <div class="form-group">
            <label for="password-new">
              New password
              <span>*</span>
            </label>

            <input
              id="password-new"
              v-model="passwordForm.next"
              type="password"
              minlength="8"
              maxlength="128"
              autocomplete="new-password"
              placeholder="At least 8 characters"
              required
            />

            <small class="field-hint">
              Use at least 8 characters.
            </small>
          </div>


          <!-- Confirm password -->
          <div class="form-group">
            <label for="password-confirm">
              Confirm new password
              <span>*</span>
            </label>

            <input
              id="password-confirm"
              v-model="passwordForm.confirm"
              type="password"
              autocomplete="new-password"
              placeholder="Repeat the new password"
              required
            />
          </div>


          <div class="form-actions">
            <button
              type="submit"
              class="save-button"
              :disabled="changingPassword"
            >
              {{
                changingPassword
                  ? "Updating…"
                  : "Update password"
              }}
            </button>
          </div>

        </form>

      </section>


      <!-- ===================================================
           SESSION
      ==================================================== -->

      <section class="settings-card">

        <div class="settings-card-title">
          <span class="settings-icon green">↪</span>

          <div>
            <h2>Session</h2>
            <p>Signed in on this device.</p>
          </div>
        </div>


        <div class="session-list">

          <div class="session-row">
            <span>Signed in as</span>
            <strong>{{ userEmail }}</strong>
          </div>


          <div class="session-row">
            <span>Device</span>
            <strong>Current browser</strong>
          </div>


          <div class="session-row">
            <span>Status</span>

            <strong class="session-status">
              <i class="status-live"></i>
              Active
            </strong>
          </div>

        </div>


        <button
          type="button"
          class="session-logout"
          @click="logout"
        >
          Log out of this device
        </button>

      </section>

    </div>

  </section>
</template>


<script setup>
import {
  computed,
  onMounted,
  reactive,
  ref,
  watch,
} from "vue";

import {
  useRouter,
} from "vue-router";

import {
  useAuthStore,
} from "../stores/authStore";


/* =========================================================
   STORE
======================================================== */

const router = useRouter();
const authStore = useAuthStore();


/* =========================================================
   CURRENT USER
======================================================== */

const user = computed(() => {
  return authStore.user || {};
});


const userName = computed(() => {
  return user.value.name || "User";
});


const userEmail = computed(() => {
  return user.value.email || "—";
});


const userIdLabel = computed(() => {
  return user.value.id
    ? `#${user.value.id}`
    : "—";
});


const userInitials = computed(() => {
  const parts = userName.value
    .trim()
    .split(/\s+/)
    .filter(Boolean);

  if (parts.length === 0) {
    return "U";
  }

  if (parts.length === 1) {
    return parts[0].slice(0, 2).toUpperCase();
  }

  return (
    parts[0][0] +
    parts[parts.length - 1][0]
  ).toUpperCase();
});


const memberSince = computed(() => {
  const value = user.value.created_at;

  if (!value) {
    return "—";
  }

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return "—";
  }

  return date.toLocaleDateString(
    "en-GB",
    {
      day: "numeric",
      month: "short",
      year: "numeric",
    }
  );
});


/* =========================================================
   ERROR HELPER
======================================================== */

const extractError = (error, fallback) => {
  const data = error?.response?.data;

  return (
    data?.detail ||
    data?.message ||
    data?.error ||
    fallback
  );
};


/* =========================================================
   EDIT PROFILE
======================================================== */

const profileForm = reactive({
  name: authStore.user?.name || "",
});

const profileTouched = ref(false);
const profileError = ref("");
const profileSuccess = ref("");
const savingProfile = ref(false);


const profileDirty = computed(() => {
  return profileForm.name !== userName.value;
});


const saveProfile = async () => {
  profileError.value = "";
  profileSuccess.value = "";

  if (profileForm.name.length < 2) {
    profileError.value =
      "Please enter your full name (at least 2 characters).";
    return;
  }

  savingProfile.value = true;

  try {
    await authStore.updateProfile(
      profileForm.name
    );

    profileSuccess.value =
      "Profile updated successfully.";

  } catch (error) {
    profileError.value = extractError(
      error,
      "Could not save your profile."
    );

  } finally {
    savingProfile.value = false;
  }
};


const resetProfile = () => {
  profileForm.name = userName.value;
  profileTouched.value = false;
  profileError.value = "";
  profileSuccess.value = "";
};


/*
 * Keep the form in sync with the server
 * until the user starts editing.
 */
watch(
  () => user.value.name,
  (value) => {
    if (!profileTouched.value) {
      profileForm.name = value || "";
    }
  }
);


/* =========================================================
   PREFERENCES (same keys as the contacts list)
======================================================== */

const readPref = (key, fallback) => {
  return localStorage.getItem(key) || fallback;
};


const writePref = (key, value) => {
  localStorage.setItem(key, value);
};


const viewMode = ref(
  readPref("contacts_view_mode", "grid")
);


const density = ref(
  readPref("contacts_density", "comfortable")
);


watch(
  viewMode,
  (value) => writePref("contacts_view_mode", value)
);


watch(
  density,
  (value) => writePref("contacts_density", value)
);


/* =========================================================
   CHANGE PASSWORD
======================================================== */

const passwordForm = reactive({
  current: "",
  next: "",
  confirm: "",
});


const passwordError = ref("");
const passwordSuccess = ref("");
const changingPassword = ref(false);


const changePassword = async () => {
  passwordError.value = "";
  passwordSuccess.value = "";

  const { current, next, confirm } = passwordForm;


  if (!current || !next || !confirm) {
    passwordError.value =
      "Please fill in all password fields.";
    return;
  }


  if (next.length < 8) {
    passwordError.value =
      "New password must be at least 8 characters.";
    return;
  }


  if (next !== confirm) {
    passwordError.value =
      "New passwords don't match.";
    return;
  }


  if (next === current) {
    passwordError.value =
      "New password must be different from your current one.";
    return;
  }


  changingPassword.value = true;

  try {
    await authStore.changePassword(
      current,
      next
    );

    passwordSuccess.value =
      "Password updated successfully.";

    passwordForm.current = "";
    passwordForm.next = "";
    passwordForm.confirm = "";

  } catch (error) {
    passwordError.value = extractError(
      error,
      "Could not update your password."
    );

  } finally {
    changingPassword.value = false;
  }
};


/* =========================================================
   LOGOUT
======================================================== */

const logout = () => {
  authStore.logout();

  router.push({
    name: "login",
  });
};


/* =========================================================
   LOAD FRESH PROFILE
======================================================== */

onMounted(() => {
  authStore.fetchProfile();
});
</script>
