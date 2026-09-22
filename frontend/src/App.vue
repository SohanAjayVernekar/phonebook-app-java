<template>
  <!-- =====================================================
       COVER PAGE
  ====================================================== -->

  <div
    v-if="isLandingPage"
    class="landing-wrapper"
  >
    <router-view />
  </div>


  <!-- =====================================================
       APPLICATION
  ====================================================== -->

  <div
    v-else
    class="app-shell"
  >

    <!-- Mobile overlay -->
    <div
      v-if="sidebarOpen"
      class="sidebar-overlay"
      @click="sidebarOpen = false"
    ></div>


    <!-- ===================================================
         SIDEBAR
    ==================================================== -->

    <aside
      class="sidebar"
      :class="{
        'sidebar-open': sidebarOpen
      }"
    >

      <div class="sidebar-top">

        <!-- Brand -->
        <div class="brand">

          <div class="brand-logo">
            <span>☎</span>
          </div>

          <div class="brand-text">
            <h2>
              Connect
            </h2>

            <span>
              Contact Manager
            </span>
          </div>


          <!-- Mobile close -->
          <button
            type="button"
            class="mobile-close"
            @click="
              sidebarOpen = false
            "
            aria-label="Close menu"
          >
            ×
          </button>

        </div>


        <!-- Navigation -->
        <nav class="sidebar-nav">

          <span class="nav-heading">
            MENU
          </span>


          <!-- Contacts -->
          <router-link
            to="/contacts"
            class="nav-item"
            active-class="active"
            @click="
              sidebarOpen = false
            "
          >

            <span class="nav-item-icon">
              ⌂
            </span>

            <span>
              Contacts
            </span>

          </router-link>


          <!-- Add Contact -->
          <router-link
            to="/contacts/new"
            class="nav-item"
            active-class="active"
            @click="
              sidebarOpen = false
            "
          >

            <span class="nav-item-icon">
              ＋
            </span>

            <span>
              Add Contact
            </span>

          </router-link>

        </nav>

      </div>


      <!-- =================================================
           SIDEBAR FOOTER
      ================================================== -->

      <div class="sidebar-bottom">

        <!-- User -->
        <div class="sidebar-user">

          <div class="user-avatar">
            {{ userInitials }}
          </div>

          <div class="user-details">

            <strong>
              {{ currentUserName }}
            </strong>

            <span>
              {{ currentUserEmail }}
            </span>

          </div>

        </div>


        <!-- System Status -->
        <div class="system-status">

          <div class="status-title">

            <span>
              System Status
            </span>

            <span
              class="overall-status"
              :class="
                overallStatusClass
              "
            >
              {{ overallStatusText }}
            </span>

          </div>


          <!-- API -->
          <div class="status-item">

            <span
              class="status-indicator"
              :class="
                apiStatusClass
              "
            ></span>

            <span>
              API
            </span>

            <span class="status-value">
              {{ apiStatusText }}
            </span>

          </div>


          <!-- Database -->
          <div class="status-item">

            <span
              class="status-indicator"
              :class="
                databaseStatusClass
              "
            ></span>

            <span>
              Database
            </span>

            <span class="status-value">
              {{ databaseStatusText }}
            </span>

          </div>

        </div>


        <!-- Logout -->
        <button
          type="button"
          class="logout-button"
          @click="logout"
        >
          <span aria-hidden="true">↪</span>
          <span>Log out</span>
        </button>

      </div>

    </aside>


    <!-- ===================================================
         MAIN CONTENT
    ==================================================== -->

    <main class="main-content">


      <!-- =================================================
           TOPBAR
      ================================================== -->

      <header class="topbar">

        <!-- Mobile menu -->
        <button
          type="button"
          class="mobile-menu"
          @click="
            sidebarOpen = true
          "
          aria-label="Open menu"
        >
          ☰
        </button>


        <!-- Breadcrumb -->
        <div class="topbar-breadcrumb">

          <span>
            PHONEBOOK
          </span>

          <span class="breadcrumb-separator">
            /
          </span>

          <strong>
            {{ currentPageTitle }}
          </strong>

        </div>


        <!-- Add Contact -->
        <router-link
          to="/contacts/new"
          class="topbar-action"
        >

          <span>
            ＋
          </span>

          <span>
            Add Contact
          </span>

        </router-link>

      </header>


      <!-- =================================================
           PAGE CONTENT
      ================================================== -->

      <div class="page-container">

        <router-view />

      </div>

    </main>

  </div>
</template>


<script setup>
import {
  computed,
  onBeforeUnmount,
  onMounted,
  ref,
} from "vue";

import {
  useRoute,
  useRouter,
} from "vue-router";

import {
  useAuthStore,
} from "./stores/authStore";

import api from "./services/api";


/* =========================================================
   ROUTER
========================================================= */

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();


/* =========================================================
   CURRENT USER
========================================================= */

const currentUserName = computed(() => {
  return authStore.user?.name || "User";
});

const currentUserEmail = computed(() => {
  return authStore.user?.email || "Signed-in user";
});

const userInitials = computed(() => {
  const parts = currentUserName.value
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


/* =========================================================
   LOGOUT
========================================================= */

const logout = () => {
  authStore.logout();
  router.push({
    name: "login",
  });
};


/* =========================================================
   LANDING PAGE
========================================================= */

const isLandingPage = computed(() => {
  return ["home", "login", "register"].includes(route.name);
});


/* =========================================================
   SIDEBAR
========================================================= */

const sidebarOpen = ref(false);


/* =========================================================
   CURRENT PAGE TITLE
========================================================= */

const currentPageTitle = computed(() => {

  if (
    route.name === "contact-create"
  ) {
    return "Add Contact";
  }


  if (
    route.name === "contact-detail"
  ) {
    return "Edit Contact";
  }


  if (
    route.name === "contacts"
  ) {
    return "All Contacts";
  }


  return "Contacts";
});


/* =========================================================
   SYSTEM HEALTH
========================================================= */

const apiStatus = ref(
  "checking"
);

const databaseStatus = ref(
  "checking"
);

let healthTimer = null;


/* =========================================================
   HEALTH CHECK
========================================================= */

const checkSystemHealth = async () => {

  try {

    const response = await api.get(
      "/health"
    );


    /* API */

    apiStatus.value =
      response.data.api === "online"
        ? "online"
        : "offline";


    /* Database */

    databaseStatus.value =
      response.data.database === "online"
        ? "online"
        : "offline";

  } catch (error) {

    console.error(
      "Health check failed:",
      error
    );

    apiStatus.value =
      "offline";

    databaseStatus.value =
      "offline";
  }
};


/* =========================================================
   API STATUS CLASS
========================================================= */

const apiStatusClass = computed(() => {

  if (
    apiStatus.value ===
    "online"
  ) {
    return "status-online";
  }


  if (
    apiStatus.value ===
    "offline"
  ) {
    return "status-offline";
  }


  return "status-checking";
});


/* =========================================================
   DATABASE STATUS CLASS
========================================================= */

const databaseStatusClass =
  computed(() => {

    if (
      databaseStatus.value ===
      "online"
    ) {
      return "status-online";
    }


    if (
      databaseStatus.value ===
      "offline"
    ) {
      return "status-offline";
    }


    return "status-checking";
  });


/* =========================================================
   API STATUS TEXT
========================================================= */

const apiStatusText = computed(() => {

  if (
    apiStatus.value ===
    "checking"
  ) {
    return "Checking";
  }


  return apiStatus.value ===
    "online"
    ? "Online"
    : "Offline";
});


/* =========================================================
   DATABASE STATUS TEXT
========================================================= */

const databaseStatusText =
  computed(() => {

    if (
      databaseStatus.value ===
      "checking"
    ) {
      return "Checking";
    }


    return databaseStatus.value ===
      "online"
      ? "Online"
      : "Offline";
  });


/* =========================================================
   OVERALL STATUS CLASS
========================================================= */

const overallStatusClass =
  computed(() => {

    if (
      apiStatus.value ===
        "online" &&
      databaseStatus.value ===
        "online"
    ) {
      return "overall-online";
    }


    if (
      apiStatus.value ===
        "offline" ||
      databaseStatus.value ===
        "offline"
    ) {
      return "overall-offline";
    }


    return "overall-checking";
  });


/* =========================================================
   OVERALL STATUS TEXT
========================================================= */

const overallStatusText =
  computed(() => {

    if (
      apiStatus.value ===
        "online" &&
      databaseStatus.value ===
        "online"
    ) {
      return "Healthy";
    }


    if (
      apiStatus.value ===
        "offline" ||
      databaseStatus.value ===
        "offline"
    ) {
      return "Issue";
    }


    return "Checking";
  });


/* =========================================================
   START HEALTH MONITORING
========================================================= */

onMounted(() => {

  checkSystemHealth();


  /*
   * Recheck every 30 seconds.
   */
  healthTimer =
    setInterval(
      checkSystemHealth,
      30000
    );
});


/* =========================================================
   CLEANUP
========================================================= */

onBeforeUnmount(() => {

  if (healthTimer) {
    clearInterval(
      healthTimer
    );
  }

});
</script>