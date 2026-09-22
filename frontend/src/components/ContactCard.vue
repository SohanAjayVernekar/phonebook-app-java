<template>
  <article
    class="contact-card-modern"
    :class="[
      mode === 'list' ? 'list-mode' : 'grid-mode',
      {
        'is-selected': selected,
        'is-focused': focused,
      },
    ]"
    tabindex="0"
    @focus="emit('item-focus')"
    @keydown="onKeydown"
  >

    <!-- Card header -->
    <div class="contact-card-top">

      <div class="profile-area">

        <!-- Selection checkbox (bulk actions) -->
        <label
          v-if="selectable"
          class="card-checkbox"
          title="Select contact"
          @click.stop
        >
          <input
            type="checkbox"
            :checked="selected"
            aria-label="Select contact"
            @change="emit('toggle-select')"
          />
        </label>

        <!-- Avatar -->
        <div class="large-avatar">
          {{ initials }}
        </div>

        <!-- Profile -->
        <div class="profile-details">

          <!-- Name (same label as the table column) -->
          <span class="field-label">
            Name
          </span>

          <!-- Name -->
          <h2>
            {{ contact.name }}
          </h2>

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


    <!-- Contact information
         Same clear labels as the table columns:
         Name · Phone Number · Email Address · Category · Address -->
    <div class="contact-fields">

      <!-- Phone Number -->
      <div class="contact-field">

        <span class="field-label">
          Phone Number
        </span>

        <strong class="field-value phone-value">
          <a
            :href="phoneLink"
            class="contact-link"
            :title="contact.phone_number"
          >
            {{ contact.phone_number }}
          </a>
        </strong>

      </div>


      <!-- Email Address -->
      <div class="contact-field">

        <span class="field-label">
          Email Address
        </span>

        <strong
          v-if="contact.email"
          class="field-value"
        >
          <a
            :href="emailLink"
            class="contact-link"
            :title="contact.email"
          >
            {{ contact.email }}
          </a>
        </strong>

        <strong
          v-else
          class="field-value field-empty"
        >
          —
        </strong>

      </div>


      <!-- Category -->
      <div class="contact-field">

        <span class="field-label">
          Category
        </span>

        <span
          class="contact-category"
          :class="categoryClass"
        >
          {{ categoryLabel }}
        </span>

      </div>


      <!-- Address -->
      <div class="contact-field">

        <span class="field-label">
          Address
        </span>

        <strong
          v-if="contact.address"
          class="field-value address-value"
          :title="contact.address"
        >
          {{ contact.address }}
        </strong>

        <strong
          v-else
          class="field-value field-empty"
        >
          —
        </strong>

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

  /*
   * "grid" = card layout, "list" = compact row layout.
   */
  mode: {
    type: String,
    default: "grid",
  },

  selectable: {
    type: Boolean,
    default: false,
  },

  selected: {
    type: Boolean,
    default: false,
  },

  focused: {
    type: Boolean,
    default: false,
  },
});


/* =========================================================
   Events
========================================================= */

const emit = defineEmits([
  "contact-deleted",
  "toggle-select",
  "item-focus",
  "open",
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
   Keyboard (Enter opens, Space selects)
========================================================= */

const onKeydown = (event) => {

  /*
   * Ignore keys pressed inside buttons,
   * links, and the checkbox.
   */
  if (event.target !== event.currentTarget) {
    return;
  }


  if (event.key === "Enter") {
    event.preventDefault();
    emit("open");
    return;
  }


  if (event.key === " ") {
    event.preventDefault();

    if (props.selectable) {
      emit("toggle-select");
    }
  }
};


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
