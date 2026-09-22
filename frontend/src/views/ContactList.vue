<template>
  <section
    class="contact-list"
    :class="'density-' + density"
  >

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
           LIST CONTROLS
           View toggle · Sort · Density · Filters
      ==================================================== -->

      <div class="list-controls">

        <!-- Grid / List -->
        <div
          class="view-toggle"
          role="group"
          aria-label="Switch view"
        >

          <button
            type="button"
            :class="{ active: viewMode === 'grid' }"
            title="Grid view"
            aria-label="Grid view"
            @click="viewMode = 'grid'"
          >
            ▦
          </button>

          <button
            type="button"
            :class="{ active: viewMode === 'list' }"
            title="Table view"
            aria-label="Table view"
            @click="viewMode = 'list'"
          >
            ☰
          </button>

        </div>


        <!-- Sort -->
        <label class="control-field">
          <span>Sort</span>

          <select
            v-model="sortBy"
            aria-label="Sort contacts"
            @change="onListOptionChange"
          >
            <option value="newest">
              Newest first
            </option>

            <option value="oldest">
              Oldest first
            </option>

            <option value="name_asc">
              Name A – Z
            </option>

            <option value="name_desc">
              Name Z – A
            </option>
          </select>
        </label>


        <!-- Density -->
        <label class="control-field">
          <span>Density</span>

          <select
            v-model="density"
            aria-label="Row density"
          >
            <option value="comfortable">
              Comfortable
            </option>

            <option value="compact">
              Compact
            </option>
          </select>
        </label>


        <!-- Filters -->
        <button
          type="button"
          class="filter-toggle"
          :class="{
            active:
              filtersOpen ||
              activeFilterCount > 0
          }"
          :aria-expanded="filtersOpen"
          @click="filtersOpen = !filtersOpen"
        >
          <span>⚑</span>

          <span>Filters</span>

          <span
            v-if="activeFilterCount > 0"
            class="filter-count"
          >
            {{ activeFilterCount }}
          </span>
        </button>

      </div>


      <!-- ===================================================
           GROUP TABS
      ==================================================== -->

      <div class="category-tabs">

        <!-- ALL -->
        <button
          type="button"
          class="category-tab all"
          :class="{ active: isTabActive(null) }"
          @click="selectCategory(null)"
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
          :class="{ active: isTabActive('WORK') }"
          @click="selectCategory('WORK')"
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
          :class="{ active: isTabActive('FAMILY') }"
          @click="selectCategory('FAMILY')"
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
          :class="{ active: isTabActive('FRIEND') }"
          @click="selectCategory('FRIEND')"
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
           BULK ACTIONS
      ==================================================== -->

      <div
        v-if="selectedCount > 0"
        class="bulk-bar"
        role="region"
        aria-label="Bulk actions"
      >

        <strong>
          {{ selectedCount }}
          {{ selectedCount === 1 ? "contact" : "contacts" }}
          selected
        </strong>

        <button type="button" @click="toggleSelectPage">
          {{ allPageSelected ? "Unselect page" : "Select page" }}
        </button>

        <button
          type="button"
          class="bulk-delete"
          :disabled="bulkDeleting"
          @click="deleteSelected"
        >
          {{ bulkDeleting ? "Deleting..." : "Delete selected" }}
        </button>

        <button type="button" @click="clearSelection">
          Clear
        </button>

      </div>


      <!-- ===================================================
           LIVE SEARCH (debounced)
      ==================================================== -->

      <div class="search-container">

        <div class="search-box">

          <span class="search-icon">
            ⌕
          </span>


          <input
            ref="searchRef"
            v-model="searchInput"
            type="search"
            autocomplete="off"
            placeholder="Search by name or phone number...   ( press / )"
            aria-label="Search contacts"
            @focus="showRecent = true"
            @blur="hideRecentSoon"
            @keydown.esc="onSearchEscape"
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


          <!-- Recent searches -->
          <div
            v-if="showRecent && recentList.length > 0"
            class="recent-dropdown"
          >

            <div class="recent-head">
              <span>Recent searches</span>

              <button
                type="button"
                @mousedown.prevent="clearRecent"
              >
                Clear
              </button>
            </div>

            <button
              v-for="term in recentList"
              :key="term"
              type="button"
              class="recent-item"
              @mousedown.prevent="applyRecent(term)"
            >
              <span class="recent-icon">↻</span>

              <span>{{ term }}</span>
            </button>

          </div>

        </div>

      </div>


      <!-- ===================================================
           RESULTS AREA
      ==================================================== -->

      <div
        class="contacts-layout"
        :class="{ 'has-filters': filtersOpen }"
      >


        <!-- ==============================================
             FILTER SIDEBAR
        =============================================== -->

        <aside
          v-show="filtersOpen"
          class="filter-sidebar"
          aria-label="Contact filters"
        >

          <div class="filter-sidebar-head">

            <span>
              FILTERS
            </span>

            <button
              type="button"
              @click="clearFilters"
            >
              Clear all
            </button>

          </div>


          <!-- Categories (multi-select) -->
          <div class="filter-group">

            <span class="filter-group-label">
              CATEGORIES
            </span>

            <label
              v-for="option in CATEGORY_OPTIONS"
              :key="option.value"
              class="filter-check"
            >
              <input
                type="checkbox"
                :checked="selectedCategories.includes(option.value)"
                @change="toggleCategoryFilter(option.value)"
              />

              <span>{{ option.label }}</span>
            </label>

          </div>


          <!-- Has email -->
          <div class="filter-group">

            <span class="filter-group-label">
              EMAIL
            </span>

            <select
              v-model="hasEmail"
              aria-label="Filter by email"
              @change="onListOptionChange"
            >
              <option value="">
                Any
              </option>

              <option value="true">
                Has email
              </option>

              <option value="false">
                No email
              </option>
            </select>

          </div>


          <!-- Added between -->
          <div class="filter-group">

            <span class="filter-group-label">
              ADDED BETWEEN
            </span>

            <input
              v-model="dateFrom"
              type="date"
              aria-label="Added from date"
              @change="onListOptionChange"
            />

            <input
              v-model="dateTo"
              type="date"
              aria-label="Added to date"
              @change="onListOptionChange"
            />

          </div>

        </aside>


        <!-- ==============================================
             RESULTS
        =============================================== -->

        <div class="contacts-main">


          <!-- SEARCH INFO -->
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


          <!-- LOADING (skeleton loaders) -->
          <template v-if="loading">


            <!-- Table skeleton -->
            <div
              v-if="viewMode === 'list'"
              class="contact-table-wrap"
              role="status"
              aria-label="Loading contacts"
            >

              <table class="contact-table">

                <colgroup>
                  <col class="w-select" />
                  <col class="w-name" />
                  <col class="w-phone" />
                  <col class="w-email" />
                  <col class="w-category" />
                  <col class="w-address" />
                  <col class="w-actions" />
                </colgroup>

                <thead>
                  <tr>
                    <th
                      class="col-select"
                      scope="col"
                    ></th>

                    <th
                      class="col-name"
                      scope="col"
                    >
                      Name
                    </th>

                    <th
                      class="col-phone"
                      scope="col"
                    >
                      Phone Number
                    </th>

                    <th
                      class="col-email"
                      scope="col"
                    >
                      Email Address
                    </th>

                    <th
                      class="col-category"
                      scope="col"
                    >
                      Category
                    </th>

                    <th
                      class="col-address"
                      scope="col"
                    >
                      Address
                    </th>

                    <th
                      class="col-actions"
                      scope="col"
                    >
                      Actions
                    </th>
                  </tr>
                </thead>

                <tbody>
                  <tr
                    v-for="n in skeletonCount"
                    :key="'table-skeleton-' + n"
                    class="contact-table-row skeleton-row"
                  >

                    <td>
                      <span class="skeleton-shimmer skeleton-box"></span>
                    </td>

                    <td>
                      <span class="skeleton-shimmer skeleton-line w-70"></span>
                    </td>

                    <td>
                      <span class="skeleton-shimmer skeleton-line w-60"></span>
                    </td>

                    <td>
                      <span class="skeleton-shimmer skeleton-line w-80"></span>
                    </td>

                    <td>
                      <span class="skeleton-shimmer skeleton-pill"></span>
                    </td>

                    <td>
                      <span class="skeleton-shimmer skeleton-line w-70"></span>
                    </td>

                    <td>
                      <span class="skeleton-shimmer skeleton-line w-60"></span>
                    </td>

                  </tr>
                </tbody>

              </table>

            </div>


            <!-- Card skeleton -->
            <div
              v-else
              class="contacts skeleton-grid"
              role="status"
              aria-label="Loading contacts"
            >

              <div
                v-for="n in skeletonCount"
                :key="'skeleton-' + n"
                class="contact-card-modern skeleton-card grid-mode"
              >

                <span class="skeleton-shimmer skeleton-avatar"></span>

                <div class="skeleton-lines">
                  <span class="skeleton-shimmer skeleton-line w-60"></span>
                  <span class="skeleton-shimmer skeleton-line w-35"></span>
                  <span class="skeleton-shimmer skeleton-line w-80"></span>
                </div>

              </div>

            </div>


          </template>


          <!-- ERROR -->
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


          <!-- EMPTY PHONEBOOK -->
          <div
            v-else-if="
              totalContacts === 0 &&
              !activeSearch &&
              activeFilterCount === 0
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


          <!-- EMPTY CATEGORY -->
          <div
            v-else-if="
              totalContacts === 0 &&
              !activeSearch &&
              categoryOnlyFilter
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


          <!-- NO SEARCH / FILTER RESULTS -->
          <div
            v-else-if="
              contacts.length === 0 &&
              (activeSearch || activeFilterCount > 0)
            "
            class="message empty"
          >

            <div class="empty-icon">
              🔍
            </div>


            <h2>
              No matching contacts
            </h2>


            <p v-if="activeSearch">

              No contact was found for

              <strong>
                "{{ activeSearch }}"
              </strong>

              in

              <strong>
                {{ currentGroupLabel }}
              </strong>.
            </p>

            <p v-else>
              No contacts match the current
              filters in

              <strong>
                {{ currentGroupLabel }}
              </strong>.
            </p>


            <button
              type="button"
              class="primary-button"
              @click="activeSearch ? clearSearch() : clearFilters()"
            >
              {{ activeSearch ? "Clear Search" : "Clear Filters" }}
            </button>

          </div>


          <!-- ===================================================
               CONTACT TABLE (list) / CARDS (grid)
               (virtual scrolling window below)
          ==================================================== -->

          <div
            v-else
            ref="frameRef"
            class="contacts-frame"
            @keydown.down.prevent="focusCardAt(focusedIndex + 1)"
            @keydown.up.prevent="focusCardAt(focusedIndex - 1)"
          >

            <div
              v-if="virtual.top > 0"
              class="virtual-spacer"
              :style="{ height: virtual.top + 'px' }"
            ></div>


            <!-- ==============================================
                 TABLE VIEW
                 Fixed layout keeps every column aligned,
                 Phone Number always sits in column 2.
            =============================================== -->

            <div
              v-if="viewMode === 'list'"
              class="contact-table-wrap"
            >

              <table class="contact-table">

                <colgroup>
                  <col class="w-select" />
                  <col class="w-name" />
                  <col class="w-phone" />
                  <col class="w-email" />
                  <col class="w-category" />
                  <col class="w-address" />
                  <col class="w-actions" />
                </colgroup>

                <thead>
                  <tr>

                    <th
                      class="col-select"
                      scope="col"
                    >
                      <label
                        class="table-checkbox"
                        title="Select all contacts on this page"
                      >
                        <input
                          type="checkbox"
                          :checked="allPageSelected"
                          aria-label="Select all contacts on this page"
                          @change="toggleSelectPage"
                        />
                      </label>
                    </th>

                    <th
                      class="col-name"
                      scope="col"
                    >
                      Name
                    </th>

                    <th
                      class="col-phone"
                      scope="col"
                    >
                      Phone Number
                    </th>

                    <th
                      class="col-email"
                      scope="col"
                    >
                      Email Address
                    </th>

                    <th
                      class="col-category"
                      scope="col"
                    >
                      Category
                    </th>

                    <th
                      class="col-address"
                      scope="col"
                    >
                      Address
                    </th>

                    <th
                      class="col-actions"
                      scope="col"
                    >
                      Actions
                    </th>

                  </tr>
                </thead>

                <tbody>
                  <ContactTableRow
                    v-for="(contact, index) in visibleContacts"
                    :key="contact.id"
                    :contact="contact"
                    :selected="isContactSelected(contact.id)"
                    :focused="fullIndex(index) === focusedIndex"
                    @item-focus="onItemFocus(fullIndex(index))"
                    @open="openContact(contact)"
                    @toggle-select="toggleSelect(contact.id)"
                    @contact-deleted="removeContact"
                  />
                </tbody>

              </table>

            </div>


            <!-- ==============================================
                 GRID VIEW
            =============================================== -->

            <div
              v-else
              class="contacts"
            >

              <ContactCard
                v-for="(contact, index) in visibleContacts"
                :key="contact.id"
                :contact="contact"
                :mode="viewMode"
                :selectable="true"
                :selected="isContactSelected(contact.id)"
                :focused="fullIndex(index) === focusedIndex"
                @item-focus="onItemFocus(fullIndex(index))"
                @open="openContact(contact)"
                @toggle-select="toggleSelect(contact.id)"
                @contact-deleted="removeContact"
              />

            </div>

            <div
              v-if="virtual.bottom > 0"
              class="virtual-spacer"
              :style="{ height: virtual.bottom + 'px' }"
            ></div>

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

      </div>

    </div>

  </section>
</template>


<script setup>
import {
  computed,
  nextTick,
  onBeforeUnmount,
  onMounted,
  reactive,
  ref,
  watch,
} from "vue";

import { useRouter } from "vue-router";

import api from "../services/api";

import ContactCard from "../components/ContactCard.vue";

import ContactTableRow from "../components/ContactTableRow.vue";


const router = useRouter();


/* =========================================================
   STATE
========================================================= */

const contacts = ref([]);

const loading = ref(true);

const error = ref("");

const searchInput = ref("");

const activeSearch = ref("");

const currentPage = ref(1);


/*
 * Exactly 3 contacts per page.
 */
const pageSize = ref(8);

const totalContacts = ref(0);

const totalPages = ref(1);


/* =========================================================
   VIEW MODE · DENSITY · SORT
   (persisted like Google keeps your preferences)
========================================================= */

const CATEGORY_OPTIONS = [
  { value: "WORK", label: "Work" },
  { value: "FAMILY", label: "Family" },
  { value: "FRIEND", label: "Friend" },
];

const CATEGORY_LABELS = {
  WORK: "Work",
  FAMILY: "Family",
  FRIEND: "Friend",
};

const readPref = (key, fallback) => {
  try {
    return localStorage.getItem(key) || fallback;
  } catch {
    return fallback;
  }
};

const writePref = (key, value) => {
  try {
    localStorage.setItem(key, value);
  } catch {
    /* preferences are optional */
  }
};

const viewMode = ref(readPref("contacts_view_mode", "grid"));

const density = ref(readPref("contacts_density", "comfortable"));

const sortBy = ref(readPref("contacts_sort", "newest"));

watch(viewMode, (value) => writePref("contacts_view_mode", value));

watch(density, (value) => writePref("contacts_density", value));

watch(sortBy, (value) => writePref("contacts_sort", value));


/* =========================================================
   FILTERS (sidebar)
========================================================= */

const filtersOpen = ref(false);

const selectedCategories = ref([]);

const hasEmail = ref("");

const dateFrom = ref("");

const dateTo = ref("");

const activeFilterCount = computed(() => {
  return (
    selectedCategories.value.length +
    (hasEmail.value ? 1 : 0) +
    (dateFrom.value ? 1 : 0) +
    (dateTo.value ? 1 : 0)
  );
});


/*
 * Filters made of categories only — used for
 * the dedicated "empty group" message.
 */
const categoryOnlyFilter = computed(() => {
  return (
    selectedCategories.value.length > 0 &&
    !hasEmail.value &&
    !dateFrom.value &&
    !dateTo.value
  );
});


/* =========================================================
   GROUP LABEL
========================================================= */

const currentGroupLabel =
  computed(() => {

    const picked = selectedCategories.value;

    if (picked.length === 0) {
      return "All Contacts";
    }

    if (picked.length === 1) {
      return CATEGORY_LABELS[picked[0]] || picked[0];
    }

    return `${picked.length} Categories`;
  });


const isTabActive = (category) => {
  if (category === null) {
    return selectedCategories.value.length === 0;
  }

  return selectedCategories.value.includes(category);
};


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

      sort:
        sortBy.value,
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
     * Categories (one tab or many sidebar boxes)
     */
    if (selectedCategories.value.length === 1) {
      params.category =
        selectedCategories.value[0];
    } else if (selectedCategories.value.length > 1) {
      params.categories =
        selectedCategories.value.join(",");
    }


    /*
     * Sidebar filters
     */
    if (hasEmail.value) {
      params.has_email = hasEmail.value;
    }

    if (dateFrom.value) {
      params.date_from = dateFrom.value;
    }

    if (dateTo.value) {
      params.date_to = dateTo.value;
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

    focusedIndex.value = -1;

    nextTick(recalcVirtual);
  }
};


/* =========================================================
   REAL-TIME SEARCH (debounced)
========================================================= */

const RECENT_KEY = "contacts_recent_searches";

const searchRef = ref(null);

const showRecent = ref(false);

let searchTimer = null;

const loadRecent = () => {
  try {
    const list =
      JSON.parse(localStorage.getItem(RECENT_KEY) || "[]");

    return Array.isArray(list)
      ? list.filter((term) => typeof term === "string").slice(0, 5)
      : [];
  } catch {
    return [];
  }
};

const recentSearches = ref(loadRecent());


const recentList = computed(() => {
  const query =
    searchInput.value.trim().toLowerCase();

  if (!query) {
    return recentSearches.value;
  }

  return recentSearches.value.filter((term) =>
    term.toLowerCase().includes(query)
  );
});


const rememberSearch = (term) => {
  const next = [
    term,
    ...recentSearches.value.filter(
      (existing) => existing.toLowerCase() !== term.toLowerCase()
    ),
  ].slice(0, 5);

  recentSearches.value = next;

  try {
    localStorage.setItem(RECENT_KEY, JSON.stringify(next));
  } catch {
    /* storage is optional */
  }
};

const clearRecent = () => {
  recentSearches.value = [];

  try {
    localStorage.removeItem(RECENT_KEY);
  } catch {
    /* storage is optional */
  }
};

const applyRecent = (term) => {
  searchInput.value = term;

  showRecent.value = false;

  searchRef.value?.focus();
};

const hideRecentSoon = () => {
  setTimeout(() => {
    showRecent.value = false;
  }, 150);
};

const onSearchEscape = () => {
  if (searchInput.value) {
    clearSearch();
  } else {
    searchRef.value?.blur();
  }
};


watch(
  searchInput,
  (newValue) => {

    clearTimeout(searchTimer);


    searchTimer = setTimeout(() => {

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


      if (value) {
        rememberSearch(value);
      }


      /*
       * Load matching contacts.
       */
      loadContacts();

    }, 300);
  }
);


/* =========================================================
   GROUP SELECTION
   Tabs pick a single group, sidebar boxes
   can combine several — same state, one source.
========================================================= */

const selectCategory = (
  category
) => {

  selectedCategories.value =
    category ? [category] : [];


  /*
   * Every group starts
   * on page 1.
   */
  currentPage.value = 1;


  loadContacts();
};


const toggleCategoryFilter = (
  category
) => {

  const picked = [
    ...selectedCategories.value,
  ];

  const index =
    picked.indexOf(category);

  if (index >= 0) {
    picked.splice(index, 1);
  } else {
    picked.push(category);
  }

  selectedCategories.value = picked;

  currentPage.value = 1;

  loadContacts();
};


/*
 * Sort / email / date controls changed
 * from the toolbar or the sidebar.
 */
const onListOptionChange = () => {
  currentPage.value = 1;

  loadContacts();
};


const clearFilters = () => {
  selectedCategories.value = [];

  hasEmail.value = "";

  dateFrom.value = "";

  dateTo.value = "";

  currentPage.value = 1;

  loadContacts();
};


/* =========================================================
   CLEAR SEARCH
========================================================= */

const clearSearch = () => {

  clearTimeout(searchTimer);

  searchInput.value = "";

  activeSearch.value = "";

  currentPage.value = 1;

  showRecent.value = false;

  loadContacts();
};


/* =========================================================
   BULK ACTIONS
========================================================= */

const selectedIds = ref([]);

const bulkDeleting = ref(false);

const selectedCount = computed(
  () => selectedIds.value.length
);

const isContactSelected = (id) =>
  selectedIds.value.includes(id);


const allPageSelected = computed(() => {
  return (
    contacts.value.length > 0 &&
    contacts.value.every((contact) =>
      selectedIds.value.includes(contact.id)
    )
  );
});


const toggleSelect = (id) => {
  if (selectedIds.value.includes(id)) {
    selectedIds.value =
      selectedIds.value.filter(
        (existing) => existing !== id
      );
  } else {
    selectedIds.value = [
      ...selectedIds.value,
      id,
    ];
  }
};


const toggleSelectPage = () => {
  if (allPageSelected.value) {
    const pageIds = new Set(
      contacts.value.map((contact) => contact.id)
    );

    selectedIds.value =
      selectedIds.value.filter(
        (id) => !pageIds.has(id)
      );
  } else {
    selectedIds.value = [
      ...new Set([
        ...selectedIds.value,
        ...contacts.value.map((contact) => contact.id),
      ]),
    ];
  }
};


const clearSelection = () => {
  selectedIds.value = [];
};


const deleteSelected = async () => {
  const count = selectedCount.value;

  if (count === 0 || bulkDeleting.value) {
    return;
  }

  const confirmed = window.confirm(
    `Delete ${count} selected ${
      count === 1 ? "contact" : "contacts"
    }?`
  );

  if (!confirmed) {
    return;
  }

  bulkDeleting.value = true;

  try {
    const response = await api.post(
      "/contacts/bulk-delete",
      { ids: selectedIds.value }
    );

    selectedIds.value = [];

    await loadContacts();

    window.alert(
      `${response.data.deleted ?? count} contacts deleted.`
    );
  } catch (err) {
    console.error("Bulk delete error:", err);

    window.alert(
      err?.response?.data?.detail ||
        "Unable to delete the selected contacts."
    );
  } finally {
    bulkDeleting.value = false;
  }
};


/* =========================================================
   KEYBOARD NAVIGATION
   ↓ / ↑ move between cards, Enter opens,
   Space selects, / jumps to search.
========================================================= */

const focusedIndex = ref(-1);

const frameRef = ref(null);


const onItemFocus = (index) => {
  focusedIndex.value = index;
};


const focusCardAt = (index) => {
  const max = contacts.value.length - 1;

  if (index < 0 || index > max) {
    return;
  }

  focusedIndex.value = index;


  nextTick(() => {
    const frame = frameRef.value;

    if (!frame) {
      return;
    }

    const cards =
      frame.querySelectorAll(".contact-card-modern, .contact-table-row");

    const domIndex = virtualActive.value
      ? index - virtual.start
      : index;

    if (
      domIndex >= 0 &&
      domIndex < cards.length
    ) {
      cards[domIndex].focus();
      return;
    }

    /*
     * Outside the rendered window:
     * scroll it into view first.
     */
    const frameTop =
      frame.getBoundingClientRect().top +
      window.scrollY;

    const row = Math.floor(
      index / columnCount.value
    );

    window.scrollTo({
      top: Math.max(
        0,
        frameTop + row * rowHeight.value - 40,
      ),
    });

    recalcVirtual();

    nextTick(() => {
      const rendered =
        frame.querySelectorAll(".contact-card-modern, .contact-table-row");

      const domIndexAfter =
        index - virtual.start;

      if (
        domIndexAfter >= 0 &&
        domIndexAfter < rendered.length
      ) {
        rendered[domIndexAfter].focus();
      }
    });
  });
};


const openContact = (contact) => {
  router.push(`/contacts/${contact.id}`);
};


const onGlobalKeydown = (event) => {
  const active = document.activeElement;

  const typing =
    active &&
    ["INPUT", "TEXTAREA", "SELECT"].includes(
      active.tagName
    );

  if (
    event.key === "/" &&
    !typing &&
    searchRef.value
  ) {
    event.preventDefault();

    searchRef.value.focus();
  }
};


/* =========================================================
   VIRTUAL SCROLLING
   Only the rows near the viewport are rendered;
   spacer elements keep the scrollbar honest.
   Activates automatically for large pages
   (the API allows up to 100 per page).
========================================================= */

const VIRTUAL_THRESHOLD = 12;

const OVERSCAN_ROWS = 2;

const virtual = reactive({
  top: 0,
  bottom: 0,
  start: 0,
  end: 0,
});

const columnCount = ref(3);

const virtualActive = computed(
  () => contacts.value.length > VIRTUAL_THRESHOLD
);


const visibleContacts = computed(() => {
  if (!virtualActive.value) {
    return contacts.value;
  }

  return contacts.value.slice(
    virtual.start,
    virtual.end
  );
});


const fullIndex = (index) => {
  return virtualActive.value
    ? virtual.start + index
    : index;
};


const rowHeight = computed(() => {
  if (viewMode.value === "list") {
    return density.value === "compact" ? 54 : 72;
  }

  return density.value === "compact" ? 260 : 350;
});


const measureColumns = () => {
  if (viewMode.value === "list") {
    columnCount.value = 1;
    return;
  }

  const width = window.innerWidth;

  columnCount.value =
    width <= 700 ? 1 : width <= 1050 ? 2 : 3;
};


const recalcVirtual = () => {
  const items = contacts.value;

  if (
    items.length <= VIRTUAL_THRESHOLD ||
    !frameRef.value
  ) {
    virtual.top = 0;
    virtual.bottom = 0;
    virtual.start = 0;
    virtual.end = items.length;
    return;
  }

  const columns = columnCount.value;

  const rowH = rowHeight.value;

  const rows = Math.ceil(items.length / columns);

  const rect =
    frameRef.value.getBoundingClientRect();

  const viewportRows = Math.ceil(
    window.innerHeight / rowH
  );

  const firstRow =
    Math.floor(Math.max(0, -rect.top) / rowH) -
    OVERSCAN_ROWS;

  const startRow = Math.max(
    0,
    Math.min(rows - 1, firstRow)
  );

  const endRow = Math.min(
    rows - 1,
    startRow + viewportRows + OVERSCAN_ROWS * 2
  );

  virtual.start = startRow * columns;

  virtual.end = Math.min(
    items.length,
    (endRow + 1) * columns
  );

  virtual.top = startRow * rowH;

  virtual.bottom =
    (rows - 1 - endRow) * rowH;
};


const onWindowScroll = () => recalcVirtual();

const onWindowResize = () => {
  measureColumns();

  recalcVirtual();
};


watch(contacts, () => {
  nextTick(recalcVirtual);
});

watch([viewMode, density], () => {
  measureColumns();

  nextTick(recalcVirtual);
});


const skeletonCount = computed(() =>
  viewMode.value === "list" ? 8 : 6
);


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

  measureColumns();

  loadContacts();

  window.addEventListener(
    "scroll",
    onWindowScroll,
    { passive: true }
  );

  window.addEventListener(
    "resize",
    onWindowResize
  );

  document.addEventListener(
    "keydown",
    onGlobalKeydown
  );
});


onBeforeUnmount(() => {

  clearTimeout(searchTimer);

  window.removeEventListener(
    "scroll",
    onWindowScroll
  );

  window.removeEventListener(
    "resize",
    onWindowResize
  );

  document.removeEventListener(
    "keydown",
    onGlobalKeydown
  );
});
</script>
