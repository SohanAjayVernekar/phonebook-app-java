import {
  createRouter,
  createWebHistory,
} from "vue-router";


import Home from
  "../views/Home.vue";

import ContactList from
  "../views/ContactList.vue";

import CreateContact from
  "../views/CreateContact.vue";

import ContactDetail from
  "../views/ContactDetail.vue";

import Login from
  "../views/Login.vue";

import Register from
  "../views/Register.vue";


const router = createRouter({

  history: createWebHistory(),


  routes: [

    {
      path: "/",
      name: "home",
      component: Home,
    },


    // =====================================================
    // Login
    // =====================================================

    {
      path: "/login",
      name: "login",
      component: Login,

      meta: {
        guestOnly: true,
      },
    },


    // =====================================================
    // Register
    // =====================================================

    {
      path: "/register",
      name: "register",
      component: Register,

      meta: {
        guestOnly: true,
      },
    },


    // =====================================================
    // Contacts
    // =====================================================

    {
      path: "/contacts",
      name: "contacts",
      component: ContactList,

      meta: {
        requiresAuth: true,
      },
    },


    // =====================================================
    // Create contact
    // =====================================================

    {
      path: "/contacts/new",
      name: "contact-create",
      component: CreateContact,

      meta: {
        requiresAuth: true,
      },
    },


    // =====================================================
    // Contact details
    // =====================================================

    {
      path: "/contacts/:contactId",
      name: "contact-detail",
      component: ContactDetail,

      meta: {
        requiresAuth: true,
      },

      props: (route) => ({
        contactId: Number(
          route.params.contactId
        ),
      }),
    },

  ],

});


// =========================================================
// Authentication guard
// =========================================================

router.beforeEach(
  (to) => {

    const token =
      localStorage.getItem(
        "access_token"
      );


    // -----------------------------------------------------
    // Protected page
    // -----------------------------------------------------

    if (
      to.meta.requiresAuth &&
      !token
    ) {

      return {
        name: "login",

        query: {
          redirect: to.fullPath,
        },

      };

    }


    // -----------------------------------------------------
    // Guest-only page
    // -----------------------------------------------------

    if (
      to.meta.guestOnly &&
      token
    ) {

      return {
        name: "contacts",
      };

    }


    return true;

  }
);


export default router;