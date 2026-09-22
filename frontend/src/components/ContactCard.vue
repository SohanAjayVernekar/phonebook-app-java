<template>
  <article class="contact-card-modern">

    <!-- Card header -->
    <div class="contact-card-top">

      <div class="profile-area">

        <!-- Avatar -->
        <div class="large-avatar">
          {{ initials }}
        </div>

        <!-- Profile -->
        <div class="profile-details">

          <!-- Category -->
          <span
            class="contact-category"
            :class="categoryClass"
          >
            {{ categoryLabel }}
          </span>

          <!-- Name -->
          <h2>
            {{ contact.name }}
          </h2>

          <!-- Status -->
          <div class="contact-status">
            <span class="status-dot"></span>
            <span>Active contact</span>
          </div>

        </div>

      </div>


      <!-- Options -->
      <button
        type="button"
        class="more-button"
        title="Contact options"
        aria-label="Contact options"
        @click="showOptions"
      >
        ⋮
      </button>

    </div>


    <!-- Contact information -->
    <div class="contact-info">

      <!-- Phone -->
      <div class="contact-info-row">

        <div class="detail-icon phone-detail">
          ☎
        </div>

        <div class="detail-content">

          <span class="info-label">
            PHONE
          </span>

          <strong class="info-value">
            <a
              :href="phoneLink"
              class="contact-link"
            >
              {{ contact.phone_number }}
            </a>
          </strong>

        </div>

      </div>


      <!-- Email -->
      <div
        v-if="contact.email"
        class="contact-info-row"
      >

        <div class="detail-icon email-detail">
          ✉
        </div>

        <div class="detail-content">

          <span class="info-label">
            EMAIL
          </span>

          <strong class="info-value">
            <a
              :href="emailLink"
              class="contact-link"
            >
              {{ contact.email }}
            </a>
          </strong>

        </div>

      </div>


      <!-- Address -->
      <div
        v-if="contact.address"
        class="contact-info-row"
      >

        <div class="detail-icon address-detail">
          ⌂
        </div>

        <div class="detail-content">

          <span class="info-label">
            ADDRESS
          </span>

          <strong
            class="info-value address-value"
          >
            {{ contact.address }}
          </strong>

        </div>

      </div>

    </div>


    <!-- Divider -->
    <div class="card-divider"></div>


    <!-- Actions -->
    <div class="modern-card-actions">

      <!-- Edit -->
      <router-link
        :to="`/contacts/${contact.id}`"
        class="edit-contact-button"
      >
        ✎
        <span>
          Edit Contact
        </span>
      </router-link>


      <!-- Delete -->
      <button
        type="button"
        class="delete-contact-button"
        :disabled="deleting"
        @click="deleteContact"
      >
        <span v-if="deleting">
          Deleting...
        </span>

        <span v-else>
          ▣
          Delete
        </span>
      </button>

    </div>

  </article>
</template>


<script setup>
import {
  computed,
  ref,
} from "vue";

import api from "../services/api";


/* =========================================================
   Props
========================================================= */

const props = defineProps({
  contact: {
    type: Object,
    required: true,
  },
});


/* =========================================================
   Events
========================================================= */

const emit = defineEmits([
  "contact-deleted",
]);


/* =========================================================
   State
========================================================= */

const deleting = ref(false);


/* =========================================================
   Initials
========================================================= */

const initials = computed(() => {

  const name =
    props.contact?.name?.trim() || "";

  if (!name) {
    return "?";
  }

  const parts =
    name
      .split(/\s+/)
      .filter(Boolean);


  if (parts.length === 1) {
    return parts[0]
      .slice(0, 2)
      .toUpperCase();
  }


  return (
    parts[0][0] +
    parts[parts.length - 1][0]
  ).toUpperCase();
});


/* =========================================================
   Category
========================================================= */

const categoryLabel = computed(() => {

  const category =
    props.contact?.category;


  if (category === "WORK") {
    return "WORK";
  }

  if (category === "FAMILY") {
    return "FAMILY";
  }

  return "FRIEND";
});


const categoryClass = computed(() => {

  if (
    props.contact?.category === "WORK"
  ) {
    return "category-work";
  }

  if (
    props.contact?.category === "FAMILY"
  ) {
    return "category-family";
  }

  return "category-friend";
});


/* =========================================================
   Phone
========================================================= */

const phoneLink = computed(() => {

  const phone =
    props.contact?.phone_number || "";

  const cleanPhone =
    phone.replace(
      /[^0-9+]/g,
      ""
    );

  return `tel:${cleanPhone}`;
});


/* =========================================================
   Email
========================================================= */

const emailLink = computed(() => {

  if (!props.contact?.email) {
    return "#";
  }

  return `mailto:${props.contact.email}`;
});


/* =========================================================
   Options
========================================================= */

const showOptions = () => {
  console.info(
    "Contact options:",
    props.contact
  );
};


/* =========================================================
   Delete
========================================================= */

const deleteContact = async () => {

  if (deleting.value) {
    return;
  }


  const confirmed =
    window.confirm(
      `Are you sure you want to delete "${props.contact.name}"?`
    );


  if (!confirmed) {
    return;
  }


  deleting.value = true;


  try {

    await api.delete(
      `/contacts/${props.contact.id}`
    );


    emit(
      "contact-deleted",
      props.contact.id
    );

  } catch (error) {

    console.error(
      "Delete contact error:",
      error
    );


    const message =
      error?.response?.data?.detail ||
      "Unable to delete contact. Please try again.";


    window.alert(message);

  } finally {

    deleting.value = false;
  }
};
</script>