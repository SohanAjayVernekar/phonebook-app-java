<template>
  <tr
    class="contact-table-row"
    :class="{
      'is-selected': selected,
      'is-focused': focused,
    }"
    tabindex="0"
    @focus="emit('item-focus')"
    @keydown="onKeydown"
  >

    <!-- Selection -->
    <td class="col-select">

      <label
        class="table-checkbox"
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

    </td>


    <!-- Name -->
    <td class="col-name">

      <div class="table-person">

        <span class="table-avatar">
          {{ initials }}
        </span>

        <span
          class="cell-text table-name"
          :title="contact.name"
        >
          {{ contact.name }}
        </span>

      </div>

    </td>


    <!-- Phone number -->
    <td class="col-phone">

      <a
        class="cell-text contact-link table-phone"
        :href="phoneLink"
        :title="contact.phone_number"
      >
        {{ contact.phone_number }}
      </a>

    </td>


    <!-- Email address -->
    <td class="col-email">

      <a
        v-if="contact.email"
        class="cell-text contact-link table-email"
        :href="emailLink"
        :title="contact.email"
      >
        {{ contact.email }}
      </a>

      <span
        v-else
        class="cell-empty"
      >
        —
      </span>

    </td>


    <!-- Category -->
    <td class="col-category">

      <span
        class="contact-category"
        :class="categoryClass"
      >
        {{ categoryLabel }}
      </span>

    </td>


    <!-- Address -->
    <td class="col-address">

      <span
        v-if="contact.address"
        class="cell-text table-address"
        :title="contact.address"
      >
        {{ contact.address }}
      </span>

      <span
        v-else
        class="cell-empty"
      >
        —
      </span>

    </td>


    <!-- Actions -->
    <td class="col-actions">

      <div class="table-actions">

        <router-link
          :to="`/contacts/${contact.id}`"
          class="row-action edit-action"
        >
          Edit
        </router-link>

        <button
          type="button"
          class="row-action delete-action"
          :disabled="deleting"
          :title="`Delete ${contact.name}`"
          @click="deleteContact"
        >
          {{ deleting ? "…" : "Delete" }}
        </button>

      </div>

    </td>

  </tr>
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
   * Ignore keys pressed inside the
   * checkbox and the action buttons.
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

    emit("toggle-select");
  }
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
