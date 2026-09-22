<template>
  <section class="contact-list">

    <!-- =====================================================
         DASHBOARD HEADER
    ====================================================== -->

    <div class="dashboard-heading">

      <div>
        <span class="section-label">
          OVERVIEW
        </span>

        <h1>
          All your contacts
        </h1>

        <p>
          Search, organize, and manage your phonebook.
        </p>
      </div>


      <div class="dashboard-date">
        <span>
          CONTACTS
        </span>

        <strong>
          {{ totalContacts }}
        </strong>
      </div>

    </div>


    <!-- =====================================================
         STATISTICS
    ====================================================== -->

    <div class="stats-row">

      <!-- Total contacts -->
      <div class="stat-card">

        <div class="stat-icon blue">
          👥
        </div>

        <div>

          <span class="stat-label">
            TOTAL CONTACTS
          </span>

          <strong>
            {{ totalContacts }}
          </strong>

        </div>

      </div>


      <!-- Current group -->
      <div class="stat-card">

        <div class="stat-icon purple">
          ▦
        </div>

        <div>

          <span class="stat-label">
            CURRENT GROUP
          </span>

          <strong>
            {{ currentGroupLabel }}
          </strong>

        </div>

      </div>


      <!-- Current page -->
      <div class="stat-card">

        <div class="stat-icon green">
          ●
        </div>

        <div>

          <span class="stat-label">
            CURRENT PAGE
          </span>

          <strong>
            {{ currentPage }} / {{ totalPages }}
          </strong>

        </div>

      </div>

    </div>


    <!-- =====================================================
         CONTACT PANEL
    ====================================================== -->

    <div class="contacts-panel">


      <!-- ===================================================
           PANEL HEADER
      ==================================================== -->

      <div class="contacts-toolbar">

        <div>

          <span class="section-label">
            DIRECTORY
          </span>

          <h2>
            {{ currentGroupLabel }}
          </h2>

        </div>


        <div class="contact-total">

          {{ totalContacts }}

          {{
            totalContacts === 1
              ? "contact"
              : "contacts"
          }}

        </div>

      </div>


      <!-- ===================================================
           GROUP TABS
      ==================================================== -->

      <div class="category-tabs">

        <!-- ALL -->
        <button
          type="button"
          class="category-tab all"
          :class="{
            active:
              selectedCategory === null
          }"
          @click="
            selectCategory(null)
          "
        >

          <span>
            ▦
          </span>

          <span>
            All Contacts
          </span>

        </button>


        <!-- WORK -->
        <button
          type="button"
          class="category-tab work"
          :class="{
            active:
              selectedCategory === 'WORK'
          }"
          @click="
            selectCategory('WORK')
          "
        >

          <span>
            ●
          </span>

          <span>
            Work
          </span>

        </button>


        <!-- FAMILY -->
        <button
          type="button"
          class="category-tab family"
          :class="{
            active:
              selectedCategory === 'FAMILY'
          }"
          @click="
            selectCategory('FAMILY')
          "
        >

          <span>
            ●
          </span>

          <span>
            Family
          </span>

        </button>


        <!-- FRIEND -->
        <button
          type="button"
          class="category-tab friend"
          :class="{
            active:
              selectedCategory === 'FRIEND'
          }"
          @click="
            selectCategory('FRIEND')
          "
        >

          <span>
            ●
          </span>

          <span>
            Friend
          </span>

        </button>

      </div>


      <!-- ===================================================
           LIVE SEARCH
      ==================================================== -->

      <div class="search-container">

        <div class="search-box">

          <span class="search-icon">
            ⌕
          </span>


          <input
            v-model="searchInput"
            type="search"
            autocomplete="off"
            placeholder="Search by name or phone number..."
            aria-label="Search contacts"
          />


          <!-- Clear -->
          <button
            v-if="searchInput"
            type="button"
            class="clear-search"
            aria-label="Clear search"
            @click="clearSearch"
          >
            ×
          </button>

        </div>

      </div>


      <!-- ===================================================
           SEARCH INFO
      ==================================================== -->

      <div
        v-if="activeSearch"
        class="search-info"
      >

        <span>

          Searching for

          <strong>
            "{{ activeSearch }}"
          </strong>

          in

          <strong>
            {{ currentGroupLabel }}
          </strong>

        </span>


        <button
          type="button"
          @click="clearSearch"
        >
          Clear
        </button>

      </div>


      <!-- ===================================================
           LOADING
      ==================================================== -->

      <div
        v-if="loading"
        class="message loading-state"
      >

        <div class="loading-spinner"></div>

        <p>
          Loading contacts...
        </p>

      </div>


      <!-- ===================================================
           ERROR
      ==================================================== -->

      <div
        v-else-if="error"
        class="message error"
      >

        <div class="empty-icon">
          !
        </div>


        <h2>
          Unable to load contacts
        </h2>


        <p>
          {{ error }}
        </p>


        <button
          type="button"
          class="primary-button"
          @click="loadContacts"
        >
          Try Again
        </button>

      </div>


      <!-- ===================================================
           EMPTY PHONEBOOK
      ==================================================== -->

      <div
        v-else-if="
          totalContacts === 0 &&
          !activeSearch &&
          selectedCategory === null
        "
        class="message empty"
      >

        <div class="empty-icon">
          👥
        </div>


        <h2>
          Your phonebook is empty
        </h2>


        <p>
          Start building your directory by
          adding your first contact.
        </p>


        <router-link
          to="/contacts/new"
          class="primary-button"
        >
          ＋ Add Contact
        </router-link>

      </div>


      <!-- ===================================================
           EMPTY CATEGORY
      ==================================================== -->

      <div
        v-else-if="
          totalContacts === 0 &&
          !activeSearch &&
          selectedCategory !== null
        "
        class="message empty"
      >

        <div class="empty-icon">
          ▦
        </div>


        <h2>
          No {{ currentGroupLabel }} contacts
        </h2>


        <p>
          There are currently no contacts
          in this group.
        </p>


        <router-link
          to="/contacts/new"
          class="primary-button"
        >
          ＋ Add Contact
        </router-link>

      </div>


      <!-- ===================================================
           NO SEARCH RESULTS
      ==================================================== -->

      <div
        v-else-if="
          contacts.length === 0 &&
          activeSearch
        "
        class="message empty"
      >

        <div class="empty-icon">
          🔍
        </div>


        <h2>
          No matching contacts
        </h2>


        <p>
          No contact was found for

          <strong>
            "{{ activeSearch }}"
          </strong>

          in

          <strong>
            {{ currentGroupLabel }}
          </strong>.
        </p>


        <button
          type="button"
          class="primary-button"
          @click="clearSearch"
        >
          Clear Search
        </button>

      </div>


      <!-- ===================================================
           CONTACT CARDS
      ==================================================== -->

      <div
        v-else
        class="contacts"
      >

        <ContactCard
          v-for="contact in contacts"
          :key="contact.id"
          :contact="contact"
          @contact-deleted="removeContact"
        />

      </div>


      <!-- ===================================================
           PAGINATION
      ==================================================== -->

      <div
        v-if="
          !loading &&
          !error &&
          totalContacts > 0 &&
          totalPages > 1
        "
        class="pagination"
      >


        <!-- Previous -->
        <button
          type="button"
          class="pagination-button"
          :disabled="
            currentPage === 1
          "
          @click="
            goToPage(currentPage - 1)
          "
        >
          ← Previous
        </button>


        <!-- Direct page selector -->
        <div class="page-selector">

          <span>
            Page
          </span>


          <select
            v-model.number="currentPage"
            aria-label="Select page"
            @change="onPageSelect"
          >

            <option
              v-for="page in totalPages"
              :key="page"
              :value="page"
            >
              {{ page }}
            </option>

          </select>


          <span>
            of {{ totalPages }}
          </span>

        </div>


        <!-- Page numbers -->
        <div class="page-numbers">

          <button
            v-for="page in visiblePages"
            :key="page"
            type="button"
            class="page-number"
            :class="{
              active:
                page === currentPage
            }"
            @click="
              goToPage(page)
            "
          >
            {{ page }}
          </button>

        </div>


        <!-- Next -->
        <button
          type="button"
          class="pagination-button"
          :disabled="
            currentPage === totalPages
          "
          @click="
            goToPage(currentPage + 1)
          "
        >
          Next →
        </button>

      </div>


      <!-- ===================================================
           RANGE INFORMATION
      ==================================================== -->

      <div
        v-if="
          !loading &&
          !error &&
          totalContacts > 0
        "
        class="pagination-info"
      >

        Showing

        <strong>
          {{ firstDisplayedContact }}
        </strong>

        -

        <strong>
          {{ lastDisplayedContact }}
        </strong>

        of

        <strong>
          {{ totalContacts }}
        </strong>

        contacts

      </div>

    </div>

  </section>
</template>


<script setup>
import {
  computed,
  onMounted,
  ref,
  watch,
} from "vue";

import api from "../services/api";

import ContactCard from "../components/ContactCard.vue";


/* =========================================================
   STATE
========================================================= */

const contacts = ref([]);

const loading = ref(true);

const error = ref("");

const searchInput = ref("");

const activeSearch = ref("");

const selectedCategory =
  ref(null);

const currentPage = ref(1);


/*
 * Exactly 3 contacts per page.
 */
const pageSize = ref(8);

const totalContacts = ref(0);

const totalPages = ref(1);


/* =========================================================
   GROUP LABEL
========================================================= */

const currentGroupLabel =
  computed(() => {

    if (
      selectedCategory.value ===
      "WORK"
    ) {
      return "Work";
    }


    if (
      selectedCategory.value ===
      "FAMILY"
    ) {
      return "Family";
    }


    if (
      selectedCategory.value ===
      "FRIEND"
    ) {
      return "Friend";
    }


    return "All Contacts";
  });


/* =========================================================
   LOAD CONTACTS
========================================================= */

const loadContacts = async () => {

  loading.value = true;

  error.value = "";


  try {

    const params = {
      page:
        currentPage.value,

      page_size:
        pageSize.value,
    };


    /*
     * Search
     */
    if (
      activeSearch.value
    ) {
      params.search =
        activeSearch.value;
    }


    /*
     * Category
     */
    if (
      selectedCategory.value
    ) {
      params.category =
        selectedCategory.value;
    }


    const response =
      await api.get(
        "/contacts",
        {
          params,
        }
      );


    contacts.value =
      response.data.items || [];


    totalContacts.value =
      Number(
        response.data.total || 0
      );


    totalPages.value =
      Number(
        response.data.total_pages || 1
      );


    /*
     * Keep current page valid.
     */
    if (
      currentPage.value >
      totalPages.value
    ) {

      currentPage.value =
        totalPages.value;

      await loadContacts();

      return;
    }

  } catch (err) {

    console.error(
      "Load contacts error:",
      err
    );


    error.value =
      err?.response?.data?.detail ||
      "Unable to load contacts.";

  } finally {

    loading.value = false;
  }
};


/* =========================================================
   REAL-TIME SEARCH
========================================================= */

watch(
  searchInput,
  (newValue) => {

    const value =
      newValue.trim();


    /*
     * Don't make another request
     * if the value hasn't changed.
     */
    if (
      value ===
      activeSearch.value
    ) {
      return;
    }


    /*
     * Update immediately.
     */
    activeSearch.value =
      value;


    /*
     * Always return to page 1
     * after changing search.
     */
    currentPage.value = 1;


    /*
     * Load matching contacts.
     */
    loadContacts();
  }
);


/* =========================================================
   GROUP SELECTION
========================================================= */

const selectCategory = (
  category
) => {

  selectedCategory.value =
    category;


  /*
   * Every group starts
   * on page 1.
   */
  currentPage.value = 1;


  loadContacts();
};


/* =========================================================
   CLEAR SEARCH
========================================================= */

const clearSearch = () => {

  searchInput.value = "";

  activeSearch.value = "";

  currentPage.value = 1;

  loadContacts();
};


/* =========================================================
   GO TO PAGE
========================================================= */

const goToPage = (
  page
) => {

  if (
    page < 1 ||
    page > totalPages.value
  ) {
    return;
  }


  currentPage.value =
    page;


  loadContacts();


  window.scrollTo({
    top: 0,
    behavior: "smooth",
  });
};


/* =========================================================
   PAGE SELECTOR
========================================================= */

const onPageSelect = () => {

  loadContacts();


  window.scrollTo({
    top: 0,
    behavior: "smooth",
  });
};


/* =========================================================
   VISIBLE PAGE NUMBERS
========================================================= */

const visiblePages =
  computed(() => {

    const pages = [];

    const maxVisiblePages = 5;


    let start = Math.max(
      1,
      currentPage.value - 2
    );


    let end = Math.min(
      totalPages.value,
      start +
        maxVisiblePages -
        1
    );


    if (
      end - start + 1 <
      maxVisiblePages
    ) {

      start = Math.max(
        1,
        end -
          maxVisiblePages +
          1
      );
    }


    for (
      let page = start;
      page <= end;
      page++
    ) {

      pages.push(page);
    }


    return pages;
  });


/* =========================================================
   FIRST DISPLAYED CONTACT
========================================================= */

const firstDisplayedContact =
  computed(() => {

    if (
      totalContacts.value === 0
    ) {
      return 0;
    }


    return (
      (currentPage.value - 1) *
        pageSize.value +
      1
    );
  });


/* =========================================================
   LAST DISPLAYED CONTACT
========================================================= */

const lastDisplayedContact =
  computed(() => {

    return Math.min(
      currentPage.value *
        pageSize.value,
      totalContacts.value
    );
  });


/* =========================================================
   DELETE CONTACT
========================================================= */

const removeContact =
  async () => {

    await loadContacts();


    /*
     * If the current page became
     * empty after deleting the last
     * contact on that page, go back.
     */
    if (
      contacts.value.length === 0 &&
      currentPage.value > 1
    ) {

      currentPage.value -= 1;

      await loadContacts();
    }
  };


/* =========================================================
   EXPOSE
========================================================= */

defineExpose({
  loadContacts,
});


/* =========================================================
   INITIAL LOAD
========================================================= */

onMounted(() => {
  loadContacts();
});
</script>