<template>
  <main class="landing-page">
    <div class="landing-background">
      <div class="landing-orb orb-one"></div>
      <div class="landing-orb orb-two"></div>
      <div class="landing-orb orb-three"></div>
      <div class="landing-grid"></div>
    </div>

    <div class="landing-content">
      <header class="landing-header">
        <div class="landing-brand">
          <div class="landing-logo">
            <span>☎</span>
          </div>

          <div class="landing-brand-text">
            <strong>Connect</strong>
            <span>Contact Manager</span>
          </div>
        </div>

        <div class="landing-header-status">
          <span class="landing-status-dot"></span>
          System ready
        </div>
      </header>

      <section class="landing-hero">
        <div class="landing-badge">
          <span class="badge-spark">✦</span>
          YOUR PERSONAL CONTACT SPACE
        </div>

        <h1>
          Everyone you know,
          <span>in one place.</span>
        </h1>

        <p class="landing-description">
          A simple and elegant way to manage your contacts, phone numbers,
          email addresses, and locations.
        </p>

        <router-link to="/login" class="landing-cta">
          <span class="cta-icon">→</span>
          <span class="cta-text">
            <strong>Open Phonebook</strong>
            <small>Sign in to view your contacts</small>
          </span>
          <span class="cta-arrow">→</span>
        </router-link>

        <div class="landing-features">
          <div class="landing-feature">
            <span class="feature-check">✓</span>
            <span>Easy to manage</span>
          </div>

          <div class="landing-feature">
            <span class="feature-check">✓</span>
            <span>Fast search</span>
          </div>

          <div class="landing-feature">
            <span class="feature-check">✓</span>
            <span>Organized contacts</span>
          </div>
        </div>
      </section>

      <section class="landing-preview">
        <div class="preview-window">
          <div class="preview-topbar">
            <div class="preview-dots">
              <span></span>
              <span></span>
              <span></span>
            </div>

            <div class="preview-title">CONTACTS</div>
            <div class="preview-placeholder"></div>
          </div>

          <div class="preview-body">
            <div class="preview-heading">
              <div>
                <small>DIRECTORY</small>
                <strong>Your Contacts</strong>
              </div>

              <span class="preview-count">128 contacts</span>
            </div>

            <div class="preview-search">
              <span>⌕</span>
              Search contacts...
            </div>

            <div class="preview-cards">
              <div class="preview-card">
                <div class="preview-avatar">SK</div>
                <div>
                  <strong>Sohan Kumar</strong>
                  <span>+91 98765 43210</span>
                </div>
              </div>

              <div class="preview-card">
                <div class="preview-avatar second">AR</div>
                <div>
                  <strong>Ankit Rao</strong>
                  <span>+91 98220 11223</span>
                </div>
              </div>

              <div class="preview-card">
                <div class="preview-avatar third">NP</div>
                <div>
                  <strong>Neha Patel</strong>
                  <span>+91 99110 33445</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </main>
</template>
