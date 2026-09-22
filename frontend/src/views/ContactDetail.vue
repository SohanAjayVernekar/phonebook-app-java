<template>
  <section class="contact-detail">

    <!-- =====================================================
         PAGE HEADER
    ====================================================== -->

    <div class="page-header">

      <div>
        <span class="section-label">
          CONTACT DETAILS
        </span>

        <h1>
          Edit Contact
        </h1>

        <p>
          Update the contact information below.
        </p>
      </div>


      <button
        type="button"
        class="back-button"
        @click="goBack"
      >
        <span>←</span>
        <span>Back to Contacts</span>
      </button>

    </div>


    <!-- =====================================================
         LOADING
    ====================================================== -->

    <div
      v-if="loading"
      class="message loading-state"
    >
      <div class="loading-spinner"></div>

      <p>
        Loading contact...
      </p>
    </div>


    <!-- =====================================================
         ERROR
    ====================================================== -->

    <div
      v-else-if="loadError"
      class="message error"
    >
      <div class="empty-icon">
        !
      </div>

      <h2>
        Unable to load contact
      </h2>

      <p>
        {{ loadError }}
      </p>

      <button
        type="button"
        class="primary-button"
        @click="loadContact"
      >
        Try Again
      </button>
    </div>


    <!-- =====================================================
         CONTACT FORM
    ====================================================== -->

    <form
      v-else
      class="detail-form"
      @submit.prevent="updateContact"
    >

      <!-- Form title -->
      <div class="form-title">

        <div class="edit-icon">
          ✎
        </div>

        <div>
          <h2>
            Contact Information
          </h2>

          <p>
            Update the details of this contact.
          </p>
        </div>

      </div>


      <!-- ===================================================
           UPDATE ERROR
      ==================================================== -->

      <div
        v-if="updateError"
        class="form-error"
      >
        {{ updateError }}
      </div>


      <!-- ===================================================
           SUCCESS
      ==================================================== -->

      <div
        v-if="successMessage"
        class="form-success"
      >
        {{ successMessage }}
      </div>


      <!-- ===================================================
           NAME
      ==================================================== -->

      <div class="form-group">

        <label for="name">
          Full Name
          <span>*</span>
        </label>

        <input
          id="name"
          v-model.trim="form.name"
          type="text"
          maxlength="255"
          autocomplete="name"
          placeholder="Enter full name"
          required
        />

      </div>


      <!-- ===================================================
           PHONE
      ==================================================== -->

      <div class="form-group">

        <label for="phone_number">
          Phone Number
          <span>*</span>
        </label>

        <input
          id="phone_number"
          v-model.trim="form.phone_number"
          type="tel"
          autocomplete="tel"
          placeholder="+919876543210"
          required
        />

      </div>


      <!-- ===================================================
           EMAIL
      ==================================================== -->

      <div class="form-group">

        <label for="email">
          Email
        </label>

        <input
          id="email"
          v-model.trim="form.email"
          type="email"
          autocomplete="email"
          placeholder="example@email.com"
        />

      </div>


      <!-- ===================================================
           ADDRESS
      ==================================================== -->

      <div class="form-group">

        <label for="address">
          Address
        </label>

        <textarea
          id="address"
          v-model.trim="form.address"
          rows="4"
          placeholder="Enter address"
        ></textarea>

      </div>


      <!-- ===================================================
           CATEGORY
      ==================================================== -->

      <div class="form-group">

        <label for="category">
          Category
          <span>*</span>
        </label>

        <select
          id="category"
          v-model="form.category"
          required
        >
          <option value="WORK">
            Work
          </option>

          <option value="FAMILY">
            Family
          </option>

          <option value="FRIEND">
            Friend
          </option>
        </select>

      </div>


      <!-- ===================================================
           ACTIONS
      ==================================================== -->

      <div class="form-actions">

        <button
          type="button"
          class="secondary-button"
          :disabled="saving"
          @click="goBack"
        >
          Cancel
        </button>


        <button
          type="submit"
          class="save-button"
          :disabled="saving"
        >

          <span v-if="saving">
            Saving...
          </span>

          <span v-else>
            Save Changes
          </span>

        </button>

      </div>

    </form>

  </section>
</template>


<script setup>
import {
  reactive,
  ref,
  onMounted,
} from "vue";

import {
  useRoute,
  useRouter,
} from "vue-router";

import api from "../services/api";


/* =========================================================
   Router
========================================================= */

const route = useRoute();
const router = useRouter();


/* =========================================================
   Contact ID
========================================================= */

const contactId =
  Number(route.params.contactId);


/* =========================================================
   State
========================================================= */

const loading = ref(true);

const saving = ref(false);

const loadError = ref("");

const updateError = ref("");

const successMessage = ref("");


/* =========================================================
   Form
========================================================= */

const form = reactive({
  name: "",
  phone_number: "",
  email: "",
  address: "",
  category: "FRIEND",
});


/* =========================================================
   Load Contact
========================================================= */

const loadContact = async () => {

  loading.value = true;

  loadError.value = "";

  try {

    if (
      !contactId ||
      Number.isNaN(contactId)
    ) {
      throw new Error(
        "Invalid contact ID."
      );
    }


    const response =
      await api.get(
        `/contacts/${contactId}`
      );


    const contact =
      response.data;


    form.name =
      contact.name || "";


    form.phone_number =
      contact.phone_number || "";


    form.email =
      contact.email || "";


    form.address =
      contact.address || "";


    /*
     * Existing contacts created
     * before categories were added
     * may not have a category.
     */
    form.category =
      contact.category ||
      "FRIEND";

  } catch (error) {

    console.error(
      "Load contact error:",
      error
    );


    loadError.value =
      error?.response?.data?.detail ||
      error?.message ||
      "Unable to load contact.";

  } finally {

    loading.value = false;
  }
};


/* =========================================================
   Validation
========================================================= */

const validateForm = () => {

  const name =
    form.name.trim();

  const phone =
    form.phone_number.trim();

  const email =
    form.email.trim();


  /* Name */

  if (!name) {

    updateError.value =
      "Please enter the contact name.";

    return false;
  }


  /* Phone */

  if (!phone) {

    updateError.value =
      "Please enter a phone number.";

    return false;
  }


  /*
   * Accept numbers with an optional +
   * and common separators.
   */
  const phonePattern =
    /^\+?[0-9\s().-]{7,20}$/;


  const digitsOnly =
    phone.replace(
      /\D/g,
      ""
    );


  if (
    !phonePattern.test(phone) ||
    digitsOnly.length < 7 ||
    digitsOnly.length > 20
  ) {

    updateError.value =
      "Please enter a valid phone number.";

    return false;
  }


  /* Email */

  if (email) {

    const emailPattern =
      /^[^\s@]+@[^\s@]+\.[^\s@]+$/;


    if (
      !emailPattern.test(email)
    ) {

      updateError.value =
        "Please enter a valid email address.";

      return false;
    }
  }


  /* Category */

  const validCategories = [
    "WORK",
    "FAMILY",
    "FRIEND",
  ];


  if (
    !validCategories.includes(
      form.category
    )
  ) {

    updateError.value =
      "Please select a valid category.";

    return false;
  }


  return true;
};


/* =========================================================
   Update Contact
========================================================= */

const updateContact = async () => {

  updateError.value = "";

  successMessage.value = "";


  if (!validateForm()) {
    return;
  }


  saving.value = true;


  try {

    const payload = {

      name:
        form.name.trim(),

      phone_number:
        form.phone_number.trim(),

      email:
        form.email.trim() || null,

      address:
        form.address.trim() || null,

      category:
        form.category,
    };


    await api.put(
      `/contacts/${contactId}`,
      payload
    );


    successMessage.value =
      "Contact updated successfully.";


    /*
     * Give the user a moment to see
     * the success message, then return
     * to the dashboard.
     */
    setTimeout(() => {

      router.push(
        "/contacts"
      );

    }, 500);

  } catch (error) {

    console.error(
      "Update contact error:",
      error
    );


    if (
      error?.response?.status === 409
    ) {

      updateError.value =
        error.response.data?.detail ||
        "A contact with these details already exists.";

    }

    else if (
      error?.response?.status === 422
    ) {

      const detail =
        error.response.data?.detail;


      if (
        Array.isArray(detail)
      ) {

        updateError.value =
          detail
            .map(
              (item) =>
                item.msg
            )
            .join(", ");

      } else {

        updateError.value =
          detail ||
          "Please check the entered information.";
      }

    }

    else {

      updateError.value =
        error?.response?.data?.detail ||
        "Unable to update contact. Please try again.";
    }

  } finally {

    saving.value = false;
  }
};


/* =========================================================
   Back
========================================================= */

const goBack = () => {

  router.push(
    "/contacts"
  );
};


/* =========================================================
   Initial load
========================================================= */

onMounted(() => {
  loadContact();
});
</script>